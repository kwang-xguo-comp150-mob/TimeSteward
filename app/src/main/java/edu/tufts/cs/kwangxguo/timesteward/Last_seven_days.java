package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class Last_seven_days extends AppCompatActivity {
    private ArrayList<Integer> timelimit;
    private ArrayList<Integer> usagetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_seven_days);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();

        timelimit = new ArrayList<Integer>();
        usagetime = new ArrayList<Integer>();
        //get the timelimit list and usagetime list to replace these later
        for (int i = 0; i < 7; i++) {
            timelimit.add(i*20 + 1);
            usagetime.add(i*20 + 5);
        }

        // should be the date
        String[] labels = new String[7];
        for (int i = 0; i < 7; i++) {
            labels[i] = "date";
        }

        HorizontalBarChart barchart2 = (HorizontalBarChart) findViewById(R.id.barchart2);
        List<BarEntry> yVals1 = new ArrayList<>();
        List<BarEntry> yVals2 = new ArrayList<>();
        for (int i = 0; i < 7; i++){
            yVals1.add(new BarEntry(i,timelimit.get(i),labels[i]));
            yVals2.add(new BarEntry(i,usagetime.get(i)));
        }
        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "Limit Time");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(yVals2, "Usage Time");
        set2.setColor(Color.BLUE);
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
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
        l.setTextSize(8f);
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
}
