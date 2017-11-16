package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
        sb1 = findViewById(R.id.seekBar);
        sb2 = findViewById(R.id.seekBar2);
        sb3 = findViewById(R.id.seekBar3);
        sb1.setMax(timeLimit);

        //it should get from the sqlite
        int default_startPoint = 0;
        sb2.setMax(default_startPoint);
        sb3.setMax(timeLimit-default_startPoint);
        sb1.setProgress(default_startPoint);
        TextView t = findViewById(R.id.textView13);
        t.setText(default_startPoint+" mins");
        t = findViewById(R.id.textView12);
        t.setText(timeLimit+"mins (your time limit)");
        t = findViewById(R.id.textView16);
        t.setText("Start Point: " + default_startPoint+" mins");
        t = findViewById(R.id.textView19);
        t.setText("Left Time: " + (timeLimit - default_startPoint)+" mins");
        //the interval should be stored in sqlite
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView t = findViewById(R.id.textView13);
                t.setText("In " + progressChangedValue+" mins");
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
                //the interval should be stored in sqlite
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
                //the interval should be stored in sqlite
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
                //the interval should be stored in sqlite
            }
        });
    }
}
