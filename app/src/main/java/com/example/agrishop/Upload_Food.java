package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Upload_Food extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    String[] page = {"Choose Option", "Apple", "Banana-Robusta", "Greps", "Brinjal Thai Eggplant", "Papaya", "Black Greps", "Potato", "Lady finger", "Tomato", "Kiwi", "Cauliflower", "Pineapple", "Onion", "Chillies", "Carrot", "Orange", "Strawberry", "Beans"};
    TextView textView;

    ImageView uploadPicIV;
    EditText name, description, offer, rate;
    ProgressBar uploadProgressBar;
    final int IMAGE_REQUEST = 71;
    Uri imageLocationPath;
    FirebaseFirestore objectFirebaseFirestore;

    String CLOUD_NAME = "da9xvfoye";
    String UPLOAD_PRESET = "ml_default"; // Replace with your Cloudinary upload preset

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_food);

        spinner = findViewById(R.id.spinner);
        description = findViewById(R.id.adddes);
        textView = findViewById(R.id.text);
        offer = findViewById(R.id.enteroffer);
        rate = findViewById(R.id.enterrate);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, page);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        uploadPicIV = findViewById(R.id.imageID);
        name = findViewById(R.id.imageName);
        uploadProgressBar = findViewById(R.id.progress_bar);

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void selectImage(View view) {
        Intent objectIntent = new Intent();
        objectIntent.setType("image/*");
        objectIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(objectIntent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageLocationPath = data.getData();
            try {
                Bitmap objectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageLocationPath);
                uploadPicIV.setImageBitmap(objectBitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadImage(View view) {
        if (imageLocationPath == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadProgressBar.setVisibility(View.VISIBLE);

        File imageFile = FileUtils.getFileFromUri(this, imageLocationPath);
        if (imageFile == null) {
            uploadProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(imageFile, MediaType.parse("image/*")))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    uploadProgressBar.setVisibility(View.GONE);
                    Toast.makeText(Upload_Food.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String imageUrl = jsonObject.getString("secure_url");
                        storeDataInFirestore(imageUrl);
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(Upload_Food.this, "JSON Error", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Upload_Food.this, "Image upload failed", Toast.LENGTH_SHORT).show());
                }
                runOnUiThread(() -> uploadProgressBar.setVisibility(View.GONE));
            }
        });
    }

    private void storeDataInFirestore(String imageUrl) {
        String productName = name.getText().toString();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("imageUrl", imageUrl);
        objectMap.put("name", name.getText().toString());
        objectMap.put("description", description.getText().toString());
        objectMap.put("type", textView.getText().toString());
        objectMap.put("offer", offer.getText().toString());
        objectMap.put("rate", Integer.parseInt(rate.getText().toString()));

        objectFirebaseFirestore.collection("Product").add(objectMap)
                .addOnSuccessListener(documentReference -> {
                    saveNotification(productName);
                    runOnUiThread(() -> Toast.makeText(Upload_Food.this, "Upload Successfully", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(Upload_Food.this, "Failed to upload data", Toast.LENGTH_SHORT).show()));
    }

    private void saveNotification(String productName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String notificationId = databaseReference.push().getKey(); // Generate unique ID for notification
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", "New product added: " + productName);
        notificationData.put("timestamp", currentTime);


        if (notificationId != null) {
            databaseReference.child(notificationId).setValue(notificationData)
                    .addOnSuccessListener(aVoid -> runOnUiThread(() -> Toast.makeText(Upload_Food.this, "Notification Saved", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(Upload_Food.this, "Failed to save notification", Toast.LENGTH_SHORT).show()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textView.setText(spinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
