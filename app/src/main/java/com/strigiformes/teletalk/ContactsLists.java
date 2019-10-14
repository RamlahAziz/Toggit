package com.strigiformes.teletalk;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.ListView;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strigiformes.teletalk.CustomObjects.User;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;

public class ContactsLists extends Activity{

    private static final String TAG = "tag";

    private ListView mListView;
    private CustomListAdapter mAdapter;

    private List<User> contacts = new ArrayList<User>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_lists);

        mListView = findViewById(R.id.list);
        readContacts();

        mAdapter = new CustomListAdapter(ContactsLists.this, R.layout.contact_list_item, contacts);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void readContacts() {

        contacts.clear();

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
            {

                //the below cursor will give you details for multiple contacts
                Cursor pCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{contactId}, null);

                String checkNumber = "";
                // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                while (pCursor.moveToNext())
                {
                    String phoneNo    = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //you will get all phone numbers according to it's type as below switch case.
                    //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                    Log.d("phoneNo", phoneNo);

                    final User user = new User();

                    int length = phoneNo.length();
                    if(length >= 10 && !checkNumber.equals(phoneNo)){
                        checkNumber = phoneNo;

                        user.setName(name);
                        user.setPhoneNumber("+92"+phoneNo.substring(length-9));

                        contacts.add(user);}
                }
                pCursor.close();
            }
        }
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
        cursor.close();
    }


}
