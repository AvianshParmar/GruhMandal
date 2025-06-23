package com.example.gruhmandal;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Security extends Fragment {
    private TextView noSecurityText;
    private RecyclerView recyclerView;
    private SecurityAdapter adapter;
    private List<SecurityModel> guardList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSecurity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);
        noSecurityText = view.findViewById(R.id.noSecurityText);

        guardList = new ArrayList<>();
        adapter = new SecurityAdapter(getContext(), guardList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("security_guards");
        fetchGuardsFromFirebase();


        return view;
    }
    private void fetchGuardsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String SocietyId = prefs.getString("societyId", "");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                guardList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    SecurityModel guard = data.getValue(SecurityModel.class);
                    if(guard != null){
                        if(guard.getSid().equals(SocietyId)){
                            guardList.add(guard);
                        }
                    }
                }
                if(guardList.isEmpty()){
                    noSecurityText.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}