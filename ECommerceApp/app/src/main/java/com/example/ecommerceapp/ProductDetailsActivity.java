package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
TextView ProductName,ProductDes,ProductPrice;
ImageView ProductImage;
ElegantNumberButton numberButton;
FloatingActionButton AddCart;
String pid,userId;
//SharedPreferences sp;
DatabaseReference RootRefCartList;
DatabaseReference HistoryOrderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        RootRefCartList= FirebaseDatabase.getInstance().getReference().child("Cart List");
        HistoryOrderRef=FirebaseDatabase.getInstance().getReference().child("History Order");
//        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
//
//        pid=sp.getString("pid","null");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

       initializeFields();
       AddCart.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AddProductToFirebaseListCart();
           }
       });

    }

    private void AddProductToFirebaseListCart() {
        String SavedCurrentTime,SavedCurrentDate;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat  Time=new SimpleDateFormat("MMM dd, yyyy");
        SavedCurrentTime=Time.format(calendar.getTime());

        SimpleDateFormat  Date=new SimpleDateFormat("HH:mm:ss a");
        SavedCurrentDate=Date.format(calendar.getTime());

        HashMap<String,Object>Pmap=new HashMap<>();
        //Pmap.put("Pid",pid);
        Pmap.put("time",SavedCurrentTime);
        Pmap.put("date",SavedCurrentDate);
        Pmap.put("PName",ProductName.getText().toString());
        Pmap.put("PPrice",ProductPrice.getText().toString());
        Pmap.put("quantity",numberButton.getNumber());
        Pmap.put("discount","");

        RootRefCartList.child("User View").child(userId).child("Products").child(pid).updateChildren(Pmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful())
                  {
                      RootRefCartList.child("Admin View").child(userId).child("Products").child(pid).updateChildren(Pmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful())
                               {
                                   HistoryOrderRef.push().updateChildren(Pmap);
                                   Toast.makeText(ProductDetailsActivity.this, "Add Product To Cart Successfully...", Toast.LENGTH_SHORT).show();
                                   finish();
                                //   Intent intent=new Intent(ProductDetailsActivity.this,home_Activity.class);
                                  // startActivity(intent);
                               }
                          }
                      });
                  }
            }
        });
    }

    private void initializeFields() {
        ProductName=findViewById(R.id.ProductNameDetails);
        ProductDes=findViewById(R.id.ProductDesDetails);
        ProductPrice=findViewById(R.id.ProductPriceDetails);
        ProductImage=findViewById(R.id.ProductImageDetails);
        numberButton=findViewById(R.id.numberBtn);
        AddCart=findViewById(R.id.fabDetails);
        Bundle bundle=getIntent().getBundleExtra("ProductDetails");
        ProductName.setText(bundle.getString("name"));
        ProductDes.setText(bundle.getString("des"));
        ProductPrice.setText(bundle.getString("price"));
        Picasso.get().load(bundle.getString("image")).into(ProductImage);
        pid=bundle.getString("pid");
    }
}