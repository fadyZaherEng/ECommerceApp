package com.example.ecommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HistoryCategoriesPerMonthActivity extends AppCompatActivity {
    HashMap<String,Integer> QuantityProductPerMonth;
    DatabaseReference reference ;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries=new ArrayList<>();
    ArrayList<String>labelNames=new ArrayList<>();
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_categories_per_month);
        reference=FirebaseDatabase.getInstance().getReference();
        QuantityProductPerMonth=new HashMap<>();
        barChart=findViewById(R.id.BarChart);
    }
    private void FillQuantityProductPerMonth()
    {
        reference.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists())
               {
                //   QuantityProductPerMonth.clear();
                   for (DataSnapshot dataSnapshot:snapshot.getChildren())
                   {
                       QuantityProductPerMonth.put(dataSnapshot.child("PName").getValue().toString(),0);
                   //    Log.v(dataSnapshot.child("PName").getValue().toString(), String.valueOf(QuantityProductPerMonth.get(dataSnapshot.child("PName").getValue().toString())));
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FillQuantityProductPerMonth();
        reference.child("History Order").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (QuantityProductPerMonth.containsKey(dataSnapshot.child("PName").getValue().toString())) {
                            int v = QuantityProductPerMonth.get(dataSnapshot.child("PName").getValue().toString())
                                    + Integer.parseInt(dataSnapshot.child("quantity").getValue().toString());
                            QuantityProductPerMonth.replace(dataSnapshot.child("PName").getValue().toString(), v);
                            Log.v(dataSnapshot.child("PName").getValue().toString(),
                                    String.valueOf(QuantityProductPerMonth.get(dataSnapshot.child("PName").getValue().toString())));
                        }
                    }
                    DrawBarChartMethod(QuantityProductPerMonth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DrawBarChartMethod(HashMap<String, Integer> quantityProductPerMonth)
    {
        int i=0;
        for (Map.Entry<String, Integer> entry : quantityProductPerMonth.entrySet()) {
            barEntries.add(new BarEntry(i, entry.getValue()));
            labelNames.add(entry.getKey());
            i++;
        }

        barDataSet = new BarDataSet(barEntries,"Monthly Products");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Description description=new Description();
        description.setText("Months");
        barChart.setDescription(description);
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
     //   xAxis.setLabelRotationAngle(180);
        barChart.animateY(1200);
        barChart.invalidate();
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
    }
}
