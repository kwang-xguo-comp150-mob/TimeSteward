package edu.tufts.cs.kwangxguo.timesteward;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class report extends AppCompatActivity {
    private AppListAdapter2 appListAdapter;
    private PackageManager packageManager;
    private int timeLimit;
    private int timeRemain;
    private int usagetime;
    private ArrayList<ApplicationInfo> selectedApps;
    private ArrayList<String> selectedAppPackageNames;
    private HashMap<String,Integer> usageTime = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        packageManager = getPackageManager();
        selectedApps = new ArrayList<>();

        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        String selectedAppNames_string = cursor.getString(0);
        timeLimit = cursor.getInt(1);
        cursor.close();
        db.close();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        selectedAppPackageNames = gson.fromJson(selectedAppNames_string, type);

        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            if (selectedAppPackageNames.contains(app.packageName)) {
                selectedApps.add(app);
            }
        }

        getUsage();

        TextView time_limit = (TextView)findViewById(R.id.time_limit);
        time_limit.setText("Total Time Limit: "+timeLimit+"mins.");

        TextView time_remain = (TextView)findViewById(R.id.time_remaining);
        time_remain.setText("Remaining Time: " + timeRemain+"mins.");

        appListAdapter = new AppListAdapter2(this, selectedApps, packageManager, selectedAppPackageNames, usageTime);
        ListView listView = (ListView)findViewById(R.id.selected_applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();
        if (selectedAppPackageNames.size() > 5)
        lp.height = 100 * selectedAppPackageNames.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(appListAdapter);

        /********************************************************************
         *        Schedule Sticky Background Monitor Service
         ********************************************************************/
        if (! isMyServiceRunning(BackgroundMonitor.class)) {
            // if the service is not running, schedule the service
            JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(this, BackgroundMonitor.class));
            builder.setMinimumLatency((long)5e3);
            builder.setOverrideDeadline((long)6e3);
            JobScheduler js = (JobScheduler)getSystemService(this.JOB_SCHEDULER_SERVICE);
            int code = js.schedule(builder.build());
            if (code <= 0) Log.d("monitor", "report: _______ Job scheduling failed --------");
            else Log.d("monitor", "report: -------- Job scheduled ---------");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        for (UsageStats us : uStatsList) {
            if (us.getTotalTimeInForeground() < 1e6) continue;
            String pkgName = us.getPackageName();
            try {
                if (selectedAppPackageNames.contains(pkgName)) {
                    String appName = packageManager.getApplicationInfo(pkgName, 0).packageName;
                    Log.d("setting", "testUsage: AppName:" + appName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
                    usagetime += us.getTotalTimeInForeground() / 6e4;
                    usageTime.put(appName, (int)(us.getTotalTimeInForeground() / 6e4));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        timeRemain = (timeLimit - usagetime) > 0 ? timeLimit - usagetime : 0;
        Log.d("report", "getUsage: total time:" + usagetime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }
    public void onMenuAction(MenuItem mi){

    }
    public void onSettingAction(MenuItem mi){
        Intent intent = new Intent(this, SetPage.class);
        startActivity(intent);
    }
}
