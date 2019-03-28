package com.example.utumbi_project.models;

import android.support.annotation.NonNull;

public class Officer {
    private String name;
    private String contact;
    private String profilePicName;
    private String deptName;

    public Officer() {
    }

    public Officer(String name, String contact, String profilePicName, String deptName) {
        this.name = name;
        this.contact = contact;
        this.profilePicName = profilePicName;
        this.deptName = deptName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Officer " + getName();
    }
}
