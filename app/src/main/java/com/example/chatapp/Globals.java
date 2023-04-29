package com.example.chatapp;

import java.nio.charset.StandardCharsets;

public class Globals {

    //User
    public static String USER_EMAIL = null;
    //Message Sender
    public static String MESSAGE_SENDER = "temporary";
    //Database Conversation Table
    public static final String CONVERSATION_TABLE = "Conversation";
    public static final String C_COLUMN_NAME = "person_name";
    public static final String C_COLUMN_LAST_MESSAGE = "last_message";
    public static final String C_COLUMN_TIMESTAMP = "timestamp";
    public static final String C_COLUMN_MESSAGE_TYPE = "message_type";
    //Database Message Table
    public static final String MESSAGE_TABLE = "Message";
    public static final String M_COLUMN_USERNAME = "username";
    public static final String M_COLUMN_DETAIL = "detail";
    public static final String M_COLUMN_TIME = "time";
    public static final String M_COLUMN_IS_SENDER = "is_sender";
    public static final String M_COLUMN_C_ID = "c_id";     //conversation id

    //database
    public static IChatInterface dao;

    //firebase persistance enabled
    public static Boolean isPersistenceEnabled = false;
    //firebase constants
    public static String Full_Name = "full_name";
    public static final String Email_Extension = "@chatapp.com";
    public static final String CHAT_DB = "ChatDb";
    // functions
    //global function that takes phone number as input and check it is correct or not
    public static boolean verifyPhoneNumber(String phoneNumber){
        if(phoneNumber.startsWith("+")){
            phoneNumber = phoneNumber.replaceAll("\\s", "");
            return phoneNumber.length() == 13;
        }else{
            return phoneNumber.length() == 11 &&
                    phoneNumber.startsWith(String.valueOf(0));
        }
    }

    public static String formatPhoneNumber(String phoneNumber){
        if(phoneNumber.startsWith("+92") || phoneNumber.startsWith("0")){
            phoneNumber = phoneNumber.replaceAll("\\s", "");
            phoneNumber = "0" + phoneNumber.substring(phoneNumber.length() - 10);
            if(phoneNumber.length() == 11){
                return phoneNumber;
            }
        }
        return "-1";
    }
}
