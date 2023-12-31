package com.example.carapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.carapp.R;
import com.example.carapp.asynctasks.TokenIdLoader;
import com.example.carapp.databasehelpers.DatabaseHelper;
import com.example.carapp.databinding.ActivityMainBinding;
import com.example.carapp.entities.BookedCar;
import com.example.carapp.entities.User;
import com.example.carapp.fragments.ExtrasFragment;
import com.example.carapp.fragments.MainFragment;
import com.example.carapp.fragments.SearchFragment;
import com.example.carapp.storage.RemoteMessageSaver;
import com.example.carapp.taxi_utils.ClientRequest;
import com.example.carapp.utils.FCMTokenManager;
import com.example.carapp.utils.TaxiModeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private FCMTokenManager fcmTokenManager;
    private ClientRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("ride_id") && getIntent().hasExtra("client_id")) {
                // Type A: Contains ClientRequest details
                for (String key : getIntent().getExtras().keySet()) {
                    Object value = getIntent().getExtras().get(key);
                    Log.d("NotificationData", "Key: " + key + " Value: " + value);
                    // Handle or process the received data here
                }
                 request = extractClientRequestFromIntent(getIntent());
                openDriverMaps(request);
                // Handle Type A notification with ClientRequest details
            } else {
                // Type B: Another form of notification without ClientRequest details
                String notificationMessage = getIntent().getStringExtra("message");
                // Handle Type B notification without ClientRequest details
            }
        }

        // Retrieve the message extra from the intent
        if (getIntent() != null && getIntent().hasExtra("request")) {
            request = getIntent().getParcelableExtra("request");
            if (getIntent().getAction() != null) {
                if (getIntent().getAction().equals("ACCEPT_ACTION")) {
                    request.setStatus("Accepted");
                    openDriverMaps(request);
                } else if (getIntent().getAction().equals("DECLINE_ACTION")) {
                    request.setStatus("Cancelled");
                    showSnackbar(activityMainBinding.getRoot(),
                            "Request decline. Click the button to change preference.");
                }
            } else {
                openDriverMaps(request);

            }
        }

        boolean isLoggedIn = getIntent().getBooleanExtra("signIn", false);
//        loadFragment(new MainFragment(isLoggedIn)); // Load the default fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(activityMainBinding.bottomNavView, navController);
        // Pass isLoggedIn value when navigating to MainFragment
        Bundle bundle = new Bundle();
        bundle.putBoolean("isLoggedIn", isLoggedIn);
        navController.setGraph(R.navigation.nav_graph);
        navController.navigate(R.id.mainFragment, bundle);

        FCMTokenManager.fetchToken(new FCMTokenManager.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.d("Token", token);
                if (isLoggedIn){
                    TokenIdLoader tokenIdLoader = new TokenIdLoader(MainActivity.this, token);
                    tokenIdLoader.forceLoad();
                    tokenIdLoader.registerListener(7, new Loader.OnLoadCompleteListener<String>() {
                        @Override
                        public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                            if (data != null) {

                            }
                        }
                    });
                }
            }
        });

        activityMainBinding.bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    navController.navigate(R.id.mainFragment, bundle);
                    return true;
                } else if (item.getItemId() == R.id.drive) {
                    navController.navigate(R.id.driverMainFragment, bundle);
                    return true;
                } else if (item.getItemId() == R.id.extras) {
                    navController.navigate(R.id.extrasFragment, bundle);
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    navController.navigate(R.id.searchFragment, bundle);
                    return true;
                }
                return false;
            }
        });
        if (getCurrentAccountType() != null) {
            toggleDriverMainFragment(TaxiModeManager.getTaxiMode(this));
        } else {
            toggleDriverMainFragment(false);
        }
        updateUser();
    }

    private void openDriverMaps(ClientRequest request) {
        Intent intent = new Intent(this, TaxiMapsActivity.class);
        intent.putExtra("request", request);
        startActivity(intent);
    }

    private void toggleDriverMainFragment(boolean isInTaxiMode) {
        Menu menu = activityMainBinding.bottomNavView.getMenu();
        MenuItem driverMainMenuItem = menu.findItem(R.id.drive);

        if (driverMainMenuItem != null) {
            driverMainMenuItem.setVisible(isInTaxiMode);
        }
    }

    private void updateUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if (getCurrentAccountId() != null) {
            User user = databaseHelper.getUserById(getCurrentAccountId());
            if (user != null) {
                setCurrentProfile(user);
            }
        }
    }

    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }

    public String getCurrentPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserPassword", null);
    }

    public String getCurrentEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserEmail", null);
    }

    public String getCurrentAccountUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserName", null);
    }

    public String getCurrentAccountType() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentAccountType", null);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void setCurrentProfile(User selectedProfile) {
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUserId", selectedProfile.getUserId());
        editor.putString("currentAccountType", selectedProfile.getAccountType());
        editor.putString("currentUserEmail", selectedProfile.getEmail());
        editor.putString("currentUserName", selectedProfile.getUsername());
        editor.putString("currentDateJoined", selectedProfile.getDateCreated());
        editor.putString("currentUserPassword", selectedProfile.getPassword());
        editor.putString("currentProfileImage", selectedProfile.getProfilePic());
        editor.apply();
    }

    private void showSnackbar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.blue));
        snackbar.setAction("Change", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.show();
    }
    private void showTaxiConfirmationDialog(ClientRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Client request");
        builder.setMessage("You have a new ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private ClientRequest extractClientRequestFromIntent(Intent intent) {
        ClientRequest clientRequest = new ClientRequest();

        clientRequest.setRide_id(intent.getStringExtra("ride_id"));
        clientRequest.setClient_id(intent.getStringExtra("client_id"));
        clientRequest.setUser_phone(intent.getStringExtra("user_phone"));
        clientRequest.setUser_name(intent.getStringExtra("user_name"));
        clientRequest.setDest_lat(Float.parseFloat(intent.getStringExtra("dest_lat")));
        clientRequest.setDest_lon(Float.parseFloat(intent.getStringExtra("dest_lon")));
        clientRequest.setCurrent_lat(Float.parseFloat(intent.getStringExtra("current_lat")));
        clientRequest.setCurrent_lon(Float.parseFloat(intent.getStringExtra("current_lon")));

        Log.e(MainActivity.class.getSimpleName(), String.valueOf(clientRequest.getDest_lat()));
        return clientRequest;
    }
}