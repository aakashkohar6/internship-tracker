package com.internshiptracker;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(@JsonProperty("username") String username,
                        @JsonProperty("password") String password){
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername(){
        return username;
    }
}
