package com.example.mobileappdev_nt118n11.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileappdev_nt118n11.Model.Request;
import com.example.mobileappdev_nt118n11.R;
import com.example.mobileappdev_nt118n11.ui.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PaidFragment extends Fragment {

    private RecyclerView rcvPaidOrders;
    private OrderAdapter orderAdapter;
    private List<Request> requestList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_paid, container, false);
        rcvPaidOrders = root.findViewById(R.id.rcv_paid_orders);

        rcvPaidOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(requestList, getContext());
        rcvPaidOrders.setAdapter(orderAdapter);

        loadPaidOrders();

        return root;
    }

    private void loadPaidOrders() {
        FirebaseDatabase.getInstance().getReference("Request")
                .orderByChild("status").equalTo("1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Request request = snapshot.getValue(Request.class);
                            request.setRequestId(snapshot.getKey());
                            Log.d("D", "Request ID: " + snapshot.getKey() + " " + request.getRequestId());
                            requestList.add(request);
                        }
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}