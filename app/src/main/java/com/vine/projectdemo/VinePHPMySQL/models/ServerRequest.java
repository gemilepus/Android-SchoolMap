package com.vine.projectdemo.VinePHPMySQL.models;

public class ServerRequest {
    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
