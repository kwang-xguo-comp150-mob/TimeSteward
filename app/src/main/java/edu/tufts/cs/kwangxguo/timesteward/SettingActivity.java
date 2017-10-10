package edu.tufts.cs.kwangxguo.timesteward;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    private PackageManager packageManager;
    private Button confirm_button;
    private Button clear_button;
    private Set<ApplicationInfo> selectedAppSet;
    private AppListAdapter appListAdapter;
    private Activity settingActivity;

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
        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo app : apps) {
            // check if the app is a system app
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                installedApps.add(app);
                Log.d("Setting, app-list", (String) packageManager.getApplicationLabel(app));
            }
        }

        // create a hashset to store selected appinfo
        selectedAppSet = new HashSet<>();
        // create an instance of my customized adapter
        appListAdapter = new AppListAdapter(this, installedApps, packageManager, selectedAppSet);
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

        /************************************
         *          Deal with Buttons       *
         ************************************/
        //button actions
        confirm_button = (Button)findViewById(R.id.confirm_button);
        addListenerOn_ConfirmButton();

        clear_button = (Button)findViewById(R.id.clear_button);
        addListenerOn_ClearButton();
    }

    public void addListenerOn_ConfirmButton() {
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ApplicationInfo selectedApp : selectedAppSet)
                    Log.d("the selected apps are: ", (String) packageManager.getApplicationLabel(selectedApp));
                /* should jump to another view */
            }
        });
    }

    public void addListenerOn_ClearButton() {
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear selectedAppSet and refresh the activity
                selectedAppSet.clear();
                restartActivity(settingActivity);
            }
        });
    }

    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
}