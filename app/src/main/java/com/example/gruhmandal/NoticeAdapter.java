package com.example.gruhmandal;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private Context context;
    private List<NoticeModel> noticeList;
    private DatabaseReference noticeRef;
    private FirebaseUser currentUser;

    public NoticeAdapter(Context context, List<NoticeModel> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
        noticeRef = FirebaseDatabase.getInstance().getReference("notices");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeModel notice = noticeList.get(position);
        holder.title.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());
        holder.agreeCount.setText("Agree: " + notice.getAgreeCount());
        holder.notAgreeCount.setText("Not Agree: " + notice.getNotAgreeCount());
        holder.post_date.setText("Posted on: "+notice.getDate()+" "+notice.getTime());
        String poll = notice.getPoll();

        String uid = notice.getUid();

        Glide.with(context)
                .load(notice.getImageUrl())
                .placeholder(R.drawable.placeholder)
                //.error(R.drawable.notice)// Add a placeholder image in drawable
                .into(holder.image);

        if(currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference voteRef = noticeRef.child(notice.getNoticeId()).child("votes").child(userId);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String uname = snapshot.child("name").getValue(String.class);
                        holder.post_name.setText("Posted By: " + uname);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (poll.equals("Yes")) {
                // Check if the user has already voted
                voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userVote = snapshot.getValue(String.class);
                            if ("agree".equals(userVote)) {
                                holder.btnAgree.setText("Agreed");
                                holder.btnNotAgree.setVisibility(View.GONE);
                            } else if ("notAgree".equals(userVote)) {
                                holder.btnNotAgree.setText("Not Agreed");
                                holder.btnAgree.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }else if(poll.equals("No")){
//                holder.btnNotAgree.setVisibility(View.GONE);
//                holder.btnAgree.setVisibility(View.GONE);
                holder.agree_notagree_layout.setVisibility(View.GONE);
                holder.btn_layout.setVisibility(View.GONE);
            }

            holder.btnAgree.setOnClickListener(v -> {
                updateVote(notice, true);
            });
            holder.btnNotAgree.setOnClickListener(v -> {
                updateVote(notice, false);
            });
        }
    }

    private void updateVote(NoticeModel notice, boolean isAgree) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get logged-in user ID
        DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(notice.getNoticeId());
        DatabaseReference userVoteRef = noticeRef.child("votes").child(userId);


        userVoteRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(context, "You have already voted on this notice!", Toast.LENGTH_SHORT).show();
            } else {
                // User has not voted yet, proceed
                if (isAgree) {
                    noticeRef.child("agreeCount").setValue(notice.getAgreeCount() + 1);
                    userVoteRef.setValue("agree");

                } else {
                    noticeRef.child("notAgreeCount").setValue(notice.getNotAgreeCount() + 1);
                    userVoteRef.setValue("notAgree");
                }
                Toast.makeText(context, "Your vote has been recorded!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, agreeCount, notAgreeCount,post_name,post_date;
        Button btnAgree, btnNotAgree;
        ImageView image;
        LinearLayout btn_layout,agree_notagree_layout;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noticeTitle);
            description = itemView.findViewById(R.id.noticeDescription);
            agreeCount = itemView.findViewById(R.id.txtAgreeCount);
            image = itemView.findViewById(R.id.notice_image);
            notAgreeCount = itemView.findViewById(R.id.txtNotAgreeCount);
            btnAgree = itemView.findViewById(R.id.btnAgree);
            btnNotAgree = itemView.findViewById(R.id.btnNotAgree);
            post_name = itemView.findViewById(R.id.post_name);
            post_date = itemView.findViewById(R.id.post_date);
            btn_layout = itemView.findViewById(R.id.btns_agree_notagree);
            agree_notagree_layout = itemView.findViewById(R.id.agree_notagree_layout);
        }
    }
}


