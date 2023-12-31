package com.example.carapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.carapp.R;
import com.example.carapp.databinding.FragmentDriverMainBinding;
import com.example.carapp.entities.TaxiLocation;
import com.example.carapp.taxi_utils.DriverAvailabilityManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverMainFragment extends Fragment {
    private static final int REQUEST_CODE = 4;
    private FragmentDriverMainBinding driverMainBinding;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private double currentLongitude, currentLatitude;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TaxiLocation taxiLocation;
    private int seat_count;
    private DriverAvailabilityManager availabilityManager;

    public DriverMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverMainFragment newInstance(String param1, String param2) {
        DriverMainFragment fragment = new DriverMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        driverMainBinding = DataBindingUtil.inflate(
                inflater,R.layout.fragment_driver_main, container, false);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("taxi_locations");

        availabilityManager = new DriverAvailabilityManager(requireContext());
        seat_count = availabilityManager.getSeatCount();

        updateSeatCount();
        driverMainBinding.statusSwitch.setChecked(availabilityManager.getAvailabilityStatus());

        driverMainBinding.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatusText(isChecked);
            }
        });

        driverMainBinding.addSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seat_count = seat_count + 1;
                updateSeatCount();
            }
        });

        driverMainBinding.minusSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = seat_count - 1;
                if (newQuantity >= 1) {
                    seat_count = newQuantity;
                    updateSeatCount();
                } else {
                    showSnackbar(driverMainBinding.getRoot(),"Seats cannot be empty");
                }
            }
        });
        return driverMainBinding.getRoot();
    }

    private void updateSeatCount() {
        driverMainBinding.seatCount.setText(String.valueOf(seat_count));
        availabilityManager.saveSeatCount(seat_count);
    }

    private void showSnackbar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue));
        snackbar.show();
    }
    private void updateStatusText(boolean isChecked) {
        if (isChecked) {
            driverMainBinding.statusText.setText("Currently: Available");
            saveTaxiLocationToFirebase();
        } else {
            driverMainBinding.statusText.setText("Currently: Unavailable");
            deleteTaxiLocationFromFirebase();
        }
    }
    private void saveTaxiLocationToFirebase() {
        // Get the driverId, longitude, and latitude
        String driverId = getCurrentAccountId(); // Replace with your driver's ID
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                            taxiLocation = new TaxiLocation(driverId, seat_count, currentLongitude, currentLatitude);
                            reference.child(driverId).setValue(taxiLocation);
                            availabilityManager.saveAvailabilityStatus(true);

                        }
                    }
                });

        // Create a TaxiLocation object

        // Save the TaxiLocation object to Firebase Realtime Database
    }

    private void deleteTaxiLocationFromFirebase() {
        // Get the driverId
        String driverId = getCurrentAccountId(); // Replace with your driver's ID

        // Delete the TaxiLocation object from Firebase Realtime Database
        reference.child(driverId).removeValue();
        availabilityManager.saveAvailabilityStatus(false);
    }
    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
    public String getCurrentPassword() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserPassword", null);
    }
    public String getCurrentEmail() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserEmail", null);
    }
    public String getCurrentAccountUserName() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserName", null);
    }
    public String getCurrentAccountType() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentAccountType", null);
    }
}