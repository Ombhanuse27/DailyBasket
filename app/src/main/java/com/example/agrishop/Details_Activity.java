package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Details_Activity extends AppCompatActivity {
    private static final int MAX_QUANTITY = 10;

    TextView quantity, weight;
    int totalQuantity = 0;
    int totalPrice = 0;
    int productPrice = 0;  // Default price per kg
    int selectedWeight = 0;  // Stores the selected weight in grams
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    DatabaseReference reference;
    ImageView imageView;
    TextView name, rate, des, rating,total;

    productmodel productmodel = null;
    Button addCart, add, remove, weight500, weight250;
    Usermodel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        // Retrieve product ID from Intent
        String productId = getIntent().getStringExtra("productId");

        // Initialize UI elements
        imageView = findViewById(R.id.detailimage);
        name = findViewById(R.id.dname);
        rate = findViewById(R.id.drate);
        des = findViewById(R.id.des);
        addCart = findViewById(R.id.addtocart);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        quantity = findViewById(R.id.quantity);
        weight = findViewById(R.id.totalquantity);
        rating = findViewById(R.id.rating);
        weight500 = findViewById(R.id.btn_500gm);  // 500 gm button
        weight250 = findViewById(R.id.btn_250gm);
        total = findViewById(R.id.totalAmount);
        // 250 gm button

        // Fetch product details from Firestore
        firebaseFirestore.collection("Product").document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                productmodel = document.toObject(productmodel.class);
                                if (productmodel != null) {
                                    Glide.with(Details_Activity.this).load(productmodel.getImageUrl()).into(imageView);
                                    name.setText(productmodel.getName());
                                    rate.setText(String.valueOf(productmodel.getRate()));
                                    des.setText(productmodel.getDescription());

                                    rating.setText(String.valueOf(document.getDouble("averageRating") != null ? document.getDouble("averageRating") : 0.0));

                                    if (document.contains("rate")) {
                                        productPrice = document.getLong("rate").intValue(); // Convert to int
                                        rate.setText(String.valueOf(productPrice)); // Display correct price
                                    }

                                    // Set product price
                                    productPrice = productmodel.getRate();
                                    updateTotalPrice();
                                }
                            }
                        }
                    }
                });

        // Add to cart logic
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    addToCart();
                } else {
                    startActivity(new Intent(getApplicationContext(), Login_Page.class));
                    finish();
                }
            }
        });

        // Increase quantity
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity < MAX_QUANTITY) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    updateTotalPrice();
                }
            }
        });

        // Decrease quantity
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity > 0) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    updateTotalPrice();
                }
            }
        });

        // Select 500 gm
        weight500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWeight = 500;
                updateTotalPrice();
            }
        });

        // Select 250 gm
        weight250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWeight = 250;
                updateTotalPrice();
            }
        });
    }

    // Update total price method
    // Update total price method
    private void updateTotalPrice() {
        if (productmodel != null) {
            try {
                // Ensure selectedWeight is at least 0 gm if not set
                int effectiveWeight = selectedWeight > 0 ? selectedWeight : 0;

                int totalGrams = (totalQuantity * 1000) + effectiveWeight; // Convert total quantity to grams
                double totalKg = totalGrams / 1000.0; // Convert grams to kg
                totalPrice = (int) (productPrice * totalKg); // Calculate total price

                weight.setText(formatWeightDisplay(totalQuantity, effectiveWeight));
                total.setText(String.valueOf(totalPrice));
                 // Update UI price display
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // Format weight display for UI
    private String formatWeightDisplay(int quantity, int weight) {
        int totalGrams = (quantity * 1000) + weight;
        int kg = totalGrams / 1000;
        int gm = totalGrams % 1000;
        return kg + " kg " + (gm > 0 ? gm + " gm" : "");
    }

    // Add to Cart Function
    private void addToCart() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM, dd, yyyy", Locale.getDefault());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());

        String saveCurrentDate = currentDate.format(calForDate.getTime());
        String saveCurrentTime = currentTime.format(calForDate.getTime());
        String productName = name.getText().toString();

        // Generate unique ID
        String uniqueId = productName + "_" + System.currentTimeMillis();

        reference.child("Usersregister").child(auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            user = task.getResult().getValue(Usermodel.class);

                            HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("Name", productName);
                            cartMap.put("Price", totalPrice);
                            cartMap.put("Date", saveCurrentDate);
                            cartMap.put("Time", saveCurrentTime);
                            cartMap.put("Quantity", totalQuantity);
                            cartMap.put("Weight", formatWeightDisplay(totalQuantity, selectedWeight));
                            cartMap.put("username", user != null ? user.getName() : "Unknown");
                            cartMap.put("mobilenumber", user != null ? user.getPhone() : "N/A");
                            cartMap.put("address", user != null ? user.getUseraddress() : "N/A");
                            cartMap.put("imageUrl", productmodel != null ? productmodel.getImageUrl() : "https://example.com/default-image.png");

                            firebaseFirestore.collection("AddToCart")
                                    .document(auth.getUid())
                                    .collection("CurrentUser")
                                    .document(uniqueId)
                                    .set(cartMap)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(Details_Activity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}
