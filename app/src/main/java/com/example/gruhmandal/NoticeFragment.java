package com.example.gruhmandal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoticeAdapter noticeAdapter;
    private List<NoticeModel> noticeList;
    private DatabaseReference noticeRef;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notice, container, false);

        recyclerView = root.findViewById(R.id.rvNotice);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = root.findViewById(R.id.progressBar);

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(getContext(), noticeList);
        recyclerView.setAdapter(noticeAdapter);

        noticeRef = FirebaseDatabase.getInstance().getReference("notices");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");

        noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                noticeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    NoticeModel notice = dataSnapshot.getValue(NoticeModel.class);
                    if (notice != null) {
                        if(notice.isActive()) {
                            if (notice.getSocietyId().equals(societyId)) {
                                noticeList.add(notice);
                            }
                        }
                    }
                }
                Collections.sort(noticeList, (n1, n2) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    try {
                        Date date1 = sdf.parse(n1.getDate()); // Convert String to Date
                        Date date2 = sdf.parse(n2.getDate());
                        return date2.compareTo(date1); // Latest First
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
                noticeAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch notices", error.toException());
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });


        return root;
    }
}