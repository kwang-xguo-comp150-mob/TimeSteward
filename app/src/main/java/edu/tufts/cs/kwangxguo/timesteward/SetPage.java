package edu.tufts.cs.kwangxguo.timesteward;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SetPage extends AppCompatActivity {
    Button button1, button2, button3, button4, button5, button6, button7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        button1 = (Button)findViewById(R.id.set_button1);
        button2 = (Button)findViewById(R.id.set_button2);
        button3 = (Button)findViewById(R.id.set_button3);
        button4 = (Button)findViewById(R.id.set_button4);
        button5 = (Button)findViewById(R.id.set_button5);
        button6 = (Button)findViewById(R.id.set_button6);
        button7 = (Button)findViewById(R.id.set_button7);

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

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, About.class);
                startActivity(intent);
            }
        });
    }
}
