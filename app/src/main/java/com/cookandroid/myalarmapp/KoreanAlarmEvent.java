package com.cookandroid.myalarmapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class KoreanAlarmEvent extends Activity {

    TextView alarmN, line;
    EditText answer;
    Button input;
    MediaPlayer mediaPlayer;
    Random random;
    String selectedLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.korean_alram_layout);

        alarmN = findViewById(R.id.alarmN);
        line = findViewById(R.id.line);
        answer = findViewById(R.id.answer);
        input = findViewById(R.id.input);

        Uri alarmUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
        mediaPlayer = MediaPlayer.create(this, alarmUri); // 알람 소리 설정

        mediaPlayer.start();

        int index = getIntent().getIntExtra("index", -1);
        if (index >= 0) {
            alarmN.setText(FileControl.loadExistingData(this).get(index).getName());
        }

        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = this.getResources().openRawResource(R.raw.line);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String lines;
            random = new Random();

            int lineIndex = random.nextInt(9);
            int i = 0;
            while ((lines = bufferedReader.readLine()) != null) {
                if (i == lineIndex) {
                    selectedLine = lines;
                    line.setText(selectedLine);
                    break;
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = answer.getText().toString();
                if (userInput.equals(selectedLine)) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    Intent intent = new Intent(getApplicationContext(), AlarmInterface.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(KoreanAlarmEvent.this, "오답입니다", Toast.LENGTH_SHORT).show(); // 오답 처리
                }
            }
        });
    }

}
