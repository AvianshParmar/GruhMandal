package com.example.gruhmandal.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gruhmandal.CloudinaryResponse;
import com.example.gruhmandal.CloudinaryService;
import com.example.gruhmandal.MaintenanceFragment;
import com.example.gruhmandal.NoticeModel;
import com.example.gruhmandal.R;
import com.example.gruhmandal.modeladmin.Admin_Notice_Adapter;
import com.example.gruhmandal.modeladmin.Notice_Model;
import com.example.gruhmandal.modeladmin.View_All_Notice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String CLOUD_NAME = "dmepjjljl";  // Replace with your Cloudinary Cloud Name
    private static final String UPLOAD_PRESET = "ml_default";
    private EditText etTitle, etDescription;
    private ImageView ivNoticeImage;
    private Button btnUploadImage, btnPublishNotice;
    private Uri imageUri;
    private DatabaseReference noticeRef;
    private RecyclerView rvPastNotices;
    private RadioGroup radioGroup;
    //Functions
    private ProgressBar progressBar;
    private Admin_Notice_Adapter noticeAdapter;
    private List<Notice_Model> noticeList;
    private TextView noNoticeText,view_text;
    private String poll=null; // Replace with actual default URL
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice2, container, false);



        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnPublishNotice = view.findViewById(R.id.btnPublishNotice);
        rvPastNotices = view.findViewById(R.id.rvPastNotices);
        ivNoticeImage = view.findViewById(R.id.ivNoticeImage);
        radioGroup = view.findViewById(R.id.radioGroup);
        progressBar = view.findViewById(R.id.loading_screen);
        view_text = view.findViewById(R.id.view_more_txt);

        view_text.setOnClickListener(v -> {
            View_All_Notice targetFragment = new View_All_Notice();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_admin, targetFragment); // Replace with your container ID
            transaction.addToBackStack(null); // Allows going back
            transaction.commit();
        });

        noticeRef = FirebaseDatabase.getInstance().getReference("notices");

        //recyclerView = view.findViewById(R.id.rvPastNotices);
        noNoticeText = view.findViewById(R.id.noNoticeText);
        rvPastNotices.setLayoutManager(new LinearLayoutManager(getContext()));

        noticeList = new ArrayList<>();
        noticeAdapter = new Admin_Notice_Adapter(getContext(), noticeList);
        rvPastNotices.setAdapter(noticeAdapter);

        btnUploadImage.setOnClickListener(v -> openFileChooser());
        btnPublishNotice.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (imageUri != null) {
                int selectedId = radioGroup.getCheckedRadioButtonId(); // Get selected ID
                if (selectedId == -1) {
                    Toast.makeText(getContext(), "Please select an vote option!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                poll = selectedRadioButton.getText().toString();
                uploadImageToCloudinary();
            } else {
                // No image case
                int selectedId = radioGroup.getCheckedRadioButtonId(); // Get selected ID
                if (selectedId == -1) {
                    Toast.makeText(getContext(), "Please select an vote option!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                poll = selectedRadioButton.getText().toString();
                publishNotice("");
            }

        });
        loadNotices();
        return view;
    }
    private void loadNotices() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");
        noticeRef.orderByChild("active").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Notice_Model notice = ds.getValue(Notice_Model.class);
                        if(notice.getSocietyId() != null && notice.getSocietyId().equals(societyId)) {
                            noticeList.add(notice);
                        }
                    }
                    if(noticeList.isEmpty()){
                        noNoticeText.setVisibility(View.VISIBLE);
                    }
                    noticeAdapter.notifyDataSetChanged();
                    //noNoticeText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(ivNoticeImage);
                    ivNoticeImage.setImageURI(imageUri);
                }
            }
    );
    private void publishNotice(String imageUrl) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String noticeId = noticeRef.push().getKey();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Enter title and description", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

            // ✅ If no image is uploaded, set a default image
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = "https://res.cloudinary.com/dmepjjljl/image/upload/v1742294999/notice_ephloz.png";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            String currentTime = timeFormat.format(new Date());

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String societyId = sharedPreferences.getString("societyId", "");
            String uid = sharedPreferences.getString("userId", "");


            Notice_Model notice = new Notice_Model(noticeId, title, description, imageUrl, true,societyId , currentDate, currentTime,uid,poll);

            noticeRef.child(noticeId).setValue(notice).addOnSuccessListener(unused ->
                    Toast.makeText(getContext(), "Notice Published", Toast.LENGTH_SHORT).show());
            etTitle.setText("");
            etDescription.setText("");
            ivNoticeImage.setImageResource(R.drawable.notice);
            progressBar.setVisibility(View.GONE);
    }
    private void uploadImageToCloudinary() {
        //showLoadingScreen();

        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image!", Toast.LENGTH_SHORT).show();
            //hideLoadingScreen();
            return;
        }

        File file = new File(getRealPathFromUri(imageUri)); // Convert URI to file path

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody uploadPreset = RequestBody.create(MediaType.parse("text/plain"), UPLOAD_PRESET);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CloudinaryService service = retrofit.create(CloudinaryService.class);
        Call<CloudinaryResponse> call = service.uploadImage(body, uploadPreset);

        call.enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getSecureUrl();
                    Log.d("Cloudinary", "Upload successful: " + imageUrl);
                    publishNotice(imageUrl); // ✅ Pass the image URL to Firebase
                } else {
                   // hideLoadingScreen();
                    Log.e("Cloudinary", "Upload failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                hideLoadingScreen();
                Log.e("Cloudinary", "Upload failed: " + t.getMessage());
            }
        });
    }
    private String getRealPathFromUri(Uri uri) {
        String result = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}