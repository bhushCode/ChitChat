package com.example.chitchat.adapter;

import static com.example.chitchat.activity.ChatActivity.rImage;
import static com.example.chitchat.activity.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.R;
import com.example.chitchat.modelclass.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND =1;
    int ITEM_RECIVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return new senderViewHolder(view);

        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item,parent,false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
           Messages messages = messagesArrayList.get(position);
           if(holder.getClass()==senderViewHolder.class)
           {
               senderViewHolder viewHolder=(senderViewHolder) holder;
               viewHolder.txtmessage.setText(messages.getMessage());
               Picasso.get().load(sImage).into(viewHolder.circleImageView);
           }
           else
           {
               reciverViewHolder viewHolder = (reciverViewHolder) holder;
               viewHolder.txtmessage.setText(messages.getMessage());
               Picasso.get().load(rImage).into(viewHolder.circleImageView);

           }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages= messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId()))
        {
            return ITEM_SEND;
        }
        else
        {
           return ITEM_RECIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
           circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.txtMessages);
        }
    }
    class reciverViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.txtMessages);
        }
    }
}
