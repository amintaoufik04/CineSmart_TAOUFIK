package com.example.cinesmart_taoufik.api;

import com.example.cinesmart_taoufik.models.AuthRequest;
import com.example.cinesmart_taoufik.models.AuthResponse;
import com.example.cinesmart_taoufik.models.FavoriteRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("auth/signup")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("favorites/add")
    Call<ResponseBody> addFavorite(@Header("Authorization") String token, @Body FavoriteRequest request);
}
