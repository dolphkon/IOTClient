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
import com.dolphkon.me.activity.HistoryDoorActivity;
import com.dolphkon.me.adapter.DoorSwitchAdapter;
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
 *  文件名： DoorSwitchFragment
 *  创建者：  dolphkon
 *  创建时间：2018/06/04 10:19 AM
 *  描述：    门开关传感器
 */
public class DoorSwitchFragment extends Fragment implements View.OnClickListener{
    private XRecyclerView recy_doorswitch;
    private List<DoorEventMsg> newDoorswitchList=new ArrayList<>();
    private List<DoorEventMsg>updateDoorList=new ArrayList<>();
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private DoorSwitchAdapter doorswitchAdapter;
    private LinearLayout door_error_page;
    private Button btn_reload_door;
    private int refreshTime=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_door_switch, container, false);
        //        初始化从服务器获取数据
        //判断是否有网络连接
        if (HttpUtils.isNetworkConnected(getContext())) {
            //如果有网络连接
            LogUtil.d( "onCreate_doorsw: 初始化中.......");
            //重新请求网络数据 从服务器获取数据
            new UploadDoorAsyncTask().execute();
        } else {
            //当前没有网络连接
            Toast.makeText(getContext(), "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
        door_error_page=(LinearLayout)view.findViewById(R.id.door_error_page);
        recy_doorswitch=(XRecyclerView)view.findViewById(R.id.recy_doorswitch);
        btn_reload_door=(Button)view.findViewById(R.id.btn_reload_door);
        btn_reload_door.setOnClickListener(this);

        recy_doorswitch.addItemDecoration(new RecycleViewDivider(getActivity(),LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.color_yellow)));
//        recy_doorswitch.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
//       下拉刷新
        recy_doorswitch.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recy_doorswitch.setLoadingListener(new XRecyclerView.LoadingListener(){

            @Override
            public void onRefresh() {
                refreshTime ++;
                new UploadDoorAsyncTask().execute();
                doorswitchAdapter.notifyDataSetChanged();
                recy_doorswitch.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                refreshTime ++;
                new UploadDoorAsyncTask().execute();
                doorswitchAdapter.notifyDataSetChanged();
                recy_doorswitch.refreshComplete();
            }
        });
        //    注册 EventBus
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //    接收数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage(DoorEventMsg doorEventMsg) {
        LogUtil.d("Event_messageArrived: " + doorEventMsg);
    }

    @Override
    public void onClick(View v) {
        new UploadDoorAsyncTask().execute();
    }

    private class UploadDoorAsyncTask extends AsyncTask<Void, Integer, Boolean> {
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
            while((System.currentTimeMillis()-time <2000)) {
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
                doorswitchAdapter = new DoorSwitchAdapter(getActivity(),newDoorswitchList);
                recy_doorswitch.setAdapter(doorswitchAdapter);
                recy_doorswitch.setLayoutManager(new LinearLayoutManager(getActivity()));
                door_error_page.setVisibility(View.GONE);
                recy_doorswitch.setVisibility(View.VISIBLE);
                doorswitchAdapter.setOnItemClickListener(new DoorSwitchAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.i("IOTlog","temperSensorAdapter onItemClick!");
                        Intent intent = new Intent(getActivity(), HistoryDoorActivity.class);
                        intent.putExtra("device",newDoorswitchList.get(position).getDevice()+"");
                        startActivity(intent);
                    }
                });

            } else {
                door_error_page.setVisibility(View.VISIBLE);
                recy_doorswitch.setVisibility(View.GONE);
            }
        }
    }
    private void requestDataFromServer() {
        //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
        HttpUtils.sendOkHttpRequest(StaticClass.DOORSWITCH_LODE,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到服务器返回的具体内容
                String responseData=response.body().string();
                Gson gson = new Gson();
                newDoorswitchList = gson.fromJson(responseData, new TypeToken<List<DoorEventMsg>>() {}.getType());
                if(null!=newDoorswitchList && newDoorswitchList.size()!=0){
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
}