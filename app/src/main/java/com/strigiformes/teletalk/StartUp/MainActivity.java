package com.strigiformes.teletalk.StartUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.strigiformes.teletalk.ConversationThreads.HomeActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//this implements the firebase listenerRegistration process
//it goes to the welcome screens after this
public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = "tag";

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int resultcode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        //adjustFontScale(getResources().getConfiguration());

        //hide the top bar
        getSupportActionBar().hide();

        Firebase.setAndroidContext(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
               /*gets invoked in the UI thread on changes in the authentication state
                 * Right after the listener has been registered
                 * When a user is signed in
                 * When the current user is signed out
                 * When the current user changes
                 * When there is a change in the current user's token
                 */
                @Override
                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                    user = mauth.getCurrentUser();

                    if (user!=null) {
                        //check if user is already registered
                        db.collection("users").document(user.getPhoneNumber()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        /*
                                         * if the user is already registered direct them to HomeActivity
                                         * otherwise direct them to the listenerRegistration page
                                         * */

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                                Intent homeIntent = new Intent(MainActivity.this,  HomeActivity.class);

                                                //update user token to ensure the user always gets notification
                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                                                    return;
                                                                }

                                                                // Update one field, creating the document if it does not already exist.
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("tokenId", task.getResult().getToken());

                                                                db.collection("users").document(user.getPhoneNumber())
                                                                        .set(data, SetOptions.merge());
                                                            }
                                                        });
                                                startActivity(homeIntent);

                                            } else {
                                                Log.d(TAG, "No such document");

                                                Intent registrationIntent = new Intent(MainActivity.this,  UserRegistration.class);
                                                startActivity(registrationIntent);

                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                    } else {
                        //create login options
                        startActivityForResult(AuthUI.getInstance().
                                        createSignInIntentBuilder().
                                        setAvailableProviders
                                                (Arrays.asList(
                                                        new AuthUI.IdpConfig.PhoneBuilder()
                                                                .build()/*,
                                        new AuthUI.IdpConfig.EmailBuilder()
                                                .build()*/)).build(),
                                RC_SIGN_IN);
                    }

                    /*if( user != null){
                        Intent welcomeIntent = new Intent(MainActivity.this,  WelcomeActivity.class);
                        startActivity(welcomeIntent);
                        //already signed in
                        //DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Customer");
                        //isRegistered(mRef);
                    }else{
                        //create login options
                        startActivityForResult(AuthUI.getInstance().
                                        createSignInIntentBuilder().
                                        setAvailableProviders
                                                (Arrays.asList(
                                                        new AuthUI.IdpConfig.PhoneBuilder()
                                                                .build()/*,
                                        new AuthUI.IdpConfig.EmailBuilder()
                                                .build()*//*)).build(),
                                RC_SIGN_IN);
                    }*/
                }
            };

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mAuthListener);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}
