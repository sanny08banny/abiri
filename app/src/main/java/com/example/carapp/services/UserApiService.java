package com.example.carapp.services;

import com.example.carapp.entities.AdminAccessRequest;
import com.example.carapp.entities.UserDTO;
import com.example.carapp.entities.UserLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {
    @POST("")
    Call<Void> deleteUser(UserDTO userRequest);
    @POST("user/new")
    Call<Void> createUser(@Body UserDTO userRequest);
    @POST("user/login")
    Call<UserLoginResponse> signInProfileByEmail(@Body UserDTO userRequest);
    @POST("user/tokens")
    Call<Float> getTokens(@Body String currentAccountId);
    @POST("user/admin_req")
    Call<UserLoginResponse> getAdminAccess(@Body AdminAccessRequest request);

    @POST("user/admin_req")
    Call<UserLoginResponse> getDriverAccess(@Body AdminAccessRequest request);
    @GET("token_update/{user_id}/{token}")
    Call<Void> updateToken(@Path("user_id") String currentAccountId, @Path("token") String newToken);
}
