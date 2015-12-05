package com.example.sarthakmeh.todo_android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.sarthakmeh.todo_android.Adapter.ToDoCursorAdapter;
import com.example.sarthakmeh.todo_android.R;
import com.example.sarthakmeh.todo_android.Utils.DBHelper;

public class MainActivity extends Activity {

    public static String TAG = "Todoist";
    DBHelper dbHelper;
    ToDoCursorAdapter to_do_adapter;
    ListView lv;
    ImageButton add_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the DataBase
        dbHelper = new DBHelper(this);

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
                final EditText location = new EditText(MainActivity.this);
                location.setHint("Location e.g IIT DELhi");
                layout.addView(location);
                final TimePicker timePicker= new TimePicker(MainActivity.this);
                layout.addView(timePicker);
                final DatePicker datePicker = new DatePicker(MainActivity.this);
                layout.addView(datePicker);
                todoTaskBuilder.setView(layout);

                todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String todoDesc = todoET.getText().toString();
                        String todoLoc = location.getText().toString();
                        String todoTime = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();
                        String todoDate = datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
                        String todoDateTime = todoTime + " " + todoDate;

                        //Insert Task into DB and set status as Pending
                        dbHelper.insertData(todoDesc,todoDateTime,todoLoc,"Pending");
                        //update the Todo task list UI
                        loadToDOList();

                    }
                });

                todoTaskBuilder.setNegativeButton("Cancel", null);

                todoTaskBuilder.create().show();

            }
        });

    }

    public void loadToDOList(){

        //Pull All the To_Do items from DB and inflate the list
        Cursor cur = dbHelper.getData();
        if(cur!=null){
            to_do_adapter = new ToDoCursorAdapter(MainActivity.this,cur);
            lv.setAdapter(to_do_adapter);
        }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
