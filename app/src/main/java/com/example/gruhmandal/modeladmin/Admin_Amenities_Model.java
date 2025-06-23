package com.example.gruhmandal.modeladmin;

public class Admin_Amenities_Model {
    private String amenitiesId,name,otime,ctime,imageUrl,status,sid,uid,adddate;

    public Admin_Amenities_Model() {
    }

    public Admin_Amenities_Model(String amenitiesId, String name, String otime, String ctime, String imageUrl, String status, String sid, String uid, String adddate) {
        this.amenitiesId = amenitiesId;
        this.name = name;
        this.otime = otime;
        this.ctime = ctime;
        this.imageUrl = imageUrl;
        this.status = status;
        this.sid = sid;
        this.uid = uid;
        this.adddate = adddate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmenitiesId() {
        return amenitiesId;
    }

    public String getName() {
        return name;
    }

    public String getOtime() {
        return otime;
    }

    public String getCtime() {
        return ctime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getSid() {
        return sid;
    }

    public String getUid() {
        return uid;
    }

    public String getAdddate() {
        return adddate;
    }
}
