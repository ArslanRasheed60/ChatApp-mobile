package com.example.chatapp;

public class Person {
    private int id;
    private String name;
    private String lastMessage;
    private long timeStamp;

    Person(int id, String name, String lastMessage, long timeStamp){
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
