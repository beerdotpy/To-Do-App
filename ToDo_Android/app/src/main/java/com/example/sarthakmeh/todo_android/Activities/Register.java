package com.example.sarthakmeh.todo_android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthakmeh.todo_android.R;
import com.example.sarthakmeh.todo_android.Utils.DBHelper;

public class Register extends Activity {

    EditText edName,edEmail,edPass,edRePass;
    Button submit;
    DBHelper dbHelper;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Initialize the database
        dbHelper = new DBHelper(this);

        //Initialize the layouts
        edName = (EditText) findViewById(R.id.name);
        edEmail = (EditText) findViewById(R.id.email);
        edPass = (EditText) findViewById(R.id.pass);
        edRePass = (EditText) findViewById(R.id.repass);
        submit = (Button) findViewById(R.id.submit);

        /***
         * When user clicks submit check if user has filled all the fields
         * If yes then check if both passwords are same */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String email = edEmail.getText().toString();
                String pass = edPass.getText().toString();
                String repass = edRePass.getText().toString();

                if(!name.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
                    if (pass.matches(repass)) {
                        if(dbHelper.insertUser(name,email,pass)) {
                            Intent startMain = new Intent(Register.this,MainActivity.class);
                            Toast.makeText(Register.this,"Welcome "+name,Toast.LENGTH_LONG).show();
                            editor = prefs.edit();
                            editor.putBoolean("isLoggedIn",true);
                            editor.putString("user",email);
                            editor.commit();
                            startActivity(startMain);
                        }else{
                            Toast.makeText(Register.this,"Some problem occurred.Please try again!",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(Register.this,"Password do not match",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Register.this,"Please fill all the fields",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
