package edu.tufts.cs.kwangxguo.timesteward;

import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.CompoundButton;

import java.util.Set;

/**
 * Created by Guo on 10/6/17.
 */

class AppCheckBoxListener implements CompoundButton.OnCheckedChangeListener {
    private Set<ApplicationInfo> selectedAppSet;
    public AppCheckBoxListener(Set<ApplicationInfo> selectedAppSet) {
        this.selectedAppSet = selectedAppSet;
    }
    @Override
    public void onCheckedChanged(CompoundButton cBox, boolean checked) {
        if (checked) {
            selectedAppSet.add((ApplicationInfo) cBox.getTag());
            Log.d("cBox", "onCheckedChanged: app: " + cBox.getTag());
        }
    }
}
