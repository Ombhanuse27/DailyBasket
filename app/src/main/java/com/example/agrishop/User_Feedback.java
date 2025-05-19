package com.example.agrishop;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User_Feedback extends AppCompatActivity {

    private EditText nameEditText, emailEditText;
    private Spinner productSpinner;
    private RatingBar ratingBar;
    private Button submitButton;
    private FirebaseFirestore db;
    private List<String> productList = new ArrayList<>();
    private ArrayAdapter<String> productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        nameEditText = findViewById(R.id.ffname);
        emailEditText = findViewById(R.id.fgemail);
        productSpinner = findViewById(R.id.product_spinner);
        ratingBar = findViewById(R.id.rating_bar);
        submitButton = findViewById(R.id.fjsubmit);

        // Set up Spinner adapter
        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productList);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        // Load products from Firestore
        loadProducts();

        // Handle submit button click
        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void loadProducts() {
        CollectionReference productsRef = db.collection("Product");
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear(); // Clear previous data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String productName = document.getString("name"); // Ensure Firestore has "name" field
                    if (productName != null) {
                        productList.add(productName);
                    }
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(User_Feedback.this, "Failed to load products!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitFeedback() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String selectedProduct = productSpinner.getSelectedItem().toString();
        float rating = ratingBar.getRating();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create feedback object
        FeedbackModel feedback = new FeedbackModel(name, email, selectedProduct, rating);

        // Add feedback to Firestore
        db.collection("Feedback").add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(User_Feedback.this, "Feedback submitted!", Toast.LENGTH_SHORT).show();
                    updateProductRating(selectedProduct); // Update the product rating after submission
                })
                .addOnFailureListener(e -> Toast.makeText(User_Feedback.this, "Error submitting feedback!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Fetch all ratings for a product and update the average rating in the "Product" collection.
     */
    private void updateProductRating(String productName) {
        db.collection("Feedback")
                .whereEqualTo("product", productName) // Ensure correct field name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalRating = 0;
                        int count = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Number rating = document.getDouble("rating");
                            if (rating != null) {
                                totalRating += rating.floatValue();
                                count++;
                            }
                        }

                        if (count > 0) {
                            float newAverageRating = totalRating / count;

                            // Update the average rating in the Product collection
                            db.collection("Product")
                                    .whereEqualTo("name", productName) // Find the correct product
                                    .get()
                                    .addOnCompleteListener(productTask -> {
                                        if (productTask.isSuccessful() && !productTask.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot productDoc : productTask.getResult()) {
                                                // Use SetOptions.merge() to ensure field exists
                                                Map<String, Object> ratingUpdate = new HashMap<>();
                                                ratingUpdate.put("averageRating", newAverageRating);

                                                db.collection("Product")
                                                        .document(productDoc.getId())
                                                        .set(ratingUpdate, SetOptions.merge())
                                                        .addOnSuccessListener(aVoid ->
                                                                Toast.makeText(User_Feedback.this, "Product rating updated!", Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(User_Feedback.this, "Failed to update product rating!", Toast.LENGTH_SHORT).show());
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
