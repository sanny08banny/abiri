package com.example.carapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.adapters.MediaAdapter;
import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MediaPickerActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CODE_ADD_DETAILS = 3;
    private static final int REQUEST_CODE_PHOTO_EDITOR = 101;

    private GridView gridView;
    private MediaAdapter mediaAdapter;
    private ImageView selectedPreviewImageView;
    private VideoView previewVideo;
    private MaterialButton selectMultipleButton, continueButton;
    private ArrayList<String> selectedItems;
    private Uri uri;
    private boolean isMultiSelectEnabled = false;
    private boolean isEditMode;
    private int selectedItemsInt = 0;

    // Update the permission request code and permission strings

    // Update the permission request code and permission strings
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    private static final String[] PERMISSIONS_OLD = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.media_picker_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Pick images for preview");
        }

        // Initialize views
        gridView = findViewById(R.id.grid_view);
        selectedPreviewImageView = findViewById(R.id.selectedPreviewImageView);
        selectMultipleButton = findViewById(R.id.selectButton);
        continueButton = findViewById(R.id.media_picker_continue_button);
        previewVideo = findViewById(R.id.selectedPreviewVideoView);

        // Request permission to read external storage if not granted
        selectedItems = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            checkMediaPermissions(PERMISSIONS);
        } else {
            checkMediaPermissions(PERMISSIONS_OLD);
        }

        selectMultipleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMultiSelect();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems == null){
                    Toast.makeText(MediaPickerActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }else {
                    setIntent();
                }
            }
        });
    }

    private void checkMediaPermissions(String[] permissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            loadMediaFiles();
        }
    }

    private void setIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedImagePaths", selectedItems);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(this, "Permission denied. The app won't be able to load media files.", Toast.LENGTH_LONG).show();
        // Alternatively, you can show a dialog to inform the user:
        // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("Permission Denied");
        // builder.setMessage("The app won't be able to load media files without the required permission.");
        // builder.setPositiveButton("OK", null);
        // builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMediaFiles() {
        Uri mediaUri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE};
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(mediaUri, projection, selection, null, sortOrder);

        if (cursor != null) {
            ArrayList<String> filePaths = new ArrayList<>();
            int columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(columnIndexData);
                int mediaType = cursor.getInt(columnIndexData);
                filePaths.add(filePath);
            }
            cursor.close();

            // Set up GridView adapter
            mediaAdapter = new MediaAdapter(this, filePaths);
            gridView.setAdapter(mediaAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isMultiSelectEnabled) {
                        mediaAdapter.toggleItemSelection(position);
                        selectedItems = mediaAdapter.getSelectedItems();
                        Log.d("Media picker activity", "selected items list size: " + selectedItems.size());
                        selectedItemsInt = selectedItems.size();
                        if (selectedItems.isEmpty()) {
                            selectedPreviewImageView.setVisibility(View.GONE);
                        } else {
                            updatePreview(selectedItems.get(selectedItems.size() - 1));
                            selectedPreviewImageView.setVisibility(View.VISIBLE);
                        }
                        selectMultipleButton.setText("Done (" + selectedItems.size() + ")");
                    } else {
                        String selectedItem = mediaAdapter.getItem(position);
                        selectedItems.clear();
                        selectedItems.add(selectedItem);
                        mediaAdapter.notifyDataSetChanged();
                        updatePreview(selectedItem);
                        selectedPreviewImageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void updatePreview(String filePath) {
        if (filePath.endsWith(".mp4") || filePath.endsWith(".3gp") || filePath.endsWith(".mkv")) {
            // Video file selected
            // Show video preview
            selectedPreviewImageView.setVisibility(View.GONE);
            previewVideo.setVideoPath(filePath);
            previewVideo.start();
            previewVideo.setVisibility(View.VISIBLE);
        } else {
            // Image file selected
            // Show image preview
            previewVideo.setVisibility(View.GONE);
            Glide.with(this)
                    .load(filePath)
                    .into(selectedPreviewImageView);
            selectedPreviewImageView.setVisibility(View.VISIBLE);
        }
    }

    private void toggleMultiSelect() {
        isMultiSelectEnabled = !isMultiSelectEnabled;
        selectedItems.clear();

        if (isMultiSelectEnabled) {
            selectMultipleButton.setText(MessageFormat.format("Done ({0})", selectedItemsInt));
        } else {
            selectMultipleButton.setText("Select Multiple");
        }
        mediaAdapter.setMultiSelectMode(isMultiSelectEnabled);
        mediaAdapter.notifyDataSetChanged();
    }

    private void backToActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backToActivity();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                Log.d("PermissionResult", "Permission: " + permission
                        + ", Grant Result: " + grantResult);

                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                loadMediaFiles();
            } else {
                showPermissionDeniedMessage();
            }
        }
    }

}

