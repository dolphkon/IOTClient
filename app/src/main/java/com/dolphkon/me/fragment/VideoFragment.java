package com.dolphkon.me.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dolphkon.me.R;
import com.dolphkon.me.utils.LogUtil;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/*
 *  项目名：IOTClient
 *  包名：  com.dolphkon.me.fragment
 *  文件名： VideoFragment
 *  创建者：  dolphkon
 *  创建时间：2018/06/04 10:12 AM
 *  描述：    即时监控视频
 */
public class VideoFragment   extends Fragment implements View.OnClickListener{

    private static final int SUCCESS_OFF =5;
    private static final int SUCCESS_ON = 6;
    private static final int TIMEOUT = 2;
    NetWorkStateReceiver netWorkStateReceiver;
    private FloatingActionButton fab_off;
    private static final String IP = "studio.iot-yun.com";
    private static final int PORT = 8089;
    private static final int CONNECT_SUCCESS =3;
    private static final int CONNECT_FAIL =4;
    private DataOutputStream out;
    private DataInputStream in;
    private MjpegView mjpegView;
    private LinearLayout mErrorView;
    private RelativeLayout mContainer;
    private Button mReload_on;
    byte[] SIGN_ON = {0x2f, 0x3f, 0x4f, 0x00};
    byte[] SIGN_OFF = {0x2f, 0x3f, 0x5f, 0x00};
    byte [] SIGN_CHECK_HEAD={0x2f, 0x3f};
    /**
     * 判断是否加载成功
     */
    private Socket socket;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_SUCCESS:

                    Toast.makeText(getActivity(),"Socket 连接成功!",Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(getActivity(),"Socket 连接失败!",Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS_OFF:
                    mErrorView.setVisibility(View.VISIBLE);
                    ((View) mjpegView).setVisibility(View.GONE);
                    mContainer.setBackgroundResource(R.color.mjpg);
                    break;
                case SUCCESS_ON:
                    mErrorView.setVisibility(View.GONE);
                    ((View) mjpegView).setVisibility(View.VISIBLE);
                    mContainer.setBackgroundResource(R.color.black_255);
                    loadIpCam();


                    // 阻塞线程
                    long time = System.currentTimeMillis();//去系统时间的毫秒数
                    while((System.currentTimeMillis()-time <10)) {
                        /*loadIpCam();
                        mContainer.setBackgroundResource(R.color.black_255);
                        ((View) mjpegView).setVisibility(View.VISIBLE);*/
                        mContainer.setBackgroundResource(R.color.black_255);
                        loadIpCam();
                    }
                    break;

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_video,container,false);
        initView(view);
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(netWorkStateReceiver, filter);
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=netWorkStateReceiver){
            getActivity().unregisterReceiver(netWorkStateReceiver);
        }
    }
    private void initView(View view) {
        mContainer = (RelativeLayout) view.findViewById(R.id.container);
        mErrorView = (LinearLayout)view.findViewById(R.id.ll_error_page);
        mjpegView=(MjpegView)view.findViewById(R.id.mjpegViewCustomAppearance);
        mReload_on = (Button) view.findViewById(R.id.btn_reload);
        fab_off=(FloatingActionButton)view.findViewById(R.id.fab_off);
        mReload_on.setOnClickListener(this);
        fab_off.setOnClickListener(this);
        loadIpCam();
        socket = new Socket();
    }
    private void loadIpCam() {
        Mjpeg.newInstance()
                .open("http://studio.iot-yun.com:8091/?action=stream", TIMEOUT)
                .subscribe(
                        inputStream -> {
                            Log.i("Ipcamlog","inputstream is : "+inputStream.toString());
                            mjpegView.setFpsOverlayBackgroundColor(Color.DKGRAY);
                            mjpegView.setFpsOverlayTextColor(Color.WHITE);
                            mjpegView.setSource(inputStream);
                            mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                            mjpegView.showFps(true);
                            mErrorView.setVisibility(View.GONE);
                            ((View) mjpegView).setVisibility(View.VISIBLE);
                            mContainer.setBackgroundResource(R.color.black_255);
                        },
                        throwable -> {
                            ((View) mjpegView).setVisibility(View.GONE);
                            mContainer.setBackgroundResource(R.color.mjpg);
                            mErrorView.setVisibility(View.VISIBLE);
//                          loadIpCam();
                        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reload:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket();
                            connecttoserver();
                            String strname="admin:";
                            String strpassword="admin";
                            byte[] bytename= strname.getBytes();
                            byte[] bytepassword= strpassword.getBytes();
                            byte [] user= byteMergerAll(SIGN_CHECK_HEAD,bytename,bytepassword);
                            SendMsg(socket, user);
                            byte []  result01=  ReceiveMsg(socket);
                            LogUtil.d("ReceiveMsg01: "+ result01.toString());
                            SendMsg(socket, SIGN_ON);
                            byte []  result02=  ReceiveMsg(socket);
                            LogUtil.d("ReceiveMsg02: "+ result02);
                            Message message=new Message();
                            message.what = SUCCESS_ON;
                            mHandler.sendMessage(message);
                            socket.close();
                            socket.shutdownInput();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.fab_off:
                LogUtil.d("即将为您关闭摄像头..");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connecttoserver();
                            String strname="admin:";
                            String strpassword="admin";
                            byte[] bytename= strname.getBytes();
                            byte[] bytepassword= strpassword.getBytes();
                            byte [] user= byteMergerAll(SIGN_CHECK_HEAD,bytename,bytepassword);
                            LogUtil.d("run_user: "+user);
                            SendMsg(socket, user);
                            SendMsg(socket, SIGN_OFF);
                            byte []  result01=  ReceiveMsg(socket);
                            LogUtil.d("ReceiveMsg01_off: "+ result01);
                            Message message=new Message();
                            message.what = SUCCESS_OFF;
                            mHandler.sendMessage(message);
                            socket.close();
                            socket.shutdownInput();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }
    }

    private static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    //在向服务器发送消息之前，必须先链接到服务器。
    public void connecttoserver() throws UnknownHostException, IOException {
        RequestSocket(IP, PORT);
        //判断是否链接成功
        if (socket.isConnected()) {
            Message msg = new Message();
            msg.what = CONNECT_SUCCESS;
            mHandler.sendMessage(msg);
        }else {
            Message msg = new Message();
            msg.what = CONNECT_FAIL;
            mHandler.sendMessage(msg);
        }
    }
    //连接服务器
    private void RequestSocket(String host, int port)
            throws UnknownHostException, IOException {
        socket = new Socket();
        //创建套接字地址，其中 IP 地址为通配符地址，端口号为指定值。
        //有效端口值介于 0 和 65535 之间。端口号 zero 允许系统在 bind 操作中挑选暂时的端口。
        InetSocketAddress isa = new InetSocketAddress(host, port);
        //建立一个远程链接
        socket.connect(isa);

//        return Socket;
    }
    //向服务器发送信息
    private void SendMsg(Socket socket, byte[] msg) throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        out.write(msg);
//        out.writeInt(msg.length);


    }
    // 接收服务器信息
    private byte [] ReceiveMsg(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        byte[] bytes = new byte[1024];
//        int length=in.read()
// 定义一个byte数组用来存放读取到的数据，byte数组的长度要足够大。
       /* if (length > 0) {

        }*/
        in.read(bytes);
        return bytes;
    }

    /*
    *  BroadcastReceiver when insert/remove the device USB plug into/from a USB port
    *  创建一个广播接收器接收USB插拔信息：当插入USB插头插到一个USB端口，或从一个USB端口，移除装置的USB插头
    */
    public class NetWorkStateReceiver extends BroadcastReceiver  {
        public void onReceive(Context context, Intent intent) {
            System.out.println("网络状态发生变化");
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (!wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
//                    Toast.makeText(getActivity(),"没网络了",Toast.LENGTH_SHORT).show();
                    ((View) mjpegView).setVisibility(View.GONE);
                    mErrorView.setVisibility(View.VISIBLE);
                    mContainer.setBackgroundResource(R.color.mjpg);
                }else {
//                    Toast.makeText(getActivity(),"又有网络了",Toast.LENGTH_SHORT).show();
                    ((View) mjpegView).setVisibility(View.VISIBLE);
                    mErrorView.setVisibility(View.GONE);
                    mContainer.setBackgroundResource(R.color.black_255);
                    loadIpCam();
                }
                //API大于23时使用下面的方式进行网络监听
            }else {

                System.out.println("API level 大于23");
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                if(networks.length == 0){
                    //没网
//                    Toast.makeText(getActivity(),"没网络了",Toast.LENGTH_SHORT).show();
                    ((View) mjpegView).setVisibility(View.GONE);
                    mErrorView.setVisibility(View.VISIBLE);
                    mContainer.setBackgroundResource(R.color.mjpg);
                }else{
                    //有网了
//                    Toast.makeText(getActivity(),"又有网络了",Toast.LENGTH_SHORT).show();
                    mContainer.setBackgroundResource(R.color.black_255);
                    ((View) mjpegView).setVisibility(View.VISIBLE);
                    loadIpCam();

                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
