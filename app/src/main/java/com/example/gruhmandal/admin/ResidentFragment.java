package com.example.gruhmandal.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gruhmandal.R;
import com.example.gruhmandal.modeladmin.ResidentAdapter;
import com.example.gruhmandal.modeladmin.ResidentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResidentFragment extends Fragment {
    private TextView wings,TotalResi,noText;
    private RecyclerView recyclerView;
    private ResidentAdapter adapter;
    private ArrayList<ResidentModel> residentList;
    private ProgressBar progressBar;
    private int finaltotal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resident, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");

        progressBar = view.findViewById(R.id.progressBar);
        TotalResi = view.findViewById(R.id.TotalResi);
        noText = view.findViewById(R.id.noText);

        wings = view.findViewById(R.id.tvWing);
        DatabaseReference wingsRef = FirebaseDatabase.getInstance().getReference("Societies").child(societyId).child("wings");
        wings.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), wings);
            Menu menu = popupMenu.getMenu();
            wings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_up, 0);

            wingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    menu.clear();
                    for (DataSnapshot wingSnap : snapshot.getChildren()) {
                        menu.add(wingSnap.getKey()); // Add wing name
                    }
                    popupMenu.setOnMenuItemClickListener(item -> {
                        String selectedWing = item.getTitle().toString();
                        wings.setText(selectedWing);
                        wings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                        fetchResidents(societyId,selectedWing);
                        return true;
                    });
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            wings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                        }
                    });
                    popupMenu.show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        recyclerView = view.findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        residentList = new ArrayList<>();
        adapter = new ResidentAdapter(residentList,getContext());
        recyclerView.setAdapter(adapter);

//
        return view;
    }
    //Function
    private void fetchResidents(String sid,String wing) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                residentList.clear();  // Clear previous list

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    ResidentModel user = userSnapshot.getValue(ResidentModel.class);

                    if (user != null && user.getSid() != null && user.getSid().equals(sid) && user.getWingno()!= null && user.getWingno().equals(wing)) {
                        // Get Family Members Count
                        DataSnapshot familySnapshot = userSnapshot.child("FamilyMembers");
                        int familyCount = (int) familySnapshot.getChildrenCount();
                        // Get Vehicles Count
                        DataSnapshot vehicleSnapshot = userSnapshot.child("Vehicles");
                        int vehicleCount = (int) vehicleSnapshot.getChildrenCount();

                        user.setFamilyCount(familyCount);
                        user.setVehicleCount(vehicleCount);

                        residentList.add(user);
                        finaltotal = finaltotal + 1;
                    }
                }
                TotalResi.setVisibility(View.VISIBLE);
                TotalResi.setText(wing +" Residents: " +String.valueOf(finaltotal));
                finaltotal = 0;
                // Sort the list by flat number (convert string to integer to avoid sorting issues)
                Collections.sort(residentList, new Comparator<ResidentModel>() {
                    @Override
                    public int compare(ResidentModel u1, ResidentModel u2) {
                        return Integer.compare(Integer.parseInt(u1.getFlatno()), Integer.parseInt(u2.getFlatno()));
                    }
                });
                if(residentList.isEmpty()){
                    noText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else {
                    noText.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);// Refresh RecyclerView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

