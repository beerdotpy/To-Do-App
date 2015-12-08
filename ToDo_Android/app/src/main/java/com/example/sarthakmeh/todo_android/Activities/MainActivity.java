package com.example.sarthakmeh.todo_android.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TimePicker;

import com.example.sarthakmeh.todo_android.Adapters.ToDoCursorAdapter;
import com.example.sarthakmeh.todo_android.R;
import com.example.sarthakmeh.todo_android.BroadcastReceivers.ToDoNotification;
import com.example.sarthakmeh.todo_android.Utils.DBHelper;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int due_time = 15 * 60000; //1 min = 6000 miliseconds
    DBHelper dbHelper;
    ToDoCursorAdapter to_do_adapter;
    ListView lv;
    ImageButton add_task;
    AutoCompleteTextView location;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the DataBase
        dbHelper = new DBHelper(this);

        //Get logged in user
        user = PreferenceManager.getDefaultSharedPreferences(this).getString("user",null);

        //Initialize the layout
        add_task = (ImageButton) findViewById(R.id.add_task);
        lv = (ListView) findViewById(R.id.list);

        //Load the all the To Do items of the user from SQLiteDB
        loadToDOList();

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(MainActivity.this);
                todoTaskBuilder.setTitle("Add Todo Task Item");

                // Layout of the task dialog box
                final LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText todoET = new EditText(MainActivity.this);
                todoET.setHint("Description of Task e.g Meet Vishnu");
                layout.addView(todoET);

                // Google auto complete used for location
                location = new AutoCompleteTextView(MainActivity.this);
                location.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        new GetPlaces().execute(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                layout.addView(location);

                final TimePicker timePicker = new TimePicker(MainActivity.this);
                layout.addView(timePicker);
                final DatePicker datePicker = new DatePicker(MainActivity.this);
                layout.addView(datePicker);

                //Add scrollview as DatePicker take too much of space
                final ScrollView scrollView = new ScrollView(MainActivity.this);
                scrollView.addView(layout);

                todoTaskBuilder.setView(scrollView);

                todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String todoDesc = todoET.getText().toString();
                        String todoLoc = location.getText().toString();
                        String todoTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                        String todoDate = datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
                        String todoDateTime = todoTime + " " + todoDate;

                        dbHelper.insertData(todoDesc, todoDateTime, todoLoc,user);

                        //update the To Do task list UI
                        loadToDOList();

                        /***
                         * Calculate time difference between current time and Task time
                         * Subtract 15mins and push notification
                         */
                        int time_for_notif = (int) calTime(todoDateTime);

                        /***
                         *Push notification when a To Do item is nearing its due time(say) 15minutes
                         */
                        Intent pushNotif = new Intent(MainActivity.this, ToDoNotification.class);
                        pushNotif.putExtra("task", todoDesc + " at " + todoLoc + " on " + todoTime);
                        pushNotif.putExtra("requestCode", time_for_notif);

                        //Set RequestCode to uniqueID so that new alarm doesnt override the old one
                        PendingIntent pintent = PendingIntent.getBroadcast(MainActivity.this, time_for_notif, pushNotif, 0);

                        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        //ADD milliseconds to current time
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.MILLISECOND, time_for_notif);
                        alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pintent);
                    }
                });

                todoTaskBuilder.setNegativeButton("Cancel", null);

                todoTaskBuilder.create().show();

            }
        });
    }

    public void loadToDOList(){

        //Pull All the To_Do items from DB and inflate the list
        Cursor cur = dbHelper.getData(user);
        if(cur!=null){
            to_do_adapter = new ToDoCursorAdapter(MainActivity.this,cur);
            lv.setAdapter(to_do_adapter);
        }

    }

    public long calTime(String taskDate){
        /***
        Subtract current time from task time
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm yyyy/MM/dd");
        Date taskD = null,currentD = null;
        try {
            taskD = simpleDateFormat.parse(taskDate);
            currentD = Calendar.getInstance().getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = taskD.getTime() - currentD.getTime();

        //Subtract 15 * 60000 millisec from difference
        difference = difference - due_time;

        return difference;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().clear().commit();
            LoginManager.getInstance().logOut();
            Intent logout = new Intent(this,Login.class);
            startActivity(logout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class GetPlaces extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;
        JSONArray jsonArray;
        List placesList = new ArrayList<>();

        @Override
        protected String doInBackground(String... place) {
            //Google Api Server Key for Google places API
            String data = "",input = "";
            String key = "key=AIzaSyB93ZRZWToMGoSz_ujmKEIpDjCs5DHhR4A";
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            // Parameters to be sent with web service
            String parameters = input+"&"+key+"&components=country:in";
            //Google auto complete URL
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?"+parameters;
            try{
                // Fetching the data from webs service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                //Convert the data to JSONObject
                jsonObject = new JSONObject(result);
                //Get predictions array from the response
                jsonArray = jsonObject.getJSONArray("predictions");

                //Inflate only the descriptions of places into the autocomplete list
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject place = jsonArray.getJSONObject(i);
                    placesList.add(place.get("description"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1, placesList);
            location.setAdapter(adapter);

        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Problem downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
