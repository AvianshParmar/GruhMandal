package com.example.gruhmandal.modeladmin;

public class ResidentModel {
    private String sid;
    private String name;
    private String mobile;
    private String wingno;
    private String flatno;
    private String family;
    private String vehicles;
    private String role;
    private int familyCount, vehicleCount,totalCount;
    public ResidentModel() {
    }

    public ResidentModel(String sid, String name, String mobile, String family, String vehicles, String flatno, String wingno, String role,int familyCount, int vehicleCount, int totalCount) {
        this.sid = sid;
        this.name = name;
        this.mobile = mobile;
        this.family = family;
        this.vehicles = vehicles;
        this.flatno =flatno;
        this.wingno=wingno;
        this.role =role;
        this.familyCount = familyCount;
        this.vehicleCount = vehicleCount;
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getVehicles() {
        return vehicles;
    }

    public void setVehicles(String vehicles) {
        this.vehicles = vehicles;
    }
    public int getFamilyCount() { return familyCount; }
    public void setFamilyCount(int familyCount) { this.familyCount = familyCount; }

    public int getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(int vehicleCount) { this.vehicleCount = vehicleCount; }

}
