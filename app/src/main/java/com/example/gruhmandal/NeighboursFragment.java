package com.example.gruhmandal;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NeighboursFragment extends Fragment {

    private RecyclerView recyclerView;
    private NeighbourAdapter adapter;
    private List<NeighbourModel> neighbourModelList =  new ArrayList<>();
    private DatabaseReference dr;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbours, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNeighbour);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new NeighbourAdapter(getContext(), neighbourModelList);
        recyclerView.setAdapter(adapter);

        //dr = FirebaseDatabase.getInstance().getReference("users").child("users");
        getCurrentUserWingNo();


        return view;
    }

    //Function
    private void fetchNeighbors(String currentWing) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            usersRef.orderByChild("wingno").equalTo(currentWing).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<NeighbourModel> neighborList = new ArrayList<>();
                    String uid = currentUser.getUid(); // Use FirebaseAuth to get UID

                    for (DataSnapshot data : snapshot.getChildren()) {
                        String otherUserId = data.getKey();
                        NeighbourModel neighbor = data.getValue(NeighbourModel.class);
                        if (neighbor != null && !uid.equals(otherUserId)) {
                            neighborList.add(neighbor);
                        }
                    }
                    // Update RecyclerView with neighbors
                    progressBar.setVisibility(View.GONE);
                    adapter.updateList(neighborList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load neighbors", Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void getCurrentUserWingNo() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            //String userid = userRef.getKey().toString();

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentWing = snapshot.child("wingno").getValue(String.class);
                        fetchNeighbors(currentWing);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}