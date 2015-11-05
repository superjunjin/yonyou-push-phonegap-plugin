package com.example.myuapp;

import com.yonyou.protocol.Constants;
import com.yonyou.protocol.NotifyAndMsg;
import com.yonyou.protocol.NotifyBack;




import com.yyuap.upush.plugin.pushnotification.PushNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.Toast;

public class MyPushMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action != null) {
			if ("com.example.myuapp.action.notification.CLICK".equals(action)) {

				int msgId = intent.getIntExtra("msg_Id", 0);
				System.out.println("msg_Id---------------" + msgId);
				// 获取当前通知
				NotifyAndMsg notify = null;
				if (null != PushServiceManager.getServiceOnlineReceiver()) {
					notify = PushServiceManager.getServiceOnlineReceiver()
							.getNotifyById(msgId);
					if (null != notify) {
						
						String title = notify.getSubject();
						String description = notify.getContent();
						String myvalue = notify.getExtratext();
						//传递通知数据给java插件
						 PushNotification.transmitOpen(title,
									description,myvalue);

					}
				}
			}
		}
	}

}
