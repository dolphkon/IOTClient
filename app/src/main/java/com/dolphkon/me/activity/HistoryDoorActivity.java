package com.dolphkon.me.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dolphkon.me.R;
import com.dolphkon.me.adapter.HisdoorAdapter;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.bean.DoorEventMsg;
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

public class HistoryDoorActivity extends AppCompatActivity implements View.OnClickListener{

    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private XRecyclerView hisdoor_recyclerview;
    private List<DoorEventMsg> hisdoorList=new ArrayList<>();
    private HisdoorAdapter hisdoorAdapter;
    private String device;
    private TextView tv_title;
    private ImageButton imgbtn_back;
    private LinearLayout hisdoor_error_page;
    private Button btn_reload_hisdoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_door);
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
        device=getIntent().getStringExtra("device");
        LogUtil.d("doorswitch_device:"+device);
        hisdoor_recyclerview=(XRecyclerView)findViewById(R.id.hisdoor_recyclerview);
        hisdoor_error_page=(LinearLayout)findViewById(R.id.hisdoor_error_page);
        if (HttpUtils.isNetworkConnected(HistoryDoorActivity.this)) {
            //如果有网络连接
            //重新请求网络数据 从服务器获取数据
            new hisdoorAsyncTask().execute();
        } else {

            //当前没有网络连接
            hisdoor_error_page.setVisibility(View.VISIBLE);
            hisdoor_recyclerview.setVisibility(View.GONE);
            Toast.makeText(HistoryDoorActivity.this, "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        tv_title=(TextView)findViewById(R.id.tv_title);
        hisdoor_recyclerview.setLayoutManager(new LinearLayoutManager(HistoryDoorActivity.this));
        btn_reload_hisdoor=(Button)findViewById(R.id.btn_reload_hisdoor);
        btn_reload_hisdoor.setOnClickListener(this);
        tv_title.setText(device+"号传感器前100条记录");
        imgbtn_back=(ImageButton)findViewById(R.id.imgbtn_back);
        imgbtn_back.setOnClickListener(this);
        //        下拉刷新
        hisdoor_recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        hisdoor_recyclerview.setLoadingListener(new XRecyclerView.LoadingListener(){
            public int refreshTime;
            @Override
            public void onRefresh() {
                refreshTime ++;
                new hisdoorAsyncTask().execute();
                hisdoorAdapter.notifyDataSetChanged();
                hisdoor_recyclerview.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                refreshTime ++;
                new hisdoorAsyncTask().execute();
                hisdoorAdapter.notifyDataSetChanged();
                hisdoor_recyclerview.refreshComplete();
            }
        });
    }
    @Override
    public void onClick(View v) {
   switch (v.getId()){
       case R.id.imgbtn_back:
           finish();
           break;
       case R.id.btn_reload_hisdoor:
           new hisdoorAsyncTask().execute();
           break;
   }

    }
    private class hisdoorAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(HistoryDoorActivity.this, "加载中...");
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
            if (result) {
                hisdoorAdapter = new HisdoorAdapter(HistoryDoorActivity.this,hisdoorList);
                hisdoor_recyclerview.setAdapter(hisdoorAdapter);
                //设置分割线
                hisdoor_recyclerview.addItemDecoration(new RecycleViewDivider(HistoryDoorActivity.this, LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_white)));
            } else {
                hisdoor_error_page.setVisibility(View.VISIBLE);
                hisdoor_recyclerview.setVisibility(View.GONE);
            }
        }
    }

    private void requestDataFromServer() {
        OkHttpUtils
                .get()
                .url(StaticClass.HISTORYDOOR)
                .addParams("device",device)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.d("doorswitch 从服务器获取失败");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("hisdoor_response"+response);
                        Gson gson = new Gson();
                        hisdoorList = gson.fromJson(response, new TypeToken<List<DoorEventMsg>>() {}.getType());
                        if(null!=hisdoorList && hisdoorList.size()!=0){
                            UploadSuccess = true;
                        }
                        LogUtil.d(hisdoorList.toString());
                    }
                });
    }
}
