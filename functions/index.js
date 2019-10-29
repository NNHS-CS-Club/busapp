const functions = require('firebase-functions');
const admin = require("firebase-admin");
admin.initializeApp();

exports.pushNotifications = functions.database.ref('/buses/').onUpdate((change, context) => {
    var after = change.after.val();
    var topic = 'pushNotifications';
    var jsonData = {};



    for (var key in after) {
        if (after.hasOwnProperty(key)) {
            var busNumber = "";
            var busChange = "";
            var status = "";
            if (after[key].hasOwnProperty("Bus")) {
                busNumber = String(after[key]["Bus"]);
            }
            if (after[key].hasOwnProperty("Change")) {
                busChange = String(after[key]["Change"]);
            }
            if (after[key].hasOwnProperty("Status")) {
                status = String(after[key]["Status"]);
            }

            var bus = "";
            if (busChange === "") {
                bus = busNumber;
            } else {
                bus = busNumber + "=" + busChange;
            }
            jsonData[bus] = status;
        }
    }

    var payload = {
        data: jsonData,
        topic: 'pushNotifications'
    };

    admin.messaging().send(payload).then(() => console.log("Sent Data")).catch((error) => console.log("Error"));
    return 0;
});