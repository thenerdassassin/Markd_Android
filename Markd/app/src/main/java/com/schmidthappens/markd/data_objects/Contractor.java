package com.schmidthappens.markd.data_objects;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Contractor {
    private static final String TAG = "Contractor_Bean";
    private String companyName;
    private String telephoneNumber;
    private String websiteUrl;
    private String zipCode;
    private String type; //TODO: make enum

    public Contractor(String companyName, String telephoneNumber, String websiteUrl, String zipCode) {
        this.companyName = companyName;
        this.telephoneNumber = telephoneNumber;
        this.websiteUrl = websiteUrl;
        this.zipCode = zipCode;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getTelephoneNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PhoneNumberUtils.formatNumber(telephoneNumber, Locale.getDefault().getCountry());
        } else {
            return PhoneNumberUtils.formatNumber(telephoneNumber); //Deprecated method
        }
    }
    public void setTelephoneNumber(String telephoneNumber) {
        telephoneNumber = telephoneNumber.replaceAll("[^0-9]", "");
        if(telephoneNumber.length() != 10) {
            return;
        }
        this.telephoneNumber = telephoneNumber;
    }
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Contractor(JSONObject contractor) {
        try {
            this.companyName = contractor.getString("companyName");
            this.telephoneNumber = contractor.getString("telephoneNumber");
            this.websiteUrl = contractor.getString("websiteUrl");
            this.zipCode = contractor.getString("zipCode");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }
}
