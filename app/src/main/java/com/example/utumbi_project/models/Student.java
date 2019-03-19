package com.example.utumbi_project.models;

import java.io.Serializable;

public class Student implements Serializable {

    private String name;
    private String regNo;
    private String contact;
    private String imageUrl;
    private String course;
    private String faculty;
    private String campus;
    private String program;

    public Student() {
    }

    public Student(String regNo, String name, String contact, String imageUrl, String course, String faculty, String campus, String program) {
        this.regNo = regNo;
        this.name = name;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.course = course;
        this.faculty = faculty;
        this.campus = campus;
        this.program = program;
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

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
}
