package com.example.carapp.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.R;
import com.example.carapp.databinding.TaxiItemBinding;
import com.example.carapp.entities.TaxiLocation;
import com.example.carapp.utils.IpAddressManager;


import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.TaxiViewHolder> {
    private List<TaxiLocation> taxiLocations;
    private Context context;
    private OnItemClickListener listener;
    private String baseUrl;
    private double currentLatitude,currentLongitude,travelDistance;

    public TaxiAdapter(List<TaxiLocation> houses, Context context,
                       double currentLatitude, double currentLongitude, double travelDistance) {
        this.taxiLocations = houses;
        this.context = context;
        this.baseUrl = IpAddressManager.getIpAddress(context);
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.travelDistance = travelDistance;
    }
    public void setItems(List<TaxiLocation> data) {
        taxiLocations.clear();
        taxiLocations.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaxiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TaxiItemBinding taxiItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.taxi_item,parent,false);
        return new TaxiViewHolder(taxiItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxiViewHolder holder, int position) {
        TaxiLocation taxiLocation = taxiLocations.get(position);

        holder.bind(taxiLocation);
    }

    @Override
    public int getItemCount() {
        return taxiLocations.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TaxiLocation item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class TaxiViewHolder extends RecyclerView.ViewHolder{
        private TaxiItemBinding taxiItemBinding;

        public TaxiViewHolder(@NonNull TaxiItemBinding taxiItemBinding) {
            super(taxiItemBinding.getRoot());
            this.taxiItemBinding = taxiItemBinding;
        }
        public void bind(TaxiLocation taxiLocation) {
            double distance = calculateDistance(taxiLocation.getLatitude(),taxiLocation.getLongitude()); // Distance in kilometers
            String distanceText = MessageFormat.format("{0} km", distance);

            taxiItemBinding.distance.setText(distanceText);
            taxiItemBinding.price.setText(calculatePrice(travelDistance));

            if (taxiLocation.getSeats() == 3){
                taxiItemBinding.taxiDesc.setText("Economy");
            }

            taxiItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(taxiLocation);
                }
            });
        }
        private double calculateDistance(double latitude, double longitude) {
            float[] results = new float[1];
            Location.distanceBetween(currentLatitude, currentLongitude,
                    latitude, longitude, results);
            double distance = results[0] * 0.001; // Distance in kilometers

            return distance;
        }

    }

    private String calculatePrice(double travelDistance) {
        double price = travelDistance * 50;

        Locale kenyanLocale = new Locale("sw", "KE");
        Currency kenyanShilling = Currency.getInstance("KES");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(kenyanLocale);
        numberFormat.setCurrency(kenyanShilling);
        String formattedAmount = numberFormat.format(price);
        return formattedAmount;
    }
}
