package com.example.ecommerceapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder {
    TextView CartPrice,CartName,CartQuantity;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        CartName=itemView.findViewById(R.id.CartProductName);
        CartPrice=itemView.findViewById(R.id.CartProductPrice);
        CartQuantity=itemView.findViewById(R.id.CartProductQuantity);
    }
}
