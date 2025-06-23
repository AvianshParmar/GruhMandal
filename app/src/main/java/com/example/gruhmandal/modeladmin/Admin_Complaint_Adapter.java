package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
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
import com.example.gruhmandal.ComplaintModel;
import com.example.gruhmandal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.v1.StructuredQuery;

import org.w3c.dom.Text;

import java.util.List;

public class Admin_Complaint_Adapter extends RecyclerView.Adapter<Admin_Complaint_Adapter.ViewHolder>{
    private Context context;
    private List<ComplaintModel> complaintModelList;

    public Admin_Complaint_Adapter(Context context, List<ComplaintModel> complaintModelList) {
        this.context = context;
        this.complaintModelList = complaintModelList;
    }

    @NonNull
    @Override
    public Admin_Complaint_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_complain,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_Complaint_Adapter.ViewHolder holder, int position) {
    ComplaintModel complaintModel = complaintModelList.get(position);
        holder.tvTitle.setText(complaintModel.getSubject());
        holder.tvDescription.setText(complaintModel.getDetails());
            if (complaintModel.getImageUrl() != null && !complaintModel.getImageUrl().isEmpty()) {
                Glide.with(context).load(complaintModel.getImageUrl()).into(holder.img);
            } else {
                holder.img.setImageResource(R.drawable.complaint); // Default Image
            }
            holder.tvStatus.setText("Status: " + complaintModel.getStatus());
            if(complaintModel.getStatus().equals("Pending")) {
                holder.tvStatus.setTextColor(Color.RED);

            }else{
                String green = "#4CAF50";
                holder.tvStatus.setTextColor(Color.parseColor(green));
                holder.btn_solve.setVisibility(View.GONE);
            }
        holder.tv_time.setText("Posted on: " + complaintModel.getDate());
        holder.tvName.setText(complaintModel.getName() +" - "+ complaintModel.getFlatno());
        holder.tvWing.setText(complaintModel.getWingno());



        holder.btn_solve.setOnClickListener(v -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("complaints").child(complaintModel.getCid());
            dbRef.child("status").setValue("Solved");
            Toast.makeText(context.getApplicationContext(), "Notice Removed Successfully!",Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return complaintModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWing,tvTitle,tvName,tvDescription,tvStatus,tv_time;
        private ImageView img;
        private Button btn_solve;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWing = itemView.findViewById(R.id.tv_wing_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvName = itemView.findViewById(R.id.tv_resident_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvStatus = itemView.findViewById(R.id.tv_status);
            img = itemView.findViewById(R.id.iv_complain_icon);
            btn_solve = itemView.findViewById(R.id.btn_solved);
            tv_time = itemView.findViewById(R.id.tv_post_time);
        }
    }
}
