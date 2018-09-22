package com.dolphkon.me.listener;

/**
 * Created by dolphkon on 2017/6/18.
 * 网络调用回调接口
 */

public interface HttpCallbackListener {

    /**
     * 网络数据访问成功回调
     * @param response 访问成功返回的数据
     */
    void onFinish( String response);

    /**
     * 在这里对异常情况进行处理
     * @param e
     */
    void onError(Exception e);


}
