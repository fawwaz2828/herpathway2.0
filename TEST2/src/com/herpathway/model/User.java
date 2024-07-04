package com.herpathway.model;



public class User {
    private String username;
    private String password;
    private String email;
    private Long id;
    private String userType;


    public User() {
    }

    public User(String email, String password, String username, Long id, String userType) {

        this.email = email;
        this.password = password;
        this.username = username;
        this.id = id;
        this.userType = userType;

    }




}
