package com.example.gruhmandal;

public class NeighbourModel {
    private String name, mobile, wingno, flatno;

    public NeighbourModel(){
    }
    public NeighbourModel(String name, String mobile, String wingno, String flatno){
        this.name = name;
        this.mobile = mobile;
        this.wingno = wingno;
        this.flatno = flatno;

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
}
