package com.vine.projectdemo.VinePHPMySQL.models;

import com.vine.projectdemo.VineJsonParsing.AndroidVersion;

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
