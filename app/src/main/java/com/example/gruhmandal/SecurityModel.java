package com.example.gruhmandal;

public class SecurityModel {
    private String name;
    private String contact;
    private String postion;
    private String sid,status;

    public SecurityModel() {
        // Default constructor required for Firebase
    }

    public SecurityModel(String name, String contact, String postion, String sid, String status) {
        this.name = name;
        this.contact = contact;
        this.postion = postion;
        this.sid = sid;
        this.status = status;
    }

    public String getSid() {
        return sid;
    }

    public String getStatus() {
        return status;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getPostion() { return postion; }
}
