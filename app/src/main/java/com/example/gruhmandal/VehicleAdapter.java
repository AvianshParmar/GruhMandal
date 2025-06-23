package com.example.gruhmandal;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    private Context context;
    private List<VehicleModel> vehicleList;
    private DatabaseReference databaseReference;
    private String userId;
    public VehicleAdapter(Context context, List<VehicleModel> vehicleList, String userId) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.userId = userId;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Vehicles");
    }

    @NonNull
    @Override
    public VehicleAdapter.VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_item, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.VehicleViewHolder holder, int position) {
        VehicleModel vehicle = vehicleList.get(position);
        holder.textVehicleType.setText("Vehicle Type: " + vehicle.getVehicleType());

        if (vehicle.getVehicleType().equals("Bicycle")) {
            holder.textVehicleDetail.setText("Color: " + vehicle.getVehicleNumberOrColor());
        } else {
            holder.textVehicleDetail.setText("Vehicle No: " + vehicle.getVehicleNumberOrColor());
        }

        // Remove Vehicle
        holder.btnRemoveVehicle.setOnClickListener(v -> {
            String vehicleId = vehicleList.get(position).getVehicleId(); // Get the vehicle ID
            if (vehicleId == null || vehicleId.isEmpty()) {
                Toast.makeText(context, "Error: Vehicle ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

            DatabaseReference userVehicleRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId) // Get the current user's node
                    .child("Vehicles")
                    .child(vehicleList.get(position).getVehicleId()); // Get the specific vehicle ID

            // Remove vehicle from Firebase
            userVehicleRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Vehicle Removed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to Remove Vehicle", Toast.LENGTH_SHORT).show();
                }
            });

        });

        holder.btnRemove.setOnClickListener(v -> {
            String vehicleId = vehicleList.get(position).getVehicleId(); // Get the vehicle ID
            if (vehicleId == null || vehicleId.isEmpty()) {
                Toast.makeText(context, "Error: Vehicle ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

            DatabaseReference userVehicleRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId) // Get the current user's node
                    .child("Vehicles")
                    .child(vehicleList.get(position).getVehicleId()); // Get the specific vehicle ID

            // Remove vehicle from Firebase
            userVehicleRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Vehicle Removed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to Remove Vehicle", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView textVehicleType, textVehicleDetail ;
        Button btnRemoveVehicle;
        ImageView btnRemove;
        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            textVehicleType = itemView.findViewById(R.id.vehicleType);
            textVehicleDetail = itemView.findViewById(R.id.vehicleNumber);
            btnRemoveVehicle = itemView.findViewById(R.id.removeVehicle);
            btnRemove = itemView.findViewById(R.id.imgRemove);
        }
    }
}
