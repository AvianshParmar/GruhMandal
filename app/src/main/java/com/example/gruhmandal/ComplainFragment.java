package com.example.gruhmandal;

import android.app.Activity;
import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ComplainFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String CLOUD_NAME = "dmepjjljl";  // Replace with your Cloudinary Cloud Name
    private static final String UPLOAD_PRESET = "ml_default";
    private ProgressBar progressBar,progressBar2;

    private EditText etSubject, etDetails;
    private Button btnChooseImage, btnSubmitComplaint;
    private ImageView complaintImage;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private ComplaintAdapter adapter;
    private List<ComplaintModel> complaintList;
    private Bitmap bitmap;
    private FrameLayout main_screen;
    private RelativeLayout load_screen;
    private TextView noText;
    private String SocietyId;
    private DatabaseReference databaseReference,complaintsRef;
    //private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String PhotoURl;

    public ComplainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complain, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            complaintsRef = FirebaseDatabase.getInstance().getReference("complaints");
        }
        //db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SocietyId = prefs.getString("societyId", "");
        //storageReference = FirebaseStorage.getInstance().getReference("complaints");

        // Initialize UI Elements
        etSubject = view.findViewById(R.id.et_complaint_subject);
        etDetails = view.findViewById(R.id.et_complaint_details);
        btnChooseImage = view.findViewById(R.id.btn_choose_image);
        btnSubmitComplaint = view.findViewById(R.id.btn_submit_complaint);
        complaintImage = view.findViewById(R.id.complaint_image);
        recyclerView = view.findViewById(R.id.recycler_view_complain);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar2 = view.findViewById(R.id.progressBar2);
        main_screen = view.findViewById(R.id.main_screen);
        noText = view.findViewById(R.id.noText);

       load_screen = view.findViewById(R.id.load_screen);

        // Set RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter(getContext(), complaintList);
        recyclerView.setAdapter(adapter);

        // Load Previous Complaints
       // loadComplaints();
        //hideLoadingScreen();
        // Select Image Button Click
        btnChooseImage.setOnClickListener(v -> openFileChooser());

        // Submit Complaint
        btnSubmitComplaint.setOnClickListener(v -> uploadImageToCloudinary());

        loadComplaints();
        return view;
    }
    //Functions
    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES);
        } else {  // Android 12 and below
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{permission}, 100);
        } else {
            openFileChooser();  // Permission granted, open image picker
        }
    }
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(complaintImage);
                    complaintImage.setVisibility(View.VISIBLE);
                    complaintImage.setImageURI(imageUri);
                }
            }
    );
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }
    private String getRealPathFromUri(Uri uri) {
        String result = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
    private void uploadImageToCloudinary() {
        showLoadingScreen();
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image!", Toast.LENGTH_SHORT).show();
            hideLoadingScreen();
            return;
        }

        File file = new File(getRealPathFromUri(imageUri)); // Convert URI to file path

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody uploadPreset = RequestBody.create(MediaType.parse("text/plain"), "ml_default"); // Change to your upload preset

        // ✅ Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/") // Cloudinary Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CloudinaryService service = retrofit.create(CloudinaryService.class);
        Call<CloudinaryResponse> call = service.uploadImage(body, uploadPreset);

        call.enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getSecureUrl(); // ✅ Get the image URL
                    Log.d("Cloudinary", "Upload successful: " + imageUrl);
                    saveComplaintToFirebase(imageUrl); // ✅ Save only the image URL to Firebase
                } else {
                    hideLoadingScreen();
                    Log.e("Cloudinary", "Upload failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                hideLoadingScreen();
                Log.e("Cloudinary", "Upload failed: " + t.getMessage());
            }
        });
    }

    private void saveComplaintToFirebase(String imageUrl) {

        String userId = mAuth.getCurrentUser().getUid();
        String subject = etSubject.getText().toString().trim();
        String details = etDetails.getText().toString().trim();

        if (subject.isEmpty() || details.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            hideLoadingScreen();
            return;
        }

        String complaintId = databaseReference.child(userId).push().getKey();
        String status = "Pending";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        String currentDatetime = dateFormat.format(new Date());
        Date dateObj = null;
        try {
            dateObj = dateFormat.parse(currentDatetime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long timestamp = dateObj.getTime();


        ComplaintModel complaint = new ComplaintModel(complaintId ,userId, subject, details, status, imageUrl,SocietyId,currentDatetime,timestamp);

        assert complaintId != null;
        databaseReference.child(complaintId).setValue(complaint)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hideLoadingScreen();
                        etSubject.setText(null);
                        etDetails.setText(null);
                        complaintImage.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Complaint submitted!", Toast.LENGTH_SHORT).show();
                    } else {
                        hideLoadingScreen();
                        Toast.makeText(getContext(), "Failed to submit complaint!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadComplaints() {
        //Toast.makeText(getContext(), ""+complaintsRef, Toast.LENGTH_SHORT).show();
        if (complaintsRef == null) return;

        progressBar2.setVisibility(View.VISIBLE);
        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ComplaintModel complaint = dataSnapshot.getValue(ComplaintModel.class);
                    if (complaint != null) {
                        if(complaint.getSid().equals(SocietyId)) {
                            complaintList.add(complaint);
                        }
                    }
                }
                Collections.sort(complaintList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                if(complaintList.isEmpty()){
                    noText.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load complaints", Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
            }
        });
    }
    private void showLoadingScreen() {
        load_screen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        main_screen.setVisibility(View.GONE);// Hide main content during loading
    }

    // Hide the loading screen
    private void hideLoadingScreen() {
        load_screen.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        main_screen.setVisibility(View.VISIBLE);
        // Show main content after loading
    }
}
