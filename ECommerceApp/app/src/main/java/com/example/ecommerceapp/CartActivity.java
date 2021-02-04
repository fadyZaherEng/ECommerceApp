package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    TextView Total_Price;
    Button NextButton;
    RecyclerView CartRV;
    DatabaseReference reference;
    SharedPreferences sp;
    Double TotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);

        reference=FirebaseDatabase.getInstance().getReference().child("Cart List");
        Total_Price=findViewById(R.id.TotalPrice);
        NextButton=findViewById(R.id.BtnNext);
        CartRV=findViewById(R.id.CartListItem);

        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        CartRV.setLayoutManager(manager);
        CartRV.setHasFixedSize(true);
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (Double.valueOf(Total_Price.getText().toString())>0.0)
               {
                   Intent intent=new Intent(CartActivity.this,ConfirmOrderActivity.class);
                   intent.putExtra("total_price",Total_Price.getText().toString());
                   startActivity(intent);
                   finish();
               }
               else if (Total_Price.getText().toString().equals("Total Price"))
               {
                   Toast.makeText(CartActivity.this, "You don't Buy Anything", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    @Override
    protected void onStart() {
        TotalPrice=0.0;
        super.onStart();
        String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerOptions<CartProductDetails>options=new FirebaseRecyclerOptions.Builder<CartProductDetails>()
                .setQuery(reference.child("User View").child(userid).child("Products"),CartProductDetails.class).build();
        FirebaseRecyclerAdapter<CartProductDetails,CartViewHolder> adapter=new FirebaseRecyclerAdapter<CartProductDetails, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartProductDetails model) {
                holder.CartPrice.setText("Price : "+model.getPPrice()+"$");
                holder.CartQuantity.setText("Quantity : "+model.getQuantity());
                holder.CartName.setText(model.getPName());
                TotalPrice+=(Double.valueOf(model.getQuantity())*Double.valueOf(model.getPPrice()));
                Total_Price.setText(TotalPrice.toString());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence[]option=new CharSequence[]{
                          "Remove",
                          "Edit"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options : ");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    reference.child("User View").child(userid).child("Products").child(getRef(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful())
                                              {
                                                  Toast.makeText(CartActivity.this, "Removed Successfully....", Toast.LENGTH_SHORT).show();
                                              }
                                        }
                                    });
                                }
                                if (which==1)
                                {
                                    Bundle bundle=new Bundle();
                                    FirebaseDatabase.getInstance().getReference().child("products").child(getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {
                                                bundle.putString("pid",getRef(position).getKey());
                                                bundle.putString("name",snapshot.child("PName").getValue().toString());
                                                bundle.putString("des",snapshot.child("PPrice").getValue().toString());
                                                bundle.putString("price",snapshot.child("PDes").getValue().toString());
                                                bundle.putString("image",snapshot.child("PImage").getValue().toString());
                                                Intent  intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                                intent.putExtra("ProductDetails",bundle);
                                                startActivity(intent);
                                                finish();
                                                return;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        });
                    builder.show();
                    }
                });
            }
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout,null,false);
                CartViewHolder  holder=new CartViewHolder(view);
                return holder;
            }
        };
        CartRV.setAdapter(adapter);
        adapter.startListening();
    }

}