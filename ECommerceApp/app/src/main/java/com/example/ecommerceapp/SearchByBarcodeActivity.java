package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class SearchByBarcodeActivity extends AppCompatActivity {
Button ScanningBarcode;
    RecyclerView Products;
    DatabaseReference ProductRef;
    AdapterProducts Adapterproducts;
    ArrayList<ProductDetails> product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_barcode);
        ScanningBarcode=findViewById(R.id.ScanningBarcode);
        Products=findViewById(R.id.RVSearchByBarcode);

        ProductRef= FirebaseDatabase.getInstance().getReference().child("products");
        product=new ArrayList<>();
        Adapterproducts=new AdapterProducts(product,this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        Products.setLayoutManager(manager);
        Products.setAdapter(Adapterproducts);

        ScanningBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              scanCode();
            }
        });
    }
    private void scanCode()
    {
        IntentIntegrator integrator=new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult res=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (res!=null) {
            if (res.getContents() != null) {
           //     Toast.makeText(this, ""+res.getContents(), Toast.LENGTH_SHORT).show();
               SearchAboutItemUsingBarcode(res.getContents());
            }
            else
            {
                Toast.makeText(this, "No res", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void SearchAboutItemUsingBarcode(String ProductBarcode)
    {
        ProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String Name="",image="",des="";
                    Double price=0.0;
                    Adapterproducts.Clear();
                    Boolean flag=false;
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        if (dataSnapshot.child("Barcode").exists() &&dataSnapshot.child("Barcode").getValue().toString().equals(ProductBarcode)) {
                            flag=true;
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
                            Adapterproducts.addItem(new ProductDetails(Name, des, image, price,dataSnapshot.child("Pid").getValue().toString()));
                        }
                    }
                    if (!flag)
                    {
                        Toast.makeText(SearchByBarcodeActivity.this, "This Product Not Found....", Toast.LENGTH_SHORT).show();
                    }
                    Adapterproducts.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}