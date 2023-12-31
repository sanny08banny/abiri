package com.example.carapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityAddPhoneNumberBinding;
import com.example.carapp.utils.SimCardManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddPhoneNumberActivity extends AppCompatActivity {

    private ActivityAddPhoneNumberBinding addPhoneNumberBinding;
    private SimCardManager simCardManager;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPhoneNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_phone_number);

        setSupportActionBar(addPhoneNumberBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        addPhoneNumberBinding.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsMenu(addPhoneNumberBinding.optionsButton);
            }
        });

        addPhoneNumberBinding.logoHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        addPhoneNumberBinding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhoneNumber = addPhoneNumberBinding.addPhoneNumberEdittext.getText().toString().trim();

// Validate the input format (7xxxxxxxx)
                if (newPhoneNumber.matches("7\\d{8}")) {
                    // Format the phone number to match the desired format (254input)
                    String formattedPhoneNumber = "254" + newPhoneNumber;
                    SimCardManager.setPhoneNumber(AddPhoneNumberActivity.this,formattedPhoneNumber);
                    if (!SimCardManager.getPhoneNumber(AddPhoneNumberActivity.this).equals("")) {
                        showSnackbar(addPhoneNumberBinding.getRoot(), formattedPhoneNumber + " added successfully");
                        Intent intent = new Intent();
                        intent.putExtra("selectedNo",formattedPhoneNumber);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                } else {
                    addPhoneNumberBinding.addPhoneNumberEdittext.setError("Invalid format. Please use 7xxxxxxxx format.");
                }
            }
        });
    }
    // In your activity or fragment
    private void showSnackbar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.blue));
        snackbar.show();
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
    public void showOptionsMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.signin_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.feed_back) {
                    showFeedbackBottomSheet();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }
    private void showBottomSheetDialog() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}