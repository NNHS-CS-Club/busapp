const functions = require('firebase-functions');
const admin = require("firebase-admin");
admin.initializeApp();

exports.pushNotifications = functions.database.ref('/buses/').onUpdate((change, context) => {
    var before = change.before.val();
    var after = change.after.val();
    var messages = [];

    for (var i = 0; i < after.length; i++) {
        var bus = String(after[i]["Bus"]);
        var busChange = String(after[i]["Change"]);
        var status = String(after[i]["Status"]);
        var previousChange = String(before[i]["Change"]);
        var previousStatus = String(before[i]["Status"]);
        
        var message = {
            "notification": {
                "title": "",
                "body": ""
            },
            "android": {
                "notification": {
                    "icon": "ic_user_bus",
                    "tag": "",
                    "channel_id": ""
                }
            },
            "apns": {
                "headers": {
                    "apns-collapse-id": ""
                }
            },
            "topic": bus
        }

        if (busChange === "") {
            if (status !== "NOT HERE" && status !== previousStatus) {
                message["notification"]["title"] = "Status Change"
                message["notification"]["body"] = "Bus " + bus + " is " + status
                message["android"]["notification"]["tag"] = "com.csclub.busapp.0"
                message["android"]["notification"]["channel_id"] = "0"
                message["apns"]["headers"]["apns-collapse-id"] = "0"
                messages.push(message);
            }
        } else {
            if (busChange !== previousChange) {
                message["notification"]["title"] = "Bus Change"
                message["notification"]["body"] = "Bus " + bus + " is now Bus " + busChange
                message["android"]["notification"]["tag"] = "com.csclub.busapp.1"
                message["android"]["notification"]["channel_id"] = "1"
                message["apns"]["headers"]["apns-collapse-id"] = "1"
                messages.push(message);
            }
            if (status !== "NOT HERE" && status !== previousStatus) {
                message["notification"]["title"] = "Status Change"
                message["notification"]["body"] = "Bus " + bus + " (" + busChange + ") is " + status
                message["android"]["notification"]["tag"] = "com.csclub.busapp.0"
                message["android"]["notification"]["channel_id"] = "0"
                message["apns"]["headers"]["apns-collapse-id"] = "0"
                messages.push(message);
            }
        }
    }

    if (messages.length !== 0) {
        return admin.messaging().sendAll(messages);
    }
    return 0;
});