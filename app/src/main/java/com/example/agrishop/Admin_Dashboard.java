package com.example.agrishop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Admin_Dashboard extends AppCompatActivity {
CardView upload,delete,order,feed,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        upload=findViewById(R.id.uploadfood);
        delete=findViewById(R.id.deletefood);
        order=findViewById(R.id.order);
        feed=findViewById(R.id.feedback);
        logout=findViewById(R.id.adminlogout);


logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Admin_Dashboard.this, Login_Page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
});

feed.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Admin_Dashboard.this,FeedbackActivity.class);
        startActivity(intent);
    }
});
order.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Admin_Dashboard.this,Order.class);
        startActivity(intent);
    }
});
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this,Delete_Product.class);
                startActivity(intent);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this,Upload_Food.class);
                startActivity(intent);
            }
        });
    }

    public void notification(View view) {
        Intent intent = new Intent(Admin_Dashboard.this, NotificationsActivity.class);
        startActivity(intent);
    }
}