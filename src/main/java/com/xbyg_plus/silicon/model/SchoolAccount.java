package com.xbyg_plus.silicon.model;

public class SchoolAccount {
    private String name;
    private String classRoom;
    private int classNo;
    private String id;
    private String password;
    private String HKid;

    public SchoolAccount(String name, String classRoom, int classNo, String id, String password) {
        this.name = name;
        this.classRoom = classRoom;
        this.classNo = classNo;
        this.id = id;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getClassRoom() {
        return this.classRoom;
    }

    public int getClassNo() {
        return this.classNo;
    }

    public String getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getHKid() {
        return HKid;
    }

    public void setHKid(String HKid) {
        this.HKid = HKid;
    }
}
