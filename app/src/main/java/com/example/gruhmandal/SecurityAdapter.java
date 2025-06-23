package com.example.gruhmandal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder>{
    private List<SecurityModel> guardList;
    private Context context;

    public SecurityAdapter(Context context, List<SecurityModel> guardList) {
        this.context = context;
        this.guardList = guardList;
    }

    @NonNull
    @Override
    public SecurityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.security_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityAdapter.ViewHolder holder, int position) {
        SecurityModel guard = guardList.get(position);

        holder.tvName.setText("Name: " + guard.getName());
        holder.tvContact.append(guard.getContact());
        holder.tvPlace.setText("Place: " + guard.getPostion());

        holder.btnCallNow.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + guard.getContact()));
            context.startActivity(callIntent);
        });
    }

    @Override
    public int getItemCount() {
        return guardList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContact, tvPlace;
        LinearLayout btnCallNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_guard_name);
            tvContact = itemView.findViewById(R.id.tv_guard_contact);
            tvPlace = itemView.findViewById(R.id.tv_guard_place);
            btnCallNow = itemView.findViewById(R.id.btn_call_now);
        }
    }
}
