package com.example.gruhmandal;

public class VehicleModel {
    private String vehicleType;
    private String vehicleNumber;
    private String color;
    private String vehicleId;
    private String vehicleNumberOrColor;

    public VehicleModel() {}

    public VehicleModel(String vehicleId, String vehicleType, String vehicleNumberOrColor) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.vehicleNumberOrColor = vehicleNumberOrColor;
    }

    // Constructor for Bicycle
//    public VehicleModel(String vehicleType, String color, boolean isBicycle) {
//        this.vehicleType = vehicleType;
//        this.color = color;
//        this.vehicleNumber = null;
//    }

    // Getters
    public String getVehicleId() { return vehicleId; }
    public String getVehicleType() { return vehicleType; }
    public String getVehicleNumberOrColor() { return vehicleNumberOrColor; }
}
