package com.example.chatapp.signUp;

import static android.graphics.Color.RED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.chatapp.Globals.*;
import com.example.chatapp.ConversationMainActivityLists;
import com.example.chatapp.Globals;
import com.example.chatapp.R;
import com.example.chatapp.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText signUpName, signUpPhoneNumber, signUpPassword;
    TextView popUpPhoneNumber, popUpPassword;
    Button login, signUpVerify;
    FirebaseAuth firebaseAuth;
    boolean isPhoneNumberSatisfyFormat = false;
    boolean isPasswordSatisfyFormat = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpName = findViewById(R.id.signUpName);
        signUpPhoneNumber = findViewById(R.id.signUpPhoneNumber);
        signUpPassword = findViewById(R.id.signUpPassword);
        popUpPhoneNumber = findViewById(R.id.popUpSignUpPhoneNumber);
        popUpPassword = findViewById(R.id.popUpSignUpPassword);

        login = findViewById(R.id.login);
        signUpVerify = findViewById(R.id.signUpVerify);

        firebaseAuth = FirebaseAuth.getInstance();

        //verify phone number and password on text change
        verifyNumberOnTextChange();
        verifyPasswordOnTextChange();

        //create new user
        createUserOnSignUp();
        //start login activity
        switchActivityOnLogin();
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

    private void verifyNumberOnTextChange(){
        this.signUpPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                popUpPhoneNumber.setVisibility(View.VISIBLE);
                ColorStateList colorStateList;
                if(Globals.verifyPhoneNumber(charSequence.toString().trim())){
                    popUpPhoneNumber.setText("Format Verified. ");
                    int greenColor = getResources().getColor(R.color.ForestGreen,null);
                    colorStateList = ColorStateList.valueOf(greenColor);
                    isPhoneNumberSatisfyFormat = true;
                    if(isPasswordSatisfyFormat){
                        signUpVerify.setEnabled(true);
                    }
                }else{
                    popUpPhoneNumber.setText("Starts with 0, Should contain 11 letters");
                    colorStateList = ColorStateList.valueOf(RED);
                    signUpVerify.setEnabled(false);
                    isPhoneNumberSatisfyFormat = false;
                }
                popUpPhoneNumber.setBackgroundTintList(colorStateList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void verifyPasswordOnTextChange(){
        signUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                popUpPassword.setVisibility(View.VISIBLE);
                ColorStateList colorStateList;
                if(charSequence.toString().trim().length() > 5){
                    popUpPassword.setText("Format Verified. ");
                    int greenColor = getResources().getColor(R.color.ForestGreen,null);
                    colorStateList = ColorStateList.valueOf(greenColor);
                    isPasswordSatisfyFormat = true;
                    if(isPhoneNumberSatisfyFormat){
                        signUpVerify.setEnabled(true);
                    }
                }else{
                    popUpPassword.setText("Min 6 letters");
                    colorStateList = ColorStateList.valueOf(RED);
                    signUpVerify.setEnabled(false);
                    isPasswordSatisfyFormat = false;
                }
                popUpPassword.setBackgroundTintList(colorStateList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void createUserOnSignUp(){
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
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(CHAT_DB);
                                        Map<String, Object> childObject = new HashMap<>();
                                        childObject.put(Full_Name, name);
                                        usersRef.child(phoneNumber).setValue(childObject);
                                        //switch to main activity
                                        Intent intent = new Intent(getApplicationContext(), ConversationMainActivityLists.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, "Authentication failed. User Already Exists",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void switchActivityOnLogin(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}