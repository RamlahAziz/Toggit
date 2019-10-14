package com.strigiformes.teletalk;

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
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;

//Home screen of our app
//This displays a list of current chats user has
//with pictures, message received, time etc
//new chat button in bottom right corner
public class HomeActivity extends AppCompatActivity {

    private String phone_Number;
    private ListView mListView;
    private View mNoChatsLayout;
    private List<ChatListItem> chatsList = new ArrayList<ChatListItem>();
    private FloatingActionButton fab;

    private Button mSignOut;

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
        mSignOut = (Button) findViewById(R.id.action_logout);

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
                signOut(mSignOut);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // [START auth_sign_out]
                FirebaseAuth.getInstance().signOut();
                // [END auth_sign_out]

                finishAffinity();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
}
