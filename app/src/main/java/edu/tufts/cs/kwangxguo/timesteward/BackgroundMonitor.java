
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
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BackgroundMonitor extends JobService {
    private List<String> selectedPackageNames;
    private int timeLimit;
    private PackageManager pm;
    private int totalTime;
    private int timeRemaining;
    private String currentRunningPackageName;
    private PowerManager powerManager;
    private int start_point;
    private int gentle_interval;
    private int intense_interval;

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

        // get start_point, gentle_interval and intense_interval from Notification database
        SQLiteDatabase db_notification = null;
        String path = this.getDatabasePath("notification.db").getAbsolutePath();
        try {
            db_notification = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch(SQLiteException e) {
            Log.d("notification", "AppListAdapter: db doesn't exist !!!!!!!!");
        }
        if (db_notification != null) {
            Cursor cursor_notification = db_notification.rawQuery("SELECT * FROM NOTIFICATION", null);
            cursor_notification.moveToFirst();
            start_point = cursor_notification.getInt(0);
            gentle_interval = cursor_notification.getInt(1);
            intense_interval = cursor_notification.getInt(2);
            Log.d("monitors", "onStart: start: " + start_point + " gentle: " + gentle_interval + " intense: " + intense_interval);
        } else {
            start_point = timeLimit/2;
            gentle_interval = timeLimit/4;
            intense_interval = timeLimit/8;
            Log.d("monitors_default", "onStart: start: " + start_point + " gentle: " + gentle_interval + " intense: " + intense_interval);
        }

        //build notification
        if (selectedPackageNames.contains(currentRunningPackageName)) {
            String content = timeRemaining < 0 ? "Time is up !" : "Reminder";

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_round2)
                            .setContentTitle(content)
                            .setContentText("You have spent your time on your phone for " + totalTime + " minutes.")
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

        // |------------------------------—————|————————————---——--------————--—————|————————————————————————————————> time line
        // 0     gentle_interval          start_point      intense interval     time's up     3 minutes per alert

        int minLatency;
        if (totalTime < start_point) {
            minLatency = gentle_interval * 60 * 1000;
        } else if (totalTime > start_point && timeRemaining > 0) {
            minLatency = intense_interval * 60 * 1000;
        } else {
            // timeRemaining < 0, 3 minutes per alert
            minLatency = 3 * 60 * 1000;
        }
        builder.setMinimumLatency(minLatency); // should be minLatency

        JobScheduler js = getSystemService(JobScheduler.class);
        int code = js.schedule(builder.build());
        if (code <= 0) Log.d("monitor", "onCreate: _______ Job scheduling failed --------");
        Log.d("monitor", "onCreate: -------- Job scheduled after " + minLatency / 1000 / 60 + " minutes ---------");

        //upload data to firebase

////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // get the current user's uid and current date, this part should be placed to the backgroundMonitor
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("user-time");
        if (user != null) {
            String uid = user.getUid();
            //today
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String id = uid + "_" + date;
            User u = new User(totalTime,timeLimit);
            //pushing user to "UserDate" node
            mDatabase.child(id).setValue(u);
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void getUsage() {
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 1);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long beginTime = today.getTimeInMillis();
        long currTime = System.currentTimeMillis();
        Map<String, UsageStats> uStatsMap = usm.queryAndAggregateUsageStats(beginTime, currTime);
        long lastUsedAppTime = 0;
        for (Map.Entry<String, UsageStats> entry : uStatsMap.entrySet()) {
            String packageName = entry.getKey();
            UsageStats us = entry.getValue();
            // determine current foreground app
            if (! us.getPackageName().equals(this.getPackageName()) && us.getLastTimeUsed() > lastUsedAppTime) {
                currentRunningPackageName = us.getPackageName();
                lastUsedAppTime = us.getLastTimeUsed();
            }

            if (us.getTotalTimeInForeground() < 1e4) continue;
            if (selectedPackageNames.contains(packageName)) {
                Log.d("monitor", "testUsage: " + packageName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
                totalTime += us.getTotalTimeInForeground() / 6e4;
                Log.d("monitor", "getUsage: Total time: " + totalTime + " minutes. ");
            }
        }
        timeRemaining = (timeLimit - totalTime);
        Log.d("monitor", "getUsage: tiem remaining = " + timeRemaining + " minutes.");
    }
}