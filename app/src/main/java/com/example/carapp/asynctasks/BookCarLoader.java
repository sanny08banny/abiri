package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.R;
import com.example.carapp.entities.BookingRequest;
import com.example.carapp.enums.ActionType;
import com.example.carapp.services.CarApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookCarLoader extends AsyncTaskLoader<String> {
    private static final String TAG = BookCarLoader.class.getSimpleName();
    private String baseUrl;
    private String car_id;
    private ActionType actionType;

    public BookCarLoader(@NonNull Context context, String car_id, ActionType actionType) {
        super(context);
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.car_id = car_id;
        this.actionType = actionType;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CarApiService service = retrofit.create(CarApiService.class);
            Log.e(TAG, "UserId " + getCurrentAccountId());
            Log.e(TAG, "CarId " + car_id);

            if (actionType == ActionType.BOOK) {
                BookingRequest bookingRequest = new BookingRequest(getCurrentAccountId(), car_id, "book");
                Call<Void> call = service.bookCar(bookingRequest);
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    return "Booking successful";
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response for booking
                }
            } else if (actionType == ActionType.DELETE) {
                // Perform delete action here
                BookingRequest bookingRequest = new BookingRequest(getCurrentAccountId(), car_id, "unbook");
                Call<Void> call = service.bookCar(bookingRequest);
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    return "Booking successful";
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response for booking
                }
            } else if (actionType == ActionType.UPDATE) {
                // Perform update action here
                // ...
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
