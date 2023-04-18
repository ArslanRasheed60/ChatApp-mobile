package com.example.chatapp.contacts;

public class Contact {
    String name;
    String phoneNumber;

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Contact(String name, String phoneNumeber){
        this.name = name;
        this.phoneNumber =    phoneNumeber;
    }


}
