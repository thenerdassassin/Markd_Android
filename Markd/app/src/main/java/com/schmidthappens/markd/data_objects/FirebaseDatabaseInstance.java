package com.schmidthappens.markd.data_objects;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by joshua.schmidtibm.com on 3/29/18.
 */

public class FirebaseDatabaseInstance {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
