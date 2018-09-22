package com.dolphkon.me.utils;
/*
 *  项目名： IOTClient
 *  包名：   com.example.dolphkon.IOTClient.utils
 *  文件名： LogUtil
 *  创建者：  dolphkon
 *  创建时间：2018/05/31   11:44 AM
 *  描述：    TODO
 */

import android.util.Log;
public class LogUtil {
//    开关
    public  static  final boolean DEBUG=true;
//    TAG
private static final String TAG = "IOTProj";
//五个等级 DIWE
    public  static void d(String text){
        if(DEBUG){
            Log.d(TAG, text);
        }
    }
    public  static void i(String text){
        if(DEBUG){
            Log.i(TAG, text);
        }
    }
    public  static void w(String text){
        if(DEBUG){
            Log.w(TAG, text);
        }
    }
    public  static void e(String text){
        if(DEBUG){
            Log.e(TAG, text);
        }
    }
}
