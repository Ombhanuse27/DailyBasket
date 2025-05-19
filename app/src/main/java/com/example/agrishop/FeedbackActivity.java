package com.example.agrishop;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<FeedbackModel> feedbackList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        recyclerView = findViewById(R.id.feedback_recycler);
        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadFeedbacks();
    }

    private void loadFeedbacks() {
        db.collection("Feedback")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    feedbackList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FeedbackModel feedback = doc.toObject(FeedbackModel.class);
                        feedbackList.add(feedback);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
