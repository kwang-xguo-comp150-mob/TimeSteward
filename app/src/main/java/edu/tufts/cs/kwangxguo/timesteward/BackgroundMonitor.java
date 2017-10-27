
package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BackgroundMonitor extends JobService {
    private List<String> selectedPackageNames;
    private int timeLimit;
    private PackageManager pm;
    private int totalTime;
    private int timeRemaining;
    private String currentRunningPackageName;
    private PowerManager powerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("monitor", "onCreate: ---------- job created ----------");
        this.pm = getPackageManager();
        this.totalTime = 0;
        this.powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);

        // get selected package name list and time limit from db.
        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        String selectedAppNames_string = cursor.getString(0);
        this.timeLimit = cursor.getInt(1);
        cursor.close();
        db.close();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        selectedPackageNames = gson.fromJson(selectedAppNames_string, type);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("monitor", "onStartJob: ----------- job started ----------");
        getUsage(); // get usage time and currently running app package name.
        if (! powerManager.isInteractive()) {
            Log.d("monitor", "onStartJob: Screen is off, mute.");
            return false;
        }
        // if time limit is up, pop up notification
        if (selectedPackageNames.contains(currentRunningPackageName) && timeRemaining <= 0) {
            //build notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.clock)
                            .setContentTitle("Time Steward")
                            .setContentText("Time is up !")
                            .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                            .setPriority(NotificationCompat.PRIORITY_HIGH); //must give priority to High, Max which will considered as heads-up notification

            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
            notificationManager.notify(0, builder.build());
        }

        Log.d("monitor", "onCreate: current app is " + currentRunningPackageName);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("monitor", "onStopJob: ------------ job stopped ---------");

        return false;
    }

    @Override
    public void onDestroy() {
        Log.d("monitor", "onDestroy: ------------ Job Destroy -----------");

        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(this, BackgroundMonitor.class));

        // if timeRemaining is 10 minutes, next check will be in 10 minutes;
        int minLatency;
        if (timeRemaining > 60) {
            minLatency = timeRemaining * 1000;
        } else {
            minLatency = 60 * 1000;
        }
        builder.setMinimumLatency(10000); // should be minLatency

        JobScheduler js = getSystemService(JobScheduler.class);
        int code = js.schedule(builder.build());
        if (code <= 0) Log.d("monitor", "onCreate: _______ Job scheduling failed --------");
        Log.d("monitor", "onCreate: -------- Job scheduled after " + minLatency / 1000 + " seconds ---------");
    }

    public void getUsage() {
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, -1);
        today.set(Calendar.MILLISECOND, 999);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.HOUR_OF_DAY, 23);
        long beginTime = today.getTimeInMillis();
        long currTime = System.currentTimeMillis();
        List<UsageStats> uStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currTime);
        long lastUsedAppTime = 0;
        for (UsageStats us : uStatsList) {
            // determine current foreground app
            if (! us.getPackageName().equals(this.getPackageName()) && us.getLastTimeUsed() > lastUsedAppTime) {
                currentRunningPackageName = us.getPackageName();
                lastUsedAppTime = us.getLastTimeUsed();
            }

            if (us.getTotalTimeInForeground() < 1e6) continue;
            String pkgName = us.getPackageName();
            try {
                if (selectedPackageNames.contains(pkgName)) {
                    String appName = pm.getApplicationInfo(pkgName, 0).packageName;
                    Log.d("setting", "testUsage: AppName:" + appName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
                    totalTime += us.getTotalTimeInForeground() / 6e4;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        timeRemaining = (timeLimit - totalTime);
    }
}