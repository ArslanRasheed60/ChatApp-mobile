package com.example.chatapp.conversation;

public class Message {
    private  String message;
    private String time;
    private int type;               //0 for sender 1 for receiver
    private String username;

    public Message(String username, String message, String time, int type) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.username = username;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
