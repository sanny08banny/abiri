package com.example.carapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carapp.R;
import com.example.carapp.adapters.SearchHistoryAdapter;
import com.example.carapp.asynctasks.CarsRetrieverLoader;
import com.example.carapp.databinding.ActivitySearchBinding;
import com.example.carapp.entities.Car;
import com.example.carapp.utils.SearchHistoryManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Car>>{

    private static final int VOICE_REQUEST_CODE = 7;
    private ActivitySearchBinding searchBinding;
    private SearchHistoryAdapter searchHistoryAdapter;
    private List<Car> cars;
    private List<String> searchHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        searchHistory = SearchHistoryManager.getSearchHistory(this);
        searchHistoryAdapter = new SearchHistoryAdapter(searchHistory,this);

        cars = new ArrayList<>();
        if (searchHistory != null) {
            searchBinding.searchResultsRecyclerView.setAdapter(searchHistoryAdapter);
        }else {
        }

        searchBinding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBinding.voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });

        searchBinding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchHouses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fetchHouses(String string) {
        LoaderManager.getInstance(this).restartLoader(6,null,this);
    }

    @NonNull
    @Override
    public Loader<List<Car>> onCreateLoader(int id, @Nullable Bundle args) {
        return new CarsRetrieverLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Car>> loader, List<Car> data) {
        if (data != null){

        }else {

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Car>> loader) {

    }
    private void setMargin(View view, int marginDp) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginDp,
                getResources().getDisplayMetrics()
        );
        layoutParams.leftMargin = marginPx;
        layoutParams.rightMargin = marginPx;
        view.setLayoutParams(layoutParams);
    }

    private void startVoiceRecognition() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if (voiceIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(voiceIntent, VOICE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null && voiceResults.size() > 0) {
                String spokenText = voiceResults.get(0);
                searchBinding.searchEditText.setText(spokenText);
            }
        }
    }
}