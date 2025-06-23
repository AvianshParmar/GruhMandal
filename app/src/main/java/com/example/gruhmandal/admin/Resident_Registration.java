package com.example.gruhmandal.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gruhmandal.AdminDashboard;
import com.example.gruhmandal.R;
import com.example.gruhmandal.modeladmin.Registration_Adapter;
import com.example.gruhmandal.modeladmin.Registration_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Resident_Registration extends Fragment {

    private RecyclerView recyclerView;
    private Registration_Adapter adapter;
    private List<Registration_Model> pendingUsers;
    private DatabaseReference usersRef;
    private ProgressBar progressBar;
    private TextView no_request_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resident__registration, container, false);
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycler_registraion_status);
        progressBar = view.findViewById(R.id.progressBar);
        no_request_text = view.findViewById(R.id.no_request_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pendingUsers = new ArrayList<>();
        adapter = new Registration_Adapter(getContext(), pendingUsers);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fetchresident();

        return view;
    }
    //Functions
    private void fetchresident() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Fetch user data inside the unique userId node
                    Registration_Model user = userSnapshot.getValue(Registration_Model.class);
                    if (user != null && user.getSid() != null && user.getStatus() != null) {
                        // Check if user belongs to same society and has pending status
                        if (user.getSid().equals(societyId) && user.getStatus().equals("Pending")) {
                            user.setUserId(userSnapshot.getKey()); // Store userId
                            pendingUsers.add(user);
                        }
                    }
                }
                if(pendingUsers.isEmpty()){
                    no_request_text.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}