
/**   
 * @Title: MsgManagerImpl.java 
 * @Package: com.yonyou.myowndome 
 * @Description: TODO
 * @author Administrator  
 * @date 2014-10-23 下午1:49:10 
 * @version 1.3.1 
 */


package com.example.myuapp;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myuapp.R;
import com.yonyou.protocol.NotifyAndMsg;
import com.yonyou.upush.impl.InformationManagerAdapter;



/** 
 * 用户自定义消息处理类
 * @package com.yonyou.myowndome
 * @author huangbq
 * @date 2014-10-23 下午1:49:10 
 */

public class MsgManager extends InformationManagerAdapter {

	
	private Context context;
	private SimpleDateFormat sDateFormat;
	public static NotifyAndMsg currentNotify;
	public static NotifyAndMsg currentMsg;
	public MsgManager() {
		
	}

	@SuppressLint("SimpleDateFormat") public MsgManager(Context context) {
		this.context = context;
		sDateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
	} 

	
	@Override
	public void dealWithMessage(NotifyAndMsg notifyAndMsg) {
		// TODO Auto-generated method stub
		super.dealWithMessage(notifyAndMsg);
		currentMsg = notifyAndMsg;
		if (null != context) {
			TextView view = (TextView) ((Activity)context).
					findViewById(R.id.show_message);
			view.clearAnimation();
			view.setText("消息标题： " + notifyAndMsg.getSubject() + "\n" 
				+"消息内容： " + notifyAndMsg.getContent() + "\n"
				+"消息时间： " + sDateFormat.format(new Date()) );	
			Toast.makeText(context, "消息标题： " + notifyAndMsg.getSubject() + "\n" 
					+"消息内容： " + notifyAndMsg.getContent() + "\n"
					+"消息时间： " + sDateFormat.format(new Date()), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void dealWithNotification(NotifyAndMsg notifyAndMsg) {
		// TODO Auto-generated method stub
		super.dealWithNotification(notifyAndMsg);
		currentNotify = notifyAndMsg;
		
	}

	
}
