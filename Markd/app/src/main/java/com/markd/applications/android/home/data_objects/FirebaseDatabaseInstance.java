package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Josh Schmidt on 3/29/18.
 */

public class FirebaseDatabaseInstance {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        return mDatabase;
    }
}
