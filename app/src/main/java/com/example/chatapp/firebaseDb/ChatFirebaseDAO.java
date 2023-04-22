package com.example.chatapp.firebaseDb;

import static com.example.chatapp.Globals.*;

import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chatapp.ConversationMainActivityLists;
import com.example.chatapp.IChatInterface;
import com.example.chatapp.MessageType;
import com.example.chatapp.Person;
import com.example.chatapp.conversation.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ChatFirebaseDAO implements IChatInterface {

    public interface DataObserver{
        public void update();
    }

    private DataObserver observer;
    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Person> personArrayList;
    ArrayList<Message> messageArrayList;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userPhoneNumber;
    String userName;

    public ChatFirebaseDAO(DataObserver obs){

        //firebase auth and user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();
        userPhoneNumber = email.substring(0,11);

        observer = obs;
        database = FirebaseDatabase.getInstance();

        //setting full name
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(Full_Name);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    userName = dataSnapshot.getValue(String.class);
                    MESSAGE_SENDER = userName;
                    observer.update();
                    Log.d("yyyyy",userName);
                }
                catch (Exception ex) {
                    Log.e("firebasedb", ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("firebasedb", "Failed to read value.", error.toException());
            }
        });

        //Load Conversations/Persons
        //handling conversation class
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(CONVERSATION_TABLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    personArrayList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String conversationId = childSnapshot.getKey();
                        String lastMessage = childSnapshot.child(C_COLUMN_LAST_MESSAGE).getValue(String.class);
                        String personName = childSnapshot.child(C_COLUMN_NAME).getValue(String.class);
                        long timestamp = childSnapshot.child(C_COLUMN_TIMESTAMP).getValue(long.class);
                        String messageType = childSnapshot.child(C_COLUMN_MESSAGE_TYPE).getValue(String.class);

                        //store in array list
//                        int id = Integer.parseInt(conversationId.substring(2, conversationId.length()));
                        Person person = new Person(conversationId,personName, lastMessage, timestamp,messageType);

                        // Finally, you can add this conversation object to an ArrayList.
                        personArrayList.add(person);
                    }

                    observer.update();
                }
                catch (Exception ex) {
                    Log.e("firebasedb", ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("firebasedb", "Failed to read value.", error.toException());
            }
        });

        //Load Messages
        //handling messages class
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(MESSAGE_TABLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    messageArrayList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String messageId = childSnapshot.getKey();
                        String messsageUsername = childSnapshot.child(M_COLUMN_USERNAME).getValue(String.class);
                        String messageDetail = childSnapshot.child(M_COLUMN_DETAIL).getValue(String.class);
                        long messageTime = childSnapshot.child(M_COLUMN_TIME).getValue(Long.class);
                        int messageIsSender = childSnapshot.child(M_COLUMN_IS_SENDER).getValue(int.class);
                        String messagePersonId = childSnapshot.child(M_COLUMN_C_ID).getValue(String.class);

                        //store in array list
//                        int id = Integer.parseInt(conversationId.substring(2, conversationId.length()));
                        Message message = new Message(messsageUsername,messageDetail, messageTime, messageIsSender, messagePersonId);

                        // Finally, you can add this conversation object to an ArrayList.
                        messageArrayList.add(message);
                    }

                    observer.update();
                }
                catch (Exception ex) {
                    Log.e("firebasedb", ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("firebasedb", "Failed to read value.", error.toException());
            }
        });

    }


    @Override
    public void savePerson(Person person) {
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(CONVERSATION_TABLE);
        //making string id
        String personId = person.getId();
        //making hashmap
        Map<String, Object> childObject = new HashMap<>();
        childObject.put(C_COLUMN_NAME, person.getName());
        childObject.put(C_COLUMN_LAST_MESSAGE, person.getLastMessage());
        childObject.put(C_COLUMN_TIMESTAMP, person.getTimeStamp());
        childObject.put(C_COLUMN_MESSAGE_TYPE, person.getMessageType());

        myRef.child(personId).setValue(childObject);
    }

    @Override
    public ArrayList<Person> loadPersonList() {
        if(personArrayList == null){
            personArrayList = new ArrayList<>();
        }
        personArrayList.sort(Comparator.comparingLong(Person::getTimeStamp).reversed());
        return personArrayList;
    }

    @Override
    public void deleteOnePerson(String id) {
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(CONVERSATION_TABLE).child(id);
        myRef.removeValue();
    }

    @Override
    public void deleteAllPersons() {
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(CONVERSATION_TABLE);
        myRef.removeValue();
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(MESSAGE_TABLE);
        myRef.removeValue();
    }

    @Override
    public void updatePersonConversation(Person person) {
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(CONVERSATION_TABLE);
        //making string id
        String personId = person.getId();
        //making hashmap
        Map<String, Object> childObject = new HashMap<>();
        childObject.put(C_COLUMN_NAME, person.getName());
        childObject.put(C_COLUMN_LAST_MESSAGE, person.getLastMessage());
        childObject.put(C_COLUMN_TIMESTAMP, person.getTimeStamp());
        childObject.put(C_COLUMN_MESSAGE_TYPE, person.getMessageType());

        myRef.child(personId).updateChildren(childObject);
    }

    @Override
    public void saveMessage(Message message, String conversationID) {
        //add messsage at sender side
        myRef = database.getReference().child(CHAT_DB).child(userPhoneNumber).child(MESSAGE_TABLE);
        String messageId = UUID.randomUUID().toString();
        Map<String, Object> childObject = new HashMap<>();
        childObject.put(M_COLUMN_USERNAME, userName);
        childObject.put(M_COLUMN_DETAIL, message.getMessage());
        childObject.put(M_COLUMN_TIME, message.getTime());
        childObject.put(M_COLUMN_IS_SENDER, 0);
        childObject.put(M_COLUMN_C_ID, conversationID);
        myRef.child(messageId).setValue(childObject);

        //add messsage at receiver side
        myRef = database.getReference().child(CHAT_DB).child(conversationID).child(MESSAGE_TABLE);
        String messageId2 = UUID.randomUUID().toString();
        Map<String, Object> childObject2 = new HashMap<>();
        childObject2.put(M_COLUMN_USERNAME, userName);
        childObject2.put(M_COLUMN_DETAIL, message.getMessage());
        childObject2.put(M_COLUMN_TIME, message.getTime());
        childObject2.put(M_COLUMN_IS_SENDER, 1);
        childObject2.put(M_COLUMN_C_ID, userPhoneNumber);

        myRef.child(messageId2).setValue(childObject2);

        //update receiver conversation row
        myRef = database.getReference().child(CHAT_DB).child(conversationID).child(CONVERSATION_TABLE);
        //making string id
        String personId = userPhoneNumber;
        //making hashmap
        Map<String, Object> childObject3 = new HashMap<>();
        childObject3.put(C_COLUMN_NAME, userName);
        childObject3.put(C_COLUMN_LAST_MESSAGE, message.getMessage());
        childObject3.put(C_COLUMN_TIMESTAMP, System.currentTimeMillis());
        childObject3.put(C_COLUMN_MESSAGE_TYPE, MessageType.RECEIVED.toString());

        myRef.child(userPhoneNumber).updateChildren(childObject3);

    }

    @Override
    public ArrayList<Message> loadMessageList(String CID) {
        ArrayList<Message> filteredMessageList;
        filteredMessageList = new ArrayList<>();
        if(messageArrayList == null){
            messageArrayList = new ArrayList<>();
        }else{
            for (Message m :messageArrayList) {
                if(Objects.equals(m.getConversation_ID(), CID)){
                    filteredMessageList.add(m);
                }
            }
        }
        filteredMessageList.sort(Comparator.comparingLong(Message::getTime));
        return filteredMessageList;
    }
}
