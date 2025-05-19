package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register_Page extends AppCompatActivity {
    EditText mFullName,mEmail,mPassword,mPhone,address;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase database;
    FirebaseFirestore fstore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mFullName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        address=findViewById(R.id.address);
        mRegisterBtn=findViewById(R.id.register);
        mLoginBtn = findViewById(R.id.login);
        database=FirebaseDatabase.getInstance();
        fstore=FirebaseFirestore.getInstance();

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

       /* if(fAuth.getCurrentUser() !=null){
            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            //finish();
            Intent intent = new Intent(Register.this, Login_Page.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/

        mRegisterBtn.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmail.getText().toString().trim();
                String password= mPassword.getText().toString().trim();
                String name= mFullName.getText().toString().trim();
                String phone= mPhone.getText().toString().trim();
                String useraddress= address.getText().toString().trim();

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


                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            Usermodel users = new Usermodel (email,password,name,phone,useraddress,uid,0);

                            database.getReference().child("Usersregister").child(uid).setValue(users);
                            Toast.makeText(Register_Page.this, "User Create", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register_Page.this, Login_Page.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });


            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login_Page.class));
            }
        });


    }
}