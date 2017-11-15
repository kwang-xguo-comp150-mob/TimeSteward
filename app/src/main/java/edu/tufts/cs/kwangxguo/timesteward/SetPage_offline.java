package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SetPage_offline extends AppCompatActivity {

    Button button1, button2, button3, button4, button5, button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_page_offline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4b);
        setSupportActionBar(toolbar);

        button1 = (Button)findViewById(R.id.set_button1b);
        button2 = (Button)findViewById(R.id.set_button2b);
        //button3 = (Button)findViewById(R.id.set_button3b);
        button4 = (Button)findViewById(R.id.set_button4b);
        button5 = (Button)findViewById(R.id.set_button5b);
        button6 = (Button)findViewById(R.id.set_button6);

        addListenerOnButton();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Report.class);
        startActivity(intent);
    }

    public void addListenerOnButton() {
        final Context context = this;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Notification.class);
                startActivity(intent);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, About.class);
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserGuide.class);
                startActivity(intent);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
