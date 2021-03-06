package com.strigiformes.toggit.ConversationThreads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strigiformes.toggit.CustomObjects.ChatListItem;
import com.strigiformes.toggit.Contacts.ContactsLists;
import com.strigiformes.toggit.CustomObjects.Message;
import com.strigiformes.toggit.CustomObjects.User;
import com.strigiformes.toggit.MessageActivity;
import com.strigiformes.toggit.R;
import com.strigiformes.toggit.StartUp.MainActivity;

public class HomeActivity extends AppCompatActivity {

    private String TAG = "HomeActivity";

    private ListView mListView;
    private View mNoChatsLayout;
    private List<ChatListItem> chatsList = new ArrayList<ChatListItem>();
    private FloatingActionButton fab;
    ChatListAdapter mAdapter;

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration listenerRegistration;

    private List<String> phoneContacts = new ArrayList<String>();
    private List<User> appContacts = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.list_view);
        mNoChatsLayout = findViewById(R.id.noChatsLayout);
        fab = findViewById(R.id.fab);

        showChats();
        noChatsLayout();

         mAdapter = new ChatListAdapter(HomeActivity.this, R.layout.activity_home, chatsList);
         mListView.setAdapter(mAdapter);

        //Open selected chat
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent chatIntent = new Intent(HomeActivity.this, MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("HOME", true);
                bundle.putSerializable("CHAT", chatsList.get(i));
                if (chatsList.get(i).getToPhone()==null) {
                    bundle.putSerializable("GROUP_CHAT", true);
                    bundle.putSerializable("GROUP_NAME", chatsList.get(i).getName());
                }
                chatIntent.putExtras(bundle);
                startActivity(chatIntent);
            }
        });

        //popup box with actions blocking, deleting, archiving chat
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                //builder.setTitle("Choose an option");

                // add a list
                String[] actions = {"Delete"};
                builder.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChatListItem chat = (ChatListItem) mListView.getItemAtPosition(i);
                        if (which == 0) {
                            deleteChat(chat);
                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();


                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactsIntent = new Intent(HomeActivity.this, ContactsLists.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_CONTACTS", (Serializable) appContacts);
                contactsIntent.putExtras(bundle);
                startActivity(contactsIntent);
            }
        });

        //readContacts();
    }

    private void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        fromPath.delete();
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void archiveChat(ChatListItem chat){
        String type;
        if(chat.getGroup()){
            type = "ChatRooms";
        }else{
            type = "userchats";
        }

        DocumentReference fromPath = db.collection("users").document(user.getPhoneNumber())
                .collection(type).document(chat.getChatId());
        DocumentReference toPath = db.collection("users").document(user.getPhoneNumber())
                .collection("archived").document(chat.getChatId());
        moveFirestoreDocument(fromPath, toPath);

        chatsList.remove(chat);
        mAdapter.notifyDataSetChanged();
    }

    /*
    * Delete chate from user home
    * not from database
    * */
    private void deleteChat(ChatListItem chat){
        String type;
        if(chat.getGroup()){
            type = "ChatRooms";
        }else{
            type = "userchats";
        }
        db.collection("users").document(user.getPhoneNumber())
                .collection(type).document(chat.getChatId())
                .delete();
        chatsList.remove(chat);
        mAdapter.notifyDataSetChanged();
    }

    //cannot go back to previous activity, closes down app to background
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChats(){

        Query query = db.collection("users").document(user.getPhoneNumber())
                .collection("userchats");
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()) {
                        case ADDED:

                            Map<String, Object> doc =dc.getDocument().getData();
                            Log.d(TAG, "New individual chat: " + doc);
                            ChatListItem chatListItem = new ChatListItem();
                            chatListItem.setToPhone(Objects.requireNonNull(doc.get("idReceiver")).toString());
                            chatListItem.setFromPhone(Objects.requireNonNull(doc.get("idSender")).toString());
                            chatListItem.setChatId(dc.getDocument().getId());
                            chatListItem.setMsgPreview(Objects.requireNonNull(doc.get("textMessage")).toString());
                            chatListItem.setTimeStamp(Objects.requireNonNull(doc.get("timestamp")).toString());
                            if(doc.get("lastSeen") != null){
                                chatListItem.setLastSeen(doc.get("lastSeen").toString());
                            } else {
                                chatListItem.setLastSeen("0000000000000");
                            }

                            if(user.getPhoneNumber().equals(chatListItem.getFromPhone())){
                                chatListItem.setName(Objects.requireNonNull(doc.get("receiverName")).toString());
                            } else{
                                chatListItem.setName(Objects.requireNonNull(doc.get("senderName")).toString());
                            }

                            chatsList.add(chatListItem);
                            mAdapter.notifyDataSetChanged();
                            noChatsLayout();
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified chat: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed chat: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });

        Query groupQuery = db.collection("users").document(user.getPhoneNumber())
                .collection("ChatRooms");
        listenerRegistration = groupQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()) {
                        case ADDED:
                            ChatListItem chatListItem = new ChatListItem();
                            chatListItem.setGroup(true);
                            chatListItem.setChatId(dc.getDocument().getId());
                            chatListItem.setName(dc.getDocument().getId());
                            Map<String, Object> doc =dc.getDocument().getData();
                            Log.d(TAG, "New group chat: " + doc);
                            if (doc.get("lastMessage") != null) {
                                final ObjectMapper mapper = new ObjectMapper();
                                final Message message = mapper.convertValue(doc.get("lastMessage"), Message.class);
                                Log.d("inside if", chatListItem.getChatId());
                                //chatListItem.setToPhone(Objects.requireNonNull(message.getIdReceiver()));
                                chatListItem.setFromPhone(Objects.requireNonNull(message.getIdSender()));
                                chatListItem.setMsgPreview(Objects.requireNonNull(message.getTextMessage()));
                                chatListItem.setTimeStamp(String.valueOf(message.getTimestamp()));
                            } else {
                                chatListItem.setMsgPreview("");
                                chatListItem.setTimeStamp("0000000000000");
                            }
                            if(doc.get("lastSeen") != null){
                                chatListItem.setLastSeen(doc.get("lastSeen").toString());
                            } else {
                                chatListItem.setLastSeen("0000000000000");
                            }
                            Log.d("Adding chat in list", chatListItem.toString());
                            chatsList.add(chatListItem);
                            mAdapter.notifyDataSetChanged();
                            noChatsLayout();
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified chat: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed chat: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    private void signOut(){
                FirebaseAuth.getInstance().signOut();
                // [END auth_sign_out]

                finishAffinity();

                Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
    }

    private void readContacts() {

        phoneContacts.clear();
        appContacts.clear();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            //String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //remove white spaces
            phoneNo = phoneNo.replaceAll("\\s+","");

            int length = phoneNo.length();

            //make sure you are not adding a landline number
            if(length >= 10){

                //convert format to match the format in the database
                phoneNo = "+92"+phoneNo.substring(length-10);

                //make sure the contacts are not repeated
                if (!phoneContacts.contains(phoneNo)) {
                    phoneContacts.add(phoneNo);
                }
            }
        }
        phones.close();

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if(phoneContacts.contains(document.getId())){

                            User user = new User();
                            user.setName(Objects.requireNonNull(document.getData().get("name")).toString());
                            user.setPhoneNumber(document.getId());
                            user.setDeviceToken(Objects.requireNonNull(document.getData().get("tokenId")).toString());

                            appContacts.add(user);
                            mAdapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        finish();
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        readContacts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void noChatsLayout(){
        if(chatsList.size()>0){
            mNoChatsLayout.setVisibility(View.INVISIBLE);
        } else {
            mNoChatsLayout.setVisibility(View.VISIBLE);
        }
    }
}
