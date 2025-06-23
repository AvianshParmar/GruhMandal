package com.example.gruhmandal.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.gruhmandal.AmenitiesFragment;
import com.example.gruhmandal.ComplainFragment;
import com.example.gruhmandal.FamilyFragment;
import com.example.gruhmandal.NeighboursFragment;
import com.example.gruhmandal.R;
import com.example.gruhmandal.Security;
import com.example.gruhmandal.VehicleFragment;
import com.example.gruhmandal.modeladmin.Admin_Aminities_Fragment;

public class DisplayAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_admin);

        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");

        if (fragmentName != null) {
            Fragment selectedFragment = null;

            // Load the corresponding fragment
            switch (fragmentName) {
                case "ResidentFragment":
                    selectedFragment = new ResidentFragment();
                    break;
                case "BillFragment":
                    selectedFragment = new BillFragment();
                    break;
                case "ResidentRegistrationFragment":
                    selectedFragment = new Resident_Registration();
                    break;
                case "NoticeFragment":
                    selectedFragment = new NoticeFragment();
                    break;
                case "SecurityFragment":
                    selectedFragment = new Admin_Security_Fragment();
                    break;
                case "AmenitiesFragment":
                    selectedFragment = new Admin_Aminities_Fragment();
                    break;
                case "ComplainFragment":
                    selectedFragment = new Admin_Complain_Fragment();
                    break;
                default:
                    Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity if fragment not found
            }

            // Replace the fragment in the container
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_admin, selectedFragment)
                        .commit();
            }
        }
    }
}