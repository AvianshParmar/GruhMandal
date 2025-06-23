package com.example.gruhmandal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AmenitiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AmenityAdapter adapter;
    private List<AmenityModel> amenityList;
    private DatabaseReference databaseReference;

    public AmenitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amenities, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewAmenities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Amenities");
        amenityList = new ArrayList<>();
        adapter = new AmenityAdapter(getContext(), amenityList);
        recyclerView.setAdapter(adapter);

        fetchAmenitiesFromFirebase();
        //saveComplaintToFirebase();

        return view;
    }
    //Function
    private void fetchAmenitiesFromFirebase() {
        //Toast.makeText(getContext(), "" + databaseReference, Toast.LENGTH_SHORT).show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String societyId = sharedPreferences.getString("societyId", "");

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    amenityList.clear(); // Clear old data to prevent duplicates
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AmenityModel amenity = dataSnapshot.getValue(AmenityModel.class);
                        if (amenity != null && amenity.getSid().equals(societyId) ) {
                            amenityList.add(amenity);
                            Log.d("FirebaseData", "Amenity: " + amenity.getName());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d("FirebaseData", "Amenity: " + "No data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load Amenities", Toast.LENGTH_SHORT).show();
            }
        });
    }

}