package com.example.carapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.asynctasks.TokensLoader;
import com.example.carapp.databasehelpers.DatabaseHelper;
import com.example.carapp.databinding.ActivityProfileBinding;
import com.example.carapp.entities.User;
import com.example.carapp.enums.TokenAction;
import com.example.carapp.utils.IpAddressManager;
import com.example.carapp.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private ActivityProfileBinding profileBinding;
    private static final int PICK_WALLPAPER = 9;
    private ArrayList<String> selectedImagePaths;
    private String selectedImagePath;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        setSupportActionBar(profileBinding.aboutSchoolToolbar);
        profileBinding.aboutSchoolToolbar.setNavigationOnClickListener(v -> onBackPressed());

        tokenManager = new TokenManager(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        selectedImagePath = getWallPaper();
        updateProfileImage(selectedImagePath);

        profileBinding.changeWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseWallPaper();
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new AccountSettingsFragment())
                .commit();
        updateTokens();

        profileBinding.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(profileBinding.options);
            }
        });
    }

    private void updateTokens() {
        // Retrieve the token amount
        double storedTokenAmount = tokenManager.getTokenAmount();
        if (storedTokenAmount != 0.0){
            profileBinding.tokenBalance.setText(String.valueOf(storedTokenAmount));
        }
        LoaderManager.getInstance(this).initLoader(4,null,this);
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
        Intent intent = new Intent(ProfileActivity.this, MediaPickerActivity.class);
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
        return sharedPreferences.getString("currentEmail", null);
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
                .into(profileBinding.profileImage);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new TokensLoader(this, null, TokenAction.GET);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null){
            double tokens = Double.parseDouble(data);
            tokenManager.setTokenAmount(tokens);
            profileBinding.tokenBalance.setText(data);
            Toast.makeText(this, "updated :" + data , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public static class AccountSettingsFragment extends PreferenceFragmentCompat {

        private static final int REQUEST_ADD_ACCOUNT = 75;
        private static final int REQUEST_ABOUT_ACCOUNT = 58;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.account_preferences, rootKey);

            Preference addNewAccountPreference = findPreference("add_new_account");
            assert addNewAccountPreference != null;
            addNewAccountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(requireContext(), CreateAccountActivity.class);
                    intent.putExtra("secondaryCall",true);
                    startActivityForResult(intent, REQUEST_ADD_ACCOUNT);
                    return true;
                }
            });
            Preference aboutAccountPreference = findPreference("about_account");
            assert aboutAccountPreference != null;
            aboutAccountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(),ManageProfiles.class);
                    startActivityForResult(intent, REQUEST_ABOUT_ACCOUNT);
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_ADD_ACCOUNT) {
                if (resultCode == Activity.RESULT_OK) {
                    boolean accountCreatedSuccessfully = data.getBooleanExtra("account_created", false);
                    // Handle the result here based on the accountCreatedSuccessfully value
                    showSnackbar(accountCreatedSuccessfully);
                }
            }
        }

        private void showSnackbar(boolean accountCreatedSuccessfully) {
            View view = getView(); // Get the root view of the fragment
            if (view != null) {
                String message = accountCreatedSuccessfully ? "Account created successfully!" : "Failed to create account.";
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }
    public void showChangeIpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_url, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText newIpAddress = dialogView.findViewById(R.id.new_url_edittext);

        dialogBuilder.setTitle("Change base ip address");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newIp = newIpAddress.getText().toString();
                if (newIp.length() != 0){
                    IpAddressManager.setIpAddress(ProfileActivity.this,newIp);
                }else {
                    newIpAddress.setError("Cannot be empty");
                }
                // Show a toast message indicating successful creation
                Toast.makeText(ProfileActivity.this, "Ip changed successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing on cancel
            }
        });
        AlertDialog b = dialogBuilder.create();

        // Apply the animation to the dialog
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up_dialog);
        dialogView.startAnimation(scaleAnimation);

        b.show();
    }
    private void showOptionsMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.change_ip) {
                showChangeIpDialog();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }



}