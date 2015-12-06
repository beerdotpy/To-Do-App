package com.example.sarthakmeh.todo_android.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sarthakmeh.todo_android.R;

public class ToDoNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        TODO When user cancel notification update status to complete
         */
        String task = intent.getStringExtra("task");
        Intent snooze = new Intent(context, Snooze.class);
        snooze.putExtra("task",task);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, snooze, 0);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification.Builder(context)
                .setContentTitle("Todoist")
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .setStyle(new Notification.BigTextStyle().bigText(task +
                        "\nClick to show notification again in 30 minutes.Slide to cancel"))
                .build();
        notificationManager.notify(0, n);
    }
}

