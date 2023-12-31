package com.example.carapp.messagingUtils;

public class MessageStatus {
    private String recepient_id;
    private String msg_id;
    private String status;

    public MessageStatus(String recepient_id, String msg_id, String status) {
        this.recepient_id = recepient_id;
        this.msg_id = msg_id;
        this.status = status;
    }

    // Getter and Setter methods for the fields
    public String getRecepient_id() {
        return recepient_id;
    }

    public void setRecepient_id(String recepient_id) {
        this.recepient_id = recepient_id;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
