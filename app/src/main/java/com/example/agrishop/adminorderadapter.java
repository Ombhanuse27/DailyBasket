package com.example.agrishop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class adminorderadapter extends RecyclerView.Adapter<adminorderadapter.OrderViewHolder> {
    private List<OrderModel> orderList;
    private FirebaseFirestore db;

    public adminorderadapter(List<OrderModel> orderList) {
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();  // Initialize Firestore
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.orderName.setText(order.getOrderName());
        holder.orderPrice.setText(String.valueOf(order.getOrderPrice()));
        holder.orderQuantity.setText(String.valueOf(order.getOrderQuantity()));
        holder.address.setText(order.getAddress());
        holder.mobileNumber.setText(order.getMobileNumber());
        holder.username.setText(order.getUsername());
        holder.orderStatus.setText(order.getOrderStatus());  // Show current status

        // Load Image from URL
        Glide.with(holder.itemView.getContext())
                .load(order.getImageUrl())
                .into(holder.productImage);

        // Handle "In Progress" button click
        holder.btnInProgress.setOnClickListener(v -> updateOrderStatus(order.getOrderId(), "In Progress", holder));

        // Handle "Delivered" button click
        holder.btnDelivered.setOnClickListener(v -> updateOrderStatus(order.getOrderId(), "Delivered", holder));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderPrice, orderQuantity, address, mobileNumber, username, orderStatus;
        ImageView productImage;
        Button btnInProgress, btnDelivered;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.order_name);
            orderPrice = itemView.findViewById(R.id.order_price2);
            orderQuantity = itemView.findViewById(R.id.order_quantity);
            address = itemView.findViewById(R.id.order_address);
            mobileNumber = itemView.findViewById(R.id.order_mobile);
            username = itemView.findViewById(R.id.order_username);
            orderStatus = itemView.findViewById(R.id.order_status);  // Status TextView
            productImage = itemView.findViewById(R.id.product_image);
            btnInProgress = itemView.findViewById(R.id.btn_in_progress);
            btnDelivered = itemView.findViewById(R.id.btn_delivered);
        }
    }

    // Function to update the order status in Firestore
    private void updateOrderStatus(String orderId, String newStatus, OrderViewHolder holder) {
        db.collection("Orders").document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    holder.orderStatus.setText(newStatus);  // Update UI
                    Toast.makeText(holder.itemView.getContext(), "Order marked as " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(), "Failed to update order", Toast.LENGTH_SHORT).show();
                });
    }
}
