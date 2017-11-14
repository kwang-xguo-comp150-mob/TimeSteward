package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Last_seven_days extends AppCompatActivity {
    private ArrayList<Integer> timelimit;
    private ArrayList<Integer> usagetime;
    private ImageButton left;
    private ImageButton right;
    private int leftCount;
    private String[] dates;
    private String[] labels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_seven_days);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        mCheckInforInServer();
        addListenerOnButton();
        leftCount = 0;

    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        //when retrieve data successfully, draw the chart
//
//    }

    public interface OnGetDataListener {
        void onStart();
        void onSuccess(DataSnapshot data);
        void onFailed(DatabaseError databaseError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }
    public void onMenuAction(MenuItem mi){

    }
    public void onSettingAction(MenuItem mi){
        Intent intent = new Intent(this, SetPage.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Report.class);
        startActivity(intent);
    }

    public void addListenerOnButton() {
        final Context context = this;
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftCount++;
                mCheckInforInServer();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftCount > 0 ) leftCount--;
                mCheckInforInServer();
            }
        });
    }

    public void set_barValues(final OnGetDataListener listener){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // date
        labels = new String[7];
        dates = new String[7];
        int nth = 0;
        for (int i = 7*(leftCount+1)-1; i >= 7*leftCount; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-i);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            String date2 = new SimpleDateFormat("MM-dd").format(cal.getTime());
            labels[nth] = date;
            dates[nth] = date2;
            nth++;
        }
        //get the timelimit list and usagetime list to replace these later
        timelimit = new ArrayList<Integer>();
        usagetime = new ArrayList<Integer>();

        if (mDatabase != null){
            listener.onStart();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (int i = 0; i < 7; i++) {
                        String id = user.getUid() + "_" + labels[i];
                        User u = dataSnapshot.child("test").child(id).getValue(User.class);
                        if (u != null){
                            timelimit.add(u.timelimit);
                            usagetime.add(u.usagetime);
                        }
                        else {
                            timelimit.add(0);
                            usagetime.add(0);
                        }
                    }
                    listener.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    listener.onFailed(databaseError);
                }
            });
        }
    }

    private void mCheckInforInServer(){
        set_barValues(new OnGetDataListener() {
            @Override
            public void onStart() {
                Log.d("listener","onStart");
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Log.d("listener","onSuccess");
                draw_bar(timelimit,usagetime,dates);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.d("listener","onFailed");
            }
        });
    }

    public void draw_bar(ArrayList<Integer> timelimit, ArrayList<Integer> usagetime, String dates[]){
        HorizontalBarChart barchart2 = (HorizontalBarChart) findViewById(R.id.barchart2);
        List<BarEntry> yVals1 = new ArrayList<>();
        List<BarEntry> yVals2 = new ArrayList<>();
        for (int i = 0; i < 7; i++){
            yVals1.add(new BarEntry(i,timelimit.get(i),dates[i]));
            yVals2.add(new BarEntry(i,usagetime.get(i)));
        }
        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "Limit Time");
        set1.setColor(0xFFffa860);
        set2 = new BarDataSet(yVals2, "Usage Time");
        set2.setColor(0xFF59C2E5);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        barchart2.setData(data);
        barchart2.getBarData().setBarWidth(0.3f);

        barchart2.getXAxis().setAxisMinimum(0);
        barchart2.getXAxis().setAxisMaximum(0 + barchart2.getBarData().getGroupWidth(0.4f, 0f) * 7);
        barchart2.groupBars(0, 0.4f, 0f);
        barchart2.getData().setHighlightEnabled(false);
        barchart2.invalidate();

        //X-axis
        XAxis xAxis = barchart2.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        //Y-axis
        barchart2.getAxisRight().setEnabled(false);
        YAxis leftAxis = barchart2.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        barchart2.getDescription().setEnabled(false);

        Legend l = barchart2.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(6f);
    }
}
