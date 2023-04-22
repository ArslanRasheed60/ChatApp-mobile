package com.example.chatapp;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import com.example.chatapp.conversation.Message;

import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {
    private ArrayList<Person> personArrayList;
    private IChatInterface dao;
    public ArrayList<Person> getConversations(Bundle SaveInstanceState, String key){
        if(personArrayList == null){
            if (SaveInstanceState == null) {
                if (dao != null){
                    personArrayList = Person.load(dao);
                }
                else {
                    personArrayList = new ArrayList<>();
                }
            }else{
                personArrayList = (ArrayList<Person>) SaveInstanceState.get(key);
            }
        }
        return  personArrayList;
    }


    public void setDao(IChatInterface dao) {
        this.dao = dao;
    }
}
