/*     */ package com.example.myuapp;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.app.ActivityManager;
/*     */ import android.app.ActivityManager.RunningServiceInfo;
/*     */ import android.app.Service;
/*     */ import android.content.ComponentName;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.IntentFilter;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.pm.PackageManager.NameNotFoundException;
/*     */ import android.location.LocationManager;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.telephony.TelephonyManager;
/*     */ import android.util.Log;

/*     */ import com.yonyou.protocol.Constants;
/*     */ import com.yonyou.protocol.Constants.Notificatin;
/*     */ import com.yonyou.protocol.LocationUPush;
/*     */ import com.yonyou.protocol.LogUtil;
import com.yonyou.pushclient.ShowNotification;
/*     */ import com.yonyou.upush.impl.LBSManagerImpl;
/*     */ import com.yonyou.upush.impl.TagManagerImpl;
/*     */ import com.yonyou.upush.interf.InformationManager;
import com.example.myuapp.ServiceOnlineReceiver;

/*     */ import java.util.List;
/*     */ 
/*     */ public class PushServiceManager
/*     */ {
/*  39 */   private static final String TAG = LogUtil.makeLogTAG(PushServiceManager.class);
/*     */   public static String packageName;
/*     */   public static String className;
/*     */   public static Class<?> activityClass;
/*     */   public static Context context;
/*     */   private static Context receiver;
/*  56 */   public static LocationUPush lastLocation = new LocationUPush();
/*     */   private static ServiceOnlineReceiver serviceOnlineReceiver;
/*     */   private static InformationManager mmi;
/*  62 */   private static TagManagerImpl tagManagerImpl = null;
/*     */ 
/*  64 */   private static LBSManagerImpl lbsManagerImpl = null;
/*     */ 
/*  66 */   private static boolean isConnected = false;
/*     */ 
/*  68 */   private static SharedPreferences sharedPref = null;
/*     */ 
/*  70 */   private static TelephonyManager telephonyManager = null;
/*     */ 
/*  72 */   private static String device_id = null;
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/*  92 */     if (context == null) {
/*  93 */       if (Constants.isLog()) {
/*  94 */         Log.d(TAG, "参数异常： 初始化时Context值不能为null");
/*     */       }
/*  96 */       return;
/*     */     }
/*  98 */     PushServiceManager.context = context;
/*     */     try
/*     */     {
/* 101 */       ApplicationInfo appInfo = context.getPackageManager()
/* 102 */         .getApplicationInfo(context.getPackageName(), 
/* 103 */         128);
/* 104 */       Constants.setApp_id(appInfo.metaData.getInt("APP_ID"));
/*     */     } catch (PackageManager.NameNotFoundException e) {
/* 106 */       if (Constants.isLog()) {
/* 107 */         Log.d(TAG, "配置文件读取异常：读不到app_id值");
/*     */       }
/* 109 */       return;
/*     */     }
/*     */ 
/* 113 */     packageName = context.getPackageName();
/* 114 */     className = ShowNotification.class.getName();
/* 115 */     activityClass = ShowNotification.class;
/* 116 */     Constants.setSERVICE_ACTION(packageName + ".NOTIFICATION_PUSH_SERVICE");
/* 117 */     telephonyManager = (TelephonyManager)context.getSystemService("phone");
/*     */ 
/* 119 */     sharedPref = context.getSharedPreferences(packageName + "." + 
/* 120 */       Constants.getSharedPreferenceName(), 0);
/*     */   }
/*     */ 
/*     */   public static void Start()
/*     */   {
/* 131 */     Constants.setConnect(true);
/*     */ 
/* 133 */     LocationManager locationManager = 
/* 134 */       (LocationManager)context.getSystemService("location");
/*     */ 
///* 136 */     tagManagerImpl = new TagManagerImpl(context);
///* 137 */     lbsManagerImpl = new LBSManagerImpl(context, locationManager);
/*     */ 
/* 139 */     String pushServiceName = Constants.getServiceName();
/*     */ 
/* 141 */     Boolean exist = Boolean.valueOf(isServiceRunning(context, pushServiceName));
/* 142 */     if (!exist.booleanValue()) {
/* 143 */       Thread serviceThread = new Thread(new Runnable()
/*     */       {
/*     */         public void run() {
/* 146 */           if (Constants.isLog()) {
/* 147 */             Log.d(PushServiceManager.TAG, "服务不存在开始创建服务");
/*     */           }
/* 149 */           Intent intent = new Intent(Constants.getServiceAction());
/* 150 */           PushServiceManager.context.startService(intent);
/*     */         }
/*     */       });
/* 153 */       serviceThread.start();
/*     */     }
/* 155 */     else if (Constants.isLog()) {
/* 156 */       Log.d(TAG, "服务已经存在，不用创建服务");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void Stop()
/*     */   {
/* 169 */     Constants.setConnect(false);
/* 170 */     setConnected(false);
/* 171 */     if (tagManagerImpl != null) {
/* 172 */       tagManagerImpl.clearup();
/* 173 */       tagManagerImpl = null;
/*     */     }
/* 175 */     if (lbsManagerImpl != null) {
/* 176 */       lbsManagerImpl.clearup();
/* 177 */       lbsManagerImpl = null;
/*     */     }
/* 179 */     if (serviceOnlineReceiver != null) {
/* 180 */       receiver.unregisterReceiver(serviceOnlineReceiver);
/* 181 */       serviceOnlineReceiver = null;
/*     */     }
/* 183 */     Intent intent = new Intent(Constants.getServiceAction());
/* 184 */     context.stopService(intent);
/* 185 */     if (Constants.isLog())
/* 186 */       Log.d(TAG, "推送服务结束");
/*     */   }
/*     */ 
/*     */   public static void clearup()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void setAddress(String ip, int port)
/*     */   {
/* 209 */     Constants.setIP(ip);
/* 210 */     Constants.setPORT(port);
/*     */   }
/*     */ 
/*     */   public static void setDebugMode(boolean debug)
/*     */   {
/* 221 */     Constants.setLog(debug);
/*     */   }
/*     */ 
/*     */   public static boolean isConnected()
/*     */   {
/* 232 */     return isConnected;
/*     */   }
/*     */ 
/*     */   public static void setConnected(boolean connected)
/*     */   {
/* 244 */     isConnected = connected;
/*     */   }
/*     */ 
/*     */   public static InformationManager getInformationManager()
/*     */   {
/* 255 */     return mmi;
/*     */   }
/*     */ 
/*     */   public static void setInformationManager(InformationManager mmi)
/*     */   {
/* 266 */     mmi = mmi;
/*     */   }
/*     */ 
/*     */   public static void setNotificationActivity(Class<?> object)
/*     */   {
/*     */     try
/*     */     {
/* 279 */       if (object == null) {
/* 280 */         className = ShowNotification.class.getName();
/* 281 */         activityClass = ShowNotification.class;
/* 282 */       } else if ((object.newInstance() instanceof Activity)) {
/* 283 */         className = object.getName();
/* 284 */         activityClass = object;
/*     */       }
/* 286 */       else if (Constants.isLog()) {
/* 287 */         Log.d(TAG, "不是一个Activity组件无法提供通知推送服务");
/*     */       }
/*     */     }
/*     */     catch (InstantiationException e)
/*     */     {
/* 292 */       Log.d(TAG, e.getMessage());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 295 */       Log.d(TAG, e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void registerServiceOnlineReceiver(Service service)
/*     */   {
/* 305 */     receiver = service;
/* 306 */     serviceOnlineReceiver = new ServiceOnlineReceiver(service);
/* 307 */     IntentFilter filter = new IntentFilter();
/*     */ 
/* 309 */     filter.addAction(Constants.getActionPushServiceOnline());
/*     */ 
/* 311 */     filter.addAction(packageName + Constants.getNotificationAndMessage());
/*     */ 
/* 313 */     filter.addAction(Constants.getActionLbsRevoke() + Constants.getApp_id());
/*     */ 
/* 315 */     filter.addAction(packageName + "." + Constants.getActionConnectState());
/* 316 */     filter.addAction(Constants.getActionConnectState());
/* 317 */     filter.addAction(packageName + "." + Constants.getActionNotifyDelete());
/* 318 */     service.registerReceiver(serviceOnlineReceiver, filter);
/* 319 */     if (Constants.isLog()) {
/* 320 */       Log.d(TAG, "注册服务在线监听接收器");
/*     */     }
/*     */ 
/* 323 */     serviceOnlineReceiver.pushServiceTimeoutHandle();
/*     */   }
/*     */ 
/*     */   public static void unregisterServiceOnlineReceiver(Service service)
/*     */   {
/* 334 */     if (serviceOnlineReceiver != null) {
/* 335 */       service.unregisterReceiver(serviceOnlineReceiver);
/* 336 */       serviceOnlineReceiver = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Object getService(ServiceType type)
/*     */   {
/* 350 */     Object instance = null;
/* 351 */     switch (type) {
/*     */     case LBS_MANAGER:
/* 353 */       instance = tagManagerImpl;
/* 354 */       break;
/*     */     case TAG_MANAGER:
/* 356 */       instance = lbsManagerImpl;
/*     */     }
/*     */ 
/* 359 */     return instance;
/*     */   }
/*     */ 
/*     */   public static ServiceOnlineReceiver getServiceOnlineReceiver()
/*     */   {
/* 370 */     return serviceOnlineReceiver;
/*     */   }
/*     */ 
/*     */   public static SharedPreferences getSharedPreferences()
/*     */   {
/* 382 */     return sharedPref;
/*     */   }
/*     */ 
/*     */   private static boolean isServiceRunning(Context mContext, String className)
/*     */   {
/* 393 */     boolean IsRunning = false;
/* 394 */     if (Constants.isLog()) {
/* 395 */       Log.d(TAG, "className:" + className);
/*     */     }
/* 397 */     ActivityManager activityManager = (ActivityManager)
/* 398 */       mContext.getSystemService("activity");
/* 399 */     List serviceList = 
/* 400 */       activityManager.getRunningServices(2147483647);
/* 401 */     if (serviceList.size() <= 0) {
/* 402 */       return false;
/*     */     }
/* 404 */     for (int i = 0; i < serviceList.size(); i++) {
/* 405 */       if (Constants.isLog()) {
/* 406 */         Log.d(TAG, "isServiceRunning:" + 
/* 407 */           ((ActivityManager.RunningServiceInfo)serviceList.get(i)).service.getClassName());
/*     */       }
/* 409 */       if (((ActivityManager.RunningServiceInfo)serviceList.get(i)).service.getClassName().equals(className)) {
/* 410 */         IsRunning = true;
/* 411 */         break;
/*     */       }
/*     */     }
/* 414 */     return IsRunning;
/*     */   }
/*     */ 
/*     */   public static String getDeviceId()
/*     */   {
/* 509 */     String device_id = null;
/* 510 */     TelephonyManager telephonyManager = null;
/* 511 */     if (context != null) {
/* 512 */       telephonyManager = (TelephonyManager)context.getSystemService("phone");
/*     */     }
/* 514 */     if (telephonyManager != null) {
/* 515 */       device_id = telephonyManager.getDeviceId();
/* 516 */       if (Build.VERSION.SDK_INT > 8) {
/* 517 */         device_id = Build.SERIAL + device_id;
/*     */       }
/*     */     }
/*     */ 
/* 521 */     if ((device_id == null) || (device_id.trim().length() == 0))
/*     */     {
/* 523 */       device_id = "EMU";
/*     */     }
/* 525 */     if (Constants.isLog()) {
/* 526 */       Log.d(TAG, "DEVICE_ID: " + device_id);
/*     */     }
/* 528 */     return device_id;
/*     */   }
/*     */ 
/*     */   public static enum ServiceType
/*     */   {
/*  42 */     TAG_MANAGER, LBS_MANAGER;
/*     */   }
/*     */ 
/*     */   public static class SetNotification
/*     */   {
/*     */     public static void setTicker(String ticker)
/*     */     {
/* 435 */       Constants.Notificatin.setTicker(ticker);
/*     */     }
/*     */ 
/*     */     public static String getTicker()
/*     */     {
/* 447 */       return Constants.Notificatin.getTicker();
/*     */     }
/*     */ 
/*     */     public static void setContentTitle(String contentTitle)
/*     */     {
/* 459 */       Constants.Notificatin.setContentTitle(contentTitle);
/*     */     }
/*     */ 
/*     */     public static String getContentTitle()
/*     */     {
/* 471 */       return Constants.Notificatin.getContentTitle();
/*     */     }
/*     */ 
/*     */     public static void setContentText(String contentText)
/*     */     {
/* 483 */       Constants.Notificatin.setContentText(contentText);
/*     */     }
/*     */ 
/*     */     public static String getContentText()
/*     */     {
/* 495 */       return Constants.Notificatin.getContentText();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\用友推送\UPushV2.0.jar
 * Qualified Name:     com.yonyou.pushclient.PushServiceManager
 * JD-Core Version:    0.6.2
 */