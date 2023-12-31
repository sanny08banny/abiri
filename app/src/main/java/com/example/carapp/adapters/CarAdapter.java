package com.example.carapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.carapp.R;
import com.example.carapp.activities.AboutCarActivity;
import com.example.carapp.activities.BookedActivity;
import com.example.carapp.databinding.CarItemBinding;
import com.example.carapp.entities.Car;
import com.example.carapp.utils.IpAddressManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private Context context;
    private List<Car> carList;
    private String baseUrl;
    private OnItemClickListener listener;

    public CarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
        this.baseUrl = IpAddressManager.getIpAddress(context);
    }

    public List<Car> getCars() {
        if (carList.size() != 0) {
            return carList;
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(Car item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CarItemBinding carItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.car_item, parent, false);
        return new ViewHolder(carItemBinding);
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
        private CarItemBinding carItemBinding;

        public ViewHolder(@NonNull CarItemBinding carItemBinding) {
            super(carItemBinding.getRoot());
            this.carItemBinding = carItemBinding;
        }

        void bind(Car car) {
            carItemBinding.location.setText(car.getLocation());
            carItemBinding.model.setText(car.getModel());
            carItemBinding.description.setText(car.getDescription());

            if (car.getCar_images() != null) {
                glideImage(car, carItemBinding.imageView, carItemBinding.price);
            }

            // Only daily pricing is available, show the daily booking button
            carItemBinding.price.setText(MessageFormat.format("Book at {0}/day", car.getAmount()));
            carItemBinding.price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(context, car);
                }
            });

            carItemBinding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAboutCar(car);
                }
            });
            carItemBinding.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOptionsMenu(carItemBinding.options,car);
                }
            });

        }

        private void glideImage(Car car, ImageView imageView, View gradientView) {
            if (car != null) {
                String endPoint = baseUrl + "/car/" + car.getOwner_id() + "/"
                        + car.getCar_id() + "/" + car.getCar_images().get(0);
                Glide.with(context).asBitmap().load(endPoint)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                                .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, 500)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Set the loaded bitmap to the ImageView
                                imageView.setImageBitmap(resource);

                                // Retain the original aspect ratio of the image
                                float aspectRatio = (float) resource.getWidth() / resource.getHeight();

                                // Calculate the desired height based on the original aspect ratio
                                int desiredHeight = (int) (imageView.getWidth() / aspectRatio);

                                // Resize the ImageView to the desired height while keeping the width MATCH_PARENT
                                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                                layoutParams.height = desiredHeight;
                                imageView.setLayoutParams(layoutParams);

                                // Generate color palette
                                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@Nullable Palette palette) {
                                        if (palette != null) {
                                            int vibrantColor = palette.getDominantColor(0xFF000000);

                                            // Set the dynamic end color of the gradient overlay
                                            GradientDrawable gradientDrawable = (GradientDrawable) gradientView.getBackground();
                                            gradientDrawable.setColors(new int[]{vibrantColor, vibrantColor});
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Clear any previous loaded resources if needed
                            }
                        });
            }
        }
        private void showOptionsMenu(View view, Car car) {
            Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, view);
            popupMenu.getMenuInflater().inflate(R.menu.item_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.review) {
                    listener.onItemClick(car);
                    return true;
                } else if (itemId == R.id.fav) {
                    // Implement edit action
                    return true;
                } else if (itemId == R.id.wish_list) {
                    // Implement toggle availability action
                    item.setChecked(!item.isChecked());
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        }


    }

    private void openAboutCar(Car car) {
        Intent intent = new Intent(context, AboutCarActivity.class);
        intent.putExtra("selectedCar", car);
        context.startActivity(intent);
    }

    private void bookCar(Car car, String duration) {
        Intent intent = new Intent(context, BookedActivity.class);
        intent.setAction("book car");
        intent.putExtra("car", car);
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

