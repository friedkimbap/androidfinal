package com.cookandroid.myalarmapp;

public class alarmSet {
    private String name;
    private String time;
    private boolean enabled;
    private int index;
    private boolean method;

    alarmSet(String name, String time, boolean enabled) { // 새로운 알람을 만들 때 사용하는 생성자
        this.name = name;
        this.time = time;
        this.enabled = enabled;
        this.index = -1;
        this.method=true;
    }

    public alarmSet(String name, String time, boolean enabled, int index, boolean method) { // 알람 목록 파일을 다시 alarmset으로 변환하기 위한 생성자
        this.name = name;
        this.time = time;
        this.enabled = enabled;
        this.index = index;
        this.method = method;
    }

    String alarmFormat(){ // 알람 목록 파일에 저장하는 각 알람 형식
        return (name+":"+time+":"+enabled+":"+index+":"+method+"\n");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getEnabled(){
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean getMethod() {
        return method;
    }
    public void setMethod(boolean method) {
        this.method = method;
    }

}
