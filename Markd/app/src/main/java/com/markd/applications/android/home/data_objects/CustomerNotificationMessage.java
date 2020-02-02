package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;
import com.markd.applications.android.home.utilities.StringUtilities;

/**
 * Created by Josh Schmidt on 12/13/17.
 */

@IgnoreExtraProperties
public class CustomerNotificationMessage {
    private String dateSent;
    private String companyFrom;
    private String message;

    public CustomerNotificationMessage(String companyFrom, String message) {
        this.companyFrom = companyFrom;
        this.message = message;
        this.dateSent = StringUtilities.getCurrentDateString();
    }
    public CustomerNotificationMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(Customer.class)
    }

    public String getDateSent() {
        return dateSent;
    }
    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }
    public String getCompanyFrom() {
        return companyFrom;
    }
    public void setCompanyFrom(String companyFrom) {
        this.companyFrom = companyFrom;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
