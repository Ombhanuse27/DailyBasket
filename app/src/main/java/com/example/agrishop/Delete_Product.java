package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Delete_Product extends AppCompatActivity {
    private FirebaseFirestore database;
    private RecyclerView productView;
    private List<productmodel> productmodelList;
    private Deleteadapter deleteadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        database = FirebaseFirestore.getInstance();
        productView = findViewById(R.id.foodrec);
        productmodelList = new ArrayList<>();
        deleteadapter = new Deleteadapter(this, productmodelList);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productView.setLayoutManager(layoutManager);
        productView.setAdapter(deleteadapter);

        fetchProductsFromFirestore();
    }

    private void fetchProductsFromFirestore() {
        database.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productmodelList.clear(); // Ensure no duplicate data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                productmodel product_model = document.toObject(productmodel.class);
                                product_model.setProductId(document.getId()); // Store document ID
                                productmodelList.add(product_model);
                            }
                            deleteadapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Delete_Product.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
