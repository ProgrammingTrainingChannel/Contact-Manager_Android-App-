package com.btitsolutions.contactmanager;

import android.graphics.Bitmap;

/**
 * Created by Trinity Tuts on 10-01-2015.
 */
public class SelectContact {
    String name;
    String phone;
    String email;

    public SelectContact()
    {

    }

    public SelectContact(String paramName, String paramPhone, String paramEmail)
    {
        name = paramName;
        phone = paramPhone;
        email = paramEmail;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
