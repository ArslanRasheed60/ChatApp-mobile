package com.example.chatapp;

import static com.example.chatapp.Globals.*;
import static com.example.chatapp.R.drawable.rounded;
import static com.example.chatapp.R.drawable.rounded_background;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.example.chatapp.contacts.Contact;
import com.example.chatapp.contacts.ContactAdaptor;
import com.example.chatapp.conversation.MainActivity;
import com.example.chatapp.firebaseDb.ChatFirebaseDAO;
import com.example.chatapp.login.LoginActivity;
import com.example.chatapp.sqliteDB.ChatDbDAO;
import com.example.chatapp.sqliteDB.MyDataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Objects;

public class ConversationMainActivityLists extends AppCompatActivity implements ConversationAdaptor.SelectItemListener, ContactAdaptor.SelectItemListenerContact{

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

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //handle contacts
    RecyclerView contactRecyclerView;
    ContactAdaptor contactAdaptor;
    ArrayList<Contact> contactArrayList;
    DialogPlus dialogPlus;

    private static final int CONTACT_PICKER_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //firebase Auth
        if(!isPersistenceEnabled){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //firebase db
        dao = new ChatFirebaseDAO(new ChatFirebaseDAO.DataObserver() {
            @Override
            public void update() {
                refresh();
            }
        });


        setContentView(R.layout.activity_conversation_main_lists);

        //connect databases
        //sqlite
//        dao = new ChatDbDAO(this);

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
                                    if(Objects.equals(person.getId(), personId)){
                                        person.update(personLastMessage, personTimeStamp, MessageType.SENT.toString());
                                        break;
                                    }
                                }
                                //move person to the top of the list
                                Person updatedItem = personsList.remove(recyclerViewItemId);
                                personsList.add(0, updatedItem);
                                refresh();
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

        //handling contacts
        dialogPlus = DialogPlus.newDialog(this).setContentHolder(new ViewHolder(R.layout.contact_list))
                .setExpanded(true, 1500)
                .setBackgroundColorResId(R.color.Black1)
                .create();

        View view = dialogPlus.getHolderView();
        view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Black1,null ));
        contactArrayList = new ArrayList<>();
        contactRecyclerView = view.findViewById(R.id.contactnamelists);
        contactAdaptor = new ContactAdaptor( contactArrayList, this );
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactRecyclerView.setBackgroundResource(rounded);
        contactRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        contactRecyclerView.setAdapter(contactAdaptor);
    }

    public void refresh(){
//        NotesViewModel vm = new ViewModelProvider(getActivity()).get(NotesViewModel.class);
        personsList = Person.load(dao);
        if (personsList != null){
            myAdapt.updateData(personsList);
        }
    }


    @Override
    public void onItemClick(Person person, int pos) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", person.getId());
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
        removeDialog.getWindow().setBackgroundDrawableResource(rounded_background);
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

        EditText editTextName = new EditText(this);
        textView.setBackgroundColor(backgroundColor);
        editTextName.setTextColor(Color.WHITE);
        editTextName.setGravity(Gravity.CENTER);
        editTextName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        EditText editTextPhoneNumber = new EditText(this);
        textView.setBackgroundColor(backgroundColor);
        editTextPhoneNumber.setTextColor(Color.WHITE);
        editTextPhoneNumber.setGravity(Gravity.CENTER);
        editTextPhoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

        // Create the "Delete" button
        Button AddButton = new Button(this);
        AddButton.setText("Add Person");
        AddButton.setBackgroundColor(backgroundColor);
        AddButton.setTextColor(Color.RED);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextName.getText().toString().trim().equals("") || editTextPhoneNumber.getText().toString().trim().equals("")){
                    Toast.makeText(ConversationMainActivityLists.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }else{
                    Person newPerson;

                    String phoneNumber_ID = formatPhoneNumber(editTextPhoneNumber.getText().toString().trim());
                    if(phoneNumber_ID.equals("-1")){
                        Toast.makeText(ConversationMainActivityLists.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }else{
                        //add phone Number Verification;
                        if(dao instanceof ChatFirebaseDAO){
                            long timeStamp = System.currentTimeMillis();
                            newPerson = new Person(phoneNumber_ID,editTextName.getText().toString().trim(), "" ,timeStamp,MessageType.SENT.toString(),dao);
                        }else{
                            newPerson = new Person(phoneNumber_ID,editTextName.getText().toString().trim(), dao);
                        }
                        newPerson.save();
                        editTextName.setText("");
                        editTextPhoneNumber.setText("");
                        personsList.add(0,newPerson);
                        myAdapt.notifyDataSetChanged();
                    }
                    addDialog.dismiss();
                }
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
        layout.addView(editTextName);
        layout.addView(editTextPhoneNumber);
        layout.addView(AddButton);
        layout.addView(cancelButton);

        // Set the layout as the dialog view
        addBuilder.setView(layout);

        // Create and show the AlertDialog
        addDialog = addBuilder.create();

        // Customize the dialog box background and corners
        addDialog.getWindow().setBackgroundDrawableResource(rounded_background);

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
        }else if(item.getItemId() == R.id.logoutUser){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.contacts1){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, CONTACT_PICKER_REQUEST);
        }else if(item.getItemId() == R.id.contacts2){
            getPhoneContacts();
            showContactPopUp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST && resultCode == RESULT_OK) {
            // The user selected a contact
            Uri contactUri = data.getData();

            // Use the contactUri to query the contact data
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Get the contact name and phone number
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String phoneNumber_ID = formatPhoneNumber(phoneNumber);
                if(phoneNumber_ID.equals("-1")){
                    Toast.makeText(ConversationMainActivityLists.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }else{
                    Person newPerson;
                    if(dao instanceof ChatFirebaseDAO){
                        long timeStamp = System.currentTimeMillis();
                        newPerson = new Person(phoneNumber_ID,name, "" ,timeStamp,MessageType.SENT.toString(),dao);
                    }else{
                        newPerson = new Person(phoneNumber_ID,name, dao);
                    }
                    newPerson.save();
                    personsList.add(0,newPerson);
                    myAdapt.notifyDataSetChanged();
                }
                cursor.close();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getPhoneContacts(){
        contactArrayList.removeAll(contactArrayList);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},0);
        }
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                @SuppressLint("Range")
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range")
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Contact newContact = new Contact(contactName, contactNumber);
                contactArrayList.add(newContact);

                Log.d("ccccc", contactName + "---" + contactNumber);
            }
        }else{
            Toast.makeText(this,"No Contact Exists", Toast.LENGTH_SHORT).show();
        }
        contactAdaptor.notifyDataSetChanged();
    }

    public void showContactPopUp(){
        dialogPlus.show();
    }

    @Override
    public void onContactItemClick(Contact contact, int pos) {
        String contactNameId = contact.getName();

        String contactPhoneNumberID = formatPhoneNumber(contact.getPhoneNumber());
        if(contactPhoneNumberID.equals("-1")){
            Toast.makeText(ConversationMainActivityLists.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
        }else{
            Person newPerson;
            //add phone Number Verification;
            if(dao instanceof ChatFirebaseDAO){
                long timeStamp = System.currentTimeMillis();
                newPerson = new Person(contactPhoneNumberID,contactNameId, "" ,timeStamp,MessageType.SENT.toString(),dao);
            }else{
                newPerson = new Person(contactPhoneNumberID,contactNameId, dao);
            }
            newPerson.save();
            personsList.add(0,newPerson);
            myAdapt.notifyDataSetChanged();
        }
        dialogPlus.dismiss();
    }
}