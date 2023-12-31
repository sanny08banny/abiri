package com.example.carapp.messagingUtils;

public class ChatMessage {
    private String sender_id;
    private String recepient_id;
    private String message;
    private String msg_id;
    private String time;

    public ChatMessage() {
    }

    public ChatMessage(String sender_id, String recepient_id, String message, String msg_id, String time) {
        this.sender_id = sender_id;
        this.recepient_id = recepient_id;
        this.message = message;
        this.msg_id = msg_id;
        this.time = time;
    }

    // Getter and Setter methods for the fields
    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getRecepient_id() {
        return recepient_id;
    }

    public void setRecepient_id(String recepient_id) {
        this.recepient_id = recepient_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

