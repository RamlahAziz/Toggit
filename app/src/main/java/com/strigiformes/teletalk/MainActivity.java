package com.strigiformes.teletalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


//this implements the firebase registration process
//it goes to the welcome screens after this
public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = "tag";

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRef;
    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int resultcode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        //adjustFontScale(getResources().getConfiguration());

        //hide the top bar
        getSupportActionBar().hide();

        Firebase.setAndroidContext(this);

        //mRef = FirebaseDatabase.getInstance().getReference().child("Driver");


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

                    if( user != null){
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
                                                .build()*/)).build(),
                                RC_SIGN_IN);
                    }
                }
            };

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    /*private void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }*/

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
