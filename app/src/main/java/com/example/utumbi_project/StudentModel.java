package com.example.utumbi_project;

import java.io.Serializable;

public class StudentModel implements Serializable {

    private String fName;
    private String lName;
    private String contact;
    private String imageUrl;
    private String course;
    private String faculty;
    private String campus;
    private String program;

    public StudentModel() {
    }

    public StudentModel(String fName, String lName, String contact, String imageUrl, String course, String faculty, String campus, String program) {
        this.fName = fName;
        this.lName = lName;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.course = course;
        this.faculty = faculty;
        this.campus = campus;
        this.program = program;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
