package com.strigiformes.teletalk.Startup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.strigiformes.teletalk.R;

import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity {

    private static final String TAG = "UserRegistration";
    //For finding the current signed in user
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();

    //Widget references from xml
    private TextInputEditText mName;
    private Button mNext;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        //hide the top bar
        getSupportActionBar().hide();

        mName = (TextInputEditText) findViewById(R.id.name_field);

        mNext = (Button) findViewById(R.id.nextButton);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mName.getText().length()>0){

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    Map<String, Object> newUser = new HashMap<>();
                                    newUser.put("name", mName.getText().toString());
                                    newUser.put("uid", user.getUid());
                                    newUser.put("tokenId", task.getResult().getToken());

                                    // Add a new document with a generated ID
                                    db.collection("users")
                                            .add(newUser)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                    //Add name to Customer in Firebase
                                    /*final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference()
                                            .child("Customer").child(user.getPhoneNumber());
                                    mRef.child("name").setValue(mName.getText().toString());
                                    mRef.child("token_id").setValue(token_id);
                                    mRef.child("rating").setValue(5.0);*/

                                    //Goto Trips Activity
                                    Intent intentWelcome = new Intent(UserRegistration.this, WelcomeActivity.class);
                                    startActivity(intentWelcome);

                                }
                            });

                }
                else{
                    Toast.makeText(UserRegistration.this, "Please enter your name!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
