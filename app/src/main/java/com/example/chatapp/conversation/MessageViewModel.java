package com.example.chatapp.conversation;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MessageViewModel extends ViewModel {
    private ArrayList<Message> messageArrayList;
    public ArrayList<Message> getMessages(Bundle SaveInstanceState, String key){
        if(messageArrayList == null){
            messageArrayList = new ArrayList<>();
        }
        else{
            messageArrayList = (ArrayList<Message>) SaveInstanceState.get(key);
        }
        return  messageArrayList;
    }
}
