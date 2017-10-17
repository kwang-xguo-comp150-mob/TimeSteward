package edu.tufts.cs.kwangxguo.timesteward;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private ArrayList<String> selectedAppNames;
    private HashMap<String,Integer> usageTime = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
//        Intent in = getIntent();
//        Bundle b = in.getExtras();
//        timeLimit = b.getInt("timeLimit");

        // create an instance of my customized adapter
        packageManager = getPackageManager();
        //ArrayList<ApplicationInfo> installedApps = b.getParcelableArrayList("installedApps");
        selectedApps = new ArrayList<>();
//        selectedAppNames = b.getStringArrayList("selectedAppList");
        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        String selectedAppNames_string = cursor.getString(0);
        timeLimit = cursor.getInt(1);
        cursor.close();
        db.close();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        selectedAppNames = gson.fromJson(selectedAppNames_string, type);
        for (int i = 0; i < selectedAppNames.size(); i++) {
            Log.d("selectedAppName",selectedAppNames.get(i));
        }

        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            if (selectedAppNames.contains(app.packageName)) {
                selectedApps.add(app);
                Log.d("selectedAppName", app.packageName);
            }
        }

        getUsage();
        Log.d("timelimit:",timeLimit + "");
        Log.d("timeremain:", timeRemain + "");
        TextView time_limit = (TextView)findViewById(R.id.time_limit);
        time_limit.setText("Total Time Limit: "+timeLimit+"mins.");

        TextView time_remain = (TextView)findViewById(R.id.time_remaining);
        time_remain.setText("Remaining Time: " + timeRemain+"mins.");

        appListAdapter = new AppListAdapter2(this, selectedApps, packageManager, selectedAppNames, usageTime);
        ListView listView = (ListView)findViewById(R.id.selected_applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();
        if (selectedAppNames.size() > 5)
        lp.height = 100 * selectedAppNames.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(appListAdapter);

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
        Log.d("setting", "testUsage: succeed!!, ListSize = " + uStatsList.size());
        for (UsageStats us : uStatsList) {
            if (us.getTotalTimeInForeground() < 1e6) continue;
            String pkgName = us.getPackageName();
            Log.d("getPackageName()",pkgName);
            try {
                if (selectedAppNames.contains(pkgName)) {
                    String appName = packageManager.getApplicationInfo(pkgName, 0).packageName;
                    Log.d("setting", "testUsage: AppName:" + appName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
                    usagetime += us.getTotalTimeInForeground() / 6e4;
                    usageTime.put(appName, (int)(us.getTotalTimeInForeground() / 6e4));
                }
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
        }
        timeRemain = timeLimit - usagetime;
    }
}
