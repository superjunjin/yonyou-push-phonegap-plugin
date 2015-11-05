/*     */ package com.example.myuapp;
/*     */ 
/*     */ import android.annotation.SuppressLint;
/*     */ import android.app.ActivityManager;
/*     */ import android.app.ActivityManager.RunningServiceInfo;
/*     */ import android.app.Notification;
/*     */ import android.app.Notification.Builder;
/*     */ import android.app.NotificationManager;
/*     */ import android.app.PendingIntent;
/*     */ import android.app.Service;
/*     */ import android.content.BroadcastReceiver;
/*     */ import android.content.ComponentName;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.text.Html;
/*     */ import android.util.Log;
/*     */ import android.widget.Toast;

/*     */ import com.yonyou.protocol.AppInfo;
/*     */ import com.yonyou.protocol.Constants;
/*     */ import com.yonyou.protocol.Constants.Notificatin;
/*     */ import com.yonyou.protocol.ExistMsgQueue;
/*     */ import com.yonyou.protocol.LogUtil;
/*     */ import com.yonyou.protocol.NotifyAndMsg;
/*     */ import com.yonyou.protocol.PushServiceOnlinePackage;

/*     */ import com.yonyou.upush.interf.InformationManager;

/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServiceOnlineReceiver extends BroadcastReceiver
/*     */ {
/*  44 */   private static final String TAG = LogUtil.makeLogTAG(ServiceOnlineReceiver.class);
/*     */   private static HashMap<String, Long> onlineServiceMap;
/*     */   private static final long HEARTTIMEOUT = 18000L;
/*     */   private Service service;
/*     */   private NotificationManager nm;
/*     */   private ArrayList<Integer> app_list;
/*     */   private static HashMap<Integer, NotifyAndMsg> notify_List;
/*  58 */   private static SharedPreferences sharedPref = null;
/*     */   private static final String MSG_TAG = "msg_detail_tag";
/*  62 */   private ExistMsgQueue<String> existMsgQueues = null;
/*     */ 
/*  64 */   public static ExistMsgQueue<String> currentMsgQueue = null;
/*     */ 
/*     */   @SuppressLint({"UseSparseArrays"})
/*     */   public ServiceOnlineReceiver(Service service)
/*     */   {
/*  77 */     this.service = service;
/*     */ 
/*  79 */     this.nm = ((NotificationManager)service.getSystemService("notification"));
/*     */ 
/*  81 */     onlineServiceMap = new HashMap();
/*  82 */     this.app_list = new ArrayList();
/*  83 */     notify_List = new HashMap();
/*  84 */     this.existMsgQueues = new ExistMsgQueue();
/*  85 */     currentMsgQueue = new ExistMsgQueue();
/*  86 */     sharedPref = PushServiceManager.getSharedPreferences();
/*  87 */     Set detailIdSet = null;
/*  88 */     detailIdSet = sharedPref.getStringSet("msg_detail_tag", detailIdSet);
/*  89 */     if (detailIdSet != null)
/*  90 */       this.existMsgQueues.putAll(detailIdSet);
/*     */   }
/*     */ 
/*     */   public void onReceive(Context onecontext, Intent intent)
/*     */   {
/*  97 */     String action = intent.getAction();
/*     */ 
/*  99 */     String notfAndMsgAction = this.service.getPackageName() + 
/* 100 */       Constants.getNotificationAndMessage();
/*     */ 
/* 102 */     String serviceOnlineAction = Constants.getActionPushServiceOnline();
/*     */ 
/* 104 */     String lbsRevoke = Constants.getActionLbsRevoke() + Constants.getApp_id();
/*     */ 
/* 106 */     String connectState1 = Constants.getActionConnectState();
/* 107 */     String connectState2 = PushServiceManager.packageName + 
/* 108 */       "." + Constants.getActionConnectState();
/*     */ 
/* 110 */     String notifyDelete = PushServiceManager.packageName + 
/* 111 */       "." + Constants.getActionNotifyDelete();
/* 112 */     if ((action != null) && (notfAndMsgAction.equals(action))) {
/* 113 */       if (Constants.isLog()) {
/* 114 */         Log.d(TAG, "收到通知或消息包");
/*     */       }
/* 116 */       NotifyAndMsg notifyAndMsg = (NotifyAndMsg)intent
/* 117 */         .getSerializableExtra(Constants.getExtraKeyNotifyAndMsg());
/* 118 */       String detailId = Integer.toString(notifyAndMsg.getMsgDetailId());
/* 119 */       if (this.existMsgQueues.contains(detailId))
/*     */       {
/* 121 */         return;
/*     */       }
/*     */ 
/* 125 */       this.existMsgQueues.put(detailId);
/* 126 */       Set existMsgSet = this.existMsgQueues.elementSet();
/* 127 */       SharedPreferences.Editor editor = sharedPref.edit();
/* 128 */       editor.putStringSet("msg_detail_tag", existMsgSet);
/* 129 */       editor.commit();
/*     */ 
/* 131 */       if ((notifyAndMsg != null) && (1 == notifyAndMsg.getType()))
/*     */       {
/* 134 */         if (Constants.isLog()) {
/* 135 */           Log.d(TAG, "收到通知…………………………………notification…………………………………………………");
/* 136 */           Log.d(TAG, "activity object: " + PushServiceManager.activityClass.toString());
/* 137 */           Log.d(TAG, notifyAndMsg.toString());
/*     */         }
/* 139 */         if (Constants.isDenyNotify()) {
/* 140 */           return;
/*     */         }
/* 142 */         if (notify_List.size() == 50) {
/* 143 */           int deleteId = Integer.parseInt((String)currentMsgQueue.poll());
/* 144 */           this.nm.cancel(deleteId);
/* 145 */           notify_List.remove(Integer.valueOf(deleteId));
/*     */         }
/* 147 */         notify_List.put(Integer.valueOf(notifyAndMsg.getMsgDetailId()), notifyAndMsg);
/* 148 */         currentMsgQueue.put(Integer.toString(notifyAndMsg.getMsgDetailId()));
/* 149 */         pushNotification(this.service, PushServiceManager.activityClass, this.nm, notifyAndMsg);
/* 150 */         if (PushServiceManager.getInformationManager() != null)
/* 151 */           PushServiceManager.getInformationManager().dealWithNotification(notifyAndMsg);
/*     */       }
/* 153 */       else if ((notifyAndMsg != null) && (notifyAndMsg.getType() == 0))
/*     */       {
/* 156 */         if (Constants.isLog()) {
/* 157 */           Log.d(TAG, "收到消息……………………………………………message…………………………………………………");
/* 158 */           Log.d(TAG, notifyAndMsg.toString());
/*     */         }
/* 160 */         if (PushServiceManager.getInformationManager() != null) {
/* 161 */           PushServiceManager.getInformationManager().dealWithMessage(notifyAndMsg);
/*     */         }
/* 163 */         else if (Constants.isLog()) {
/* 164 */           Log.d(TAG, "缺少消息处理器： InformationManager");
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 169 */     else if ((action != null) && (serviceOnlineAction.equals(action))) {
/* 170 */       if (Constants.isLog()) {
/* 171 */         Log.d(TAG, "收到推送服务在线心跳包");
/*     */       }
/*     */ 
/* 174 */       PushServiceOnlinePackage onlinePackage = (PushServiceOnlinePackage)intent
/* 175 */         .getSerializableExtra(Constants.getExtraKeyPushServiceOnline());
/*     */ 
/* 177 */       this.app_list = onlinePackage.getAppOnlineList();
/*     */ 
/* 179 */       String pushServicePackageName = onlinePackage.getPackageName();
/* 180 */       if (Constants.isLog()) {
/* 181 */         Log.d(TAG, "当前服务提供者包名：~~~ " + onlinePackage.getPackageName() + "~~~!");
/* 182 */         Log.d(TAG, "当前存在app列表：" + this.app_list.toString());
/*     */       }
/*     */ 
/* 185 */       if (onlineServiceMap.isEmpty())
/*     */       {
/* 187 */         onlineServiceMap.put(pushServicePackageName, Long.valueOf(System.currentTimeMillis()));
/* 188 */       } else if ((1 == onlineServiceMap.size()) && 
/* 189 */         (onlineServiceMap.containsKey(pushServicePackageName)))
/*     */       {
/* 191 */         onlineServiceMap.put(pushServicePackageName, Long.valueOf(System.currentTimeMillis()));
/* 192 */       } else if ((1 == onlineServiceMap.size()) && 
/* 193 */         (!onlineServiceMap.containsKey(pushServicePackageName)))
/*     */       {
/* 195 */         onlineServiceMap.clear();
/* 196 */         onlineServiceMap.put(pushServicePackageName, Long.valueOf(System.currentTimeMillis()));
/*     */       }
/*     */ 
/* 199 */       responseOrRegisterService();
/* 200 */     } else if ((action != null) && (lbsRevoke.equals(action)))
/*     */     {
/* 202 */       if (Constants.isLog()) {
/* 203 */         Log.d(TAG, "~~~~~~~~~~~~~~~~~~接收到LBS撤销反馈广播~~~~~~~~~~~~~~~~~~");
/*     */       }
/* 205 */       com.yonyou.upush.impl.LBSManagerImpl.closeLBS = true;
/* 206 */     } else if ((action != null) && ((connectState1.equals(action)) || (connectState2.equals(action)))) {
/* 207 */       boolean state = false;
/* 208 */       state = intent.getBooleanExtra(Constants.getExtraKeyConnectState(), false);
/* 209 */       if (state != PushServiceManager.isConnected()) {
/* 210 */         PushServiceManager.setConnected(state);
/* 211 */         if (Constants.isLog()) {
/* 212 */           Log.d(TAG, "~~~链接服务器地址：" + Constants.getIP() + 
/* 213 */             ":" + Constants.getPORT() + " ~~~ 当前连接状态： " + 
/* 214 */             PushServiceManager.isConnected());
/* 215 */           Toast.makeText(PushServiceManager.context, "~~~链接服务器地址：" + 
/* 216 */             Constants.getIP() + ":" + Constants.getPORT() + " ~~~ 当前连接状态： " + 
/* 217 */             PushServiceManager.isConnected(), 1).show();
/*     */         }
/*     */       }
/* 220 */     } else if ((action != null) && (notifyDelete.equals(action))) {
/* 221 */       int detailId = intent.getIntExtra(Constants.getExtraKeyNotifyDelete(), 0);
/* 222 */       if ((detailId != 0) && (notify_List.containsKey(Integer.valueOf(detailId)))) {
/* 223 */         notify_List.remove(Integer.valueOf(detailId));
/* 224 */         currentMsgQueue.remove(Integer.toString(detailId));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void responseOrRegisterService()
/*     */   {
/* 243 */     Intent intent = new Intent(Constants.getActionAppOnline());
/* 244 */     AppInfo appInfo = new AppInfo();
/* 245 */     appInfo.setPackage_name(PushServiceManager.packageName);
/* 246 */     appInfo.setActivity_class_name(PushServiceManager.className);
/* 247 */     appInfo.setApp_id(Constants.getApp_id());
/* 248 */     intent.putExtra(Constants.getExtraKeyAppOnline(), appInfo);
/* 249 */     this.service.sendBroadcast(intent);
/* 250 */     if (Constants.isLog())
/* 251 */       Log.d(TAG, "app在线广播已发送");
/*     */   }
/*     */ 
/*     */   public void pushServiceTimeoutHandle()
/*     */   {
/* 261 */     Thread pushServiceTimeouThread = new Thread(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 265 */         while (Constants.isConnect()) {
/* 266 */           if (!ServiceOnlineReceiver.onlineServiceMap.isEmpty()) {
/* 267 */             for (String name : ServiceOnlineReceiver.onlineServiceMap.keySet())
/*     */             {
/* 269 */               if (System.currentTimeMillis() - 
/* 269 */                 ((Long)ServiceOnlineReceiver.onlineServiceMap.get(name)).longValue() > 18000L)
/*     */               {
/* 271 */                 if (ServiceOnlineReceiver.this.app_list.size() < 2)
/*     */                 {
/* 273 */                   ServiceOnlineReceiver.this.startPushService();
/* 274 */                   if (Constants.isLog()) {
/* 275 */                     Log.d(ServiceOnlineReceiver.TAG, "推送服务心跳超时，开始创建新推送服务");
/*     */                   }
/*     */ 
/*     */                 }
/* 279 */                 else if (Constants.getApp_id() == ((Integer)ServiceOnlineReceiver.this.app_list.get(1)).intValue())
/*     */                 {
/* 281 */                   ServiceOnlineReceiver.this.startPushService();
/* 282 */                   if (Constants.isLog())
/* 283 */                     Log.d(ServiceOnlineReceiver.TAG, "推送服务心跳超时，开始创建新推送服务");
/*     */                 }
/*     */                 else
/*     */                 {
/* 287 */                   ServiceOnlineReceiver.this.app_list.remove(1);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/*     */           try
/*     */           {
/* 295 */             Thread.sleep(18000L);
/*     */           }
/*     */           catch (InterruptedException e) {
/* 298 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 304 */     pushServiceTimeouThread.start();
/*     */   }
/*     */ 
/*     */   private boolean isServiceRunning(Context mContext, String className)
/*     */   {
/* 316 */     boolean IsRunning = false;
/* 317 */     if (Constants.isLog()) {
/* 318 */       Log.d(TAG, "className:" + className);
/*     */     }
/* 320 */     ActivityManager activityManager = (ActivityManager)mContext.getSystemService("activity");
/* 321 */     List serviceList = activityManager.getRunningServices(2147483647);
/* 322 */     if (serviceList.size() <= 0) {
/* 323 */       return false;
/*     */     }
/* 325 */     for (int i = 0; i < serviceList.size(); i++) {
/* 326 */       if (Constants.isLog()) {
/* 327 */         Log.d(TAG, "isServiceRunning:" + ((ActivityManager.RunningServiceInfo)serviceList.get(i)).service.getClassName());
/*     */       }
/* 329 */       if (((ActivityManager.RunningServiceInfo)serviceList.get(i)).service.getClassName().equals(className)) {
/* 330 */         IsRunning = true;
/* 331 */         break;
/*     */       }
/*     */     }
/* 334 */     return IsRunning;
/*     */   }
/*     */ 
/*     */   public void startPushService()
/*     */   {
/* 344 */     String pushServiceName = Constants.getServiceName();
/*     */ 
/* 346 */     Boolean exist = Boolean.valueOf(isServiceRunning(this.service, pushServiceName));
/* 347 */     if (!exist.booleanValue()) {
/* 348 */       Thread serviceThread = new Thread(new Runnable()
/*     */       {
/*     */         public void run() {
/* 351 */           if (Constants.isLog()) {
/* 352 */             Log.d(ServiceOnlineReceiver.TAG, "服务不存在开始创建服务");
/*     */           }
/* 354 */           Intent intent = new Intent(Constants.getServiceAction());
/* 355 */           ServiceOnlineReceiver.this.service.startService(intent);
/*     */         }
/*     */       });
/* 358 */       serviceThread.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   @SuppressLint({"NewApi"})
/*     */   private void pushNotification(Context pushContext, Class<?> activityClass, NotificationManager nm, NotifyAndMsg notifyAndMsg)
/*     */   {
///* 367 */     Intent intent = new Intent(pushContext, activityClass);
///* 368 */     intent.setFlags(335544320);
///* 369 */     intent.putExtra("msg_Id", notifyAndMsg.getMsgDetailId());
///* 370 */     PendingIntent pi = PendingIntent.getActivity(
///* 371 */       pushContext, notifyAndMsg.getMsgDetailId(), intent, 134217728);
	
	/* 367 */     Intent intent = new Intent();
	/* 368 */     intent.setAction("com.example.myuapp.action.notification.CLICK");
	/* 369 */     intent.putExtra("msg_Id", notifyAndMsg.getMsgDetailId());
	/* 370 */     PendingIntent pi = PendingIntent.getBroadcast(
	/* 371 */       pushContext, notifyAndMsg.getMsgDetailId(), intent, 134217728);
/*     */ 
/* 373 */     Intent deleteIntent = new Intent(PushServiceManager.packageName + "." + Constants.getActionNotifyDelete());
/* 374 */     deleteIntent.putExtra(Constants.getExtraKeyNotifyDelete(), notifyAndMsg.getMsgDetailId());
/* 375 */     PendingIntent deletePi = PendingIntent.getBroadcast(
/* 376 */       pushContext, notifyAndMsg.getMsgDetailId(), deleteIntent, 134217728);
/*     */ 
/* 378 */     ApplicationInfo ai = pushContext.getApplicationInfo();
/* 379 */     Resources resources = pushContext.getResources();
/* 380 */     CharSequence tickertext = null;
/* 381 */     CharSequence contenttext = null;
/* 382 */     CharSequence contentTitle = null;
/*     */ 
/* 384 */     if ((Constants.Notificatin.getTicker() == null) || 
/* 385 */       ("notification_title".equals(Constants.Notificatin.getTicker())))
/* 386 */       tickertext = Html.fromHtml(notifyAndMsg.getSubject());
/* 387 */     else if ("notification_content".equals(Constants.Notificatin.getTicker()))
/* 388 */       tickertext = Html.fromHtml(notifyAndMsg.getContent());
/* 389 */     else if ("app_label".equals(Constants.Notificatin.getTicker()))
/* 390 */       tickertext = resources.getString(ai.labelRes);
/*     */     else {
/* 392 */       tickertext = Html.fromHtml(Constants.Notificatin.getTicker());
/*     */     }
/*     */ 
/* 395 */     if ((Constants.Notificatin.getContentText() == null) || 
/* 396 */       ("notification_content".equals(Constants.Notificatin.getContentText())))
/* 397 */       contenttext = Html.fromHtml(notifyAndMsg.getContent());
/* 398 */     else if ("notification_title".equals(Constants.Notificatin.getContentText()))
/* 399 */       contenttext = Html.fromHtml(notifyAndMsg.getSubject());
/* 400 */     else if ("app_label".equals(Constants.Notificatin.getContentText()))
/* 401 */       contenttext = resources.getString(ai.labelRes);
/*     */     else {
/* 403 */       contenttext = Html.fromHtml(Constants.Notificatin.getContentText());
/*     */     }
/*     */ 
/* 406 */     if ((Constants.Notificatin.getContentTitle() == null) || 
/* 407 */       ("app_label".equals(Constants.Notificatin.getContentTitle())))
/* 408 */       contentTitle = resources.getString(ai.labelRes);
/* 409 */     else if ("notification_title".equals(Constants.Notificatin.getContentTitle()))
/* 410 */       contentTitle = Html.fromHtml(notifyAndMsg.getSubject());
/* 411 */     else if ("notification_content".equals(Constants.Notificatin.getContentTitle()))
/* 412 */       contentTitle = Html.fromHtml(notifyAndMsg.getContent());
/*     */     else {
/* 414 */       contentTitle = Html.fromHtml(Constants.Notificatin.getContentTitle());
/*     */     }
/* 416 */     Notification notify = new Notification.Builder(pushContext)
/* 417 */       .setAutoCancel(true)
/* 419 */       .setTicker(tickertext)
/* 421 */       .setSmallIcon(ai.icon)
/* 423 */       .setLargeIcon(BitmapFactory.decodeResource(resources, ai.icon))
/* 425 */       .setContentTitle(contentTitle)
/* 427 */       .setContentText(contenttext)
/* 429 */       .setDefaults(1)
/* 431 */       .setWhen(System.currentTimeMillis())
/* 433 */       .setDeleteIntent(deletePi)
/* 435 */       .setContentIntent(pi)
/* 436 */       .build();
/*     */ 
/* 438 */     nm.notify(notifyAndMsg.getMsgDetailId(), notify);
/* 439 */     if (Constants.isLog())
/* 440 */       Log.d(TAG, "当前通知: ID=" + Constants.getApp_id() + 
/* 441 */         " info=" + notifyAndMsg.getContent() + 
/* 442 */         " extra=" + notifyAndMsg.getExtratext());
/*     */   }
/*     */ 
/*     */   public NotifyAndMsg getNotifyById(int msg_id)
/*     */   {
/* 458 */     NotifyAndMsg notify = (NotifyAndMsg)notify_List.get(Integer.valueOf(msg_id));
/* 459 */     return notify;
/*     */   }
/*     */ 
/*     */   public void removeNotifyById(int msg_id)
/*     */   {
/* 471 */     notify_List.remove(Integer.valueOf(msg_id));
/*     */   }
/*     */ 
/*     */   public Service getService()
/*     */   {
/* 482 */     return this.service;
/*     */   }
/*     */ }

/* Location:           D:\用友推送\UPushV2.0.jar
 * Qualified Name:     com.yonyou.pushclient.ServiceOnlineReceiver
 * JD-Core Version:    0.6.2
 */