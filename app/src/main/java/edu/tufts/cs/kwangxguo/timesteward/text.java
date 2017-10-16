package edu.tufts.cs.kwangxguo.timesteward;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class text extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent currIntent = getIntent();
        Bundle b = currIntent.getExtras();
        int timeLimit = b.getInt("timeLimit", 0);
        ArrayList<String> appList = b.getStringArrayList("selectedPackageNames");

        TextView t1 = (TextView) findViewById(R.id.test1);
        t1.setText("timeLimit is: " + timeLimit);
        TextView t2 = (TextView) findViewById(R.id.test2);
        t2.setText("Number of app selected: " + appList.size());

        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        String gsonString = cursor.getString(0);
        int time = cursor.getInt(1);

        while (! cursor.isAfterLast()) {
            Log.d("test", "onCreate: " + cursor.getString(0) + " " + cursor.getInt(1));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        ArrayList<String> lst = gson.fromJson(gsonString, type);

        TextView t3 = (TextView) findViewById(R.id.test3);
        TextView t4 = (TextView) findViewById(R.id.test4);
        t3.setText("1 " + String.valueOf(time));
//        t4.setText("2 " + lst.size() + "");
        t4.setText("2 " + gsonString);

        // test for TestService
        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(this, TestService.class));
        builder.setOverrideDeadline(1000);

        JobScheduler js = (JobScheduler) getSystemService(this.JOB_SCHEDULER_SERVICE);
        int code = js.schedule(builder.build());
        if (code <= 0) Log.d("TestService", "onCreate: _______ Job scheduling failed --------");
        Log.d("TestService", "onCreate: -------- Job scheduled ---------");
    }
}
