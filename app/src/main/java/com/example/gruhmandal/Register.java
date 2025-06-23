package com.example.gruhmandal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gruhmandal.modeladmin.FCMHelper;
import com.example.gruhmandal.modeladmin.Registration_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText reg_name,reg_mobile_no, reg_email, reg_pas, reg_confirm_pass, reg_role;
    TextView loginRedirectText,reg_wing_no,reg_flat_no,reg_society,reg_sid;
    Button reg_btn;
    ImageView togglePassword,toggledPassword1;
    FirebaseDatabase database;
    DatabaseReference reference;

    TextView signup_text;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String token,adminFMC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        reg_name = findViewById(R.id.name_input);
        reg_society = findViewById(R.id.society_input);
        reg_wing_no = findViewById(R.id.wing_input);
        reg_flat_no = findViewById(R.id.flat_input);
        reg_mobile_no = findViewById(R.id.phone_input);
        reg_email = findViewById(R.id.email_phone_input);
        reg_sid = findViewById(R.id.reg_sid);

        reg_pas = findViewById(R.id.password_input);
        reg_confirm_pass = findViewById(R.id.confirm_password_input);

        loginRedirectText = findViewById(R.id.reg_text);
        reg_btn = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        DatabaseReference societiesRef = FirebaseDatabase.getInstance().getReference("Societies");

        reg_society.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Register.this, reg_society);
            Menu menu = popupMenu.getMenu();
            reg_society.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_up, 0);

            societiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    menu.clear();
                    for (DataSnapshot societySnap : snapshot.getChildren()) {
                        String societyId = societySnap.getKey();
                        String societyName = societySnap.child("sname").getValue(String.class);
                        menu.add(societyId + " - " + societyName);
                    }
                    popupMenu.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            popupMenu.setOnMenuItemClickListener(item -> {
                reg_society.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_down, 0);
                String selectedSocietyId = item.getTitle().toString().split(" - ")[0]; // Extract Society ID
                reg_society.setText(item.getTitle());
                reg_sid.setText(selectedSocietyId);// Set selected society
                loadWings(selectedSocietyId);
                return true;
            });
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    reg_society.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_down, 0);
                }
            });
        });

        //Wing Drop Down
        reg_btn.setOnClickListener(v -> registerUser());
//        reg_wing_no.setOnClickListener(v -> {
//            PopupMenu popupMenu = new PopupMenu(this.getApplicationContext(), reg_wing_no);
//            reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential,0, R.drawable.arrow_drop_up,0);
//            for (String wing : wing_no_drop){
//                popupMenu.getMenu().add(wing);
//            }
//            popupMenu.setOnMenuItemClickListener(item -> {
//                reg_wing_no.setText(item.getTitle());
//                String gray = "#595959";
//                reg_wing_no.setTextColor(Color.parseColor(gray));
//                reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential,0,R.drawable.arrow_drop_down,0);
//                return true;
//            });
//            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//                @Override
//                public void onDismiss(PopupMenu menu) {
//                    reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_down, 0);
//                }
//            });
//            popupMenu.show();
//        });

        //Flat Drop Down
//        reg_flat_no.setOnClickListener(v -> {
//            PopupMenu popupMenu = new PopupMenu(this.getApplicationContext(),reg_flat_no);
//            reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat,0,R.drawable.arrow_drop_up,0);
//            for(String flat : flat_no_drop){
//                popupMenu.getMenu().add(flat);
//            }
//            popupMenu.setOnMenuItemClickListener(item -> {
//                reg_flat_no.setText(item.getTitle());
//                String gray = "#595959";
//                reg_flat_no.setTextColor(Color.parseColor(gray));
//                reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat,0,R.drawable.arrow_drop_down,0);
//                return true;
//            });
//            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//                @Override
//                public void onDismiss(PopupMenu menu) {
//                    reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat, 0, R.drawable.arrow_drop_down, 0);
//                }
//            });
//            popupMenu.show();
//        });

        //Show And Hide Password
        togglePassword = findViewById(R.id.iv_toggle_password);
        togglePassword.setOnClickListener(view -> {

            if(reg_pas.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                reg_pas.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.eye);
            }else {
                reg_pas.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.hiding); // Change to hidden icon
            }
            reg_pas.setSelection(reg_pas.getText().length());
        });
        toggledPassword1 = findViewById(R.id.iv_toggle_password1);
        toggledPassword1.setOnClickListener(view -> {
            if(reg_confirm_pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                reg_confirm_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggledPassword1.setImageResource(R.drawable.eye);
            }else {
                reg_confirm_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggledPassword1.setImageResource(R.drawable.hiding); // Change to hidden icon
            }
            reg_confirm_pass.setSelection(reg_confirm_pass.getText().length());
        });

        //Link to Redirect to login Page
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    //Functions
    private void registerUser() {
        String name = reg_name.getText().toString().trim();
        String wing = reg_wing_no.getText().toString().trim();
        String flat = reg_flat_no.getText().toString().trim();
        String mobile = reg_mobile_no.getText().toString().trim();
        String email = reg_email.getText().toString().trim();
        String pass = reg_pas.getText().toString().trim();
        String conf_pass = reg_confirm_pass.getText().toString().trim();
        String reg_soc = reg_society.getText().toString().trim();
        String sid = reg_sid.getText().toString().trim();



        if (name.isEmpty() || wing.isEmpty() || flat.isEmpty() || mobile.isEmpty() || email.isEmpty() || pass.isEmpty() || reg_soc.isEmpty()) {
            Toast.makeText(this, "Fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!pass.equals(conf_pass)){
            Toast.makeText(this, "Password is not matched with Confirm Password!", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.w("FCM", "Fetching FCM registration token failed", task1.getException());
                                return;
                            }
                            // Get new FCM token
                            token = task1.getResult();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                                    .child(userId);
                            userRef.child("fcmToken").setValue(token);
                        });


                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("wingno", wing);
                userData.put("flatno", flat);
                userData.put("mobile", mobile);
                userData.put("email", email);
                userData.put("role", "0");
                userData.put("status", "Pending");
                userData.put("sid",sid);
                //userData.put("fcm",token);


                mDatabase.child(userId).setValue(userData)
                        .addOnSuccessListener(aVoid -> {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        // Fetch user data inside the unique userId node
                                        Registration_Model user = userSnapshot.getValue(Registration_Model.class);
                                        if (user != null && user.getSid() != null && user.getRole() != null) {
                                            // Check if user belongs to same society and has pending status
                                            if (user.getSid().equals(sid) && user.getRole().equals("1")) {
                                                user.setUserId(userSnapshot.getKey()); // Store userId
                                                adminFMC =  user.getFcmToken();
                                                Log.e("FMCTOKEN",adminFMC);
                                                sendNotificationToUser(adminFMC,name,wing,flat);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("societyId", sid);
                            editor.putString("userId", userId);
                            editor.putString("fcm",token);
                            editor.apply();

                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Pending_Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save data" + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "The Email address is already exist!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendNotificationToUser(String userToken, String userName, String wingno, String flatno) {
        FCMHelper.getAccessToken(this, new FCMHelper.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject message = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("title", "New Resident Registration!");
                    notification.put("body", " New " + userName + ", of Your Society has Registered for " + wingno+", "+ flatno + "! Approve it or Reject it ASAP!");
                    //notification.put("icon", "https://res.cloudinary.com/dmepjjljl/image/upload/v1742566441/1000298533_xpm8ti.jpg");

                    message.put("notification", notification);
                    message.put("token", userToken);  // Sending to the specific user token

                    jsonObject.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        "https://fcm.googleapis.com/v1/projects/gruh-mandal/messages:send",
                        jsonObject,
                        response -> Log.d("FCM", "Success: " + response.toString()),
                        error -> Log.e("FCM", "Notification Error: " + error.toString())

                )
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);  // Replace with your token
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
                //Toast.makeText(getApplicationContext(),"Reminder Send Successfully!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FCM", "Error fetching token", e);
            }
        });
    }
    private void loadWings(String societyId) {
        reg_wing_no.setVisibility(View.VISIBLE);
        DatabaseReference wingsRef = FirebaseDatabase.getInstance().getReference("Societies").child(societyId).child("wings");
        reg_wing_no.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Register.this, reg_wing_no);
            Menu menu = popupMenu.getMenu();
            reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_up, 0);
            wingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    menu.clear();
                    for (DataSnapshot wingSnap : snapshot.getChildren()) {
                        menu.add(wingSnap.getKey()); // Add wing name
                    }
                    popupMenu.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            popupMenu.setOnMenuItemClickListener(item -> {
                reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_down, 0);
                String selectedWing = item.getTitle().toString();
                reg_wing_no.setText(selectedWing);
                loadFlats(societyId, selectedWing);
                return true;
            });
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    reg_wing_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.residential, 0, R.drawable.arrow_drop_down, 0);
                }
            });
        });
    }

    private void loadFlats(String societyId, String wing) {
        reg_flat_no.setVisibility(View.VISIBLE);
        DatabaseReference flatsRef = FirebaseDatabase.getInstance().getReference("Societies").child(societyId).child("wings").child(wing).child("flats");
        reg_flat_no.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Register.this, reg_flat_no);
            Menu menu = popupMenu.getMenu();
            reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat, 0, R.drawable.arrow_drop_up, 0);

            flatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    menu.clear();
                    for (DataSnapshot flatSnap : snapshot.getChildren()) {
                        menu.add(flatSnap.getValue(String.class));
                    }
                    popupMenu.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            popupMenu.setOnMenuItemClickListener(item -> {
                reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat, 0, R.drawable.arrow_drop_down, 0);
                String selectedFlat = item.getTitle().toString();
                reg_flat_no.setText(selectedFlat);
                return true;
            });
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    reg_flat_no.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flat, 0, R.drawable.arrow_drop_down, 0);
                }
            });
        });
    }

}
