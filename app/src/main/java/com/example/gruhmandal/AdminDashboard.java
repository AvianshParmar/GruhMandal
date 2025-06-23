package com.example.gruhmandal;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gruhmandal.admin.DisplayAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;
import java.util.TimerTask;


public class AdminDashboard extends AppCompatActivity {
    private Button logoutbtn;
    private TextView snametxt;
    private RelativeLayout load_screen;
    private FrameLayout main_screen;
    private FirebaseAuth mAuth;
    private ViewPager2 viewPager;
    private int[] images = {R.drawable.caring_society, R.drawable.image_3, R.drawable.image_3};
    private Handler handler = new Handler();
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard2);

        mAuth = FirebaseAuth.getInstance();

        snametxt = findViewById(R.id.nametextview);
        load_screen = findViewById(R.id.loading_screen);
        main_screen = findViewById(R.id.fragment_container);
        logoutbtn = findViewById(R.id.btnLogout1);

        //Logout button Funtion
        logoutbtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
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

        //Load Society Name
        loadusername();


        //For Bill card
        LinearLayout BillCard = findViewById(R.id.maintenancecard);
        BillCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "BillFragment");
            startActivity(intent);
        });
        //For Resident Card
        LinearLayout ResidentCard = findViewById(R.id.residentcard);
        ResidentCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "ResidentFragment");
            startActivity(intent);
        });
        //For Resident Registration Request
        LinearLayout Resident_Registration = findViewById(R.id.admin_approve_card);
        Resident_Registration.setOnClickListener(v ->{
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "ResidentRegistrationFragment");
            startActivity(intent);
        });
        //For Notice card
        LinearLayout Notice_card = findViewById(R.id.add_noice_card);
        Notice_card.setOnClickListener( v->{
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "NoticeFragment");
            startActivity(intent);
        });
        //For Security card
        LinearLayout Security_card = findViewById(R.id.securitycard);
        Security_card.setOnClickListener( v->{
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "SecurityFragment");
            startActivity(intent);
        });
        //For Amenities card
        LinearLayout Amenities_card = findViewById(R.id.amenities_card);
        Amenities_card.setOnClickListener( v->{
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "AmenitiesFragment");
            startActivity(intent);
        });
        //For Complain card
        LinearLayout Complain_card = findViewById(R.id.comlaincard);
        Complain_card.setOnClickListener( v->{
            Intent intent = new Intent(AdminDashboard.this, DisplayAdmin.class);
            intent.putExtra("FRAGMENT_NAME", "ComplainFragment");
            startActivity(intent);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                 new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

    }
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
    //Functions
    // Declare the launcher at the top of your Activity/Fragment:

    private void loadusername() {
        show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference societyRef = FirebaseDatabase.getInstance().getReference("Societies");
        String adminId = auth.getCurrentUser().getUid();

        adminRef.child(adminId).child("sid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String societyId = snapshot.getValue(String.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("societyId", societyId);
                    //editor.putString("userId", adminId);
                    editor.apply();

                    // Fetch Society Name using Society ID
                    societyRef.child(societyId).child("sname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot societySnapshot) {
                            if (societySnapshot.exists()) {
                                String societyName = societySnapshot.getValue(String.class);
                                snametxt.setText(societyName); // Display in TextView
                            } else {
                                snametxt.setText("Society not found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                hide();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hide();
                Toast.makeText(AdminDashboard.this, "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void show() {
        load_screen.setVisibility(View.VISIBLE);
        main_screen.setVisibility(View.GONE);
    }

    private void hide() {
        load_screen.setVisibility(View.GONE);
        main_screen.setVisibility(View.VISIBLE);
    }
}
