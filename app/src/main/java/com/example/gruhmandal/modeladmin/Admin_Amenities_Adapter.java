package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
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
import com.example.gruhmandal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

public class Admin_Amenities_Adapter extends RecyclerView.Adapter<Admin_Amenities_Adapter.AmenitiesViewHolder> {

    private Context context;
    private List<Admin_Amenities_Model> AmList;

    public Admin_Amenities_Adapter(Context context, List<Admin_Amenities_Model> amList) {
        this.context = context;
        this.AmList = amList;
    }

    @NonNull
    @Override
    public Admin_Amenities_Adapter.AmenitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amenity, parent, false);
        return new AmenitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_Amenities_Adapter.AmenitiesViewHolder holder, int position) {
    Admin_Amenities_Model amenities = AmList.get(position);
        holder.name.setText(amenities.getName());
        holder.time.setText(amenities.getOtime() +" to " + amenities.getCtime());
        holder.status.setText(amenities.getStatus());
        Glide.with(context).load(amenities.getImageUrl()).into(holder.image);
        holder.btn_remove.setVisibility(View.VISIBLE);

        holder.btn_remove.setOnClickListener(v -> {
            DatabaseReference amRef = FirebaseDatabase.getInstance().getReference("Amenities").child(amenities.getAmenitiesId());
            amRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(context.getApplicationContext(), "Amenities Removed Successfully!",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context.getApplicationContext(), "Failed to Remove Amenities!",Toast.LENGTH_SHORT).show();
                }
            });
        });




    }

    @Override
    public int getItemCount() {
        return AmList.size();
    }

    public class AmenitiesViewHolder extends RecyclerView.ViewHolder {
        private TextView name,time,status;
        private Button btn_remove;
        private ImageView image;
        public AmenitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.amenityName);
            time = itemView.findViewById(R.id.amenityTimings);
            status = itemView.findViewById(R.id.amenityStatus);
            btn_remove = itemView.findViewById(R.id.btnDeactive);
            image = itemView.findViewById(R.id.amenityImage);
        }
    }
}
