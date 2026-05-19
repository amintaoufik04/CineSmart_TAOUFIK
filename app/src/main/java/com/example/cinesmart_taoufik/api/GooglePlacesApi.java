package com.example.cinesmart_taoufik.api;

import com.example.cinesmart_taoufik.models.PlacesApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApi {
    @GET("maps/api/place/nearbysearch/json")
    Call<PlacesApiResponse> getNearbyCinemas(
            @Query("location") String location,
            @Query("radius") Integer radius,
            @Query("type") String type,
            @Query("keyword") String keyword,
            @Query("key") String apiKey
    );
}
