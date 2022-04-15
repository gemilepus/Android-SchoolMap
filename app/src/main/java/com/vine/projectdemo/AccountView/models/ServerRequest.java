package com.vine.projectdemo.AccountView.models;

public class ServerRequest {

    private String operation;
    private User user;
    private String token;

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setToken(String token) {
        this.token = token;
    }
}