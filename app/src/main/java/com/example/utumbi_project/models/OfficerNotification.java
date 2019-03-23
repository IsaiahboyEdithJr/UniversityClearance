package com.example.utumbi_project.models;

public class OfficerNotification {

    private String userID;
    private String department;
    private String email;
    private String request;

    public OfficerNotification() {
    }

    public OfficerNotification(String userID, String department, String email, String request) {
        this.userID = userID;
        this.department = department;
        this.email = email;
        this.request = request;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
