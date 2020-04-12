package com.dolphkon.me.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.utils.Config;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.utils.LoginService;
import com.dolphkon.me.utils.Sputil;
import com.dolphkon.me.view.MainActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONException;
import org.json.JSONStringer;

/*
 *  项目名：IOTClient
 *  包名：  com.dolphkon.me.activity
 *  文件名： LoginActivity
 *  创建者：  dolphkon
 *  创建时间：2018/05/30 09:51 PM
 *  描述：    用户登陆界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static  final int DISMISS = 0 ;
    private EditText edt_account,edt_pwd;
private Button btn_login_click_to_login;
private Button tx_login_click_to_register;
    private String username;
    private String password;
    private Dialog progressDialog;
    private CheckBox keep_password;
    private ProgressDialog pd;//进度条
    private String wyt_account;
    private String wyt_pwd;
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==DISMISS){
                pd.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wyt_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint 激活导航栏
            tintManager.setNavigationBarTintEnabled(true);
            //设置系统栏设置颜色
            //tintManager.setTintColor(R.color.red);
            //给状态栏设置颜色
            tintManager.setStatusBarTintResource(R.color.white_15);
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            tintManager.setNavigationBarTintResource(R.color.white_15);
        }
        initView();

    }
//初始化
    private void initView() {
        edt_account=(EditText)findViewById(R.id.edt_login_account);
        edt_pwd=(EditText)findViewById(R.id.edt_login_pwd);
        btn_login_click_to_login=(Button)findViewById(R.id.btn_login_click_to_login);
        tx_login_click_to_register=(Button)findViewById(R.id.tx_login_click_to_register);
        keep_password=(CheckBox)findViewById(R.id.keep_password);
        btn_login_click_to_login.setOnClickListener(this);
        tx_login_click_to_register.setOnClickListener(this);
        keep_password.setOnClickListener(this);
        username = Sputil.getString(LoginActivity.this,"username",null);
        password=Sputil.getString(LoginActivity.this,"password",null);
        if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            keep_password.setChecked(false);
        }else {
            edt_account.setText(username);
            edt_pwd.setText(password);
            keep_password.setChecked(true);
        }
    }
    @Override
    public void onClick(View view) {
         switch (view.getId()){
            case R.id.btn_login_click_to_login:
                //点击的是登录按钮
                  wyt_account = edt_account.getText().toString();//获取账号
                  wyt_pwd = edt_pwd.getText().toString();//获取密码
                if(TextUtils.isEmpty(wyt_account)){
                    Toast.makeText(LoginActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(wyt_pwd)){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(wyt_account.length()>20){
                    Toast.makeText(LoginActivity.this,"您输入的账号过长",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(wyt_pwd.length()>20){
                    Toast.makeText(LoginActivity.this,"您输入的密码过长",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    String token =  Config.getTokenFromPreferences(this);
                    LogUtil.d("token的值"+token);
                    //显示进度对话框
                    pd = ProgressDialog.show(this,"温馨提示","正在登录",false,true);
                    new LoginService(Config.URL, Config.KEY_LOGIN, wyt_account, wyt_pwd, "", new LoginService.ISuccessCallback() {
                        @Override
                        public void onSuccess(String response, int id) {
                            if (keep_password.isChecked()){
                                Sputil.putString(LoginActivity.this,"username",wyt_account);
                                Sputil.putString(LoginActivity.this,"password",wyt_pwd);
                            }else {
                                Sputil.deleteAllShare(LoginActivity.this);
                            }
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            pd.setMessage("恭喜你登录成功");
                            LogUtil.d("当前进程id"+android.os.Process.myTid());
                            handler.sendEmptyMessage(DISMISS);
                            finish();
                        }
                    }, new LoginService.IFailCallback() {

                        @Override
                        public void onFail(String s) {
                            LogUtil.d("the s is"+s);
                            try {
                                String s2 = new JSONStringer().object()
                                        .key("status")
                                        .value(0)
                                        .key("errCode")
                                        .value("0x00000100")
                                        .endObject()
                                        .toString();
                                String  s3 = new JSONStringer().object()
                                        .key("status")
                                        .value(0)
                                        .key("errCode")
                                        .value("0x00000011")
                                        .endObject()
                                        .toString();
                                if (s==s2){
                                    pd.setMessage("密码不匹配!"+s);
                                    handler.sendEmptyMessageDelayed(DISMISS,1000);
                                }else {
                                    pd.setMessage("你的信息不匹配!"+s);
                                    handler.sendEmptyMessageDelayed(DISMISS,1000);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
//                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }


                break;
            case  R.id.tx_login_click_to_register:
         startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
        }

    }

}
