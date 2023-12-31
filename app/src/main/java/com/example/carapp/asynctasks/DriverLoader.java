package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.entities.UserDTO;
import com.example.carapp.enums.ActionType;
import com.example.carapp.enums.TaxiActions;
import com.example.carapp.services.TaxiApiService;
import com.example.carapp.services.UserApiService;
import com.example.carapp.taxi_utils.ClientRequest;
import com.example.carapp.taxi_utils.DriverResponse;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverLoader extends AsyncTaskLoader<String> {
    private static final String TAG = DriverLoader.class.getSimpleName();
    private String baseUrl;
    private ClientRequest request;
    private TaxiActions taxiActions;

    public DriverLoader(@NonNull Context context, ClientRequest request, TaxiActions taxiActions) {
        super(context);
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.request = request;
        this.taxiActions = taxiActions;
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
    public String loadInBackground() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TaxiApiService service = retrofit.create(TaxiApiService.class);
            Log.e(TAG, "UserId " + getCurrentAccountId());

            if (taxiActions == TaxiActions.ACCEPT) {
                DriverResponse driverResponse = new DriverResponse(request.getClient_id(),request.getStatus());
                Call<Void> call = service.acceptRequest(driverResponse);
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    return "success";
                } else {
                    Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                    // Handle the error response for booking
                }
            }else if (taxiActions == TaxiActions.DECLINE) {
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
