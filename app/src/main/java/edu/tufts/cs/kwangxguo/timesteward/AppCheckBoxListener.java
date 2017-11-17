package edu.tufts.cs.kwangxguo.timesteward;

import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.ArrayList;

class AppCheckBoxListener implements CompoundButton.OnCheckedChangeListener {

    private ArrayList<String> selectedAppPackageNames;
    public AppCheckBoxListener(ArrayList<String> selectedAppSet) {
        this.selectedAppPackageNames = selectedAppSet;
    }

    @Override
    public void onCheckedChanged(CompoundButton cBox, boolean checked) {
        if (checked) {
            selectedAppPackageNames.add(((ApplicationInfo)cBox.getTag()).packageName);
        }
        else {
            selectedAppPackageNames.remove(((ApplicationInfo)cBox.getTag()).packageName);
        }
    }
}
