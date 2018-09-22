package com.dolphkon.me.bean;

/**
 * Created by langstone on 2018/9/8.
 */

public class SensirJson {
    private String newtime;

    @Override
    public String toString() {
        return "SensirJson{" +
                "newtime='" + newtime + '\'' +
                ", tempmax=" + tempmax +
                ", tempmin=" + tempmin +
                ", i=" + i +
                '}';
    }

    public String getNewtime() {
        return newtime;
    }
    public String getNewtime(int i){
        this.i=i;
        return newtime;
    }

    public void setNewtime(String newtime) {
        this.newtime = newtime;
    }

    public float getTempmax() {
        return tempmax;
    }

    public void setTempmax(float tempmax) {
        this.tempmax = tempmax;
    }

    public float getTempmin() {
        return tempmin;
    }

    public void setTempmin(float tempmin) {
        this.tempmin = tempmin;
    }

    private float tempmax;
    private float tempmin;
    private int i;

}
