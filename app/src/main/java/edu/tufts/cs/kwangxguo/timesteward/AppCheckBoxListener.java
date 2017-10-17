package edu.tufts.cs.kwangxguo.timesteward;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Guo on 10/6/17.
 */

class AppCheckBoxListener implements CompoundButton.OnCheckedChangeListener {

    private ArrayList<String> selectedAppPackageNames;
    public AppCheckBoxListener(ArrayList<String> selectedAppSet) {
        this.selectedAppPackageNames = selectedAppSet;
    }

    @Override
    public void onCheckedChanged(CompoundButton cBox, boolean checked) {
        if (checked) {
            selectedAppPackageNames.add(((ApplicationInfo)cBox.getTag()).packageName);
            Log.d("setting_cBox", "onCheckedChanged: app: " + ((ApplicationInfo) cBox.getTag()));
        }
        else {
            selectedAppPackageNames.remove(((ApplicationInfo)cBox.getTag()).packageName);
            Log.d("setting_cBox", "onCheckedChanged: removeapp: " + ((ApplicationInfo) cBox.getTag()));
        }
    }
}
