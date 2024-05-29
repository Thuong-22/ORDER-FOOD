package com.example.mobileappdev_nt118n11.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.mobileappdev_nt118n11.FoodDetailActivity;
import com.example.mobileappdev_nt118n11.AdminDetailOrderActivity;
import com.example.mobileappdev_nt118n11.Model.Request;
import com.example.mobileappdev_nt118n11.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Request> requestList;
    private Context context;

    public OrderAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.oder_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.tvOrderId.setText(request.getRequestId());
        holder.tvCustomer.setText(request.getName());
        holder.tvPhone.setText(request.getPhone());
        holder.tvOrderTime.setText(request.getOrderDate());
        holder.tvTotalAmount.setText(request.getTotal());
        holder.tvDeliveryAddress.setText(request.getAddress());
        holder.tvStatus.setText((request.getStatus().equals("0") ? "In Progress" : "Completed"));
//        holder.tvNumberOfItems.setText("Number of Items: " + request.getNumberOfItems());
        if (request.getStatus().equals("0")) {
            holder.btnDelivered.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelivered.setVisibility(View.GONE);
        }

        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminDetailOrderActivity.class);
                intent.putExtra("idKey", request.getRequestId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                        .child("Request").child(request.getRequestId()).child("status");
                orderRef.setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update local UI (optional): change status text or hide button
                            holder.tvStatus.setText("Completed");
                            holder.btnDelivered.setVisibility(View.GONE);
                        } else {
                            // Handle failed update
                            // You can show an error toast or log the error
                        }
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId, tvCustomer, tvPhone, tvOrderTime, tvTotalAmount, tvPaymentMethod, tvDeliveryAddress, tvStatus, tvNumberOfItems;
        Button btnViewDetails, btnDelivered;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
//            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
//            tvNumberOfItems = itemView.findViewById(R.id.tvNumberOfItems);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnDelivered = itemView.findViewById(R.id.btnDelivered);
        }


    }
}
