package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {
    PackageManager packageManager;
    //String[] testlist = {"Android","IPhone","234","343","342","Android","IPhone","234","343","342"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();
        List<String> appsNames = new ArrayList<String>();
        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
                appsNames.add((String)packageManager.getApplicationLabel(app));
                //System.out.println((String)packageManager.getApplicationLabel(app));
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                installedApps.add(app);
                appsNames.add((String)packageManager.getApplicationLabel(app));
                //System.out.println((String)packageManager.getApplicationLabel(app));
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_applistview, appsNames);

        ListView listView = (ListView)findViewById(R.id.applist);
        listView.setAdapter(adapter);
    }
}
