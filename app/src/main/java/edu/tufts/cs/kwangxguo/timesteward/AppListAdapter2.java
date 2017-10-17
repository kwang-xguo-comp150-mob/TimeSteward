package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wangkeyue on 10/15/17.
 */

class AppListAdapter2 extends ArrayAdapter {

    private PackageManager pm;
    private ArrayList<String> selectedAppPackageNames;
    private HashMap<String, Integer> usageTime;
    AppListAdapter2(Context context, ArrayList<ApplicationInfo> appInfoList, PackageManager pm, ArrayList<String> set, HashMap<String, Integer> usageTime) {
        super(context, 0, appInfoList);
        this.pm = pm;
        this.selectedAppPackageNames = set;
        this.usageTime = usageTime;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ApplicationInfo app = (ApplicationInfo) getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.applist_usage_time, parent, false);
        }
        // look up views
        ImageView appIcon = convertView.findViewById(R.id.app_icon2);
        TextView appName = convertView.findViewById(R.id.app_name2);
        TextView appTime = convertView.findViewById(R.id.app_time);
        // populate data into template view
        String appNameString = app.packageName;
        Drawable appIconDrawable = app != null ? app.loadIcon(pm) : null;
        int appUsageTime;
        if (usageTime.containsKey(appNameString)) appUsageTime = usageTime.get(appNameString);
        else appUsageTime = 0;
        appIcon.setImageDrawable(appIconDrawable);
        appName.setText(pm.getApplicationLabel(app));
        appTime.setText(appUsageTime+" mins");
        return convertView;
    }


}
