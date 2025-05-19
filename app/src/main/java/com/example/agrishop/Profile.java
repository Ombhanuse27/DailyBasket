package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile extends AppCompatActivity {
    ImageView profile;
    TextView name, email, number, address;
    Button update;
    FirebaseUser auth;
    FirebaseDatabase database;
    private String userID;
    private DatabaseReference reference;
    private static final String CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/da9xvfoye/image/upload";
    private static final String CLOUDINARY_PRESET = "ml_default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Usersregister");
        userID = auth.getUid();

        profile = findViewById(R.id.profile_image);
        update = findViewById(R.id.updatebtn);
        name = findViewById(R.id.yourname);
        email = findViewById(R.id.youremail);
        number = findViewById(R.id.yourmobilenumber);
        address = findViewById(R.id.youraddress);

        if (auth == null) {
            startActivity(new Intent(getApplicationContext(), Login_Page.class));
            finish();
        }

        loadUserProfile();
        loadProfileImage();

        profile.setOnClickListener(v -> selectProfileImage());
        update.setOnClickListener(v -> updateUserProfile());
    }

    private void loadUserProfile() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usermodel userProfile = snapshot.getValue(Usermodel.class);
                if (userProfile != null) {
                    name.setText(userProfile.getName());
                    email.setText(userProfile.getEmail());
                    number.setText(userProfile.getPhone());
                    address.setText(userProfile.getUseraddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usermodel user = snapshot.getValue(Usermodel.class);
                if (user != null && user.getProfileImg() != null) {
                    Glide.with(getApplicationContext()).load(user.getProfileImg()).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 33);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            Uri profileUri = data.getData();
            profile.setImageURI(profileUri);
            uploadProfileImageToCloudinary(profileUri);
        } else {
            Toast.makeText(getApplicationContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImageToCloudinary(Uri imageUri) {
        new Thread(() -> {
            try {
                File file = FileUtils.getFileFromUri(this, imageUri);
                if (file == null) {
                    Log.e("Cloudinary", "File conversion failed");
                    return;
                }

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                        .addFormDataPart("upload_preset", CLOUDINARY_PRESET)
                        .build();

                Request request = new Request.Builder()
                        .url(CLOUDINARY_URL)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();

                    // Parse JSON response correctly
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String imageUrl = jsonObject.get("secure_url").getAsString();

                    saveImageUrlToDatabase(imageUrl);
                } else {
                    Log.e("Cloudinary", "Upload failed: " + response.message());
                }
            } catch (Exception e) {
                Log.e("Cloudinary", "Error uploading file", e);
            }
        }).start();
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        reference.child(userID).child("profileImg").setValue(imageUrl)
                .addOnSuccessListener(unused ->
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e ->
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()));
    }

    private void updateUserProfile() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name.getText().toString().trim());
        updates.put("email", email.getText().toString().trim());
        updates.put("phone", number.getText().toString().trim());
        updates.put("useraddress", address.getText().toString().trim());

        reference.child(userID).updateChildren(updates)
                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
