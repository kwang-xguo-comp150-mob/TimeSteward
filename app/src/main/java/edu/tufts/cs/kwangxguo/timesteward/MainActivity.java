package edu.tufts.cs.kwangxguo.timesteward;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity {
    private Button signInButton, registerButton, offLineButton, forgotButton;
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.button1);
        offLineButton = findViewById(R.id.button2);
        registerButton = findViewById(R.id.button3);
        forgotButton = findViewById(R.id.forgetButton);
        emailField = findViewById(R.id.input_username);
        passwordField = findViewById(R.id.input_password);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        addListenerOnButton();
    }

    // [START on_start_check_user]
    @Override
    public void onStart(){
        super.onStart();
        final Context context = this;
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(context, Report.class);
            startActivity(intent);
        }
    }
    // [END on_start_check_user]


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void addListenerOnButton() {
        final Context context = this;
        offLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPermissionRequestNotification();
                if(dbexist()) {
                    Intent intent = new Intent(context, Report_offline.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, SettingActivity.class);
                    startActivity(intent);
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPermissionRequestNotification();
                signIn(emailField.getText().toString(), passwordField.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUp.class);
                startActivity(intent);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Please Enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        final Context context = this;
        Log.d("", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Good", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Bad", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

                // [START_EXCLUDE]
                if (!task.isSuccessful()) {
                    //mStatusTextView.setText(R.string.auth_failed);
                } else {
                    if(dbexist()) {
                        Intent intent = new Intent(context, Report.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, SettingActivity.class);
                        startActivity(intent);
                    }
                }
                hideProgressDialog();
                if (task.isSuccessful()) {
                    if(dbexist()) {
                        Intent intent = new Intent(context, Report.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, SettingActivity.class);
                        startActivity(intent);
                    }
                }
                // [END_EXCLUDE]
                }
            });
        // [END sign_in_with_email]
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

    private void sendPermissionRequestNotification() {
        Toast.makeText(getApplicationContext(),
                "You need to grant the usage access through \n\"Setting -> Security -> Apps with usage access -> Time Steward\".",
                Toast.LENGTH_LONG).show();
    }
}
