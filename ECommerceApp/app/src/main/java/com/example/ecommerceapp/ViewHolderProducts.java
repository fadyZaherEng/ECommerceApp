package com.example.ecommerceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolderProducts> {

   private ArrayList<ProductDetails> Products;
   private Context context;

    public AdapterProducts(ArrayList<ProductDetails> products, Context context) {
        this.Products = products;
        this.context = context;
    }
    public void addItem(ProductDetails details)
    {
        Products.add(details);
    }
    public void Clear()
    {
        Products.clear();
    }
    @NonNull
    @Override
    public ViewHolderProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycle_view_holder_product_layout,null,false);
        ViewHolderProducts viewHolderProducts=new ViewHolderProducts(view);
        return viewHolderProducts;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProducts holder, int position) {
        Picasso.get().load(Products.get(position).getImage()).into(holder.ImageProduct);
        holder.Name.setText(Products.get(position).getName());
        holder.Price.setText(String.valueOf(Products.get(position).getPrice()));
        holder.Des.setText(Products.get(position).getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("name",Products.get(position).getName());
                bundle.putString("price",Products.get(position).getPrice().toString());
                bundle.putString("image",Products.get(position).getImage());
                bundle.putString("des",Products.get(position).getDescription());
                bundle.putString("pid",Products.get(position).getPid());
                Intent intent=new Intent(context,ProductDetailsActivity.class);
                intent.putExtra("ProductDetails",bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Products.size();
    }

    class ViewHolderProducts extends RecyclerView.ViewHolder{
        TextView Name,Price,Des;
        ImageView ImageProduct;
        public ViewHolderProducts(@NonNull View itemView) {
            super(itemView);
            Name=itemView.findViewById(R.id.ProductNameHolder);
            Price=itemView.findViewById(R.id.ProductPriceHolder);
            Des=itemView.findViewById(R.id.ProductDesHolder);
            ImageProduct=itemView.findViewById(R.id.ProductImageHolder);
        }
    }
}
