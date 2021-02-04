package com.example.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;

public class AdminCategoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

    }

    public void addSport(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Sport");
        startActivity(intent);
    }

    public void addFemaleDresses(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Female_Adresses");
        startActivity(intent);
    }

    public void addSweather(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Sweather");
        startActivity(intent);
    }

    public void addTShirts(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","tShirt");
        startActivity(intent);
    }

    public void addBag(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Bag");
        startActivity(intent);
    }

    public void addGlasses(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Glasses");
        startActivity(intent);
    }

    public void addShoes(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Shoes");
        startActivity(intent);
    }

    public void addHat(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Hat");
        startActivity(intent);
    }

    public void addHeadPhones(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","HeadPhones");
        startActivity(intent);
    }

    public void addMobile(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Mobile");
        startActivity(intent);
    }

    public void addWatches(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Watches");
        startActivity(intent);
    }

    public void addLaptop(View view)
    {
        Intent  intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
        intent.putExtra("Category","Laptop");
        startActivity(intent);
    }

    public void ShowNewOrder(View view)
    {
       Intent intent=new Intent(this,AdminShowOrdersActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
    }

    public void AdminLogOut(View view)
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}