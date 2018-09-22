package com.dolphkon.me.mqtt;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.dolphkon.me.bean.DoorEventMsg;
import com.dolphkon.me.bean.SensirEventMsg;
import com.dolphkon.me.bean.TemperEventMsg;
import com.dolphkon.me.constant.StaticClass;
import com.dolphkon.me.utils.LogUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

public class MQTTService extends Service {
    public static final String TAG = MQTTService.class.getSimpleName();
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private String host = "tcp://studio.iot-yun.com:1883";
    private String userName = "nick";
    private String passWord = "dolphkon";
    String[] myTopic = {StaticClass.TOPIC_DOOR, StaticClass.TOPIC_SENSIRION, StaticClass.TOPIC_TEMPER};
    private String clientId = "Android_client";
    int[] Qos = {0, 0, 0};
    private NetWorkStateReceiver netWorkStateReceiver;
    public MQTTService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(this, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());
        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
//        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
       /* for (String topic:myTopic){
            if ((!message.equals("")) || (!topic.equals(""))) {
                // 最后的遗嘱
                try {
                    conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
                } catch (Exception e) {
                    Log.i(TAG, "Exception Occured",e);
                    doConnect = false;
                    iMqttActionListener.onFailure(null, e);
                    LogUtil.d("已断开连接");
                }
            }
        }*/
        if (doConnect) {
            doClientConnection();
        }
    }
    @Override
    public void onDestroy() {
        try {
            client.disconnect();
            if (null != netWorkStateReceiver) {
                unregisterReceiver(netWorkStateReceiver);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            LogUtil.d("连接成功！");
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic, Qos);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            doClientConnection();
            LogUtil.d("连接失败！");
        }
    };
    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str = new String(message.getPayload());
            LogUtil.d(str);
            //            将接收到的数据进行以逗号分隔
            String[] sensorArray = str.split(",");
            String device = sensorArray[0];
            float battery = Float.parseFloat(sensorArray[1]);
            float strength = Float.parseFloat(sensorArray[2]);
            String address = sensorArray[3];
            String time = sensorArray[4];
            switch (topic) {
                case StaticClass.TOPIC_TEMPER:
                    float temperature = Float.parseFloat(sensorArray[5]);
                    TemperEventMsg temperEventMsg = TemperEventMsg.getInstance();
                    temperEventMsg.setTdevice(device);
                    temperEventMsg.setTbattery(battery);
                    temperEventMsg.setTstrength(strength);
                    temperEventMsg.setTaddress(address);
                    temperEventMsg.setTime(time);
                    temperEventMsg.setTemperature(temperature);
                    LogUtil.d("device:" + device);
                    LogUtil.d("batery:" + battery);
                    LogUtil.d("mqtt_messageArrived: " + temperEventMsg.toString());

                    //    通过 EventBus 事件分发机制再次发布
                    EventBus.getDefault().post(temperEventMsg);
                    break;
                case StaticClass.TOPIC_SENSIRION:
                    float stemperature = Float.parseFloat(sensorArray[5]);
                    float shumidity = Float.parseFloat(sensorArray[6]);
                    SensirEventMsg sensirEventMsg = SensirEventMsg.getInstance();
                    sensirEventMsg.setSdevice(device);
                    sensirEventMsg.setSbattery(battery);
                    sensirEventMsg.setStrength(strength);
                    sensirEventMsg.setSaddress(address);
                    sensirEventMsg.setStime(time);
                    sensirEventMsg.setStemperature(stemperature);
                    sensirEventMsg.setShumidity(shumidity);
                    //    通过 EventBus 事件分发机制再次发布
                    EventBus.getDefault().post(sensirEventMsg);
                    break;
                case StaticClass.TOPIC_DOOR:
                    int disopen = Integer.parseInt(sensorArray[5]);
                    DoorEventMsg doorEventMsg = DoorEventMsg.getInstance();
                    doorEventMsg.setDevice(device);
                    doorEventMsg.setDbattery(battery);
                    doorEventMsg.setDstrength(strength);
                    doorEventMsg.setDaddress(address);
                    doorEventMsg.setDtime(time);
                    doorEventMsg.setDisopen(disopen);
                    //    通过 EventBus 事件分发机制再次发布
                    EventBus.getDefault().post(doorEventMsg);
                    break;
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            LogUtil.d("MQTT当前网络名称：" + name);
            return true;
        } else {
            LogUtil.d("MQTT 没有可用网络");
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
   *  BroadcastReceiver when insert/remove the device USB plug into/from a USB port
   *  创建一个广播接收器接收USB插拔信息：当插入USB插头插到一个USB端口，或从一个USB端口，移除装置的USB插头
   */
    public class NetWorkStateReceiver extends BroadcastReceiver {
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
                    Toast.makeText(MQTTService.this, "网络已断开，MQTT 服务暂时断开", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MQTTService.this, "MQTT 已正常运行！", Toast.LENGTH_SHORT).show();
                    doClientConnection();
                }
                //API大于23时使用下面的方式进行网络监听
            } else {

                System.out.println("API level 大于23");
                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                if (networks.length == 0) {
                    Toast.makeText(MQTTService.this, "网络已断开，MQTT 服务暂时断开", Toast.LENGTH_SHORT).show();
                } else {
                    //有网了
                    Toast.makeText(MQTTService.this, "MQTT 已正常运行！", Toast.LENGTH_SHORT).show();
                    doClientConnection();
                }
            }

        }
    }
}
