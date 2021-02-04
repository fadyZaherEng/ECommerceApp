package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class home_Activity extends AppCompatActivity {

    FirebaseAuth auth;
    SharedPreferences sp;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    View view;
    CircleImageView userProfileImage;
    TextView userProfileName;
    FloatingActionButton floatingActionButton;

    String UserNameIntent;
   // String UserIDIntent;

    DatabaseReference ProductRef;
    AdapterProducts Adapterproducts;
    RecyclerView recyclerView;
    ArrayList<ProductDetails>Products;

    ProductDetails productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        floatingActionButton=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.RVProducts);
        Products=new ArrayList<>();
        Adapterproducts=new AdapterProducts(Products,this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(Adapterproducts);
        ProductRef= FirebaseDatabase.getInstance().getReference();

        Bundle  bundle=getIntent().getBundleExtra("b");
        UserNameIntent=bundle.getString("Name");
     // UserIDIntent=bundle.getString("Id");// current user id used in cart list,users


        auth=FirebaseAuth.getInstance();
        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);

        view= LayoutInflater.from(this).inflate(R.layout.nav_header,null,false);

        toolbar = findViewById(R.id.homeToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
       // getSupportActionBar().setIcon(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        drawerLayout=findViewById(R.id.drwalLayout);
        navigationView=findViewById(R.id.nav_view);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        userProfileImage=navigationView.getHeaderView(0).findViewById(R.id.user_profile_image);
        userProfileName=navigationView.getHeaderView(0).findViewById(R.id.user_name_id);

        userProfileName.setText(UserNameIntent);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(home_Activity.this,CartActivity.class);
                startActivity(intent);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_cart:
                        Intent intentCart=new Intent(home_Activity.this,CartActivity.class);
                        startActivity(intentCart);
                        break;
                    case R.id.nav_Categories:
                        Intent intentCat=new Intent(home_Activity.this,UserViewCategoryActivity.class);
                        startActivity(intentCat);
                        break;
                    case R.id.nav_Search:
                        View view=LayoutInflater.from(home_Activity.this).inflate(R.layout.custom_view__different_search_icons,null,false);
                        AlertDialog.Builder builder=new AlertDialog.Builder(home_Activity.this);
                        builder.setView(view);
                        builder.show();
                        view.findViewById(R.id.SearchUsingText).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentSearch=new Intent(home_Activity.this,SearchProductsActivity.class);
                                intentSearch.putExtra("SearchingKey","text");
                                startActivity(intentSearch);
                            }
                        });
                        view.findViewById(R.id.SearchUsingVoice).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentSearch=new Intent(home_Activity.this,SearchProductsActivity.class);
                                intentSearch.putExtra("SearchingKey","voice");
                                startActivity(intentSearch);
                            }
                        });
                        view.findViewById(R.id.SearchUsingBarcode).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentSearch=new Intent(home_Activity.this,SearchByBarcodeActivity.class);
                                startActivity(intentSearch);
                            }
                        });
                        break;
                    case R.id.nav_Settings:
                        Intent intent=new Intent(home_Activity.this,SettingActivity.class);
//                        Bundle bundle=new Bundle();
//                        bundle.putString("Name",UserNameIntent);
//                   //   bundle.putString("Id",UserIDIntent);
//                        intent.putExtra("b",bundle);
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:
                        auth.signOut();
                        SendUserToMainActivity();
                        SharedPreferences.Editor editor=sp.edit();
                        editor.remove("UserState");
                        editor.commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.child("image").exists())
                    {
                        Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile).into(userProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       ProductRef.child("products").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
             if (snapshot.exists())
             {       String Name="",image=null,des="";
                     Double price=0.0;
                     Adapterproducts.Clear();
                 for (DataSnapshot dataSnapshot:snapshot.getChildren())
                 {

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
                     productDetails=new ProductDetails(Name,des,image,price,dataSnapshot.child("Pid").getValue().toString());
                     Adapterproducts.addItem(productDetails);
                     Adapterproducts.notifyDataSetChanged();
                 }
                 Adapterproducts.notifyDataSetChanged();
             }
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) {
          }
      });

    }

    public void SendUserToMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

}