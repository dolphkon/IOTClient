package com.dolphkon.me.constant;
/*
 *  项目名：IOTClient
 *  包名：  com.dolphkon.me.constant
 *  文件名： StaticClass
 *  创建者：  dolphkon
 *  创建时间：2018/06/02 12:34 PM
 *  描述：    TODO
 */
public class StaticClass {
//    闪屏页的延时
    public  static final int HANDLER_SPLASH=0;
//  是否第一次启动
    public static  final  String SHARE_IS_FIRST="isFirst";
//    常开开关
    public static  final String TOPIC_DOOR="doorswitch";
//    温湿度传感器
public static  final String TOPIC_SENSIRION="sensirion";
//温度传感器
    public static  final String TOPIC_TEMPER="temperature";
    public static final String LOGIN="http://39.108.126.221:8080/IOTProj/searchUser";
//    同一温度传感器最新数据
    public static  final String TEMPERATURE_LODE="http://39.108.126.221:8080/IOTProj/querytemperature";
//    同一温湿度传感器最新数据
public static  final String  SENSIRION_LODE="http://39.108.126.221:8080/IOTProj/querysensirion";
    //    同一温湿度传感器最新数据
    public static  final String  DOORSWITCH_LODE="http://39.108.126.221:8080/IOTProj/querydoor";
    //    同一温GPS传感器最新数据
    public static  final String  GPS_LODE="http://39.108.126.221:8080/IOTProj/querygps";
//    7日内同一设备温度传感器的历史记录
    public static final String HISTORYTEMP="http://39.108.126.221:8080/IOTProj/querytemperatures";
//    7日内同一设备开关传感器的历史记录
public static final String HISTORYDOOR="http://39.108.126.221:8080/IOTProj/querydoors";
    //    7日内同一设备温湿度传感器的历史记录
    public static final String HISTORYSENSIR="http://39.108.126.221:8080/IOTProj/querysensirions";
  //7天内最高温度和最低温度
    public static  final String TEMPMAX="http://39.108.126.221:8080/IOTProj/queryseventemperature";
    //7天内每天的最高温度,最低温度,最大湿度和最小湿度
    public static  final String TEMPSENMAX="http://39.108.126.221:8080/IOTProj/SearchSensirMax";
    public static final int REQUEST_NORMAL_DATA = 1002;
    //sp的名称
    public static String SP_NAME = "yc";
    public static final long CLICK_TIME = 5;
    public static final String APP_OPEN_COUNT = "app_open_count";
}