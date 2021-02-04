package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;

public class AdminAddNewProductActivity extends AppCompatActivity {


    ImageView select_product_image;
    EditText productName,productDes,productPrice,productBarcode;

    String SaveCurrentTime, SaveCurrentDate;
    Calendar calender;
    private final static int GalleryPick = 1;
    private final static int CAMERA_PERMISION = 8;
    DatabaseReference RootRef;
    StorageReference storageReference;
    private String CatName="", ProductImage="", ProductRandomKey;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);

        productName=findViewById(R.id.productName);
        productPrice=findViewById(R.id.productPrice);
        productDes=findViewById(R.id.productDes);
        select_product_image=findViewById(R.id.select_product_image);
        productBarcode=findViewById(R.id.productBarcode);
        RootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Products Images");
        CatName = getIntent().getStringExtra("Category");
        calender = Calendar.getInstance();

    }
    public void OpenGalaryToSelectImage(View view)
    {
        if ((int) Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(AdminAddNewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISION);
                return;
            }
        }
        OpenGalleryForSelectImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (CAMERA_PERMISION == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            OpenGalleryForSelectImage();
        }
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

        if (requestCode == GalleryPick && resultCode == RESULT_OK&&data!=null)
        {
            Uri image = data.getData();

            SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
            SaveCurrentDate = date.format(calender.getTime());
            SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
            SaveCurrentTime = time.format(calender.getTime());

            ProductRandomKey=SaveCurrentDate+SaveCurrentTime;

            storageReference.child(ProductRandomKey).putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        storageReference.child(ProductRandomKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                Toast.makeText(AdminAddNewProductActivity.this,"Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                                Picasso.get().load(uri.toString()).into(select_product_image);
                                ProductImage =uri.toString();
                            }
                        });
                    }
                    else
                    {
                        String massage = task.getException().toString();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error: " + massage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void AddProductToDatabase(View view) {
        String Name, Des;
        Double  Price;
        Name = productName.getText().toString();
        Price = Double.valueOf(productPrice.getText().toString());
        Des = productDes.getText().toString();
        if (ProductImage.isEmpty()) {
            Toast.makeText(this, "Product Image Is Mandatory...", Toast.LENGTH_SHORT).show();
        }
       else if (Name.isEmpty()) {
            Toast.makeText(this, "Please Write Product Name...", Toast.LENGTH_SHORT).show();
        }
       else if (Des.isEmpty()) {
            Toast.makeText(this, "Please Write Product Description...", Toast.LENGTH_SHORT).show();
        }
        else if (productPrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Write Product Price...", Toast.LENGTH_SHORT).show();
        }
        else if (productBarcode.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please Write Product Barcode", Toast.LENGTH_SHORT).show();
        }
        else
        {
//            SharedPreferences.Editor editor=sp.edit();
//            editor.putString("pid",ProductRandomKey);
//            editor.commit();

            HashMap<String, Object> ProductMap = new HashMap<>();
            String id= RootRef.child("products").push().getKey();
            ProductMap.put("PName",Name);
            ProductMap.put("PPrice",Price);
            ProductMap.put("PDes",Des);
            ProductMap.put("PImage",ProductImage);
            ProductMap.put("PCat",CatName);
            ProductMap.put("Pid",id);
            ProductMap.put("date",SaveCurrentDate);
            ProductMap.put("time",SaveCurrentTime);
            ProductMap.put("Barcode",productBarcode.getText().toString());
            RootRef.child("products").child(id).updateChildren(ProductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful())
                      {
                          Toast.makeText(AdminAddNewProductActivity.this, "Add Product Successfully....", Toast.LENGTH_SHORT).show();
                          productDes.setText("");productName.setText("");productPrice.setText("");select_product_image.setImageResource(R.drawable.open_camera);
                          productBarcode.setText("");
                      }
                      else
                      {
                          Toast.makeText(AdminAddNewProductActivity.this, "Error add Product"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                      }
                }
            });
        }
    }
}