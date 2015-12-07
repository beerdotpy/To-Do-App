package com.example.sarthakmeh.todo_android.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class Snooze extends BroadcastReceiver {

    int snooze_time_min = 30;

    @Override
    public void onReceive(Context context, Intent intent) {
        /***
         *Cancel current notifications and snooze notification for 30 minutes when notif was clicked
         */
        NotificationManager nMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nMgr.cancel(intent.getIntExtra("requestCode",-1));
        Intent pushNotif = new Intent(context,ToDoNotification.class);
        pushNotif.putExtra("task",intent.getStringExtra("task"));
        PendingIntent pintent = PendingIntent.getBroadcast(context, intent.getIntExtra("requestCode",-1), pushNotif, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //Snooze time to current time
        calendar.add(Calendar.MINUTE, snooze_time_min);
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pintent);
    }
}
