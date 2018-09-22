package com.dolphkon.me.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by dolphkon on 2018/7/20.
 * 温度传感器的消息实体类
 * 地址： 1 代表常凌云实验室地址，2 代表2B栋302寝室地址，3 代表16栋机房地址
 * 传感器类型：1 代表常开开关传感器，2 代表温度传感器，3 代表温湿度传感器
 */
public class TemperEventMsg{
    private int tid;
    private String tdevice;
    private float tbattery;
    private float tstrength;

    @Override
    public String toString() {
        return "TemperEventMsg{" +
                "tid=" + tid +
                ", tdevice='" + tdevice + '\'' +
                ", tbattery=" + tbattery +
                ", tstrength=" + tstrength +
                ", taddress='" + taddress + '\'' +
                ", time='" + time + '\'' +
                ", temperature=" + temperature +
                '}';
    }

    private String taddress;
    private String time;
    private float temperature;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTdevice() {
        return tdevice;
    }

    public void setTdevice(String tdevice) {
        this.tdevice = tdevice;
    }

    public float getTbattery() {
        return tbattery;
    }

    public void setTbattery(float tbattery) {
        this.tbattery = tbattery;
    }

    public float getTstrength() {
        return tstrength;
    }

    public void setTstrength(float tstrength) {
        this.tstrength = tstrength;
    }

    public String getTaddress() {
        return taddress;
    }

    public void setTaddress(String taddress) {
        this.taddress = taddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public static void setInstance(TemperEventMsg instance) {
        TemperEventMsg.instance = instance;
    }

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static TemperEventMsg instance = null;

    /* 私有构造方法，防止被实例化 */
    private TemperEventMsg() {
    }

    /*2.懒汉式变种，解决线程安全问题**/
    public static synchronized TemperEventMsg getInstance() {
        if (instance == null) {
            instance = new TemperEventMsg();
        }
        return instance;
    }
}
