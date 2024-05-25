package com.example.mobileappdev_nt118n11.ui.favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileappdev_nt118n11.Database.Database;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.R;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FavouriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private List<Food> favouriteList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference favouriteRef;
    private Database localDatabase;
    private String userPhone;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);

        // Khởi tạo RecyclerView
        recyclerView = root.findViewById(R.id.rcv_favourite_favouritelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        favouriteList = new ArrayList<>();
        favouriteAdapter = new FavouriteAdapter(favouriteList, getContext());
        recyclerView.setAdapter(favouriteAdapter);

        // Khởi tạo Firebase và SQLite Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        favouriteRef = firebaseDatabase.getReference("Favourite").child(Phone.getKey_Phone());
        localDatabase = new Database(getContext());

        // Lấy danh sách món ăn yêu thích từ Firebase và SQLite Database
        loadFavourites();

        return root;
    }

    private void loadFavourites() {
        // Lấy dữ liệu từ Firebase
        favouriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favouriteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food food = snapshot.getValue(Food.class);
                    food.setId(snapshot.getKey());
                    if (food != null) {
                        favouriteList.add(food);
                    }
                }
                // Lấy dữ liệu từ SQLite Database và kết hợp
                List<String> localFavourites = localDatabase.getAllFavourites(Phone.getKey_Phone());
                for (String foodId : localFavourites) {
                    // Kiểm tra nếu món ăn đã có trong danh sách thì bỏ qua
                    boolean exists = false;
                    for (Food food : favouriteList) {
                        if (food != null && food.getId() != null && food.getId().equals(foodId)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        // Lấy thông tin chi tiết của món ăn từ Firebase
                        firebaseDatabase.getReference("Food").child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Food food = snapshot.getValue(Food.class);
                                if (food != null) {
                                    favouriteList.add(food);
                                    // Sau khi thêm một món ăn mới, cập nhật danh sách và đảm bảo không có mục trùng lặp
//                                    deduplicateFavourites();
                                    favouriteAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý lỗi nếu có
                            }
                        });
                    }
                }
                // Cập nhật danh sách và đảm bảo không có mục trùng lặp
                deduplicateFavourites();
                favouriteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    // Phương pháp để loại bỏ các mục trùng lặp trong danh sách yêu thích
    private void deduplicateFavourites() {
        HashSet<String> seenIds = new HashSet<>();
        List<Food> uniqueList = new ArrayList<>();
        for (Food food : favouriteList) {
            if (food != null && food.getId() != null && !seenIds.contains(food.getId())) {
                uniqueList.add(food);
                seenIds.add(food.getId());
            }
        }
        favouriteList.clear();
        favouriteList.addAll(uniqueList);
    }


}
