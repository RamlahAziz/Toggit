package com.strigiformes.teletalk;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.strigiformes.teletalk.CustomObjects.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsLists extends Activity{

    private static final String TAG = "tag";

    private ListView mListView;
    private CustomListAdapter mAdapter;

    private List<String> phoneContacts = new ArrayList<String>();
    private List<User> appContacts = new ArrayList<User>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_lists);

        mListView = findViewById(R.id.list);

        readContacts();

        mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, appContacts);
        mListView.setAdapter(mAdapter);

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

    private void readContacts() {

        phoneContacts.clear();
        appContacts.clear();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(phoneContacts.contains(document.getId())){

                            User user = new User();
                            user.setName(document.getData().get("name").toString());
                            user.setPhoneNumber(document.getId());

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

    }

