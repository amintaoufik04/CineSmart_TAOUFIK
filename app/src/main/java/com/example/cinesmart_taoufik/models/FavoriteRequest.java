package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class FavoriteRequest {
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("movieId")
    private int movieId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("posterPath")
    private String posterPath;

    public FavoriteRequest(String userId, int movieId, String title, String posterPath) {
        this.userId = userId;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
    }
}
