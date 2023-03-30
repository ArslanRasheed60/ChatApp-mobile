package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ConversationAdaptor extends RecyclerView.Adapter<ConversationAdaptor.MyViewHolder> {

    Context context;
    public ArrayList<Person>  personsList;
    private SelectItemListener listener;

    public ConversationAdaptor(Context context, ArrayList<Person> personsList, SelectItemListener listener){
        this.context = context;
        this.personsList = personsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =  layoutInflater.inflate(R.layout.conversation_field, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdaptor.MyViewHolder holder, int position) {
        String name = personsList.get(position).getName();
        String lastMessage = personsList.get(position).getLastMessage();

        long timeStamp = personsList.get(position).getTimeStamp();
        Date dateTime = new Date(timeStamp);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatted = new SimpleDateFormat("HH:mm: a");
        String formattedTime = formatted.format(dateTime);

        holder.textViewName.setText(name);
        if(Objects.equals(lastMessage, "")){
            holder.textViewMessage.setText("");
            holder.textViewTime.setText("");
        }else{
            holder.textViewMessage.setText(lastMessage);
            holder.textViewTime.setText(formattedTime);
        }


    }

    @Override
    public int getItemCount() {
        return personsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTime;
        LinearLayout conversationField;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.conversationPersonNameField);
            textViewMessage = itemView.findViewById(R.id.conversationMessageField);
            textViewTime = itemView.findViewById(R.id.conversationTimeField);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(personsList.get(pos), pos);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        listener.onItemLongClick(personsList.get(pos), pos);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public interface SelectItemListener{
        public void onItemClick(Person person, int pos);
        public void onItemLongClick(Person person, int pos);
    }

}
