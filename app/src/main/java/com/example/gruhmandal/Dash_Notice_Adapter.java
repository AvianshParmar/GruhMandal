package com.example.gruhmandal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gruhmandal.R;
import com.example.gruhmandal.NoticeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Dash_Notice_Adapter extends RecyclerView.Adapter<Dash_Notice_Adapter.NoticeViewHolder> {
    private Context context;
    private List<NoticeModel> DashnoticeList;
    private OnNoticeClickListener listener;
    private DatabaseReference noticeRef;
    private FirebaseUser currentUser;

    public interface OnNoticeClickListener {
        void onNoticeClick();
    }
    public Dash_Notice_Adapter(Context context, List<NoticeModel> noticeList, OnNoticeClickListener listener) {
        this.context = context;
        this.DashnoticeList = noticeList;
        this.listener = listener;
        noticeRef = FirebaseDatabase.getInstance().getReference("notices");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashborad_item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeModel notice = DashnoticeList.get(position);
        holder.title.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());

        Glide.with(context)
                .load(notice.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.notice)
                .into(holder.image);

        holder.see_more_btn.setOnClickListener(v -> {
            if(listener != null){
                listener.onNoticeClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return DashnoticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;
        Button see_more_btn;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Dash_noticeTitle);
            description = itemView.findViewById(R.id.Dash_noticeDescription);
            image = itemView.findViewById(R.id.Dash_noticeImage);
            see_more_btn = itemView.findViewById(R.id.see_more_btn);
        }
    }
}
