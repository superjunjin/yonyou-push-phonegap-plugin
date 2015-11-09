package com.example.myuapp;

import com.yonyou.protocol.LogUtil;
import com.yonyou.pushclient.PushServiceManager;

import android.app.Application;
import android.util.Log;


/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = 
    		LogUtil.makeLogTAG(ExampleApplication.class);

    @Override
    public void onCreate() {    	     
    	 Log.d(TAG, "[ExampleApplication] onCreate");
         super.onCreate();
         PushServiceManager.setDebugMode(true);  // 开启日志调试模式
//         PushServiceManager.setAddress("push.yyuap.com", 80);  // 测试阶段使用，正式发版时关闭此接口
//         PushServiceManager.setAddress("172.20.17.11", 9001);
//         PushServiceManager.setAddress("169.254.106.76", 9001);
//         PushServiceManager.setAddress("10.1.222.164", 9001);
         PushServiceManager.init(this);
    }
}
