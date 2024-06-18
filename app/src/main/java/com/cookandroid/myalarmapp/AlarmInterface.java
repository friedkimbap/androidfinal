package com.cookandroid.myalarmapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cookandroid.myapplication.R;

import java.util.List;

public class AlarmInterface extends Activity {

    private ListView listView;
    private Button newAlarm;
    private List<alarmSet> alarmList;
    private AlarmAdapter adapter;
    private AlarmManager alarmManager;
    private PendingIntent[] pendingIntent;
    private Context c=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alram_interface);

        listView = findViewById(R.id.lv);
        newAlarm = findViewById(R.id.newAlarm);

        alarmList = FileControl.loadExistingData(this);
        adapter = new AlarmAdapter(c, alarmList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                alarmSet selectAlarm = alarmList.get(i);
                int index = selectAlarm.getIndex();
                alarmList.remove(i);

                // AlarmManager 가져오기
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                // 알람 취소를 위한 PendingIntent 생성
                Intent alarmBroadcastReceiverIntent = new Intent(c, MathAlarmEvent.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        c, index, alarmBroadcastReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // 알람 취소
                alarmManager.cancel(pendingIntent);

                // 리스트뷰 업데이트
                adapter = new AlarmAdapter(c, alarmList);
                listView.setAdapter(adapter);

                Toast.makeText(AlarmInterface.this, selectAlarm.getName() + " 알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmCreate.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 알람 추가 후에도 리스트를 다시 로드하여 어댑터에 설정
        alarmList = FileControl.loadExistingData(this);
        adapter.clear();
        adapter.addAll(alarmList);
        adapter.notifyDataSetChanged();
    }

}
