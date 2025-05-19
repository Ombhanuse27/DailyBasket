package com.example.agrishop;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        fetchNotifications();
    }

    private void fetchNotifications() {
        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                    if (notification != null) {
                        notificationList.add(notification);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
