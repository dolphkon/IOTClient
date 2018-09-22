package com.dolphkon.me.bean;

/**
 * Created by dolphkon on 2018/7/30.
 */

public class SensirEventMsg {
    private int sid;
    private String sdevice;
    private float sbattery;
    private float strength;

    @Override
    public String toString() {
        return "SensirEventMsg{" +
                "sid=" + sid +
                ", sdevice='" + sdevice + '\'' +
                ", sbattery=" + sbattery +
                ", strength=" + strength +
                ", saddress='" + saddress + '\'' +
                ", stime='" + stime + '\'' +
                ", stemperature=" + stemperature +
                ", shumidity=" + shumidity +
                '}';
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSdevice() {
        return sdevice;
    }

    public void setSdevice(String sdevice) {
        this.sdevice = sdevice;
    }

    public float getSbattery() {
        return sbattery;
    }

    public void setSbattery(float sbattery) {
        this.sbattery = sbattery;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public float getStemperature() {
        return stemperature;
    }

    public void setStemperature(float stemperature) {
        this.stemperature = stemperature;
    }

    public float getShumidity() {
        return shumidity;
    }

    public void setShumidity(float shumidity) {
        this.shumidity = shumidity;
    }

    public static void setInstance(SensirEventMsg instance) {
        SensirEventMsg.instance = instance;
    }

    private String saddress;
    private String stime;
    private float stemperature;
    private float shumidity;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static SensirEventMsg instance = null;

    /* 私有构造方法，防止被实例化 */
    private SensirEventMsg() {
    }

    /*2.懒汉式变种，解决线程安全问题**/
    public static synchronized SensirEventMsg getInstance() {
        if (instance == null) {
            instance = new SensirEventMsg();
        }
        return instance;
    }
}
