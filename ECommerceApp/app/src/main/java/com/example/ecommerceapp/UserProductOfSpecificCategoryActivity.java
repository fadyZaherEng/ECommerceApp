package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProductOfSpecificCategoryActivity extends AppCompatActivity {
    RecyclerView UserViewCatRV;

    DatabaseReference ProductRef;
    AdapterProducts Adapterproducts;
    ArrayList<ProductDetails> Products;
    ProductDetails productDetails;
    String CatName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_of_specific_category);
        UserViewCatRV=findViewById(R.id.UserViewCatRV);

        Products=new ArrayList<>();
        Adapterproducts=new AdapterProducts(Products,this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        UserViewCatRV.setLayoutManager(manager);
        UserViewCatRV.setAdapter(Adapterproducts);
        ProductRef= FirebaseDatabase.getInstance().getReference();
        CatName=getIntent().getStringExtra("CatName");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProductRef.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {       String Name="",image="",des="";
                    Double price=0.0;
                    Adapterproducts.Clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        if (CatName.equals(dataSnapshot.child("PCat").getValue().toString())) {
                            if (!dataSnapshot.child("PName").getValue().toString().isEmpty()) {
                                Name = dataSnapshot.child("PName").getValue().toString();
                            }
                            if (!dataSnapshot.child("PImage").getValue().toString().isEmpty()) {
                                image = dataSnapshot.child("PImage").getValue().toString();
                            }
                            if (!dataSnapshot.child("PDes").getValue().toString().isEmpty()) {
                                des = dataSnapshot.child("PDes").getValue().toString();
                            }
                            if (!dataSnapshot.child("PPrice").getValue().toString().isEmpty()) {
                                price = Double.valueOf(dataSnapshot.child("PPrice").getValue().toString());
                            }
                            productDetails = new ProductDetails(Name, des, image, price, dataSnapshot.child("Pid").getValue().toString());
                            Adapterproducts.addItem(productDetails);
                        }
                    }
                    Adapterproducts.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  Toast.makeText(home_Activity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}