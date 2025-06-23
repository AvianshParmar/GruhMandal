package com.example.gruhmandal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.ViewHolder> {
    private Context context;
    private List<AmenityModel> amenitiesList;

    public AmenityAdapter(Context context, List<AmenityModel> amenitiesList) {
        this.context = context;
        this.amenitiesList = amenitiesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amenity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AmenityModel amenity = amenitiesList.get(position);
        holder.amenityName.setText(amenity.getName());
        String openTime = amenity.getOtime();
        String closeTime = amenity.getCtime();

        holder.amenityTimings.setText(openTime + " TO " + closeTime);
        holder.amenityStatus.setText(amenity.getStatus());
        Glide.with(context)
                .load(amenity.getImageUrl())
                .placeholder(R.drawable.placeholder)
                //error(R.drawable.caring_society)// Add a placeholder image in drawable
                .into(holder.amenityImage);
    }

    @Override
    public int getItemCount() {
        return amenitiesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amenityName, amenityTimings, amenityStatus;
        ImageView amenityImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amenityName = itemView.findViewById(R.id.amenityName);
            amenityTimings = itemView.findViewById(R.id.amenityTimings);
            amenityStatus = itemView.findViewById(R.id.amenityStatus);
            amenityImage = itemView.findViewById(R.id.amenityImage);
        }
    }

}
