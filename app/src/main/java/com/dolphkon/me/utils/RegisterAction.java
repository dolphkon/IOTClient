package com.dolphkon.me.utils;


import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by dolphkon on 2018/07/15.
 */

public class RegisterAction {
    /*
    * 注册
    * */
    public RegisterAction(String url, String action, String account, String pwd, String picBase64, final ISuccessCallback successCallback, final IFailCallback failCallback){
        OkHttpUtils
                .post()
                .url(url)
                .addParams(Config.KEY_ACTION,action)
                .addParams(Config.KEY_ACCOUNT,account)
                .addParams(Config.KEY_PWD,pwd)
                .addParams(Config.KEY_USER_ICON,picBase64)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(null!=failCallback){
                            LogUtil.d("注册失败"+e.getMessage());
                            failCallback.onFail(e.getMessage());
                        }
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt(Config.KEY_STATUS);
                            int errCode = jsonObject.getInt(Config.KEY_ERR_CODE);
                            if(status==1){
                                successCallback.onSuccess(response,id);
                            }else if(status==0){
                                failCallback.onFail(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
    public interface ISuccessCallback{
        void onSuccess(String response, int id);
    };
    public interface IFailCallback{
        void onFail(String errCause);
    }
}

