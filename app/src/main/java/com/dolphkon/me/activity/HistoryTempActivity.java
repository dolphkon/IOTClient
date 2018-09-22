package com.dolphkon.me.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.adapter.HistempAdapter;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.bean.TemperEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
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

public class HistoryTempActivity extends AppCompatActivity implements View.OnClickListener{

    private XRecyclerView histemp_recyclerview;
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private HistempAdapter histempAdapter;
    private List<TemperEventMsg> histempList=new ArrayList<>();
    private String device;
    private TextView tv_title;
    private ImageButton imgbtn_back;
    private LinearLayout histemp_error_page;
    private Button btn_reload_histemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_temp);
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
        device=getIntent().getStringExtra("tdevice");
        LogUtil.d("histemp_device"+device);
        histemp_error_page=(LinearLayout)findViewById(R.id.histemp_error_page);
        histemp_recyclerview=(XRecyclerView)findViewById(R.id.histemp_recyclerview);
        if (HttpUtils.isNetworkConnected(HistoryTempActivity.this)) {
            //如果有网络连接
            //重新请求网络数据 从服务器获取数据
            new hisTempAsyncTask().execute();
        } else {
            histemp_recyclerview.setVisibility(View.GONE);
            histemp_error_page.setVisibility(View.VISIBLE);
            //当前没有网络连接
            Toast.makeText(HistoryTempActivity.this, "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        btn_reload_histemp=(Button)findViewById(R.id.btn_reload_histemp);
        btn_reload_histemp.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText(device+"号传感器前100条记录");
        ActionBar actionBar = getSupportActionBar();
        imgbtn_back=(ImageButton)findViewById(R.id.imgbtn_back);
        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //        下拉刷新
        histemp_recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        histemp_recyclerview.setLoadingListener(new XRecyclerView.LoadingListener(){
            public int refreshTime;
            @Override
            public void onRefresh() {
                refreshTime ++;
                new hisTempAsyncTask().execute();
                histempAdapter.notifyDataSetChanged();
                histemp_recyclerview.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                refreshTime ++;
                new hisTempAsyncTask().execute();
                histempAdapter.notifyDataSetChanged();
                histemp_recyclerview.refreshComplete();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbtn_back:
                finish();
                break;
            case R.id.btn_reload_histemp:
                new hisTempAsyncTask().execute();
                break;
        }
    }
    private class hisTempAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(HistoryTempActivity.this, "加载中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            requestDataFromServer();
            // 阻塞线程
           long time = System.currentTimeMillis();//去系统时间的毫秒数
            while((System.currentTimeMillis()-time < 2000)) {
            }
            Log.i("IOTlog","histemp_UploadSucess is : "+ UploadSuccess);
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
                histempAdapter = new HistempAdapter(HistoryTempActivity.this,histempList);
                histemp_recyclerview.setLayoutManager(new LinearLayoutManager(HistoryTempActivity.this));
                histemp_recyclerview.setAdapter(histempAdapter);
                histemp_error_page.setVisibility(View.GONE);
                //设置分割线
                histemp_recyclerview.addItemDecoration(new RecycleViewDivider(HistoryTempActivity.this, LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_white)));
            } else {
                histemp_recyclerview.setVisibility(View.GONE);
                histemp_error_page.setVisibility(View.VISIBLE);
                Toast.makeText(HistoryTempActivity.this, "获取历史记录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void requestDataFromServer() {
        OkHttpUtils
                .get()
                .url(StaticClass.HISTORYTEMP)
                .addParams("tdevice",device)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("histemp_response"+response);
                        Gson gson = new Gson();
                        histempList = gson.fromJson(response, new TypeToken<List<TemperEventMsg>>() {}.getType());
                        if(null!=histempList && histempList.size()!=0){
                            UploadSuccess = true;
                        }
                        LogUtil.d(histempList.toString());
                    }
                });
    }
}
