package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.entities.Car;
import com.example.carapp.review.CarReviewResponse;
import com.example.carapp.review.RequestCarReview;
import com.example.carapp.review.Review;
import com.example.carapp.enums.ReviewAction;
import com.example.carapp.services.CarApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewLoader extends AsyncTaskLoader<Object> {
    private static final String TAG = ReviewLoader.class.getSimpleName();
    private String baseUrl;
    private Car car;
    private Review review;
    private ReviewAction reviewAction;

    public ReviewLoader(@NonNull Context context, Car car, Review review, ReviewAction reviewAction) {
        super(context);
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.car = car;
        this.review = review;
        this.reviewAction = reviewAction;
    }
    public interface ProfileFetchCallback {
        void onProfileFetched(String userId);
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CarApiService service = retrofit.create(CarApiService.class);
            Log.e(TAG, "UserId " + getCurrentAccountId());

            if (reviewAction == ReviewAction.CREATE) {
                Call<Void> call = service.createReview(review);
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    return "success";
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response for booking
                }
            } else if (reviewAction == ReviewAction.DELETE) {
                // Perform delete action here
//                UserDTO userRequest = new UserDTO(email, password);
//                Call<Void> call = service.deleteUser(userRequest);
//                Response<Void> response = call.execute();
//                if (response.isSuccessful()) {
//                    // Handle the successful response for booking
//                    return "User deleted successful";
//                } else {
//                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
//                    // Handle the error response for booking
//                }
            } else if (reviewAction == ReviewAction.UPDATE) {
                // Perform update action here
                // ...
            } else if (reviewAction == ReviewAction.GET) {
                // Perform update action here
                // ...
                RequestCarReview requestCarReview = new RequestCarReview(car.getCar_id(),car.getOwner_id());
                Call<CarReviewResponse> call = service.getCarReview(requestCarReview);
                Response<CarReviewResponse> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    CarReviewResponse carReviewResponse = response.body();
                    return carReviewResponse;
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response for booking
                }

            }
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            // Handle the failure
        }
        return null;
    }

    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
}
