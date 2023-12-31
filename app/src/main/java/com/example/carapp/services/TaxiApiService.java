package com.example.carapp.services;

import com.example.carapp.taxi_utils.DriverResponse;
import com.example.carapp.taxi_utils.TaxiRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TaxiApiService {
    @POST("req_ride")
    Call<Void> requestTaxi(@Body TaxiRequest taxiRequest);
    @POST("driver_response")
    Call<Void> acceptRequest(@Body DriverResponse response);
}
