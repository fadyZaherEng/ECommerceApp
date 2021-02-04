package com.example.ecommerceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterOrdersDetails extends RecyclerView.Adapter<AdapterOrdersDetails.ViewholderOrders> {

    private Context context;
    private ArrayList<OrderDetails>Orders;

    public AdapterOrdersDetails(Context context, ArrayList<OrderDetails> orders) {
        this.context = context;
        Orders = orders;
    }
    public void addItem(OrderDetails orderDetails)
    {
        Orders.add(orderDetails);
        notifyDataSetChanged();
    }
    public void Clear()
    {
        Orders.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewholderOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_layout_show_order,null,false);
        ViewholderOrders viewholderOrders = new ViewholderOrders(view);
        return viewholderOrders;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewholderOrders holder, int position) {
        holder.name.setText("Name: "+Orders.get(position).getName());
        holder.price.setText("Price: "+Orders.get(position).getPrice().toString());
        holder.phone.setText("Phone: "+Orders.get(position).getPhone());
        holder.address.setText("Address: "+Orders.get(position).getAddress());
        holder.date.setText("Date: "+Orders.get(position).getDate());

        holder.btnShowOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProductsOfOrderActivity.class);
                intent.putExtra("id",Orders.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CharSequence[] sequence=new CharSequence[]{
                        "Yes",
                        "No"
                } ;
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Have you shipped this order Products ?");

                builder.setItems(sequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
                            FirebaseDatabase.getInstance().getReference().child("Orders").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int i=0;
                                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                                    {
                                        if (i==position)
                                        {
                                            String key=dataSnapshot.getKey();
                                            FirebaseDatabase.getInstance().getReference().child("Orders").child(key).removeValue();
                                            Toast.makeText(context, "Removed Successfully.....", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        i++;
                                    }
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Orders.size();
    }

    class ViewholderOrders extends RecyclerView.ViewHolder{
      TextView name,phone,address,price,date;
      Button btnShowOrder;
        public ViewholderOrders(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.CusName);
            phone=itemView.findViewById(R.id.CusPhone);
            address=itemView.findViewById(R.id.cusAddress);
            price=itemView.findViewById(R.id.orderPrice);
            date=itemView.findViewById(R.id.orderDate);
            btnShowOrder=itemView.findViewById(R.id.btnShowOrder);
        }
    }
}
