package com.example.gruhmandal;

public class ComplaintModel {
    private String userId, subject, details, status, imageUrl,cid,sid,date;
    private String name, wingno, flatno;
    private long timestamp;

    public ComplaintModel() {}
    public ComplaintModel(String cid, String userId, String subject, String details, String status, String imageUrl,String sid,String date,long timestamp) {
        this.cid = cid;
        this.userId = userId;
        this.subject = subject;
        this.details = details;
        this.status = status;
        this.imageUrl = imageUrl;
        this.sid = sid;
        this.date = date;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getWingno() {
        return wingno;
    }
    public void setWingno(String wingno) {
        this.wingno = wingno;
    }
    public String getFlatno() {
        return flatno;
    }
    public void setFlatno(String flatno) {
        this.flatno = flatno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
