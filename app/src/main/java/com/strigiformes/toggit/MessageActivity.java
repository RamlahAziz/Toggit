package com.strigiformes.toggit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.strigiformes.toggit.ConversationThreads.HomeActivity;
import com.strigiformes.toggit.CustomObjects.ChatListItem;
import com.strigiformes.toggit.CustomObjects.Message;
import com.strigiformes.toggit.CustomObjects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity  {

    //private static final int ACTIVITY_NUM = 1;
    private String TAG = "MessageListActivity";
    private static final int GET_FROM_PHONE = 1;

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listenerRegistration;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button mSendButton;
    private ImageButton mAttachButton;
    private EditText mTextbox;
    private View mNoMessageLayout;

    private List<Message> messageList = new ArrayList<>();
    private ChatListItem chat;
    private String mFileName;
    private String receiver;

    //For Group chat rooms
    List<User> groupList;
    private String groupName;
    Boolean groupChat = false;
    Boolean home= false;

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     *
     * Go to chatsList (HomeActivity) on leaving chat
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //update lastseen of the chat
        Map<String, Object> data = new HashMap<>();
        data.put("lastSeen", System.currentTimeMillis());

        String type;
        if(groupChat){
            type = "ChatRooms";
        } else {
            type = "userchats";
        }

        db.collection("users").document(user.getPhoneNumber())
                .collection(type).document(chat.getChatId())
                .set(data, SetOptions.merge());

        startActivity(new Intent(MessageActivity.this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mNoMessageLayout = findViewById(R.id.noMessageLayout);

        Intent i = getIntent();
        chat = (ChatListItem) i.getSerializableExtra("CHAT");
        home = Objects.requireNonNull(getIntent().getExtras()).getBoolean("HOME");

        if(getIntent().getExtras().getSerializable("GROUP_CHAT") != null){

            groupChat = (Boolean) getIntent().getExtras().getSerializable("GROUP_CHAT");
            groupName = (String) getIntent().getExtras().getSerializable("GROUP_NAME");

            //if group chat is opened from create group
            groupList = (List<User>) getIntent().getExtras().getSerializable("CONTACTS");

            //if group chat is opened form home page
            if(groupList == null){
                groupList = new ArrayList<User>();
                db.collection("ChatRooms").document(groupName).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot document = task.getResult();
                                    Map<String, Object> map = document.getData();

                                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                                        ArrayList<Map<String, Object>> arrayList = (ArrayList<Map<String, Object>>) entry.getValue();

                                        for (Map<String, Object> userEntry : arrayList) {
                                            final ObjectMapper mapper = new ObjectMapper();
                                            final User groupMember = mapper.convertValue(userEntry, User.class);
                                            groupList.add(groupMember);
                                        }
                                    }
                                    Log.d(TAG, "groupList final: "+groupList);
                                }
                            }
                        });
            }
        }

        if(home){
            if (!groupChat) {
                if (chat.getFromPhone().equals(user.getPhoneNumber())){
                    receiver = chat.getToPhone();
                }else{
                    receiver = chat.getFromPhone();
                }
            }
        } else {
            if (!groupChat) {
                receiver = chat.getToPhone();
            }
        }

        assert getSupportActionBar() != null;   //null check
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        if(groupChat){
            setTitle(groupName);

        } else{
            setTitle(chat.getName());
        }

        user = mauth.getCurrentUser();

        mMessageRecycler =  findViewById(R.id.reyclerview_message_list);
        /*
         * Add or Update when chat was last opened
         * so number of unread messages can be tracked
         * */

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mAttachButton = findViewById(R.id.attach_button);
        mSendButton =  findViewById(R.id.button_chatbox_send);
        mTextbox =  findViewById(R.id.edittext_chatbox);

        mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calls gallery
                Intent addFile = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                addFile.addCategory(Intent.CATEGORY_OPENABLE);
                //addFile.setType("image/jpeg,image/png,image/jpg");
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //    addFile.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                //}
                startActivityForResult(addFile, GET_FROM_PHONE);
            }
        });

        mMessageAdapter = new MessageListAdapter(MessageActivity.this, messageList);

        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setHasFixedSize(true);
        final LinearLayoutManager mManager = new
                LinearLayoutManager(MessageActivity.this, RecyclerView.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(mManager);
        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMessageRecycler.smoothScrollToPosition(
                                    mMessageRecycler.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
        noMessagesLayout();

        if(groupChat){
            sendGroupMessage(mSendButton);
            retrieveGroupMessages();
        }
        else{
            sendMessage(mSendButton);
            retrieveChatMessages();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_PHONE && resultCode == Activity.RESULT_OK) {
            Uri selectedFile = null;

            //new RequestOptions();
            //Glide.with(ProfileDisplay.this)
            //        .load(selectedFile)
            //        .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_avatar)).into(mIDImage);
            //String imageid;

            String fileId;
            if(data != null)
            {
                String size = null;
                selectedFile =data.getData();
                Log.i(TAG, "Uri: " + selectedFile.toString());
                size = getFileSize(selectedFile);


                //Get popup dialog ready to confirm if user want to send file
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Send \""+ mFileName+"\" to \""+chat.getName()+"\"?");
                builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                if (!size.equalsIgnoreCase("unknown")){
                    if(Integer.valueOf(size)<20971520){

                        //WRITE CODE TO show file HERE
                        Log.d(TAG, "I'm less than 20 mb");

                        //show the dialog
                        builder.show();

                        //uploading stuff HERE MARIA :D WE ALL LOVE YOU
                        //https://firebase.google.com/docs/storage/android/upload-files
                        if(selectedFile != null)
                        {

                            if (groupChat) {
                                fileId="ChatRooms/"+ groupName +"/"+ UUID.randomUUID().toString();
                                Log.d("imagelink",fileId);
                            } else {
                                fileId="Chats/"+ chat.getChatId() +"/"+ UUID.randomUUID().toString();
                                Log.d("imagelink",fileId);
                            }

                            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileId);
                            ref.putFile(selectedFile)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d("Driver Upload", "onSuccess: uri= "+ uri.toString());

                                                    Message message = new Message();
                                                    /*
                                                    * TODO RAMLAH this text message needs to be a clickable
                                                    *  widget that opens the file in the required reader
                                                    * instead of a textview
                                                    * */

                                                    message.setTextMessage(mFileName);
                                                    message.setFile(true);
                                                    Log.d("ImageUriActivity",uri.toString());

                                                    message.setFileLoction(uri.toString());

                                                    makeMessage(message);

                                                }
                                            });
                                            Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();

                                            while(!urlTask.isSuccessful()){

                                            }

                                        }
                                    })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    })

                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        }
                                    });
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Choose a file less than 20MB", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "This file is not supported, sorry", Toast.LENGTH_SHORT)
                            .show();

                }

            }

        }

    }

    public String getFileSize(Uri uri) {

        String size = null;
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                mFileName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + mFileName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return size;
    }

    /*
    * Requires: Message object with the content field already set
    * function: sets the remaining fields for the message object
    *           adds the message to the thread in the database
    *           call addToUserDocument()
    * */
    private void makeMessage(Message m){

        final Message message = m;
        //database call needed to get the receiver's token id so notification can be sent
        db.collection("users").document(chat.getToPhone()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot receiverDoc = task.getResult();
                            message.setTokenReceiver(receiverDoc.getData().get("tokenId").toString());
                            message.setIdSender(user.getPhoneNumber());
                            message.setIdReceiver(receiver);
                            message.setTimestamp(System.currentTimeMillis());
                            message.setReceiverName(chat.getName());
                        }

                        db.collection("users").document(user.getPhoneNumber()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot senderDoc = task.getResult();
                                            message.setSenderName(senderDoc.getData().get("name").toString());

                                            /*
                                             * the id generated from add does not automatically
                                             * have a timestamp as in push() from realtime database
                                             */
                                            db.collection("chats").document(chatId(user.getPhoneNumber(), receiver))
                                                    .collection("messages")
                                                    .add(message);


                                            addToUserDocument(message);
                                        }
                                    }
                                });
                    }
                });
    }

    /*
    * requires: send button id
    * functions: checks that input message is not empty
    *           and calls makeMessage()
    * */
    private void sendMessage(Button button){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Message message = new Message();
                message.setTextMessage(mTextbox.getText().toString().trim());
                mTextbox.setText("");

                if (message.getTextMessage().length() > 0) {

                    makeMessage(message);

                }
            }
        });
    }

    /*
    * creates chat id for one-on-one chats by comparing the phone numbers
    * of the sender and the receiver
    * */
    public String chatId(String sender, String receiver) {

        String chatId;

        /*
        * The result is positive
        * if the first string is lexicographically greater than the second string
        * else the result would be negative
        * */
        if(sender.compareTo(receiver) > 0 ){
            chatId = sender+receiver;
        }else {
            chatId = receiver+sender;
        }

        return chatId;
    }

    /*
    * Requires: QuerySnapshot
    * Function: transforms documents into message object and adds them to the message list
    * */
    private void retrieveMessages(QuerySnapshot queryDocumentSnapshots){
        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    Log.d(TAG, "New message: " + dc.getDocument().getData());
                    Map<String, Object> doc =dc.getDocument().getData();
                    Message message = new Message();
                    message.setSenderName(Objects.requireNonNull(doc.get("senderName")).toString());
                    message.setIdSender(Objects.requireNonNull(doc.get("idSender")).toString());
                    message.setTimestamp((Long) doc.get("timestamp"));
                    if ((Boolean) Objects.requireNonNull(doc.get("file"))) {
                        message.setFile(true);
                        message.setFileLoction(doc.get("fileLoction").toString());
                        message.setTextMessage(doc.get("textMessage").toString());

                    } else {
                        if (groupChat) {
                            message.setTextMessage(message.getSenderName()+": "+
                                    Objects.requireNonNull(doc.get("textMessage")).toString());
                        } else {
                            message.setTextMessage(Objects.requireNonNull(doc.get("textMessage")).toString());
                        }
                    }
                    messageList.add(message);
                    mMessageAdapter.notifyDataSetChanged();
                    noMessagesLayout();
                    break;
                case MODIFIED:
                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                    break;
                case REMOVED:
                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                    break;
            }
        }
    }

    /*
    * Retrieves messages for one-to-one chats
    *  */
    private void retrieveChatMessages(){
        Log.d("chatId", chatId(user.getPhoneNumber(), receiver));

        Query query = db.collection("chats").document(chatId(user.getPhoneNumber(), receiver))
                .collection("messages").orderBy("timestamp");
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                retrieveMessages(queryDocumentSnapshots);
        }
            });
    }

    /*
    * Requires: Message object with all field set
    * Function: updates the the chats document within the chats collection
    *           in the user document (used to show the chats preview in the
    *           for the home page of the user)
    * */
    private void addToUserDocument(Message message){

        String chatID = chatId(user.getPhoneNumber(), receiver);
        chat.setChatId(chatID);
        db.collection("users").document(user.getPhoneNumber())
                .collection("userchats").document(chatID).set(message);
        db.collection("users").document(receiver)
                .collection("userchats").document(chatID).set(message);
    }

    private void sendGroupMessage(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Message message = new Message();
                message.setTextMessage(mTextbox.getText().toString().trim());
                mTextbox.setText("");

                if (message.getTextMessage().length() > 0) {
                    message.setIdSender(user.getPhoneNumber());
                    message.setTimestamp(System.currentTimeMillis());

                    db.collection("users").document(Objects.requireNonNull(user.getPhoneNumber())).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot senderDoc = task.getResult();
                                        message.setSenderName(Objects.requireNonNull(Objects
                                                .requireNonNull(Objects.requireNonNull(senderDoc)
                                                .getData()).get("name")).toString());

                                        /*
                                         * the id generated from ".add()" does not automatically
                                         * have a timestamp as in push() from realtime database
                                         */
                                        //add message to chatroom
                                        message.setThreadId(groupName);
                                        db.collection("ChatRooms").document(groupName)
                                                .collection("messages")
                                                .add(message);

                                        //add to last message received of every member
                                        for(User member:groupList){
                                            //message.setIdReceiver(member.getPhoneNumber());
                                            message.setReceiverName(member.getName());
                                            message.setTokenReceiver(member.getDeviceToken());

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("lastMessage", message);

                                            db.collection("users").document(member.getPhoneNumber())
                                                    .collection("ChatRooms").document(groupName)
                                                    .set(data, SetOptions.merge());
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void retrieveGroupMessages(){
        Query query = db.collection("ChatRooms").document(groupName)
                .collection("messages").orderBy("timestamp");
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                retrieveMessages(queryDocumentSnapshots);
            }
        });
    }

    //on clicking back button finish activity and go back
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        listenerRegistration.remove();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void noMessagesLayout(){
        if(messageList.size()>0){
            mNoMessageLayout.setVisibility(View.INVISIBLE);
        } else {
            mNoMessageLayout.setVisibility(View.VISIBLE);
        }
    }
}
