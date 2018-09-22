package com.dolphkon.me.bean;

/**
 * Created by dolphkon on 2018/7/30.
 */

public class DoorEventMsg {
    private int did;
    private String device;
    private float dbattery;

    @Override
    public String toString() {
        return "DoorEventMsg{" +
                "did=" + did +
                ", device='" + device + '\'' +
                ", dbattery=" + dbattery +
                ", dstrength=" + dstrength +
                ", daddress='" + daddress + '\'' +
                ", dtime='" + dtime + '\'' +
                ", disopen=" + disopen +
                '}';
    }

    private float dstrength;
    private String daddress;
    private String dtime;
    private int disopen;

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public float getDbattery() {
        return dbattery;
    }

    public void setDbattery(float dbattery) {
        this.dbattery = dbattery;
    }

    public float getDstrength() {
        return dstrength;
    }

    public void setDstrength(float dstrength) {
        this.dstrength = dstrength;
    }

    public String getDaddress() {
        return daddress;
    }

    public void setDaddress(String daddress) {
        this.daddress = daddress;
    }

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }

    public int getDisopen() {
        return disopen;
    }

    public void setDisopen(int disopen) {
        this.disopen = disopen;
    }

    public static void setInstance(DoorEventMsg instance) {
        DoorEventMsg.instance = instance;
    }

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static DoorEventMsg instance = null;

    /* 私有构造方法，防止被实例化 */
    private DoorEventMsg() {
    }

    /*2.懒汉式变种，解决线程安全问题**/
    public static synchronized DoorEventMsg getInstance() {
        if (instance == null) {
            instance = new DoorEventMsg();
        }
        return instance;
    }
}
