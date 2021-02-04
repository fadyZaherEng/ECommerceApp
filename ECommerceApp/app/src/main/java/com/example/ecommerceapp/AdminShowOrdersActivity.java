package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminShowOrdersActivity extends AppCompatActivity {
    ArrayList<OrderDetails> orders;
    RecyclerView recyclerView;
    AdapterOrdersDetails adapter;
    String name="" , phone="",address, date ;
    Double price=0.0;
    SharedPreferences sp;
    CircleImageView History;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_orders);

        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
        phone=sp.getString("Phone",null);
        name=sp.getString("Name",null);
        History=findViewById(R.id.IVHistory);
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminShowOrdersActivity.this,HistoryCategoriesPerMonthActivity.class));
            }
        });
        recyclerView=findViewById(R.id.NewOrder);
        orders=new ArrayList<>();
        adapter=new AdapterOrdersDetails(this,orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists())
              {
                  adapter.Clear();
                  for (DataSnapshot dataSnapshot:snapshot.getChildren())
                  {
                      if (dataSnapshot.child("address").exists())
                      {
                          address=dataSnapshot.child("address").getValue().toString();
                      }
                      if (dataSnapshot.child("date").exists())
                      {
                          date=dataSnapshot.child("date").getValue().toString();
                      }
                      if (dataSnapshot.child("time").exists())
                      {
                          date=date+"   ,"+dataSnapshot.child("time").getValue().toString();
                      }
                      if (dataSnapshot.child("totalPrice").exists())
                      {
                          price=Double.valueOf(dataSnapshot.child("totalPrice").getValue().toString());
                      }
                      adapter.addItem( new OrderDetails(name,phone,address,date,dataSnapshot.child("CustID").getValue().toString(),price));
                  }
                  adapter.notifyDataSetChanged();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}