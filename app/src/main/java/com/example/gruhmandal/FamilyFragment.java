package com.example.gruhmandal;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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


public class FamilyFragment extends Fragment {

    EditText etFamilyMemberName, etRelation, etAge;
    private TextView tvGender,noFamilyText,txt_name;
    Button add_Family;
    private RecyclerView recyclerView;
    private FamilyAdapter adapter2;
    private List<FamilyMember> familyList;
    private DatabaseReference reference;
    private ProgressBar progressBar;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_family);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noFamilyText = view.findViewById(R.id.noFamilyText);
        txt_name = view.findViewById(R.id.txt_name);


        progressBar = view.findViewById(R.id.progressBar);

    tvGender = view.findViewById(R.id.tvGender);
        //Drop Down of Gender
        String[] genderOptions2 = {"Male", "Female"};
        tvGender.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this.getContext(), tvGender);
            tvGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_up, 0);
            for (String gender : genderOptions2) {
                popupMenu.getMenu().add(gender);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                tvGender.setText(item.getTitle());  // Set selected gender
                tvGender.setTextColor(Color.BLACK); // Change color after selection
                // Change arrow back to "down" after selection
                tvGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                return true;
            });
            // When popup closes, reset the arrow
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    tvGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_drop_down, 0);
                }
            });
            popupMenu.show();
        });

        etFamilyMemberName = view.findViewById(R.id.etFamilyMemberName);
        etRelation = view.findViewById(R.id.etRelation);
        etAge = view.findViewById(R.id.etAge);

        add_Family = view.findViewById(R.id.btnAddMember);

        add_Family.setOnClickListener(v -> addFamilyMember());

        familyList = new ArrayList<>();
        adapter2 = new FamilyAdapter(getContext(), familyList);
        recyclerView.setAdapter(adapter2);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users")
                .child(auth.getCurrentUser().getUid())
                .child("FamilyMembers");

        loadFamilyMembers();

        return view;
    }
    //Functions
    private void addFamilyMember() {
        // Get Firebase instance
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");

        // Get logged-in user's ID
        String userId = auth.getCurrentUser().getUid();

        // Get details from input fields
        String name = etFamilyMemberName.getText().toString().trim();
        String relation = etRelation.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String gender = tvGender.getText().toString();

        // Validate input
        if (name.isEmpty() || relation.isEmpty() || age.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique ID for the family member
        String memberId = reference.child(userId).child("FamilyMembers").push().getKey();

        // Create a FamilyMember object
        FamilyMember familyMember = new FamilyMember(memberId, name, relation, age, gender);

        // Store in Firebase under the user ID
        reference.child(userId).child("FamilyMembers").child(memberId)
                .setValue(familyMember)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Family Member Added", Toast.LENGTH_SHORT).show();
                    etFamilyMemberName.setText("");
                    etRelation.setText("");
                    etAge.setText("");
                    tvGender.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to Add Member", Toast.LENGTH_SHORT).show();
                    etFamilyMemberName.setText("");
                    etRelation.setText("");
                    etAge.setText("");
                    tvGender.setText("");
                });
    }
    private void loadFamilyMembers() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                familyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FamilyMember member = dataSnapshot.getValue(FamilyMember.class);
                    familyList.add(member);
                }
                if(familyList.isEmpty()){
                    noFamilyText.setVisibility(View.VISIBLE);
                }
                adapter2.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load family members", Toast.LENGTH_SHORT).show();

            }
        });
    }
}