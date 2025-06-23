package com.example.gruhmandal;

public class AmenityModel {
    private String Aid;
    private String ctime;
    private String imageUrl;
    private String name;
    private String otime;
    private String status,sid;

    public AmenityModel() {
        //Empty constructor needed for Firebase
    }

    public AmenityModel(String Aid, String name, String otime, String ctime, String status, String imageUrl, String sid) {
        this.Aid = Aid;
        this.name = name;
        this.otime = otime;
        this.ctime = ctime;
        this.status = status;
        this.imageUrl = imageUrl;
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAid() { return Aid; }
    public String getName() { return name; }
    public String getOtime() { return otime; }
    public String getCtime() { return ctime; }
    public String getStatus() { return status; }

    public String getImageUrl() {
        return imageUrl;
    }
}
