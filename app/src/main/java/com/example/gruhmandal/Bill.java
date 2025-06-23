package com.example.gruhmandal;

public class Bill {

    private String billId;
    private double amount;
    private String dueDate;
    private String status;

    // Empty constructor (required for Firebase)
    public Bill(String billId, double amount, String dueDate, String status) {
        this.billId = billId;
        this.amount = amount;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Getters and setters
    public double getAmount() {
        return amount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return (status != null) ? status : "unpaid";
    }
    public String getBillId() {
        return billId;
    }



}
