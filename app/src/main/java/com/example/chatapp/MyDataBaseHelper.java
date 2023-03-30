package com.example.chatapp;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Chats.db";

    public static final String CONVERSATION_TABLE = "Conversation";
    public static final String C_COLUMN_NAME = "person_name";
    public static final String C_COLUMN_LAST_MESSAGE = "last_message";
    public static final String C_COLUMN_TIMESTAMP = "timestamp";
//
    public static final String MESSAGE_TABLE = "Message";
    public static final String M_COLUMN_DETAIL = "detail";
    public static final String M_COLUMN_TIME = "time";
    public static final String M_COLUMN_IS_SENDER = "is_sender";
    public static final String M_COLUMN_C_ID = "c_id";     //conversation id



    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery1 = "CREATE TABLE " + CONVERSATION_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_COLUMN_NAME + " TEXT, " +
                C_COLUMN_LAST_MESSAGE + " TEXT, " +
                C_COLUMN_TIMESTAMP + " INTEGER)";
        db.execSQL(sqlQuery1);

        String sqlQuery2 = "CREATE TABLE " + MESSAGE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                M_COLUMN_DETAIL + " TEXT, " +
                M_COLUMN_TIME + " TEXT, " +
                M_COLUMN_IS_SENDER + " INTEGER, " +
                M_COLUMN_C_ID + " INTEGER, FOREIGN KEY (" + M_COLUMN_C_ID + ") REFERENCES " +
                CONVERSATION_TABLE + " (id) on DELETE CASCADE on UPDATE CASCADE)";
        db.execSQL(sqlQuery2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CONVERSATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
        onCreate(db);
    }

    public void addNewConversation(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        long timeStamp = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(C_COLUMN_NAME, name);
        cv.put(C_COLUMN_LAST_MESSAGE, "");
        cv.put(C_COLUMN_TIMESTAMP, timeStamp);
        long result =  db.insert(CONVERSATION_TABLE, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Added Successfully!", LENGTH_SHORT).show();
        }
    }

    Cursor ReadAllData(){
        String Query = "SELECT * FROM " + CONVERSATION_TABLE + " ORDER BY " + C_COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(Query, null);
        }
        return cursor;
    }

    public void addNewMessage(int conversationID, String messageDetail,String time, int isSender ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(M_COLUMN_DETAIL, messageDetail);
        content.put(M_COLUMN_TIME, time);
        content.put(M_COLUMN_IS_SENDER, isSender);
        content.put(M_COLUMN_C_ID, conversationID);
        long result =  db.insert(MESSAGE_TABLE, null, content);
        if(result == -1){
            Toast.makeText(context, "Failed", LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Added Successfully!", LENGTH_SHORT).show();
        }
    }

    public Cursor ReadMessagesData(String CID){
        //SELECT c.name, m.detail, m.time, m.isSender from conversation as c JOIN message as m on c.id = m.CID where CID = '3'
        String Query = "SELECT c." + C_COLUMN_NAME +
                ", m." + M_COLUMN_DETAIL +
                ", m." + M_COLUMN_TIME +
                ", m." + M_COLUMN_IS_SENDER + " FROM " +
                CONVERSATION_TABLE + " as c JOIN " + MESSAGE_TABLE + " as m on c.id = m." +
                M_COLUMN_C_ID + " WHERE " + M_COLUMN_C_ID + " = " + CID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(Query, null);
        }
        return cursor;
    }

    public void updateConversationMessageTimestamp(String id, String message, long timeStamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(C_COLUMN_LAST_MESSAGE, message);
        content.put(C_COLUMN_TIMESTAMP, timeStamp);

        long result = db.update(CONVERSATION_TABLE, content, "id=?", new String[]{id});
        if (result == -1){
            Toast.makeText(context, "Failed to update!", LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Update Successfully", LENGTH_SHORT).show();
        }

    }

    public void removeDatabase(){
        Toast.makeText(context, "Ja kam kr ja k", LENGTH_SHORT).show();
//        context.deleteDatabase(DATABASE_NAME);
    }

    public void DeleteOneConversation(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CONVERSATION_TABLE,"id=?",new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete!", LENGTH_SHORT).show();
        }{
            Toast.makeText(context, "Deleted Successfully!", LENGTH_SHORT).show();
        }
    }

    public void DeleteAllConversation(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CONVERSATION_TABLE);
        Toast.makeText(context, "Deleted Successfully!", LENGTH_SHORT).show();
    }

}
