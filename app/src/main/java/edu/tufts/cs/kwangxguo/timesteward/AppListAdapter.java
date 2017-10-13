package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

/**
 * Created by Guo on 10/5/17.
 */

class AppListAdapter extends ArrayAdapter {

    private PackageManager pm;
    private Set<String> selectedAppPackageNames;
    AppListAdapter(Context context, List<ApplicationInfo> appInfoList, PackageManager pm, Set<String> set) {
        super(context, 0, appInfoList);
        this.pm = pm;
        this.selectedAppPackageNames = set;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ApplicationInfo app = (ApplicationInfo) getItem(position);
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_applist_item, parent, false);
        }
        // look up views
        ImageView appIcon = convertView.findViewById(R.id.app_icon);
        TextView appName = convertView.findViewById(R.id.app_name);
        // populate data into template view
        String appNameString = (String)pm.getApplicationLabel(app);
        Drawable appIconDrawable = app != null ? app.loadIcon(pm) : null;
        appIcon.setImageDrawable(appIconDrawable);
        appName.setText(appNameString);

        // deal with check box
        CheckBox cBox = convertView.findViewById(R.id.app_checkbox);
        cBox.setTag(app);
        cBox.setOnCheckedChangeListener(new AppCheckBoxListener(selectedAppPackageNames));
        return convertView;
    }

}