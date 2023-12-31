package com.example.carapp.asynctasks;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.carapp.activities.CreateAccountActivity;
import com.example.carapp.activities.MainActivity;
import com.example.carapp.databasehelpers.DatabaseHelper;
import com.example.carapp.entities.AdminAccessRequest;
import com.example.carapp.entities.User;
import com.example.carapp.entities.UserDTO;
import com.example.carapp.entities.UserLoginResponse;
import com.example.carapp.enums.LoginActions;
import com.example.carapp.services.UserApiService;
import com.example.carapp.utils.IpAddressManager;
import com.example.carapp.utils.TaxiModeManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFetchRunnable implements Runnable {
    private final String email;
    private final String password;
    private final Context context;
    private DatabaseHelper dataBaseHelper;
    private ProgressBar progressBar;
    private boolean isLogin;
    private LoginActions loginActions;
    private UserLoader.ProfileFetchCallback callback;

    public ProfileFetchRunnable(String email, String password, Context context,
                                ProgressBar progressBar, LoginActions loginActions, UserLoader.ProfileFetchCallback callback) {
        this.email = email;
        this.password = password;
        this.context = context;
        this.progressBar = progressBar;
        this.loginActions = loginActions;
        this.callback = callback;
    }

    @Override
    public void run() {
        // Make API call to retrieve the profile account using the provided email
        // Replace the URL with your actual Spring application endpoint
        if (progressBar != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
                }
            });
        }

        String baseUrl = IpAddressManager.getIpAddress(context);
        String apiUrl = baseUrl + "/";
        Log.e("ProfileFetchRunnable", "Email chosen: " + apiUrl);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);
        UserDTO userRequest = new UserDTO(email, password);

        Call<UserLoginResponse> call = null;
        if (loginActions == LoginActions.LOGIN) {
            call = apiService.signInProfileByEmail(userRequest);
        } else if (loginActions == LoginActions.ADMIN_ACCESS) {
            AdminAccessRequest request = new AdminAccessRequest(getCurrentAccountId(), "admin");
            call = apiService.getAdminAccess(request);
        } else if (loginActions == LoginActions.DRIVER_ACCESS) {
            AdminAccessRequest request = new AdminAccessRequest(getCurrentAccountId(), "driver");
            call = apiService.getDriverAccess(request);
        }

        assert call != null;
        call.enqueue(new Callback<UserLoginResponse>() {

            @Override
            public void onResponse(@NonNull Call<UserLoginResponse> call, @NonNull Response<UserLoginResponse> response) {
                UserLoginResponse loginResponse = response.body();
                assert loginResponse != null;
                String userId = loginResponse.getUser_id();
                if (userId != null) {
                    // Save the profile account to the SQLite database
                    showToast("Login successful");
                    saveProfileToDatabase(userId, loginResponse.getIs_admin(), loginResponse.getIs_driver());
                    Log.e("ProfileFetchSuccess", loginResponse.toString());

                    if (callback == null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);// Show the ProgressBar
                                }
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("signIn", true);
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        callback.onProfileFetched(userId);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                showToast("Oops something went wrong !!! " +
                        "Please make sure you have an account");
            }
        });
    }

    private void setCurrentProfile(User selectedProfile) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUserId", selectedProfile.getUserId());
        editor.putString("currentAccountType", selectedProfile.getAccountType());
        editor.putString("currentUserEmail", selectedProfile.getEmail());
        editor.putString("currentUserName", selectedProfile.getUsername());
        editor.putString("currentDateJoined", selectedProfile.getDateCreated());
        editor.putString("currentUserPassword", selectedProfile.getPassword());
        editor.putString("currentProfileImage", selectedProfile.getProfilePic());
        editor.apply();
    }

    private void saveProfileToDatabase(String profileJson, Boolean isAdmin, Boolean isDriver) {
        // Parse the JSON or extract necessary data and save it to the SQLite database
        // Use your preferred database library (e.g., Room, SQLiteOpenHelper, etc.)
        dataBaseHelper = new DatabaseHelper(context);
        Log.e(ProfileFetchRunnable.class.getSimpleName(),String.valueOf(isAdmin) + isDriver);
        User user = dataBaseHelper.getUserById(profileJson);
        if (user == null) {
            User newUser = new User();
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setUserId(profileJson);
            newUser.setDateCreated(String.valueOf(new Date()));

            if (isAdmin) {
                newUser.setAccountType("Admin");
            } else {
                newUser.setAccountType("User");
            }
            if (isDriver) {
                TaxiModeManager.setTaxiMode(context, true);
            }
            setCurrentProfile(newUser);
            String accountSaved = dataBaseHelper.addUser(newUser);
            if (accountSaved != null) {
                Toast.makeText(context, "New user saved here " + accountSaved, Toast.LENGTH_SHORT).show();
            }
        } else {
            user.setUserId(profileJson);
            if (isAdmin != null && isAdmin) {
                user.setAccountType("Admin");
            } else {
                user.setAccountType("User");
            }
            TaxiModeManager.setTaxiMode(context, isDriver != null && isDriver);
            setCurrentProfile(user);
            int accountSavedLocally = dataBaseHelper.updateUser(user);
            Toast.makeText(context, "User saved here " + accountSavedLocally, Toast.LENGTH_SHORT).show();
        }

    }

    private void showToast(final String message) {
        // Show a toast message on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
}