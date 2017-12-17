package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

class AppListAdapter extends ArrayAdapter {

    private PackageManager pm;
    private ArrayList<String> selectedAppPackageNames;
    private Set<String> packageNamesInDB;
    Context theContext;
    AppListAdapter(Context context, ArrayList<ApplicationInfo> appInfoList, PackageManager pm, ArrayList<String> set) {
        super(context, 0, appInfoList);
        this.theContext = context;
        this.pm = pm;
        this.selectedAppPackageNames = set;
        packageNamesInDB = new HashSet<>();
        SQLiteDatabase db = null;
        String path = context.getDatabasePath("setting.db").getAbsolutePath();
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            Log.d("setting", "AppListAdapter: db doesn't exist !!!!!!!!");
//            db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        }
        if (db != null) {
            try {
                Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
                cursor.moveToFirst();
                String selectedAppNames_string = cursor.getString(0);
                cursor.close();
                db.close();

                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                Gson gson = new Gson();
                packageNamesInDB.addAll((ArrayList<String>) gson.fromJson(selectedAppNames_string, type));
            } catch (Exception e) {
                // table is empty
                Log.d("setting", "AppListAdapter: db is empty !");
            }
        }
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
        Bitmap bitmap = ((BitmapDrawable)appIconDrawable).getBitmap();
        Drawable smallIcon = new BitmapDrawable(theContext.getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        appIcon.setLayoutParams(layoutParams);
        appIcon.setImageDrawable(smallIcon);
        appName.setText(appNameString);

        // deal with check box
        CheckBox cBox = convertView.findViewById(R.id.app_checkbox);
        cBox.setTag(app);
        cBox.setOnCheckedChangeListener(new AppCheckBoxListener(selectedAppPackageNames));

        /*************************************************************
         *  Restore checked states according to database
         ************************************************************/
        String thisPackageName = app.packageName;
        if (packageNamesInDB.contains(thisPackageName)) {
            cBox.setChecked(true);
        }

        return convertView;
    }

}








