package com.cookandroid.myalarmapp;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.cookandroid.myapplication.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class AlarmAdapter extends ArrayAdapter<alarmSet> {

    private Context c;
    private List<alarmSet> alarmData;

    public AlarmAdapter(Context context, List<alarmSet> alarms) {
        super(context, R.layout.alarm_adapter_layout, alarms);
        c = context;
        alarmData = alarms; // 처음 한 번만 데이터를 불러옴
        FileControl.saveData(c, alarmData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        alarmSet alarm = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_adapter_layout, parent, false);
        }

        TextView alarmName = convertView.findViewById(R.id.alarmName);
        TextView alarmTime = convertView.findViewById(R.id.alarmTime);
        TextView alarmMethod = convertView.findViewById(R.id.method);
        Switch alarmSwitch = convertView.findViewById(R.id.alarmSwitch);

        alarmName.setText(alarm.getName());
        alarmTime.setText(alarm.getTime());
        if(alarm.getMethod()==true){
            alarmMethod.setText("수학 문제");
        } else alarmMethod.setText("문장 타자");
        alarmSwitch.setChecked(alarm.getEnabled());


        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarmSwitch.setChecked(!alarm.getEnabled()); // 알람 스위치의 상태
            onOffAlarm(!alarm.getEnabled(), alarm.getIndex()); // 변경된 상태를 파일에 저장
        });

        return convertView;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void onOffAlarm(boolean enabled, int index) {

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE); // 생성자의 컨텍스트를 통해 불러오기

        alarmSet alarm = null;

        for (int i = 0; i < alarmData.size(); i++) {
            if (alarmData.get(i).getIndex() == index) {
                alarm = alarmData.get(i);
                alarm.setEnabled(enabled);
                break; // 해당 알람을 찾으면 더 이상 반복할 필요 없음
            }
        }


        if (alarm.getEnabled()==true) { //바꿔서 켜지면 알람 다시 설정
            String[] parts = alarm.getTime().split(":");
            if (parts.length == 5) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1; // Calendar.MONTH는 0부터 시작하므로 1을 뺌
                int day = Integer.parseInt(parts[2]);
                int hour = Integer.parseInt(parts[3]);
                int minute = Integer.parseInt(parts[4]);
                System.out.println("create 클래스 136번쨰 줄 : 잘 됨"); // 136번째

                // Calendar 객체를 초기화
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute, 0);

                // 알람에 대해 개별 PendingIntent를 생성
                Intent alarmReceiverIntent = new Intent(c, MyAlarmBroadcastReceiver.class);
                alarmReceiverIntent.putExtra("index", alarm.getIndex());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        c, alarm.getIndex(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // 알람 설정
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent
                    );
                }
            }

            FileControl.saveData(c, alarmData); // 변경된 데이터를 덮어쓰기
        } else if(alarm.getEnabled()==false){

            Intent alarmReceiverIntent = new Intent(c, MyAlarmBroadcastReceiver.class);
            alarmReceiverIntent.putExtra("index", alarm.getIndex());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    c, alarm.getIndex(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 알람 취소
            alarmManager.cancel(pendingIntent);
        }

    }
}