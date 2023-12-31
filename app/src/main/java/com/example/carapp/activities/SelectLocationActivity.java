package com.example.carapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.example.carapp.R;
import com.example.carapp.adapters.AddressAdapter;
import com.example.carapp.entities.AddressItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private TextView currentLocationText, errorMessage;
    private ListView nearbyAddressesListView,selectedLocationsRecyclerView;
    private View selectedLocationsLt;
    private AddressAdapter nearbyAddressesAdapter;
    private double currentLongitude;
    private double currentLatitude;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 120;

    private SearchView searchView;
    private ProgressBar progressBar;
    private SearchLocationTask searchTask;

    private boolean isMultipleSelectionEnabled = false;
    private List<AddressItem> selectedLocations = new ArrayList<>();
    private AddressAdapter selectedLocationsAdapter; // For selected locations
    private MaterialToolbar toolbar;
    private MaterialButton proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        toolbar = findViewById(R.id.new_stori_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentLocationText = findViewById(R.id.current_location_text);
        nearbyAddressesListView = findViewById(R.id.locations_list);
        progressBar = findViewById(R.id.locations_progress_bar);
        errorMessage = findViewById(R.id.error_message);
        selectedLocationsLt = findViewById(R.id.selected_locations_lt);
        proceedButton = findViewById(R.id.location_pick_proceed);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        nearbyAddressesAdapter = new AddressAdapter(this);
        nearbyAddressesListView.setAdapter(nearbyAddressesAdapter);
        nearbyAddressesListView.setOnItemClickListener(this);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        // Retrieve the selection type from the intent (default to single selection)
        boolean isMultipleSelection = getIntent().getBooleanExtra("isMultipleSelection", false);
        if (isMultipleSelection) {
            // Enable multiple selection mode if requested
            isMultipleSelectionEnabled = true;
            selectedLocationsRecyclerView = findViewById(R.id.selected_locations_recycler_view);
            selectedLocationsAdapter = new AddressAdapter(this);
            selectedLocationsRecyclerView.setAdapter(selectedLocationsAdapter);
            selectedLocationsAdapter.setAddressItems(selectedLocations);
            selectedLocationsLt.setVisibility(View.VISIBLE);

            proceedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        getLocation();
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                            Geocoder geocoder = new Geocoder(SelectLocationActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                String currentAddress = address.getAddressLine(0);
                                currentLocationText.setText(String.format("Current Location: %s", currentAddress));
                            }

                            searchNearbyAddresses();
                        }
                    }
                });
    }

    private void searchNearbyAddresses() {
        List<Address> nearbyAddresses = getNearbyAddresses(currentLatitude, currentLongitude);
        displayNearbyAddresses(nearbyAddresses);
    }

    private List<Address> getNearbyAddresses(double latitude, double longitude) {
        List<Address> addresses = new ArrayList<>();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    private void displayNearbyAddresses(List<Address> addresses) {
        List<AddressItem> addressItems = new ArrayList<>();
        for (Address address : addresses) {
            double distance = calculateDistance(address.getLatitude(), address.getLongitude());
            AddressItem item = new AddressItem(address.getAddressLine(0), distance);
            addressItems.add(item);
        }
        nearbyAddressesAdapter.setAddressItems(addressItems);
        nearbyAddressesAdapter.notifyDataSetChanged();
    }

    private double calculateDistance(double latitude, double longitude) {
        final double R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(latitude - currentLatitude);
        double lonDistance = Math.toRadians(longitude - currentLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(currentLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (searchTask != null) {
            searchTask.cancelLoad();
        }
        searchTask = new SearchLocationTask(this, query);
        searchTask.registerListener(0, new Loader.OnLoadCompleteListener<List<Address>>() {
            @Override
            public void onLoadComplete(Loader<List<Address>> loader, List<Address> addresses) {
                progressBar.setVisibility(View.GONE);
                if (addresses != null && !addresses.isEmpty()) {
                    displayNearbyAddresses(addresses);
                } else {
                    showError();
                }
            }
        });
        searchTask.startLoading();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (searchTask != null) {
            searchTask.cancelLoad();
        }
        searchTask = new SearchLocationTask(this, newText);
        searchTask.registerListener(0, new Loader.OnLoadCompleteListener<List<Address>>() {
            @Override
            public void onLoadComplete(Loader<List<Address>> loader, List<Address> addresses) {
                progressBar.setVisibility(View.GONE);
                if (addresses != null && !addresses.isEmpty()) {
                    displayNearbyAddresses(addresses);
                } else {
                    showError();
                }
            }
        });
        searchTask.startLoading();
        return true;    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddressItem selectedAddress = nearbyAddressesAdapter.getItem(position);
        if (selectedAddress != null) {
            String address = selectedAddress.getAddress();
            if (isMultipleSelectionEnabled) {
                // If multiple selections are enabled, add the address to the selected locations list
                selectedLocations.add(selectedAddress);
                selectedLocationsAdapter.addAddressItem(selectedAddress);
                selectedLocationsAdapter.notifyDataSetChanged();
                Log.e("Location selection","selected locations: " + selectedLocations.size());
            } else {
                // If single selection is enabled, pass the address back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedAddress", address);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
      proceed();
        super.onBackPressed();
    }

    private void proceed() {
        List<String> selectedAddresses = new ArrayList<>();
        for (AddressItem addressItem : selectedLocations) {
            String address = addressItem.getAddress();
            selectedAddresses.add(address);
        }
        if (isMultipleSelectionEnabled) {
            // If in multiple selection mode, pass back the list of selected locations
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedLocations", new ArrayList<>(selectedAddresses));
            setResult(Activity.RESULT_OK, resultIntent);
        }
        else {
            deliverResults(selectedAddresses);
        }
    }

    private static class SearchLocationTask extends AsyncTaskLoader<List<Address>> {

        private String query;

        public SearchLocationTask(Context context, String query) {
            super(context);
            this.query = query;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Address> loadInBackground() {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(query, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        public void deliverResult(List<Address> data) {
            if (isStarted()) {
                super.deliverResult(data);
            }
        }
    }

    private void showError() {
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText("No location found");
    }

    private void deliverResults(List<String> addresses) {
        Intent resultIntent = new Intent();
        if (isMultipleSelectionEnabled) {
            // In multiple selection mode, pass the list of selected locations
            resultIntent.putStringArrayListExtra("selectedLocations", new ArrayList<>(addresses));
        } else if (!addresses.isEmpty()) {
            // In single selection mode, pass the single selected location
            resultIntent.putExtra("selectedAddress", addresses.get(0));
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}




