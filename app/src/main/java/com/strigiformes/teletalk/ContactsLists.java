package com.strigiformes.teletalk;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strigiformes.teletalk.CustomObjects.User;


import java.util.ArrayList;
import java.util.List;

public class ContactsLists extends Activity{

    private static final String TAG = "tag";

    private ListView mListView;
    private CustomListAdapter mAdapter;

    private List<User> phoneContacts = new ArrayList<User>();
    private List<User> appContacts = new ArrayList<User>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String[] PROJECTION_OUTER = new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    private static final String[] PROJECTION_INNER = new String[] {
            ContactsContract.CommonDataKinds.Phone.TYPE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_lists);

        mListView = findViewById(R.id.list);

        readContacts();

        mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, phoneContacts);
        mListView.setAdapter(mAdapter);
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

            final User user = new User();
            int length = phoneNo.length();

            //make sure you are not adding a landline number
            if(length >= 10){
                user.setName(name);

                //convert format to match the format in the database
                user.setPhoneNumber("+92"+phoneNo.substring(length-9));

                //make sure the contacts are not repeated
                if (!phoneContacts.contains(user)) {
                    phoneContacts.add(user);
                }
            }
        }
        Log.d("phonecontacs", phoneContacts.toString());
        phones.close();

        final int[] check = {0};

        for(final User currentUser : phoneContacts){

            db.collection("users").document(currentUser.getPhoneNumber()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("mydocument", document.toString());

                                if (document.exists()) {
                                    User user = new User();
                                    user.setPhoneNumber(document.getId());
                                    user.setName(document.getData().get("name").toString());
                                    appContacts.add(user);
                                    check[0] +=1;

                                   /* if (check[0] == phoneContacts.size()) {
                                        mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, appContacts);
                                        mListView.setAdapter(mAdapter);
                                        mAdapter.notifyDataSetChanged();
                                        Log.d("appcontacts", appContacts.toString());
                                    }*/
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }

        /*db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    String number = doc.getId();
                    if () {
                        break
                    }
                }
            }
        });*/


        /*Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION_OUTER, null, null, null);

        while (cursor.moveToNext()) {

            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
            {
                Cursor pCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION_INNER,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{contactId}, null);

                while (pCursor.moveToNext())
                {
                    String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNo = phoneNo.replaceAll("\\s+","");

                    final User user = new User();
                    int length = phoneNo.length();

                    //make sure you are not adding a landline number
                    if(length >= 10){
                        user.setName(name);

                        //convert format to match the format in the database
                        user.setPhoneNumber("+92"+phoneNo.substring(length-9));

                        //make sure the contacts are not repeated
                        if (!contacts.contains(user)) {
                            contacts.add(user);
                        }else {
                            break;
                        }
                    }
                }
                pCursor.close();
            }
            mAdapter.notifyDataSetChanged();
        }*/
        /*Log.d("contacts", contacts.toString());


        int counter = 0;

        for(final User currentUser : contacts){

            Log.d("currentUser", currentUser.getPhoneNumber());
            counter +=1;
            final int finalCounter = counter;
            db.collection("users").document(currentUser.getPhoneNumber()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    contacts.remove(currentUser);
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }

                            if(contacts.size()== finalCounter){
                                mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, contacts);
                                mListView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }*/
        /*cursor.close();*/
        }

    }

