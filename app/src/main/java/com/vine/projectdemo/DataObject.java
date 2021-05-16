package com.vine.projectdemo;

public class DataObject {

    String heading,description,value;

    public String getHeading() {
        return heading;
    }

    public String getDescription(){
        return description;
    }

    public String getValue(){
        return value;
    }

    public DataObject(String heading, String description, String value) {
        this.heading = heading;
        this.description = description;
        this.value = value;
    }
}