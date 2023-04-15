package com.example.chatapp.conversation;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.chatapp.IChatInterface;

import java.util.ArrayList;

public class MessageViewModel extends ViewModel {
    private ArrayList<Message> messageArrayList;
    private IChatInterface dao;
    public ArrayList<Message> getMessages(Bundle SaveInstanceState, String key, String receiverId){
        if(messageArrayList == null){
            if (SaveInstanceState == null) {
                if (dao != null){
                    messageArrayList = Message.load(dao,receiverId);
                }
                else {
                    messageArrayList = new ArrayList<>();
                }
            }else{
                messageArrayList = (ArrayList<Message>) SaveInstanceState.get(key);
            }
        }
        return  messageArrayList;
    }


    public void setDao(IChatInterface dao) {
        this.dao = dao;
    }
}
