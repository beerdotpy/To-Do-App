package com.example.sarthakmeh.todo_android.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sarthakmeh.todo_android.R;

/**
 * Created by sarthakmeh on 6/12/15.
 */
public class ToDoCursorAdapter extends CursorAdapter {

    public ToDoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.to_do_task, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBody = (TextView) view.findViewById(R.id.todoTask);
        TextView tvLoc = (TextView) view.findViewById(R.id.location);
        TextView tvStatus = (TextView) view.findViewById(R.id.status);
        TextView tvTime = (TextView) view.findViewById(R.id.datetime);
        // Extract properties from cursor
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String body = cursor.getString(cursor.getColumnIndexOrThrow("task"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String datetime = cursor.getString(cursor.getColumnIndexOrThrow("time"));
        // Populate fields with extracted properties
        tvBody.setText(id+". "+body);
        tvTime.setText(datetime);
        tvLoc.setText("Location: "+location);
        tvStatus.setText(status);
    }
}
