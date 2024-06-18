package com.cookandroid.myalarmapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileControl {


    static public List<alarmSet> loadExistingData(Context c) {
        List<alarmSet> alarms = new ArrayList<>();
        File file = new File(c.getFilesDir(), "alarmInf.txt");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 9) {
                    alarms.add(new alarmSet(parts[0], parts[1] + ":" + parts[2] + ":" + parts[3] + ":" + parts[4] + ":" + parts[5], parts[6].equals("true"),Integer.parseInt(parts[7]),parts[8].equals("true")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return alarms;
    }

    static void saveData(Context c, List<alarmSet> alarmSets) {
        try (FileOutputStream fos = c.openFileOutput("alarmInf.txt", Context.MODE_PRIVATE)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (alarmSet alarm : alarmSets) {
                stringBuilder.append(alarm.alarmFormat());
            }
            fos.write(stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
