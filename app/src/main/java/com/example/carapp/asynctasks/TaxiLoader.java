package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.entities.BookingRequest;
import com.example.carapp.enums.ActionType;
import com.example.carapp.services.CarApiService;
import com.example.carapp.services.TaxiApiService;
import com.example.carapp.taxi_utils.TaxiRequest;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaxiLoader extends AsyncTaskLoader<String> {
    private static final String TAG = TaxiLoader.class.getSimpleName();
    private String baseUrl;
    private String driver_id;
    private float dest_lat,dest_lon;
    private float current_lat,current_lon;
    private ActionType actionType;

    public TaxiLoader(@NonNull Context context, String driver_id,
                      float dest_lat, float dest_lon, float current_lat, float current_lon,
                      ActionType actionType) {
        super(context);
        this.driver_id = driver_id;
        this.dest_lat = dest_lat;
        this.dest_lon = dest_lon;
        this.current_lat = current_lat;
        this.current_lon = current_lon;
        this.actionType = actionType;
        this.baseUrl = IpAddressManager.getIpAddress(context);
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

            TaxiApiService service = retrofit.create(TaxiApiService.class);
            Log.e(TAG, "UserId " + getCurrentAccountId());

            if (actionType == ActionType.BOOK) {
                TaxiRequest taxiRequest = new TaxiRequest();
                taxiRequest.setClient_id(getCurrentAccountId());
                taxiRequest.setDriver_id(driver_id);
                taxiRequest.setCurrent_lat(current_lat);
                taxiRequest.setCurrent_lon(current_lon);
                taxiRequest.setDest_lat(dest_lat);
                taxiRequest.setDest_lon(dest_lon);
                Call<Void> call = service.requestTaxi(taxiRequest);
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
//                BookingRequest bookingRequest = new BookingRequest(getCurrentAccountId(), car_id, "unbook");
//                Call<Void> call = service.bookCar(bookingRequest);
//                Response<Void> response = call.execute();
//                if (response.isSuccessful()) {
//                    // Handle the successful response for booking
//                    return "Booking successful";
//                } else {
//                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
//                    // Handle the error response for booking
//                }
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
