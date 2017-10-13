package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    }

}
