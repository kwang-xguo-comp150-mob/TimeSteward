package edu.tufts.cs.kwangxguo.timesteward;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SetPage extends AppCompatActivity {
    Button button1, button2, button3, button4, button5, button6, button7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_page);

        Toolbar toolbar = findViewById(R.id.toolbar4b);
        setSupportActionBar(toolbar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        button1 = findViewById(R.id.set_button1a);
        button2 = findViewById(R.id.set_button2a);
        button4 = (Button)findViewById(R.id.set_button4a);
        button5 = (Button)findViewById(R.id.set_button5a);
        button7 = (Button)findViewById(R.id.set_button7);
        button7.setText("Sign Out (" + email+")");
        addListenerOnButton();
    }

    @Override
    public void onBackPressed() {
        if (dbexist()) {
            Intent intent = new Intent(this, Report.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
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

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance() != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean dbexist() {
        SQLiteDatabase checkDB = null;
        try{
            Context context = this;
            String path = context.getDatabasePath("setting.db").getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        if (checkDB == null) return false;
        if (dbIsEmpty(checkDB)) {
            this.deleteDatabase("setting.db");
            return false;
        } else {
            checkDB.close();
            return true;
        }
    }

    private boolean dbIsEmpty(SQLiteDatabase db) {
        boolean empty = true;
        Cursor cur = null;
        try {
            cur = db.rawQuery("SELECT COUNT(*) FROM Setting", null);
        } catch (SQLiteException e) {
            // Setting table doesn't exist
        }
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        if (cur != null) cur.close();
        Log.d("main", "dbIsEmpty: db is not empty");
        return empty;
    }

}
