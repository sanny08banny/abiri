package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.entities.Car;
import com.example.carapp.services.CarApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchLoader extends AsyncTaskLoader<List<Car>> {
    private static final String TAG = SearchLoader.class.getSimpleName();
    private List<Car> houses;
    private String baseUrl;

    public SearchLoader(Context context) {
        super(context);
        this.baseUrl = this.baseUrl = IpAddressManager.getIpAddress(context) + "/";
    }

    @Override
    protected void onStartLoading() {
        if (houses != null) {
            deliverResult(houses);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Car> loadInBackground() {
        try {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            final String username = "Sanny 08 Banny";
            final String password = "200Pilot";

            // Create an Interceptor to add the authentication header
            Interceptor interceptor = chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", Credentials.basic(username, password))
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            };

            httpClient.addInterceptor(interceptor);

            String url = baseUrl;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CarApiService service = retrofit.create(CarApiService.class);

            Call<ArrayList<Car>> call = service.getCars();
            Response<ArrayList<Car>> response = call.execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e(TAG, "Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deliverResult(List<Car> data) {
        houses = data;
        super.deliverResult(data);
    }

    public String getCurrentAccountEmail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentEmail", null);
    }
}


