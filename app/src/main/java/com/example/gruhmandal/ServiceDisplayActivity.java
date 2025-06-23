package com.example.gruhmandal;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class ServiceDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_display);

        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");

        if (fragmentName != null) {
            Fragment selectedFragment = null;

            // Load the corresponding fragment
            switch (fragmentName) {
                case "VehicleFragment":
                    selectedFragment = new VehicleFragment();
                    break;
                case "AddFamilyFragment":
                    selectedFragment = new FamilyFragment();
                    break;
                case "ComplaintFragment":
                    selectedFragment = new ComplainFragment();
                    break;
                case "SecurityFragment":
                    selectedFragment = new Security();
                    break;
                case "NoticeFragment":
                    selectedFragment = new NoticeFragment();
                    break;
                case "NeighborsFragment":
                    selectedFragment = new NeighboursFragment();
                    break;
                case "AminitiesFragment":
                    selectedFragment = new AmenitiesFragment();
                    break;
                default:
                    Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity if fragment not found
            }

            // Replace the fragment in the container
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

        }
    }
}