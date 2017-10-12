package com.schmidthappens.markd.account_authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by joshua.schmidtibm.com on 10/7/17.
 */

public class FirebaseAuthentication {
    private static final String TAG = "FirebaseAuthentication";

    private Activity activity;
    private FirebaseUser currentUser;
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
                if(!(activity instanceof LoginActivity) && activity != null) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        }
    };

    public FirebaseAuthentication(Activity activity) {
        if(firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        this.activity = activity;
        attachListener();
    }

    public FirebaseUser getCurrentUser() {
        setCurrentUser();
        return currentUser;
    }

    private void setCurrentUser() {
        currentUser = firebaseAuth.getCurrentUser();
    }

    private void attachListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void detachListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    //Should be called during the onStart of Activities
    // Returns true if logged in else returns false
    public boolean checkLogin() {
        currentUser = firebaseAuth.getCurrentUser();
        return (currentUser != null);
    }

    @NonNull
    public Task<AuthResult> createAccount(final Activity ctx, String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            currentUser = firebaseAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    @NonNull
    public Task<AuthResult> signIn(final Activity ctx, final String email, final String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            currentUser = firebaseAuth.getCurrentUser();
                            if(currentUser != null) {
                                String userId = currentUser.getUid();
                                //TODO: Get customer from db who has this userId and load into TempCustomerData
                            }
                        }
                    }
                });
    }

    public void signOut(Activity ctx) {
        firebaseAuth.signOut();
    }
}
