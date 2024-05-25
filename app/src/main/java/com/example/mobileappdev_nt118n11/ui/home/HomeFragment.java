package com.example.mobileappdev_nt118n11.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdev_nt118n11.Common;
import com.example.mobileappdev_nt118n11.Database.Database;
import com.example.mobileappdev_nt118n11.FoodDetailActivity;
import com.example.mobileappdev_nt118n11.FoodViewHolder;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.FoodMenuAdapter;
import com.example.mobileappdev_nt118n11.R;
import com.example.mobileappdev_nt118n11.SearchActivity;
import com.example.mobileappdev_nt118n11.databinding.FragmentHomeBinding;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView rcvFoodList;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Food> foodList;
    FirebaseDatabase database;
    Database localDB;
    DatabaseReference dbRefFoodMenu;
    EditText etSearch;
    FoodMenuAdapter recyclerAdapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> menuAdapter;

//    private Context fragmentContext;

    public HomeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        try {
            View root = inflater.inflate(R.layout.fragment_home, container, false);

            foodList = new ArrayList();
            recyclerAdapter = new FoodMenuAdapter(foodList, getActivity().getApplicationContext());

            rcvFoodList = (RecyclerView) root.findViewById(R.id.rcv_home_food_menu);
            rcvFoodList.setHasFixedSize(true);
            //layoutManager = new LinearLayoutManager(fragmentContext);
            layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            rcvFoodList.setLayoutManager(layoutManager);
            //rcvFoodList.addItemDecoration(new DividerItemDecoration(rcvFoodList.getContext(), DividerItemDecoration.VERTICAL));
            rcvFoodList.setAdapter(recyclerAdapter);

            //add Fav, sau do combine food model hien tai voi fav
            localDB = new Database(requireContext());

            database = FirebaseDatabase.getInstance();
            DatabaseReference favouriteRef = database.getReference().child("Favourite").child(Phone.getKey_Phone());
            Log.d("Database", "CURENT USER PHONE: "+ Phone.getKey_Phone());

//            foodList = database.getReference("Foods");
            database.getReference().child("Food").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Food foodModel = dataSnapshot.getValue(Food.class);
                        String pushkey = dataSnapshot.getKey().toString();
                        //get fav
//                        if(localDB.isFavorites_new(pushkey, Phone.getKey_Phone())) {
//                            foodModel.setIsFavourite(true);
//                        } else {
//                            foodModel.setIsFavourite(false);
//                        }

                        // Kiểm tra xem pushkey (id của món ăn) có tồn tại trong bảng Favourite của user không
                        favouriteRef.child(pushkey).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (snapshot.exists() && localDB.isFavorites_new(pushkey, Phone.getKey_Phone())) {
                                   // Nếu tồn tại trong Favourite, đánh dấu là IsFavourite = true
                                   foodModel.setIsFavourite(true);
                               } else {
                                   // Nếu không tồn tại trong Favourite, đánh dấu là IsFavourite = false
                                   foodModel.setIsFavourite(false);
                               }
                           }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Error checking favourite status: " + error.getMessage());
                            }
                        });

                        Log.i("idKey",pushkey);
                        //keyList.add(pushkey);
                        foodModel.setId(pushkey);
                        foodList.add(foodModel);
                    }
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return root;
        }catch (Exception e) {
            Log.e("HomeFragment", "onCreateView", e);
            throw e;}
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    private void loadMenu(){
//        FirebaseRecyclerOptions<Food> option = new FirebaseRecyclerOptions.Builder<Food>()
//                .setQuery(dbRefFoodMenu,Food.class)
//                .build();
//        menuAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(option) {
//            @Override
//            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
//                final Food foodModel = model;
//                final String key = menuAdapter.getRef(position).getKey();
//                Picasso.get().load(foodModel.getImage()).placeholder(R.drawable.background).into(holder.item_image);
//                holder.item_name.setText(foodModel.getName());
//                holder.item_type.setText(foodModel.getFoodtype());
//                holder.item_price.setText(StrDecimalFormat(foodModel.getPrice()));
//                ///add favorite
//                if(localDB.isFavorites(menuAdapter.getRef(position).getKey(), Phone.getKey_Phone())){
//                    holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24);
//                }
//                ///////Click button tym
//                holder.item_isFav.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (!localDB.isFavorites(menuAdapter.getRef(position).getKey(), Common.currentUser.getName())) {
//                            localDB.addToFavorites(menuAdapter.getRef(position).getKey(), Common.currentUser.getName());
//                            holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24);
//                            Toast.makeText(getBaseContext(), "" + foodModel.getName() + "was added to Favorites", Toast.LENGTH_SHORT).show();
//                        } else {
//                            localDB.addToFavorites(menuAdapter.getRef(position).getKey(), Common.currentUser.getName());
//                            holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24);
//                            Toast.makeText(getBaseContext(), "" + foodModel.getName() + "was removed from Favorites", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(requireContext(), FoodDetailActivity.class);
//                        intent.putExtra("idKey", key);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
//                return new FoodViewHolder(view);
//            }
//        };
//        menuAdapter.startListening();
//        rcvFoodList.setAdapter(menuAdapter);
//    }

//    private void loadMenu() {
//        FirebaseRecyclerAdapter<> adapter = new FirebaseRecy
//    }


}