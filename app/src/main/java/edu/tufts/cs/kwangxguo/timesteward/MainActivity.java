package edu.tufts.cs.kwangxguo.timesteward;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button2);
        addListenerOnButton();
    }
    public void addListenerOnButton() {
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(dbexist()) {
                Intent intent = new Intent(context, report.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
            }
            }
        });
    }

    private boolean dbexist() {
        SQLiteDatabase checkDB = null;
        try{
            Context context = this;
            String path = context.getDatabasePath("setting.db").getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        }
        catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }
}
