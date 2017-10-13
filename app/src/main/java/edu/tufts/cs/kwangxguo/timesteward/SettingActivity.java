package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    private PackageManager packageManager;
    private Activity settingActivity;
    private Set<String> selectedAppPackageNames;
    private int timeLimit; // in minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.settingActivity = SettingActivity.this;


        /*******************************************************
         *             Create App List:
         *   Using customized adapter, display icon and app name
         *******************************************************/
        packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        List<ApplicationInfo> installedApps = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            // check if the app is a system app
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                installedApps.add(app);
//                Log.d("Setting, app-list", (String) packageManager.getApplicationLabel(app));
            }
        }

        /* create a hashset to store selected app's package name */
        selectedAppPackageNames = new HashSet<>();
        // create an instance of my customized adapter
        AppListAdapter appListAdapter = new AppListAdapter(this, installedApps, packageManager, selectedAppPackageNames);
        ListView listView = (ListView)findViewById(R.id.applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();
        lp.height = 100 * installedApps.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(appListAdapter);

        /***********************************
         *          Set Time Limit          *
         ************************************/
        NumberPicker np_minute = (NumberPicker)findViewById(R.id.timer_minute);

        np_minute.setMinValue(0);
        np_minute.setMaxValue(59);
        np_minute.setWrapSelectorWheel(true);

        NumberPicker np_hour = (NumberPicker)findViewById(R.id.timer_hour);
        np_hour.setMinValue(0);
        np_hour.setMaxValue(23);
        np_hour.setWrapSelectorWheel(true);

        final int[] time = {0, 0}; // hour, minute

        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                time[0] = newVal;
            }
        });
        np_minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                time[1] = newVal;
            }
        });

        /************************************
         *          Deal with Buttons       *
         ************************************/
        //button actions
        Button confirm_button = (Button) findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set selected time in minutes
                timeLimit = (time[0] * 60) + time[1];
                for (String selectedApp : selectedAppPackageNames)
                    Log.d("setting_confirm", "the selected apps are: " + selectedApp);
                Log.d("setting_confirm", "time: " + timeLimit);

                
            }
        });

        Button clear_button = (Button) findViewById(R.id.clear_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // for (String name : selectedAppPackageNames) Log.d("setting_clear", "onClick: " + name);
                // clear selectedAppSet and refresh the activity
                selectedAppPackageNames.clear();
                restartActivity(settingActivity);
            }
        });

    }

    public static void restartActivity(Activity activity) {
            activity.recreate();
    }

    /***********************************************
                     Test
    ***********************************************/
    public void testUsage() {
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long beginTime = cal.getTimeInMillis();
        long currTime = System.currentTimeMillis();
        List<UsageStats> uStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currTime);
        Log.d("setting", "testUsage: succeed!!, ListSize = " + uStatsList.size());
        for (UsageStats us : uStatsList) {
            if (us.getTotalTimeInForeground() < 1e6) continue;
            String pkgName = us.getPackageName();
            try {
                String appName = packageManager.getApplicationInfo(pkgName, 0).loadLabel(packageManager).toString();
                Log.d("setting", "testUsage: AppName:" + appName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}