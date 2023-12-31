package com.example.carapp.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.carapp.entities.User;
import com.example.carapp.messagingUtils.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_database.db";
    private static final int DATABASE_VERSION = 1;

    // Table for users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    private static final String COLUMN_ACCOUNT_TYPE = "accountType";
    private static final String COLUMN_PROFILE_PIC = "profilePic";
    private static final String COLUMN_ONLINE_STATUS = "onlineStatus";

    // Table for chat messages
    private static final String TABLE_CHAT_MESSAGES = "chat_messages";
    private static final String COLUMN_MESSAGE_ID = "messageId";
    private static final String COLUMN_SENDER_ID = "senderId";
    private static final String COLUMN_RECEIVER_ID = "receiverId";
    private static final String COLUMN_MESSAGE_CONTENT = "messageContent";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Create the users table query
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_USER_ID + " TEXT PRIMARY KEY," +
                    COLUMN_USERNAME + " TEXT," +
                    COLUMN_PHONE_NUMBER + " TEXT," +
                    COLUMN_ACCOUNT_TYPE + " TEXT," +
                    COLUMN_PROFILE_PIC + " TEXT," +
                    COLUMN_ONLINE_STATUS + " TEXT" +
                    ")";

    // Create the chat messages table query
    private static final String CREATE_CHAT_MESSAGES_TABLE =
            "CREATE TABLE " + TABLE_CHAT_MESSAGES + "(" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SENDER_ID + " TEXT," +
                    COLUMN_RECEIVER_ID + " TEXT," +
                    COLUMN_MESSAGE_CONTENT + " TEXT," +
                    COLUMN_TIMESTAMP + " TEXT" +
                    ")";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades here if needed
        // Example: db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        //          db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        //          onCreate(db);
    }

    // Methods for managing users
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_ACCOUNT_TYPE, user.getAccountType());
        values.put(COLUMN_PROFILE_PIC, user.getProfilePic());
        values.put(COLUMN_ONLINE_STATUS, user.getOnlineStatus());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserId(cursor.getString(0));
                user.setUsername(cursor.getString(1));
                user.setPhoneNumber(cursor.getString(2));
                user.setAccountType(cursor.getString(3));
                user.setProfilePic(cursor.getString(4));
                user.setOnlineStatus(cursor.getString(5));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_ACCOUNT_TYPE, user.getAccountType());
        values.put(COLUMN_PROFILE_PIC, user.getProfilePic());
        values.put(COLUMN_ONLINE_STATUS, user.getOnlineStatus());
        int result = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{user.getUserId()});
        db.close();
        return result;
    }

    public void deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{userId});
        db.close();
    }
    public boolean doesUserExist(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,         // Table name
                new String[]{COLUMN_USER_ID}, // Projection (columns to retrieve)
                COLUMN_USER_ID + " = ?",     // Selection (where clause)
                new String[]{userId},        // Selection arguments (values to compare to)
                null,                       // Group by
                null,                       // Having
                null                        // Order by
        );

        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return userExists;
    }
    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                // Table name
                new String[]{              // Projection (columns to retrieve)
                        COLUMN_USER_ID,
                        COLUMN_USERNAME,
                        COLUMN_PHONE_NUMBER,
                        COLUMN_ACCOUNT_TYPE,
                        COLUMN_PROFILE_PIC,
                        COLUMN_ONLINE_STATUS
                },
                COLUMN_USER_ID + " = ?",   // Selection (where clause)
                new String[]{userId},      // Selection arguments (values to compare to)
                null,                       // Group by
                null,                       // Having
                null                        // Order by
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(0));
            user.setUsername(cursor.getString(1));
            user.setPhoneNumber(cursor.getString(2));
            user.setAccountType(cursor.getString(3));
            user.setProfilePic(cursor.getString(4));
            user.setOnlineStatus(cursor.getString(5));
        }

        cursor.close();
        db.close();

        return user;
    }


    // Methods for managing chat messages
    public long insertChatMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, message.getSender_id());
        values.put(COLUMN_RECEIVER_ID, message.getRecepient_id());
        values.put(COLUMN_MESSAGE_CONTENT, message.getMessage());
        values.put(COLUMN_TIMESTAMP, message.getTime());
        long result = db.insert(TABLE_CHAT_MESSAGES, null, values);
        db.close();
        return result;
    }

    public List<ChatMessage> getChatMessages(String senderId, String receiverId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CHAT_MESSAGES +
                " WHERE (" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)" +
                " OR (" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)";
        Cursor cursor = db.rawQuery(query, new String[]{senderId, receiverId, receiverId, senderId});

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setSender_id(cursor.getString(1));
                message.setRecepient_id(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setTime(cursor.getString(4));
                chatMessages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chatMessages;
    }

    // Add methods for updating and deleting chat messages as needed
}


