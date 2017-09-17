package com.schmidthappens.markd.account_authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by Josh on 7/29/2017.
 * Used from: https://stackoverflow.com/questions/6112269/android-keep-username-in-session-until-logout
 */


public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MarkdSessionPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    private static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    private static final String KEY_EMAIL = "email";

    // Email address (make variable public to access from outside)
    private static final String KEY_USER_TYPE = "user_type";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    /**
     * Create login session
     * Made package private
     * */
    void createLoginSession(String name, String email, String userType){
        editor = pref.edit();
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        //Storing user account type in pref
        editor.putString(KEY_USER_TYPE, userType);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Starting Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getUserType() { return pref.getString(KEY_USER_TYPE, null);}


    /**
     * Clear session details
     * */
    public void logoutUser(){
        if(editor == null) {
            editor = pref.edit();
        }

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();


        // After logout redirect user to LoginActivity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity and close all old Activities
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    private boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}