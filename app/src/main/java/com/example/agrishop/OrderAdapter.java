package com.example.agrishop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderModel> orderList;

    public OrderAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.orderName.setText(order.getOrderName());
        holder.orderDate.setText(order.getOrderDateTime());
        holder.orderPrice.setText(String.valueOf(order.getOrderPrice()));
        holder.orderQuantity.setText(String.valueOf(order.getOrderWeight()));

        holder.orderStatus.setText(order.getOrderStatus().equals("Unknown") ? "Pending" : order.getOrderStatus());




        // Load Image from URL
        Glide.with(holder.itemView.getContext())
                .load(order.getImageUrl())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderDate, orderPrice, orderQuantity, orderTime, address, mobileNumber, username, orderStatus;
        ImageView productImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.order_name);
            orderDate = itemView.findViewById(R.id.order_date);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderQuantity = itemView.findViewById(R.id.order_quantity);

            orderStatus = itemView.findViewById(R.id.status);

            productImage = itemView.findViewById(R.id.product_image);

        }
    }
}
