package com.example.gruhmandal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MaintenanceFragment extends Fragment {
    private RecyclerView recyclerView;
   TextView txt_bill;
    private BillAdapter billAdapter;
    private List<Bill> billList;
    private DatabaseReference databaseReference;
    private String userId;
    private ProgressBar progressBar;
    public interface PaymentCallback {
        void onPaymentInitiated(String billId, double amount);
    }

    private PaymentCallback paymentCallback;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PaymentCallback) {
            paymentCallback = (PaymentCallback) context;
        } else {
            paymentCallback = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        recyclerView = view.findViewById(R.id.rvBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);

        txt_bill = view.findViewById(R.id.txt_no_bill);

        billList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            progressBar.setVisibility(View.VISIBLE);
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("bills");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        billList.clear();
                        for (DataSnapshot billSnapshot : snapshot.getChildren()) {
                            String billId = billSnapshot.getKey();
                            double amount = billSnapshot.child("amount").getValue(Double.class);
                            String dueDate = billSnapshot.child("dueDate").getValue(String.class);
                            String status = billSnapshot.child("status").getValue(String.class);

                            billList.add(new Bill(billId, amount, dueDate, status));
                        }
                        // Sort the bills in newest-first order
                        Collections.sort(billList, (b1, b2) -> {
                            String dueDate1 = (b1.getDueDate() != null) ? b1.getDueDate() : "";
                            String dueDate2 = (b2.getDueDate() != null) ? b2.getDueDate() : "";
                            return dueDate2.compareTo(dueDate1); // Newest first
                        });

                        billAdapter = new BillAdapter(billList, getContext(), paymentCallback);
                        billAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(billAdapter);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        txt_bill.setVisibility(view.VISIBLE);
                        progressBar.setVisibility(View.GONE);
//                        txt_bill.setText("No Bills Found!");
                        Log.e("FirebaseData", "No bills found!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to fetch bills", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

}