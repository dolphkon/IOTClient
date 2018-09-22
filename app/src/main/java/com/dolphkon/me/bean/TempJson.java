package com.dolphkon.me.bean;

/**
 * Created by dolphkon on 2018/8/1.
 */

public class TempJson {
    private String newtime;
    private int i;

    public String getNewtime() {
        return newtime;
    }
    public String getNewtime(int i) {
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

    @Override
    public String toString() {
        return "TempJson{" +
                "newtime='" + newtime + '\'' +
                ", tempmax=" + tempmax +
                ", tempmin=" + tempmin +
                '}';
    }

    public float getTempmin() {
        return tempmin;
    }

    public void setTempmin(float tempmin) {
        this.tempmin = tempmin;
    }

    private float tempmax;
    private float tempmin;
}
