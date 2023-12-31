package com.example.carapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.entities.Car;

import java.util.List;

public class CarViewModel extends ViewModel {
    private MutableLiveData<List<Car>> carListLiveData = new MutableLiveData<>();

    public LiveData<List<Car>> getCarListLiveData() {
        return carListLiveData;
    }

    public void setCarList(List<Car> cars) {
        carListLiveData.setValue(cars);
    }
}

