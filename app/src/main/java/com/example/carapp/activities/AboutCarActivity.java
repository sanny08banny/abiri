package com.example.carapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carapp.R;
import com.example.carapp.asynctasks.BookCarLoader;
import com.example.carapp.asynctasks.ReviewLoader;
import com.example.carapp.databasehelpers.BookedCarsDatabaseHelper;
import com.example.carapp.databinding.ActivityAboutCarBinding;
import com.example.carapp.dialogs.ProgressDialogFragment;
import com.example.carapp.entities.BookedCar;
import com.example.carapp.entities.Car;
import com.example.carapp.enums.ActionType;
import com.example.carapp.enums.ReviewAction;
import com.example.carapp.review.CarReviewResponse;
import com.example.carapp.utils.ImagePagerAdapter;
import com.example.carapp.utils.IpAddressManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AboutCarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {
    private ActivityAboutCarBinding aboutCarBinding;
    private Car car;
    private ProgressDialogFragment progressDialogFragment;
    private BookedCarsDatabaseHelper databaseHelper;
    private String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutCarBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_car);
        setSupportActionBar(aboutCarBinding.toolBar);
        car = getIntent().getParcelableExtra("selectedCar");
        databaseHelper = new BookedCarsDatabaseHelper(this);
        aboutCarBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (car != null) {

            aboutCarBinding.description.setText(car.getDescription());
            aboutCarBinding.model.setText(car.getModel());
            aboutCarBinding.location.setText(car.getLocation());
            double amount = car.getAmount();
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(amount);

            aboutCarBinding.price.setText(formattedAmount);
        }
        ArrayList<String> images = new ArrayList<>();
        String instruction = getIntent().getStringExtra("instruction");
        if (instruction != null && instruction.equals("local")){
            images.addAll(car.getCar_images());
        }else {
            for (String filePath : car.getCar_images()) {
                String baseUrl = IpAddressManager.getIpAddress(this);
                String endPoint = baseUrl + "/car/" + car.getOwner_id() + "/"
                        + car.getCar_id() + "/" + filePath;
                images.add(endPoint);
            }
        }

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
        aboutCarBinding.houseImagesViewPager.setAdapter(adapter);
        removeDots();
        createDotsIndicator(car, car.getCar_images());
        checkBookStatus();

        aboutCarBinding.findCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(AboutCarActivity.this, car);
            }
        });
        loadRating();
    }
    private void loadRating() {
        LoaderManager.getInstance(this).initLoader(1, null, this);
    }

    private void checkBookStatus() {
        BookedCar bookedCar = databaseHelper.getBookedCarByCarId(car.getCar_id());
        if (bookedCar != null){
            aboutCarBinding.toolBar.setTitle(bookedCar.getDuration());
        }else {
            aboutCarBinding.toolBar.setTitle("");
        }
    }

    private void createDotsIndicator(Car car, ArrayList<String> imageResources) {
        ImageView[] dots = new ImageView[imageResources.size()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.ic_dot_unselected); // Use your unselected dot drawable
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0); // Adjust margin as needed
            aboutCarBinding.dotsIndicator.addView(dots[i], params);
        }
    }

    private void updateDotsIndicator(int position) {
        for (int i = 0; i < aboutCarBinding.dotsIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) aboutCarBinding.dotsIndicator.getChildAt(i);
            dot.setImageResource(i == position ? R.drawable.ic_dot_selected : R.drawable.ic_dot_unselected);
        }
    }

    public void removeDots() {
        aboutCarBinding.dotsIndicator.removeAllViews();
    }

    private int calculateDuration(MaterialTimePicker fromTimePicker, MaterialTimePicker toTimePicker) {
        int fromHour = fromTimePicker.getHour();
        int fromMinute = fromTimePicker.getMinute();
        int toHour = toTimePicker.getHour();
        int toMinute = toTimePicker.getMinute();

        // Calculate the duration in minutes
        int durationInMinutes = (toHour - fromHour) * 60 + (toMinute - fromMinute);

        return durationInMinutes;
    }

    private void showDatePickerDialog(Context context, Car car) {
        // Create a MaterialDatePicker for selecting a date range
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .setSelection(Pair.create(System.currentTimeMillis(), System.currentTimeMillis())) // Initial selection (today)
                .build();

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                long fromDateMillis = selection.first;
                long toDateMillis = selection.second;

                // Convert milliseconds to a duration string
                long durationMillis = toDateMillis - fromDateMillis;
                long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
                long hours = TimeUnit.MILLISECONDS.toHours(durationMillis) - TimeUnit.DAYS.toHours(days);
                duration = String.format(Locale.US, "%d days", days);

                // Call the bookCar method with the car and duration
                bookCar(car.getCar_id());
            }
        });


        picker.show(((AppCompatActivity) context).getSupportFragmentManager(), picker.toString());
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
                    Toast.makeText(AboutCarActivity.this, "Successful booking", Toast.LENGTH_SHORT).show();

                    BookedCar bookedCar = new BookedCar();
                    bookedCar.setCar_id(car.getCar_id());
                    bookedCar.setOwner_id(car.getOwner_id());
                    bookedCar.setDuration(duration);
                    bookedCar.setImage(car.getCar_images().get(0));
                    bookedCar.setPricing(String.valueOf(car.getAmount()));

                    boolean isSaved = databaseHelper.insertBookedCar(bookedCar);
                    if (isSaved) {
                        Toast.makeText(AboutCarActivity.this, "Successful save",
                                Toast.LENGTH_SHORT).show();
                        aboutCarBinding.toolBar.setTitle(duration);
                    } else {
                        Toast.makeText(AboutCarActivity.this, "Unsuccessful save", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AboutCarActivity.this, "Unsuccessful booking", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showProgreeBar() {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getSupportFragmentManager(), "progress_dialog");
    }

    private void hideProgreeBar() {
        progressDialogFragment.dismiss();
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        return new ReviewLoader(this,car,null, ReviewAction.GET);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        if (data instanceof CarReviewResponse){
            CarReviewResponse carReviewResponse = (CarReviewResponse) data;
            aboutCarBinding.carRatingBar.setRating((float) carReviewResponse.getAverageRating());
            aboutCarBinding.carRatingText.setText(String.valueOf(carReviewResponse.getAverageRating()));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }
    private static CarReviewResponse deserializeCarReviewResponse(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, CarReviewResponse.class);
    }
}