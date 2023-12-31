package com.example.carapp.services;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChangeCredentialsApi {
    @POST("jaba/users/changePassword")
    Call<Void> changePassword(@Query("email") String email,
                              @Query("currentPassword") String currentPassword,
                              @Query("newPassword") String newPassword);

    @POST("jaba/users/changeUsername")
    Call<Void> changeUsername(@Query("email") String email,
                              @Query("newUserName") String newUserName);

    @POST("jaba/users/changeFullname")
    Call<Void> changeFullname(@Query("email") String email,
                              @Query("newFullname") String newFullName);


}

