package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ConfirmOrderActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Double TotalPrice;
    Button  Final_order_confirm_btn;
    String address="";
    DatabaseReference RootRef;
    SharedPreferences sp;
    String name;
    String EmailCurrentUser="";
    String SenderMail,SenderPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        SenderMail="fedo.zaher@gmail.com";
        SenderPassword="01205221661";

        TotalPrice=Double.valueOf(getIntent().getStringExtra("total_price"));
        Final_order_confirm_btn=findViewById(R.id.Final_order_confirm_btn);
        RootRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
     //   userId=sp.getString("Id",null);
        name=sp.getString("Name",null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Final_order_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String SavedCurrentTime, SavedCurrentDate;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat Time = new SimpleDateFormat("MMM dd, yyyy");
                SavedCurrentTime = Time.format(calendar.getTime());

                SimpleDateFormat Date = new SimpleDateFormat("HH:mm:ss a");
                SavedCurrentDate = Date.format(calendar.getTime());

                HashMap<String, Object> OrderMap = new HashMap<>();
                OrderMap.put("totalPrice", TotalPrice);
                OrderMap.put("address", address);
                OrderMap.put("CustID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                OrderMap.put("time", SavedCurrentTime);
                OrderMap.put("date", SavedCurrentDate);
             //   OrderMap.put("state", "not shipped");

                RootRef.push().updateChildren(OrderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //intial proprites
                                        Properties props = new Properties();
                                        props.put("mail.smtp.auth", "true");
                                        props.put("mail.smtp.starttls.enable", "true");
                                        props.put("mail.smtp.host", "smtp.gmail.com");
                                        props.put("mail.smtp.port", "587");

                                        // Get the Session object.
                                        Session session = Session.getInstance(props,
                                                new javax.mail.Authenticator() {
                                                    protected PasswordAuthentication getPasswordAuthentication() {
                                                        return new PasswordAuthentication(SenderMail, SenderPassword);
                                                    }
                                                });
                                        //initial email content
                                        try {
                                            Message message = new MimeMessage(session);
                                            message.setFrom(new InternetAddress(SenderMail));
                                            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(EmailCurrentUser));
                                            message.setSubject("Confirmation Order");
                                            message.setText("Your Final Order has been Placed Successfully..");
                                            new sendEmailAsync().execute(message);
                                            Handler handler=new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            },6000);

                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng Muscat = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(Muscat).title(getAddress(Muscat.latitude,Muscat.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))).showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Muscat,8F));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng.latitude,latLng.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))).showInfoWindow();
                address=getAddress(latLng.latitude,latLng.longitude);
            }
        });
    }
    private String getAddress(Double latitude,Double longitude)  {
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());
        List<Address> addresses= null;
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  addresses.get(0).getAddressLine(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.child("UserEmail").exists())
                    {
                        EmailCurrentUser=snapshot.child("UserEmail").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class sendEmailAsync extends AsyncTask<Message,String,String>
    {
        private ProgressDialog statusDialog;

        protected void onPreExecute() {
            statusDialog = new ProgressDialog(ConfirmOrderActivity.this);
            statusDialog.setMessage("Confirmation Order...");
            statusDialog.setIndeterminate(false);
            statusDialog.setCancelable(false);
            statusDialog.show();
        }
        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            statusDialog.dismiss();
            if (s.equals("Success"))
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(ConfirmOrderActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'> Success</font>"));
                builder.setMessage("Confirmation Order and Send Mail Confirmation Successfully");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusDialog.dismiss();
                    }
                });
                builder.show();
            }
            else
                {
                Toast.makeText(ConfirmOrderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


}