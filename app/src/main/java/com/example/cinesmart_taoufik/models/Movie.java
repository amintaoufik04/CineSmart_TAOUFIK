package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("poster_path")
    private String posterPath;
    
    @SerializedName("overview")
    private String overview;
    
    @SerializedName("vote_average")
    private double voteAverage;

    public Movie(int id, String title, String posterPath, String overview, double voteAverage) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return "https://image.tmdb.org/t/p/w500" + posterPath; }
    public String getOverview() { return overview; }
    public double getVoteAverage() { return voteAverage; }
}
