package com.example.utumbi_project.models;

import android.support.annotation.NonNull;

public class AdminNotification {

    private String userId;
    private String request;
    private String group;
    private String email;

    public AdminNotification() {
    }

    public AdminNotification(String userId, String request, String group, String email) {
        this.userId = userId;
        this.request = request;
        this.group = group;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Notification(user:%s|request:%s|group:%s)", userId, request, group);
    }
}
