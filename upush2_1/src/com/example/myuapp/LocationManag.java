
/**   
 * @Title: LocationManag.java 
 * @Package: com.yonyou.upushv20 
 * @Description: TODO
 * @author Administrator  
 * @date 2014-12-4 上午9:42:11 
 * @version 1.3.1 
 */


package com.example.myuapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.yonyou.protocol.Constants;
import com.yonyou.protocol.LocationUPush;
import com.yonyou.protocol.LogUtil;
import com.yonyou.pushclient.PushServiceManager;
import com.yonyou.pushclient.PushServiceManager.ServiceType;
import com.yonyou.upush.interf.LBSManager;


/** 
 *
 * @package com.yonyou.upushv20
 * @author huangbq
 * @date 2014-12-4 上午9:42:11 
 */

public class LocationManag implements LBS {

	/** 日志信息*/
	private final static String TAG = LogUtil.makeLogTAG(LocationManager.class);
	/** 当前位置信息*/
	private LocationUPush locationUPush;
	/** 位置信息管理器*/
	private LocationManager locationManager;
	/** 位置监听器*/
	private LocatListener locatListener;
	/** Context 对象*/
	@SuppressWarnings("unused")
	private Context context;

	public LocationManag() {
		locationUPush = new LocationUPush();
	}
	
	/**
	 * Description  
	 * @see com.yonyou.upush.interf.LBS#onClearup() 
	 */

	@Override
	public void onClearup() {
		if (null != locatListener && null != locationManager) {
			locationManager.removeUpdates(locatListener);
		}
		locatListener = null;
		locationManager = null;
	}

	/**
	 * Description 
	 * @return 
	 * @see com.yonyou.upush.interf.LBS#getLocation() 
	 */

	@Override
	public LocationUPush getLocation() {
		// TODO Auto-generated method stub
		return locationUPush;
	}

	/** 
	 * 位置变化监听器
	 * @package com.yonyou.upush.impl
	 * @author huangbq
	 * @date 2014-11-24 下午4:53:43 
	 */ 
	  	
	private class LocatListener implements LocationListener {

		/**
		 * Description 
		 * @param location 
		 * @see android.location.LocationListener#onLocationChanged(android.location.Location) 
		 */ 
			
		@Override
		public void onLocationChanged(Location location) {
			// 当位置信息发生变化时，更新位置信息
//			PushServiceManager.sendLocation(getLocationUPush(location));
			((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER))
			.sendLocation(location.getLatitude(), location.getLongitude());
			getLocationUPush(location).toStrin();
			System.out.println("*****************LocationChanged*******************");
			
		}

		/**
		 * Description 
		 * @param provider 
		 * @see android.location.LocationListener#onProviderDisabled(java.lang.String) 
		 */ 
			
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub			
		}

		/**
		 * Description 
		 * @param provider 
		 * @see android.location.LocationListener#onProviderEnabled(java.lang.String) 
		 */ 
			
		@Override
		public void onProviderEnabled(String provider) {
			// 当locatingProvider可用时
			if (null != provider && null != locationManager) {
				Location location = locationManager.getLastKnownLocation(provider);
				if (null == location) {
					if (Constants.isLog()) {
						Log.d(TAG, "获取地理位置信息失败： location = null");
					}
				} else {
			//		PushServiceManager.sendLocation(getLocationUPush(location));
					((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER))
					.sendLocation(location.getLatitude(), location.getLongitude());
					getLocationUPush(location).toStrin();
				}				
			} else {
				if (Constants.isLog()) {
					Log.d(TAG, "获取地理位置信息失败： location = null");
				}
			}
			System.out.println("***************************Provider Enabled888888888888888888888");			
		}

		/**
		 * Description 
		 * @param provider
		 * @param status
		 * @param extras 
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle) 
		 */ 
			
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub		
		}
	
	}

	/**
	 * Description 
	 * @param context
	 * @param locationManager
	 * @param minTime
	 * @param minDistance 
	 * @see com.yonyou.upush.interf.LBS#onInit(android.content.Context, android.location.LocationManager, long, float) 
	 */ 
		
	@Override
	public void onInit(Context context, LocationManager locationManager,
			long minTime, float minDistance) {
		// 参数检查
		if (null == context || null == locationManager) {
			if (Constants.isLog()) {
				Log.d(TAG, "context 或 LocationManager 不能为null");
			}
			return;
		}
		if (minTime < 0 || minDistance< 0 ) {
			if (Constants.isLog()) {
				Log.d(TAG, "重定位间隔 minTime 或 minDistance 不能为负值");
			}
			return;
		}
		System.out.println("onInit()  *******");
		// LBS服务初始化
		this.context = context;
		this.locationManager = locationManager;
		this.locatListener = new LocatListener();
		Location location = null;
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDistance, locatListener);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
		} else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, minTime, minDistance, locatListener);
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (null != location) {
			// 发送当前地理位置
//			PushServiceManager.sendLocation(getLocationUPush(location));
			((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER))
			.sendLocation(location.getLatitude(), location.getLongitude());
			getLocationUPush(location).toStrin();
		} else {
			// 获取地理位置信息失败
			if (Constants.isLog()) {
				Log.d(TAG, "获取地理位置信息失败： location = null");
			}
		}
			
	}
	
	 
	  	
	public void LBSCancel() {
		Thread LBScancel = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER))
						.isLBS()) {
					((LBSManager)PushServiceManager.getService(ServiceType.LBS_MANAGER))
					.closeLBSPush();
					System.out.println("关闭LBS服务：  …………&&***（（");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		LBScancel.start();
	}
	
	private LocationUPush getLocationUPush(Location location) {
		LocationUPush locaU = new LocationUPush();
		locaU.setLbs(true);
		locaU.setLatitude(location.getLatitude());
		locaU.setLongitude(location.getLongitude());
		return locaU;
	}
}
