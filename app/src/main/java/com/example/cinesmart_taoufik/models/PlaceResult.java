package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class PlaceResult {
    @SerializedName("name")
    private String name;

    @SerializedName("vicinity")
    private String vicinity;

    @SerializedName("geometry")
    private PlaceGeometry geometry;

    @SerializedName("rating")
    private double rating;

    public String getName() { return name; }
    public String getVicinity() { return vicinity; }
    public PlaceGeometry getGeometry() { return geometry; }
    public double getRating() { return rating; }
}
