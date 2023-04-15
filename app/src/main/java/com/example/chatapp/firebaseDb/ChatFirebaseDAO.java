package com.example.chatapp.firebaseDb;

import static com.example.chatapp.Globals.*;

import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.ConversationMainActivityLists;
import com.example.chatapp.IChatInterface;
import com.example.chatapp.Person;
import com.example.chatapp.conversation.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ChatFirebaseDAO implements IChatInterface {

    public interface DataObserver{
        public void update();
    }

    private DataObserver observer;
    FirebaseDatabase database;
    DatabaseReference myRef;

    ArrayList<Person> personArrayList;
    ArrayList<Message> messageArrayList;

    public ChatFirebaseDAO(DataObserver obs){
        observer = obs;
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        myRef = database.getReference().child("ChatDb").child("Conversation");

        //handling conversation class
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

                        //store in array list
                        int id = Integer.parseInt(conversationId.substring(2, conversationId.length()));
                        Person person = new Person(id,personName, lastMessage, timestamp);

                        Log.d("yyyy", personName + "  " + lastMessage);

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

    }


    @Override
    public void savePerson(Person person) {

    }

    @Override
    public ArrayList<Person> loadPersonList() {
        if(personArrayList == null){
            personArrayList = new ArrayList<>();
        }
        Log.d("yyyy", "size hai: "+ Integer.toString(personArrayList.size()));
        return personArrayList;
    }

    @Override
    public void deleteOnePerson(String id) {

    }

    @Override
    public void deleteAllPersons() {

    }

    @Override
    public void updatePersonConversation(String id, String message, long timeStamp) {

    }

    @Override
    public void saveMessage(Message message, int conversationID) {

    }

    @Override
    public ArrayList<Message> loadMessageList(String CID) {
        return null;
    }
}
