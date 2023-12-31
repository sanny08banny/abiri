package com.example.carapp.taxi_utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ClientRequest implements Parcelable {
    private String ride_id;
    private String client_id;
    private String user_phone;
    private String user_name;
    private float dest_lat,dest_lon;
    private float current_lat,current_lon;
    private String status;

    public ClientRequest() {
    }

    protected ClientRequest(Parcel in) {
        ride_id = in.readString();
        client_id = in.readString();
        user_phone = in.readString();
        user_name = in.readString();
        dest_lat = in.readFloat();
        dest_lon = in.readFloat();
        current_lat = in.readFloat();
        current_lon = in.readFloat();
        status = in.readString();
    }

    public static final Creator<ClientRequest> CREATOR = new Creator<ClientRequest>() {
        @Override
        public ClientRequest createFromParcel(Parcel in) {
            return new ClientRequest(in);
        }

        @Override
        public ClientRequest[] newArray(int size) {
            return new ClientRequest[size];
        }
    };

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getDest_lat() {
        return dest_lat;
    }

    public void setDest_lat(float dest_lat) {
        this.dest_lat = dest_lat;
    }

    public float getDest_lon() {
        return dest_lon;
    }

    public void setDest_lon(float dest_lon) {
        this.dest_lon = dest_lon;
    }

    public float getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(float current_lat) {
        this.current_lat = current_lat;
    }

    public float getCurrent_lon() {
        return current_lon;
    }

    public void setCurrent_lon(float current_lon) {
        this.current_lon = current_lon;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ride_id);
        dest.writeString(client_id);
        dest.writeString(user_phone);
        dest.writeString(user_name);
        dest.writeFloat(dest_lat);
        dest.writeFloat(dest_lon);
        dest.writeFloat(current_lat);
        dest.writeFloat(current_lon);
        dest.writeString(status);
    }
}
