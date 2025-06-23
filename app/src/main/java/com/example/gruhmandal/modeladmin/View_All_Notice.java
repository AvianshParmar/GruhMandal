package com.example.gruhmandal.modeladmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gruhmandal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class View_All_Notice extends Fragment {
    private DatabaseReference noticeRef;
    private RecyclerView rvPastNotices;
    private Admin_Notice_Adapter noticeAdapter;
    private List<Notice_Model> noticeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view__all__notice, container, false);
        // Inflate the layout for this fragment
        rvPastNotices = view.findViewById(R.id.rvPastNotices);

        noticeRef = FirebaseDatabase.getInstance().getReference("notices");

        //recyclerView = view.findViewById(R.id.rvPastNotices);
        //noNoticeText = view.findViewById(R.id.noNoticeText);
        rvPastNotices.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeList = new ArrayList<>();
        noticeAdapter = new Admin_Notice_Adapter(getContext(), noticeList);
        rvPastNotices.setAdapter(noticeAdapter);
        loadNotices();

        return view;
    }
    //Functions
    private void loadNotices() {
        noticeRef.orderByChild("active").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Notice_Model notice = ds.getValue(Notice_Model.class);
                        noticeList.add(notice);
                    }
                    noticeAdapter.notifyDataSetChanged();
                    //noNoticeText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}