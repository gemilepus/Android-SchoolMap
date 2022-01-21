package com.vine.projectdemo.AccountView.models;

import com.vine.projectdemo.DataView.AndroidVersion;

public class ServerResponse {

    private String result;
    private String message;
    private User user;

    public String getResult() {
        return result;
    }
    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }

    private AndroidVersion[] android;
    public AndroidVersion[] getAndroid() {
        return android;
    }
}
