package com.dolphkon.me.fragment;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.activity.ShowTempInfoActivity;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.adapter.TemperSensorAdapter;
import com.dolphkon.me.bean.TemperEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.utils.NetUtil;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 *  项目名：IOTClient
 *  包名： com.dolphkon.me.fragment
 *  文件名： TemperatureFragment
 *  创建者： dolphkon
 *  创建时间：2018/06/04 10:06 AM
 *  描述：温度传感器
 */
public class TemperatureFragment extends Fragment implements View.OnClickListener{
    private static final int TEMP_FAIL =0;
    private static final int TEMP_SUCCESS =1;
    List<TemperEventMsg> newtempList = new ArrayList<>();
    private TemperSensorAdapter temperSensorAdapter;
    private XRecyclerView recy_temper;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterView.OnItemClickListener onItemClickListener;
    private Dialog progressDialog;
    private boolean UploadSuccess = false;
    private int refreshTime = 0;
    private List<TemperEventMsg> updateTemperList=new ArrayList<TemperEventMsg>();
    private LinearLayout temp_error_page;
    private Button btn_reload_temp;
    OkHttpClient ok;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        //判断是否有网络连接
        if (HttpUtils.isNetworkConnected(getContext())) {
            //如果有网络连接
            //重新请求网络数据 从服务器获取数据
            new UploadTempAsyncTask().execute();
        } else {
            //当前没有网络连接
            Toast.makeText(getContext(), "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        temp_error_page=(LinearLayout)view.findViewById(R.id.temp_error_page);
        recy_temper = (XRecyclerView) view.findViewById(R.id.recy_temper);
        btn_reload_temp=(Button)view.findViewById(R.id.btn_reload_temp);
        btn_reload_temp.setOnClickListener(this);
        //设置分割线
        recy_temper.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_yellow)));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recy_temper.setHasFixedSize(true);
        recy_temper.setLayoutManager(new LinearLayoutManager(getActivity()));
//        下拉刷新
        recy_temper.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        recy_temper.setLaodingMoreProgressStyle(ProgressStyle.Pacman);
        recy_temper.setLoadingListener(new XRecyclerView.LoadingListener(){
            @Override
            public void onRefresh() {
                refreshTime ++;
                new UploadTempAsyncTask().execute();
                temperSensorAdapter.notifyDataSetChanged();
                recy_temper.refreshComplete();
            }
            @Override
            public void onLoadMore() {
                refreshTime ++;
                new UploadTempAsyncTask().execute();
                requestDataFromServer();
                temperSensorAdapter = new TemperSensorAdapter(getActivity(), newtempList);
                recy_temper.setAdapter(temperSensorAdapter);
                temperSensorAdapter.notifyDataSetChanged();
                recy_temper.refreshComplete();
            }
        });
        return view;
    }
    private void requestDataFromServer() {
        Log.i("IOTlog","requestDataFromServer begin");
       HttpUtils.sendOkHttpRequest(StaticClass.TEMPERATURE_LODE, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的具体内容
                Log.i("IOTlog","requestDataFromServer onResponse");
                String responseData = response.body().string();
                Gson gson = new Gson();
                newtempList = gson.fromJson(responseData, new TypeToken<List<TemperEventMsg>>() {}.getType());
                if(null!=newtempList && newtempList.size()!=0){
                    UploadSuccess = true;
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("IOTlog","requestDataFromServer onFailure");
//                btn_reload_temp.performClick();

            }
        });
    }

    @Override
    public void onClick(View v) {
        new UploadTempAsyncTask().execute();
    }

    private class UploadTempAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(getActivity(), "加载中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            requestDataFromServer();
           // 阻塞线程
            long time = System.currentTimeMillis();//去系统时间的毫秒数
            while((System.currentTimeMillis()-time <4500)) {
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
                temperSensorAdapter = new TemperSensorAdapter(getActivity(), newtempList);
                recy_temper.setAdapter(temperSensorAdapter);
                recy_temper.setVisibility(View.VISIBLE);
                temp_error_page.setVisibility(View.GONE);
                LogUtil.d("newtempList"+newtempList.toString());
                temperSensorAdapter.setOnItemClickListener(new TemperSensorAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ShowTempInfoActivity.class);
                        intent.putExtra("tdevice",newtempList.get(position).getTdevice()+"");
                        startActivity(intent);
                    }
                });
            } else {
                recy_temper.setVisibility(View.GONE);
                temp_error_page.setVisibility(View.VISIBLE);
            }
        }
    }
    private OkHttpClient getOk() {
        if (ok == null) {
            synchronized (OkHttpClient.class) {
                if (ok == null) {
                    initOk();
                }
            }
        }
        return ok;
    }

    void initOk(){

        File httpCacheDirectory = new File("/sdcard", "cache_xx");

        //Cache cache  = new Cache(this.getCacheDir(), 10240 * 1024);
        Cache cache  = new Cache(httpCacheDirectory, 10240 * 1024 * 10); //10M
        OkHttpClient okHttpClient = new OkHttpClient();

        ok= okHttpClient.newBuilder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Log.e("MainActivity", "新请求 =request==" + request.toString());

                        Response response = chain.proceed(request);
                        Response response_build ;
                        if (NetUtil.checkNet(getActivity())) {

                            int maxAge = 60 * 60*24; // 有网络的时候从缓存1天后失效
                            response_build = response.newBuilder()
                                    .removeHeader("Pragma")
                                    .removeHeader("Cache-Control")
                                    .header("Cache-Control", "public, max-age=" + maxAge)
                                    .build();
                        } else {
                            int maxStale = 60 * 60 * 24 * 28; // // 无网络缓存保存四周
                            response_build = response.newBuilder()
                                    .removeHeader("Pragma")
                                    .removeHeader("Cache-Control")
                                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                    .build();
                        }

                        return response_build;
                    }
                })
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }
}
