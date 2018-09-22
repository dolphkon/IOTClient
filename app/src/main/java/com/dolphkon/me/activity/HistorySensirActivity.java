package com.dolphkon.me.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.adapter.HisSensirAdapter;
import com.dolphkon.me.adapter.HistempAdapter;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.bean.SensirEventMsg;
import com.dolphkon.me.bean.TemperEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.view.MainActivity;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HistorySensirActivity extends AppCompatActivity implements View.OnClickListener{

    private XRecyclerView hisensir_recyclerview;
    private String device;
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private List<SensirEventMsg> historySensirList=new ArrayList<>();
    private HisSensirAdapter historySensirAdapter;
    private TextView tv_title;
    private ImageButton imgbtn_back;
    private LinearLayout hissensir_error_page;
    private Button btn_reload_hissensir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sensir);
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
        LogUtil.d("hisensir_device"+device);
        hissensir_error_page=(LinearLayout)findViewById(R.id.hissensir_error_page);
        hisensir_recyclerview=(XRecyclerView)findViewById(R.id.hisensir_recyclerview);
        if (HttpUtils.isNetworkConnected(HistorySensirActivity.this)) {
            //如果有网络连接
            //重新请求网络数据 从服务器获取数据
            new  historySensirAsyncTask().execute();
        } else {
            hissensir_error_page.setVisibility(View.VISIBLE);
            hisensir_recyclerview.setVisibility(View.GONE);

            //当前没有网络连接
            Toast.makeText(HistorySensirActivity.this, "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
//        lin_his_sen=(LinearLayout)findViewById(R.id.lin_his_sen);

        btn_reload_hissensir=(Button)findViewById(R.id.btn_reload_hissensir);
        btn_reload_hissensir.setOnClickListener(this);
        hisensir_recyclerview.setLayoutManager(new LinearLayoutManager(HistorySensirActivity.this));
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText(device+"号传感器前100条记录");
        ActionBar actionBar = getSupportActionBar();
        imgbtn_back=(ImageButton)findViewById(R.id.imgbtn_back);
        imgbtn_back.setOnClickListener(this);

        //        下拉刷新
        hisensir_recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        hisensir_recyclerview.setLoadingListener(new XRecyclerView.LoadingListener(){
            public int refreshTime;
            @Override
            public void onRefresh() {
                refreshTime ++;
                new  historySensirAsyncTask().execute();
                historySensirAdapter.notifyDataSetChanged();
                hisensir_recyclerview.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                refreshTime ++;
                new  historySensirAsyncTask().execute();
                historySensirAdapter.notifyDataSetChanged();
                hisensir_recyclerview.refreshComplete();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbtn_back:
                finish();
                break;
            case R.id.btn_reload_hissensir:
                new  historySensirAsyncTask().execute();
                break;
        }

    }
    private class historySensirAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(HistorySensirActivity.this, "加载中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            requestDataFromServer();
            // 阻塞线程
            long time = System.currentTimeMillis();//去系统时间的毫秒数
            while((System.currentTimeMillis()-time <2000)) {
            }
            Log.i("IOTlog","historysensir_UploadSucess is : "+ UploadSuccess);
            if (UploadSuccess) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            Log.i("IOTlog","histemp_result is : "+ result);

            if (result) {
                historySensirAdapter = new HisSensirAdapter(HistorySensirActivity.this,historySensirList);
                hisensir_recyclerview.setAdapter(historySensirAdapter);
                //设置分割线
                hisensir_recyclerview.addItemDecoration(new RecycleViewDivider(HistorySensirActivity.this, LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_white)));


            } else {
                hissensir_error_page.setVisibility(View.VISIBLE);
                hisensir_recyclerview.setVisibility(View.GONE);

            }
        }
    }
    private void requestDataFromServer() {
        OkHttpUtils
                .get()
                .url(StaticClass.HISTORYSENSIR)
                .addParams("sdevice",device)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("hisensirion_response"+response);
                        Gson gson = new Gson();
                        historySensirList = gson.fromJson(response, new TypeToken<List<SensirEventMsg>>() {}.getType());
                        if(null!=historySensirList && historySensirList.size()!=0){
                            UploadSuccess = true;
                        }
                        LogUtil.d(historySensirList.toString());
                    }
                });
    }
}
