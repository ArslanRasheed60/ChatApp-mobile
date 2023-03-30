package com.example.chatapp.conversation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.MyDataBaseHelper;
import com.example.chatapp.R;
import com.example.chatapp.RandomText;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String MESSAGE_SENDER = "Arslan";
    private ArrayList<Message> SRMessages;
    private EditText editText;
    RecyclerView recyclerViewMessageLists;
    private RecyclerView.Adapter mAdaptor;

    //
    private String receiverId , receiverName;
    MyDataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SRMessages = new ArrayList<>();
        recyclerViewMessageLists = (RecyclerView) findViewById(R.id.messageLists);
        recyclerViewMessageLists.setHasFixedSize(true);

        //linear layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessageLists.setLayoutManager(linearLayoutManager);

        //set id of edit text
        editText = findViewById(R.id.sender1Text);

        //adaptor
        mAdaptor = new MessageAdaptor(SRMessages);
        recyclerViewMessageLists.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewMessageLists.setAdapter(mAdaptor);


        //Database helper
        myDB = new MyDataBaseHelper(MainActivity.this);

        //data passed from parent
        Intent intent = getIntent();
        receiverId = intent.getStringExtra("id");
        receiverName = intent.getStringExtra("name");
        storeMessageDataInArrays();
    }


    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleOnClick(View v) {

        String convert_editText = editText.getText().toString();

        if(v.getId() == R.id.sender1TextBtn && !convert_editText.equals("")){


            long timeStamp = System.currentTimeMillis();
            Date dateTime = new Date(timeStamp);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatted = new SimpleDateFormat("HH:mm: a");
            String formattedTime = formatted.format(dateTime);


//            LocalTime time = LocalTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
//            String formattedTime = time.format(formatter);
            Message newMessage = new Message(MESSAGE_SENDER, convert_editText, formattedTime,0 );
            SRMessages.add(newMessage);

//            messageLists.setAdapter(new MessageAdaptor(SRMessages));
            mAdaptor.notifyDataSetChanged();
            editText.setText("");

            //database update for sender
//            MyDataBaseHelper myDB = new MyDataBaseHelper(MainActivity.this);
            myDB.addNewMessage(Integer.parseInt(receiverId),newMessage.getMessage(), newMessage.getTime(), newMessage.getType());

            Message newMessage2 = new Message(receiverName, RandomText.generateRandomText(80), formattedTime,1 );
            SRMessages.add(newMessage2);
//            messageLists.setAdapter(new MessageAdaptor(SRMessages));
            mAdaptor.notifyDataSetChanged();
            myDB.addNewMessage(Integer.parseInt(receiverId), newMessage2.getMessage(), newMessage2.getTime(), newMessage2.getType());

            //add message to last index of recycler view
            recyclerViewMessageLists.scrollToPosition(mAdaptor.getItemCount() - 1);

            //update conversation table in the database
            myDB.updateConversationMessageTimestamp(receiverId, newMessage2.getMessage(), timeStamp);
        }
        else{
            Toast toast = Toast.makeText(this,"Field is empty",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void storeMessageDataInArrays(){
        Cursor cursor = myDB.ReadMessagesData(receiverId);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "There are no current messages exists!", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                int senderType = Integer.parseInt(cursor.getString(3));
                String username = senderType == 0 ? MESSAGE_SENDER : cursor.getString(0);
                Message message = new Message(username,cursor.getString(1),cursor.getString(2),senderType);
                SRMessages.add(message);
//              personIdLists.add(cursor.getString(0));
//              personNamesLists.add(cursor.getString(1));
            }
        }
        mAdaptor.notifyDataSetChanged();
    }

}