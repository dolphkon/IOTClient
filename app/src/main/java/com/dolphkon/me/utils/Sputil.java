package com.dolphkon.me.utils;
/*
 *  项目名： IOTClient
 *  包名：   com.dolphkon.me.IOTClient.utils
 *  文件名： Sputil
 *  创建者：  dolphkon
 *  创建时间：2018/07/02 12:27 PM
 *  描述：    TODO
 */

import android.content.Context;
import android.content.SharedPreferences;


public class Sputil {
    public static final String NAME="SP";

//键 值
    public static  void putString(Context mcontext,String key,String value){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();

    }
//键默认值
    public static  String getString(Context mcontext,String key,String defvalue){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
     return sp.getString(key,defvalue);

    }

    //键 值
    public static  void putInt(Context mcontext,String key,Integer value){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();

    }
    //键默认值
    public static  int getInt(Context mcontext,String key,Integer defvalue){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getInt(key,defvalue);

    }

    //键 值
    public static  void putBoolean(Context mcontext,String key,boolean value){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();

    }
    //键默认值
    public static  boolean getBoolean(Context mcontext,String key,boolean defvalue){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(key,defvalue);

    }

//    删除单个
    public static void deleteShare(Context mcontext,String key){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
          sp.edit().remove( key).commit();
    }

//    删除全部
    public  static void deleteAllShare(Context mcontext){
        SharedPreferences sp=mcontext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
