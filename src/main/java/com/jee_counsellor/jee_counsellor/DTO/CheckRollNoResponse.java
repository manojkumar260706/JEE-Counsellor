package com.jee_counsellor.jee_counsellor.dto;

public class CheckRollNoResponse {

    private boolean exists;
    private String rollNo;
    private String name;
    private String message;

    public CheckRollNoResponse() {
    }

    public CheckRollNoResponse(boolean exists, String rollNo, String name, String message) {
        this.exists = exists;
        this.rollNo = rollNo;
        this.name = name;
        this.message = message;
    }

    public static CheckRollNoResponse existing(String rollNo, String name) {
        return new CheckRollNoResponse(true, rollNo, name, "Candidate exists. Please enter your password to log in.");
    }

    public static CheckRollNoResponse newCandidate(String rollNo) {
        return new CheckRollNoResponse(false, rollNo, null, "Candidate does not exist. Please provide registration details.");
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
