package com.example.cinesmart_taoufik.models;

public class AuthResponse {
    private String token;
    private String message;
    private User user;

    public String getToken() { return token; }
    public String getMessage() { return message; }
    public User getUser() { return user; }

    public static class User {
        private String id;
        private String email;
        private String name;

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
    }
}
