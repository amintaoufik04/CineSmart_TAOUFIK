package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class PlaceGeometry {
    @SerializedName("location")
    private PlaceLocation location;

    public PlaceLocation getLocation() { return location; }
}
