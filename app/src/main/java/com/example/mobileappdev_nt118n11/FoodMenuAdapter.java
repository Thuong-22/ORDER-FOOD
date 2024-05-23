package com.example.mobileappdev_nt118n11;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdev_nt118n11.Database.Database;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.ViewHolder> {

    ArrayList<Food> list;
    Context context;
    FirebaseDatabase database;

    public FoodMenuAdapter(ArrayList<Food> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);

        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodMenuAdapter.ViewHolder holder, int position) {
        database = FirebaseDatabase.getInstance();

        Food foodModel = list.get(position);
        Picasso.get().load(foodModel.getImage()).placeholder(R.drawable.background).into(holder.item_image);
        holder.item_name.setText(foodModel.getName());
        holder.item_type.setText(foodModel.getFoodtype());
        holder.item_price.setText(StrDecimalFormat(foodModel.getPrice()));

        if (!foodModel.getIsFavourite()) {
            holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24_fill);
        }
        Log.d("Database", "Food Fav in position: " + position + foodModel.getIsFavourite());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foodModel.getId().equals(null) && !foodModel.getId().isEmpty()){
                    Toast.makeText(context, "Không tìm thấy món được chọn!", Toast.LENGTH_SHORT).show();

                }
                else{
                    Intent intent = new Intent(context, FoodDetailActivity.class);
                    intent.putExtra("idKey", foodModel.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        holder.item_isFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database localDB = new Database(context);
                DatabaseReference favouriteRef = database.getReference().child("Favourite").child(Phone.getKey_Phone());
                if (foodModel.getIsFavourite()) {
                    //Xóa khỏi favorites trong localDB
                    localDB.removeToFavorites(foodModel.getId(), Phone.getKey_Phone());
                    //Xóa khỏi favourite trong firebase
                    favouriteRef.child(foodModel.getId()).removeValue();

                    Log.d("Database", "Call func: removed favorite for foodId: " + foodModel.getId());

                    foodModel.setIsFavourite(false);
                    holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    // Thêm vào favorites
                    localDB.addToFavorites(foodModel.getId(), Phone.getKey_Phone());
                    //Thêm vào favourite trong firebase
                    Map<String, Object> favData = new HashMap<>();
                    favData.put("descr", foodModel.getDescr());
                    favData.put("foodtype", foodModel.getFoodtype());
                    favData.put("image", foodModel.getImage());
                    favData.put("name", foodModel.getName());
                    favData.put("price", foodModel.getPrice());

                    favouriteRef.child(foodModel.getId()).setValue(favData);
                    Log.d("Database", "Call func: add favorite for foodId: " + foodModel.getId());

                    foodModel.setIsFavourite(true);
                    holder.item_isFav.setImageResource(R.drawable.ic_baseline_favorite_24_fill);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView item_name, item_type, item_price;
        ImageButton item_isFav;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = (ImageView) itemView.findViewById(R.id.food_image);
            item_name = (TextView) itemView.findViewById(R.id.food_name);
            item_type = (TextView) itemView.findViewById(R.id.food_type);
            item_price = (TextView) itemView.findViewById(R.id.food_price);
            item_isFav = (ImageButton) itemView.findViewById(R.id.food_button_favourite);
        }
    }

    //Ngắt số vd: xxxxxx -> xxx,xxx
    public static String StrDecimalFormat(String value)
    {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1)
        {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt( -1 + str1.length()) == '.')
        {
            j--;
            str3 = ".";
        }
        for (int k = j;; k--)
        {
            if (k < 0)
            {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3)
            {
                str3 = "." + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }

    }
}
