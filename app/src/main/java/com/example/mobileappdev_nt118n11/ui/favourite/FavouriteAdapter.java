package com.example.mobileappdev_nt118n11.ui.favourite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdev_nt118n11.FoodDetailActivity;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private List<Food> favouriteList;
    private Context context;

    public FavouriteAdapter(List<Food> favouriteList, Context context) {
        this.favouriteList = favouriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = favouriteList.get(position);
        holder.foodName.setText(food.getName());
        Picasso.get().load(food.getImage()).into(holder.foodImage);

        // Xử lý sự kiện khi nhấn vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy id của món ăn được chọn
                String foodId = food.getId();
                // Chuyển sang FoodDetailActivity và truyền id của món ăn được chọn
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("idKey", foodId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodImage = itemView.findViewById(R.id.food_image);
        }
    }
}


