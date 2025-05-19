package com.example.agrishop;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrishop.R;
import com.example.agrishop.productmodel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class Deleteadapter extends RecyclerView.Adapter<Deleteadapter.ViewHolder> {
    private Context context;
    private List<productmodel> productList;
    private FirebaseFirestore database;

    public Deleteadapter(Context context, List<productmodel> productList) {
        this.context = context;
        this.productList = productList;
        this.database = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        productmodel product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("₹" + product.getRate());




        // Load and display product image using Glide or Picasso
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .into(holder.productImage);

        // Set delete button click listener
        holder.deleteProduct.setOnClickListener(view -> {
            deleteProductFromFirestore(product.getProductId(), position);
        });
    }

    private void deleteProductFromFirestore(String productId, int position) {
        database.collection("Product").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
                    productList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView deleteProduct, productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.dname);
            productPrice = itemView.findViewById(R.id.drate);
            productImage = itemView.findViewById(R.id.d_image);
            deleteProduct = itemView.findViewById(R.id.deleteproduct);
        }
    }
}
