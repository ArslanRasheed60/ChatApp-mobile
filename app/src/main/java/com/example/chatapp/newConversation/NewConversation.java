package com.example.chatapp.newConversation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.MyDataBaseHelper;
import com.example.chatapp.R;

public class NewConversation extends AppCompatActivity {

    EditText addPersonText;
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        addPersonText = (EditText) findViewById(R.id.personName);
        addButton = (Button) findViewById(R.id.addPersonBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addPersonText.getText().toString().trim().equals("")){
                    Toast.makeText(NewConversation.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }else{
                    MyDataBaseHelper myDB = new MyDataBaseHelper(NewConversation.this);
                    myDB.addNewConversation(addPersonText.getText().toString().trim());
                    addPersonText.setText("");
//                    Intent intent = new Intent();
//                    setResult(RESULT_OK, intent);
//                    finish();
                }
            }
        });

    }
}