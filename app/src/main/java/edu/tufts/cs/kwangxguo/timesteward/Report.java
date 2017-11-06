package edu.tufts.cs.kwangxguo.timesteward;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends AppCompatActivity {
    private AppListAdapter2 appListAdapter;
    private PackageManager packageManager;
    private int timeLimit;
    private int timeRemain;
    private int usagetime;
    private ArrayList<ApplicationInfo> selectedApps;
    private ArrayList<String> selectedAppPackageNames;
    private HashMap<String,Integer> usageTime = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        packageManager = getPackageManager();
        selectedApps = new ArrayList<>();

        SQLiteDatabase db = openOrCreateDatabase("setting.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Setting", null);
        cursor.moveToFirst();
        String selectedAppNames_string = cursor.getString(0);
        timeLimit = cursor.getInt(1);
        cursor.close();
        db.close();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        selectedAppPackageNames = gson.fromJson(selectedAppNames_string, type);

        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            if (selectedAppPackageNames.contains(app.packageName)) {
                selectedApps.add(app);
            }
        }

        for (String name : selectedAppPackageNames) Log.d("report", "onCreate: selected: " + name);
    }

    @Override
    public void onStart() {
        super.onStart();
        usagetime = 0;
        timeRemain = 0;
        getUsage();

        //piechart
        PieChart piechart = (PieChart) findViewById(R.id.chart);
        //piechart.setUsePercentValues(true);
        //create dataset for the piechart
        List<PieEntry> yvalues = new ArrayList<PieEntry>();
        yvalues.add(new PieEntry(usagetime,"Total Time Limit"));
        yvalues.add(new PieEntry(timeRemain,"Remaining Time"));
        PieDataSet dataSet = new PieDataSet(yvalues, "Time");
        PieData data = new PieData(dataSet);
        piechart.setData(data);
        piechart.invalidate();
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        piechart.setEntryLabelColor(1);
        piechart.setContentDescription("Usage summary");
        piechart.getDescription().setEnabled(false);

        //bar chart
        HorizontalBarChart barchart = (HorizontalBarChart) findViewById(R.id.barchart);
        List<BarEntry> valueSet = new ArrayList<>();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) barchart.getLayoutParams();
        lp.height = 100 * selectedApps.size();
        if (lp.height < 300) lp.height = 300;
        barchart.setLayoutParams(lp);
        String[] labels = new String[selectedApps.size()];
        for (int i = 0; i < selectedApps.size(); i++){
                labels[i] = (String)packageManager.getApplicationLabel(selectedApps.get(i));
                int time = 0;
                if (usageTime.get(selectedApps.get(i).packageName) != null)
                    time = usageTime.get(selectedApps.get(i).packageName);
                BarEntry e = new BarEntry(i,
                        time,labels[i]);
                valueSet.add(e);
        }
        BarDataSet barDataSet = new BarDataSet(valueSet, "");

        BarData bardata = new BarData(barDataSet);
        bardata.setBarWidth(0.6f);
        barchart.setData(bardata);
        barchart.invalidate();
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        //hide some axises, labels and gridlines
        barchart.getDescription().setEnabled(false);
        barchart.getLegend().setEnabled(false);
        barchart.getXAxis().setDrawGridLines(false);
        barchart.getXAxis().setDrawAxisLine(false);
        barchart.getXAxis().setGranularityEnabled(true);
        barchart.getXAxis().setLabelCount(selectedApps.size());
        barchart.getXAxis().setGranularity(1f);
        barchart.getXAxis().setValueFormatter(new LabelFormatter(labels));

        barchart.getAxisLeft().setDrawTopYLabelEntry(false);
        barchart.getAxisLeft().setDrawTopYLabelEntry(false);
        barchart.getAxisLeft().setDrawLimitLinesBehindData(false);
        barchart.getAxisLeft().setDrawAxisLine(false);
        barchart.getAxisLeft().setDrawZeroLine(false);
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getAxisLeft().setDrawLabels(false);

        barchart.getAxisRight().setDrawZeroLine(false);
        barchart.getAxisRight().setDrawLimitLinesBehindData(false);
        barchart.getAxisRight().setDrawAxisLine(false);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.getAxisRight().setDrawTopYLabelEntry(false);
        barchart.getAxisRight().setDrawLabels(false);

        /********************************************************************
         *        Schedule Sticky Background Monitor Service
         ********************************************************************/
        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(this, BackgroundMonitor.class));
        builder.setMinimumLatency((long)5e3);
        JobScheduler js = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int code = js.schedule(builder.build());
        if (code <= 0) Log.d("monitor", "report: _______ Job scheduling failed --------");
        else Log.d("monitor", "report: -------- Job scheduled ---------");
        Log.d("report", "onStart: onstart called !!!!!!!!!!!!!!!!!!");
    }

    public class LabelFormatter implements IAxisValueFormatter {
        private String[] mLabels;

        public LabelFormatter(String[] labels) {
            this.mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Log.d("value is", "" + value);
            return mLabels[(int) value];
        }
    }

    public void getUsage() {
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 1);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long beginTime = today.getTimeInMillis();
        long currTime = System.currentTimeMillis();

        Map<String, UsageStats> uStatsMap = usm.queryAndAggregateUsageStats(beginTime, currTime);

        usageTime = new HashMap<>();
        for (Map.Entry<String, UsageStats> entry : uStatsMap.entrySet()) {
            String packageName = entry.getKey();
            UsageStats us = entry.getValue();
            Log.d("report", "getUsage: " + packageName + "  " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
            if (us.getTotalTimeInForeground() < 1e4) continue;
            if (selectedAppPackageNames.contains(packageName)) {
                Log.d("setting", "getUsage: " + packageName + "  usage time: " + us.getTotalTimeInForeground() / 6e4 + " minutes.");
                Log.d("setting", "getUsage: in " + (currTime - beginTime) / 1000 / 3600.0 + " hours");
                usagetime += us.getTotalTimeInForeground() / 6e4;
                usageTime.put(packageName, (int)(us.getTotalTimeInForeground() / 6e4));
            }
        }
        timeRemain = (timeLimit - usagetime) > 0 ? timeLimit - usagetime : 0;
        Log.d("report", "getUsage: total time:" + usagetime);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

}
