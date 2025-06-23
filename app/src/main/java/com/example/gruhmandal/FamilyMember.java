package com.example.gruhmandal;

public class FamilyMember {
    String name,relation, age, gender, famId;
    public FamilyMember() { }
    public FamilyMember(String famId, String name,String relation, String age, String gender) {
        this.famId = famId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.relation = relation;
    }

    public String getFamId() {
        return famId;
    }

    public void setFamId(String famId) {
        this.famId = famId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
