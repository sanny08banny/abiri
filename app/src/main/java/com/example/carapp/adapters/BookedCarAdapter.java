package com.example.carapp.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;
import com.example.carapp.activities.BookedActivity;
import com.example.carapp.asynctasks.BookCarLoader;
import com.example.carapp.databasehelpers.BookedCarsDatabaseHelper;
import com.example.carapp.databinding.BookedCarItemBinding;
import com.example.carapp.entities.BookedCar;
import com.example.carapp.enums.ActionType;
import com.example.carapp.utils.IpAddressManager;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class BookedCarAdapter extends RecyclerView.Adapter<BookedCarAdapter.BookedCarViewHolder> {
    private List<BookedCar> cars;
    private Context context;
    private OnItemClickListener listener;
    private String baseUrl;
    private BookedCarsDatabaseHelper databaseHelper;

    public BookedCarAdapter(List<BookedCar> cars, Context context) {
        this.cars = cars;
        this.context = context;
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.databaseHelper = new BookedCarsDatabaseHelper(context);
    }

    public void setItems(List<BookedCar> data) {
        cars.clear();
        cars.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookedCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BookedCarItemBinding bookedCarItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.booked_car_item, parent, false);
        return new BookedCarViewHolder(bookedCarItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedCarViewHolder holder, int position) {
        BookedCar bookedCar = cars.get(position);

        holder.bind(bookedCar);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class BookedCarViewHolder extends RecyclerView.ViewHolder {
        private BookedCarItemBinding bookedCarItemBinding;

        public BookedCarViewHolder(@NonNull BookedCarItemBinding bookedCarItemBinding) {
            super(bookedCarItemBinding.getRoot());
            this.bookedCarItemBinding = bookedCarItemBinding;

            bookedCarItemBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean isExpanded = bookedCarItemBinding.swipeLayout.getVisibility() == View.VISIBLE;
                    toggleExpansion(bookedCarItemBinding.swipeLayout, isExpanded);
                    return true;
                }
            });
            bookedCarItemBinding.hideButtons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isExpanded = bookedCarItemBinding.swipeLayout.getVisibility() == View.VISIBLE;
                    toggleExpansion(bookedCarItemBinding.swipeLayout, isExpanded);
                }
            });

        }

        public void bind(BookedCar car) {
            if (car.getImage() != null) {
                String image = car.getImage();
                String endPoint = baseUrl + "/car/" + car.getOwner_id() + "/"
                        + car.getCar_id() + "/" + car.getImage();
                Glide.with(context)
                        .asBitmap()
                        .load(endPoint)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                                .error(R.drawable.car_01)      // Error image if loading fails
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .override(ViewGroup.LayoutParams.MATCH_PARENT, 500)
                        .into(bookedCarItemBinding.carImage);
            }
            String rentalDuration = String.valueOf(car.getDuration());
            String attendantName = "[CHANGE PREFERENCES]"; // Replace with the actual method to get the name

// Create a SpannableString for the rental price
            SpannableString rentalDurationSpannable = new SpannableString(rentalDuration);
            ClickableSpan rentalPriceClickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle click action for the rental price here
                    // For example, you can open a dialog or perform some action
                }
            };
            rentalDurationSpannable.setSpan(rentalPriceClickSpan, 0, rentalDuration.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

// Create a SpannableString for the attendant name
            SpannableString attendantNameSpannable = new SpannableString(attendantName);
            ClickableSpan attendantNameClickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    boolean isExpanded = bookedCarItemBinding.swipeLayout.getVisibility() == View.VISIBLE;
                    toggleExpansion(bookedCarItemBinding.swipeLayout, isExpanded);
                }
            };
            attendantNameSpannable.setSpan(attendantNameClickSpan, 0, attendantName.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

// Create a SpannableStringBuilder to combine the formatted text
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Car rented for ");
            builder.append(rentalDurationSpannable);
            builder.append(". Long click on this item to change preferences.");
            builder.append(attendantNameSpannable);

            bookedCarItemBinding.carDescription.setText(builder);

            bookedCarItemBinding.carDescription.setMovementMethod(LinkMovementMethod.getInstance());

            if (car.getPricing() != null) {
                double amount = Double.parseDouble(car.getPricing());
                Locale kenyanLocale = new Locale("sw", "KE");
                Currency kenyanShilling = Currency.getInstance("KES");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
                numberFormat.setCurrency(kenyanShilling);
                String formattedAmount = numberFormat.format(amount);

                bookedCarItemBinding.price.setText(formattedAmount);
            }

            bookedCarItemBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(car);
                }
            });
        }

        private void deleteCar(BookedCar car) {
            BookCarLoader bookCarLoader = new BookCarLoader(context, car.getCar_id(), ActionType.DELETE);
            showLoadingState();
            bookCarLoader.forceLoad();
            bookCarLoader.registerListener(7, new Loader.OnLoadCompleteListener<String>() {
                @Override
                public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                    hideLoadingState();
                    if (data != null) {
                        Toast.makeText(context, "Successful connect", Toast.LENGTH_SHORT).show();
                        databaseHelper.deleteBookedCarByCarId(car.getCar_id());
                        cars.remove(car);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        private void showLoadingState() {
            bookedCarItemBinding.deleteButton.setVisibility(View.GONE);
        }

        private void hideLoadingState() {
            bookedCarItemBinding.deleteButton.setVisibility(View.VISIBLE);
        }

        private void showDeleteConfirmationDialog(BookedCar car) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Car");
            builder.setMessage("Are you sure you want to delete this car?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteCar(car);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    private void toggleExpansion(View expandLayout, boolean isExpanded) {
        if (isExpanded) {
            expandLayout.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 1f, 0f)
                    .setDuration(300)
                    .start();
        } else {
            expandLayout.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(expandLayout, "scaleY", 0f, 1f)
                    .setDuration(300)
                    .start();
        }
    }
}
