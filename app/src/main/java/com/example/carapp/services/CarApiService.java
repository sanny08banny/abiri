package com.example.carapp.services;

import com.example.carapp.entities.BookingRequest;
import com.example.carapp.entities.Car;
import com.example.carapp.review.CarReviewResponse;
import com.example.carapp.review.RequestCarReview;
import com.example.carapp.review.Review;
import com.example.carapp.taxi_utils.TaxiRequest;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CarApiService {
    @GET("cars")
    Call<ArrayList<Car>> getCars();
//    @Multipart
//    @POST("car/mult_upload")
//    Call<Void> newCar(@Part("car") CarRequest car, @Part List<MultipartBody.Part> files);
    @Multipart
    @POST("car/mult_upload")
    Call<Void> newCar(@Part("admin_id") String admin_id, @Part("car_id")String car_id,
                      @Part("model") String model,
                      @Part("location") String location, @Part("description")String description,
                      @Part("daily_price")Double daily_price,
                      @Part("daily_down_payment") Double daily_down_payment,
                      @Part MultipartBody.Part... files);

    @POST("car/action")
    Call<Void> bookCar(@Body BookingRequest request);
    @POST("car/create_review")
    Call<Void> createReview(@Body Review review);
    @POST("car/review")
    Call<CarReviewResponse> getCarReview(@Body RequestCarReview review);
}