package com.example.carapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingRequest implements Parcelable {
    private String user_id;
    private String car_id;
    private String description;

    public BookingRequest() {
    }

    public BookingRequest(String user_id, String car_id, String description) {
        this.user_id = user_id;
        this.car_id = car_id;
        this.description = description;
    }

    protected BookingRequest(Parcel in) {
        user_id = in.readString();
        car_id = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(car_id);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingRequest> CREATOR = new Creator<BookingRequest>() {
        @Override
        public BookingRequest createFromParcel(Parcel in) {
            return new BookingRequest(in);
        }

        @Override
        public BookingRequest[] newArray(int size) {
            return new BookingRequest[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
