
/**   
 * @Title: NotificationActivity.java 
 * @Package: com.yonyou.myowndome 
 * @Description: TODO
 * @author Administrator  
 * @date 2014-10-23 下午3:24:10 
 * @version 1.3.1 
 */


package com.example.myuapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yonyou.protocol.NotificationActivity;
import com.yonyou.protocol.NotifyAndMsg;
import com.yonyou.pushclient.PushServiceManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 *
 * @package com.yonyou.myowndome
 * @author huangbq
 * @date 2014-10-23 下午3:24:10 
 */

public class NotificationActivityImple extends NotificationActivity {

	@SuppressLint("SimpleDateFormat") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		// 创建一个线性布局管理器
		LinearLayout linearLayout = new LinearLayout(this);
		super.setContentView(linearLayout);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		// 创建一个TextView对象
		final TextView showNotf = new TextView(this);
		showNotf.setEnabled(false);
		linearLayout.addView(showNotf);
		// 获取当前通知
		NotifyAndMsg notify = null;
		if(null != PushServiceManager.getServiceOnlineReceiver()) {
			notify = PushServiceManager
					.getServiceOnlineReceiver().getNotifyById(getNotifyId());
			if (null != notify) {
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
				showNotf.clearAnimation();
				showNotf.setText("自定义通知"+"\n"+"My通知标题： " + notify.getSubject() + "\n" 
				+"通知内容： " + notify.getContent() + "\n"
				+"扩展内容：" + notify.getExtratext() + "\n"
				+"通知时间： " + sDateFormat.format(new Date())+"\n" 
				+"msgID: " + getNotifyId());

			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

//	public void ok(View view) {
//		finish();
//	}
	
}
