"use strict";
/*const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
  
  //Welcome Notification to User
  exports.WelcomeNotification = functions.firestore.document('/users/{phoneNumber}/tokenId')
  .onCreate(async (msnapshot: { val: () => void; }, context: { params: { phoneNumber: any; }; }) => {
  
      const mPhoneNumber = context.params.phoneNumber
  
      return admin.firestore().document('/users/'+mPhoneNumber)
      .once('value').then((snapshot: { val: { (): void; (): { name: any; }; (): { tokenId: any; }; }; }) => {
    
        const original = snapshot.val();
        console.log('original', original);
    
        const Username = snapshot.val();
        console.log('Username ', Username);
    
        const receiver = snapshot.val();
        console.log('customer', receiver);
    
        const myoptions = {
          priority: "high",
          timeToLive: 60 * 60 * 24
       };
    
        const payload = {
          data: {
            title: 'Hello!',
            body: 'Welcome to TeleTalk, ' + Username,
            click_action: 'WELCOME'
          }
        };
    
        return admin.messaging().sendToDevice(receiver, payload, myoptions);
      })
  
    });
  
  // // Create and Deploy Your First Cloud Functions
  // // https://firebase.google.com/docs/functions/write-firebase-functions
  //
  // exports.helloWorld = functions.https.onRequest((request, response) => {
  //  response.send("Hello from Firebase!");
  // });*/
const functions = require('firebase-functions');
exports.createUser = functions.firestore
    .document('/users/{phoneNumber}/tokenId')
    .onCreate((snap, context) => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    const newValue = snap.data();
    // access a particular field as you would any JS property
    const name = newValue.name;
    // perform desired operations ...
});
//# sourceMappingURL=index.js.map