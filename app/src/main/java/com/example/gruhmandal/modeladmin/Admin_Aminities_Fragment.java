package com.example.gruhmandal.modeladmin;

import android.app.Activity;
import android.app.TimePickerDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gruhmandal.CloudinaryResponse;
import com.example.gruhmandal.CloudinaryService;
import com.example.gruhmandal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Admin_Aminities_Fragment extends Fragment {
    private EditText amName;
    private TextView noText,amOpenTime,amCloseTime;
    private ImageView amimg;
    private Button btn_upload,btn_submit;
    private Uri imageUri;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private String selectedTime;
    private Admin_Amenities_Adapter adapter;
    private List<Admin_Amenities_Model> AmList;
    private static final String CLOUD_NAME = "dmepjjljl";  // Replace with your Cloudinary Cloud Name
    private static final String UPLOAD_PRESET = "ml_default";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_admin__aminities_,container,false);

        amName =view.findViewById(R.id.etAmenitiesName);
        amOpenTime =view.findViewById(R.id.etOpenTime);
        amCloseTime =view.findViewById(R.id.etCloseTime);
        amimg =view.findViewById(R.id.ivAmenitiesImage);
        btn_upload = view.findViewById(R.id.btnUploadImage);
        btn_submit = view.findViewById(R.id.btnPublishAmenities);
        progressBar = view.findViewById(R.id.loading_screen);
        recyclerView = view.findViewById(R.id.rvAllAmenities);
        noText = view.findViewById(R.id.noText);



        amOpenTime.setOnClickListener(v ->{
            showOpenTimePickerDialog();
        });
        amCloseTime.setOnClickListener(v -> {
            showCloseTimePickerDialog();
        });

        btn_upload.setOnClickListener(v -> {
            openFileChooser();
        });
        btn_submit.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if(imageUri != null){
                uploadImageToCloudinary();
            }else {
                publishAmenities("");
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AmList = new ArrayList<>();
        adapter = new Admin_Amenities_Adapter(getContext(),AmList);
        recyclerView.setAdapter(adapter);

        loadAmenities();

        return view;
    }
    private void loadAmenities(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference amRef = FirebaseDatabase.getInstance().getReference("Amenities");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");
        amRef.orderByChild("sid").equalTo(societyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AmList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Admin_Amenities_Model amenities = ds.getValue(Admin_Amenities_Model.class);
                        AmList.add(amenities);
                    }
                    if(AmList.isEmpty()){
                        noText.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    //noText.setVisibility(View.GONE);
                }else {
                    noText.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
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
                    Glide.with(this).load(imageUri).into(amimg);
                    amimg.setImageURI(imageUri);
                }
            }
    );
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
                    publishAmenities(imageUrl); // ✅ Pass the image URL to Firebase
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
    private void publishAmenities(String imageURL){

        DatabaseReference amRef = FirebaseDatabase.getInstance().getReference("Amenities");
        String name = amName.getText().toString().trim();
        String otime = amOpenTime.getText().toString().trim();
        String close = amCloseTime.getText().toString().trim();
        String AmenitiesId = amRef.push().getKey();
        String status = "Open";


        if (name.isEmpty() || otime.isEmpty() || close.isEmpty() || AmenitiesId.isEmpty()) {
            if(name.isEmpty()) {
                Toast.makeText(getContext(), "Enter Amenities Name!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (otime.isEmpty()) {
                Toast.makeText(getContext(), "Enter Amenities Opening Time!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (close.isEmpty()) {
                Toast.makeText(getContext(), "Enter Amenities Closing Time!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        }

        if (imageURL.isEmpty() || imageURL == null) {
            imageURL = "https://res.cloudinary.com/dmepjjljl/image/upload/v1742440949/amenities_jbjget.png";
        }

        // ✅ If no image is uploaded, set a default image


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String societyId = sharedPreferences.getString("societyId", "");
        String uid = sharedPreferences.getString("userId", "");


        Admin_Amenities_Model amenities = new Admin_Amenities_Model(AmenitiesId, name, otime,close, imageURL, status,societyId , uid,currentDate);

        amRef.child(AmenitiesId).setValue(amenities).addOnSuccessListener(unused ->
                Toast.makeText(getContext(), "Amenities Published", Toast.LENGTH_SHORT).show());

        amName.setText(null);
        amCloseTime.setText(null);
        amOpenTime.setText(null);
        amimg.setImageResource(R.drawable.amenities);
        progressBar.setVisibility(View.GONE);
    }

    // Inside your Fragment or Activity
    private void showOpenTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean isPM = calendar.get(Calendar.AM_PM) == Calendar.PM;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(), // Use "this" if inside an Activity
                (view, selectedHour, selectedMinute) -> {
                    // Convert 24-hour format to 12-hour format
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int hourIn12Format = selectedHour % 12;
                    if (hourIn12Format == 0) {
                        hourIn12Format = 12; // Convert 0 AM/PM to 12 AM/PM
                    }

                    // Format the selected time
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                    amOpenTime.setText(selectedTime); // Set time in EditText or TextView
                },
                hour, minute, false // 'false' ensures 12-hour format
        );
        timePickerDialog.show();
    }
    private void showCloseTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean isPM = calendar.get(Calendar.AM_PM) == Calendar.PM;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(), // Use "this" if inside an Activity
                (view, selectedHour, selectedMinute) -> {
                    // Convert 24-hour format to 12-hour format
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int hourIn12Format = selectedHour % 12;
                    if (hourIn12Format == 0) {
                        hourIn12Format = 12; // Convert 0 AM/PM to 12 AM/PM
                    }

                    // Format the selected time
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                    amCloseTime.setText(selectedTime); // Set time in EditText or TextView
                },
                hour, minute, false // 'false' ensures 12-hour format
        );
        timePickerDialog.show();
    }


}