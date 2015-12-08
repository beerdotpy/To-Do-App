package com.example.sarthakmeh.todo_android.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sarthakmeh.todo_android.R;

public class ToDoNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String task = intent.getStringExtra("task");
        int requestCode = intent.getIntExtra("requestCode",-1);
        Log.d("rquestCode",Integer.toString(requestCode));

        Intent snooze = new Intent(context, Snooze.class);
        snooze.putExtra("task",task);
        snooze.putExtra("requestCode",requestCode);

        // use  same requestCode to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, snooze, 0);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification.Builder(context)
                .setContentTitle("Todoist")
                .setContentText(task)
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .setStyle(new Notification.BigTextStyle().bigText(task +
                        "\nClick to snooze or Slide to cancel"))
                .build();

        notificationManager.notify(requestCode, n);
    }
}

