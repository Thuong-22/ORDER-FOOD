package com.example.mobileappdev_nt118n11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileappdev_nt118n11.Database.Database;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.Model.Order;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FoodDetailActivity extends AppCompatActivity {

    FirebaseDatabase database;
    Database localDB;
    ImageView ivImageDetail;
    FloatingActionButton btnFavourite;
    TextView tvNameDetail, tvTypeDetail, tvPriceDetail, tvDescrDetail;
    Food foodDetail;
    Button btnCart;
    ImageView ivMinusQuantity, ivPlusQuantity;
    TextView tvQuantity;
    private int numberOrder = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        ivImageDetail = (ImageView) findViewById(R.id.food_image_detail);
        tvNameDetail = (TextView) findViewById(R.id.food_name_detail);
        tvTypeDetail = (TextView) findViewById(R.id.food_type_detail);
        tvPriceDetail = (TextView) findViewById(R.id.food_price_detail);
        tvDescrDetail = (TextView) findViewById(R.id.food_description_detail);
        btnFavourite = (FloatingActionButton) findViewById(R.id.btn_add_to_favourite);


        Intent intent = getIntent();
        String id = intent.getStringExtra("idKey");

        localDB = new Database(this);

        //Log.i("Key in food detail",id);

        database = FirebaseDatabase.getInstance();
        DatabaseReference favouriteRef = database.getReference().child("Favourite").child(Phone.getKey_Phone());
        database.getReference().child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (id.equals(dataSnapshot.getKey().toString())) {
                        foodDetail = dataSnapshot.getValue(Food.class);
                        foodDetail.setId(id);
                        Log.d("Databse", "FoodDetail.ID() in init: " + foodDetail.getId() + " " + foodDetail.getName());
//                        Log.i("Check", "get into compare");
//                        Log.i("Name", foodDetail.getName().toString());
//                        Log.i("Price", foodDetail.getPrice().toString());
//                        Log.i("Image", foodDetail.getImage().toString());
//                        Log.i("Type", foodDetail.getFoodtype().toString());
//                        Log.i("Descr", foodDetail.getDescr().toString());
                        Picasso.get().load(foodDetail.getImage()).placeholder(R.drawable.background).into(ivImageDetail);
                        tvNameDetail.setText(foodDetail.getName());
                        tvTypeDetail.setText(foodDetail.getFoodtype());
                        tvPriceDetail.setText(getDecimalFormattedString(foodDetail.getPrice()));
                        tvDescrDetail.setText(foodDetail.getDescr());

                        //Kiem tra xem mon an nay user co thich ko
                        favouriteRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() && localDB.isFavorites_new(id, Phone.getKey_Phone())) {
                                    // Nếu tồn tại trong Favourite, đánh dấu là IsFavourite = true
                                    foodDetail.setIsFavourite(true);
                                    btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_24_fill);
                                } else {
                                    // Nếu không tồn tại trong Favourite, đánh dấu là IsFavourite = false
                                    foodDetail.setIsFavourite(false);
                                    btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Error checking favourite status: " + error.getMessage());
                            }
                        });

                        btnFavourite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (foodDetail.getIsFavourite()) {
                                    Log.d("Databse", "FoodDetail.ID(): " + foodDetail.getId());
                                    //Xóa khỏi favorites trong localDB
                                    localDB.removeToFavorites(foodDetail.getId(), Phone.getKey_Phone());
                                    //Xóa khỏi favourite trong firebase
                                    favouriteRef.child(foodDetail.getId()).removeValue();

                                    Log.d("Database", "Call func: removed favorite for foodId: " + foodDetail.getId());

                                    foodDetail.setIsFavourite(false);
                                    btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
                                } else {
                                    Log.d("Databse", "FoodDetail.ID(): " + foodDetail.getId());
                                    // Thêm vào favorites
                                    localDB.addToFavorites(foodDetail.getId(), Phone.getKey_Phone());
                                    //Thêm vào favourite trong firebase
                                    Map<String, Object> favData = new HashMap<>();
                                    favData.put("descr", foodDetail.getDescr());
                                    favData.put("foodtype", foodDetail.getFoodtype());
                                    favData.put("image", foodDetail.getImage());
                                    favData.put("name", foodDetail.getName());
                                    favData.put("price", foodDetail.getPrice());

                                    favouriteRef.child(foodDetail.getId()).setValue(favData);
                                    Log.d("Database", "Call func: add favorite for foodId: " + foodDetail.getId());

                                    foodDetail.setIsFavourite(true);
                                    btnFavourite.setImageResource(R.drawable.ic_baseline_favorite_24_fill);
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Here the foodDetail is null, cannot get
//        Log.i("Name", foodDetail.getName().toString());
//        Log.i("Price", foodDetail.getPrice().toString());
//        Log.i("Image", foodDetail.getImage().toString());
//        Log.i("Type", foodDetail.getFoodtype().toString());
//        Log.i("Descr", foodDetail.getDescr().toString());

//        Picasso.get().load(foodDetail.getImage()).placeholder(R.drawable.background).into(ivImageDetail);
//        tvNameDetail.setText(foodDetail.getName());
//        tvTypeDetail.setText(foodDetail.getFoodtype());
//        tvPriceDetail.setText(foodDetail.getPrice());
//        tvDescrDetail.setText(foodDetail.getDescr());

        btnCart = (Button) findViewById(R.id.btn_add_to_cart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        Phone.Key_Phone,
                        id,
                        foodDetail.getName(),
                        tvQuantity.getText().toString(),
                        foodDetail.getPrice(),
                        foodDetail.getImage()
                ));

                Toast.makeText(FoodDetailActivity.this,"Added to cart",Toast.LENGTH_SHORT).show();
            }
        });
        tvQuantity = (TextView) findViewById(R.id.tv_number);
        ivMinusQuantity= (ImageView) findViewById(R.id.iv_minus);
        ivPlusQuantity = (ImageView) findViewById(R.id.iv_plus);
        ivMinusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOrder > 1) {
                    numberOrder--;
                    tvQuantity.setText(String.valueOf(numberOrder));
                }
            }
        });
        ivPlusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOrder < 10) {
                    numberOrder++;
                    tvQuantity.setText(String.valueOf(numberOrder));
                }
            }
        });
    }


    public static String getDecimalFormattedString(String value)
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