package com.example.agrishop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Productadapter extends RecyclerView.Adapter<Productadapter.ViewHolder> {
    private Context context;
    private List<productmodel> productList;

    public Productadapter(Context context, List<productmodel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        productmodel product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("₹" + product.getRate());


        // Load image using Glide with placeholder and error handling
        Glide.with(context)
                .load(product.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.order)
                        .error(R.drawable.delete))
                .into(holder.productImage);

        // Handle item click to open ProductDetails activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Details_Activity.class);
            intent.putExtra("productId", product.getProductId()); // Ensure product has a valid ID
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Optimized list update (no full refresh)
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<productmodel> newList) {
        this.productList.clear();
        this.productList.addAll(newList);
        notifyDataSetChanged(); // Better: Use notifyItemRangeChanged() if possible
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.pname);
            productPrice = itemView.findViewById(R.id.prate);
            productImage = itemView.findViewById(R.id.p_image);
        }
    }
}
