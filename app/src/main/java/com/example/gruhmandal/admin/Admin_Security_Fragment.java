package com.example.gruhmandal.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gruhmandal.R;
import com.example.gruhmandal.SecurityModel;
import com.example.gruhmandal.modeladmin.Admin_Security_Adapter;
import com.example.gruhmandal.modeladmin.Admin_Security_Model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Security_Fragment extends Fragment {
    private EditText etname,etcontact,etposition;
    private TextView all_txt;
    private RecyclerView recyclerView;
    private List<Admin_Security_Model> guardList;
    private Admin_Security_Adapter adapter;
    private ProgressBar pbar1,pbar2;
    private Button btn_add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin__security_, container, false);
        // Inflate the layout for this fragment

        etname = view.findViewById(R.id.etname);
        etcontact =view.findViewById(R.id.etcontact);
        etposition =view.findViewById(R.id.etPostion);
        recyclerView = view.findViewById(R.id.rvallSecurity);
        btn_add = view.findViewById(R.id.btnAddSecurity);
        pbar1 = view.findViewById(R.id.loading_screen);
        pbar2 = view.findViewById(R.id.progressBar2);
        all_txt = view.findViewById(R.id.noNoticeText);

        //Button For adding new Secutrity Guard!
        btn_add.setOnClickListener(v -> {
            updateSecurity();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        guardList = new ArrayList<>();
        adapter = new Admin_Security_Adapter(getContext(),guardList);
        recyclerView.setAdapter(adapter);
        fetchGuardsFromFirebase();
        return view;
    }
    //Function
    private void updateSecurity(){
        pbar1.setVisibility(View.VISIBLE);
        String name = etname.getText().toString();
        String contact = etcontact.getText().toString();
        String postion = etposition.getText().toString();

        if (name.isEmpty() || contact.isEmpty() || postion.isEmpty()) {
            Toast.makeText(getContext(), "Enter all the details!", Toast.LENGTH_SHORT).show();
            pbar1.setVisibility(View.GONE);
            return;
        }
        DatabaseReference securityRef = FirebaseDatabase.getInstance().getReference("security_guards");
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String SocietyId = prefs.getString("societyId", "");
        String userdId = prefs.getString("userId", "");

        securityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String securityId = securityRef.child("security_guards").push().getKey();
                    if (securityId != null) {
                        Map<String, Object> SecurityData = new HashMap<>();
                        SecurityData.put("name", name);
                        SecurityData.put("contact", "+91 "+ contact);
                        SecurityData.put("postion", postion);
                        SecurityData.put("sid", SocietyId);
                        SecurityData.put("uid", userdId);
                        SecurityData.put("status", "0");
                        SecurityData.put("securityid", securityId);

                        securityRef.child(securityId).setValue(SecurityData);
                        etname.setText(null);
                        etcontact.setText(null);
                        etposition.setText(null);
                    }
                    Toast.makeText(getContext(), "Security Updated Successfully!", Toast.LENGTH_SHORT).show();
                    pbar1.setVisibility(View.GONE);
                }
                @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Security Updated Error!", Toast.LENGTH_SHORT).show();
                    pbar1.setVisibility(View.GONE);
            }
        });
    }
    private void fetchGuardsFromFirebase() {
        pbar2.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("security_guards");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                guardList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Admin_Security_Model guard = data.getValue(Admin_Security_Model.class);
                    if (guard != null && guard.getSid() != null){
                        if (guard.getSid().equals(societyId)){
                            guardList.add(guard);
                        }
                    }
                }
                if(guardList.isEmpty()){
                    all_txt.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                pbar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbar2.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}