
/**   
 * @Title: LBSManager.java 
 * @Package: com.yonyou.upush.interf 
 * @Description: TODO
 * @author Administrator  
 * @date 2014-11-24 下午3:24:53 
 * @version 1.3.1 
 */


package com.example.myuapp;

import com.yonyou.protocol.LocationUPush;

import android.content.Context;
import android.location.LocationManager;

/** 
 *
 * @package com.yonyou.upush.interf
 * @author huangbq
 * @date 2014-11-24 下午3:24:53 
 */

public interface LBS {

	/** 接口名*/
	public static final String name = "LBSManager.interface";
	
	 
	/** 
	 * 初始化
	 * @param locationManager
	 *                 android 定位管理器实例
	 * @author huangbq
	 * @data 2014-11-24 下午4:00:21 
	 */
	  	
	public void onInit(Context context, LocationManager 
			locationManager, long minTime, float minDistance);
	
	 
	/** 
	 * 结束清理
	 * @author huangbq
	 * @data 2014-11-24 下午4:02:19 
	 */
	  	
	public void onClearup();
	
	 
	/** 
	 * 获取位置信息
	 * @return LocationUPush
	 * 				UPush 定位信息
	 * @author huangbq
	 * @data 2014-11-24 下午4:03:22 
	 */
	  	
	public LocationUPush getLocation();
	
	 
	/** 
	 * 撤销LBS服务
	 * @author huangbq
	 * @data 2014-12-3 下午4:40:47 
	 */
	  	
	public void LBSCancel();
}
