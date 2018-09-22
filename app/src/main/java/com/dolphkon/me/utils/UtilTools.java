package com.dolphkon.me.utils;
/*
 *  项目名： IOTClient
 *  包名：   com.dolphkon.me.IOTClient.utils
 *  文件名： UtilTools
 *  创建者：  dolphkon
 *  创建时间：2018/05/31 06:04 PM
 *  描述：    工具类
 */

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class UtilTools {
//    设置字体
     public static  void setFont(Context mcontext, TextView textView){
         Typeface typeFace =Typeface.createFromAsset(mcontext.getAssets(),"FZSTK.TTF");
         textView.setTypeface(typeFace);

     }


}
