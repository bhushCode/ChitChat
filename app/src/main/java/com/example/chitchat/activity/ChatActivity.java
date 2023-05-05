package com.example.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.adapter.MessagesAdapter;
import com.example.chitchat.modelclass.Messages;
import com.example.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
String ReciverImage,ReciverUID,ReciverName,senderUID,senderRoom,reciverRoom;
CircleImageView profileImage;
TextView reciverName;
FirebaseDatabase database;
FirebaseAuth firebaseAuth;
public static String  sImage;
public static String  rImage;
CardView sendBtn;
EditText edtMessage;
RecyclerView messageAdapter;
ArrayList<Messages> messagesArrayList;
MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ReciverName = getIntent().getStringExtra("name");
        ReciverImage = getIntent().getStringExtra("RecieverImage").toString();
        ReciverUID = getIntent().getStringExtra("uid");
        messagesArrayList = new ArrayList<>();

        adapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter = findViewById(R.id.messageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setAdapter(adapter);
        messageAdapter.setLayoutManager(linearLayoutManager);
        sendBtn = findViewById(R.id.sendBtn);
        edtMessage=findViewById(R.id.edtMessage);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reciverName=findViewById(R.id.reciverName);

        profileImage = findViewById(R.id.profile_image);
        Picasso.get().load(ReciverImage).into(profileImage);
        reciverName.setText(""+ReciverName);

        senderUID=firebaseAuth.getUid();

        senderRoom=senderUID+ReciverUID;
        reciverRoom=ReciverUID+senderUID;

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              sImage=  snapshot.child("imageuri").toString();
              rImage=ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String message = edtMessage.getText().toString();
              if(message.isEmpty())
              {
                  Toast.makeText(ChatActivity.this, "please enter valid message", Toast.LENGTH_SHORT).show();
                  return;
              }
              edtMessage.setText("");
              Date date = new Date();
              Messages messages = new Messages(message,senderUID,date.getTime());
              database.getReference().child("chats")
                      .child(senderRoom)
                      .child("messages")
                      .push()
                      .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              database.getReference().child("chats")
                                      .child(reciverRoom)
                                      .child("messages")
                                      .push()
                                      .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {

                                          }
                                      });
                          }
                      });
            }
        });
    }
}