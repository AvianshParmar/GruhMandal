package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gruhmandal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration_Adapter extends RecyclerView.Adapter<Registration_Adapter.ViewHolder> {
    private Context context;
    private List<Registration_Model> userList;
    private DatabaseReference usersRef;
    private String token,name,wing,flat;

    public Registration_Adapter(Context context, List<Registration_Model> userList) {
        this.context = context;
        this.userList = userList;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registration, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Registration_Adapter.ViewHolder holder, int position) {
        Registration_Model user = userList.get(position);
        holder.txtWing.setText(user.getWingno());
        holder.name.setText(user.getName() +" - "+ user.getFlatno());
        holder.email.setText("Email : "+user.getEmail());
        holder.contact.setText("Contact : "+user.getMobile());
        token = user.getFcmToken();
        name = user.getName();
        wing= user.getWingno();
        flat= user.getFlatno();

        holder.btnApprove.setOnClickListener(v -> {
            approveUser(user.getUserId(), token,name,wing,flat,position);
        });
        holder.btnReject.setOnClickListener(v -> {
            rejectUser(user.getUserId(),token,name,wing,flat, position);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, contact, txtWing, txtFlat,email;
        Button btnApprove,btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWing = itemView.findViewById(R.id.tvResidentWing);
            name = itemView.findViewById(R.id.tvResidentName);
            email = itemView.findViewById(R.id.tvResidentemail);
            contact = itemView.findViewById(R.id.tvResidentContact);
            btnApprove = itemView.findViewById(R.id.btnApprov);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
    private void approveUser(String userId,String FMCtoken,String name,String wing,String flat, int position) {
        usersRef.child(userId).child("status").setValue("Approve")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Resident Approved!", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    notifyItemRemoved(position);
                    sendNotificationToUser(context,FMCtoken,name,wing,flat);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Approval Failed!", Toast.LENGTH_SHORT).show());
    }
    private void rejectUser(String userId,String FMCtoken,String name,String wing,String flat, int position) {
        usersRef.child(userId).child("status").setValue("Reject")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Resident Rejected!", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    notifyItemRemoved(position);
                    sendNotificationToUserReject(context,FMCtoken,name,wing,flat);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Rejection Failed!", Toast.LENGTH_SHORT).show());
    }

    private void sendNotificationToUser(Context context, String userToken, String userName, String wingno, String flatno) {
        FCMHelper.getAccessToken(context, new FCMHelper.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject message = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("title", "Your Registration is Approved! ");
                    notification.put("body", " Welcome " + userName + ", to Society, Your " + wingno+", "+ flatno + " is Approved by Society!");
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

                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                requestQueue.add(jsonObjectRequest);
                //Toast.makeText(getApplicationContext(),"Reminder Send Successfully!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FCM", "Error fetching token", e);
            }
        });
    }

    private void sendNotificationToUserReject(Context context, String userToken, String userName, String wingno, String flatno) {
        FCMHelper.getAccessToken(context, new FCMHelper.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject message = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("title", "Your Registration is Approved! ");
                    notification.put("body", " Welcome " + userName + ", to Society, Your " + wingno+", "+ flatno + " is Approved by Society!");
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

                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                requestQueue.add(jsonObjectRequest);
                //Toast.makeText(getApplicationContext(),"Reminder Send Successfully!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FCM", "Error fetching token", e);
            }
        });
    }
}
