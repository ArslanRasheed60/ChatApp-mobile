package com.example.chatapp.signUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.chatapp.Globals.*;
import com.example.chatapp.ConversationMainActivityLists;
import com.example.chatapp.R;
import com.example.chatapp.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class SignUpActivity extends AppCompatActivity {

    EditText signUpName, signUpPhoneNumber, signUpPassword;
    Button login, signUpVerify;
    FirebaseAuth firebaseAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpName = findViewById(R.id.signUpName);
        signUpPhoneNumber = findViewById(R.id.signUpPhoneNumber);
        signUpPassword = findViewById(R.id.signUpPassword);

        login = findViewById(R.id.login);
        signUpVerify = findViewById(R.id.signUpVerify);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signUpName.getText().toString().trim();
                String phoneNumber = signUpPhoneNumber.getText().toString().trim();
                String password = signUpPassword.getText().toString().trim();


                if( name.equals("") || phoneNumber.equals("") || password.equals("")){
                    Toast.makeText(SignUpActivity.this, "Field is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(phoneNumber + Email_Extension, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        //
                                        if(!isPersistenceEnabled){
                                            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                                            isPersistenceEnabled = true;
                                        }
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(CHAT_DB);
                                        usersRef.child(phoneNumber).child(Full_Name).setValue(name);
                                        usersRef.child(phoneNumber).child(CONVERSATION_TABLE).setValue(null);
                                        usersRef.child(phoneNumber).child(MESSAGE_TABLE).setValue(null);
                                        //switch to main activity
                                        Intent intent = new Intent(getApplicationContext(), ConversationMainActivityLists.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //start login activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            //switch to main activity
            Intent intent = new Intent(getApplicationContext(), ConversationMainActivityLists.class);
            startActivity(intent);
            finish();
        }
    }
}