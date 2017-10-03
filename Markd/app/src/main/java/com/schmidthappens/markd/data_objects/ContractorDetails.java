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

public class ContractorDetails {
    private static final String TAG = "ContractorDetailsBean";
    private String companyName;
    private String telephoneNumber;
    private String websiteUrl;
    private String zipCode;
    private String type; //TODO: make enum

    public ContractorDetails(String companyName, String telephoneNumber, String websiteUrl, String zipCode, String type) {
        this.companyName = companyName;
        this.telephoneNumber = telephoneNumber;
        this.websiteUrl = websiteUrl;
        this.zipCode = zipCode;
        this.type = type;
    }
    public ContractorDetails(ContractorDetails oldContractorDetails) throws NullPointerException {
        this(
                oldContractorDetails.getCompanyName(),
                oldContractorDetails.getTelephoneNumber(),
                oldContractorDetails.getWebsiteUrl(),
                oldContractorDetails.getZipCode(),
                oldContractorDetails.getType()
        );
    }
    public ContractorDetails(JSONObject contractor) {
        this(
                contractor.optString("companyName"),
                contractor.optString("telephoneNumber"),
                contractor.optString("websiteUrl"),
                contractor.optString("zipCode"),
                contractor.optString("type")
        );
    }

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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
