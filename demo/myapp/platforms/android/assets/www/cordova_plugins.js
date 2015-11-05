cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/com.yyuap.upush.plugin.pushnotification/www/PushNotification.js",
        "id": "com.yyuap.upush.plugin.pushnotification.FGPushNotification",
        "clobbers": [
            "fastgoPushNotification"
        ]
    },
    {
        "file": "plugins/cordova-plugin-whitelist/whitelist.js",
        "id": "cordova-plugin-whitelist.whitelist",
        "runs": true
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.yyuap.upush.plugin.pushnotification": "1.0.0",
    "cordova-plugin-whitelist": "1.0.0"
}
// BOTTOM OF METADATA
});