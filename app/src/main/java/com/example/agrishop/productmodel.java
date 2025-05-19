package com.example.agrishop;

public class productmodel {
    private String productId;
    private String name;
    private int rate;  // Using "rate" instead of "price"
    private String imageUrl;
    private String description;


    // Default constructor required for Firestore
    public productmodel() {}

    // Constructor with productId
    public productmodel(String productId, String name, int rate, String imageUrl, String description) {
        this.productId = productId;  // ✅ Assign productId
        this.name = name;
        this.rate = rate;
        this.imageUrl = imageUrl;
        this.description = description;

    }

    // Getter for productId
    public String getProductId() {
        return productId;
    }

    // Getter for rating


    // Setter for productId (in case you need it)
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public int getRate() {
        return rate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
