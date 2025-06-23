package com.example.gruhmandal.modeladmin;

public class Admin_Security_Model {
    private String name, contact, postion,sid,uid,status,securityid;

    public Admin_Security_Model() {
    }

    public Admin_Security_Model(String name, String contact, String postion, String sid, String uid,String status,String securityid) {
        this.name = name;
        this.contact = contact;
        this.postion = postion;
        this.sid = sid;
        this.uid = uid;
        this.status = status;
        this.securityid = securityid;
    }

    public String getStatus() {
        return status;
    }

    public String getSecurityid() {
        return securityid;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getPostion() {
        return postion;
    }

    public String getSid() {
        return sid;
    }

    public String getUid() {
        return uid;
    }
}

