package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.UUID;

/**
 * Created by Josh Schmidt on 9/30/17.
 */

@IgnoreExtraProperties
public class Contractor {
    public final String userType= "contractor";
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String type;
    private ContractorDetails contractorDetails;
    private List<String> customers;
    private String logoFileName;
    private String subscriptionExpiration;

    //Mark:- Constructors
    private Contractor(ContractorDetails details, List<String> customers) {
        this.contractorDetails = details;
        this.customers = customers;
    }
    public Contractor(String logoFileName) {
        this.logoFileName = logoFileName;
    }
    public Contractor() {
        // Default constructor required for calls to DataSnapshot.getValue(Home.class)
    }

    //Mark:- Getters/Setters
    public String getNamePrefix() {
        return namePrefix;
    }
    public Contractor setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }
    public Contractor setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }
    public Contractor setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getType() {
        return type;
    }
    public Contractor setType(String type) {
        this.type = type;
        return this;
    }

    public String getSubscriptionExpiration() {
        return subscriptionExpiration;
    }
    // Format: mm.dd.YYYY
    public Contractor setSubscriptionExpiration(final String subscriptionExpiration) {
        this.subscriptionExpiration = subscriptionExpiration;
        return this;
    }

    public ContractorDetails getContractorDetails() {
        return contractorDetails;
    }
    public Contractor setContractorDetails(ContractorDetails contractorDetails) {
        this.contractorDetails = contractorDetails;
        return this;
    }

    public List<String> getCustomers() {
        return customers;
    }
    public Contractor setCustomers(List<String> customers) {
        this.customers = customers;
        return this;
    }

    public String getLogoFileName() {
        return logoFileName;
    }
    public Contractor setLogoFileName() {
        this.logoFileName = UUID.randomUUID().toString();
        return this;
    }

    //Mark:- Helper functions
    public void updateProfile(String namePrefix, String firstName, String lastName, String contractorType) {
        this.namePrefix = namePrefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = contractorType;
    }
}
