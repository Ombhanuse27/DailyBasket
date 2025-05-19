package com.example.agrishop;

public class FeedbackModel {
    private String name;
    private String email;
    private String product;
    private float rating;

    // Required empty constructor for Firestore
    public FeedbackModel() {}

    public FeedbackModel(String name, String email, String product, float rating) {
        this.name = name;
        this.email = email;
        this.product = product;
        this.rating = rating;
    }

    // Getters required for Firestore serialization
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProduct() { return product; }
    public float getRating() { return rating; }
}
