package com.example.carapp.asynctasks;


import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.carapp.entities.DirectionsResponse;
import com.example.carapp.entities.Leg;
import com.example.carapp.entities.Polyline;
import com.example.carapp.entities.Route;
import com.example.carapp.entities.Step;
import com.example.carapp.services.DirectionsService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsLoader extends AsyncTaskLoader<List<LatLng>> {
    private static final String TAG = DirectionsLoader.class.getSimpleName();
    private LatLng source;
    private LatLng destination;

    public DirectionsLoader(Context context, LatLng source, LatLng destination) {
        super(context);
        this.source = source;
        this.destination = destination;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<LatLng> loadInBackground() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsService service = retrofit.create(DirectionsService.class);

        Call<DirectionsResponse> call = service.getDirections(
                source.latitude + "," + source.longitude,
                destination.latitude + "," + destination.longitude,
                "AIzaSyAlGhvKajzrEZiLaY0XfF-yoPzQnxuKtGM"
        );

        try {
            Response<DirectionsResponse> response = call.execute();
            if (response.isSuccessful()) {
                DirectionsResponse directions = response.body();
                if (directions != null) {
                    return parseDirectionsResponse(directions);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"Unsuccessful connection");
        }

        return null;
    }

    private List<LatLng> parseDirectionsResponse(DirectionsResponse response) {
        List<LatLng> routePoints = new ArrayList<>();

        if (response != null && response.getRoutes() != null && !response.getRoutes().isEmpty()) {
            List<Route> routes = response.getRoutes();
            for (Route route : routes) {
                List<Leg> legs = route.getLegs();
                for (Leg leg : legs) {
                    List<Step> steps = leg.getSteps();
                    for (Step step : steps) {
                        Polyline polyline = step.getPolyline();
                        if (polyline != null) {
                            String encodedPoints = polyline.getPoints();
                            List<LatLng> decodedPoints = decodePolyline(encodedPoints);
                            routePoints.addAll(decodedPoints);
                        }
                    }
                }
            }
        }else {
            Log.d(TAG,"No routes");
        }

        return routePoints;
    }

    // Decode polyline points
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> points = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat / 1e5;
            double longitude = lng / 1e5;
            LatLng point = new LatLng(latitude, longitude);
            points.add(point);
        }
        return points;
    }

}
