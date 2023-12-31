package com.example.carapp.services;

import okhttp3.*;
import java.io.IOException;

public class ApiService {
    private final OkHttpClient httpClient = new OkHttpClient();

    public void sendPostRequestWithFormData(String url, String admin_id, String car_id, String model,
                                            String location, String description, String daily_price,
                                            String hourly_price, String daily_down_payment,
                                            String hourly_down_payment) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("admin_id", admin_id)
                .addFormDataPart("car_id", car_id)
                .addFormDataPart("model", model)
                .addFormDataPart("location", location)
                .addFormDataPart("description", description)
                .addFormDataPart("daily_price", daily_price)
                .addFormDataPart("hourly_price", hourly_price)
                .addFormDataPart("daily_down_payment", daily_down_payment)
                .addFormDataPart("hourly_down_payment", hourly_down_payment)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Handle the successful response here
                String responseData = response.body().string();
                System.out.println("Response: " + responseData);
            } else {
                // Handle the error response here
                System.err.println("Request failed: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other API service methods

    public static void main(String[] args) {
        ApiService apiService = new ApiService();
        apiService.sendPostRequestWithFormData("your_api_endpoint", "admin123", "car456", "ModelX", "LocationA", "Description", "50", "5", "20", "2");
    }
}

