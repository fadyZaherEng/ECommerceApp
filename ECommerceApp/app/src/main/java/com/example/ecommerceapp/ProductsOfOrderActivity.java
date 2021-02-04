package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductsOfOrderActivity extends AppCompatActivity {

    RecyclerView Product;
    UserViewOrderAdapter adapter;
    ArrayList<adminViewClass>list;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_of_order);
        Product=findViewById(R.id.ProductListItem);
        list=new ArrayList<>();
        adapter=new UserViewOrderAdapter(this,list);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        Product.setLayoutManager(manager);
        Product.setHasFixedSize(true);
        Product.setAdapter(adapter);
        id=getIntent().getStringExtra("id");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(id).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists())
               {
                   String name = "",price="",quantity="";
                   adapter.Clear();
                   for (DataSnapshot  dataSnapshot:snapshot.getChildren())
                   {
                       if (dataSnapshot.child("PName").exists())
                       {
                           name=dataSnapshot.child("PName").getValue().toString();
                       }
                       if (dataSnapshot.child("PPrice").exists())
                       {
                           price=dataSnapshot.child("PPrice").getValue().toString();
                       }
                       if (dataSnapshot.child("quantity").exists())
                       {
                           quantity=dataSnapshot.child("quantity").getValue().toString();
                       }
                       adapter.AddItem(new adminViewClass(name,quantity,Double.valueOf(price)));
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