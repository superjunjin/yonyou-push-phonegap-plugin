
/**   
 * @Title: PushServiceListenService.java 
 * @Package: com.yonyou.myowndome 
 * @Description: TODO
 * @author Administrator  
 * @date 2014-10-23 下午1:57:31 
 * @version 1.3.1 
 */


package com.example.myuapp;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** 
 *
 * @package com.yonyou.myowndome
 * @author huangbq
 * @date 2014-10-23 下午1:57:31 
 */

public class PushServiceListenService extends Service {
	
//	private static final String TAG = LogUtil.makeLogTAG(PushServiceListenService.class);
	
	private static final String ACTION = PushServiceManager.packageName+".PUSH_LISTENSERVICE";

//	private MsgManager msgManagerImpl;
	
//	private PushServiceManager pushServiceManager;
	
	public PushServiceListenService() {
	}

	/**
	 * Description 
	 * @param arg0
	 * @return 
	 * @see android.app.Service#onBind(android.content.Intent) 
	 */

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	 
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 自定义�?知显示界�?
//		PushServiceManager.setNotificationActivity(NotificationActivityImple.class);
		// �?��推�?服务
		PushServiceManager.Start();
		// 注册推�?服务在线监听�?
		PushServiceManager.registerServiceOnlineReceiver(this);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PushServiceManager.unregisterServiceOnlineReceiver(this);
		PushServiceManager.Stop();
//		msgManagerImpl = null;
		
	}


	/** 
	 * 获取服务响应action
	 * @return ACTION
	 * @author huangbq
	 * @data 2014-10-23 下午2:01:05 
	 */
	  	
	public static String getAction() {
		return ACTION;
	}

	
}
