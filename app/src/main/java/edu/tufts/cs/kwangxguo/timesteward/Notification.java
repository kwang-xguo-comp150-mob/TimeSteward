package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Notification extends AppCompatActivity {
    private SeekBar sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //get timelimit
        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        final int timeLimit = cursor.getInt(1);
        sb = findViewById(R.id.seekBar);
        sb.setMax(timeLimit);

        //it should get from the sqlite
        int default_startPoint = 1;

        sb.setProgress(default_startPoint);
        TextView t = findViewById(R.id.textView13);
        t.setText(default_startPoint+" mins");
        t = findViewById(R.id.textView12);
        t.setText(timeLimit+"mins (your time limit)");
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                TextView t = findViewById(R.id.textView13);
                t.setText("Every " + progressChangedValue+" mins");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Notification.this, "You just chose the alarm interval:"
                                + progressChangedValue + "mins", Toast.LENGTH_SHORT).show();
                //the interval should be stored in sqlite
            }
        });
    }
}
