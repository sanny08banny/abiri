package com.example.carapp.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.carapp.entities.User;
import com.example.carapp.taxi_utils.ClientRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ClientsHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ClientsDB";
    private static final int DATABASE_VERSION = 1;

    // Define table creation SQL statement
    private static final String CREATE_TABLE_CLIENTS = "CREATE TABLE clients (" +
            "clientId TEXT PRIMARY KEY," + // Use TEXT for UUID
            "username TEXT," +
            "timestamp TEXT," +
            "timestamp TEXT," +
            "dest_lat TEXT," +
            "dest_lon TEXT," +
            "pick_up_lat TEXT," +
            "pick_up_lon TEXT)";

    public ClientsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades if needed
    }

    // Insert a new user into the database
    // Insert a new user into the database with a UUID
    public String addUser(ClientRequest clientRequest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("clientId", clientRequest.getClient_id()); // Convert UUID to String
        values.put("username", clientRequest.getUser_name());
        values.put("timestamp", String.valueOf(new Date()));
        values.put("dest_lat", clientRequest.getDest_lat());
        values.put("dest_lon", clientRequest.getDest_lon());
        values.put("pick_up_lat", clientRequest.getCurrent_lat());
        values.put("pick_up_lon", clientRequest.getCurrent_lon());

        long insertResult = db.insert("clients", null, values);
        db.close();

        if (insertResult != -1) {
            return clientRequest.getRide_id();
        } else {
            return null;
        }
    }
    // Retrieve all users
//    public List<User> getAllProfiles() {
//        List<User> profileList = new ArrayList<>();
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                long profileId = cursor.getLong(0);
//                String name = cursor.getString(1);
//                String email = cursor.getString(2);
//                String username = cursor.getString(3);
//                String password = cursor.getString(4);
//                int age = cursor.getInt(5);
//                String gender = cursor.getString(6);
//                String address = cursor.getString(7);
//                String accountType = cursor.getString(8);
//                String dateCreated = cursor.getString(9);
//                String fcmToken = cursor.getString(10);
//                String profileImage = cursor.getString(11);
//
//                User profile = new User(username,name, email, password,",",
//                        gender, address, accountType,age,null,profileImage,
//                        dateCreated, fcmToken);
//                profileList.add(profile);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return profileList;
//    }

    // Retrieve a user by ID
    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("users",
                new String[]{"userId", "username","email","password", "phoneNumber", "accountType", "profilePic"},
                "userId=?",
                new String[]{userId},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            db.close();
            return user;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    // Update user information
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("accountType", user.getAccountType());
        values.put("profilePic", user.getProfilePic());

        int rowsAffected = db.update("users", values, "userId=?",
                new String[]{String.valueOf(user.getUserId())});

        db.close();
        return rowsAffected;
    }

    // Delete a user by ID
    public void deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "userId=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId((cursor.getString(0)));
        user.setUsername(cursor.getString(1));
        user.setEmail(cursor.getString(2));
        user.setPassword(cursor.getString(3));
        user.setPhoneNumber(cursor.getString(4));
        user.setAccountType(cursor.getString(5));
        user.setProfilePic(cursor.getString(6));
        return user;
    }
}

