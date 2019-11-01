package com.strigiformes.toggit.Contacts;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.strigiformes.toggit.ConversationThreads.HomeActivity;
import com.strigiformes.toggit.CustomObjects.ChatListItem;
import com.strigiformes.toggit.CustomObjects.User;
import com.strigiformes.toggit.GroupCreation.SelectGroupContacts;
import com.strigiformes.toggit.MessageActivity;
import com.strigiformes.toggit.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsLists extends AppCompatActivity{

    /*
    * Views and objects required for the custom adapter
    * */
    private ListView mListView;
    private CustomListAdapter mAdapter;
    private List<User> appContacts = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_lists);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setTitle("Select Contact");

        mListView = findViewById(R.id.contacts_list);

        //Retrieves the contacts list created in the home page
        //creating the the list in homepage makes the loading faster
        appContacts = (List<User>) getIntent().getExtras().getSerializable("APP_CONTACTS");

        mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, appContacts);
        mListView.setAdapter(mAdapter);

        //Opens the chat activity for one-to-one chat
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactsLists.this, MessageActivity.class);
                User contactSelected = appContacts.get(position);
                ChatListItem chat = new ChatListItem(contactSelected.getName(), contactSelected.getPhoneNumber());
                intent.putExtra("CHAT", chat);
                startActivity(intent);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContactsLists.this, HomeActivity.class));
        finish();
    }

    //on clicking back button finish activity and go back
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /*
    * Requires: view used to start creation of the new chatroom
    * Function: Starts new intent that allows the user to select
    *           contacts to be added in the new chat
    * */
    public void newChat(View view){
        Intent groupIntent = new Intent(ContactsLists.this, SelectGroupContacts.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CONTACTS", (Serializable) appContacts);
        groupIntent.putExtras(bundle);
        startActivity(groupIntent);
    }

}