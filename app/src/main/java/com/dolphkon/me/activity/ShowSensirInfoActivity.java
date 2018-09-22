package com.dolphkon.me.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.SensirJson;
import com.dolphkon.me.bean.TempJson;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jn.chart.charts.LineChart;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ShowSensirInfoActivity extends AppCompatActivity {

    private String device;
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private List<SensirJson>sensirJsonList=new ArrayList<>();
    private LineChart linchart_sensir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sensir_info);
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
        device=getIntent().getStringExtra("sdevice");
        if (HttpUtils.isNetworkConnected(ShowSensirInfoActivity.this)) {
            //如果有网络连接
            LogUtil.d( "onCreate: 初始化中.......");
            //重新请求网络数据 从服务器获取数据
            new UploadSensirMaxAsyncTask().execute();


        } else {
            //当前没有网络连接
            Toast.makeText( ShowSensirInfoActivity.this, "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        }
    private void initView() {
        linchart_sensir=(LineChart)findViewById(R.id.linchart_sensir);

    }

    private class UploadSensirMaxAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressBarDialog.createLoadingDialog(ShowSensirInfoActivity.this, "加载中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
          requsetDateFromServer();
          long time= System.currentTimeMillis();
          while (System.currentTimeMillis()-time<1000){
          }
            if (UploadSuccess) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (result){
                initView();
                Log.i("IOTlog","sensirJsonList is : "+ sensirJsonList.toString());

            }
        }
    }


    private void requsetDateFromServer() {
        OkHttpUtils
                .get()
                .url(StaticClass.TEMPMAX)
                .addParams("sdevice",device)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("response_sensirmax"+response);
                        Gson gson = new Gson();
                        sensirJsonList = gson.fromJson(response, new TypeToken<List<SensirJson>>() {}.getType());
                        if(null!=sensirJsonList && sensirJsonList.size()!=0){
                            UploadSuccess = true;
                        }
                        LogUtil.d(sensirJsonList.toString());
                        
                    }
                });

    }


}

