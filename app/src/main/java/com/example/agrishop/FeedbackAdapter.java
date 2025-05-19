package com.example.agrishop;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrishop.FeedbackModel;
import com.example.agrishop.R;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackModel> feedbackList;

    public FeedbackAdapter(List<FeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackModel feedback = feedbackList.get(position);
        holder.name.setText(feedback.getName());
        holder.email.setText(feedback.getEmail());
        holder.product.setText("Product: " + feedback.getProduct());
        holder.ratingBar.setRating(feedback.getRating());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, product;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.feedback_name);
            email = itemView.findViewById(R.id.feedback_email);
            product = itemView.findViewById(R.id.feedback_product);
            ratingBar = itemView.findViewById(R.id.feedback_rating);
        }
    }
}
