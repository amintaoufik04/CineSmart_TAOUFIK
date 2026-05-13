package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName(value = "userId", alternate = {"_id", "id", "uid", "user_id", "pk"})
    private String userId;

    @SerializedName(value = "token", alternate = {"accessToken", "jwt", "access_token", "auth_token", "token_key"})
    private String token;

    private String message;
    
    @SerializedName(value = "user", alternate = {"data", "result", "account"})
    private User user;

    public String getToken() {
        if (token != null && !token.isEmpty()) return token;
        if (user != null && user.getToken() != null) return user.getToken();
        if (user != null && user.getId() != null) return user.getId();
        return null;
    }

    public String getMessage() { return message; }
    public User getUser() { return user; }

    public String getUserId() {
        if (userId != null && !userId.isEmpty()) return userId;
        if (user != null && user.getId() != null) return user.getId();
        return null;
    }

    public static class User {
        @SerializedName(value = "id", alternate = {"_id", "uid", "user_id", "pk"})
        private String id;
        private String email;
        private String name;

        @SerializedName(value = "token", alternate = {"accessToken", "jwt", "access_token", "auth_token"})
        private String token;

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getToken() { return token; }
    }
}
