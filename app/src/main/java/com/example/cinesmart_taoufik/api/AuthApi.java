package com.example.cinesmart_taoufik.api;

import com.example.cinesmart_taoufik.models.AuthRequest;
import com.example.cinesmart_taoufik.models.AuthResponse;
import com.example.cinesmart_taoufik.models.FavoriteRequest;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApi {
    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("auth/signup")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("favorites/add")
    Call<ResponseBody> addFavorite(@Header("Authorization") String token, @Body FavoriteRequest request);

    @GET("favorites/{userId}")
    Call<List<FavoriteRequest>> getFavorites(@Header("Authorization") String token, @Path("userId") String userId);

    @DELETE("favorites/{userId}/{movieId}")
    Call<ResponseBody> removeFavorite(@Header("Authorization") String token, @Path("userId") String userId, @Path("movieId") int movieId);
}
