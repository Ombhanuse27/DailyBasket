package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Page extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mRegisterBtn;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    TextView ForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login);
        database=FirebaseDatabase.getInstance();
        ForgotPassword = findViewById(R.id.forgot_password);

        fAuth=FirebaseAuth.getInstance();
//        checkUserSession();

      /*  if(fAuth.getCurrentUser() !=null){
            Intent intent = new Intent(Login_Page.this, Login_Page.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password= mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6)
                {
                    mPassword.setError("Password Must be >=6 Characters");
                    return;
                }

                //authenticate the user


                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            database.getReference().child("Usersregister").child(uid).child("usertype").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int usertype = snapshot.getValue(Integer.class);
                                    if (usertype == 0){
                                        Intent intent = new Intent(Login_Page.this, User_Dashboard.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    if (usertype == 1){
                                        Intent intent = new Intent(Login_Page.this, Admin_Dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    //    Toast.makeText(Login_Page.this, "Enter Service Email", Toast.LENGTH_SHORT).show();

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login_Page.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), Register_Page.class);
                startActivity(intent);
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });
    }

//    private void checkUserSession() {
//        if (fAuth.getCurrentUser() != null) {
//            String uid = fAuth.getCurrentUser().getUid();
//            database.getReference().child("Usersregister").child(uid).child("usertype")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                Integer usertype = snapshot.getValue(Integer.class);
//                                if (usertype != null) {
//                                    navigateToDashboard(usertype);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(Login_Page.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }

    private void navigateToDashboard(int usertype) {
        Intent intent;
        if (usertype == 0) {
            intent = new Intent(Login_Page.this, User_Dashboard.class);
        } else {
            intent = new Intent(Login_Page.this, Admin_Dashboard.class);
        }
        startActivity(intent);
        finish();
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        EditText emailInput = dialogView.findViewById(R.id.reset_email);

        builder.setTitle("Reset Password")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailInput.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(Login_Page.this, "Enter your email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        fAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Login_Page.this, "Reset email sent!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login_Page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}


