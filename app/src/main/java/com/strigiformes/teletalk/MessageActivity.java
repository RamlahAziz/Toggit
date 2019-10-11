package com.strigiformes.teletalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    //private static final int ACTIVITY_NUM = 1;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button mSendButton;
    private EditText mTextbox;
    private View mNoMessageLayout;

    private List<Message> messageList = new ArrayList<>();

    private ChatListItem chat;
    private String TAG = "MessageListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent i = getIntent();
        chat = (ChatListItem) i.getSerializableExtra("CHAT");

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        mMessageRecycler =  findViewById(R.id.reyclerview_message_list);
        /*
         * Add or Update when chat was last opened
         * so number of unread messages can be tracked
         * */

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        mSendButton =  findViewById(R.id.button_chatbox_send);
        mTextbox =  findViewById(R.id.edittext_chatbox);
        mNoMessageLayout = findViewById(R.id.noMessageLayout);

        /*
        mMessageAdapter = new MessageListAdapter(MessageActivity.this, messageList);

        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setHasFixedSize(true);
        final LinearLayoutManager mManager = new
                LinearLayoutManager(MessageActivity.this, RecyclerView.VERTICAL,false);
        mManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(mManager);

        sendMessage(mSendButton);
        */
    }

    private void sendMessage(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = mTextbox.getText().toString().trim();
                if (content.length() > 0) {

                }
            }
        });
    }

    //on clicking back button finish activity and go back
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
