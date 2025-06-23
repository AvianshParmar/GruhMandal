package com.example.gruhmandal.modeladmin;

import android.content.Context;
import android.graphics.Color;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminBillAdapter extends RecyclerView.Adapter<AdminBillAdapter.ViewHolder> {
    private Context context;
    private List<AdminBillModel> billList;

    public AdminBillAdapter(Context context, List<AdminBillModel> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public AdminBillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBillAdapter.ViewHolder holder, int position) {

        AdminBillModel bill = billList.get(position);
        // Format amount with commas
        //String formattedAmount = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(bill.getAmount());
        String flatno = bill.getFlatNo();
        holder.tvResidentName.setText( flatno+" - "+ bill.getName());
        try {
            int amount = Integer.parseInt(bill.getAmount()); // Convert String to Integer
            DecimalFormat formatter = new DecimalFormat("#,###");
            holder.tvAmount.setText("Amount : ₹"+formatter.format(amount)); // Format properly
        } catch (NumberFormatException e) {
            holder.tvAmount.setText(bill.getAmount()); // Use raw value if parsing fails
        }
        if(bill.getStatus().equals("Pending")) {
            holder.tvStatus.setText("Status: " + bill.getStatus());
        }else{
            String DarkGreen = "#0B7100";
            holder.tvStatus.setTextColor(Color.parseColor(DarkGreen));
            holder.tvStatus.setText("Status: " + bill.getStatus());
        }

        holder.btn_send.setOnClickListener(v -> {
            String userToken = bill.getFcmToken();  // Get User FCM Token
            String userName = bill.getName();
            int amount = Integer.parseInt(bill.getAmount()); // Get User Name
             ;  // Get Bill Amount
            sendNotificationToUser(context,userToken, userName, amount);
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResidentName, tvAmount, tvStatus, btn_send;

        public ViewHolder(View itemView) {
            super(itemView);
            tvResidentName = itemView.findViewById(R.id.tv_resident_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btn_send = itemView.findViewById(R.id.btn_send);
        }
    }
    private void sendNotificationToUser(Context context,String userToken, String userName, int billAmount) {
        FCMHelper.getAccessToken(context.getApplicationContext(), new FCMHelper.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject message = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("title", "Bill Payment Reminder");
                    notification.put("body", "Hello " + userName + ", your bill of ₹" + billAmount + " is still pending. Please pay it soon.");
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

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(jsonObjectRequest);
                //Toast.makeText(context,"Reminder Send Successfully!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("FCM", "Error fetching token", e);
            }
        });
        }
    }

