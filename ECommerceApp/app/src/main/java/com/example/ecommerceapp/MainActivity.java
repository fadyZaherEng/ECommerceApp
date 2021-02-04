package com.example.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    Button BtnRegister;
    Button BtnLogIn;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnRegister=findViewById(R.id.BtnRegister);
        BtnLogIn=findViewById(R.id.BtnLogIn);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        BtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });
        sp=getSharedPreferences("UserCheckedRemember",MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String State=sp.getString("UserState","null");
        String name=sp.getString("Name","null");
     //   String id=sp.getString("Id","null");
        if (FirebaseAuth.getInstance().getCurrentUser() != null&&State.equals("Checked")) {
                Intent intent = new Intent(this, home_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Name", name);
       //         bundle.putString("Id", id);
                intent.putExtra("b", bundle);
                startActivity(intent);
                finish();
                return;
            }
    }
}