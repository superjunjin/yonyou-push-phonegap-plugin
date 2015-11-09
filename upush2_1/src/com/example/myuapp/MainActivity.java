package com.example.myuapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.example.myuapp.R;
import com.yonyou.protocol.LocationUPush;
import com.yonyou.protocol.LogUtil;
import com.yonyou.protocol.Constants.Notificatin;
import com.yonyou.pushclient.PushServiceManager;
import com.yonyou.pushclient.PushServiceManager.ServiceType;
import com.yonyou.upush.impl.LBSManagerImpl;
import com.yonyou.upush.impl.TagManagerImpl;
import com.yonyou.upush.interf.LBSManager;
import com.yonyou.upush.interf.TagManager;




import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = LogUtil.makeLogTAG(MainActivity.class);
	private boolean isLBS = false;
	private LocationManag locationManag = null;
	private LocationManager lm = null;
	private SharedPreferences sharedPref = null;
	private int mode=0;
	private PopupWindow popup ;
	private boolean start = false;
	@SuppressLint("HandlerLeak") 
	public Handler handler = new Handler() {

		
		@Override
		public void handleMessage(Message msg) {
			
			if (0x111 == msg.what) {
					if (PushServiceManager.isConnected()) {
						((Button)MainActivity.this.findViewById(R.id.bt_on_off)).setText("关闭upush");						
					}				
			} else if (0x112 == msg.what) {
				((Button)MainActivity.this.findViewById(R.id.bt_on_off)).setText("开启upush");
			} else if (0x113 == msg.what) {
				TextView locaText = (TextView)findViewById(R.id.show_location);
				Bundle bundle = msg.getData();
				if (1 == bundle.getInt("type")) {
					String lBS_provider = sharedPref.getString(LBSManagerImpl.LBS_PROVIDER, null);
					// 更新经纬度
					locaText.setText("");
					locaText.setText("LBS:" + lBS_provider + "\n"
					+ "纬度: " + bundle.getDouble("latitude") + "\n"
							+ "经度: " + bundle.getDouble("longitude"));
					
//					Toast.makeText(MainActivity.this, "纬度: " + bundle.getDouble("latitude") + "\n"
//							+ "经度: " + bundle.getDouble("longitude"), Toast.LENGTH_SHORT).show();
				} else if (2 == bundle.getInt("type")) {
					// 没有获取到位置信息
					Toast.makeText(MainActivity.this, "未获取到位置信息", Toast.LENGTH_SHORT).show();
				} else if (3 == bundle.getInt("type")) {
					// 位置服务没打开
					Toast.makeText(MainActivity.this, "请打开位置推送服务", Toast.LENGTH_SHORT).show();
				}
			}
		}		
	};

    @SuppressWarnings("deprecation")
	@SuppressLint({ "SimpleDateFormat", "InlinedApi" }) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity onCreate()");
        sharedPref = PushServiceManager.getSharedPreferences();
//        Button button = (Button)findViewById(R.id.set_LBS);
        // 读取状态信息
        // 读取LBS状态
//        if (sharedPref.getBoolean(LBSManagerImpl.LBS_STATE, false)) {
//        	button.setText("关闭位置服务");
//        	isLBS = true;  
//        } else {
//        	button.setText("开启位置服务");
//        	isLBS =false;
//        }
        // 读取当前应用所拥有的Tag
        EditText editText = (EditText)findViewById(R.id.edit_tag);
        Set<String> tags = null;
        tags = sharedPref.getStringSet(TagManagerImpl.TAGS_KEY, tags);
        StringBuffer tagBuffer = new StringBuffer(1024);
        if (null != tags && !tags.isEmpty()) {
        	for (String tag : tags) {
        		tagBuffer.append(tag);
        		tagBuffer.append(',');
        	}
        	editText.setText(tagBuffer.toString());
        }  
        // 读取自定义消息
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        TextView view = (TextView) findViewById(R.id.show_message);
		if (null != MsgManager.currentMsg) {
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
			view.clearAnimation();
			view.setText("消息标题： " + MsgManager.currentMsg.getSubject() + "\n" 
				+"消息内容： " + MsgManager.currentMsg.getContent() + "\n"
				+"消息时间： " + sDateFormat.format(new Date()) );	
			MsgManager.currentMsg = null;
		}
		
		// 装载页面布局
		View explainView = this.getLayoutInflater().inflate(R.layout.explain, null);
		// 创建PopupWindow对象
		popup = new PopupWindow(explainView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		explainView.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 关闭
				popup.setAnimationStyle(R.anim.pupopstyleout);
				popup.dismiss();
			}
		});
		ColorDrawable dw = new ColorDrawable(-00000);
        popup.setBackgroundDrawable(dw);
        popup.update();
    }

    

    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		start = true;
		PushServiceManager.SetNotification.setContentTitle(Notificatin.NOTIFICATION_TITLE);
		MsgManager msgManagerImpl = new MsgManager(this);
		// 设置自定义消息处理器
		PushServiceManager.setInformationManager(msgManagerImpl);
		PushServiceManager.setNotificationActivity(NotificationActivityImple.class);
		// 启动状态监控服务，用于注册推送服务在线监听器
		Intent intent = new Intent(PushServiceListenService.getAction());
		startService(intent);
		String device_id = PushServiceManager.getDeviceId();
//		Thread lbsThread = new Thread(new Runnable() {			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (isLBS) {
//					// 获取地理位置管理器LBSManager
//		        	LBSManager lbsManager = (LBSManager) 
//		        			PushServiceManager.getService(ServiceType.LBS_MANAGER);
//		        	if (null != lbsManager) {
//		        		lbsManager.closeLBSPush();
//		        		lbsManager.openLBSPush();
//		        	}
//		        	
//				} else {
//					// 获取地理位置管理器LBSManager
//		        	LBSManager lbsManager = (LBSManager) 
//		        			PushServiceManager.getService(ServiceType.LBS_MANAGER);
//		        	if (null != lbsManager) {
//		        		lbsManager.closeLBSPush();
//		        	}		        	
//				}
//			}
//		});
//		lbsThread.start();
		
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		start = false;
	}



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
     
    /** 
     * 设置链接地址
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:44:49 
     */
      	
    public void setAddress(View source) {
    	EditText ipEdit = (EditText)MainActivity.this.findViewById(R.id.edit_ip);
    	String ip = ipEdit.getText().toString();
    	EditText portEdit = (EditText)MainActivity.this.findViewById(R.id.edit_port);
    	if (null == ip || ip.equals("") || null == portEdit 
    			|| portEdit.getText().toString().equals("")) {
    		Toast.makeText(MainActivity.this, "请输入合法地址", Toast.LENGTH_SHORT).show();
    	} else {
    		Thread setAddressThread = new Thread(new Runnable() {	
    			@Override
    			public void run() {
    				// TODO Auto-generated method stub
    				handler.sendEmptyMessage(0x112);
    				Intent intent = new Intent(PushServiceListenService.getAction());
			    	stopService(intent);
			    	
    				EditText ipEdit = (EditText)MainActivity.this.findViewById(R.id.edit_ip);
    		    	String ip = ipEdit.getText().toString();
    		    	EditText portEdit = (EditText)MainActivity.this.findViewById(R.id.edit_port);
    		    		int port = Integer.valueOf(portEdit.getText().toString());   		    		
//    			    	PushServiceManager.Stop();
    			    	try {
    						Thread.sleep(5000);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    			    	PushServiceManager.setAddress(ip, port);
    			    	Intent intent1 = new Intent(PushServiceListenService.getAction());
    		    		startService(intent1);	    		
//    			    	PushServiceManager.Start();
    		    		try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    		    		handler.sendEmptyMessage(0x111);
    		    	}		    	
    		});
        	setAddressThread.start();
    	}   	
    }
    
     public void bt_explain(View source) {
    	 popup.setAnimationStyle(R.anim.pupopstylein);
    	 popup.showAtLocation(source, Gravity.CENTER, 20, 20);
    	 
    	 
     }
    /** 
     * UPush 服务开启或关闭按钮
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:45:43 
     */
      	
    public void bt_on_off(View source) {
    	Button button = (Button) this.findViewById(R.id.bt_on_off);
    	if (button.getText().equals("关闭upush")) {
    		Intent intent = new Intent(PushServiceListenService.getAction());
	    	stopService(intent);	    	
//	    	PushServiceManager.Stop();
	    	button.setText("开启upush");
    	} else if (button.getText().equals("开启upush")) {
    		Intent intent = new Intent(PushServiceListenService.getAction());
    		startService(intent);
    		button.setText("关闭upush");
    	}
    }
    
     
    /** 
     * 添加标签按钮
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:47:03 
     */
      	
    public void addTag(View source) {
    	if (PushServiceManager.isConnected() && 
    			((Button) this.findViewById(R.id.bt_on_off))
    			.getText().equals("关闭upush")) {
    		EditText view =(EditText) findViewById(R.id.edit_tag);
        	String tagText = view.getText().toString();
        	String[] tags = tagText.split(",");
        	ArrayList<String> tagList = new ArrayList<String>();
        	for (String tag : tags) {
        		tagList.add(tag);
        	}
        	if (1 == tagList.size()) {
        		String tag = tagList.get(0);
        		if (tag.equals("")) {
        			Toast.makeText(MainActivity.this, "请在编辑框中输入要贴的标签，多标签以逗号(',')分隔", Toast.LENGTH_SHORT).show();
        		} else {
        			TagManager tagManager = (TagManager) PushServiceManager.getService(ServiceType.TAG_MANAGER);
            		tagManager.addTag(tag);
            		Toast.makeText(MainActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
        		}
        		
        	} else {
        		((TagManager) PushServiceManager.getService(ServiceType.TAG_MANAGER)).addTags(tagList);
        		Toast.makeText(MainActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
        	}
        	view.setText("");
    	} else {
    		Toast.makeText(MainActivity.this, "没有连上服务器，操作失败", Toast.LENGTH_SHORT).show();
    	}   	
    }
     
    /** 
     * 删除标签按钮
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:47:20 
     */
      	
    public void deleteTag(View source) {
    	if (PushServiceManager.isConnected()&& 
    			((Button) this.findViewById(R.id.bt_on_off))
    			.getText().equals("关闭upush")) {
    		EditText view = (EditText)findViewById(R.id.edit_tag);
        	String tagText = view.getText().toString();
        	String[] tags = tagText.split(",");
        	ArrayList<String> tagList = new ArrayList<String>();
        	for (String tag : tags) {
        		tagList.add(tag);
        	}
        	if (0 == tagList.size()) {
        		
        	}else if (1 == tagList.size()) {
        		String tag = tagList.get(0);
        		if (tag.equals("")) {
        			Toast.makeText(MainActivity.this, "请输入要撕的标签", Toast.LENGTH_SHORT).show();
        		} else {
        			((TagManager)PushServiceManager.getService(ServiceType.TAG_MANAGER)).deleteTag(tag);
        			Toast.makeText(MainActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
        		}
        		
        	} else {
        		((TagManager)PushServiceManager.getService(ServiceType.TAG_MANAGER)).deleteTags(tagList);
        		Toast.makeText(MainActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
        	}
        	view.setText("");
    	} else {
    		Toast.makeText(MainActivity.this, "没有连上服务器，操作失败", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
     
    /** 
     * 设置LBS服务状态（开启或关闭）
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:48:59 
     */
      	
    public void setLBS(View source) {
    	if (PushServiceManager.isConnected()&& 
    			((Button) this.findViewById(R.id.bt_on_off))
    			.getText().equals("关闭upush")) {
    		Button setLbsButton = (Button)findViewById(R.id.set_LBS);
        	// 获取地理位置管理器LBSManager
        	LBSManager lbsManager = (LBSManager) 
        			PushServiceManager.getService(ServiceType.LBS_MANAGER);
//        	RadioGroup rg = (RadioGroup)findViewById(R.id.rg);
//        	int checkedId = rg.getCheckedRadioButtonId();
        	int checkedId = 0;
        	if (isLBS) {
        		// 关闭LBS服务
            	lbsManager.closeLBSPush();
        		// 当前使用LBS，操作——撤销LBS	 
        		isLBS = false;
        		setLbsButton.setText("开启位置服务");
        		if (null != locationManag) {
        			locationManag.LBSCancel();
        			locationManag.onClearup();
        			locationManag = null;
        		} else {
        			locationManag = new LocationManag();
        			locationManag.LBSCancel();
        			locationManag.onClearup();
        			locationManag = null;
        		}
        	}else{
        		// 当前未使用LBS，操作——开启LBS服务
        		isLBS = true;
        		setLbsButton.setText("关闭位置服务");
        		switch (checkedId) {
        		case 0: // R.id.mode1:
        			// 用户选择 mode1 ： 默认模式	
        			lbsManager.openLBSPush();
        			mode=1;
        			break;
        		case 1: //R.id.mode2:
        			// 用户选择 mode2: 用户自定义位置变化监听条件 (如5分钟、1000米)
        			lbsManager.openLBSPush(300000, 1000);
        			mode=2;
        			break;
        		case 2: //R.id.mode3:
        			// 用户选择 mode3： 用户自己实现地理位置信息的获取，和位置变化监听。自定义位置信息发送条件。
        			if (null != locationManag) {
            			locationManag.onClearup();
            			locationManag = null;
            		}
            		locationManag = new LocationManag();
            		locationManag.onInit(this, lm, 3000,0);
            		mode=3;
        			break;
        		}
        	}
    	} else {
    		Toast.makeText(MainActivity.this, "没有连上服务器，操作失败", Toast.LENGTH_SHORT).show();
    	}   	
    }
     
    /** 
     * 展示当前地理位置
     * @param source
     * @author huangbq
     * @data 2014-12-30 下午6:49:22 
     */
      	
    public void showLBS(View source) {
    	TextView locaText = (TextView)findViewById(R.id.show_location);
        LocationUPush loc = null;
    	if (PushServiceManager.isConnected()&& 
    			((Button) this.findViewById(R.id.bt_on_off))
    			.getText().equals("关闭upush")) {
    		switch (mode) {
        	case 0:
        		Toast.makeText(MainActivity.this, "请打开位置推送服务", Toast.LENGTH_SHORT).show();
        		break;
    		case 1:
    			Thread thread = new Thread(new Runnable() {					
					@Override
					public void run() {					
						while(isLBS && start) {
							Bundle bundle = new Bundle();
							Message message = new Message();
							message.what = 0x113;
							try {    				
								LocationUPush loc = ((LBSManager)PushServiceManager
										.getService(ServiceType.LBS_MANAGER)).getLocationUPush();						
			    				if (null != loc) {			    					
			    					bundle.putInt("type", 1);
			    					bundle.putDouble("latitude", loc.getLatitude());
			    					bundle.putDouble("longitude", loc.getLongitude());		
			    					message.setData(bundle);
			    					handler.sendMessage(message);			    				
			    				} else {
			    					bundle.putInt("type", 2);
			    					message.setData(bundle);
			    					handler.sendMessage(message);
			    					Toast.makeText(MainActivity.this, "未获取到位置信息", Toast.LENGTH_SHORT).show();
			    				}
			    			} catch (Exception e) {
			    				bundle.putInt("type", 3);
			    				message.setData(bundle);
			    				handler.sendMessage(message);
			    				Toast.makeText(MainActivity.this, "请打开位置推送服务", Toast.LENGTH_SHORT).show();
			    			}
							
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// TODO Auto-generated method stub						
					}
				});
				thread.start();    			
    			break;
    		case 2:
    			try {
    				loc = ((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER)).getLocationUPush();
    				if (null != loc) {
    					locaText.setText("");
    					locaText.setText("Latitude: " + loc.getLatitude() + "\n"
    							+ "Longitude: " + loc.getLongitude());
    				} else {
    					Toast.makeText(MainActivity.this, "未获取到位置信息", Toast.LENGTH_SHORT).show();
    				}
    			} catch (Exception e) {
    				Toast.makeText(MainActivity.this, "请打开位置推送服务", Toast.LENGTH_SHORT).show();
    			}			
    			break;
    		case 3:
    			try {
    				loc = locationManag.getLocation();
    				if (null != loc) {
    					locaText.setText("");
    					locaText.setText("Latitude: " + loc.getLatitude() + "\n"
    							+ "Longitude: " + loc.getLongitude());
    				} else {
    					Toast.makeText(MainActivity.this, "未获取到位置信息", Toast.LENGTH_SHORT).show();
    				}
    			} catch (Exception e) {
    				Toast.makeText(MainActivity.this, "请打开位置推送服务", Toast.LENGTH_SHORT).show();
    			}
    			
    		}
    	} else {
    		Toast.makeText(MainActivity.this, "没有连上服务器，操作失败", Toast.LENGTH_SHORT).show();
    	}
    
    }
}
