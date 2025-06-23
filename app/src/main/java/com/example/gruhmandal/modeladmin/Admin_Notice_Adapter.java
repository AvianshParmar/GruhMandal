package com.example.gruhmandal.modeladmin;

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

import com.bumptech.glide.Glide;
import com.example.gruhmandal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Admin_Notice_Adapter extends RecyclerView.Adapter<Admin_Notice_Adapter.NoticeViewHolder>{
    private Context context;
    private List<Notice_Model> noticeList;
    public Admin_Notice_Adapter(Context context, List<Notice_Model> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }
    @NonNull
    @Override
    public Admin_Notice_Adapter.NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_Notice_Adapter.NoticeViewHolder holder, int position) {
        Notice_Model notice = noticeList.get(position);
        holder.noticeTitle.setText(notice.getTitle());
        holder.noticeDescription.setText(notice.getDescription());
        holder.noticeDateTime.setText(notice.getDate());

        // Load Image (If ImageURL Exists, Otherwise Show Default)
        if (notice.getImageUrl() != null && !notice.getImageUrl().isEmpty()) {
            Glide.with(context).load(notice.getImageUrl()).into(holder.noticeImage);
        } else {
            holder.noticeImage.setImageResource(R.drawable.notice); // Default Image
        }
        // Hide "Deactivate" button if notice is already inactive
        if (notice.isActive()) {
            holder.btnDeactivate.setVisibility(View.VISIBLE);
        } else {
            holder.btnDeactivate.setVisibility(View.GONE);
        }

        // Deactivate Notice
        holder.btnDeactivate.setOnClickListener(v -> deactivateNotice(notice.getNoticeId()));

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView noticeTitle, noticeDescription, noticeDateTime;
        ImageView noticeImage;
        Button btnDeactivate;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            noticeTitle = itemView.findViewById(R.id.noticeTitle);
            noticeDescription = itemView.findViewById(R.id.noticeDescription);
            noticeDateTime = itemView.findViewById(R.id.post_date);
            noticeImage = itemView.findViewById(R.id.notice_image);
            btnDeactivate = itemView.findViewById(R.id.btnDeactive);
        }
    }
    private void deactivateNotice(String noticeId) {
        DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeId);
        noticeRef.child("active").setValue(false);
        Toast.makeText(context.getApplicationContext(), "Notice Removed Successfully!",Toast.LENGTH_SHORT).show();
    }
}
