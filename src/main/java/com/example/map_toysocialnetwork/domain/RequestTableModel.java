package com.example.map_toysocialnetwork.domain;

public class RequestTableModel {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String status;
    private final String date;

    public RequestTableModel(String username, String firstName, String lastName, String status, String date) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}

