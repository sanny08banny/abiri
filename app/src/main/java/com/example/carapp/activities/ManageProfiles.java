package com.example.carapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;
import com.example.carapp.asynctasks.ChangeUserDataLoader;
import com.example.carapp.asynctasks.ProfileFetchRunnable;
import com.example.carapp.databasehelpers.DatabaseHelper;
import com.example.carapp.databinding.ActivityManageProfilesBinding;
import com.example.carapp.entities.User;
import com.example.carapp.utils.SimCardManager;
import com.example.carapp.utils.TaxiModeManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ManageProfiles extends AppCompatActivity {

    private static final int PICK_WALLPAPER = 9;
    private MaterialToolbar toolbar;
    private ImageView profileImage;
    private TextView dateJoined;
    private EditText userNameEditText;
    private ImageButton changeUsernameButton;
    private ActivityManageProfilesBinding manageProfilesBinding;
    private ArrayList<String> selectedImagePaths;
    private String selectedImagePath;
    private DatabaseHelper databaseHelper;
    private DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageProfilesBinding = DataBindingUtil.setContentView(this,R.layout.activity_manage_profiles);
        toolbar = findViewById(R.id.manage_profile_toolbar);
        profileImage = findViewById(R.id.profile_image);
        userNameEditText = findViewById(R.id.username_edit_text);
        dateJoined = findViewById(R.id.date_joined);
        changeUsernameButton = findViewById(R.id.change_user_name_button);
        setSupportActionBar(toolbar);
        manageProfilesBinding.manageProfileToolbar.setNavigationOnClickListener(v -> onBackPressed());
        databaseHelper = new DatabaseHelper(this);

        String instruction = getIntent().getStringExtra("instruction");
        if (instruction != null){
            if (instruction.matches("configure")){
                manageProfilesBinding.usernameEditText.setHint("Enter desired username");
                showFirstTimePrompt();
            }
        }
        manageProfilesBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseWallPaper();
            }
        });
        User user = getIntent().getParcelableExtra("profile");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About");
        }
        if (user == null) {
            Glide.with(this)
                    .load(getWallPaper())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                            .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(profileImage);

            if (getCurrentUserName() == null){
                userNameEditText.setHint("Enter desired username");
            }else {
                userNameEditText.setHint(getCurrentUserName());
            }
            dateJoined.setText(getCurrentDateJoined());

            manageProfilesBinding.email.setText(MessageFormat.format("Email: {0}", getCurrentAccountEmail()));

            changeUsernameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeUserName();
                }
            });
        }

        TextView publicViewDescriptionText = findViewById(R.id.public_view_description_text);
        String descriptionText = getString(R.string.public_view_description);

        // Find the index of the clickable text "[CHANGE PREFERENCES]"
        int changePreferencesStartIndex = descriptionText.indexOf("[CHANGE PREFERENCES]");

        // Only proceed if the clickable text is found in the original string
        if (changePreferencesStartIndex != -1) {
            SpannableString spannableString = new SpannableString(descriptionText);

            // Create a ClickableSpan for the clickable text
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // Perform your action here, e.g., open the recommended activity
                    onBackPressed();
                }
            };

            // Set the ClickableSpan to the part of the text that needs to be clickable
            spannableString.setSpan(clickableSpan, changePreferencesStartIndex, changePreferencesStartIndex + "[CHANGE PREFERENCES]".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the modified SpannableString to the TextView
            publicViewDescriptionText.setText(spannableString);
            publicViewDescriptionText.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            // If the clickable text is not found, simply set the original text to the TextView
            publicViewDescriptionText.setText(descriptionText);
        }
        String logOutText = getString(R.string.log_out);

        // Find the index of the clickable text "[CHANGE PREFERENCES]"
        int changePreferencesStartIndex1 = logOutText.indexOf("LOG OUT");

        // Only proceed if the clickable text is found in the original string
        if (changePreferencesStartIndex1 != -1) {
            SpannableString spannableString = new SpannableString(logOutText);

            // Create a ClickableSpan for the clickable text
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // Perform your action here, e.g., open the recommended activity
                    logOut();
                }
            };

            // Set the ClickableSpan to the part of the text that needs to be clickable
            spannableString.setSpan(clickableSpan, changePreferencesStartIndex1,
                    changePreferencesStartIndex1 + "LOG OUT".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the modified SpannableString to the TextView
            manageProfilesBinding.logUtBt.setText(spannableString);
            manageProfilesBinding.logUtBt.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            // If the clickable text is not found, simply set the original text to the TextView
            manageProfilesBinding.logUtBt.setText(logOutText);
        }
    }

    private void logOut() {
        dataBaseHelper = new DatabaseHelper(this);
        User user = dataBaseHelper.getUserById(getCurrentAccountId());
        if (user != null) {
            databaseHelper.deleteUser(Long.parseLong(user.getUserId()));
            setCurrentProfile();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }else {
            if (getCurrentAccountId() != null) {
                setCurrentProfile();
                Toast.makeText(this, "No account", Toast.LENGTH_SHORT).show();
            }
        }
        onBackPressed();
    }
    private void setCurrentProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUserId", "");
        editor.putString("currentAccountType", "");
        editor.putString("currentUserEmail", "");
        editor.putString("currentUserName", "");
        editor.putString("currentDateJoined", "");
        editor.putString("currentUserPassword", "");
        editor.putString("currentProfileImage", "");
        editor.apply();

        if (!SimCardManager.getPhoneNumber(this).equals("")){
            SimCardManager.setPhoneNumber(this,"");
        }
        TaxiModeManager.setTaxiMode(this,false);
    }

    private void changeUserName() {
        String newUsername = userNameEditText.getText().toString().trim();

        if (newUsername.isEmpty()) {
            userNameEditText.setError("Username cannot be empty");
        }
        User user = databaseHelper.getUserById(getCurrentAccountId());
        if (user != null){
            user.setUsername(newUsername);
            databaseHelper.updateUser(user);
            userNameEditText.setText(newUsername);
        }else {
            Toast.makeText(this, "You must have an account to modify username", Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentProfileImage() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileImage", null);
    }
    public String getCurrentUserName() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserName", null);
    }
    public String getCurrentDateJoined() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentDateJoined", null);
    }
    private void showFirstTimePrompt() {
        // Create and configure an AlertDialog or DialogFragment
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome!");
        builder.setMessage("Please click the image to set your profile picture.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Mark that the user has seen the prompt
            }
        });

        builder.setCancelable(false);
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_WALLPAPER && resultCode == RESULT_OK && data != null) {
            selectedImagePaths = data.getStringArrayListExtra("selectedImagePaths");
            if (selectedImagePaths != null){
                selectedImagePath = selectedImagePaths.get(0);
                updateProfileImage(selectedImagePath);
                setWallPaper(selectedImagePath);

                // Set the selected image path as a tag on the ImageView
//                createAccountBinding.createProfileImage.setTag(selectedImagePath);
            }
        }

    }
    private void chooseWallPaper() {
        Intent intent = new Intent(ManageProfiles.this, MediaPickerActivity.class);
        startActivityForResult(intent, PICK_WALLPAPER);
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
    public String getCurrentAccountEmail() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserEmail", null);
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
    private void updateProfileImage(String selectedImage) {
        Glide.with(this)
                .load(selectedImage)
                .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) // Set thedesired width and height for resizing
                .into(manageProfilesBinding.profileImage);
    }
}