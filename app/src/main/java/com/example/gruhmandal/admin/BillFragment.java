package com.example.gruhmandal.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gruhmandal.R;
import com.example.gruhmandal.modeladmin.AdminBillAdapter;
import com.example.gruhmandal.modeladmin.AdminBillModel;
import com.example.gruhmandal.modeladmin.FCMHelper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BillFragment extends Fragment {
    private EditText etAmount;
    private TextView tvDueDate,etBillDate,no_bill_text1;
    private Button btnGenerateBill;
    private DatabaseReference usersRef;
    private String SocietyId,userId,tokenn;
    private RecyclerView rvBillStatus;
    private AdminBillAdapter billStatusAdapter;
    private List<AdminBillModel> billList;
    private ProgressBar progressBar;
    private String selectedDueDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        etAmount = view.findViewById(R.id.st_bill_amount);
        tvDueDate = view.findViewById(R.id.st_Due);
        btnGenerateBill = view.findViewById(R.id.st_generate_bill);
        rvBillStatus = view.findViewById(R.id.recycler_view_society_status);
        progressBar = view.findViewById(R.id.progressBar);
        no_bill_text1 = view.findViewById(R.id.no_bill_text);

        etBillDate = view.findViewById(R.id.st_fetch_Due);

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                etAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString().replaceAll(",", "");
                    if (!originalString.isEmpty()) {
                        long longVal = Long.parseLong(originalString);
                        String formattedString = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(longVal);
                        etAmount.setText(formattedString);
                        etAmount.setSelection(formattedString.length());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                etAmount.addTextChangedListener(this);
            }
        });

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Fetch admin's society ID from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SocietyId = prefs.getString("societyId", "");
        userId = prefs.getString("userId", "");
        tokenn = "fp2hLjbcS1iLPEX0V6crUc:APA91bFJsKbdeDEtSLxt4C_2bEdE_tBpp27XK8SqpEImeu-T4T6bUNDZ3gU7WREI-CMTuAHpYo95g4h9rO2FbsKQxuJz5njYlObWmqt6QdO0O0jn3DDjijk";

        rvBillStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        billList = new ArrayList<>();
        billStatusAdapter = new AdminBillAdapter(getContext(),billList);
        rvBillStatus.setAdapter(billStatusAdapter);

        // Date Picker Dialog
        etBillDate.setOnClickListener(v -> showDatePicker());

        // Fetch Bills on Date Selection
        etBillDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker();
        });


        tvDueDate.setOnClickListener(v -> showDatePickerDialog());
        btnGenerateBill.setOnClickListener(v -> generateBillsForAllUsers());
        //fetchBillStatus();
       // sendNotificationToUsers(userId, "20000");
       // sendNotificationToUsers(tokenn,"Avinash","amount");


        return view;
    }
    // Function to Show Date Picker Dialog


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    selectedDueDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    tvDueDate.setText(selectedDueDate);
                }, year, month, day);
        datePickerDialog.show();


    }
    // Function to Generate Bills for All Users in Society
    private void generateBillsForAllUsers() {
        String amount = etAmount.getText().toString().trim();


        if (amount.isEmpty() || selectedDueDate == null) {
            Toast.makeText(getContext(), "Enter amount and select due date", Toast.LENGTH_SHORT).show();
            return;
        }
        String amountStr = etAmount.getText().toString().replaceAll(",", ""); // Remove commas
        int amountInt = Integer.parseInt(amountStr); // Convert to integer
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String name = userSnapshot.child("name").getValue(String.class);
                    String userSocietyId = userSnapshot.child("sid").getValue(String.class);

                    //Toast.makeText(getContext(), ""+userSocietyId, Toast.LENGTH_SHORT).show();

                    if (userSocietyId != null && userSocietyId.equals(SocietyId)) {
                        String billId = usersRef.child(userId).child("bills").push().getKey();
                        if (billId != null) {
                            Map<String, Object> billData = new HashMap<>();
                            billData.put("amount", amountInt);
                            billData.put("month", getCurrentMonth());
                            billData.put("status", "Pending");
                            billData.put("dueDate", selectedDueDate);

                            usersRef.child(userId).child("bills").child(billId).setValue(billData);
                            sendNotificationToUser(userId,name, amount);
                            tvDueDate.setText(null);
                            etAmount.setText(null);
                            //sendNotificationToUsers(fcmToken,"Name" ,amount);
                        }
                    }
                }
                Toast.makeText(getContext(), "Bills Generated Successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendNotificationToUser(String userId,String name, String amount) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        List<String> fcmToken = new ArrayList<>();
        userRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //fcmToken = snapshot.getValue(String.class);
                fcmToken.add(snapshot.getValue(String.class));
                if (fcmToken != null) {

                    sendNotificationsToAllUsers(fcmToken, name ,amount);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error fetching user token: " + error.getMessage());
            }
        });
    }
    private void sendNotificationsToAllUsers(List<String> tokens, String title, String amount) {
        for (String token : tokens) {
            sendNotificationToUser(getContext(), token, title, amount);
        }
    }
    private void sendNotificationToUser(Context context,String userToken, String title, String amount) {
        FCMHelper.getAccessToken(getContext(), new FCMHelper.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                JSONObject json = new JSONObject();
                try {
                    //RequestQueue queue = Volley.newRequestQueue(getContext());
                    //String url = "https://fcm.googleapis.com/v1/projects/gruh-mandal/messages:send";
                    JSONObject message = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("title", "Hello " + title+"!");
                    notification.put("body", "Your bill of ₹" + amount + " is Generated for " +selectedDueDate+ ". Please pay before due date!.");

                    message.put("notification", notification);
                    message.put("token", userToken);  // ✅ Send to one user at a time

                    json.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //String dynamicToken = FCMHelper.getAccessToken(context);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "https://fcm.googleapis.com/v1/projects/gruh-mandal/messages:send",
                        json,
                        response -> Log.d("FCM", "Notification Sent: " + response.toString()),
                        error -> Log.e("FCM", "Notification Error: " + error.toString())
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);  // Replace with your token
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FCM", "Error fetching token", e);
            }
        });

    }

    // Function to Get Current Month
    private String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    etBillDate.setText(selectedDate);
                    fetchBillStatus(selectedDate); // Fetch bill status based on selected date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void fetchBillStatus(String selectedDueDate) {
        progressBar.setVisibility(View.VISIBLE);
        no_bill_text1.setVisibility(View.GONE);
        billList.clear();
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Get user ID
                        String userSid = userSnapshot.child("sid").getValue(String.class); // Get user's society ID

                                if (userSid != null && userSid.equals(SocietyId)) { // Check if user belongs to the same society
                                    String name = userSnapshot.child("name").getValue(String.class);
                                    String wing = userSnapshot.child("wing").getValue(String.class);
                                    String flatNo = userSnapshot.child("flatno").getValue(String.class);
                                    String fcmToken = userSnapshot.child("fcmToken").getValue(String.class);
                                    DataSnapshot billsSnapshot = userSnapshot.child("bills");
                                    for (DataSnapshot bill : billsSnapshot.getChildren()) { // Loop through each bill

                                        String billdueDate = bill.child("dueDate").getValue(String.class);
                                        if(billdueDate != null && billdueDate.equals(selectedDueDate)){
                                            Integer billAmountInt = bill.child("amount").getValue(Integer.class);
                                            String billAmount = (billAmountInt != null) ? String.valueOf(billAmountInt) : "0";
                                            String billStatus = bill.child("status").getValue(String.class);
                                            AdminBillModel residentBill = new AdminBillModel(name, wing, flatNo, billAmount, billStatus,fcmToken);
                                            billList.add(residentBill);
                                        }
                                    }
                                }
                            }

                        if(billList.isEmpty()){
                            no_bill_text1.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }else {
                            billStatusAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error fetching bills!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


}