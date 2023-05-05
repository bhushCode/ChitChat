package com.example.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.modelclass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
TextView txt_signin,btn_signup;
CircleImageView profile_image;
TextInputLayout reg_name,reg_email,reg_pass,reg_Cpass;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
String imageURI;
    Uri imageuri;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ProgressDialog progressDialog;
    String status = "Hey There I'm Using chitchat";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        txt_signin=findViewById(R.id.txt_signin);
        profile_image=findViewById(R.id.profile_image);
        reg_name=findViewById(R.id.reg_name);
        reg_email=findViewById(R.id.reg_email);
        reg_pass=findViewById(R.id.reg_pass);
        reg_Cpass=findViewById(R.id.reg_Cpass);
        btn_signup=findViewById(R.id.btn_signup);


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String name = reg_name.getEditText().getText().toString();
                String email = reg_email.getEditText().getText().toString();
                String password = reg_pass.getEditText().getText().toString();
                String Cpassword = reg_Cpass.getEditText().getText().toString();


                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(Cpassword))
                {
                    Toast.makeText(RegistrationActivity.this, " please Enter Valid Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else if(!email.matches(emailPattern))
                {
                    reg_email.setError("invalid email");
                    Toast.makeText(RegistrationActivity.this, " please Enter Valid email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else if(!password.equals(Cpassword)  )
                {
                    Toast.makeText(RegistrationActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else  if ( password.length()<6)
                {
                    Toast.makeText(RegistrationActivity.this, "Enter 6 character password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else
                {
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        Log.d("register","create user successfull");
                                        DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
                                        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                                        if (imageuri != null) {
                                            Log.d("register","enter into imageuri if statement");

                                            storageReference.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("register","successfully put file ");

                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                imageURI = uri.toString();
                                                                Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                                                databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            progressDialog.dismiss();

                                                                            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));

                                                                        } else {
                                                                            Toast.makeText(RegistrationActivity.this, "error in Registration", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(RegistrationActivity.this, "error in image", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            imageURI = "https://firebasestorage.googleapis.com/v0/b/chitchat-60182.appspot.com/o/user.png?alt=media&token=060e7651-e8d4-46c7-b5d4-580e3a22ad75";
                                            Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                            databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));

                                                    } else {
                                                        Toast.makeText(RegistrationActivity.this, "error in Registration", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(RegistrationActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                        Log.d("register",""+task.getException());
                                        progressDialog.dismiss();

                                    }
                                }
                            });

                }
            }
        });



        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });


      txt_signin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
          }
      });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      if(requestCode==10&&resultCode==RESULT_OK)
      {
          if(data!=null)
          {
              imageuri=data.getData();
              profile_image.setImageURI(imageuri);
          }
      }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
       startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
    }
}