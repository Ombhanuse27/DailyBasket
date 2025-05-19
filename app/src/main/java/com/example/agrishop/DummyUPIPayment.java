package com.example.agrishop;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;




import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DummyUPIPayment extends AppCompatActivity {

    TextView totalAmountTextView;
    EditText upiPasswordEditText;
    Button payButton, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Zero;
    ImageButton Clrbtn;
    private MediaPlayer mediaPlayer;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    AlertDialog.Builder builderDialog;
    AlertDialog dialog;
    private final StringBuilder passwordBuilder = new StringBuilder();
    private int totalAmount;
    private List<Mycartmodel> myCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_upipayment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        totalAmountTextView = findViewById(R.id.totalAmount);
        upiPasswordEditText = findViewById(R.id.pinpass);
        payButton = findViewById(R.id.pay);
        One = findViewById(R.id.one);
        Two = findViewById(R.id.two);
        Three = findViewById(R.id.three);
        Four = findViewById(R.id.four);
        Five = findViewById(R.id.five);
        Six = findViewById(R.id.six);
        Seven = findViewById(R.id.seven);
        Eight = findViewById(R.id.eight);
        Nine = findViewById(R.id.nine);
        Zero = findViewById(R.id.zero);
        Clrbtn = findViewById(R.id.clrbtn);

        // Set click listeners for numeric buttons
        One.setOnClickListener(v -> appendValueToPassword("1"));
        Two.setOnClickListener(v -> appendValueToPassword("2"));
        Three.setOnClickListener(v -> appendValueToPassword("3"));
        Four.setOnClickListener(v -> appendValueToPassword("4"));
        Five.setOnClickListener(v -> appendValueToPassword("5"));
        Six.setOnClickListener(v -> appendValueToPassword("6"));
        Seven.setOnClickListener(v -> appendValueToPassword("7"));
        Eight.setOnClickListener(v -> appendValueToPassword("8"));
        Nine.setOnClickListener(v -> appendValueToPassword("9"));
        Zero.setOnClickListener(v -> appendValueToPassword("0"));
        Clrbtn.setOnClickListener(v -> deleteLastCharacter());

        mAuth = FirebaseAuth.getInstance();

        // Get the total amount and cart items from intent
        // Get the total amount and cart items from intent
        Intent intent = getIntent();
        totalAmount = intent.getIntExtra("totalAmount", 0);
        myCartItems = (List<Mycartmodel>) intent.getSerializableExtra("myCartItems");

// Ensure `myCartItems` is not null before recalculating total amount
        Toast.makeText(this, "Received Total Amount: " + totalAmount, Toast.LENGTH_LONG).show();

// Display total amount
        totalAmountTextView.setText("₹ " + totalAmount);

        payButton.setOnClickListener(view -> {
            String enteredPassword = upiPasswordEditText.getText().toString();

            if ("123456".equals(enteredPassword)) {  // Replace with actual authentication logic
                showPaymentSuccessDialog();
            } else {
                showPaymentFailedDialog();
            }
        });
    }

    private void appendValueToPassword(String value) {
        passwordBuilder.append(value);
        updatePasswordEditText();
    }

    private void deleteLastCharacter() {
        if (passwordBuilder.length() > 0) {
            passwordBuilder.deleteCharAt(passwordBuilder.length() - 1);
        }
        updatePasswordEditText();
    }

    private void updatePasswordEditText() {
        upiPasswordEditText.setText(passwordBuilder.toString());
    }

    private void showPaymentSuccessDialog() {
        builderDialog = new AlertDialog.Builder(DummyUPIPayment.this);
        View layoutView = getLayoutInflater().inflate(R.layout.payment_successful, null);
        Button okButton = layoutView.findViewById(R.id.ok);
        builderDialog.setView(layoutView);
        dialog = builderDialog.create();
        dialog.show();

        mediaPlayer = MediaPlayer.create(this, R.raw.gpsound);  // Payment success sound
        mediaPlayer.start();

        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            clearCart();
            navigateToHome();
        });
    }

    private void showPaymentFailedDialog() {
        builderDialog = new AlertDialog.Builder(DummyUPIPayment.this);
        View layoutView = getLayoutInflater().inflate(R.layout.payment_failed, null);
        Button okButton = layoutView.findViewById(R.id.ok);
        builderDialog.setView(layoutView);
        dialog = builderDialog.create();
        dialog.show();

        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            passwordBuilder.setLength(0);
            updatePasswordEditText();
        });
    }

    private void clearCart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        // Correct path for CurrentUser subcollection
        CollectionReference cartRef = firestore.collection("AddToCart").document(userId).collection("CurrentUser");
        CollectionReference ordersRef = firestore.collection("Orders");

        String orderId = ordersRef.document().getId(); // Generate unique order ID
        String currentDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Fetch all cart items for the current user
        cartRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(DummyUPIPayment.this, "No items in cart", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Map<String, Object>> orderItemsList = new ArrayList<>();
                    WriteBatch batch = firestore.batch();

                    // Convert cart items into a list and prepare for batch delete
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> itemData = document.getData();
                        itemData.put("documentId", document.getId()); // Store original cart document ID
                        orderItemsList.add(itemData);

                        // Add delete operation to batch
                        batch.delete(cartRef.document(document.getId()));
                    }

                    // Order data to store in Firestore
                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("userId", userId);
                    orderData.put("orderId", orderId);
                    orderData.put("orderDateTime", currentDateTime);
                    orderData.put("items", orderItemsList);

                    // Store order in Firestore (Orders collection will be created automatically)
                    batch.set(ordersRef.document(orderId), orderData);

                    // Commit batch write (store order and delete cart items atomically)
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DummyUPIPayment.this, "Order Placed & Cart Cleared", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DummyUPIPayment.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DummyUPIPayment.this, "Error fetching cart items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





    private void navigateToHome() {
        Intent intent = new Intent(DummyUPIPayment.this, User_Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
