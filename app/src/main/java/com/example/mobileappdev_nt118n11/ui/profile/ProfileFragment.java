package com.example.mobileappdev_nt118n11.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileappdev_nt118n11.Model.User;
import com.example.mobileappdev_nt118n11.R;
import com.example.mobileappdev_nt118n11.SignIn;
import com.example.mobileappdev_nt118n11.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String key;
    private FirebaseDatabase database;
    private TextView tvPhone;
    private EditText edName, edEmail,edAddress;
    private Button btnUpdate;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        init(root);


        key = Phone.Key_Phone;
        database = FirebaseDatabase.getInstance();
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child(key).getValue(User.class);
                tvPhone.setText(key);
                edName.setText(user.getName());
                edEmail.setText(user.getGmail());
                edAddress.setText(user.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child("User").child(key).child("Name").setValue(edName.getText().toString());
                database.getReference().child("User").child(key).child("Address").setValue(edAddress.getText().toString());
                database.getReference().child("User").child(key).child("Gmail").setValue(edEmail.getText().toString());

            }
        });
        return root;
    }

    public void init(View root) {
        edName = root.findViewById(R.id.tv_profile_name);
        tvPhone = root.findViewById(R.id.tv_profile_phone);
        edEmail = root.findViewById(R.id.tv_profile_email);
        edAddress = root.findViewById(R.id.tv_profile_address);
        btnUpdate =root.findViewById(R.id.btn_profile_Edit);
    }
}