package com.example.mobileappdev_nt118n11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileappdev_nt118n11.AsyncTask.FBDataBindingAsyncTask;
import com.example.mobileappdev_nt118n11.Database.Database;
import com.example.mobileappdev_nt118n11.Model.Food;
import com.example.mobileappdev_nt118n11.ui.profile.Phone;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class SearchActivity extends AppCompatActivity {

    RecyclerView rcvFoodList;
    RecyclerView.LayoutManager layoutManager;
    String searchKeyword;
    ArrayList<Food> recyclerFoodList;
    FirebaseDatabase database;

    Database localDB;
    DatabaseReference dbRefFoodMenu;
    EditText etSearch;
    FoodMenuAdapter recyclerAdapter;
    MaterialSearchBar materialSearchBar;
    ArrayList<String> suggestList;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        dbRefFoodMenu = database.getReference().child("Food");

        rcvFoodList = findViewById(R.id.rcv_food_menu_search);
        rcvFoodList.setHasFixedSize(true);
        /////////////////////
        localDB=new Database(this);
        //layoutManager = new LinearLayoutManager(fragmentContext);
        layoutManager = new LinearLayoutManager(this);
        rcvFoodList.setLayoutManager(layoutManager);

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.mtsearchbar);
        loadSuggestion();
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<String> suggest = new ArrayList<String>();
                for (String search:suggestList){
                    if (search.toLowerCase(Locale.ROOT).contains(materialSearchBar.getText().toLowerCase(Locale.ROOT)))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled){

                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Log.w("Call Func", "Call Funct onSearchConfirmed with startSearch, text: " + text);
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        loadMenu();
    }
    // Tìm món với từ khóa text
    private void startSearch(CharSequence text) {
        Log.d("D", "On Call startSearch func: " + text.toString());
        String searchText = text.toString();
//                .toLowerCase();

        // Tạo truy vấn để tìm kiếm theo tên
        Query searchByName = FirebaseDatabase.getInstance().getReference("Food")
                .orderByChild("Name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        // Kiểm tra truy vấn có trả về kết quả không
        searchByName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("D", "Query returned results");
                } else {
                    Log.d("D", "Query returned no results");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("D", "Query failed: " + error.getMessage());
            }
        });

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(searchByName, Food.class)
                        .build();

        // Dừng adapter hiện tại nếu đang lắng nghe
        if (menuAdapter != null) {
            menuAdapter.stopListening();
        }

        // Tạo mới adapter để hiển thị kết quả tìm kiếm
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                Log.d("D", "On Call searchAdapter func: onBindViewHolder, option: " + options.toString());
                final String key = getRef(position).getKey(); // Lấy key của item hiện tại
                Picasso.get().load(model.getImage()).placeholder(R.drawable.background).into(holder.item_image);
                holder.item_name.setText(model.getName());
                holder.item_type.setText(model.getFoodtype());
                holder.item_price.setText(StrDecimalFormat(model.getPrice()));

                // Xử lý sự kiện khi người dùng nhấp vào item
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Chuyển sang màn hình chi tiết món ăn
                        Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
                        intent.putExtra("idKey", key);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };

        // Bắt đầu lắng nghe sự kiện
        searchAdapter.startListening();
        rcvFoodList.setAdapter(searchAdapter); // Hiển thị kết quả lên RecyclerView
    }



    private void loadMenu() {
        FirebaseRecyclerOptions<Food> option = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(dbRefFoodMenu, Food.class)
                .build();

        // Dừng nghe searchAdapter (nếu có)
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }

        // Bắt đầu nghe menuAdapter
        menuAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                final Food foodModel = model;
                final String key = menuAdapter.getRef(position).getKey();
                Picasso.get().load(foodModel.getImage()).placeholder(R.drawable.background).into(holder.item_image);
                holder.item_name.setText(foodModel.getName());
                holder.item_type.setText(foodModel.getFoodtype());
                holder.item_price.setText(StrDecimalFormat(foodModel.getPrice()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
                        intent.putExtra("idKey", key);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };

        menuAdapter.startListening();
        rcvFoodList.setAdapter(menuAdapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (menuAdapter != null)
            menuAdapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
    }
    // tải gợi ý lên thanh search
    private void loadSuggestion() {
        suggestList = new ArrayList<>();
        //recyclerAdapter = new FoodMenuAdapter(recyclerFoodList, this);
        //new FBDataBindingAsyncTask.execute("Food");
        database.getReference().child("Food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.exists()) {
                        Food foodItem = dataSnapshot.getValue(Food.class);
//                    String pushkey = dataSnapshot.getKey();
//                    Log.i("key",pushkey);
//                    //keyList.add(pushkey);
//                    foodItem.setId(pushkey);
//                    recyclerFoodList.add(foodItem);
                        suggestList.add(foodItem.getName());
                    }
                    else{
                        Toast.makeText(getBaseContext(), "Không gte đc value trong snapshot!", Toast.LENGTH_SHORT).show();
                    }
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Ngắt số vd: xxxxxx -> xxx,xxx
    public static String StrDecimalFormat(String value) {
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