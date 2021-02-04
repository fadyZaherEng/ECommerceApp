package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    EditText et_name;
    EditText TextEmail;
    EditText TextPassword;
    Button createAccountButton;
    ImageView showOrHidePasswordUp;

    FirebaseAuth auth;
    ProgressDialog progressDialog;
    public String CustomerName;
    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initailizeFields();
        showAndHidePasswordMethod();
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerName=et_name.getText().toString();
                String Password=TextPassword.getText().toString();
                String Email=TextEmail.getText().toString();
                if (TextUtils.isEmpty(CustomerName)) {
                    Toast.makeText(RegisterActivity.this,"Please Enter Your Name", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(RegisterActivity.this,"Please Enter Your Email", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(RegisterActivity.this,"Please Enter Your Password", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.setTitle("Create Account");
                    progressDialog.setMessage("wait create account...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                HashMap<String,Object> UserInfoMap=new HashMap<>();
                                UserInfoMap.put("CustomerName",CustomerName);
                                UserInfoMap.put("UserEmail",Email);
                                UserInfoMap.put("UserPassword",Password);
                                RootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(UserInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         sendEmailVerification();
                                         progressDialog.dismiss();
                                         Toast.makeText(RegisterActivity.this,"Created Account Successfully...", Toast.LENGTH_LONG).show();
                                     }
                                    }
                                });
                            }
                            else
                            {
                               // Toast.makeText(RegisterActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
    private void initailizeFields(){
        et_name=findViewById(R.id.et_name);
        TextEmail=findViewById(R.id.signUpEmail);
        TextPassword=findViewById(R.id.signUpPassword);
        createAccountButton=findViewById(R.id.signUp);
        showOrHidePasswordUp=findViewById(R.id.ShowHidePassReg);
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        RootRef=FirebaseDatabase.getInstance().getReference().child("Users");
    }
    private void sendEmailVerification()
    {

        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    sendUserToLogInActivity();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void sendUserToLogInActivity()
    {

        Intent mainIntent = new Intent(this, LogInActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void showAndHidePasswordMethod()
    {
        showOrHidePasswordUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageForgotPasswordMethod();
            }
        });
    }

    private void imageForgotPasswordMethod()
    {
        if (TextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
        {
            showOrHidePasswordUp.setImageResource(R.drawable.ic_invisible);
            //Show Password
            TextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            showOrHidePasswordUp.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            //Hide Password
            TextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}