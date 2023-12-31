package com.example.carapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.asynctasks.UserLoader;
import com.example.carapp.databasehelpers.DatabaseHelper;
import com.example.carapp.databinding.ActivityCreateAccountBinding;
import com.example.carapp.entities.User;
import com.example.carapp.enums.ActionType;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 4;
    private ActivityCreateAccountBinding createAccountBinding;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private ArrayList<String> selectedImagePaths;
    private String selectedImagePath;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);

        createAccountBinding.createProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMediaPicker();
            }
        });
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);

        // Set text watcher for password field
        createAccountBinding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher matcher = pattern.matcher(s.toString());
                if (!matcher.matches()) {
                    createAccountBinding.passwordEditText.setError(
                            getResources().getString(R.string.password_must_contain_an_uppercase_and_number));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        createAccountBinding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openMediaPicker() {
        Intent intent = new Intent(CreateAccountActivity.this, MediaPickerActivity.class);
        startActivityForResult(intent,PICK_IMAGE);
    }

    private void setCurrentProfile(User selectedProfile) {
        SharedPreferences sharedPreferences = CreateAccountActivity.this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
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
    public String getCurrentAccountEmail() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentEmail", null);
    }
    private void startShakeAnimation(View view) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        view.startAnimation(shakeAnimation);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImagePaths = data.getStringArrayListExtra("selectedImagePaths");
            if (selectedImagePaths != null) {
                selectedImagePath = selectedImagePaths.get(0);
                Glide.with(this)
                        .load(selectedImagePath)
                        .override(ViewGroup.LayoutParams.WRAP_CONTENT, 300) // Set thedesired width and height for resizing
                        .centerCrop()
                        .into(createAccountBinding.createProfileImage);

                // Set the selected image path as a tag on the ImageView
                createAccountBinding.createProfileImage.setTag(selectedImagePath);
            
        }

            // Set click listener for createAccount button
            createAccountBinding.createProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String profileName = createAccountBinding.profileNameEditText.getText().toString();
                    email = createAccountBinding.emailEditText.getText().toString();
                    password = createAccountBinding.passwordEditText.getText().toString();
                    String accountType = "member";
                    String dateCreated = String.valueOf(new Date());
                        if (profileName.length() == 0 || email.length() == 0 || password.length() == 0) {
                            Toast.makeText(CreateAccountActivity.this,
                                    "Please fill all the fields", Toast.LENGTH_SHORT).show();
//                            createAccountBinding.profileNameEditText.setError("Username cannot be empty");
                         } else {
                            User profile = new User();
                            profile.setUsername(profileName);
                            profile.setProfilePic(selectedImagePath);
                            profile.setUserId(generateUserId());
                            profile.setEmail(email);
                            profile.setPassword(password);
                            profile.setAccountType(accountType);
                            profile.setDateCreated(dateCreated);
                            if (selectedImagePath != null) {
                                File imageFile = new File(selectedImagePath);
                                Uri imageUri = Uri.fromFile(imageFile);
                                Uri uri = Uri.parse(selectedImagePath);
                                createUser(profile);
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "Please select a profile image", Toast.LENGTH_SHORT).show();
                                startShakeAnimation(createAccountBinding.createProfileImage);
                            }
                        }
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveUser(User profile) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        User existingUser = databaseHelper.getUserById(profile.getUserId());
        if (existingUser == null) {
            String accountSavedLocally = databaseHelper.addUser(profile);
            if (accountSavedLocally.length() != 0) {
                Toast.makeText(this, "Account saved successfully", Toast.LENGTH_SHORT).show();
                setCurrentProfile(profile);
                setWallPaper(profile.getProfilePic());
            }
        }else {
            showSnackbar(createAccountBinding.getRoot(),"This user exists");
        }
    }
    private void createUser(User user) {
        UserLoader userLoader = new UserLoader(this, email,password, ActionType.BOOK);
        showProgressBar();
        userLoader.forceLoad();
        userLoader.registerListener(7, new Loader.OnLoadCompleteListener<String>() {
            @Override
            public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                hideProgreeBar();
                if (data != null) {
                    showSnackbar(createAccountBinding.getRoot(),"Successful");
                saveUser(user);
                onBackPressed();
                }else {
                    showSnackbar(createAccountBinding.getRoot(),"Something went wrong");                }
            }
        });
    }

    private void hideProgreeBar() {
        createAccountBinding.createProfileButton.setVisibility(View.VISIBLE);
        createAccountBinding.progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        createAccountBinding.createProfileButton.setVisibility(View.GONE);
        createAccountBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    // Generate a random user ID with a length between 1 and 3 characters
    public static String generateUserId() {
        Random random = new Random();
        int length = random.nextInt(3) + 1; // Random length between 1 and 3
        StringBuilder userIdBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            userIdBuilder.append(randomChar);
        }

        return userIdBuilder.toString();
    }
    private void setWallPaper(String selectedImagePath) {
        // Get a reference to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (getCurrentAccountEmail() != null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            User currentUser = databaseHelper.getUserById(getCurrentAccountId());
            currentUser.setProfilePic(selectedImagePath);
            databaseHelper.updateUser(currentUser);
        }

// Save the chatWallpaper string
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profilePic", selectedImagePath);
        editor.apply();

        String savedChatWallpaper = sharedPreferences.getString("profilePic", "default_dp_path");

    }
    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
    private String getWallPaper() {
        // Get a reference to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        return sharedPreferences.getString("profilePic", "default_dp_path");
    }
}