package com.vine.projectdemo.AccountView.models;

public class User {
    private String name;
    private String email;
    private String unique_id;
    private String password;
    private String old_password;
    private String new_password;
    private String head;
    private String type;
    private String text;
    private String sno;
    private String longitude;
    private String latitude;
    private String infoselect;
    private String note;

    public String getSno() {
        return sno;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTexts(String text) {
        this.text = text;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude =latitude;
    }

    public void setInfoselect(String infoselect) {
        this.infoselect =infoselect;
    }

    public void setNote(String note) {
        this.note =note;
    }
}
