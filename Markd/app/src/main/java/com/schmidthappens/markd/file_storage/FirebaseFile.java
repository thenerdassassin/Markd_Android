package com.schmidthappens.markd.file_storage;

import com.google.firebase.database.Exclude;

import java.util.UUID;

/**
 * Created by joshua.schmidtibm.com on 2/2/18.
 */

public class FirebaseFile {
    private String fileName;
    private String guid;
    //ContentType?

    public FirebaseFile() {
        // Default constructor required for calls to DataSnapshot.getValue(ContractorService.class)
    }
    public FirebaseFile(String fileName, String guid) {
        this.fileName = fileName;
        setGuid(guid);
    }

    public FirebaseFile(String fileName) {
        this.fileName = fileName;
        setGuid(null);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Exclude
    public String getFilePath(String customerId) {
        return "services/"+customerId+"/"+guid;
    }

    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        if(guid == null) {
            this.guid = UUID.randomUUID().toString();
        } else {
            this.guid = guid;
        }
    }

}
