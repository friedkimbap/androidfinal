package com.cookandroid.myalarmapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class MyAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static PowerManager.WakeLock wakeLock;

    @SuppressLint("ServiceCast")
    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLock.acquireWakeLock(context);


        int index = intent.getIntExtra("index", -1);

        if (index >= 0) {
            Intent alarmIntent = null;
            if(FileControl.loadExistingData(context).get(index).getMethod()){
                alarmIntent = new Intent(context, MathAlarmEvent.class);
            }else {
                alarmIntent = new Intent(context, KoreanAlarmEvent.class);
            }

            if(FileControl.loadExistingData(context).get(index).getEnabled()){
                alarmIntent.putExtra("index", index);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 새로운 태스크에서 액티비티 시작
                context.startActivity(alarmIntent);
                WakeLock.releaseWakeLock();
            }
        }
        else{
           System.out.println("해당 인덱스에 대한 알람이 없습니다");
        }
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "alarm_channel";
            CharSequence channelName = "Alarm Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription("Notifications for alarms");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
