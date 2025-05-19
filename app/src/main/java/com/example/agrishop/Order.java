package com.example.agrishop;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order extends AppCompatActivity {
    private RecyclerView recyclerView;
    private adminorderadapter orderAdapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        recyclerView = findViewById(R.id.orderrec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new adminorderadapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        fetchOrders();
    }

    private void fetchOrders() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        db.collection("Orders")
                 // Fetch orders for the logged-in user
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderModel> orderList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");

                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                try {
                                    String orderId = documentSnapshot.getId();
                                    String documentId = item.get("documentId") != null ? item.get("documentId").toString() : "N/A";
                                    String userId = item.get("userId") != null ? item.get("userId").toString() : "Unknown";
                                    String username = item.get("username") != null ? item.get("username").toString() : "No Name";
                                    String address = item.get("address") != null ? item.get("address").toString() : "No Address";
                                    String mobilenumber = item.get("mobilenumber") != null ? item.get("mobilenumber").toString() : "No Mobile";
                                    String imageUrl = item.get("imageUrl") != null ? item.get("imageUrl").toString() : "";
                                    String orderDateTime =documentSnapshot.get("orderDateTime") != null ? documentSnapshot.get("orderDateTime").toString() : "No Date";
                                    String orderTime = item.get("Time") != null ? item.get("Time").toString() : "No Time";
                                    String orderName = item.get("Name") != null ? item.get("Name").toString() : "Unknown";
                                    int orderPrice = item.get("Price") != null ? Integer.parseInt(item.get("Price").toString()) : 0;
                                    int orderQuantity = item.get("Quantity") != null ? Integer.parseInt(item.get("Quantity").toString()) : 1;
                                    String orderStatus = documentSnapshot.get("status") != null ? documentSnapshot.get("status").toString() : "Pending";
                                    String orderWeight = item.get("Weight") != null ? item.get("Weight").toString() : "No Weight";




                                    OrderModel order = new OrderModel(orderId, documentId, userId, username, address, mobilenumber,
                                            imageUrl, orderDateTime, orderTime, orderName, orderPrice, orderQuantity, orderStatus,orderWeight);
                                    orderList.add(order);
                                } catch (Exception e) {
                                    Log.e("Firestore", "Error parsing order data", e);
                                }
                            }
                        }
                    }

                    // Update RecyclerView Adapter
                    adminorderadapter adapter = new adminorderadapter(orderList);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching orders", e));
    }

}
