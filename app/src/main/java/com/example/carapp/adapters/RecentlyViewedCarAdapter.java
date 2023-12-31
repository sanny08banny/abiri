package com.example.carapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;
import com.example.carapp.activities.AboutCarActivity;
import com.example.carapp.activities.BookedActivity;
import com.example.carapp.databinding.RecentlyViewedCarBinding;
import com.example.carapp.entities.Car;
import com.example.carapp.utils.IpAddressManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecentlyViewedCarAdapter extends RecyclerView.Adapter<RecentlyViewedCarAdapter.ViewHolder> {

    private Context context;
    private List<Car> carList;
    private String baseUrl;

    public RecentlyViewedCarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
        this.baseUrl = IpAddressManager.getIpAddress(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecentlyViewedCarBinding recentlyViewedCarBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.recently_viewed_car,parent,false);
        return new ViewHolder(recentlyViewedCarBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void setItems(List<Car> data) {
        carList.clear();
        carList.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecentlyViewedCarBinding recentlyViewedCarBinding;

        public ViewHolder(@NonNull RecentlyViewedCarBinding recentlyViewedCarBinding) {
            super(recentlyViewedCarBinding.getRoot());
            this.recentlyViewedCarBinding = recentlyViewedCarBinding;
        }
        void bind(Car car){
            recentlyViewedCarBinding.model.setText(car.getModel());

            if (car.getCar_images() != null) {
                glideImage(car, recentlyViewedCarBinding.imageView);
            }

            double amount = car.getAmount();
            Locale kenyanLocale = new Locale("sw", "KE");
            Currency kenyanShilling = Currency.getInstance("KES");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
            numberFormat.setCurrency(kenyanShilling);
            String formattedAmount = numberFormat.format(amount);

            recentlyViewedCarBinding.price.setText(formattedAmount);

            recentlyViewedCarBinding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAboutCar(car);
                }
            });

        }
        private void glideImage(Car car, ImageView imageView) {
            if (car != null) {
                String endPoint = baseUrl + "/car/" + car.getOwner_id() + "/"
                        + car.getCar_id() + "/" + car.getCar_images().get(0);
                Glide.with(context).asBitmap().load(endPoint)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                                .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, 500)
                        .into(imageView);
            }
        }

    }

    private void openAboutCar(Car car) {
        Intent intent = new Intent(context, AboutCarActivity.class);
        intent.putExtra("selectedCar",car);
        intent.putExtra("instruction","local");
        context.startActivity(intent);
    }

    private void bookCar(Car car, String duration) {
        Intent intent = new Intent(context, BookedActivity.class);
        intent.setAction("book car");
        intent.putExtra("car",car);
        intent.putExtra("duration", duration);
        context.startActivity(intent);
    }

    private void showTimePickerDialog(Context context, Car car) {
        // Create two MaterialTimePicker instances for selecting "fromTime" and "toTime"
        MaterialTimePicker fromTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select From Time")
                .build();

        MaterialTimePicker toTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select To Time")
                .build();

        fromTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromHour = fromTimePicker.getHour();
                int fromMinute = fromTimePicker.getMinute();

                // Handle the selected "fromTime"
                // You can now proceed to the next step in the booking process
            }
        });

        toTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int toHour = toTimePicker.getHour();
                int toMinute = toTimePicker.getMinute();

                // Handle the selected "toTime"
                // You can now proceed to the next step in the booking process
                // Calculate the duration based on "fromTime" and "toTime"
                int durationInMinutes = calculateDuration(fromTimePicker, toTimePicker);

                // Call the 'bookCar' method with the car and the calculated duration
                bookCar(car, String.valueOf(durationInMinutes) + " minutes");
            }
        });

        fromTimePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), fromTimePicker.toString());

        // Show the "toTime" picker when the user selects the "fromTime."
        fromTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTimePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), toTimePicker.toString());
            }
        });
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
                String duration = String.format(Locale.US, "%d days", days);

                // Call the bookCar method with the car and duration
                bookCar(car, duration);
            }
        });


        picker.show(((AppCompatActivity) context).getSupportFragmentManager(), picker.toString());
    }
}

