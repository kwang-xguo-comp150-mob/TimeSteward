package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class SettingActivity extends Activity {
    PackageManager packageManager;
    Button confirm_button;
    Button clear_button;
    Set<ApplicationInfo> selectedAppSet;
    AppListAdapter appListAdapter;
    CheckBox cBox;
    View v;
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
        for(ApplicationInfo app : apps) {
            // check if the app is a system app
            if((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                installedApps.add(app);
                Log.d("Setting, app-list", (String)packageManager.getApplicationLabel(app));
            }
        }

        // create a hashset to store selected appinfo
        selectedAppSet = new HashSet<>();
        // create an instance of my customized adapter
        appListAdapter = new AppListAdapter(this, installedApps, packageManager, selectedAppSet);
        ListView listView = findViewById(R.id.applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)listView.getLayoutParams();
        lp.height = 100 * installedApps.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(appListAdapter);

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

        //button actions
        confirm_button = (Button)findViewById(R.id.confirm_button);
        addListenerOn_ConfirmButton();

        clear_button = (Button) findViewById(R.id.clear_button);
        addListenerOn_ClearButton();
    }
    public void addListenerOn_ConfirmButton(){
        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                for (ApplicationInfo selectedApp: selectedAppSet)
                    Log.d("the selected apps are: ", (String)packageManager.getApplicationLabel(selectedApp));
                /* should jump to another view */
            }
        });
    }
    public void addListenerOn_ClearButton(){
        clear_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //now it can only clear the first checkbox....
                cBox = (CheckBox) findViewById(R.id.app_checkbox);
                cBox.setChecked(false);
                Log.d("click","clear");
            }
        });
    }
}

//private class appListAdapter extends ArrayAdapter<ApplicationInfo> {
//    public appListAdapter(Context context, ArrayList<ApplicationInfo> appInfoList) {
//        super(context, 0, appInfoList);
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//    }
//}
