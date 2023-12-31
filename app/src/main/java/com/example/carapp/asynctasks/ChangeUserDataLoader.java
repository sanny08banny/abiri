package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.R;
import com.example.carapp.services.ChangeCredentialsApi;
import com.example.carapp.utils.IpAddressManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeUserDataLoader extends AsyncTaskLoader<Boolean> {
    private int actionType;  // 0: changePassword, 1: changeUsername, 2: changeFullname
    private String baseUrl,oldPassword,newPassword,newUserName,newFullName;

    public ChangeUserDataLoader(Context context, int actionType) {
        super(context);
        this.actionType = actionType;
        this.baseUrl = IpAddressManager.getIpAddress(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        try {

            String url = baseUrl + "/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ChangeCredentialsApi apiService = retrofit.create(ChangeCredentialsApi.class);
            Call<Void> apiCall = null;
            if (actionType == 0 && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
                apiCall = apiService.changePassword(getCurrentAccountEmail(),oldPassword,newPassword);
            } else if (actionType == 1 && !newUserName.isEmpty()) {
                Log.e(ChangeUserDataLoader.class.getSimpleName(),newUserName);
                apiCall = apiService.changeUsername(getCurrentAccountEmail(),newUserName);
            } else if (actionType == 2 && !newFullName.isEmpty()) {
                apiCall = apiService.changeFullname(getCurrentAccountEmail(),newFullName);
            }

            if (apiCall != null) {
                Response<Void> response = apiCall.execute();
                if (response.isSuccessful()) {
                    if (actionType == 1 && !newUserName.isEmpty()) {
                        // Update the username in the local database
//                        ProfileDatabaseHelper dbHelper = new ProfileDatabaseHelper(getContext());
//                        int rowsAffected = dbHelper.updateUsernameByEmail(getCurrentAccountEmail(), newUserName);
//                        dbHelper.close();

                        return null;
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getCurrentAccountEmail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentEmail", null);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public String getNewFullName() {
        return newFullName;
    }

    public void setNewFullName(String newFullName) {
        this.newFullName = newFullName;
    }
}

