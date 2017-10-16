package edu.tufts.cs.kwangxguo.timesteward;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.util.Log;

public class TestService extends JobService {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TestService", "onCreate: ---------- job created ----------");
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("TestService", "onStartJob: ----------- job started ----------");

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("TestService", "onStopJob: ------------ job stopped ---------");

        return false;
    }

    @Override
    public void onDestroy() {
        Log.d("TestService", "onDestroy: ------------ Job Destroy -----------");

        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(this, TestService.class));
        builder.setMinimumLatency(2000);

        JobScheduler js = getSystemService(JobScheduler.class);
        int code = js.schedule(builder.build());
        if (code <= 0) Log.d("TestService", "onCreate: _______ Job scheduling failed --------");
        Log.d("TestService", "onCreate: -------- Job scheduled ---------");
    }
}
