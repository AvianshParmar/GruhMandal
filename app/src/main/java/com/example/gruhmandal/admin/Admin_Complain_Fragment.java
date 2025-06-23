package com.example.gruhmandal.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gruhmandal.ComplaintModel;
import com.example.gruhmandal.R;
import com.example.gruhmandal.modeladmin.Admin_Complaint_Adapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Admin_Complain_Fragment extends Fragment {
    private TextView notext,tvTitle;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<ComplaintModel> cList;
    private Admin_Complaint_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin__complain_,container,false);

        tvTitle = view.findViewById(R.id.tvTitle);
        notext = view.findViewById(R.id.noText);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerViewComplaint);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cList = new ArrayList<>();
        adapter = new Admin_Complaint_Adapter(getContext(),cList);
        recyclerView.setAdapter(adapter);

        loadComplaints();
        // Inflate the layout for this fragment
        return view;
    }
    private void loadComplaints(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference CRef = FirebaseDatabase.getInstance().getReference("complaints");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");
        String uid = sharedPreferences.getString("userId", "");

        CRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    ComplaintModel complain = data.getValue(ComplaintModel.class);

                    if(complain.getSid() != null && complain.getSid().equals(societyId)){
                        String userId = complain.getUserId();
                        fetchUserDetails(userId,complain);

                       // cList.add(complain);
                    }else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchUserDetails(String userId, ComplaintModel complain){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String wing = snapshot.child("wingno").getValue(String.class);
                    String flat = snapshot.child("flatno").getValue(String.class);

                    complain.setName(name);
                    complain.setWingno(wing);
                    complain.setFlatno(flat);

                    cList.add(complain);
                    Collections.sort(cList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                }
                if (cList.isEmpty()) {
                    notext.setVisibility(View.VISIBLE);
                } else {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    notext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}