package com.example.sarthakmeh.todo_android.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthakmeh.todo_android.R;
import com.example.sarthakmeh.todo_android.Utils.DBHelper;

public class Login extends AppCompatActivity {

    final int WRITE_EXTERNAL_STORAGE = 1;
    Button login, register;
    EditText email, pass;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ask for Read and Write permission (needed for android 6.0 versions)
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Login.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);
        }

        //Initialize the DataBase
        db = new DBHelper(this);

        //Initialize the layouts
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        //When login button is clicked check if credentials user entered are correct or not
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean userExist = db.checkUser(email.getText().toString(), pass.getText().toString());
                //If user exist and credentials are correct take him to Main Screen
                if (userExist) {
                    Intent start = new Intent(Login.this, MainActivity.class);
                    Toast.makeText(Login.this, "Welcome", Toast.LENGTH_LONG).show();
                    startActivity(start);
                } else {
                    Toast.makeText(Login.this, "Sorry user not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        //When register button is clicked launch register screen
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Login.this, Register.class);
                startActivity(register);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Todoist","WRITE permission granted");
                } else {
                    // permission denied, so close the app with toast
                    Toast.makeText(Login.this, "Sorry app needs permission to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
}
