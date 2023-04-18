package com.example.chatapp.contacts;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ConversationAdaptor;
import com.example.chatapp.Person;
import com.example.chatapp.R;
import com.example.chatapp.conversation.Message;
import com.example.chatapp.conversation.MessageAdaptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContactAdaptor extends RecyclerView.Adapter<ContactAdaptor.ContactViewHolder>{

    private ArrayList<Contact> contacts;
    private SelectItemListenerContact listenerContact;

    public ContactAdaptor(ArrayList<Contact> contacts, SelectItemListenerContact listenerContact){
        this.contacts = contacts;
        this.listenerContact = listenerContact;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view =  layoutInflater.inflate(R.layout.contact_field, parent, false);
        return new ContactAdaptor.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        String name = contacts.get(position).getName();
        holder.contactTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView contactTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            //sender
            contactTextView = (TextView) itemView.findViewById(R.id.contactnamefield);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    listenerContact.onContactItemClick(contacts.get(pos), pos);
                }
            });
        }
    }

    public interface SelectItemListenerContact{
        public void onContactItemClick(Contact contact, int pos);
    }
}
