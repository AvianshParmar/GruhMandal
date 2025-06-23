package com.example.gruhmandal;

import static android.app.ProgressDialog.show;
import static android.graphics.Color.*;
import static android.widget.Toast.LENGTH_LONG;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<Bill> bills;
    private Context context;
    private OnBillClickListener listener;
    private String userPhoneNumber = "9999999999";
    private MaintenanceFragment.PaymentCallback paymentCallback;
    private Activity activity;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();

    public interface OnBillClickListener {
        void onPayClicked(Bill bill);
    }
    public BillAdapter(List<Bill> bills, Context context, MaintenanceFragment.PaymentCallback callback) {
        this.bills = bills;
        this.context = context;
        this.paymentCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.tvAmount.setText("Amount: ₹" + bill.getAmount());
        holder.tvDueDate.setText("Due: " + bill.getDueDate());
        holder.tvStatus.setText(bill.getStatus());

        if (bill.getStatus().equals("paid")) { // 🚨 Crash if getStatus() is null
            holder.tvStatus.setText("Paid");
        }
       // String status = bill.getStatus() != null ? bill.getStatus() : "unpaid"; // Default value


        if (bill.getStatus().equals("Paid")) {
            String DarkGreen = "#0B7100";
            holder.tvStatus.setTextColor(Color.parseColor(DarkGreen));
            holder.btnPay.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(Color.RED);
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnPay.setOnClickListener(v -> {
                if (paymentCallback != null) {
                    paymentCallback.onPaymentInitiated(bill.getBillId(), bill.getAmount());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDueDate, tvStatus;
        Button btnPay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnPay = itemView.findViewById(R.id.btnPay);
        }

    }
    //Function

    private void initiatePayment(Bill bill) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_Y35b5dTI5umxbI");

        Activity activity = (Activity) context; // Get Activity reference
        checkout.setImage(R.drawable.logo);
        try {

            JSONObject options = new JSONObject();
            options.put("name", "Society Maintenance");
            options.put("description", "Bill Payment");
            options.put("currency", "INR");
            options.put("amount", bill.getAmount() * 100); // Amount in paise
            options.put("prefill.contact",userPhoneNumber);
//            Toast.makeText(context, mob, Toast.LENGTH_LONG).show();
            options.put("notes.billId", bill.getBillId());

            checkout.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), LENGTH_LONG).show();
        }
    }



}

