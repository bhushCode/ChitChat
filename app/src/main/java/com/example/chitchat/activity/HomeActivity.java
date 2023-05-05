package com.example.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.adapter.UserAdapter;
import com.example.chitchat.modelclass.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
FirebaseAuth auth;
RecyclerView mainUserRecyclerView;
UserAdapter adapter;
FirebaseDatabase database;
ArrayList<Users> usersArrayList;

ImageView imgLogout, imgSetting;

boolean doubleBackToExitPressedOnce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgLogout = findViewById(R.id.img_logOut);
        imgSetting = findViewById(R.id.img_Setting);

        auth=FirebaseAuth.getInstance();
        usersArrayList = new ArrayList<>();

        adapter = new UserAdapter(HomeActivity.this,usersArrayList);

        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecyclerView.setAdapter(adapter);



        database =FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       if(auth.getCurrentUser()==null)
       {
         startActivity(new Intent(HomeActivity.this, MainActivity.class));
       }

       imgLogout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Dialog dialog = new Dialog(HomeActivity.this,R.style.Dialoge);
               dialog.setContentView(R.layout.dialog_layout);



               TextView nobtn,yesbtn;
               nobtn=dialog.findViewById(R.id.nobtn);
               yesbtn=dialog.findViewById(R.id.yesbtn);

               yesbtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       FirebaseAuth.getInstance().signOut();
                       startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
                   }
               });
               nobtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialog.dismiss();
                   }
               });
               dialog.show();
           }
       });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                  finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce) {
            super.onBackPressed();
              finish();
              finishAffinity();
           return ;
        }
        Toast.makeText(this, "please click Back again to Exit", Toast.LENGTH_SHORT).show();
      doubleBackToExitPressedOnce = true;
    }
}