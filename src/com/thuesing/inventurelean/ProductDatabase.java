package com.thuesing.inventurelean;

import com.thuesing.inventurelean.MainActivity.ItemData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductDatabase {

    private static final String TAG = "InventureProductDb";
    private static final String PRODUCT_TABLE = "results"; 
    private static final String DATABASE_NAME = "inventure.db";
    private static final int DATABASE_VERSION = 1;
    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ROWID = "_id";

    private SQLiteDatabase db;
    
    public ProductDatabase(Context context) {
    	Log.d(TAG,  "ProductDatabase Constructor - thuesing");   
    	
        ProductDatabaseHelper helper = new ProductDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }
    
    public boolean insert(ItemData product) {
        ContentValues vals = new ContentValues();
        vals.put("barcode", product.barcode);
        vals.put("title", product.title);
        vals.put("weight", product.weight);

        return db.insert(PRODUCT_TABLE, null, vals) != -1;
    }  
    
    /**
     * Return a Cursor over the list of all items
     * @return Cursor 
     */
    public Cursor fetchDataAll() {
    	Log.d(TAG, "fetch all - thuesing");
        return db.query(PRODUCT_TABLE, 
        		new String[] {KEY_ROWID, KEY_BARCODE, KEY_TITLE, KEY_WEIGHT}, null, null, null, null, null);
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
                .append("   created_at text,")
                .append("   title text,")
                .append("   weight integer")
                .append(")  ");

            db.execSQL(sql.toString());         
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {            
            db.execSQL("drop table if exists " + PRODUCT_TABLE);                    
            onCreate(db);
        }
        
    }    // ProductdatabaseHelper
    
    public void close() {
        db.close();
    }
    
    
}
