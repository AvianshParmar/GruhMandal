package com.example.gruhmandal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder> {
    private Context context;
    private List<FamilyMember> familyList;

    public FamilyAdapter(Context context, List<FamilyMember> familyList) {
        this.context = context;
        this.familyList = familyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.family_member_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMember member = familyList.get(position);
        holder.tvName.setText(member.getName());
        holder.tvRelation.setText("Relation: " + member.getRelation());
        holder.tvAge.setText("Age: " + member.getAge());
        holder.tvGender.setText("Gender: " + member.getGender());

        holder.btn_Remove.setOnClickListener(v -> {
            String F_id = familyList.get(position).getFamId();
            if (F_id == null || F_id.isEmpty()) {
                Toast.makeText(context, "Error: Vehicle ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Get current user ID

            DatabaseReference userFamRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId) // Get the current user's node
                    .child("FamilyMembers")
                    .child(familyList.get(position).getFamId()); // Get the specific vehicle ID

            // Remove vehicle from Firebase
            userFamRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Vehicle Removed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to Remove Vehicle", Toast.LENGTH_SHORT).show();
                }
            });

        });

        // Change icon based on gender
//        if (member.getGender().equalsIgnoreCase("Male")) {
//            holder.imgProfile.setImageResource(R.drawable.male_icon);
//        } else {
//            holder.imgProfile.setImageResource(R.drawable.female_icon);
//        }
    }

    @Override
    public int getItemCount() {
        return familyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRelation, tvAge, tvGender;
        ImageView imgdelete;
        Button btn_Remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvRelation = itemView.findViewById(R.id.tvRelation);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvGender = itemView.findViewById(R.id.tvGender);
            imgdelete = itemView.findViewById(R.id.imgRemove);
            btn_Remove = itemView.findViewById(R.id.removeVehicle);
            //imgProfile = itemView.findViewById(R.id.img_member_icon);
        }
    }
}

