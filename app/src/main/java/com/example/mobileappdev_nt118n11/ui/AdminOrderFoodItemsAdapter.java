package com.example.mobileappdev_nt118n11.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileappdev_nt118n11.Model.Order;
import com.example.mobileappdev_nt118n11.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminOrderFoodItemsAdapter extends RecyclerView.Adapter<AdminOrderFoodItemsAdapter.OrderItemViewHolder>{
    private Context context;
    private List<Order> orderItemList;

    public AdminOrderFoodItemsAdapter(Context context, List<Order> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.oder_food_detail_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        Order item = orderItemList.get(position);
        holder.itemName.setText(item.getProductName());
//        holder.itemCategory.setText(item.get());
        holder.itemQuantity.setText("Số lượng: " + item.getQuantity());
        holder.itemPrice.setText("Giá: $" + item.getPrice());
        // Load image using Glide
//        Glide.with(context).load(item.getImage()).into(holder.itemImage);
        Picasso.get().load(item.getImage()).placeholder(R.drawable.background).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemCategory, itemQuantity, itemPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
//            itemCategory = itemView.findViewById(R.id.item_category);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemPrice = itemView.findViewById(R.id.item_price);
        }
    }
}
