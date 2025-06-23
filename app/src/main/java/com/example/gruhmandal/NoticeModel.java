package com.example.gruhmandal;

public class NoticeModel {
    private String title, description,imageUrl,noticeId,date,poll,societyId,time,uid;
    private int agreeCount, notAgreeCount;
    private boolean active;

    public NoticeModel() {
    }

    public NoticeModel(String title, String description, String imageUrl, String noticeId, String date, String poll, String societyId, String time, String uid, int agreeCount, int notAgreeCount, boolean active) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.noticeId = noticeId;
        this.date = date;
        this.poll = poll;
        this.societyId = societyId;
        this.time = time;
        this.uid = uid;
        this.agreeCount = agreeCount;
        this.notAgreeCount = notAgreeCount;
        this.active = active;
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

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoll() {
        return poll;
    }

    public void setPoll(String poll) {
        this.poll = poll;
    }

    public String getSocietyId() {
        return societyId;
    }

    public void setSocietyId(String societyId) {
        this.societyId = societyId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }

    public int getNotAgreeCount() {
        return notAgreeCount;
    }

    public void setNotAgreeCount(int notAgreeCount) {
        this.notAgreeCount = notAgreeCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

