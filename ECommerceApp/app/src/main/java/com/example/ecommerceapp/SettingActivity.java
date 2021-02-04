package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

//    String UserName;
    TextView UserSettingUlpoad,UserBirthday;
    CircleImageView UserProfileImage;
    EditText phone,FullName,address,Gender,Job;
    String UserImage,UserImageChecked="";
    private static final int GalleryPick=99;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        Bundle bundle;
//        bundle=getIntent().getBundleExtra("b");
//    //    UserID=bundle.getString("Id");
//        UserName=bundle.getString("Name");

        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
        Gender=findViewById(R.id.SettingGender);
        Job=findViewById(R.id.SettingJob);

        UserSettingUlpoad=findViewById(R.id.SettingUpload);
        UserBirthday=findViewById(R.id.settingBirthday);
        UserProfileImage=findViewById(R.id.SettingImage);
        phone=findViewById(R.id.settingPhoneNumber);
        FullName=findViewById(R.id.SettingFullName);
        address=findViewById(R.id.settingAddress);

        UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserImageChecked="checked";
                OpenGalleryForSelectImage();
            }
        });
        UserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFargment dialogFragment=new DatePickerFargment();
                dialogFragment.show(getSupportFragmentManager(),"Date Picker");
            }
        });

        UserSettingUlpoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserBirthday.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Birthday...", Toast.LENGTH_SHORT).show();
                }
                else if (phone.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Phone...", Toast.LENGTH_SHORT).show();
                }
                else if (FullName.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Name...", Toast.LENGTH_SHORT).show();
                }
                else if (address.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Address...", Toast.LENGTH_SHORT).show();
                }
                else if (Job.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Job...", Toast.LENGTH_SHORT).show();
                }
                else if (Gender.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Please Enter Your Gender...", Toast.LENGTH_SHORT).show();
                }
//               else if (UserImageChecked.equals("not checked"))
//                {
//                    Toast.makeText(SettingActivity.this, "Please Choice Your Profile Image", Toast.LENGTH_SHORT).show();
//                }
                else {
                    if (UserImageChecked.equals("checked")) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Phone", phone.getText().toString());
                        editor.commit();

                        HashMap<String, Object> UserMap = new HashMap<>();
                        UserMap.put("Date", UserBirthday.getText().toString());
                        UserMap.put("Phone", phone.getText().toString());
                        UserMap.put("FullName", FullName.getText().toString());
                        UserMap.put("address", address.getText().toString());
                        UserMap.put("jop", Job.getText().toString());
                        UserMap.put("gender", Gender.getText().toString());
                        UserMap.put("image", UserImage);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingActivity.this, "Uploaded Successfully....", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(SettingActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Phone", phone.getText().toString());
                        editor.commit();

                        HashMap<String, Object> UserMap = new HashMap<>();
                        UserMap.put("Date", UserBirthday.getText().toString());
                        UserMap.put("Phone", phone.getText().toString());
                        UserMap.put("FullName", FullName.getText().toString());
                        UserMap.put("address", address.getText().toString());
                        UserMap.put("jop", Job.getText().toString());
                        UserMap.put("gender", Gender.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingActivity.this, "Uploaded Successfully....", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(SettingActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String CurrentDate= DateFormat.getDateInstance().format(calendar.getTime());
        UserBirthday.setText(CurrentDate);

    }
    @Override
    protected void onStart() {
        super.onStart();
       FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
             if (snapshot.exists()) {
                 if (snapshot.child("Date").exists()) {
                     UserBirthday.setText(snapshot.child("Date").getValue().toString());
                 }
                 if (snapshot.child("Phone").exists()) {
                     phone.setText(snapshot.child("Phone").getValue().toString());
                 }
                 if (snapshot.child("FullName").exists()) {
                     FullName.setText(snapshot.child("FullName").getValue().toString());
                 }
                 if (snapshot.child("address").exists()) {
                     address.setText(snapshot.child("address").getValue().toString());
                 }
                 if (snapshot.child("jop").exists()) {
                     Job.setText(snapshot.child("jop").getValue().toString());
                 }
                 if (snapshot.child("gender").exists()) {
                     Gender.setText(snapshot.child("gender").getValue().toString());
                 }
                 if (snapshot.child("image").exists()) {
                     Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile).into(UserProfileImage);
                 }
             }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               //Toast.makeText(SettingActivity.this, "Error : Setting "+error.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void OpenGalleryForSelectImage()
    {
        Intent intentGallery = new Intent();
        intentGallery.setAction(Intent.ACTION_PICK);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK&&data!=null) {
            Uri image = data.getData();
            FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(SettingActivity.this, "Upload Successfully...", Toast.LENGTH_SHORT).show();
                            UserImage=uri.toString();
                            Picasso.get().load(UserImage).placeholder(R.drawable.profile).into(UserProfileImage);
                        }
                    });
                }
            });

        }
    }
}