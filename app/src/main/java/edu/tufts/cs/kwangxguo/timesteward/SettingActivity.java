package edu.tufts.cs.kwangxguo.timesteward;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class SettingActivity extends AppCompatActivity {
    private PackageManager packageManager;
    private Button confirm_button;
    private Button clear_button;
    private AppListAdapter appListAdapter;
    private Activity settingActivity;
    private ArrayList<ApplicationInfo> installedApps;
    private ArrayList<String> selectedAppPackageNames;
    private int timeLimit; // in minutes
    private final int[] time = {0,0}; // hour, minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.settingActivity = SettingActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        /*******************************************************
         *             Create App List:
         *   Using customized adapter, display icon and app name
         *******************************************************/
        packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        installedApps = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            // check if the app is a system app
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 ||
                    app.packageName.equals("com.android.chrome") ||
                    app.packageName.equals("com.google.android.youtube")) {
                installedApps.add(app);
            }
        }
        /* create a list to store selected app's package name */
        selectedAppPackageNames = new ArrayList<>();
        // create an instance of my customized adapter
        appListAdapter = new AppListAdapter(this, installedApps, packageManager, selectedAppPackageNames);

        ListView listView = findViewById(R.id.applist);
        /* set the height of the listView */
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();
        lp.height = 150 * installedApps.size();
        listView.setLayoutParams(lp);
        listView.setAdapter(appListAdapter);

        /***********************************
         *          Set Time Limit          *
         ************************************/
        NumberPicker np_minute = findViewById(R.id.timer_minute);
        NumberPicker np_hour = findViewById(R.id.timer_hour);

        //first check if there is data for time limit, this is for setting the default value
        int stored_timeLimit_hour = 0;
        int stored_timeLimit_minute = 0;
        if (dbexist()){
            SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
            cursor.moveToFirst();
            int stored_timeLimit = cursor.getInt(1);
            stored_timeLimit_hour = stored_timeLimit / 60;
            stored_timeLimit_minute = stored_timeLimit % 60;
            time[0] = stored_timeLimit_hour;
            time[1] = stored_timeLimit_minute;
            cursor.close();
            db.close();
        }


        np_minute.setMinValue(0);
        np_minute.setMaxValue(59);
        np_minute.setValue(stored_timeLimit_minute);
        np_minute.setWrapSelectorWheel(true);

        np_hour.setMinValue(0);
        np_hour.setMaxValue(23);
        np_hour.setValue(stored_timeLimit_hour);
        np_hour.setWrapSelectorWheel(true);
        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                time[0] = newVal;
            }
        });
        np_minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                time[1] = newVal;
            }
        });

        /************************************
         *         Deal with Buttons        *
         ************************************/
        confirm_button = findViewById(R.id.confirm_button);

        clear_button = findViewById(R.id.clear_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear database
                settingActivity.deleteDatabase("setting.db");
                selectedAppPackageNames.clear();
                settingActivity.recreate();
            }
        });
        addListenerOnButton();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addListenerOnButton() {
        final Context context = this;
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeLimit = (time[0] * 60) + time[1];
                if (selectedAppPackageNames.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one App",
                            Toast.LENGTH_SHORT).show();
                } else if (timeLimit == 0) {
                    Toast.makeText(getApplicationContext(),
                            "TimeLimit cannot be 0 minute",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, Report.class);
                    //use sqlite to store timelimit and selectedapplist
                    Gson gson = new Gson();
                    String gsonString = gson.toJson(new ArrayList<String>(selectedAppPackageNames));
                    SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS Setting(app_package_name_list, time_limit);");
                    db.execSQL("DELETE FROM Setting");
                    ContentValues value = new ContentValues();
                    value.put("app_package_name_list", gsonString);
                    value.put("time_limit", timeLimit);
                    db.insert("Setting", null, value);
                    db.close();

                    startActivity(intent);
                }
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    public void onSettingAction(MenuItem mi){
        Intent intent = new Intent(this, SetPage.class);
        startActivity(intent);
    }

    private boolean dbexist() {
        SQLiteDatabase checkDB = null;
        try{
            Context context = this;
            String path = context.getDatabasePath("setting.db").getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        if (checkDB == null) return false;
        if (dbIsEmpty(checkDB)) {
            this.deleteDatabase("setting.db");
            return false;
        } else {
            checkDB.close();
            return true;
        }
    }

    private boolean dbIsEmpty(SQLiteDatabase db) {
        boolean empty = true;
        Cursor cur = null;
        try {
            cur = db.rawQuery("SELECT COUNT(*) FROM Setting", null);
        } catch (SQLiteException e) {
            // Setting table doesn't exist
        }
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        if (cur != null) cur.close();
        Log.d("main", "dbIsEmpty: db is not empty");
        return empty;
    }
}