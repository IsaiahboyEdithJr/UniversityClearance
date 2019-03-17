package com.example.utumbi_project.models;

public class ClearanceModel {

    private String deptName;
    private String clearanceStatus;

    public ClearanceModel(String deptName, String clearanceStatus) {
        this.deptName = deptName;
        this.clearanceStatus = clearanceStatus;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getClearanceStatus() {
        return clearanceStatus;
    }

    public void setClearanceStatus(String clearanceStatus) {
        this.clearanceStatus = clearanceStatus;
    }
}
