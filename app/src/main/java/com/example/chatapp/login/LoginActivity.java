package com.example.chatapp.login;

import static com.example.chatapp.Globals.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.ConversationMainActivityLists;
import com.example.chatapp.R;
import com.example.chatapp.signUp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginPhoneNumber, loginPassword;
    Button loginVerify, signUp;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        loginPhoneNumber = findViewById(R.id.loginPhoneNumber);
        loginPassword = findViewById(R.id.loginPassword);
        loginVerify = findViewById(R.id.loginVerify);
        signUp = findViewById(R.id.signUp);
        firebaseAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = loginPhoneNumber.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if(phoneNumber.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Field is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(phoneNumber + Email_Extension, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
//                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        //switch to main activity
                                        Intent intent = new Intent(LoginActivity.this, ConversationMainActivityLists.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
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
            Intent intent = new Intent(LoginActivity.this, ConversationMainActivityLists.class);
            startActivity(intent);
            finish();
        }
    }
}