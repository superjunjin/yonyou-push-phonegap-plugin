
var argscheck = require('cordova/argscheck'),
    cordova = require('cordova'),
    exec = require('cordova/exec');




var FGPushNotification = function() {
	this.registered = false;
	this.title = null;
	this.description = null;
	this.value = null;
	//
	this.appId = null;
    this.channelId = null;
    this.clientId = null;
    
    var me = this;

     me.getInfo(function(info) {
            me.appId = info.appId;
            me.channelId = info.channelId;
            me.clientId = info.clientId;
        });
    
};


FGPushNotification.prototype.openNotificationInAndroidCallback = function(data){
	try{
	    alert("data-------------------------"+data);
		console.log("BPushPlugin:openNotificationInAndroidCallback"); 
		var bToObj  = JSON.parse(data);
		fastgoPushNotification.title = bToObj.title;
		fastgoPushNotification.description = bToObj.description;
		fastgoPushNotification.value = bToObj.value;
		cordova.fireDocumentEvent('upush.openNotification',data);
	
		
	}
	catch(exception){               
		console.log(exception);
	}
}


FGPushNotification.prototype.init = function()
{
    
    exec(fastgoPushNotification.successFn, fastgoPushNotification.failureFn, 'FGPushNotification', 'init', []);

};

FGPushNotification.prototype.successFn = function(info)
{
    
	if(info){
		fastgoPushNotification.registered = true;
		cordova.fireDocumentEvent("cloudPushRegistered", info);
	}
};

FGPushNotification.prototype.failureFn = function(info)
{
    
	fastgoPushNotification.registered = false;
};

FGPushNotification.prototype.getInfo = function(successCallback, errorCallback) {
    
    exec(successCallback, errorCallback, "FGPushNotification", "getInfo", []);
};
var fastgoPushNotification = new FGPushNotification();

module.exports = fastgoPushNotification;


