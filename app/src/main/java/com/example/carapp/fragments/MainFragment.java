package com.example.carapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;
import com.example.carapp.activities.AddCarActivity;
import com.example.carapp.activities.MainActivity;
import com.example.carapp.activities.ManageProfiles;
import com.example.carapp.activities.MapsActivity;
import com.example.carapp.activities.ProfileActivity;
import com.example.carapp.activities.SearchActivity;
import com.example.carapp.activities.SignInActivity;
import com.example.carapp.adapters.CarAdapter;
import com.example.carapp.adapters.LogoAdapter;
import com.example.carapp.asynctasks.CarsRetrieverLoader;
import com.example.carapp.asynctasks.ReviewLoader;
import com.example.carapp.databinding.DialogConfirmBinding;
import com.example.carapp.databinding.FragmentMainBinding;
import com.example.carapp.dialogs.MyBottomSheetDialog;
import com.example.carapp.entities.Car;
import com.example.carapp.review.Review;
import com.example.carapp.enums.ReviewAction;
import com.example.carapp.utils.CarUtils;
import com.example.carapp.utils.DataCache;
import com.example.carapp.utils.IpAddressManager;
import com.example.carapp.viewmodels.CarViewModel;
import com.google.android.gms.internal.location.zzau;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Car>>,
        CarAdapter.OnItemClickListener {
    private static final int REQUEST_CODE = 9;
    private FragmentMainBinding fragmentMainBinding;
    private Car car;
    private String baseUrl;
    private CarAdapter carAdapter;
    private List<Car> cars;
    private boolean isLoggedIn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String profileImage;
    private List<String> logoList;
    private CarViewModel carViewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude,currentLongitude;

    public MainFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters

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
        fragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        assert getArguments() != null;
        isLoggedIn = getArguments().getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            if (getCurrentUserName() == null) {
                showConfirmationDialog();
            }else {
                if (getCurrentUserName().equals("")){
                    showConfirmationDialog();
                }
            }
        }
        profileImage = getWallPaper();
        if (getCurrentUserId() == null) {
            showSignInButton();
        } else {
            if (getCurrentUserId().equals("")){
                showSignInButton();
            }else {
                updateProfileImage(profileImage);
            }
        }

        baseUrl = IpAddressManager.getIpAddress(requireContext());
        cars = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        fragmentMainBinding.topCompanies.setLayoutManager(layoutManager);
        logoList = new ArrayList<>();

        LogoAdapter adapter = new LogoAdapter(requireContext(), logoList);
        fragmentMainBinding.topCompanies.setAdapter(adapter);

        carAdapter = new CarAdapter(requireContext(), cars);
        carAdapter.setOnItemClickListener(this);
        fragmentMainBinding.carsRecycler.setAdapter(carAdapter);
        fragmentMainBinding.carsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        carViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // Observe changes in car data
        carViewModel.getCarListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Car>>() {
            @Override
            public void onChanged(List<Car> cars) {
                // Update your UI with the new car data
                // For example, update your RecyclerView or other UI components
                carAdapter.setItems(cars);
            }
        });

        fragmentMainBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });
        fragmentMainBinding.viewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        fragmentMainBinding.findCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findACar();
            }
        });

        fragmentMainBinding.searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaps();
            }
        });

        fragmentMainBinding.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadCars();
            }
        });

        loadCars();
        loadMostRequestedCar("");
        return fragmentMainBinding.getRoot();

    }
    private String formatTime(long timestamp) {
         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
         return sdf.format(new Date(timestamp));
    }
    private void showMaps() {
        Intent intent = new Intent(requireContext(), MapsActivity.class);
        startActivity(intent);
    }
    private void showSignInButton() {
        fragmentMainBinding.viewProfileImage.setVisibility(View.GONE);
        fragmentMainBinding.dpHolder.setVisibility(View.GONE);
        fragmentMainBinding.signInButton.setVisibility(View.VISIBLE);
        fragmentMainBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignInActivity();
            }
        });
    }

    private void openSignInActivity() {
        Intent intent = new Intent(requireContext(), SignInActivity.class);
        startActivity(intent);
    }

    private void findACar() {
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog();
        myBottomSheetDialog.show(getParentFragmentManager(), myBottomSheetDialog.getTag());
    }

    private void loadMostRequestedCar(String endpoint) {
        if (endpoint.matches("")) {
        } else {
            Glide.with(this)
                    .load(endpoint)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                            .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(fragmentMainBinding.imageView);
        }
    }

    private void openProfileActivity() {
        Intent intent = new Intent(requireContext(), ProfileActivity.class);
        intent.setAction("profile");
        startActivity(intent);
    }

    private void openSearchActivity() {
        Intent intent = new Intent(requireContext(), SearchActivity.class);
        intent.setAction("search");
        startActivity(intent);

    }

    private void loadCars() {
        LoaderManager.getInstance(this).initLoader(1, null, this);
    }
    private void reloadCars() {
        LoaderManager.getInstance(this).restartLoader(1, null, this);
    }
    private void uploadCar() {
        Intent intent = new Intent(requireContext(), AddCarActivity.class).setAction("add car");
        startActivity(intent);
    }

    public String getCurrentUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }

    private void showProgressBar() {
        fragmentMainBinding.progressLt.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        fragmentMainBinding.progressLt.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Loader<List<Car>> onCreateLoader(int id, @Nullable Bundle args) {
        showProgressBar();
        return new CarsRetrieverLoader(requireContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Car>> loader, List<Car> data) {
        hideProgressBar();
        hideErrorLayout();
        if (data != null && data.size() != 0) {
            carViewModel.setCarList(data);
            DataCache.saveData(requireContext(), data);
        } else {
            if (DataCache.loadData(requireContext()) != null) {
                carAdapter.setItems(DataCache.loadData(requireContext()));
                long lastUpdatedTime = DataCache.getLastUpdateTime(requireContext());
                if (lastUpdatedTime != 0) {
                    String formattedTime = formatTime(lastUpdatedTime); // Implement your time formatting method
                    fragmentMainBinding.timeText.setText("Last Updated: " + formattedTime);
                }
            } else {
                showErrorLayout();
            }
        }

        if (carAdapter.getCars() == null) {

        } else {
            if (carAdapter.getCars().size() != 0) {
                Car car = CarUtils.getRandomCar(carAdapter.getCars());

                String endPoint = baseUrl + "/car/" + car.getOwner_id() + "/"
                        + car.getCar_id() + "/" + car.getCar_images().get(0);
                updateHeaderTexts(car);
                loadMostRequestedCar(endPoint);
            }
        }
    }

    private void updateHeaderTexts(Car car) {
        fragmentMainBinding.location.setText(car.getLocation());
        fragmentMainBinding.model.setText(car.getModel());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Car>> loader) {

    }

    private void showErrorLayout() {
        fragmentMainBinding.errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        fragmentMainBinding.errorLayout.setVisibility(View.GONE);
    }


    private String getWallPaper() {
        // Get a reference to SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        return sharedPreferences.getString("profilePic", null);
    }

    private void updateProfileImage(String selectedImage) {
        Glide.with(this)
                .load(selectedImage)
                .override(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) // Set thedesired width and height for resizing
                .into(fragmentMainBinding.viewProfileImage);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogConfirmBinding dialogConfirmBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.dialog_confirm, null, false);
        builder.setView(dialogConfirmBinding.getRoot());

        dialogConfirmBinding.dialogMessage.setText("Do you want to continue configuring your account?");

        final AlertDialog dialog = builder.create();

        dialogConfirmBinding.dialogConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the user's choice to continue configuring the account
                dialog.dismiss();
                openAboutAccount();
            }
        });

        dialogConfirmBinding.dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the user's choice to cancel
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openAboutAccount() {
        Intent intent = new Intent(requireContext(), ManageProfiles.class);
        intent.putExtra("instruction", "configure");
        startActivity(intent);
    }

    public String getCurrentUserName() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserName", null);
    }

    private void showReviewBottomSheet(Car item) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_add_review, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Find views in the bottom sheet layout
        RatingBar ratingBar = bottomSheetView.findViewById(R.id.ratingBar);
        TextInputLayout feedbackTextInputLayout = bottomSheetView.findViewById(R.id.feedbackTextInputLayout);
        TextInputEditText feedbackEditText = bottomSheetView.findViewById(R.id.comment);
        MaterialButton submitButton = bottomSheetView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            // Get feedback and rating inputs
            String comment = feedbackEditText.getText().toString();
            float rating = ratingBar.getRating();
            Review review = new Review(getCurrentUserId(), item.getCar_id(), item.getOwner_id(),
                    "", comment, rating, "");

            ReviewLoader reviewLoader = new ReviewLoader(requireContext(), car, review, ReviewAction.CREATE);
            reviewLoader.forceLoad();

            reviewLoader.registerListener(7, new Loader.OnLoadCompleteListener<Object>() {
                @Override
                public void onLoadComplete(@NonNull Loader<Object> loader, @Nullable Object data) {
                    if (data != null) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(requireContext(), "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Review not submitted.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Perform your submission logic here
            // You can send the feedback and rating to a server or store them locally
            // Then dismiss the bottom sheet
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onItemClick(Car item) {
        if (item != null) {
            showReviewBottomSheet(item);
        }
    }
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener((Activity) requireContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                        }
                    }
                });
    }
    public String getCurrentAccountType() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentAccountType", null);
    }

}