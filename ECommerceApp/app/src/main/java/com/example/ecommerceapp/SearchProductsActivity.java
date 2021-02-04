package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchProductsActivity extends AppCompatActivity {
    private static final int VOICE_CODE =01 ;
    EditText PName;
   Button Search;
   RecyclerView Products;
   DatabaseReference ProductRef;
   AdapterProducts Adapterproducts;
   ArrayList<ProductDetails> product;
   String SearchingKey;
   ImageView SearchVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        SearchingKey=getIntent().getStringExtra("SearchingKey");

        SearchVoice=findViewById(R.id.Voice);
        PName=findViewById(R.id.NameProduct);
        Search=findViewById(R.id.BtnSearch);
        Products=findViewById(R.id.SearchListProduct);
        ProductRef= FirebaseDatabase.getInstance().getReference().child("products");
        product=new ArrayList<>();
        Adapterproducts=new AdapterProducts(product,this);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        Products.setLayoutManager(manager);
        Products.setAdapter(Adapterproducts);


        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PName.getText().toString().isEmpty())
                      SearchAboutItemUsingTextOrVoice(PName.getText().toString());
            }
        });
        SearchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voice=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                if (voice.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(voice,VOICE_CODE);
                }
                else
                {
                    Toast.makeText(SearchProductsActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==VOICE_CODE&&resultCode==RESULT_OK&&data!=null)
        {
            ArrayList<String> VoiceRet=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SearchAboutItemUsingTextOrVoice(VoiceRet.get(0));
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (SearchingKey.equals("text"))
        {
            PName.setVisibility(View.VISIBLE);
            Search.setVisibility(View.VISIBLE);
            SearchVoice.setVisibility(View.INVISIBLE);
        }
        if (SearchingKey.equals("voice"))
        {
            PName.setVisibility(View.INVISIBLE);
            Search.setVisibility(View.INVISIBLE);
            SearchVoice.setVisibility(View.VISIBLE);

        }
    }
    private void SearchAboutItemUsingTextOrVoice(String ProductName)
    {
        ProductRef.orderByChild("PName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String Name="",image="",des="";
                    Double price=0.0;
                    Adapterproducts.Clear();
                    Boolean flag=false;
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        if (dataSnapshot.child("PName").exists() && dataSnapshot.child("PName").getValue().toString().toLowerCase().contains(ProductName.toLowerCase())) {
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
                        Toast.makeText(SearchProductsActivity.this, "This Product Not Found....", Toast.LENGTH_SHORT).show();
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