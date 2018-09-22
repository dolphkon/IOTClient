package com.dolphkon.me.utils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by dolphkon on 2018/07/15.
 */

public class LoginService {
    private static  final  String TAG = "loginService";
    //登录账号密码
    public LoginService(String url, String action, String account, String pwd, String token, final ISuccessCallback successCallback, final IFailCallback failCallback){
        OkHttpUtils.post()
                .url(url)
                .addParams(Config.KEY_ACTION,action)
                .addParams(Config.KEY_ACCOUNT,account)
                .addParams(Config.KEY_PWD,pwd)
                .addParams(Config.KEY_TOKEN,token)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(failCallback!=null){
                    failCallback.onFail(e.getMessage());
                }
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if(status==1){
                        if(null!=successCallback){
                            successCallback.onSuccess(response,  id);
                        }
                    }else if(status==0){
                        if(null!=failCallback){
                            failCallback.onFail(response);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public interface ISuccessCallback{
        void onSuccess(String response, int id);
    }
    public interface IFailCallback{
        void onFail(String s);
    }
}
