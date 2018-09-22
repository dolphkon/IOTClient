package com.dolphkon.me.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.adapter.GPSAdapter;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.bean.GPSEventMsp;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dolphkon on 2018/8/14.
 */

public class GPSFragment extends Fragment implements View.OnClickListener{
    private Dialog progressDialog;
    private List<GPSEventMsp> newgpsList=new ArrayList<>();
    private GPSAdapter gpsAdapter;
    private XRecyclerView recy_gps;
    private LinearLayout gps_error_page;
    private boolean UploadSuccess=false;
    private Button btn_reload_gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gps,container,false);
        //判断是否有网络连接
        if (HttpUtils.isNetworkConnected(getContext())) {
            //如果有网络连接
            LogUtil.d( "onCreate: 初始化中.......");
            //重新请求网络数据 从服务器获取数据
            initView(view);
            new UploadGPSAsyncTask().execute();
        } else {
            //当前没有网络连接
            Toast.makeText(getContext(), "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        recy_gps.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_yellow)));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recy_gps.setHasFixedSize(true);
        recy_gps.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    private void requestDataFromServer() {
        //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
        HttpUtils.sendOkHttpRequest(StaticClass.GPS_LODE,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的具体内容
                String responseData=response.body().string();
                LogUtil.d("responseData:"+responseData);
                Gson gson = new Gson();
                newgpsList = gson.fromJson(responseData,new TypeToken<List<GPSEventMsp>>() {}.getType());
                LogUtil.d("gpsList:"+newgpsList);
                if(null!=newgpsList && newgpsList.size()!=0){
                    UploadSuccess = true;
                }
            }
            @Override
            public void onFailure(Call call,IOException e){
                //在这里进行异常情况处理
                LogUtil.d("从服务器获取失败！");
            }
        });
    }
    private void initView(View view) {
        gps_error_page=(LinearLayout)view.findViewById(R.id.gps_error_page);
        recy_gps=(XRecyclerView)view.findViewById(R.id.recy_gps);
        btn_reload_gps=(Button)view.findViewById(R.id.btn_reload_gps);
        btn_reload_gps.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        new UploadGPSAsyncTask().execute();
    }

    private class UploadGPSAsyncTask extends AsyncTask<Void, Integer, Boolean> {
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
            while((System.currentTimeMillis()-time <500)) {
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

            Log.i("IOTlog","result is : "+ result);

            if (result) {
                gpsAdapter = new GPSAdapter(getActivity(),newgpsList);
               LogUtil.d("newgpsList"+newgpsList);
                recy_gps.setAdapter(gpsAdapter);
                gps_error_page.setVisibility(View.GONE);
                recy_gps.setVisibility(View.VISIBLE);
                LogUtil.d("执行到gps 适配这里了....");


            } else {
                gps_error_page.setVisibility(View.VISIBLE);
                recy_gps.setVisibility(View.GONE);
                LogUtil.d("获取数据失败了....");
            }
        }
    }

}
