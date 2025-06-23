package com.example.gruhmandal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {
    private Context context;
    private List<ComplaintModel> complaintList;

    public ComplaintAdapter(Context context, List<ComplaintModel> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintModel complaint = complaintList.get(position);
        holder.tvSubject.setText(complaint.getSubject());
        holder.tvDetails.setText(complaint.getDetails());
        holder.tvStatus.setText(complaint.getStatus());

        if (complaint.getStatus().equals("Pending")){
            holder.tvStatus.setText("Complain Resolve " +complaint.getStatus());
            holder.tvStatus.setTextColor(Color.RED);
        }else {
            String DarkGreen = "#0B7100";
            holder.tvStatus.setTextColor(Color.parseColor(DarkGreen));
            holder.tvStatus.setText("Complain Resolved Successfully");
        }
        // Load Cloudinary image using Glide
        Glide.with(context)
                .load(complaint.getImageUrl())
                .placeholder(R.drawable.placeholder)
                //error(R.drawable.caring_society)// Add a placeholder image in drawable
                .into(holder.ivComplaintImage);

        holder.removebtn.setOnClickListener(v -> {

            String cId = complaintList.get(position).getCid(); // Get the vehicle ID
            if (cId == null || cId.isEmpty()) {
                Toast.makeText(context, "Error: Complaint ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

            DatabaseReference userVehicleRef = FirebaseDatabase.getInstance().getReference("complaints")// Get the current user's node
                    .child(complaintList.get(position).getCid()); // Get the specific vehicle ID

            // Remove vehicle from Firebase
            userVehicleRef.removeValue().addOnCompleteListener(task -> {
                if (userVehicleRef != null) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Complaint Removed Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to Remove Complaint!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,"Error: Complaint ID is missing!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvDetails,tvStatus;
        ImageView ivComplaintImage,imgRemove;
        Button removebtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.complaint_subject);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvStatus = itemView.findViewById(R.id.complaint_status);
            ivComplaintImage = itemView.findViewById(R.id.complaint_image1);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            removebtn = itemView.findViewById(R.id.removeVehicle);
        }
    }
}
