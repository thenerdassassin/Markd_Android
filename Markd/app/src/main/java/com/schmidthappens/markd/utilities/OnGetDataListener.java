package com.schmidthappens.markd.utilities;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by joshua.schmidtibm.com on 10/14/17.
 */

public interface OnGetDataListener {
    void onStart();
    void onSuccess(DataSnapshot data);
    void onFailed(DatabaseError databaseError);
}
