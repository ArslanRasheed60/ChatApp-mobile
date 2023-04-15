package com.example.chatapp;

import com.example.chatapp.conversation.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Person {
    private int id;
    private String person_name;
    private String last_message;
    private long timestamp;
    private transient IChatInterface dao = null;

    Person(String name, IChatInterface dao){
        this.person_name = name;
        this.id = -1;
        this.timestamp = -1;
        this.last_message = "";
        this.dao = dao;
    }

    public Person(int id, String name, String lastMessage, long timeStamp){
        this.id = id;
        this.person_name = name;
        this.last_message = lastMessage;
        this.timestamp = timeStamp;
    }
    Person(int id, String name, String lastMessage, long timeStamp, IChatInterface dao){
        this.id = id;
        this.person_name = name;
        this.last_message = lastMessage;
        this.timestamp = timeStamp;
        this.dao = dao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return person_name;
    }

    public void setName(String name) {
        this.person_name = name;
    }

    public String getLastMessage() {
        return last_message;
    }

    public void setLastMessage(String lastMessage) {
        this.last_message = lastMessage;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public void save(){
        if (dao != null){
            Person p = new Person(id, person_name, last_message, timestamp);
            //save in database
            dao.savePerson(p);
        }
    }

    public void load(Person person){
        if(person != null){
             id = person.getId();
            person_name = person.getName();
            last_message = person.getLastMessage();
            timestamp = person.getTimeStamp();
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
        this.last_message = lastMessage;
        this.timestamp = timeStamp;
        if(dao != null){
            dao.updatePersonConversation(Integer.toString(this.getId()), this.getLastMessage(), this.getTimeStamp());
        }
    }

}
