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

    @SerializedName("overview")
    private String overview;

    @SerializedName("voteAverage")
    private double voteAverage;

    public FavoriteRequest(String userId, int movieId, String title, String posterPath, String overview, double voteAverage) {
        this.userId = userId;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public String getUserId() { return userId; }
    public int getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public String getOverview() { return overview; }
    public double getVoteAverage() { return voteAverage; }
}
