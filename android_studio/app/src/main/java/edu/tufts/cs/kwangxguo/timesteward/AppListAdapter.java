package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Guo on 10/5/17.
 */

public class AppListAdapter extends ArrayAdapter {
    PackageManager pm;
    public AppListAdapter(Context context, List<ApplicationInfo> appInfoList, PackageManager pm) {
        super(context, 0, appInfoList);
        this.pm = pm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApplicationInfo app = (ApplicationInfo) getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_applist_item, parent, false);
        }
        // look up views
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.app_icon);
        TextView appName = (TextView)convertView.findViewById(R.id.app_name);
        // populate data into template view
        String appNameString = (String)pm.getApplicationLabel(app);
        Drawable appIconDrawable = (Drawable)app.loadIcon(pm);
        appIcon.setImageDrawable(appIconDrawable);
        appName.setText(appNameString);

        return convertView;
    }
}