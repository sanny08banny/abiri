package com.example.carapp.databasehelpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import com.example.carapp.entities.BookedCar;

import java.util.ArrayList;
import java.util.List;

public class BookedCarsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BookedCarsDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "BookedCars";
    private static final String COLUMN_CAR_ID = "car_id";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_OWNER_ID = "owner_id";
    private static final String COLUMN_PRICING = "pricing";
    private static final String COLUMN_IMAGE = "image";

    public BookedCarsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_CAR_ID + " TEXT PRIMARY KEY, " +
                COLUMN_DURATION + " TEXT, " +
                COLUMN_OWNER_ID + " TEXT, " +
                COLUMN_PRICING + " TEXT, " +
                COLUMN_IMAGE + " TEXT)";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertBookedCar(BookedCar bookedCar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAR_ID, bookedCar.getCar_id());
        values.put(COLUMN_DURATION, bookedCar.getDuration());
        values.put(COLUMN_OWNER_ID, bookedCar.getOwner_id());
        values.put(COLUMN_PRICING, bookedCar.getPricing());
        values.put(COLUMN_IMAGE, bookedCar.getImage());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1; // Return true if insertion was successful
    }

    public List<BookedCar> getAllBookedCars() {
        List<BookedCar> bookedCars = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String carId = cursor.getString(0);
                String duration = cursor.getString(1);
                String ownerId = cursor.getString(2);
                String pricing = cursor.getString(3);
                String image = cursor.getString(4);

                BookedCar bookedCar = new BookedCar(carId, duration, ownerId, pricing, image);
                bookedCars.add(bookedCar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookedCars;
    }

    public BookedCar getBookedCarByCarId(String carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CAR_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{carId});

        if (cursor.moveToFirst()) {
            String duration = cursor.getString(1);
            String ownerId = cursor.getString(2);
            String pricing = cursor.getString(3);
            String image = cursor.getString(4);

            cursor.close();
            db.close();
            return new BookedCar(carId, duration, ownerId, pricing, image);
        } else {
            cursor.close();
            db.close();
            return null; // Car not found
        }
    }

    public void deleteBookedCarByCarId(String carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_CAR_ID + " = ?", new String[]{carId});
        db.close();
    }

    public void updateBookedCar(BookedCar bookedCar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DURATION, bookedCar.getDuration());
        values.put(COLUMN_OWNER_ID, bookedCar.getOwner_id());
        values.put(COLUMN_PRICING, bookedCar.getPricing());
        values.put(COLUMN_IMAGE, bookedCar.getImage());

        db.update(TABLE_NAME, values, COLUMN_CAR_ID + " = ?", new String[]{bookedCar.getCar_id()});
        db.close();
    }
}

