package com.dolphkon.me.fragment;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.dolphkon.me.R;
import com.dolphkon.me.activity.HistorySensirActivity;
import com.dolphkon.me.adapter.RecycleViewDivider;
import com.dolphkon.me.adapter.SensirionAdapter;
import com.dolphkon.me.bean.SensirEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/*
 *  项目名：IOTClient
 *  包名：  com.dolphkon.me.fragment
 *  文件名： SensirionFragment
 *  创建者：  dolphkon
 *  创建时间：2018/06/04 09:49 AM
 *  描述：   温湿度传感器
 */
public class SensirionFragment extends Fragment implements View.OnClickListener{
    private List<SensirEventMsg> newSensirionList=new ArrayList<>();
    private SensirionAdapter sensirionAdapter;
    private XRecyclerView recy_sensirion;
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private LinearLayout sensir_error_page;
    private Button btn_reload_sensir;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sensirion,container,false);
        //    注册 EventBus
        EventBus.getDefault().register(this);
        //        初始化从服务器获取数据
        //判断是否有网络连接
        if (HttpUtils.isNetworkConnected(getContext())) {
            //如果有网络连接
            LogUtil.d( "onCreate: 初始化中.......");
            //重新请求网络数据 从服务器获取数据
            new UploadSensirAsyncTask().execute();
        } else {
            //当前没有网络连接
            Toast.makeText(getContext(), "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        sensir_error_page=(LinearLayout)view.findViewById(R.id.sensir_error_page);
        recy_sensirion=(XRecyclerView)view.findViewById(R.id.recy_sensirion);
        btn_reload_sensir=(Button)view.findViewById(R.id.btn_reload_sensir);
        btn_reload_sensir.setOnClickListener(this);
        ////        设置分割线
        recy_sensirion.addItemDecoration(new RecycleViewDivider(getActivity(),LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_yellow)));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recy_sensirion.setHasFixedSize(true);
        recy_sensirion.setLayoutManager(new LinearLayoutManager(getActivity()));
//        下拉刷新
        recy_sensirion.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recy_sensirion.setLoadingListener(new XRecyclerView.LoadingListener(){

            public int refreshTime=0;

            @Override
            public void onRefresh() {
                refreshTime ++;
                new UploadSensirAsyncTask().execute();
                sensirionAdapter.notifyDataSetChanged();
                recy_sensirion.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                refreshTime ++;
                new UploadSensirAsyncTask().execute();
                sensirionAdapter.notifyDataSetChanged();
                recy_sensirion.refreshComplete();
            }
        });
        return view;
    }

    private void requestDataFromServer() {
        //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
        HttpUtils.sendOkHttpRequest(StaticClass.SENSIRION_LODE,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的具体内容
                String responseData=response.body().string();
                Gson gson = new Gson();
                newSensirionList = gson.fromJson(responseData, new TypeToken<List<SensirEventMsg>>() {}.getType());
                LogUtil.d("sensirion is getted:"+newSensirionList);
                if(null!=newSensirionList && newSensirionList.size()!=0){
                    UploadSuccess = true;
                }
            }
            @Override
            public void onFailure(Call call,IOException e){
                //在这里进行异常情况处理
                LogUtil.d("从服务器获取失败！");
//                btn_reload_sensir.performClick();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //    接收数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage(SensirEventMsg sensirEventMsg) {
//        new UploadSensirAsyncTask().execute();
        LogUtil.d("Event_messageArrived: " + sensirEventMsg);
    }

    @Override
    public void onClick(View v) {
        new UploadSensirAsyncTask().execute();
    }

    private class UploadSensirAsyncTask  extends AsyncTask<Void, Integer, Boolean> {
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
            while((System.currentTimeMillis()-time <3500)) {
            }
            Log.i("IOTlog","UploadSucess is : "+ UploadSuccess);
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
                sensirionAdapter = new SensirionAdapter(getActivity(),newSensirionList);
                recy_sensirion.setAdapter(sensirionAdapter);
                sensir_error_page.setVisibility(View.GONE);
                recy_sensirion.setVisibility(View.VISIBLE);
                sensirionAdapter.setOnItemClickListener(new SensirionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.i("IOTlog","temperSensorAdapter onItemClick!");
                        Intent intent = new Intent(getActivity(), HistorySensirActivity.class);
                        intent.putExtra("sdevice",newSensirionList.get(position).getSdevice()+"");
                        startActivity(intent);
                    }
                });

            } else {
                sensir_error_page.setVisibility(View.VISIBLE);
                recy_sensirion.setVisibility(View.GONE);
            }
        }
    }
}
