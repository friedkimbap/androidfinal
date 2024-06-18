package com.cookandroid.myalarmapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cookandroid.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmCreate extends Activity {
    private EditText name;
    private TimePicker time;
    private DatePicker date;
    private RadioGroup method;
    private Button btn;
    private List<alarmSet> alarmData;
    private AlarmManager alarmManager;
    private ComponentName receiver;
    private PackageManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_alram);

        // PackageManager 초기화
        pm = getPackageManager();

        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        method = findViewById(R.id.method);
        btn = findViewById(R.id.setBtn);

        time.setIs24HourView(true); // 24시 형식
        alarmData = FileControl.loadExistingData(this);

        btn.setOnClickListener(view -> {
            long currentTimeMillis = System.currentTimeMillis(); // 현재 시간 구하기
            Date currentDate = new Date(currentTimeMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
            String now = dateFormat.format(currentDate);

            String[] nowParts = now.split(":");

            int currentYear = Integer.parseInt(nowParts[0]);
            int currentMonth = Integer.parseInt(nowParts[1]);
            int currentDay = Integer.parseInt(nowParts[2]);
            int currentHour = Integer.parseInt(nowParts[3]);
            int currentMinute = Integer.parseInt(nowParts[4]);

            if (checkDateTimeValidity(currentYear, currentMonth, currentDay, currentHour, currentMinute)) {
                String setName = name.getText().toString();

                int year = date.getYear();
                int month = date.getMonth() + 1;
                int day = date.getDayOfMonth();

                int hour = Build.VERSION.SDK_INT >= 23 ? time.getHour() : time.getCurrentHour();
                int minute = Build.VERSION.SDK_INT >= 23 ? time.getMinute() : time.getCurrentMinute();

                String setDate = String.format("%04d:%02d:%02d:%02d:%02d", year, month, day, hour, minute);

                alarmSet newAlarm = new alarmSet(setName, setDate, true);
                if (method.getCheckedRadioButtonId() == R.id.math) {
                    newAlarm.setMethod(true);
                } else if (method.getCheckedRadioButtonId() == R.id.korean) {
                    newAlarm.setMethod(false);
                }
                createAlarm(newAlarm);

                Intent intent = new Intent(getApplicationContext(), AlarmInterface.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "시간을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkDateTimeValidity(int currentYear, int currentMonth, int currentDay, int currentHour, int currentMinute) {
        int selectedYear = date.getYear();
        int selectedMonth = date.getMonth() + 1;
        int selectedDay = date.getDayOfMonth();
        int selectedHour = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? time.getHour() : time.getCurrentHour();
        int selectedMinute = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? time.getMinute() : time.getCurrentMinute();

        return !(selectedYear < currentYear ||
                (selectedYear == currentYear && selectedMonth < currentMonth) ||
                (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay < currentDay) ||
                (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay == currentDay &&
                        (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute <= currentMinute))));
    }

    @SuppressLint("ScheduleExactAlarm")
    private void createAlarm(alarmSet newAlarm) {
        alarmData = FileControl.loadExistingData(this);
        alarmData.add(newAlarm);
        FileControl.saveData(this, alarmData); // 파일에 새로운 데이터 저장

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Toast.makeText(this, "AlarmManager를 가져오는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String setCalendar = newAlarm.getTime();
        String[] parts = setCalendar.split(":");
        if (parts.length == 5 && newAlarm.getEnabled()) {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1; // Calendar.MONTH는 0부터 시작하므로 1을 뺌
            int day = Integer.parseInt(parts[2]);
            int hour = Integer.parseInt(parts[3]);
            int minute = Integer.parseInt(parts[4]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);

            int index;
            for (index = 0; index < alarmData.size(); index++) {
                if (alarmData.get(index).getName().equals(newAlarm.getName())) {
                    newAlarm.setIndex(index);
                    FileControl.saveData(this, alarmData); // 파일에 새로운 데이터 저장
                    break;
                }
            }

            Intent alarmReceiverIntent = new Intent(this, MyAlarmBroadcastReceiver.class);
            alarmReceiverIntent.putExtra("index", newAlarm.getIndex());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, newAlarm.getIndex(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            receiver = new ComponentName(this, MyAlarmBroadcastReceiver.class);
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
