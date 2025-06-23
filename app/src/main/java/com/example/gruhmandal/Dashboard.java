package com.example.gruhmandal;


import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import static java.security.AccessController.getContext;

import com.example.gruhmandal.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.wallet.PaymentData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity implements PaymentResultListener, MaintenanceFragment.PaymentCallback {

    TextView nametxt,flat_show, see_more_btn,See_all_text ;
    Button logout_btn;
    ImageView profile_img,notice_img;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,notice_data,noticeRef;
    LinearLayout maintenanceCard, viewCard;
    private String billId,phone,SocietyId;
    private double amount;
    private RelativeLayout load_screen;
    private FrameLayout main_screen;
    private RecyclerView recyclerView;
    private Dash_Notice_Adapter adapter;
    private List<NoticeModel> Dash_noticeList;

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private int[] images = {R.drawable.caring_society, R.drawable.image_3, R.drawable.image_2};
    private Handler handler = new Handler();
    private Runnable sliderRunnable;
    @Override
    public void onPaymentInitiated(String billId, double amount) {
        this.billId = billId;
        this.amount = amount;
        initiatePayment();
    }
    public String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        //Checking the Role of User
        FirebaseApp.initializeApp(this);

        //FirebaseUser user = auth.getCurrentUser();

       // checkuserstate();
        //String uid = mAuth.getCurrentUser().getUid();

        //For Society Maintainece Card
        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");
        if (fragmentName != null) {
            Fragment selectedFragment = null;

            // Load the corresponding fragment
            switch (fragmentName) {
                case "MaintanceFragment":
                    selectedFragment = new MaintenanceFragment();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        }

        // Set Adapter without creating a separate class
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return ImageFragment.newInstance(images[position]);
            }

            @Override
            public int getItemCount() {
                return images.length;
            }
        });

        // Auto-slide logic
        sliderRunnable = () -> {
            if (viewPager.getCurrentItem() < images.length - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                viewPager.setCurrentItem(0);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(sliderRunnable);
            }
        }, 4000, 4000); // Change image every 3 seconds

        //Start Session


        nametxt = findViewById(R.id.nametextview);

        load_screen = findViewById(R.id.loading_screen);
        main_screen = findViewById(R.id.fragment_container);
        flat_show = findViewById(R.id.flat_show);
        See_all_text = findViewById(R.id.textView7);
        //checkuser();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this, "User not signed in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Pending_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
            loadusername();
        }
        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true; // Already in Dashboard
                } else if (itemId == R.id.nav_services) {
                    startActivity(new Intent(Dashboard.this, ServicesActivity.class));
                    overridePendingTransition(0, 0); // Removes animation
                    // Close Dashboard so it doesn’t stack
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Dashboard.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });



        //User icon Profile
        profile_img = findViewById(R.id.imageView);
        profile_img.setOnClickListener(v -> {
           startActivity(new Intent(Dashboard.this, ProfileActivity.class));
        });

        //For Society Maintainence Card
        maintenanceCard = findViewById(R.id.maintenancecard);

        LinearLayout maintenanceCard = findViewById(R.id.maintenancecard);
        maintenanceCard.setOnClickListener(v -> {
            // Replace current fragment with MaintenanceFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MaintenanceFragment())
                    .addToBackStack(null) // Optional: Add to back stack
                    .commit();
        });

        recyclerView = findViewById(R.id.recycler_view_notice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Dash_noticeList = new ArrayList<>();
        adapter = new Dash_Notice_Adapter(this, Dash_noticeList, new Dash_Notice_Adapter.OnNoticeClickListener() {
            @Override
            public void onNoticeClick() {
                Intent intent = new Intent(Dashboard.this, ServiceDisplayActivity.class);
                intent.putExtra("FRAGMENT_NAME", "NoticeFragment"); // Send fragment name
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        //For View All Card
        viewCard = findViewById(R.id.view_all_card);

        LinearLayout viewCard = findViewById(R.id.view_all_card);
        viewCard.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, ServicesActivity.class));
        });

        //For Family Members
        LinearLayout familycard = findViewById(R.id.add_family_member);
        familycard.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "AddFamilyFragment"); // Send fragment name
            startActivity(intent);
        });

        //For Complain Box Card
        LinearLayout Complain_box = findViewById(R.id.complaint_box);
        Complain_box.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "ComplaintFragment"); // Send fragment name
            startActivity(intent);
        });

        //For Notice Fragment Card
        See_all_text.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "NoticeFragment"); // Send fragment name
            startActivity(intent);
        });

        ScrollView Notice_fragment = findViewById(R.id.Scrollv1);
        Notice_fragment.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "NoticeFragment"); // Send fragment name
            startActivity(intent);
        });

        notice_data = FirebaseDatabase.getInstance().getReference("notices");
        notice_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dash_noticeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoticeModel notice = dataSnapshot.getValue(NoticeModel.class);
                    if (notice != null) {
                        Dash_noticeList.add(notice);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch notices", error.toException());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        }
        //Functions
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 101) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied! You won't receive notifications.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private void initiatePayment() {
            Checkout checkout = new Checkout();
            checkout.setKeyID("rzp_test_Y35b5dTI5umxbI");
            try {
                JSONObject options = new JSONObject();
                options.put("name", "Society Maintenance");
                options.put("description", "Bill Payment");
                options.put("currency", "INR");
                options.put("amount", amount * 100);// Convert to paise
                options.put("prefill.contact",phone);
                checkout.open(this, options);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        public void onPaymentSuccess(String razorpayPaymentID) {

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String paymentId = FirebaseDatabase.getInstance().getReference("payments").push().getKey();

            String paymentDateTime = getCurrentDateTime();
            DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payments").child(paymentId);

            SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SocietyId = prefs.getString("societyId", "");

            HashMap<String, Object> paymentData = new HashMap<>();
            paymentData.put("userId", userId);
            paymentData.put("billId", billId);
            paymentData.put("amount", amount);
            paymentData.put("paymentDate", paymentDateTime);
            paymentData.put("method", "UPI");
            paymentData.put("transactionId", razorpayPaymentID);
            paymentData.put("sid", SocietyId);

            Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentRef.setValue(paymentData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateBillStatus(userId, billId);
                }
            });
        }

        private void loadusername(){
        show();
            mDatabase = FirebaseDatabase.getInstance().getReference("users");
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        phone = snapshot.child("mobile").getValue(String.class);
                        String flatNo = snapshot.child("flatno").getValue(String.class);
                        String wingNo = snapshot.child("wingno").getValue(String.class);
                        String society_id = snapshot.child("sid").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        if (role.equals("0")) {
                            if (status.equals("Approve")) {
                                nametxt.setText(name);
                                flat_show.setText(flatNo + " , " + wingNo);
                                hide();
                            } else if (status.equals("Pending")) {
                                Intent intent = new Intent(getApplicationContext(), Pending_Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else if (role.equals("1")){
                            Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hide();
                    Toast.makeText(Dashboard.this, "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

//        private void loadnotice(NoticeModel notice, boolean isAgree){
//            notice_data = FirebaseDatabase.getInstance().getReference("notices");
//            DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(notice.getNoticeId());
//
//        }

    private void updateBillStatus(String userId, String billId) {
        DatabaseReference billRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("bills")
                .child(billId)
                .child("status");

        billRef.setValue("Paid");
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
    }

    private void show(){
        load_screen.setVisibility(View.VISIBLE);
        main_screen.setVisibility(View.GONE);
    }
    private void hide(){
        load_screen.setVisibility(View.GONE);
        main_screen.setVisibility(View.VISIBLE);
    }

}

