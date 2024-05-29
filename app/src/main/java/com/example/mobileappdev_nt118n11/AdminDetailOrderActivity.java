package com.example.mobileappdev_nt118n11;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdev_nt118n11.Model.Order;
import com.example.mobileappdev_nt118n11.ui.AdminOrderFoodItemsAdapter;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
public class AdminDetailOrderActivity extends AppCompatActivity  {
    private TextView orderId, customerName, customerPhone, customerAddress, orderTime, totalPrice, orderStatus;
    private RecyclerView orderItemsRecyclerView;
    private Button markAsDelivered;
    private String orderIdValue;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_detail_order);

        // Initialize Firebase reference
        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        // Find views
        orderId = findViewById(R.id.order_id);
        customerName = findViewById(R.id.customer_name);
        customerPhone = findViewById(R.id.customer_phone);
        customerAddress = findViewById(R.id.customer_address);
        orderTime = findViewById(R.id.order_time);
        totalPrice = findViewById(R.id.total_price);
        orderStatus = findViewById(R.id.order_status);
        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        markAsDelivered = findViewById(R.id.mark_as_delivered);

        // Get orderId from Intent
        orderIdValue = getIntent().getStringExtra("idKey");

        // Set RecyclerView layout manager
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load order details (dummy data for now)
        loadOrderDetails();

        // Set click listener for mark as delivered button
        markAsDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markOrderAsDelivered();
            }
        });
    }

    private void loadOrderDetails() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Request").child(orderIdValue);
        orderRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // Parse order details
                        String orderIdText = "ID Đơn hàng: " + dataSnapshot.getKey();
                        String customerNameText = "Tên khách hàng: " + dataSnapshot.child("name").getValue(String.class);
                        String customerPhoneText = "Điện thoại: " + dataSnapshot.child("phone").getValue(String.class);
                        String customerAddressText = "Địa chỉ: " + dataSnapshot.child("address").getValue(String.class);
                        String orderTimeText = "Thời gian đặt hàng: " + dataSnapshot.child("orderDate").getValue(String.class);
                        double totalPriceValue = Double.parseDouble(dataSnapshot.child("total").getValue(String.class));
                        String totalPriceText = "Tổng tiền: $" + totalPriceValue;
                        int orderStatusValue = Integer.parseInt(dataSnapshot.child("status").getValue(String.class));
                        String orderStatusText = orderStatusValue == 0 ? "Trạng thái: Chờ giao" : "Trạng thái: Đã giao";

                        // Set values to TextViews
                        orderId.setText(orderIdText);
                        customerName.setText(customerNameText);
                        customerPhone.setText(customerPhoneText);
                        customerAddress.setText(customerAddressText);
                        orderTime.setText(orderTimeText);
                        totalPrice.setText(totalPriceText);
                        orderStatus.setText(orderStatusText);

                        // Load order items
                        List<Order> orderItems = new ArrayList<>();
                        for (DataSnapshot foodSnapshot : dataSnapshot.child("foods").getChildren()) {
                            String itemName = foodSnapshot.child("productName").getValue(String.class);
                            String itemImage = foodSnapshot.child("image").getValue(String.class);
                            String itemPrice = foodSnapshot.child("price").getValue(String.class);
                            String itemQuantity = foodSnapshot.child("quantity").getValue(String.class);
                            orderItems.add(new Order(Phone.getKey_Phone(), itemName, itemQuantity, itemPrice, itemImage));
                        }

                        // Set adapter for RecyclerView
                        AdminOrderFoodItemsAdapter adapter = new AdminOrderFoodItemsAdapter(AdminDetailOrderActivity.this, orderItems);
                        orderItemsRecyclerView.setAdapter(adapter);

                        // Show mark as delivered button if order status is pending
                        if (orderStatusValue == 0) {
                            markAsDelivered.setVisibility(View.VISIBLE);
                        } else {
                            markAsDelivered.setVisibility(View.GONE);
                        }
                    } else {
                        // Handle case where order does not exist
                        // For example, show a toast or log an error
                    }
                } else {
                    // Handle unsuccessful task
                    // For example, show a toast or log an error
                }
            }
        });
    }


    private void markOrderAsDelivered() {
        // This method should update the order status in Firebase to delivered.
        orderRef.child(orderIdValue).child("status").setValue(1);
        orderStatus.setText("Trạng thái: Đã giao");
        markAsDelivered.setVisibility(View.GONE);
    }
}
