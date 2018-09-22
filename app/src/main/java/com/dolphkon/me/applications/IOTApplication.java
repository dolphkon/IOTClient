package com.dolphkon.me.applications;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dolphkon.me.mqtt.MQTTService;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/*
 *  项目名：IOTClient
 *  包名：  com.dolphkon.me.applications
 *  文件名： IOTApplication
 *  创建者：  dolphkon
 *  创建时间：2018/05/30 09:51 PM
 *  描述：    Activity 的基类
 */
/*
*   1.统一的属性
*    2.统一的接口
*    3.统一的方法
* */
public class IOTApplication extends Application {
    public static IOTApplication myIOTApplication=null;
    private static final String TAG = "IOTApplication";     //logt
    /**
     * 一个全局的Context
     */
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        myIOTApplication=this;
//okhttp
        HttpUtils.initOkHttp();
    }
/*//  当系统配置发生变更时被调用，如屏幕方向或系统语言发生变更
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: call with:"+"newConfig=["+newConfig+"]");
    }
//   当系统内存不够时被调用
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory: ");
    }*/
public static Context getContext(){
    return context;
}

    public static  IOTApplication getInstance(){
        return  myIOTApplication;

   }
}
