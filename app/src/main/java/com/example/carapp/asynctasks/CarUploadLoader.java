package com.example.carapp.asynctasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.databasehelpers.UploadedCarsHelper;
import com.example.carapp.entities.Car;
import com.example.carapp.entities.CarRequest;
import com.example.carapp.services.CarApiService;
import com.example.carapp.utils.IpAddressManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarUploadLoader extends AsyncTaskLoader<Boolean> {
    private static final String TAG = CarUploadLoader.class.getSimpleName();

    private final Car car;
    private final List<File> imageFiles;
    private String baseUrl;

    public CarUploadLoader(Context context, Car car, List<File> imageFiles) {
        super(context);
        this.car = car;
        this.imageFiles = imageFiles;
        this.baseUrl = IpAddressManager.getIpAddress(context) + "/";
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        try {
            // Create a Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build())
                    .build();
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (File file : imageFiles) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                parts.add(part);
            }
            // Create a service interface for your API
            CarApiService carApiService = retrofit.create(CarApiService.class);
            CarRequest carRequest = new CarRequest(car.getOwner_id(),car.getCar_id(), car.getModel(), car.getLocation(),
                    car.getDescription(),String.valueOf(car.getAmount()),
                    String.valueOf(car.getDownpayment_amt()));
            // Make the API call to create a house
            Call<Void> call = carApiService.newCar(carRequest.getAdmin_id(),carRequest.getCar_id(),
                    carRequest.getModel(),carRequest.getLocation(), carRequest.getDescription(),
                    car.getAmount(), car.getDownpayment_amt(), filesToMultipartBodyParts(imageFiles));

            // Execute the call and get the response
            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                // API call was successful
                UploadedCarsHelper uploadedCarsHelper = new UploadedCarsHelper(getContext());
                uploadedCarsHelper.insertCar(car);
                return true;
            } else {
                // API call failed
                Log.e(TAG, "API call failed with HTTP status code: " + response.code());
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making API call: " + e.getMessage());
            return null;
        }
    }
    public static MultipartBody.Part[] filesToMultipartBodyParts(List<File> files) {
        MultipartBody.Part[] parts = new MultipartBody.Part[files.size()];

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file" + i, file.getName(), requestFile);
            parts[i] = part;
        }

        return parts;
    }

}


