package com.example.ecommerceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserViewOrderAdapter extends RecyclerView.Adapter<UserViewOrderAdapter.UserViewHolder> {
    private Context context;
    private ArrayList<adminViewClass> list;

    public UserViewOrderAdapter(Context context, ArrayList<adminViewClass> list) {
        this.context = context;
        this.list = list;
    }

    public void AddItem(adminViewClass cartProductDetails) {
        list.add(cartProductDetails);
    }

    public void Clear() {
        list.clear();
    }


    @NonNull
    @Override
    public UserViewOrderAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_view_order, null, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewOrderAdapter.UserViewHolder holder, int position) {
        holder.CartPrice.setText("Price : " + list.get(position).getPrice() + "$");
        holder.CartQuantity.setText("Quantity : " + list.get(position).getQuan());
        holder.CartName.setText(list.get(position).getNawem());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView CartPrice, CartName, CartQuantity;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            CartName = itemView.findViewById(R.id.AdminViewProductName);
            CartPrice = itemView.findViewById(R.id.AdminViewProductPrice);
            CartQuantity = itemView.findViewById(R.id.AdminViewProductQuantity);
        }
    }
}
