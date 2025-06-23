package com.example.gruhmandal;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class VehicleFragment extends Fragment {
    private TextView VehicleType,noText;
    private EditText Vehile_no,vehicleColor;
    private DatabaseReference databaseReference,databaseReference1;
    private FirebaseAuth mAuth;
    private Button btn_submit;
    private VehicleAdapter vehicleAdapter;
    private List<VehicleModel> vehicleList;
    private String userId;
    private RecyclerView recyclerViewVehicles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);

        view.setClickable(false);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userId = mAuth.getCurrentUser().getUid();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Vehicles");
        VehicleType =  view.findViewById(R.id.vehicleTypeEditText);
        Vehile_no = view.findViewById(R.id.vehicleNumberEditText);
        vehicleColor = view.findViewById(R.id.vehicleColorEditText);
        btn_submit = view.findViewById(R.id.addVehicleButton);
        noText = view.findViewById(R.id.noText);

        //For Vehicle Type
        String[] Vehi_opt = {"Bicycle","Bike","Car","Other"};
        VehicleType.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this.getContext(), VehicleType);
            VehicleType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_up, 0);
            for (String vehicle : Vehi_opt) {
                popupMenu.getMenu().add(vehicle);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                VehicleType.setText(item.getTitle());  // Set selected gender
                VehicleType.setTextColor(Color.BLACK);// Change color after selection
                if(VehicleType.getText().toString().equals("Bicycle")){
                    Vehile_no.setVisibility(View.GONE);
                    vehicleColor.setVisibility(View.VISIBLE);
                }else {
                    Vehile_no.setVisibility(View.VISIBLE);
                    vehicleColor.setVisibility(View.GONE);
                }
                // Change arrow back to "down" after selection
                VehicleType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                return true;
            });
            // When popup closes, reset the arrow
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    VehicleType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                }
            });
            popupMenu.show();
        });

        Vehile_no.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private boolean deletingHyphen;
            private int hyphenIndex;
            private int cursorPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorPosition = Vehile_no.getSelectionStart();// Save cursor position
                // Check if user is deleting a hyphen
                if (count == 1 && after == 0 && s.charAt(start) == '-') {
                    deletingHyphen = true;
                    hyphenIndex = start;
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isFormatting || deletingHyphen) return;

                isFormatting = true;
                String cleanText = s.toString().replaceAll("-", ""); // Remove existing dashes
                cleanText = cleanText.toUpperCase();
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < cleanText.length(); i++) {
                    formatted.append(cleanText.charAt(i));
                    if (i == 1 || i == 3 || i == 5) { // Add dashes at correct positions
                        formatted.append("-");
                    }
                }
                Vehile_no.setText(formatted);
                Vehile_no.setSelection(Math.min(formatted.length(), formatted.length())); // Set cursor
                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (deletingHyphen && hyphenIndex > 0) {
                    s.delete(hyphenIndex - 1, hyphenIndex); // Remove hyphen properly
                }
            }
        });


        //Set RecyclerView Properties
        recyclerViewVehicles = view.findViewById(R.id.vehicleRecyclerView);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(getContext(), vehicleList, userId);
        recyclerViewVehicles.setAdapter(vehicleAdapter);


        // Add vehicle to Firebase on button click
        btn_submit.setOnClickListener(v -> {
            String vehicleType = VehicleType.getText().toString();
           // String vehicleNo = Vehile_no.getText().toString();
            String userId = mAuth.getCurrentUser().getUid();
            //To tell user to select vehicle type
            if (vehicleType.isEmpty()) {
                Toast.makeText(getContext(), "Please select a vehicle type!", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference userVehicleRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("Vehicles");

            String vehicleId = userVehicleRef.push().getKey();

            if (vehicleType.equals("Bicycle")) {
                String color = vehicleColor.getText().toString().trim();
                if (color.isEmpty()) {
                    Toast.makeText(getContext(), "Enter Bicycle Color", Toast.LENGTH_SHORT).show();
                    return;
                }
                VehicleModel vehicleModel = new VehicleModel(vehicleId, vehicleType, color);
                userVehicleRef.child(vehicleId).setValue(vehicleModel);
                vehicleColor.setText("");
            } else {
                String vehicleNumber = Vehile_no.getText().toString().trim();
                if (vehicleNumber.isEmpty()) {
                    Toast.makeText(getContext(), "Enter Vehicle Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                VehicleModel vehicleModel = new VehicleModel(vehicleId, vehicleType, vehicleNumber);
                userVehicleRef.child(vehicleId).setValue(vehicleModel);
                Vehile_no.setText("");
            }
            Toast.makeText(getContext(), "Vehicle Added Successfully", Toast.LENGTH_SHORT).show();
            VehicleType.setText("");
        });

        // Fetch and Display Vehicles
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot vehicleSnapshot : snapshot.getChildren()) {
                    VehicleModel vehicle = vehicleSnapshot.getValue(VehicleModel.class);
                    vehicleList.add(vehicle);
                }
                if(vehicleList.isEmpty()){
                    noText.setVisibility(View.VISIBLE);
                }
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}