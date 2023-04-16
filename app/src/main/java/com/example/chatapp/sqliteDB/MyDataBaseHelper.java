package com.example.chatapp.sqliteDB;

import static android.widget.Toast.LENGTH_SHORT;

import static com.example.chatapp.Globals.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Chats.db";


    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery1 = "CREATE TABLE " + CONVERSATION_TABLE + " (id TEXT, " +
                C_COLUMN_NAME + " TEXT, " +
                C_COLUMN_LAST_MESSAGE + " TEXT, " +
                C_COLUMN_TIMESTAMP + " INTEGER, " +
                C_COLUMN_MESSAGE_TYPE + " TEXT)";
        db.execSQL(sqlQuery1);

        String sqlQuery2 = "CREATE TABLE " + MESSAGE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                M_COLUMN_DETAIL + " TEXT, " +
                M_COLUMN_TIME + " TEXT, " +
                M_COLUMN_IS_SENDER + " INTEGER, " +
                M_COLUMN_C_ID + " TEXT, FOREIGN KEY (" + M_COLUMN_C_ID + ") REFERENCES " +
                CONVERSATION_TABLE + " (id) on DELETE CASCADE on UPDATE CASCADE)";
        db.execSQL(sqlQuery2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CONVERSATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
        onCreate(db);
    }










}
