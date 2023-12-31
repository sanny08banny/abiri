package com.example.carapp.kotlin_entities
import android.os.Parcel
import android.os.Parcelable

class CarKotlin(
        var car_images: ArrayList<String>,
        var model: String,
        var car_id: String,
        var owner_id: String,
        var location: String, // Assuming location is stored as "latitude,longitude"
        var description: String,
        var amount: Double,
        var downpayment_amt: Double,
        var available: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.createStringArrayList() ?: arrayListOf(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(car_images)
        parcel.writeString(model)
        parcel.writeString(car_id)
        parcel.writeString(owner_id)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeDouble(amount)
        parcel.writeDouble(downpayment_amt)
        parcel.writeString(available)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CarKotlin> {
        override fun createFromParcel(parcel: Parcel): CarKotlin {
            return CarKotlin(parcel)
        }

        override fun newArray(size: Int): Array<CarKotlin?> {
            return arrayOfNulls(size)
        }
    }

    fun parseLocation(): Location? {
        val coordinates = location.split(",") // Split the string into latitude and longitude

        if (coordinates.size == 2) {
            try {
                val latitude = coordinates[0].toDouble() // Extract latitude
                val longitude = coordinates[1].toDouble() // Extract longitude

                return Location(latitude, longitude) // Return as a Location object
            } catch (e: NumberFormatException) {
                // Handle if conversion to Double fails
                e.printStackTrace()
            }
        }

        return null // Return null if unable to parse the location string
    }

    class Location(val lat: Double, val lng: Double) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readDouble(),
                parcel.readDouble()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeDouble(lat)
            parcel.writeDouble(lng)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Location> {
            override fun createFromParcel(parcel: Parcel): Location {
                return Location(parcel)
            }

            override fun newArray(size: Int): Array<Location?> {
                return arrayOfNulls(size)
            }
        }
    }
}