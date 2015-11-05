package com.yyuap.upush.plugin.pushnotification;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myuapp.PushServiceListenService;
import com.example.myuapp.PushServiceManager;
import com.yonyou.protocol.Constants.Notificatin;


public class PushNotification extends CordovaPlugin 
{
	private BroadcastReceiver receiver = null;
    private CallbackContext pushCallbackContext = null;
    
	public static PushNotification instance;
		
	public PushNotification() {
		instance = this;
	}
	
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
	{
		if(action.equals("init"))
		{
	        
	         PushServiceManager.setDebugMode(true); 

	         PushServiceManager.init(cordova.getActivity().getApplicationContext());
	         
	         PushServiceManager.SetNotification.setContentTitle(Notificatin.NOTIFICATION_TITLE);
//	 		MsgManager msgManagerImpl = new MsgManager(cordova.getActivity());	 		
//	 		PushServiceManager.setInformationManager(msgManagerImpl);
//	 		PushServiceManager.setNotificationActivity(NotificationActivityImple.class);
	 		
	 		Intent intent = new Intent(PushServiceListenService.getAction());
	 		cordova.getActivity().startService(intent);
	        System.out.println("Service---------------------------------------"); 
            return true;
		}
		return false;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static void transmitOpen(String title,
			String description,String value) {
	
		if (instance == null) {
			return;
		}
		
		if(value==null)
		{
			value = "";
		}
		
		JSONObject data = new JSONObject();
		try {
			
			data.put("title", title);
			data.put("description", description);
			data.put("value", value);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String js = String
				.format("fastgoPushNotification.openNotificationInAndroidCallback('%s');",
						data.toString());

		try {
//			webview.sendJavascript(js);
			instance.webView.sendJavascript(js);
			

		} catch (NullPointerException e) {

		} catch (Exception e) {

		}
		
	}
	
	
}
