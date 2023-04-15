package com.example.chatapp.sqliteDB;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.chatapp.Globals.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.IChatInterface;
import com.example.chatapp.Person;
import com.example.chatapp.conversation.Message;

import java.util.ArrayList;

public class ChatDbDAO implements IChatInterface {

    private Context context;
    MyDataBaseHelper myDataBaseHelper ;

    public ChatDbDAO(Context context){
        this.context = context;
        myDataBaseHelper = new MyDataBaseHelper(this.context);
    }

    @Override
    public void savePerson(Person person) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        long timeStamp = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(C_COLUMN_NAME, person.getName());
        cv.put(C_COLUMN_LAST_MESSAGE, "");
        cv.put(C_COLUMN_TIMESTAMP, timeStamp);
        long result =  db.insert(CONVERSATION_TABLE, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", LENGTH_SHORT).show();
        }
    }

    @Override
    public ArrayList<Person> loadPersonList() {
        String Query = "SELECT * FROM " + CONVERSATION_TABLE + " ORDER BY " + C_COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        ArrayList<Person> personArrayList = new ArrayList<>();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(Query, null);
            //reading data
            while (cursor.moveToNext()){
                Person person = new Person(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getLong(3));
                personArrayList.add(person);
            }
        }
        return personArrayList;
    }

    @Override
    public void deleteOnePerson(String id) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        long result = db.delete(CONVERSATION_TABLE,"id=?",new String[]{id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete!", LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteAllPersons() {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + CONVERSATION_TABLE);
    }

    @Override
    public void updatePersonConversation(String id, String message, long timeStamp) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(C_COLUMN_LAST_MESSAGE, message);
        content.put(C_COLUMN_TIMESTAMP, timeStamp);

        long result = db.update(CONVERSATION_TABLE, content, "id=?", new String[]{id});
        if (result == -1){
            Toast.makeText(context, "Failed to update!", LENGTH_SHORT).show();
        }
    }

    @Override
    public void saveMessage(Message message, int conversationID) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(M_COLUMN_DETAIL, message.getMessage());
        content.put(M_COLUMN_TIME, message.getTime());
        content.put(M_COLUMN_IS_SENDER, message.getType());
        content.put(M_COLUMN_C_ID, conversationID);
        long result =  db.insert(MESSAGE_TABLE, null, content);
        if(result == -1){
            Toast.makeText(context, "Failed", LENGTH_SHORT).show();
        }
    }

    @Override
    public ArrayList<Message> loadMessageList(String CID) {
        //SELECT c.name, m.detail, m.time, m.isSender from conversation as c JOIN message as m on c.id = m.CID where CID = '3'
        String Query = "SELECT c." + C_COLUMN_NAME +
                ", m." + M_COLUMN_DETAIL +
                ", m." + M_COLUMN_TIME +
                ", m." + M_COLUMN_IS_SENDER + " FROM " +
                CONVERSATION_TABLE + " as c JOIN " + MESSAGE_TABLE + " as m on c.id = m." +
                M_COLUMN_C_ID + " WHERE " + M_COLUMN_C_ID + " = " + CID;
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        ArrayList<Message> messageArrayList= new ArrayList<>();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(Query, null);
            if(cursor.getCount() != 0){
                while (cursor.moveToNext()){
                    int senderType = Integer.parseInt(cursor.getString(3));
                    String username = senderType == 0 ? MESSAGE_SENDER : cursor.getString(0);
                    Message message = new Message(username,cursor.getString(1),cursor.getString(2),senderType);
                    messageArrayList.add(message);
                }
            }

        }
        return messageArrayList;
    }
}
