package com.example.gruhmandal;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText Wing,Flat,Email,mobile;
    private TextView uname;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RelativeLayout load_screen;
    private LinearLayout main_screen;
    private Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        load_screen = findViewById(R.id.loading_screen);
        main_screen = findViewById(R.id.linearLayout);
        uname = findViewById(R.id.tvProfileName);
        Wing = findViewById(R.id.etWingNo);
        Flat = findViewById(R.id.etFlatNo);
        Email = findViewById(R.id.etEmail1);
        mobile = findViewById(R.id.etMobileNo);
        logout_btn = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Bottom Navigation Bar
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    return true; // Already in Service
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, Dashboard.class));
                    overridePendingTransition(0, 0); // Removes animation
                    finish();// Close Dashboard so it doesn’t stack
                    return true;
                } else if (itemId == R.id.nav_services) {
                    startActivity(new Intent(ProfileActivity.this, ServicesActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //load User Data!
        loaduserdata();

        //Logout Button Function
        logout_btn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    //Function
    private void loaduserdata(){
        show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String userid = mAuth.getCurrentUser().getUid();
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    String wingno = snapshot.child("wingno").getValue(String.class);
                    String flatno = snapshot.child("flatno").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileno = snapshot.child("mobile").getValue(String.class);
                    uname.setText(name);
                    Wing.setText(wingno);
                    Flat.setText(flatno);
                    Email.setText(email);
                    mobile.setText("+91 "+mobileno);
                    hide();
                    Log.e("Data", "No bills found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "No bills found!");
            }
        });
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