package com.strigiformes.teletalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.strigiformes.teletalk.CustomObjects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity  {

    //private static final int ACTIVITY_NUM = 1;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button mSendButton;
    private EditText mTextbox;
    private View mNoMessageLayout;

    private List<Message> messageList = new ArrayList<>();

    private ChatListItem chat;
    private String TAG = "MessageListActivity";

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For Group chat rooms
    List<User> groupList = new ArrayList<User>();
    private String groupName;
    Boolean groupChat = false;

    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent i = getIntent();
        chat = (ChatListItem) i.getSerializableExtra("CHAT");
        groupList = (List<User>) getIntent().getExtras().getSerializable("CONTACTS");
        groupName = (String) getIntent().getExtras().getSerializable("GROUP_NAME");
        if(getIntent().getExtras().getSerializable("GROUPCHAT") != null){
            groupChat = (Boolean) getIntent().getExtras().getSerializable("GROUPCHAT");
        }

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

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

        mSendButton =  findViewById(R.id.button_chatbox_send);
        mTextbox =  findViewById(R.id.edittext_chatbox);
        mNoMessageLayout = findViewById(R.id.noMessageLayout);


        mMessageAdapter = new MessageListAdapter(MessageActivity.this, messageList);

        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setHasFixedSize(true);
        final LinearLayoutManager mManager = new
                LinearLayoutManager(MessageActivity.this, RecyclerView.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(mManager);

        if(groupChat){
            sendGroupMessage(mSendButton);
            retrieveGroupMessages();
        }
        else{
            sendMessage(mSendButton);
            retrieveChatMessages();
        }


    }

    private void sendMessage(Button button){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Message message = new Message();
                message.setTextMessage(mTextbox.getText().toString().trim());
                mTextbox.setText("");

                if (message.getTextMessage().length() > 0) {

                    //database call needed to get the receiver's token id so notification can be sent
                    db.collection("users").document(chat.getToPhone()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        DocumentSnapshot receiverDoc = task.getResult();
                                        message.setTokenReceiver(receiverDoc.getData().get("tokenId").toString());
                                        message.setIdSender(user.getPhoneNumber());
                                        message.setIdReceiver(chat.getToPhone());
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
                                                        db.collection("chats").document(chatId(user.getPhoneNumber(), chat.getToPhone()))
                                                                .collection("messages")
                                                                .add(message);


                                                        addToUserDocument(message);
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }

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

    private void retrieveMessages(QuerySnapshot queryDocumentSnapshots){
        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    Log.d(TAG, "New message: " + dc.getDocument().getData());
                    Map<String, Object> doc =dc.getDocument().getData();
                    Message message = new Message();
                    message.setSenderName(doc.get("senderName").toString());
                    message.setTextMessage(doc.get("textMessage").toString());
                    message.setIdSender(doc.get("idSender").toString());
                    message.setTimestamp((Long) doc.get("timestamp"));
                    messageList.add(message);
                    mMessageAdapter.notifyDataSetChanged();
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

    private void retrieveChatMessages(){
        Query query = db.collection("chats").document(chatId(user.getPhoneNumber(), chat.getToPhone()))
                .collection("messages");
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
        /*.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        messageList.clear();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> doc =document.getData();

                                Message message = new Message();
                                message.setSenderName(doc.get("senderName").toString());
                                message.setTextMessage(doc.get("textMessage").toString());
                                message.setIdSender(doc.get("idSender").toString());
                                message.setTimestamp((Long) doc.get("timestamp"));

                                messageList.add(message);
                            }
                        }
                    }
                });*/
            });
    }

    private void addToUserDocument(Message message){

        String chatID = chatId(user.getPhoneNumber(), chat.getToPhone());
        db.collection("users").document(user.getPhoneNumber())
                .collection("userchats").document(chatID).set(message);
        db.collection("users").document(chat.getToPhone())
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
                                         * the id generated from add does not automatically
                                         * have a timestamp as in push() from realtime database
                                         */

                                        message.setThreadId(groupName);
                                        db.collection("ChatRooms").document(groupName)
                                                .collection("messages")
                                                .add(message);

                                        //add to last message received of every member
                                        for(User member:groupList){
                                            message.setIdReceiver(member.getPhoneNumber());
                                            message.setReceiverName(member.getName());
                                            message.setTokenReceiver(member.getDeviceToken());

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("lastMessage", message);

                                            db.collection("users").document(user.getPhoneNumber())
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
                .collection("messages");
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
}
