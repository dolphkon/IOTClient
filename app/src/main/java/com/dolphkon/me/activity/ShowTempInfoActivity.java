package com.dolphkon.me.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.TempJson;
import com.dolphkon.me.bean.TemperEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.HttpUtils;
import com.dolphkon.me.utils.LogUtil;
import com.dolphkon.me.view.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.jn.chart.charts.LineChart;
import com.jn.chart.data.Entry;
import com.jn.chart.data.LineData;
import com.jn.chart.manager.LineChartManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ShowTempInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private List<TempJson> tempJsonsList=new ArrayList<>();
    private String device;
    private Dialog progressDialog;
    private boolean UploadSuccess=false;
    private com.jn.chart.charts.LineChart linchart_temp;
    private List<String> xAxisValues;
    private List<List<Float>> yAxisValues;
    private List<String> titles;
    private LineData lineData;
    private LineChart lineChart1;
    private float[] datas1;
    private float[] datas2;
    private LineChart lineChart2;
    private Typeface mTf;
    private int timetempy1;
    private int timetempy2;
//    private Description 消防物联近7日温度变化;

    ArrayList<String> xValues = new ArrayList<>();
    ArrayList<Entry> yValue = new ArrayList<>();
    ArrayList<Entry> yValue1 = new ArrayList<>();
    private TextView showtemp_temp;
    private Button showtemp_btnhistory;
    private TextView showtemp_time;
    private TextView showtemp_strength;
    private TextView showtemp_electric;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtemp_info);
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
        //注册 EventBus
        EventBus.getDefault().register(this);
        LogUtil.d("tdevice"+device);
        initView();
//        initData();
        //        初始化从服务器获取数据
        //判断是否有网络连接
        if (HttpUtils.isNetworkConnected(ShowTempInfoActivity.this)) {
            //如果有网络连接
            LogUtil.d( "onCreate: 初始化中.......");
            //重新请求网络数据 从服务器获取数据
            new UploadTempMaxAsyncTask().execute();

            LogUtil.d(tempJsonsList.toString());
        } else {
            //当前没有网络连接
            Toast.makeText( ShowTempInfoActivity.this, "网络无法连接,请稍后重试", Toast.LENGTH_LONG).show();
        }
    }
    private void initView() {
        linchart_temp = (LineChart) findViewById(R.id.linchart_temp);
        showtemp_temp=(TextView)findViewById(R.id.showtemp_temp);
        showtemp_btnhistory=(Button)findViewById(R.id.showtemp_btnhistory);
        showtemp_time=(TextView)findViewById(R.id.showtemp_time);
        showtemp_strength=(TextView)findViewById(R.id.showtemp_strength);
        showtemp_electric=(TextView)findViewById(R.id.showtemp_electric);
        showtemp_btnhistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(new Intent(ShowTempInfoActivity.this,HistoryTempActivity.class));
        intent.putExtra("tdevice",getIntent().getStringExtra("tdevice"));
        startActivity(intent);
    }
    //    接收数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage(TemperEventMsg temperEventMsg) {
        LogUtil.d("temperEventMsg"+temperEventMsg.toString());
      if (temperEventMsg.getTdevice().equals(device)){
          showtemp_temp.setText(""+temperEventMsg.getTemperature());
          showtemp_time.setText(""+temperEventMsg.getTime());
          showtemp_electric.setText(""+temperEventMsg.getTbattery());
          showtemp_strength.setText(""+temperEventMsg.getTstrength());
      }
    }

    private class UploadTempMaxAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressBarDialog.createLoadingDialog(ShowTempInfoActivity.this, "加载中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            requestDataFromServer();
            // 阻塞线程
            long time = System.currentTimeMillis();//去系统时间的毫秒数
            while((System.currentTimeMillis()-time < 1000)) {
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
            Log.i("IOTlog","result_show is : "+ result);
            if (result) {
                initView();
                Log.i("IOTlog","tempJsonsList is : "+ tempJsonsList.toString());


               //设置x轴的数据
                for (TempJson tempJson:tempJsonsList){
                    String xvalue = tempJson.getNewtime().replace("-","");
//                    xvalue.substring(xvalue.length()-2,xvalue.length())
                    xValues.add( xvalue.substring(xvalue.length()-2,xvalue.length()));
                    LogUtil.d("tempJson.getNewtime()"+tempJson.getNewtime());
                }
                Log.i("IOTlog","xValues is : "+ xValues.toString());

                //设置第一条y轴的数据
                LineChartManager.setLineName("最高温度");

                for (int i = 0;i<tempJsonsList.size();i++) {
                    TempJson tempJson = new TempJson();
                    tempJson = tempJsonsList.get(i);
                    yValue.add(new Entry(tempJson.getTempmax(),i));
                    Log.i("IOTlog","tempJson.getTempmax() is : "+ tempJson.getTempmax());
                }
                Log.i("IOTlog","yValue1 is : "+ yValue.toString());



                //设置折线的名称
                LineChartManager.setLineName1("最低温度");
                //设置第一条y轴的数据

                for (int i = 0;i<tempJsonsList.size();i++) {
                    TempJson tempJson = new TempJson();
                    tempJson = tempJsonsList.get(i);
                    yValue1.add(new Entry(tempJson.getTempmin(),i));
                    Log.i("IOTlog","tempJson.getTempmin() is : "+ tempJson.getTempmin());
                }
                Log.i("IOTlog","yValue2 is : "+ yValue1.toString());

                //创建两条折线的图表
                LineChartManager.initDoubleLineChart(ShowTempInfoActivity.this,linchart_temp,xValues,yValue,yValue1);

            } else {
//                new UploadTempMaxAsyncTask().execute();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void requestDataFromServer() {
        OkHttpUtils
                .get()
                .url(StaticClass.TEMPMAX)
                .addParams("tdevice_seven",device)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.d("response_max"+response);
                        Gson gson = new Gson();
                        tempJsonsList = gson.fromJson(response, new TypeToken<List<TempJson>>() {}.getType());
                        if(null!=tempJsonsList && tempJsonsList.size()!=0){
                            UploadSuccess = true;
                        }
                        LogUtil.d(tempJsonsList.toString());

                    }
                });

    }
}
