package com.example.agrishop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class My_Cart extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser auth;
    TextView overTotalAmount;
    Button buy;

    RecyclerView recyclerView;
    Mycartadapter mycart_adapter;
    List<Mycartmodel> myCart_modelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        db =FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance().getCurrentUser();
        buy=findViewById(R.id.buynow);
        overTotalAmount = findViewById(R.id.totalp);
        recyclerView=findViewById(R.id.mycartrec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));

        myCart_modelList = new ArrayList<>();
        mycart_adapter = new Mycartadapter(getApplicationContext(),myCart_modelList);
        recyclerView.setAdapter(mycart_adapter);
        if(auth != null){
            db.collection("AddToCart").document(auth.getUid())
                    .collection("CurrentUser").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                    String documentid = documentSnapshot.getId();
                                    Mycartmodel myCart_model = documentSnapshot.toObject(Mycartmodel.class);
                                    myCart_model.setDocumentid(documentid);
                                    myCart_modelList.add(myCart_model);
                                    mycart_adapter.notifyDataSetChanged();
                                }

                            }
                        }
                    });
        }else {

        }if(auth==null) {
            Intent intent = new Intent(getApplicationContext(), Login_Page.class);
            startActivity(intent);
           finish();

        }



        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),DummyUPIPayment.class);

                String totalAmountStr = overTotalAmount.getText().toString().replaceAll("[^0-9]", "");
                int totalAmount = totalAmountStr.isEmpty() ? 0 : Integer.parseInt(totalAmountStr);

                intent.putExtra("totalAmount",totalAmount);
                intent.putExtra("itemList",(Serializable)  myCart_modelList);

                startActivity(intent);



            }
        });



    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalBill = intent.getIntExtra("totalAmount",0);
            overTotalAmount.setText("Total Bill = "+totalBill+"₹");
        }
    };

}
