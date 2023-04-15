package com.example.chatapp;

import com.example.chatapp.conversation.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Person {
    private int id;
    private String name;
    private String lastMessage;
    private long timeStamp;
    private transient IChatInterface dao = null;

    Person(String name, IChatInterface dao){
        this.name = name;
        this.id = -1;
        this.timeStamp = -1;
        this.lastMessage = "";
        this.dao = dao;
    }

    public Person(int id, String name, String lastMessage, long timeStamp){
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }
    Person(int id, String name, String lastMessage, long timeStamp, IChatInterface dao){
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.dao = dao;
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

    public void save(){
        if (dao != null){
            Person p = new Person(id, name, lastMessage, timeStamp);
            //save in database
            dao.savePerson(p);
        }
    }

    public void load(Person person){
        if(person != null){
             id = person.getId();
             name = person.getName();
             lastMessage = person.getLastMessage();
             timeStamp = person.getTimeStamp();
        }
    }

    public static ArrayList<Person> load(IChatInterface dao){
        ArrayList<Person> persons = new ArrayList<Person>();
        if(dao != null){
            ArrayList<Person> objects = dao.loadPersonList();
            for(Person obj : objects){
                Person person1 = new Person(obj.getId(), obj.getName(), obj.getLastMessage(), obj.getTimeStamp() ,dao);
                persons.add(person1);
            }
        }
        return persons;
    }

    public void delete(){
        if(dao != null){
            dao.deleteOnePerson(Integer.toString(this.id));
        }
    }
    public static void deleteAll(IChatInterface dao){
        if(dao != null){
            dao.deleteAllPersons();
        }
    }

    public void update(String lastMessage, long timeStamp){
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        if(dao != null){
            dao.updatePersonConversation(Integer.toString(this.getId()), this.getLastMessage(), this.getTimeStamp());
        }
    }

}
