package edu.tufts.cs.kwangxguo.timesteward;

import android.app.job.JobParameters;
import android.app.job.JobService;
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

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("TestService", "onStopJob: ------------ job stopped ---------");

        return true;
    }

    @Override
    public void onDestroy() {
        Log.d("TestService", "onDestroy: ------------ Job Destroy -----------");
    }
}
