package com.example.gruhmandal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Pending_Activity extends AppCompatActivity {
    private TextView title_txt,main_txt;
    private DatabaseReference mDatabase;
    private RelativeLayout load_screen;
    private LinearLayout main_screen;
    private FirebaseAuth mAuth;
    private Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending);

        load_screen = findViewById(R.id.loading_screen);
        main_screen = findViewById(R.id.main_screen_liner);
        title_txt = findViewById(R.id.title_society);
        main_txt = findViewById(R.id.main_text);
        logout_btn = findViewById(R.id.btn_Logout);

        loadusername();
        logout_btn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
    //Functions
    private void loadusername(){
        show();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("mobile").getValue(String.class);
                    String flatNo = snapshot.child("flatno").getValue(String.class);
                    String wingNo = snapshot.child("wingno").getValue(String.class);
                    String society_id = snapshot.child("sid").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    if (status.equals("Pending")) {
                        if (society_id.isEmpty()) {
                            return;
                        }
                        DatabaseReference society_db = FirebaseDatabase.getInstance().getReference("Societies");
                        society_db.child(society_id).child("sname").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot societySnapshot) {
                                if (societySnapshot.exists()) {
                                    String societyName = societySnapshot.getValue(String.class);
                                    title_txt.setText(name + ", Welcome to " + societyName); // Display in TextView
                                    hide();
                                } else {
                                    title_txt.setText("Welcome User");
                                    hide();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //flat_show.setText(flatNo + " , " + wingNo);
                        //hide();
                    } else if (status.equals("Reject")) {
                        title_txt.setText("Request Rejected "+ name + ",");
                        main_txt.setText("Dear "+name +",Your account has been rejected by Society Management kindly contact Society Managment for more information!");
                        hide();
                    }else if (status.equals("Approved")){
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //hide();
                Toast.makeText(Pending_Activity.this, "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
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


