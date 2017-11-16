package edu.tufts.cs.kwangxguo.timesteward;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.R.attr.id;
import static edu.tufts.cs.kwangxguo.timesteward.R.id.clear_button;
import static edu.tufts.cs.kwangxguo.timesteward.R.id.start;

public class Notification extends AppCompatActivity {
    private SeekBar sb1,sb2,sb3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //get timelimit
        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        final int timeLimit = cursor.getInt(1);
        cursor.close();
        db.close();

        sb1 = findViewById(R.id.seekBar);
        sb2 = findViewById(R.id.seekBar2);
        sb3 = findViewById(R.id.seekBar3);
        sb1.setMax(timeLimit);

        int default_startPoint = 0;
        // get default_startPoint from SQLite
        SQLiteDatabase db_notification = null;
        String path = this.getDatabasePath("notification.db").getAbsolutePath();
        try {
            db_notification = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch(SQLiteException e) {
            Log.d("notification", "AppListAdapter: db doesn't exist !!!!!!!!");
        }
        if (db_notification != null) {
            Cursor cursor_notification = db_notification.rawQuery("SELECT * FROM Notification", null);
            cursor_notification.moveToFirst();
            try {
                default_startPoint = cursor_notification.getInt(0);
                Log.d("notification", "onCreate: default_startPoint  " + default_startPoint);
                cursor_notification.close();
            } catch(RuntimeException e) {
                Log.d("notification", "onCreate: db exist, but table is empty! ");
            }
        } else {
            db_notification = openOrCreateDatabase("notification.db", Context.MODE_PRIVATE, null);
            db_notification.execSQL("CREATE TABLE IF NOT EXISTS Notification(start_point, gentle_interval, intense_interval)");
            db_notification.execSQL("DELETE FROM Notification");
//            ContentValues value = new ContentValues();
//            db_notification.insert("Setting", null, value);
        }
        sb2.setMax(default_startPoint);
        sb3.setMax(timeLimit - default_startPoint);
        sb1.setProgress(default_startPoint);
        TextView t = findViewById(R.id.textView13);
        t.setText(default_startPoint + " mins");
        t = findViewById(R.id.textView12);
        t.setText(timeLimit + "mins \n(your time limit)");
        t = findViewById(R.id.textView16);
        t.setText("Start Point: " + default_startPoint + " mins");
        t = findViewById(R.id.textView19);
        t.setText("Left Time: " + (timeLimit - default_startPoint) + " mins");
        //the interval should be stored in sqlite

        final ContentValues value = new ContentValues();
        value.put("start_point", 0);
        value.put("gentle_interval", 0);
        value.put("intense_interval", 0);

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView t = findViewById(R.id.textView13);
                t.setText("In " + progressChangedValue+" mins used");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Notification.this, "You just chose the intense alarm start point:"
                                + progressChangedValue + "mins", Toast.LENGTH_SHORT).show();
                sb2.setMax(progressChangedValue);
                sb3.setMax(timeLimit - progressChangedValue);
                TextView t = findViewById(R.id.textView16);
                t.setText("Start Point: " + progressChangedValue+" mins");
                t = findViewById(R.id.textView19);
                t.setText("Left Time: " + (timeLimit - progressChangedValue)+" mins");

                // store the start_point in SQLite
//                SQLiteDatabase db = openOrCreateDatabase("notification.db", Context.MODE_PRIVATE, null);
                value.put("start_point", progressChangedValue);
            }
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView t = findViewById(R.id.textView15);
                t.setText("Every " + progressChangedValue + " mins");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Notification.this, "You just chose the gentle alarm interval:"
                        + progressChangedValue + "mins", Toast.LENGTH_SHORT).show();
                value.put("gentle_interval", progressChangedValue);
            }
        });

        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView t = findViewById(R.id.textView18);
                t.setText("Every " + progressChangedValue + " mins");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Notification.this, "You just chose the intense alarm interval:"
                        + progressChangedValue + "mins", Toast.LENGTH_SHORT).show();
                value.put("intense_interval", progressChangedValue);
            }
        });

        Button confirm_button = findViewById(R.id.button4);
        final SQLiteDatabase finalDb_notification = db_notification;
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)value.get("start_point") == 0 || (int)value.get("gentle_interval") == 0 ||
                        (int)value.get("intense_interval") == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Entry cannot be 0 minute",
                            Toast.LENGTH_SHORT).show();
                } else {
                    finalDb_notification.insert("Notification", null, value);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null) {
                        Intent intent = new Intent(getApplicationContext(), Report.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Report_offline.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

}
