package com.btitsolutions.contactmanager;

/**
 * Created by Bereket on 5/11/2017.
 */

public class CallSMSDetailModel {
    private String displayName;
    private String IsCallOrSms;
    private String IsBlocked;
    private String IsAlwaysBlocked;
    private String BlockedForHowLong;
    private String IsCallCleared;
    private String IsAlwaysCallCleared;
    private String CallClearedForHowLong;
    private String CreatedDate;

    public CallSMSDetailModel()
    {
    }

    public CallSMSDetailModel(String displayName, String IsCallOrSms, String IsBlocked, String IsAlwaysBlocked, String BlockedForHowLong
            , String IsCallCleared, String IsAlwaysCallCleared, String CallClearedForHowLong, String CreatedDate)
    {
        this.displayName = displayName;
        this.IsCallOrSms = IsCallOrSms;
        this.IsBlocked = IsBlocked;
        this.IsAlwaysBlocked = IsAlwaysBlocked;
        this.BlockedForHowLong = BlockedForHowLong;
        this.IsCallCleared = IsCallCleared;
        this.IsAlwaysCallCleared = IsAlwaysCallCleared;
        this.CallClearedForHowLong = CallClearedForHowLong;
        this.CreatedDate = CreatedDate;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setIsCallOrSms(String isCallOrSms) {
        IsCallOrSms = isCallOrSms;
    }

    public void setBlockedForHowLong(String blockedForHowLong) {
        BlockedForHowLong = blockedForHowLong;
    }

    public void setCallClearedForHowLong(String callClearedForHowLong) {
        CallClearedForHowLong = callClearedForHowLong;
    }

    public void setIsAlwaysBlocked(String isAlwaysBlocked) {
        IsAlwaysBlocked = isAlwaysBlocked;
    }

    public void setIsAlwaysCallCleared(String isAlwaysCallCleared) {
        IsAlwaysCallCleared = isAlwaysCallCleared;
    }

    public void setIsBlocked(String isBlocked) {
        IsBlocked = isBlocked;
    }

    public void setIsCallCleared(String isCallCleared) {
        IsCallCleared = isCallCleared;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getIsCallOrSms() {
        return IsCallOrSms;
    }

    public String getBlockedForHowLong() {
        return BlockedForHowLong;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCallClearedForHowLong() {
        return CallClearedForHowLong;
    }

    public String getIsAlwaysBlocked() {
        return IsAlwaysBlocked;
    }

    public String getIsAlwaysCallCleared() {
        return IsAlwaysCallCleared;
    }

    public String getIsBlocked() {
        return IsBlocked;
    }

    public String getIsCallCleared() {
        return IsCallCleared;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }
}
