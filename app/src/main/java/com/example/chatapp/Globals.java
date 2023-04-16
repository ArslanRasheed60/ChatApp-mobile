package com.example.chatapp;

public class Globals {

    //User
    public static String USER_EMAIL = null;
    //Message Sender
    public static final String MESSAGE_SENDER = "Arslan";
    //Database Conversation Table
    public static final String CONVERSATION_TABLE = "Conversation";
    public static final String C_COLUMN_NAME = "person_name";
    public static final String C_COLUMN_LAST_MESSAGE = "last_message";
    public static final String C_COLUMN_TIMESTAMP = "timestamp";
    //Database Message Table
    public static final String MESSAGE_TABLE = "Message";
    public static final String M_COLUMN_DETAIL = "detail";
    public static final String M_COLUMN_TIME = "time";
    public static final String M_COLUMN_IS_SENDER = "is_sender";
    public static final String M_COLUMN_C_ID = "c_id";     //conversation id

    //database
    public static IChatInterface dao;

    //firebase persistance enabled
    public static Boolean isPersistenceEnabled = false;

}
