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

import java.io.IOException;
import java.util.Random;

public class MathAlarmEvent extends Activity {

    TextView alarmN, num1, num2, op;
    EditText calc;
    Button input;
    int set1, set2, result;
    Random random = new Random();
    final static private int PLUS = 0;
    final static private int MINUS = 1;
    final static private int MULTIPLY = 2;
    final static private int DIVIDE = 3;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_alarm_layout);


        alarmN = findViewById(R.id.alarmN);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        op = findViewById(R.id.opt);
        calc = findViewById(R.id.calc);
        input = findViewById(R.id.input);

        Uri alarmUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
        mediaPlayer = MediaPlayer.create(this, alarmUri); // 알람 소리 설정

        mediaPlayer.start();


        int index = getIntent().getIntExtra("index", -1);

        if(index>=0){
            alarmN.setText(FileControl.loadExistingData(this).get(index).getName());
        }

        int opt = random.nextInt(4);

        if (opt == PLUS) {
            set1 = random.nextInt(99) + 10;
            set2 = random.nextInt(99) + 10;
            num1.setText(String.valueOf(set1));
            num2.setText(String.valueOf(set2));
            op.setText("+");
            result = set1 + set2;
        } else if (opt == MINUS) {
            set1 = random.nextInt(99) + 51;
            set2 = random.nextInt(39) + 10;
            num1.setText(String.valueOf(set1));
            num2.setText(String.valueOf(set2));
            op.setText("-");
            result = set1 - set2;
        } else if (opt == MULTIPLY) {
            set1 = random.nextInt(39) + 1;
            set2 = random.nextInt(6) + 4;
            num1.setText(String.valueOf(set1));
            num2.setText(String.valueOf(set2));
            op.setText("x");
            result = set1 * set2;
        } else if (opt == DIVIDE) {
            set2 = random.nextInt(10) + 4;
            set1 = (random.nextInt(10) + 1) * set2;
            num1.setText(String.valueOf(set1));
            num2.setText(String.valueOf(set2));
            op.setText("%");
            result = set1 / set2;
        }

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int userInput = Integer.parseInt(calc.getText().toString());
                    if (userInput == result) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        Intent intent = new Intent(getApplicationContext(), AlarmInterface.class);
                        startActivity(intent);
                    } else {
                        // 오답 처리
                        Toast.makeText(MathAlarmEvent.this, "오답입니다", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // 사용자가 정수가 아닌 값을 입력한 경우 처리
                    Toast.makeText(MathAlarmEvent.this, "정수를 입력하세요", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

}
