package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.R;
import com.example.carapp.entities.UserDTO;
import com.example.carapp.enums.ActionType;
import com.example.carapp.enums.TokenAction;
import com.example.carapp.services.UserApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokensLoader extends AsyncTaskLoader<String> {
    private static final String TAG = TokensLoader.class.getSimpleName();
    private String baseUrl;
    private Double tokens;
    private TokenAction actionType;

    public TokensLoader(@NonNull Context context, Double tokens, TokenAction actionType) {
        super(context);
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.tokens = tokens;
        this.actionType = actionType;
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
        try{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService service = retrofit.create(UserApiService.class);
        Log.e(TAG, "UserId " + getCurrentAccountId());
        Log.e(TAG, "CarId " + tokens);

        if (actionType == TokenAction.GET) {
                Call<Float> call = service.getTokens(getCurrentAccountId());
                Response<Float> response = call.execute();
                if (response.isSuccessful()) {
                    // Handle the successful response for booking
                    return response.body().toString();
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
