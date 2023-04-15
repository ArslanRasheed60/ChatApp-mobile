package com.example.chatapp;

import com.example.chatapp.conversation.Message;

import java.util.ArrayList;
import java.util.Hashtable;

public interface IChatInterface {
    //person
    public void savePerson(Person person);
    public ArrayList<Person> loadPersonList();
    public void deleteOnePerson(String id);
    public void deleteAllPersons();
    public void updatePersonConversation(String id, String message, long timeStamp);
    //message
    public void saveMessage(Message message, int conversationID);
    public ArrayList<Message> loadMessageList(String CID);
}