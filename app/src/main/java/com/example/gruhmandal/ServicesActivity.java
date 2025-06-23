package com.example.gruhmandal;

import static android.graphics.Color.RED;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ServicesActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final String SELECTED_COLOR = "#1e4368"; // Blue color for selected icon
    private final String DEFAULT_COLOR = "#808080";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);

        //Bottom Navigation Bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_services);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_services) {
                    return true; // Already in Service
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(ServicesActivity.this, Dashboard.class));
                    overridePendingTransition(0, 0); // Removes animation
                    finish();// Close Dashboard so it doesn’t stack
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(ServicesActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // Find the Society Maintenance Card
        View maintenanceCard = findViewById(R.id.maintence_card);
        maintenanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, Dashboard.class);
                intent.putExtra("FRAGMENT_NAME", "MaintanceFragment"); // Send fragment name
                startActivity(intent);
            }
        });

        //For add Family Card
        View add_Family_Card = findViewById(R.id.add_family_member);
        add_Family_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
                intent.putExtra("FRAGMENT_NAME", "AddFamilyFragment"); // Send fragment name
                startActivity(intent);
            }
        });

        View Complain_Box = findViewById(R.id.complaint_box);
        Complain_Box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
                intent.putExtra("FRAGMENT_NAME", "ComplaintFragment"); // Send fragment name
                startActivity(intent);
            }
        });

        //For Security Card
        View Security_Fragment = findViewById(R.id.security_card);
        Security_Fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
                intent.putExtra("FRAGMENT_NAME", "SecurityFragment"); // Send fragment name
                startActivity(intent);
            }
        });

        //For Neighbour Card
        View Neighbour_card = findViewById(R.id.neighbour_card);
        Neighbour_card.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "NeighborsFragment"); // Send fragment name
            startActivity(intent);
        });

        //For Notice Card
        View Notice_card = findViewById(R.id.notice_card);
        Notice_card.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "NoticeFragment"); // Send fragment name
            startActivity(intent);
        });

        //For Vehicle Card
        View Vehicle_Card = findViewById(R.id.VehicleCard);
        Vehicle_Card.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "VehicleFragment"); // Send fragment name
            startActivity(intent);
        });

        //For Aminities Card
        View Aminities_Card = findViewById(R.id.AminitiesCard);
        Aminities_Card.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, ServiceDisplayActivity.class);
            intent.putExtra("FRAGMENT_NAME", "AminitiesFragment"); // Send fragment name
            startActivity(intent);
        });
    }
    //Functions

}