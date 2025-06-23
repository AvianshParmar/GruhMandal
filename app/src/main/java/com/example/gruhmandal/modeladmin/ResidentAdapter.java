package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruhmandal.R;

import java.util.ArrayList;
import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ViewHolder> {

    private Context context;
    private List<ResidentModel> residentList;

    public ResidentAdapter(List<ResidentModel> residentList, Context context) {
        this.residentList = residentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resident, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentAdapter.ViewHolder holder, int position) {
        ResidentModel user = residentList.get(position);
            holder.txtName.setText(user.getName() + " - " + user.getFlatno());
            //holder.txtEmail.setText(user.getEmail());
            holder.txtMobile.setText("Contact: +91 " + user.getMobile());
            holder.txtFamily.setText("Family Members: " + user.getFamilyCount());
            holder.txtVehicles.setText("Vehicles: "+user.getVehicleCount());

    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtMobile, txtFamily, txtVehicles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvResidentName);
            //txtEmail = itemView.findViewById(R.id.txtEmail);
            txtMobile = itemView.findViewById(R.id.tvResidentContact);
            txtFamily = itemView.findViewById(R.id.tvResidentmember);
            txtVehicles = itemView.findViewById(R.id.tvResidentVehicls);
        }
    }
}
