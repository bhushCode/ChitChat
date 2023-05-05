package com.example.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
TextView txt_signup,signin_btn;
TextInputLayout login_email,login_password;
FirebaseAuth auth;
ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
  progressDialog = new ProgressDialog(this);
  progressDialog.setMessage("please wait");
  progressDialog.setCancelable(false);
        auth=FirebaseAuth.getInstance();

        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        signin_btn=findViewById(R.id.signin_btn);
     txt_signup = findViewById(R.id.txt_signup);

     signin_btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             progressDialog.show();
             String email=login_email.getEditText().getText().toString();
             String password=login_password.getEditText().getText().toString();

             if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password))
             {
                 Toast.makeText(LoginActivity.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();
             }
             else if(!email.matches(emailPattern ))
             {
                 login_email.setError("invalid email");
                 Toast.makeText(LoginActivity.this, "invalid email", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();

             }
             else if(password.length()<6)
             {
                 login_email.setError("invalid password");
                 Toast.makeText(LoginActivity.this, "invalid password", Toast.LENGTH_SHORT).show();
                 progressDialog.dismiss();


             }
             else
             {
                 auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful())
                         {
                             progressDialog.dismiss();

                             startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                         }
                         else
                         {
                             progressDialog.dismiss();

                             Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }

         }
     });
     txt_signup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
         }
     });
    }
}