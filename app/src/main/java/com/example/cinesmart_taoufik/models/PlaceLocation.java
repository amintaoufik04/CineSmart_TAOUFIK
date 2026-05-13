package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;

public class PlaceLocation {
    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
