package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("name")
    private String name;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
