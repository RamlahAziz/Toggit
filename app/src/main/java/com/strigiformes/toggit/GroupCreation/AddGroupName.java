package com.strigiformes.toggit.GroupCreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strigiformes.toggit.Contacts.CustomListAdapter;
import com.strigiformes.toggit.CustomObjects.User;
import com.strigiformes.toggit.MessageActivity;
import com.strigiformes.toggit.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupName extends Activity {

    private static final String TAG = "AddGroupName";

    ListView list;
    CustomListAdapter listviewadapter;
    List<User> groupList = new ArrayList<User>();

    private TextView mGroupName;
    private String groupName;

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);

        groupList = (List<User>) getIntent().getExtras().getSerializable("GROUP_MEMBERS");
        Log.d("groupContacts", groupList.toString());

        list = (ListView) findViewById(R.id.group_list);
        listviewadapter = new CustomListAdapter(this, R.layout.contact_list_item,
                groupList);
        list.setAdapter(listviewadapter);

        mGroupName = findViewById(R.id.group_name_field);


        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = mGroupName.getText().toString();
                if(groupName != null && !groupName.isEmpty()){

                    User[] userArray = groupList.toArray(new User[0]);
                    List<User> membersList = Arrays.asList(userArray);
                    Map<String, Object> members = new HashMap<>();
                    members.put("members", groupList);

                    Log.d(TAG,"groupname LENGTH IS greater than 0 MARIA");
                    //Create Chatroom
                    db.collection("ChatRooms").document(groupName)
                            .set(members)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    //Add Chat room to each member's account in the database
                    for(User member: groupList){
                        db.collection("users").document(member.getPhoneNumber())
                                .collection("ChatRooms").document(groupName).set(members)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });;
                    }

                    //Open chat room
                    Intent openChatIntent = new Intent(AddGroupName.this, MessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CONTACTS", (Serializable) groupList);
                    bundle.putSerializable("GROUP_NAME", groupName);
                    bundle.putSerializable("GROUP_CHAT", true);
                    openChatIntent.putExtras(bundle);
                    startActivity(openChatIntent);

                }
            }
        });

    }
}
