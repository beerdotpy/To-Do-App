package com.example.sarthakmeh.todo_android.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    final int WRITE_EXTERNAL_STORAGE = 1;
    Button login, register;
    EditText email, pass;
    DBHelper db;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Boolean isLoggedIn=false;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Initialize the FB login before inflating the layout to avoid
         inflating error
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        //Ask for Write permission (needed for android 6.0 versions)
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Login.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);
        }

        /*
         Check if user has already logged in or not
          */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isLoggedIn = prefs.getBoolean("isLoggedIn",false);

        if(isLoggedIn){

            Intent start = new Intent(Login.this, MainActivity.class);
            Toast.makeText(Login.this, "Welcome", Toast.LENGTH_LONG).show();
            startActivity(start);

        }else {

            //Initialize the DataBase
            db = new DBHelper(this);

            //Initialize the layouts
            email = (EditText) findViewById(R.id.email);
            pass = (EditText) findViewById(R.id.password);
            login = (Button) findViewById(R.id.login);
            register = (Button) findViewById(R.id.register);
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.registerCallback(callbackManager, callback);

            //When login button is clicked check if credentials user entered are correct or not
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Query DB and check for the user
                    Boolean userExist = db.checkUser(email.getText().toString(), pass.getText().toString());
                    //If user exist and credentials are correct continue else display Toast
                    if (userExist) {

                        Intent start = new Intent(Login.this, MainActivity.class);
                        editor = prefs.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.putString("user",email.getText().toString());
                        editor.commit();
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
    }

    //If user click on FB login button then login him through facebook (FBGraph API used)
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            //FB login successfull
                            Intent start = new Intent(Login.this, MainActivity.class);
                            editor = prefs.edit().putBoolean("isLoggedIn",true);
                            editor.commit();
                            Toast.makeText(Login.this, "Welcome", Toast.LENGTH_LONG).show();
                            startActivity(start);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.d("result","cancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("result","exception");
        }
    };

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
