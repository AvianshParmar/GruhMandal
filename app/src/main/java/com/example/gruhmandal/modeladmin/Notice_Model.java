package com.example.gruhmandal.modeladmin;

public class Notice_Model {
    private String noticeId;
    private String title;
    private String description;
    private String imageUrl;
    private boolean active;
    private String societyId;
    private String date;  // ✅ Add date
    private String time;
    private String uid;
    private String poll;

    public Notice_Model() {
        // Default constructor required for Firebase
    }

    public Notice_Model(String noticeId, String title, String description, String imageUrl, boolean active, String societyId, String date, String time,String uid,String poll) {
        this.noticeId = noticeId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = active;
        this.societyId = societyId;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.poll = poll;
    }

    public String getPoll() {
        return poll;
    }

    public void setPoll(String poll) {
        this.poll = poll;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSocietyId() {
        return societyId;
    }

    public void setSocietyId(String societyId) {
        this.societyId = societyId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
