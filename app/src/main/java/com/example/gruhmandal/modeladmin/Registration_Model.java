package com.example.gruhmandal.modeladmin;

public class Registration_Model {
    private String userId;
    private String sid;
    private String wingno;
    private String name;
    private String flatno;
    private String mobile;
    private String email;
    private String status,role, fcmToken;

    public Registration_Model() {
    }

    public Registration_Model(String userId, String sid, String wingno, String name, String flatno, String mobile, String email, String status,String role,String fcmToken ) {
        this.userId = userId;
        this.sid = sid;
        this.wingno = wingno;
        this.name = name;
        this.flatno = flatno;
        this.mobile = mobile;
        this.email = email;
        this.status = status;
        this.role = role;
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlatno() {
        return flatno;
    }
    public void setFlatno(String flatno) {
        this.flatno = flatno;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWingno() {
        return wingno;
    }

    public void setWingno(String wingno) {
        this.wingno = wingno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
