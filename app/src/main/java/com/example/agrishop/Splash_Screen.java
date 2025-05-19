package com.example.agrishop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Screen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = fAuth.getCurrentUser();
                if (currentUser != null) {
                    checkUserType(currentUser.getUid());
                } else {
                    redirectToLogin();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkUserType(String uid) {
        DatabaseReference userRef = database.getReference().child("Usersregister").child(uid).child("usertype");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int userType = snapshot.getValue(Integer.class);
                    if (userType == 0) {
                        startActivity(new Intent(Splash_Screen.this, User_Dashboard.class));
                    } else if (userType == 1) {
                        startActivity(new Intent(Splash_Screen.this, Admin_Dashboard.class));
                    }
                } else {
                    redirectToLogin();
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(Splash_Screen.this, Login_Page.class));
        finish();
    }
}
