package com.example.chatapp.conversation;

import android.util.Log;

import com.example.chatapp.IChatInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Message {
    private  String message;
    private String time;
    private int type;               //0 for sender 1 for receiver
    private String username;
    private String conversation_ID;
    private transient IChatInterface dao = null;

    public Message(String username, String message, String time, int type) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.username = username;
        this.conversation_ID = "";
    }

    public Message(String username, String message, String time, int type, IChatInterface dao) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.username = username;
        this.conversation_ID = "";
        this.dao = dao;
    }

    public Message(String username, String message, String time, int type, String conversation_ID) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.username = username;
        this.conversation_ID = conversation_ID;
    }

    public Message(String username, String message, String time, int type, String conversation_ID ,IChatInterface dao) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.username = username;
        this.conversation_ID = conversation_ID;
        this.dao = dao;
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

    public void save(String conversationId){
        if (dao != null){
            Message m = new Message(username, message, time, type);
            //save in database
            dao.saveMessage(m,conversationId);
        }
    }

    public void load(Message message){
        if(message != null){
            username = message.getUsername();
            this.message = message.getMessage();
            time = message.getTime();
            type = message.getType();
            conversation_ID = message.getConversation_ID();
        }
    }

    public static ArrayList<Message> load(IChatInterface dao,String receiverId){
        ArrayList<Message> messages = new ArrayList<Message>();
        if(dao != null){
            ArrayList<Message> objects = dao.loadMessageList(receiverId);
            for(Message obj : objects){
                Message message1 = new Message(obj.getUsername(), obj.getMessage(), obj.getTime(), obj.getType(),obj.getConversation_ID(),dao);
                messages.add(message1);
            }
        }
        return messages;
    }

    public String getConversation_ID() {
        return conversation_ID;
    }
}
