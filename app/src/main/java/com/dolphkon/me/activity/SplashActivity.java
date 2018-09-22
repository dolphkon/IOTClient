package com.dolphkon.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.Sputil;
import com.dolphkon.me.utils.UtilTools;

public class SplashActivity extends AppCompatActivity {
    /*   1.延时2000ms
    *    2.判断是否第一次启动
    *    3.自定义字体
    *    4.Activity 全屏主题
    *
    * */
    private TextView tv_splash;
     private Handler handler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what){
                 case StaticClass.HANDLER_SPLASH:
//                     判断程序是否第一次运行
                     if (isFirst()){
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));

                     }else {
                         startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                     }
                     finish();
                     break;
             }
         }
     };
    private ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
//        ivSplash.setImageResource(R.drawable.bg_page_background);
    }

    private void initView() {
        tv_splash=(TextView) findViewById(R.id.tv_splash);
        ivSplash=(ImageView)findViewById(R.id.iv_splash);
//        cdv_time=(CountDownView)
//        延时2000ms
        handler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH,2000);
//        设置字体
        UtilTools.setFont(this,tv_splash);
    }
//    判断是否第一次运行
    private boolean isFirst() {
        boolean isFirst= Sputil.getBoolean(this,StaticClass.SHARE_IS_FIRST,true);
        if(isFirst ){
          Sputil.putBoolean(this,StaticClass.SHARE_IS_FIRST,false);
            return  true;
        }else {
            return  false;
        }
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
