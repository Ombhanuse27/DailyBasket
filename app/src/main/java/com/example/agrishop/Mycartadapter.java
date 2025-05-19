package com.example.agrishop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Mycartadapter extends RecyclerView.Adapter<Mycartadapter.ViewHolder> {
    Context context;
    List<Mycartmodel> mycartmodelList;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    int totalPrice = 0;

    public Mycartadapter(Context context, List<Mycartmodel> mycartmodelList) {
        this.context = context;
        this.mycartmodelList = mycartmodelList;
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mycartitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mycartmodel cartItem = mycartmodelList.get(position);
        holder.name.setText(cartItem.getName());
        holder.price.setText(cartItem.getPrice() + "₹");
        holder.quntity.setText(String.valueOf(cartItem.getWeight()));


        Glide.with(context)
                .load(cartItem.getImageUrl())  // Ensure getImageUrl() exists in Mycartmodel
                .placeholder(R.drawable.order)  // Add a default placeholder image
                .into(holder.productImage);

        totalPrice += cartItem.getPrice();
        sendTotalPriceUpdate();

        holder.deleteitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition(); // Correct position reference
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    deleteCartItem(adapterPosition);
                }
            }
        });
    }

    private void deleteCartItem(int position) {
        String documentId = mycartmodelList.get(position).getDocumentid();

        firebaseFirestore.collection("AddToCart") // Corrected collection name
                .document(auth.getCurrentUser().getUid())
                .collection("CurrentUser")
                .document(documentId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            int removedPrice = mycartmodelList.get(position).getPrice();
                            mycartmodelList.remove(position);
                            notifyItemRemoved(position);

                            // Update total price and send new total
                            totalPrice -= removedPrice;
                            sendTotalPriceUpdate();

                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendTotalPriceUpdate() {
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return mycartmodelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quntity;
        ImageView deleteitem;
        ImageView  productImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.productprice);
            deleteitem = itemView.findViewById(R.id.delete);
            quntity = itemView.findViewById(R.id.totalq);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
