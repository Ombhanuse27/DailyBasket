package com.example.agrishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class User_Dashboard extends AppCompatActivity {
CardView food,mycart,myorder,profile,logout,feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        food=findViewById(R.id.food);
        mycart=findViewById(R.id.mycart);
        myorder=findViewById(R.id.myorder);
        profile=findViewById(R.id.profile);
        logout=findViewById(R.id.userlogout);
        feed=findViewById(R.id.ufeedback);

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Dashboard.this,User_Feedback.class);
                startActivity(intent);
            }
        });

logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(User_Dashboard.this, Login_Page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
});
profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(User_Dashboard.this,Profile.class);
        startActivity(intent);
    }
});
myorder.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(User_Dashboard.this,MY_Order.class);
        startActivity(intent);
    }
});
mycart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(User_Dashboard.this,My_Cart.class);
        startActivity(intent);
    }
});
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Dashboard.this,Food.class);
                startActivity(intent);
            }
        });



    }

    public void notification(View view) {
        Intent intent = new Intent(User_Dashboard.this, NotificationsActivity.class);
        startActivity(intent);
    }
}