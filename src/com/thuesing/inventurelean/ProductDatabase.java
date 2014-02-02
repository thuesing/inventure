package com.thuesing.inventurelean;

import java.math.BigDecimal;

import com.thuesing.inventurelean.MainActivity.ProductData;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDatabase {
    private static final String PRODUCT_TABLE = "products"; 
    private static final String DATABASE_NAME = "spot_pay.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;
    
    public ProductDatabase(Context context) {
   	
        ProductDatabaseHelper helper = new ProductDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }
    
    public boolean insert(ProductData product) {
        ContentValues vals = new ContentValues();
        vals.put("barcode", product.barcode);
        vals.put("format", product.format);
        vals.put("title", product.title);
        vals.put("price", product.price.multiply(new BigDecimal(100)).longValue());

        return db.insert(PRODUCT_TABLE, null, vals) != -1;
    }    
    
    private static class ProductDatabaseHelper extends SQLiteOpenHelper {

        public ProductDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {            
            StringBuilder sql = new StringBuilder();

            sql.append("create table ").append(PRODUCT_TABLE)
                .append("(  ")
                .append("   _id integer primary key,")
                .append("   barcode text,")
                .append("   format text,")
                .append("   title text,")
                .append("   price currency")
                .append(")  ");

            db.execSQL(sql.toString());         
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {            
            db.execSQL("drop table if exists " + PRODUCT_TABLE);                    
            onCreate(db);
        }
        
    }    // ProductdatabaseHelper
    
    
}
