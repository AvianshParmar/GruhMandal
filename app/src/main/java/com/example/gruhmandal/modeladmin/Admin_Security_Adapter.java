package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.InfiniteAnimationPolicy;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruhmandal.R;
import com.example.gruhmandal.SecurityAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Admin_Security_Adapter extends RecyclerView.Adapter<Admin_Security_Adapter.ViewHolder>{
    private Context context;
    private List<Admin_Security_Model> sList;

    public Admin_Security_Adapter(Context context, List<Admin_Security_Model> sList) {
        this.context = context;
        this.sList = sList;
    }

    @NonNull
    @Override
    public Admin_Security_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_security_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_Security_Adapter.ViewHolder holder, int position) {
        Admin_Security_Model secuirty = sList.get(position);

        holder.tvName.setText("Name: "+secuirty.getName());
        holder.tvContact.setText("Contact: "+secuirty.getContact());
        holder.tvPlace.setText("Place: "+secuirty.getPostion());

        holder.btnCallNow.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + secuirty.getContact()));
            context.startActivity(callIntent);
        });

        holder.btn_remove.setOnClickListener(v -> {
            DatabaseReference SecurityRef = FirebaseDatabase.getInstance().getReference("security_guards")
                    .child(secuirty.getSecurityid());

            SecurityRef.removeValue().addOnCompleteListener(task ->{
               if(task.isSuccessful()){
                   Toast.makeText(context, "Security-Guard Removed Successfully!", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(context, "Failed to Remove Security-Guard!", Toast.LENGTH_SHORT).show();
               }
            });
        });

    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContact, tvPlace;
        LinearLayout btnCallNow,btn_remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_guard_name);
            tvContact = itemView.findViewById(R.id.tv_guard_contact);
            tvPlace = itemView.findViewById(R.id.tv_guard_place);
            btnCallNow = itemView.findViewById(R.id.btn_call_now);
            btn_remove =itemView.findViewById(R.id.btn_remove);
        }
    }
}
