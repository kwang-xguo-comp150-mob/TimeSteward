package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {
    PackageManager packageManager;
    //String[] testlist = {"Android","IPhone","234","343","342","Android","IPhone","234","343","342"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /*******************************************************
         *             Create App List:
         *   Using customized adapter, display icon and app name
         *******************************************************/
        packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
        List<String> appsNames = new ArrayList<String>();
        for(ApplicationInfo app : apps) {
            // check if the app is a system app
            if((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                installedApps.add(app);
                appsNames.add((String)packageManager.getApplicationLabel(app));
                Log.d("Setting, app-list", (String)packageManager.getApplicationLabel(app));
            }
        }

        // the 2nd parameter is a template view xml
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.setting_applist_item, appsNames);
        ListView listView = (ListView)findViewById(R.id.applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)listView.getLayoutParams();
        System.out.print(appsNames.size());
        lp.height = 100 * appsNames.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(adapter);

        /***********************************
        *          Set Time Limit          *
        ************************************/
        final TextView tv = findViewById(R.id.set_time_limit);
        NumberPicker np = findViewById(R.id.np);
        //Set TextView text color
        //tv.setTextColor(Color.parseColor("#ffd32b3b"));

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(1440);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);
    }
}
