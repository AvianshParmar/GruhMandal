package com.example.gruhmandal;

import static android.widget.Toast.LENGTH_SHORT;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.health.connect.datatypes.ExerciseCompletionGoal;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button log_btn;
    private EditText password_input, email_input;
    ImageView togglePassword ;
    TextView reg_link,forgot_pass_txt;
    private RelativeLayout loadingScreen;
    private View app_icon;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userid, status = "Pending",token;
    private String role = "0";
    private ProgressBar progressBar;

    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User already logged in, go to Dashboard
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Start Session
        FirebaseApp.initializeApp(this);

        loadingScreen = findViewById(R.id.loading_screen);
        app_icon = findViewById(R.id.app_icon);
        reg_link = findViewById(R.id.register_link);
        forgot_pass_txt = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.progressBar3);

        mAuth = FirebaseAuth.getInstance();


       // mDatabase = FirebaseDatabase.getInstance().getReference("users");
//        if(mAuth.getCurrentUser().getUid().isEmpty()){
//            return;
//        }else{
//           // String userId = mAuth.getCurrentUser().getUid();
//            Intent intent = new Intent(MainActivity.this, AdminDashboard.class);
//            startActivity(intent);
//        }





        email_input = findViewById(R.id.email_phone_input);
        password_input = findViewById(R.id.password_input);
        log_btn =findViewById(R.id.login_button);

        log_btn.setOnClickListener(v -> {
            String email = email_input.getText().toString().trim();
            String password = password_input.getText().toString().trim();

            if (isValidEmail(email)) {
                if(isValidPassword(password)) {
                    loginUser(email, password);
                }else {
                    Toast.makeText(this, "Password must be longer than 6 digits!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            }
        });

        //Loading Screen
        showLoadingScreen();
        // Simulate background task (e.g., fetching data) using Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide loading screen after task completion
                hideLoadingScreen();
            }
        }, 2000);// Simulate a 2-second task

        //Hide and Show Password
        password_input = findViewById(R.id.password_input);
        togglePassword = findViewById(R.id.iv_toggle_password);

        togglePassword.setOnClickListener(view -> {

            if(password_input.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.eye);
            }else {
                password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.hiding); // Change to hidden icon
            }
            password_input.setSelection(password_input.getText().length());
        });

        //Going to Register Page
        reg_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
        //Going to Reset Password Page
        forgot_pass_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    // Functions
    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful!", LENGTH_SHORT).show();

                        mDatabase = FirebaseDatabase.getInstance().getReference("users");
                        String userId = mAuth.getCurrentUser().getUid();

                        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(task1 -> {
                                                if (!task1.isSuccessful()) {
                                                    Log.w("FCM", "Fetching FCM registration token failed", task1.getException());
                                                    progressBar.setVisibility(View.GONE);
                                                    return;
                                                }
                                                // Get new FCM token
                                                token = task1.getResult();

                                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                                                        .child(userId);
                                                userRef.child("fcmToken").setValue(token);
                                            });
                                    String sid = snapshot.child("sid").getValue(String.class);
                                    String userId = snapshot.getKey();

                                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("societyId", sid);
                                    editor.putString("userId", userId);
                                    editor.putString("fcm", token);
                                    editor.apply();


                                    status = snapshot.child("status").getValue(String.class);
                                    role = snapshot.child("role").getValue(String.class);
                                    if (status.equals("Approve")) {
                                        if (role.equals("1")) {
                                            Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else if (role.equals("0")) {
                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), Pending_Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                        }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{
                        String errorMessage = "Login failed";
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "User not found";
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Invalid email or password";
                        } else {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

                    }
                });
    }
    //For Login
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6; // Minimum 6 characters
    }

    // Show the loading screen
    private void showLoadingScreen() {
        loadingScreen.setVisibility(View.VISIBLE);
        app_icon.setVisibility(View.GONE);
        log_btn.setVisibility(View.GONE);// Hide main content during loading
    }

    // Hide the loading screen
    private void hideLoadingScreen() {
        loadingScreen.setVisibility(View.GONE);
        app_icon.setVisibility(View.VISIBLE);
        log_btn.setVisibility(View.VISIBLE);// Show main content after loading
    }

}