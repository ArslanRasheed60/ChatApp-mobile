package com.example.chatapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.conversation.MainActivity;
import com.example.chatapp.sqliteDB.ChatDbDAO;
import com.example.chatapp.sqliteDB.MyDataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ConversationMainActivityLists extends AppCompatActivity implements ConversationAdaptor.SelectItemListener{

    RecyclerView recyclerView;
    FloatingActionButton add_Button;
    FloatingActionButton remove_db_button;

    IChatInterface dao;
    public ArrayList<Person>  personsList;

    ConversationAdaptor myAdapt;
    ConversationAdaptor.SelectItemListener listener;
    ActivityResultLauncher<Intent> conversation_activity_launcher;

    //alert box
    AlertDialog.Builder removeBuilder;
    EditText input;
    AlertDialog removeDialog;
    int recyclerViewItemIdForDeletion;

    //alert box
    AlertDialog.Builder addBuilder;
    AlertDialog addDialog;

    //handle delete
    Boolean isOneMessageOnHold;

    //handle menu and search
    SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_main_lists);

        //connect databases
        dao = new ChatDbDAO(this);
        Globals.dao = dao;
        //load data
        personsList = Person.load(dao);
//        personsList = new ArrayList<>();

        recyclerView = findViewById(R.id.ConversationMessageLists);
        add_Button = findViewById(R.id.floating_add_button);
        add_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.show();
            }
        });

        myAdapt = new ConversationAdaptor(ConversationMainActivityLists.this, personsList, this );
        recyclerView.setLayoutManager(new LinearLayoutManager(ConversationMainActivityLists.this));
        recyclerView.setAdapter(myAdapt);

        //get results from the messages activity
        conversation_activity_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent data = result.getData();
                            String personId = data.getStringExtra("id");
                            String personLastMessage = data.getStringExtra("lastMessage");
                            long personTimeStamp = data.getLongExtra("timeStamp", -1);
                            int recyclerViewItemId = data.getIntExtra("recyclerViewItemId", -1);

                            if(personTimeStamp != -1 && !Objects.equals(personLastMessage, "" ) && recyclerViewItemId != -1){
                                for (Person person :
                                        personsList) {
                                    if(person.getId() == Integer.parseInt(personId)){
                                        person.update(personLastMessage, personTimeStamp);
                                        break;
                                    }
                                }
                                //move person to the top of the list
                                Person updatedItem = personsList.remove(recyclerViewItemId);
                                personsList.add(0, updatedItem);
                            }
                            myAdapt.notifyDataSetChanged();
                        }
                    }
                }
        );

        //pop ups
        CreateAndHandleAlertBox();
        UserAddingPop();
        recyclerViewItemIdForDeletion = -1;
    }



    @Override
    public void onItemClick(Person person, int pos) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", Integer.toString(person.getId()));
        intent.putExtra("name", person.getName());
        intent.putExtra("recyclerViewItemId", pos);
        conversation_activity_launcher.launch(intent);
    }

    @Override
    public void onItemLongClick(Person person, int pos) {
        isOneMessageOnHold = true;
        recyclerViewItemIdForDeletion = pos;
        removeDialog.show();
    }

    //alert box creating and handle
    public void CreateAndHandleAlertBox(){

        // Create a new AlertDialog builder
        removeBuilder = new AlertDialog.Builder(this);

        // Create a LinearLayout to hold the buttons
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);


        TextView textView = new TextView(this);
        textView.setText("Delete these conversations?");
        textView.setBackgroundColor(2839391);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        textView.setHeight(250);


        // Create the "Delete" button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setBackgroundColor(2839391);
        deleteButton.setTextColor(Color.RED);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the conversation
                ArrayList<Person> UpdatedList = new ArrayList<>();
                if(isOneMessageOnHold){
                    if(recyclerViewItemIdForDeletion != -1){
                        Person dPerson = personsList.get(recyclerViewItemIdForDeletion);
                        personsList.remove(recyclerViewItemIdForDeletion);
                        dPerson.delete();
                        removeDialog.dismiss();
                    }
                }else{
                    Person.deleteAll(dao);
                    personsList.removeAll(personsList);
                    removeDialog.dismiss();
                }
                myAdapt.notifyDataSetChanged();
            }
        });

        // Create the "Cancel" button
        Button cancelButton = new Button(this);
        cancelButton.setText("Cancel");
        cancelButton.setBackgroundColor(2839391);
        cancelButton.setTextColor(Color.RED);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog box
                removeDialog.dismiss();
            }
        });

        // Add the buttons to the layout
        layout.addView(textView);
        layout.addView(deleteButton);
        layout.addView(cancelButton);

        // Set the layout as the dialog view
        removeBuilder.setView(layout);

        // Create and show the AlertDialog
        removeDialog = removeBuilder.create();

        // Customize the dialog box background and corners
        removeDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_background);
    }

    //Pop us for adding the user
    public void UserAddingPop(){

        // Create a new AlertDialog builder
        addBuilder = new AlertDialog.Builder(this);

        // Create a LinearLayout to hold the buttons
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        int backgroundColor = 7895160;

        TextView textView = new TextView(this);
        textView.setText("Add New User");
        textView.setBackgroundColor(backgroundColor);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//        textView.setPadding(5,15,5,15);
        textView.setHeight(250);

        EditText editText = new EditText(this);
        textView.setBackgroundColor(backgroundColor);
        editText.setTextColor(Color.WHITE);
        editText.setGravity(Gravity.CENTER);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        // Create the "Delete" button
        Button AddButton = new Button(this);
        AddButton.setText("Add Person");
        AddButton.setBackgroundColor(backgroundColor);
        AddButton.setTextColor(Color.RED);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().equals("")){
                    Toast.makeText(ConversationMainActivityLists.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }else{
                    Person newPerson = new Person(editText.getText().toString().trim(), dao);
                    newPerson.save();
                    editText.setText("");
                }
                Intent intent = new Intent(ConversationMainActivityLists.this, ConversationMainActivityLists.class);
                startActivity(intent);
                finish();
            }
        });

        // Create the "Cancel" button
        Button cancelButton = new Button(this);
        cancelButton.setText("Cancel");
        cancelButton.setBackgroundColor(backgroundColor);
        cancelButton.setTextColor(Color.RED);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog box
                addDialog.dismiss();
            }
        });

        // Add the buttons to the layout
        layout.addView(textView);
        layout.addView(editText);
        layout.addView(AddButton);
        layout.addView(cancelButton);

        // Set the layout as the dialog view
        addBuilder.setView(layout);

        // Create and show the AlertDialog
        addDialog = addBuilder.create();

        // Customize the dialog box background and corners
        addDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_background);

    }


    public boolean onCreateOptionsMenu(Menu menu){
        //Inflating menu
        getMenuInflater().inflate(R.menu.main_page_menu, menu);

        //capturing reference of search button
        MenuItem search_button = menu.findItem(R.id.search_button);

        //adding search view in menu
        SearchView searchView = (SearchView) search_button.getActionView();
        searchView.setQueryHint("Search...");

        //adding on text change listener in search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(myAdapt instanceof ConversationAdaptor){
                    ConversationAdaptor mA = (ConversationAdaptor) myAdapt;
                    mA.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if (item.getItemId() == R.id.clearChat) {
            isOneMessageOnHold = false;
            removeDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}