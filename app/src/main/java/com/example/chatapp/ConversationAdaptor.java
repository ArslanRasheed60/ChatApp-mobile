package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ConversationAdaptor extends RecyclerView.Adapter<ConversationAdaptor.MyViewHolder> implements Filterable {

    Context context;
    public ArrayList<Person>  personsList;
    public ArrayList<Person> filteredPersonLists;
    private SelectItemListener listener;

    public ConversationAdaptor(Context context, ArrayList<Person> personsList, SelectItemListener listener){
        this.context = context;
        this.personsList = personsList;
        this.filteredPersonLists = personsList;
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
        String name = filteredPersonLists.get(position).getName();
        String lastMessage = filteredPersonLists.get(position).getLastMessage();

        long timeStamp = filteredPersonLists.get(position).getTimeStamp();
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
        return filteredPersonLists.size();
    }

    @Override
    public Filter getFilter() {
        return conversationFilter;
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
                    listener.onItemClick(filteredPersonLists.get(pos), pos);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        listener.onItemLongClick(filteredPersonLists.get(pos), pos);
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

    private Filter conversationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Person> filteredPersons = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredPersons = personsList;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Person person:
                filteredPersonLists){
                    if(person.getName().contains(filterPattern)){
                        filteredPersons.add(person);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredPersons;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredPersonLists = (ArrayList<Person>) filterResults.values;
            notifyDataSetChanged();
        }
    };

}
