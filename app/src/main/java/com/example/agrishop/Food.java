package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Food extends AppCompatActivity {
    private FirebaseFirestore database;
    private RecyclerView productView;
    private List<productmodel> productmodelList;
    private List<productmodel> originalList;

    private Productadapter adapter;
    private AutoCompleteTextView searchView;
    private ImageView searchIcon;
    private List<String> productNames; // For AutoComplete Suggestions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        // Initialize Firestore
        database = FirebaseFirestore.getInstance();

        // Initialize UI Components
        productView = findViewById(R.id.foodrec);
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);

        // Setup RecyclerView
        productView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productView.setLayoutManager(layoutManager);

        // Initialize Product List & Adapter
        productmodelList = new ArrayList<>();
        adapter = new Productadapter(this, productmodelList);
        productView.setAdapter(adapter);

        // List to hold product names for AutoComplete
        productNames = new ArrayList<>();

        // Load Data from Firestore
        loadProducts();

        // Setup AutoCompleteTextView Search
        setupSearchView();

        // Handle search button click
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchView.getText().toString().trim();
                filterList(query);
            }
        });
    }

    private void loadProducts() {
        database.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productmodelList.clear();
                            productNames.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getId();
                                String name = document.getString("name");
                                String imageUrl = document.getString("imageUrl");
                                Long priceLong = document.getLong("rate");
                                String description = document.getString("description");











                                if (name == null) name = "No Name";
                                if (imageUrl == null || imageUrl.isEmpty())
                                    imageUrl = "https://example.com/default-image.png";
                                int price = (priceLong != null) ? priceLong.intValue() : 0;


                                productmodel product = new productmodel(productId, name, price, imageUrl, description);
                                productmodelList.add(product);
                                productNames.add(name);
                            }
                            originalList = new ArrayList<>(productmodelList);


                            adapter.notifyDataSetChanged();

                            // Set AutoComplete Suggestions
                            setAutoCompleteSuggestions();
                        } else {
                            Toast.makeText(Food.this, "Error loading products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupSearchView() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        if (query.isEmpty()) {
            // Restore the full product list
            adapter.updateList(new ArrayList<>(originalList));
        } else {
            List<productmodel> filteredList = new ArrayList<>();
            for (productmodel item : productmodelList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            adapter.updateList(filteredList);
        }
    }



    private void setAutoCompleteSuggestions() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, productNames);
        searchView.setAdapter(adapter);
    }
}
