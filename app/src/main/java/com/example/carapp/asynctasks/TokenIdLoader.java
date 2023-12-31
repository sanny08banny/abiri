package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.services.UserApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenIdLoader extends AsyncTaskLoader<String> {
    private static final String TAG = TokenIdLoader.class.getSimpleName();
    private String baseUrl;
    private String newToken;

    public TokenIdLoader(@NonNull Context context, String newToken) {
        super(context);
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.newToken = newToken;
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

            UserApiService service = retrofit.create(UserApiService.class);

            Call<Void> call = service.updateToken(getCurrentAccountId(),newToken);
            Response<Void> response = call.execute();
            if (response.isSuccessful()) {
                // Handle the successful response for booking
                return "Success";
            } else {
                Log.e(TAG, "Error: " + response.code() + " - " + response.message());
                // Handle the error response for booking
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
