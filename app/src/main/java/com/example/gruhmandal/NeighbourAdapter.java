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

public class NeighbourAdapter extends RecyclerView.Adapter<NeighbourAdapter.ViewHolder> {
    private List<NeighbourModel> neighbourModelList;
    private Context context;

    public NeighbourAdapter(Context context, List<NeighbourModel> neighbourModelList) {
        this.context =context;
        this.neighbourModelList = neighbourModelList;
    }

    @NonNull
    @Override
    public NeighbourAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.neighbours_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NeighbourAdapter.ViewHolder holder, int position) {
        NeighbourModel neighbor = neighbourModelList.get(position);
        holder.neName.setText("Name: " + neighbor.getName());
        holder.neContact.setText("Contact: +91 " + neighbor.getMobile());
        holder.neWing_no.setText("Wing: " + neighbor.getWingno());
        holder.neFlatno.setText("Flat: " + neighbor.getFlatno());

        holder.btncall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + neighbor.getMobile()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return neighbourModelList.size();
    }
    public void updateList(List<NeighbourModel> newList) {
        this.neighbourModelList = newList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView neName,neContact,neWing_no, neFlatno;

        LinearLayout btncall;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            neName = itemView.findViewById(R.id.tv_neigh_name);
            neContact = itemView.findViewById(R.id.tv_neigh_contact);
            neWing_no = itemView.findViewById(R.id.tv_neigh_wingno);
            neFlatno = itemView.findViewById(R.id.tv_neigh_flatno);
            btncall = itemView.findViewById(R.id.btn_call);
        }
    }
}
