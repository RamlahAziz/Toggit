package com.strigiformes.teletalk.ConversationThreads;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.strigiformes.teletalk.CustomObjects.ChatListItem;
import com.strigiformes.teletalk.Contacts.ContactsLists;
import com.strigiformes.teletalk.CustomObjects.Message;
import com.strigiformes.teletalk.CustomObjects.User;
import com.strigiformes.teletalk.MessageActivity;
import com.strigiformes.teletalk.R;
import com.strigiformes.teletalk.StartUp.MainActivity;

//Home screen of our app
//This displays a list of current chats user has
//with pictures, message received, time etc
//new chat button in bottom right corner
public class HomeActivity extends AppCompatActivity {

    private String TAG = "HomeActivity";

    private String phone_Number;
    private ListView mListView;
    private View mNoChatsLayout;
    private List<ChatListItem> chatsList = new ArrayList<ChatListItem>();
    private FloatingActionButton fab;
    ChatListAdapter mAdapter;

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is the xml file being used : activity_home
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.list_view);
        mNoChatsLayout = findViewById(R.id.noChatsLayout);
        fab = findViewById(R.id.fab);

        showChats();

         mAdapter = new ChatListAdapter(HomeActivity.this, R.layout.activity_home, chatsList);
         mListView.setAdapter(mAdapter);

        //Open selected chat
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent chatIntent = new Intent(HomeActivity.this, MessageActivity.class);
                Bundle bundle = new Bundle();
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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                //builder.setTitle("Choose an option");

                // add a list
                String[] actions = {"Archive", "Delete", "Block"};
                builder.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Archive
                                //archive(chatsList.get(i).getChatId());
                                break;
                            case 1: // Delete
                                //delete(chatsList.get(i).getChatId());
                                break;
                            case 2: // Block
                                //block(chatsList.get(i).getChatId(), chatsList.get(i).getToPhone());
                                break;
                            /*case 3: // Report
                                break;*/
                            default:
                                break;
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
                startActivity(contactsIntent);
            }
        });
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

        switch(id){

            case R.id.action_blocked:
                break;

            case R.id.action_archived:
                break;

            case R.id.action_settings:
                break;

            case R.id.action_logout:
                signOut();
                break;

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

                            if(user.getPhoneNumber().equals(chatListItem.getFromPhone())){
                                chatListItem.setName(Objects.requireNonNull(doc.get("receiverName")).toString());
                            } else{
                                chatListItem.setName(Objects.requireNonNull(doc.get("senderName")).toString());
                            }

                            chatsList.add(chatListItem);
                            mAdapter.notifyDataSetChanged();
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
                            } else {
                                chatListItem.setMsgPreview("");
                            }
                            Log.d("Adding chat in list", chatListItem.toString());
                            chatsList.add(chatListItem);
                            mAdapter.notifyDataSetChanged();
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

}
