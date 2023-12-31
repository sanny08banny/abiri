package com.example.carapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;


import com.example.carapp.R;
import com.example.carapp.asynctasks.ProfileFetchRunnable;
import com.example.carapp.databinding.ActivitySignInBinding;
import com.example.carapp.enums.LoginActions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        setSupportActionBar(signInBinding.signInToolbar);
        signInBinding.signInToolbar.setNavigationOnClickListener(v -> onBackPressed());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Already have an account");
        }

        signInBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(signInBinding.signInEmailTextField.getText()).toString();
                Log.e("SignInActivity","Email chosen: " + email);
                String password = Objects.requireNonNull(signInBinding.signInPasswordTextField.getText()).toString();

                if (email.length() == 0|| password.length() == 0){
                    Toast.makeText(SignInActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }else {
                    signInBinding.signInButton.setVisibility(View.GONE);
                    ProfileFetchRunnable profileFetchRunnable = new ProfileFetchRunnable(email, password, SignInActivity.this,
                            signInBinding.progressBar, LoginActions.LOGIN, null);

                    Thread thread = new Thread(profileFetchRunnable);
                    thread.start();
                }
            }
        });
        signInBinding.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(signInBinding.optionsButton);
            }
        });
    }
    private void showOptionsMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.signin_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.new_account) {
                Intent intent = new Intent(SignInActivity.this, CreateAccountActivity.class)
                        .setAction("create account");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            } else if (itemId == R.id.feed_back) {
                showFeedbackBottomSheet();
                return true;
            } else if (itemId == R.id.back) {
               onBackPressed();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
    private void showFeedbackBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_feedback, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Find views in the bottom sheet layout
        RatingBar ratingBar = bottomSheetView.findViewById(R.id.ratingBar);
        TextInputLayout feedbackTextInputLayout = bottomSheetView.findViewById(R.id.feedbackTextInputLayout);
        TextInputEditText feedbackEditText = bottomSheetView.findViewById(R.id.feedbackEditText);
        MaterialButton submitButton = bottomSheetView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            // Get feedback and rating inputs
            String feedback = feedbackEditText.getText().toString();
            float rating = ratingBar.getRating();

            // Perform your submission logic here
            // You can send the feedback and rating to a server or store them locally
            // Then dismiss the bottom sheet
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

}