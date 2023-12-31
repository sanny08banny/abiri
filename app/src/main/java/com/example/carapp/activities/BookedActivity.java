package com.example.carapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.carapp.R;
import com.example.carapp.adapters.BookedCarAdapter;
import com.example.carapp.asynctasks.BookCarLoader;
import com.example.carapp.databasehelpers.BookedCarsDatabaseHelper;
import com.example.carapp.databinding.ActivityBookedBinding;
import com.example.carapp.dialogs.ProgressDialogFragment;
import com.example.carapp.entities.BookedCar;
import com.example.carapp.entities.Car;
import com.example.carapp.enums.ActionType;

import java.util.ArrayList;
import java.util.List;

public class BookedActivity extends AppCompatActivity {

    private ProgressDialogFragment progressDialogFragment;
    private Car receivedCar;
    private String duration;
    private BookedCarAdapter bookedCarAdapter;
    private List<BookedCar> bookedCars = new ArrayList<>();
    private BookedCarsDatabaseHelper databaseHelper;
    private ActivityBookedBinding bookedBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookedBinding = DataBindingUtil.setContentView(this,R.layout.activity_booked);
        setSupportActionBar(bookedBinding.toolbar);
        bookedBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        databaseHelper = new BookedCarsDatabaseHelper(BookedActivity.this);

        receivedCar = getIntent().getParcelableExtra("car");
        if (receivedCar != null) {
            bookCar(receivedCar.getCar_id());
        }
        String durationString = getIntent().getStringExtra("duration");

        if (durationString != null) {
            if (durationString.contains("days")) {
                // Duration is in the format "X days and Y hours"
                // Parse it to extract the days and hours
                String[] parts = durationString.split(" ");
                int days = Integer.parseInt(parts[0]);
                duration = durationString;
            } else {
                // Duration is in minutes
                int durationInMinutes = Integer.parseInt(durationString);
                duration = durationString;

                // Use 'durationInMinutes' for your logic
            }
        } else {
            // Handle the case where no duration data was received
        }

        if (databaseHelper.getAllBookedCars() != null) {
            bookedCars.addAll(databaseHelper.getAllBookedCars());
        }
        bookedCarAdapter = new BookedCarAdapter(bookedCars,this);
        bookedBinding.bookedCars.setAdapter(bookedCarAdapter);
        bookedBinding.bookedCars.setLayoutManager(new LinearLayoutManager(this));
        if (bookedCars.size() == 0){
            showErrorLayoutNothing();
        }else {
            hideErrorLayout();
        }
    }

    private void showErrorLayoutNothing() {
        bookedBinding.errorLayout.setVisibility(View.VISIBLE);
        bookedBinding.errorText.setText("Nothing to show");
    }

    private void bookCar(String carId) {
        BookCarLoader bookCarLoader = new BookCarLoader(this, carId, ActionType.BOOK);
        showProgreeBar();
        bookCarLoader.forceLoad();
        bookCarLoader.registerListener(7, new Loader.OnLoadCompleteListener<String>() {
            @Override
            public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                hideProgreeBar();
                if (data != null) {
                    Toast.makeText(BookedActivity.this, "Successful booking", Toast.LENGTH_SHORT).show();

                    BookedCar bookedCar = new BookedCar();
                    bookedCar.setCar_id(receivedCar.getCar_id());
                    bookedCar.setOwner_id(receivedCar.getOwner_id());
                    bookedCar.setDuration(duration);
                    bookedCar.setImage(receivedCar.getCar_images().get(0));

                    boolean isSaved = databaseHelper.insertBookedCar(bookedCar);
                    if (isSaved) {
                        bookedCars.add(bookedCar);
                        bookedBinding.bookedCars.smoothScrollToPosition(0);
                        hideErrorLayout();
                        Toast.makeText(BookedActivity.this, "Successful save", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookedActivity.this, "Unsuccessful save", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(BookedActivity.this, "Unsuccessful booking", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showErrorLayout() {
        bookedBinding.errorLayout.setVisibility(View.VISIBLE);
    }
    private void hideErrorLayout() {
        bookedBinding.errorLayout.setVisibility(View.GONE);
    }
    private void showProgreeBar() {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getSupportFragmentManager(), "progress_dialog");
    }

    private void hideProgreeBar() {
        progressDialogFragment.dismiss();
    }

}