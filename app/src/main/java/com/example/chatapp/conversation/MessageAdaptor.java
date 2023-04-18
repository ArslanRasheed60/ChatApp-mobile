package com.example.chatapp.conversation;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageViewHolder>{
private ArrayList<Message> messages;

public MessageAdaptor(ArrayList<Message> messages){
    this.messages = messages;
}

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getType() == 0) {
            return 0; // sender
        } else {
            return 1; // receiver
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;

        if (viewType == 0) {
            layoutId = R.layout.sender_message_layout;
        } else {
            layoutId = R.layout.receiver_message_layout;
        }

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String title = messages.get(position).getUsername();
        String message = messages.get(position).getMessage();
        long time = messages.get(position).getTime();

        Date dateTime = new Date(time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatted = new SimpleDateFormat("HH:mm: a");
        String formattedTime = formatted.format(dateTime);

        //update sender text
        if (messages.get(position).getType() == 0){
            holder.senderTextTitle.setText(title);
            holder.senderTextMessage.setText(message);
            holder.senderTextTime.setText(formattedTime);
        }
        //update receiver text
        else{
            holder.receiverTextTitle.setText(title);
            holder.receiverTextMessage.setText(message);
            holder.receiverTextTime.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

    ImageView senderIcon;
    TextView senderTextMessage;
    TextView senderTextTitle;
    TextView senderTextTime;

    ImageView receiverIcon;
    TextView receiverTextMessage;
    TextView receiverTextTitle;
    TextView receiverTextTime;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        //sender
        senderIcon = (ImageView) itemView.findViewById(R.id.sender_image);
        senderTextMessage = (TextView) itemView.findViewById(R.id.sender_text);
        senderTextTitle = (TextView)  itemView.findViewById(R.id.sender_username);
        senderTextTime = (TextView)  itemView.findViewById(R.id.sender_time);
        //receiver
        receiverIcon = (ImageView) itemView.findViewById(R.id.receiver_image);
        receiverTextMessage = (TextView) itemView.findViewById(R.id.receiver_text);
        receiverTextTitle = (TextView)  itemView.findViewById(R.id.receiver_username);
        receiverTextTime = (TextView)  itemView.findViewById(R.id.receiver_time);
    }
}

}
