const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

//Welcome Notification to User
exports.WelcomeNotificaton = functions.firestore
  .document('users/{userId}')
  .onCreate(async (snap, context) => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    const newValue = snap.data();

    // access a particular field as you would any JS property
    const username = newValue.name;
    console.log("username", username);

    const receiver = newValue.tokenId;
    console.log("receiver", receiver);

    const myoptions = {
      priority: "high",
      timeToLive: 60 * 60 * 24
    };

    const payload = {
      data: {
        title: 'Hello!',
        body: 'Welcome to TeleTalk, ' + username,
        click_action: 'WELCOME'
      }
    };

    const result = await admin.messaging().sendToDevice(receiver, payload);
    console.log("notification sent", result);
    console.log(result.results[0].error);
  });

//Message Alert
exports.MessageAlert = functions.firestore
  .document('chats/{chatId}/messages/{message}')
  .onCreate(async (snap, context) => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    const newValue = snap.data();

    // access a particular field as you would any JS property
    const sender = newValue.senderName;
    console.log("username", sender);

    const receiver = newValue.tokenReceiver;
    console.log("receiver", receiver);

    const message = newValue.textMessage;
    console.log("message", message);

    const myoptions = {
      priority: "high",
      timeToLive: 60 * 60 * 24
    };

    const payload = {
      data: {
        title: sender,
        body: message,
        click_action: 'CHAT'
      }
    };

    const result = await admin.messaging().sendToDevice(receiver, payload);
    console.log("notification sent", result);
    console.log(result.results[0].error);
  });

exports.GroupMessageAlert = functions.firestore
  .document('users/{phone}/ChatRooms/{chatroom}')
  .onUpdate(async (snap, context) => {

    const seenBefore = BigInteger(snap.before.data().lastSeen);
    const seenAfter = BigInteger(snap.after.data().lastSeen);

    const message = snap.after.data().lastMessage;
    console.log("LastMessage: ", message);

    const sender = message.idSender;
    console.log("sender: ", sender);

    const room = context.params.chatroom;
    console.log("chatroom", room);

    const phoneNumber = context.params.phone;
    console.log("phoneNumber", phoneNumber);

    const payload = {
      data: {
        title: 'New Message',
        body: 'You have a new message from chatroom: ' + room,
        click_action: 'GROUP'
      }
    };

    if (sender !== phoneNumber && !(seenAfter>seenBefore)) {
      const snapshot = await admin.firestore().collection("users").doc(phoneNumber).get();
      const receiver = snapshot.data().tokenId;
      console.log('receiver', receiver);
      try {
        const response = await admin.messaging().sendToDevice(receiver, payload);
        console.info("Successfully sent notification of start session" + response.results[0].error);
        return 0;
      }
      catch (error) {
        console.warn("Error sending notification of start session: ", error[0]);
      }
    }

  });