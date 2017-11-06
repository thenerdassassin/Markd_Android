package com.schmidthappens.markd.data_objects;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

@IgnoreExtraProperties
public class ContractorDetails {
    private String companyName;
    private String telephoneNumber;
    private String websiteUrl;
    private String zipCode;

    public ContractorDetails(String companyName, String telephoneNumber, String websiteUrl, String zipCode) {
        this.companyName = companyName;
        this.telephoneNumber = telephoneNumber;
        this.websiteUrl = websiteUrl;
        this.zipCode = zipCode;
    }
    public ContractorDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(ContractorDetails.class)
    }

    //Mark:- Getters/Setters
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTelephoneNumber() {
        if(telephoneNumber == null) {
            return "";
        }
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

}
