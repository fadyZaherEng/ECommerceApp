package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserViewCategoryActivity extends AppCompatActivity {
ListView UserViewCatLV;
ArrayAdapter<String>adapter;
Set<String>UniqueCat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_category);

        UserViewCatLV=findViewById(R.id.UserViewCatLV);
        UniqueCat=new HashSet<>();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        UserViewCatLV.setAdapter(adapter);
        UserViewCatLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  String CatName=((TextView)view).getText().toString();
                  Intent intent=new Intent(UserViewCategoryActivity.this,UserProductOfSpecificCategoryActivity.class);
                  intent.putExtra("CatName",CatName);
                  startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    UniqueCat.clear();
                    adapter.clear();
                    for (DataSnapshot  dataSnapshot:snapshot.getChildren())
                    {
                        if (dataSnapshot.child("PCat").exists())
                        {
                            UniqueCat.add(dataSnapshot.child("PCat").getValue().toString());
                        }
                    }
                    for (String s :UniqueCat)
                    {
                        adapter.add(s);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}