package com.example.carapp.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.carapp.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 1;

    // Define table creation SQL statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "userId TEXT PRIMARY KEY," + // Use TEXT for UUID
            "username TEXT," +
            "email TEXT," +
            "password TEXT," +
            "phoneNumber TEXT," +
            "accountType TEXT," +
            "profilePic TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades if needed
    }

    // Insert a new user into the database
    // Insert a new user into the database with a UUID
    public String addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId()); // Convert UUID to String
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("accountType", user.getAccountType());
        values.put("profilePic", user.getProfilePic());

        long insertResult = db.insert("users", null, values);
        db.close();

        if (insertResult != -1) {
            return user.getUserId();
        } else {
            return null;
        }
    }

    // Retrieve a user by ID (UUID)
    public User getUserByUUId(UUID userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("users",
                new String[]{"userId", "username","email","password", "phoneNumber", "accountType", "profilePic"},
                "userId=?",
                new String[]{userId.toString()}, // Convert UUID to String
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            User user = cursorToUser(cursor);
            cursor.close();
            db.close();
            return user;
        } else {
            db.close();
            return null;
        }
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("users",
                new String[]{"userId", "username", "phoneNumber", "accountType", "profilePic"},
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    User user = cursorToUser(cursor);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return userList;
    }

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

