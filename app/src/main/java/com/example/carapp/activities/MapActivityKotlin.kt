package com.example.carapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.carapp.R
import com.example.carapp.kotlin_entities.CarKotlin
import com.example.carapp.kotlin_entities.DriverLocation
import com.example.carapp.utils.LocationWebSocketService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapActivityKotlin : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 123
    val driverLocations = mutableListOf<DriverLocation>()

    private lateinit var locationWebSocketService: LocationWebSocketService

    private fun areLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_kotlin)

        val textView: TextView = findViewById(R.id.locations) // Replace with your TextView's ID

        if (areLocationPermissionsGranted()) {

        } else {
            requestLocationPermissions()
        }

        locationWebSocketService = LocationWebSocketService(
                object : LocationWebSocketService.LocationUpdateListener {
                    override fun onConnectionOpened() {
                        // Handle WebSocket connection opened
                        sendCurrentLocation()
                    }

                    override fun onLocationUpdateReceived(updates: String) {
                        try {
                            val jsonObject = JSONObject(updates)

                            if (jsonObject.has("driver")) {
                                // Handle a single object case
                                val driver = jsonObject.getString("driver")
                                val locationObject = jsonObject.getJSONObject("location")
                                val latitude = locationObject.getDouble("lat")
                                val longitude = locationObject.getDouble("lng")

                                val location = CarKotlin.Location(latitude, longitude)
                                val driverLocation = DriverLocation(driver, location)

                                driverLocations.add(driverLocation)
                            } else {
                                // Handle array of objects
                                val jsonArray = JSONArray(updates)

                                for (i in 0 until jsonArray.length()) {
                                    val obj = jsonArray.getJSONObject(i)
                                    val driver = obj.getString("driver")
                                    val locationObject = obj.getJSONObject("location")
                                    val latitude = locationObject.getDouble("lat")
                                    val longitude = locationObject.getDouble("lng")

                                    val location = CarKotlin.Location(latitude, longitude)
                                    val driverLocation = DriverLocation(driver, location)
                                    driverLocations.add(driverLocation)
                                }
                            }
                            textView.text = driverLocations.toString()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            // Handle JSON parsing exception
                        }
                    }


                    override fun onConnectionFailure(t: Throwable) {
                        // Handle WebSocket connection failure/error
                    }

                    override fun onConnectionClosed() {
                        // Handle WebSocket connection closed
                    }
                })
        getUpdatedLocationForDriver()
    }

    private fun requestLocationPermissions() {
        // Define an array of permissions to request
        val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Request permissions from the user
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // Check if the requested permissions are granted
            if (areLocationPermissionsGranted()) {
                // Permissions have been granted, proceed with map initialization
            } else {
                // Permissions were not granted, you may want to handle this situation accordingly (e.g., show a message to the user)
            }
        }
    }

    fun calculateDistance(location1: Location, location2: Location): Double {
        val R = 6371 // Radius of the Earth in kilometers

        val lat1 = location1.latitude
        val lon1 = location1.longitude
        val lat2 = location2.latitude
        val lon2 = location2.longitude

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in kilometers
    }

    private fun getUpdatedLocationForDriver() {
        locationWebSocketService.startWebSocket()
        // Simulating waiting for updates for a certain period before stopping the connection
        runBlocking {
            delay(120000) // Simulating waiting for updates for 10 seconds
        }

//        locationWebSocketService.stopWebSocket()
    }

    private fun sendCurrentLocation() {
        val locationListener = LocationListener { location ->
            val latitude = location.latitude
            val longitude = location.longitude

//                val locationString = "$latitude,$longitude"
            val locationString = "{lat:$latitude,lon:$longitude}"

            Log.d("MapActivity", "Sent location: $locationString")

            locationWebSocketService.sendMessage(locationString)

            Log.d("MapActivity1", "Sent location: $locationString")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationWebSocketService.stopWebSocket()
    }
}