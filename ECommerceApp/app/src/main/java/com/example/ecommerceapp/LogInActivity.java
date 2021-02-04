package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class LogInActivity extends AppCompatActivity {

    EditText EmailText;
    EditText PasswordText;
    Button LogInEmail;
    TextView GoToRegister;
    ImageView showOrHidePasswordIn;
    TextView GoToResetAccount;
    CheckBox CBRemember;
    TextView tv_admin;
    TextView tv_NotAdmin;

    ProgressDialog progressDialog;
    FirebaseAuth auth;
    SharedPreferences sp;
    String DBName;
    DatabaseReference RootRef;
    String userNameIntent="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initializeFields();
        GotoResetPassword();
        showAndHidePasswordMethod();

        GoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        LogInEmail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Password=PasswordText.getText().toString();
            String Email=EmailText.getText().toString();
            if (TextUtils.isEmpty(Email)) {
                Toast.makeText(LogInActivity.this,"Please Enter Your Email", Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(Password)) {
                Toast.makeText(LogInActivity.this,"Please Enter Your Password", Toast.LENGTH_LONG).show();
            }
            else
            {
                progressDialog.setTitle("Login Account");
                progressDialog.setMessage("wait to Login account...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (DBName.equals("Users")) {
                    auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                if (CBRemember.isChecked())
                                {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("UserState", "Checked");
                                    editor.commit();
                                }
                                progressDialog.dismiss();
                                verifyEmailAddress(Email);
                            }
                            else
                            {
                                Toast.makeText(LogInActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                if (DBName.equals("Admins"))
                {
                    CheckInternetConnection cic = new CheckInternetConnection(getApplicationContext());
                    Boolean ch = cic.isConnectingToInternet();
                    if (!ch) {
                        progressDialog.dismiss();
                        Toast.makeText(LogInActivity.this, "No Internet", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    FirebaseDatabase.getInstance().getReference().child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {
                                                boolean flag=false;
                                                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                                                {
                                                    if (dataSnapshot.child("Email").getValue().toString().equals(Email)&&dataSnapshot.child("Password").getValue().toString().equals(Password))
                                                    {
                                                        Toast.makeText(LogInActivity.this, "Logged Successfully", Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                        Intent intent=new Intent(LogInActivity.this,AdminCategoryActivity.class);
                                                        startActivity(intent);
                                                        flag=true;
                                                        break;
                                                    }
                                                }
                                                if (!flag){
                                                    Toast.makeText(LogInActivity.this, "Logged Invalid", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LogInActivity.this, "LogIn Failed Admin+ "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(LogInActivity.this, "Logged Invalid", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        }
      });
    }
    private void showAndHidePasswordMethod()
    {
        showOrHidePasswordIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageForgotPasswordMethod();
            }
        });
    }

    private void imageForgotPasswordMethod()
    {
        if (PasswordText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
        {
            showOrHidePasswordIn.setImageResource(R.drawable.ic_invisible);
            //Show Password
            PasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            showOrHidePasswordIn.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            //Hide Password
            PasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    private void initializeFields(){
        EmailText=findViewById(R.id.signInEmail);
        PasswordText=findViewById(R.id.signInPassword);
        LogInEmail=findViewById(R.id.signIn);
        GoToRegister=findViewById(R.id.goToreg);
        showOrHidePasswordIn=findViewById(R.id.ShowHidePassLogIN);
        GoToResetAccount=findViewById(R.id.tv_ForgetPass);
        CBRemember=findViewById(R.id.CBRemember);
        tv_admin=findViewById(R.id.tv_admin);
        tv_NotAdmin=findViewById(R.id.tv_NotAdmin);
        RootRef= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
        DBName="Users";
        tv_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInEmail.setText("Login Admin");
                DBName="Admins";
                CBRemember.setVisibility(View.INVISIBLE);
                tv_admin.setVisibility(View.INVISIBLE);
                tv_NotAdmin.setVisibility(View.VISIBLE);
            }
        });

        tv_NotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInEmail.setText("Login");
                DBName="Users";
                CBRemember.setVisibility(View.VISIBLE);
                tv_admin.setVisibility(View.VISIBLE);
                tv_NotAdmin.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void verifyEmailAddress(String email)
    {
        if (auth.getCurrentUser().isEmailVerified())
        {


            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("UserEmail").getValue().toString().equals(email) )
                            {
                                Toast.makeText(LogInActivity.this,"Logged Success", Toast.LENGTH_LONG).show();
                                userNameIntent= dataSnapshot.child("CustomerName").getValue().toString();

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("Name", userNameIntent);
                               // editor.putString("Id", dataSnapshot.getKey());
                                editor.commit();

                                Intent homeIntent = new Intent(LogInActivity.this, home_Activity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("Name",userNameIntent);
                                //bundle.putString("Id",dataSnapshot.getKey());
                                homeIntent.putExtra("b",bundle);
                                startActivity(homeIntent);
                                finish();
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this,"Please Verify Your Account", Toast.LENGTH_LONG).show();
        }
    }

    private void GotoResetPassword()
    {
        GoToResetAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(LogInActivity.this);
                View view= LayoutInflater.from(LogInActivity.this).inflate(R.layout.reset_password,null,false);
                builder.setView(view);
                final AlertDialog alertDialog=builder.show();
                view.findViewById(R.id.BtnYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ResetPasswordUsingEmail(((EditText)view.findViewById(R.id.etEmail)).getText().toString());
                        alertDialog.dismiss();
                    }
                });
                view.findViewById(R.id.BtnNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }
    private void ResetPasswordUsingEmail(String Email)
    {
        if (Email.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Enter Your Email", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"Send Reset Email Successfully...", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Send Reset Email failure...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}