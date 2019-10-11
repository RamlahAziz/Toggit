package com.strigiformes.teletalk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

//Home screen of our app
//This displays a list of current chats user has
//with pictures, message received, time etc
//new chat button in bottom right corner
public class HomeActivity extends AppCompatActivity {

    private String phone_Number;
    private ListView mListView;
    private View mNoChatsLayout;
    private List<ChatListItem> chatsList = new ArrayList<ChatListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is the xml file being used : activity_home
        setContentView(R.layout.activity_home);

        mListView = findViewById(R.id.list_view);
        mNoChatsLayout = findViewById(R.id.noChatsLayout);

        //mAdapter = new ChatListAdapter(MainChat.this, R.layout.activity_home, chatsList);
        //mListView.setAdapter(mAdapter);

        //Open selected chat
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent chatIntent = new Intent(HomeActivity.this, MessageActivity.class);
                chatIntent.putExtra("CHAT", chatsList.get(i));
                Log.d("testing5 MainChat",  chatsList.get(i).toString());
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
                                //block(chatsList.get(i).getChatId(), chatsList.get(i).getCustomerPhone());
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

    }


}
