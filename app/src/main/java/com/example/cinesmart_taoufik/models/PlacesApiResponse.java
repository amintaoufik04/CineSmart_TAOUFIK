package com.example.cinesmart_taoufik.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlacesApiResponse {
    @SerializedName("results")
    private List<PlaceResult> results;

    @SerializedName("status")
    private String status;

    public List<PlaceResult> getResults() { return results; }
    public String getStatus() { return status; }
}
