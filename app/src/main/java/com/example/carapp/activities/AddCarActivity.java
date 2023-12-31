package com.example.carapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.carapp.R;
import com.example.carapp.adapters.PreviewImageAdapter;
import com.example.carapp.asynctasks.CarUploadLoader;
import com.example.carapp.databinding.ActivityAddCarBinding;
import com.example.carapp.entities.Car;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddCarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final int IMAGE_REQUEST = 8;
    private static final int SELECT_LOCATION_REQUEST_CODE = 9;
    private ActivityAddCarBinding addCarBinding;
    private Car newCar;
    private String hourlyPrice, hourlyDownPayment, dailyPrice, dailyDownPayment,
    carId,model, description;
    private ArrayList<String> carImages;
    private ArrayList<String> selectedImagePaths;
    private List<File> selectedFiles = new ArrayList<>();
    private PreviewImageAdapter previewImageAdapter;
    private List<String> selectedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addCarBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_car);

        addCarBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addCarBinding.addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMediaPicker();
            }
        });
        addCarBinding.ltLocationsAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectLocationActivity();
            }
        });

        addCarBinding.submitHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImagePaths == null){
                    showSnackbar(addCarBinding.getRoot(),"You must select at least one image");
                }else {
                    for (String image : selectedImagePaths){
                        File file = new File(image);
                        selectedFiles.add(file);
                    }
                    uploadCar();
                }
            }
        });
    }

    private void uploadCar() {
        model = addCarBinding.modelEdittext.getText().toString();
        carId = addCarBinding.carIdEdittext.getText().toString();
        description = addCarBinding.descriptionEdittext.getText().toString();

        dailyPrice = addCarBinding.dailyPriceEdittext.getText().toString();
        dailyDownPayment = addCarBinding.dailyDownPaymentEdittext.getText().toString();
        newCar = new Car(selectedImagePaths,model,carId,getCurrentAccountId(),selectedLocations.get(0),
                description, Double.parseDouble(dailyPrice),
                Double.parseDouble(dailyDownPayment),"");
        LoaderManager.getInstance(this).initLoader(36,null,this);
    }
    private void openSelectLocationActivity() {
        Intent intent = new Intent(AddCarActivity.this, SelectLocationActivity.class);
        // Set extra to indicate multiple selection mode if needed
        intent.putExtra("isMultipleSelection", true);
        startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE);
    }

    private void updateSelectedLocationText() {
        if (selectedLocations != null && !selectedLocations.isEmpty()) {
            String firstLocation = selectedLocations.get(0);
            addCarBinding.tvLocationsAvailable.setText(firstLocation);
        } else {
            addCarBinding.tvLocationsAvailable.setText("No location selected");
        }
    }
    public String getCurrentAccountId() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
    private void openMediaPicker() {
        Intent intent = new Intent(AddCarActivity.this, MediaPickerActivity.class);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            selectedImagePaths = data.getStringArrayListExtra("selectedImagePaths");
            if (selectedImagePaths != null){
                Toast.makeText(this, "Items retrieved: " +
                        selectedImagePaths.size(), Toast.LENGTH_SHORT).show();

                previewImageAdapter = new PreviewImageAdapter(selectedImagePaths,this);
                addCarBinding.imagesRecycler.setAdapter(previewImageAdapter);
                addCarBinding.imagesRecycler.setLayoutManager(new LinearLayoutManager(
                        this,LinearLayoutManager.HORIZONTAL,false));

                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(addCarBinding.imagesRecycler);
            }
        }else if (requestCode == SELECT_LOCATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("selectedLocations")) {
                selectedLocations = data.getStringArrayListExtra("selectedLocations");
                updateSelectedLocationText();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
        showProgressBar();
        return new CarUploadLoader(this,newCar,selectedFiles);
    }

    private void showProgressBar() {
        addCarBinding.progressBar.setVisibility(View.VISIBLE);
        addCarBinding.submitHouse.setVisibility(View.GONE);
    }
    private void hideProgressBar() {
        addCarBinding.progressBar.setVisibility(View.GONE);
        addCarBinding.submitHouse.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        hideProgressBar();
        if (data != null && data){
            showSnackbar(addCarBinding.getRoot(),"Successfully uploaded");
        }else {
            showSnackbar(addCarBinding.getRoot(),"Failed to upload");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

    }
    private void showSnackbar(View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.blue));
        snackbar.show();
    }
}