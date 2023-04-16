package com.example.chatapp;

import com.example.chatapp.conversation.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Person {
    private String id_email;
    private String person_name;
    private String last_message;
    private long timestamp;
    private String messageType;
    private transient IChatInterface dao = null;

    Person(String id_email,String name, IChatInterface dao){
        this.person_name = name;
        this.id_email = id_email;
        this.timestamp = -1;
        this.last_message = "";
        this.dao = dao;
        this.messageType = MessageType.SENT.toString();
    }

    public Person(String id_email, String name, String lastMessage, long timeStamp, String messageType){
        this.id_email = id_email;
        this.person_name = name;
        this.last_message = lastMessage;
        this.timestamp = timeStamp;
        this.messageType = messageType;
    }
    Person(String id_email, String name, String lastMessage, long timeStamp, String messageType, IChatInterface dao){
        this.id_email = id_email;
        this.person_name = name;
        this.last_message = lastMessage;
        this.timestamp = timeStamp;
        this.dao = dao;
        this.messageType = messageType;
    }

    public String getId() {
        return id_email;
    }

    public String getMessageType(){return messageType;}

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
            Person p = new Person(id_email, person_name, last_message, timestamp, messageType);
            //save in database
            dao.savePerson(p);
        }
    }

    public void load(Person person){
        if(person != null){
             id_email = person.getId();
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
                Person person1 = new Person(obj.getId(), obj.getName(), obj.getLastMessage(), obj.getTimeStamp(),obj.getMessageType() ,dao);
                persons.add(person1);
            }
        }
        return persons;
    }

    public void delete(){
        if(dao != null){
            dao.deleteOnePerson(this.id_email);
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
            dao.updatePersonConversation(this.getId(), this.getLastMessage(), this.getTimeStamp());
        }
    }

}
