package com.schmidthappens.markd.firebase_cloud_messaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;

import java.io.IOException;

/**
 * Created by joshua.schmidtibm.com on 11/24/17.
 */

//https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseInstanceIDService.java
public class MarkdFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseInstanceIDSvc";
    private static String currentToken;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        currentToken = refreshedToken;
        sendRegistrationToServer();
    }

    private void sendRegistrationToServer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Context context = getApplicationContext();
        saveToken(user, context);
    }

    public static void saveToken(FirebaseUser user, Context context) {
        if(user != null) {
            if(currentToken == null) {
                getToken();
            }
            Log.d(TAG, "saveToken - User:" + user.getUid() + ", token:" + currentToken);
            getReference(user.getUid(), context).setValue(currentToken);
        } else {
            Log.d(TAG, "saveToken:nullUser");
        }
    }

    public static void deleteToken(String userId, Context context) {
        Log.d(TAG, "deleteToken  - User:" + userId + ", token:" + currentToken);
        getReference(userId, context).setValue(null);
    }

    private static DatabaseReference getReference(String uid, Context context) {
        return FirebaseDatabase.getInstance().getReference("tokens").child(uid).child(Installation.id(context));
    }

    private static void getToken() {
        currentToken = FirebaseInstanceId.getInstance().getToken();
    }
}
