package com.dolphkon.me.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dolphkon on 2018/07/15.
 * 网络工具类
 */
public class HttpUtils {
    /**
     * 获取Okhttp客户端
     * 用于管理所有的请求，内部支持并发，所以我们不必每次请求都创建一个 OkHttpClient
     * 对象，这是非常耗费资源的
     */
    public static OkHttpClient okHttpClient = null;

    /**
     * 初始化OkHttpClient
     */
    public static void initOkHttp() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
    }
    /**
     * 网络连接是否正常
     *
     * @return true:有网络    false:无网络
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void setCacheControl(Context mcontext, final String url, final okhttp3.Callback callback){
        //缓存文件夹
        File cacheFile = new File(mcontext.getCacheDir(), "responses");
        //缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        //创建缓存对象
        final Cache cache = new Cache(cacheFile, cacheSize);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .cache(cache)
                        .build();
                //设置缓存时间为60秒
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(60, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .cacheControl(cacheControl)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

//                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();




    }



}
