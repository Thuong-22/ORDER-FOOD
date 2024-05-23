package com.example.mobileappdev_nt118n11.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.mobileappdev_nt118n11.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="LocalFoodDB.db";
    private static final int DB_VERSION=3;
    private SQLiteDatabase db;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
//        setForcedUpgrade(); // Force upgrade to new version
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop old tables if needed
//        db.execSQL("DROP TABLE IF EXISTS Favorites");
//        onCreate(db); // Recreate tables
//    }

//    public void setForcedUpgrade() {
//        SQLiteDatabase db = getWritableDatabase();
//        db.setVersion(DB_VERSION);
//    }

    public Boolean checkIfFoodExist(String foodId, String userPhone){
        Boolean flag;

        SQLiteDatabase sqlDB = getReadableDatabase();
        String query = String.format("SELECT * FROM OrderDetail WHERE UserPhone = '%s' AND ProductId = '%s'",userPhone,foodId);
        Cursor cursor = sqlDB.rawQuery(query,null);
        if (cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;

    }

    public ArrayList<Order> getCart(String userPhone){
        SQLiteDatabase sqlDB = getReadableDatabase();
        SQLiteQueryBuilder queryBuild = new SQLiteQueryBuilder();
        String[] sqlSelect = {"UserPhone","ProductID","ProductName","Quantity","Price","Image"};
        String sqlTable = "OrderDetail";

        queryBuild.setTables(sqlTable);
        Cursor c = queryBuild.query(sqlDB,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final ArrayList<Order> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                int phoneCol = c.getColumnIndex("UserPhone");
                int idProductCol = c.getColumnIndex("ProductId");
                int nameCol = c.getColumnIndex("ProductName");
                int quantityCol = c.getColumnIndex("Quantity");
                int priceCol = c.getColumnIndex("Price");
                int imgCol = c.getColumnIndex("Image");

                if (phoneCol != -1 && idProductCol != -1 && nameCol!=-1 && quantityCol!=-1 && priceCol!=-1 && imgCol!=-1)
                    result.add(new Order(
                            c.getString(phoneCol),
                            c.getString(idProductCol),
                            c.getString(nameCol),
                            c.getString(quantityCol),
                            c.getString(priceCol),
                            c.getString(imgCol)));
                else
                    Log.e("databases", "cant get a product in order because column equal -1");
            }while(c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase sqlDB = getReadableDatabase();
        String query;
        if (checkIfFoodExist(order.getProductId(), order.getUserPhone())){
            int number = Integer.parseInt(order.getQuantity());
            query = String.format("UPDATE OrderDetail SET Quantity= Quantity + %d WHERE UserPhone = '%s' AND ProductId= '%s'",
                    number,
                    order.getUserPhone(),
                    order.getProductId());
        }
        else {
            query = String.format("INSERT INTO OrderDetail (UserPhone,ProductId,ProductName,Quantity,Price,Image) VALUES ('%s','%s','%s','%s','%s','%s')",
                    order.getUserPhone(),
                    order.getProductId(),
                    order.getProductName(),
                    order.getQuantity(),
                    order.getPrice(),
                    order.getImage());

        }
        sqlDB.execSQL(query);
    }

    public void cleanCart(String userPhone){
        SQLiteDatabase sqlDB = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone= '%s'",userPhone);
        sqlDB.execSQL(query);
    }

    public void updateCart(Order order) {
        SQLiteDatabase sqlDB = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= '%s' WHERE UserPhone = '%s' AND ProductId= '%s'",
                order.getQuantity(),
                order.getUserPhone(),
                order.getProductId());
        sqlDB.execSQL(query);
    }
    public void addToFavorites(String foodId, String userPhone){
        SQLiteDatabase db=getReadableDatabase();
        String query = String.format("INSERT INTO Favorites (FoodId,UserPhone) VALUES ('%s','%s');",foodId,userPhone);

        db.execSQL(query);
    }

    public void removeToFavorites(String foodId, String userPhone){
        SQLiteDatabase db=getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE  FoodId='%s' and UserPhone='%s';", foodId,userPhone);
        db.execSQL(query);
    }

    public boolean isFavorites(String foodId, String userPhone){
        SQLiteDatabase db=getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE  FoodId='%s' and UserPhone='%s';", foodId,userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return false;
    }

    public boolean isFavorites_new(String foodId, String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        boolean result = false;

        // Sử dụng try-with-resources để tự động đóng các tài nguyên như Cursor và SQLiteDatabase
        try (Cursor cursor = db.rawQuery("SELECT * FROM Favorites WHERE FoodId=? AND UserPhone=?", new String[]{foodId, userPhone})) {
            if (cursor != null && cursor.getCount() > 0) {
                result = true;
            }
        } catch (SQLiteException e) {
            Log.e("Database", "Error querying favorites: " + e.getMessage());
        }

        return result;
    }





}
