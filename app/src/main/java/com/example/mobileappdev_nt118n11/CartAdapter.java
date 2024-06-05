package com.example.mobileappdev_nt118n11;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.Model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>  {

    private ArrayList<Order> cartList = new ArrayList<Order>();
    private Context context;

    public CartAdapter(ArrayList<Order> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Order foodModel = cartList.get(position);
        //Picasso.get().load(foodModel.getImage()).placeholder(R.drawable.background).into(holder.cart_image);
        holder.cart_name.setText(foodModel.getProductName());
        holder.cart_price.setText(StrDecimalFormat(foodModel.getPrice()));
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (foodModel.getProductId().equals(null) && !foodModel.getProductId().isEmpty()){
//                    Toast.makeText(context, "Không tìm thấy món được chọn!", Toast.LENGTH_SHORT).show();
//
//                }
//                else{
//                    Intent intent = new Intent(context, FoodDetailActivity.class);
//                    intent.putExtra("idKey", foodModel.getId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder{
        ImageView cart_image;
        TextView cart_name, cart_note, cart_quantity, cart_price;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cart_image = (ImageView) itemView.findViewById(R.id.cart_image);
            cart_name = (TextView) itemView.findViewById(R.id.cart_name);
            cart_note = (TextView) itemView.findViewById(R.id.food_note);
            cart_quantity = (TextView) itemView.findViewById(R.id.cart_quantity);
            cart_price = (TextView) itemView.findViewById(R.id.cart_price);
        }
    }

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


