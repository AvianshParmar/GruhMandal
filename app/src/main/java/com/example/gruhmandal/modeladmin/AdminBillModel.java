package com.example.gruhmandal.modeladmin;

public class AdminBillModel{
    private String name, wing, flatNo, amount, status,fcmToken;

    public AdminBillModel() {}

    public AdminBillModel(String name, String wing, String flatNo, String amount, String status,String fcmToken) {
        this.name = name;
        this.wing = wing;
        this.flatNo = flatNo;
        this.amount = amount;
        this.status = status;
        this.fcmToken = fcmToken;
    }


    public String getName() { return name; }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getWing() { return wing; }
    public String getFlatNo() { return flatNo; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
}
