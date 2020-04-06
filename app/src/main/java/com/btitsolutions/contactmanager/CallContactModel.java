package com.btitsolutions.contactmanager;

/**
 * Created by Bereket on 5/11/2017.
 */

public class CallContactModel {
    private String displayName;
    private String phoneNumber;

    public CallContactModel()
    {
    }

    public CallContactModel(String displayName, String phoneNumber)
    {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
