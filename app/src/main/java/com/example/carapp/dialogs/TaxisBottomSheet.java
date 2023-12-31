package com.example.carapp.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carapp.R;
import com.example.carapp.adapters.TaxiAdapter;
import com.example.carapp.asynctasks.TaxiLoader;
import com.example.carapp.databinding.TaxisLtBinding;
import com.example.carapp.entities.TaxiLocation;
import com.example.carapp.enums.ActionType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class TaxisBottomSheet extends BottomSheetDialogFragment implements TaxiAdapter.OnItemClickListener {
    private TaxisLtBinding taxisLtBinding;
    private List<TaxiLocation> taxiLocations;
    private String string, distance;
    private double currentLatitude, currentLongitude, travelDistance;
    private float dest_lat, dest_lon;
    private TaxiBookingListener bookingListener;

    public TaxisBottomSheet(List<TaxiLocation> taxiLocations, String string, String distance, double currentLatitude, double currentLongitude, double travelDistance, float destLat, float destLon) {
        this.taxiLocations = taxiLocations;
        this.string = string;
        this.distance = distance;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.travelDistance = travelDistance;
        dest_lat = destLat;
        dest_lon = destLon;
    }

    public interface TaxiBookingListener {
        void onBookingResponse(boolean isSuccess, TaxiLocation item);
    }

    public void setBookingListener(TaxiBookingListener listener) {
        this.bookingListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        taxisLtBinding = DataBindingUtil.inflate(inflater, R.layout.taxis_lt, container, false);

        taxisLtBinding.selectedLoc.setText(string);
        taxisLtBinding.distance.setText(distance);
        Log.d("Bottom sheet", String.valueOf(taxiLocations.size()));

        TaxiAdapter taxiAdapter = new TaxiAdapter(taxiLocations, requireContext(),
                currentLatitude, currentLongitude, travelDistance);
        taxisLtBinding.taxisRecycler.setAdapter(taxiAdapter);
        taxisLtBinding.taxisRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        taxiAdapter.setOnItemClickListener(this);

        taxisLtBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return taxisLtBinding.getRoot();
    }

    @Override
    public void onItemClick(TaxiLocation item) {
        TaxiLoader taxiLoader = new TaxiLoader(requireContext(), item.getDriverId(),
                dest_lat, dest_lon, (float) currentLatitude, (float) currentLongitude, ActionType.BOOK);
        taxiLoader.forceLoad();
        taxiLoader.registerListener(4, new Loader.OnLoadCompleteListener<String>() {
            @Override
            public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                boolean isSuccess = (data != null);
                // Notify the activity about the booking response
                if (bookingListener != null) {
                    bookingListener.onBookingResponse(isSuccess,item);
                }
                if (data != null) {
                    Toast.makeText(requireContext(), "Success, wait for response", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
