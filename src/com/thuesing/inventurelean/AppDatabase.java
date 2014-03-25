package com.thuesing.inventurelean;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDatabase {

    private static final String TAG = "InventureProductDb";
    private static final String INVENTUR_TABLE = "results"; 
    private static final String PRODUCT_TABLE = "products"; 
    private static final String DATABASE_NAME = "inventure.db";
    private static final int DATABASE_VERSION = 3; // new Schema: Product and Inventur Table
    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ROWID = "_id";

    private SQLiteDatabase db;
    
    public AppDatabase(Context context) {
    	Log.d(TAG,  "AppDatabase Constructor - thuesing");       	
        ProductDatabaseHelper helper = new ProductDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }
    
    public boolean insertInventurData(ItemData product) {
    	boolean success = false;
        ContentValues vals = new ContentValues();
        vals.put(KEY_BARCODE, product.barcode);
        vals.put(KEY_TITLE, product.title);
        vals.put(KEY_WEIGHT, product.weight);
        
        db.beginTransaction();
        try {
            // insert Inventur data
        	db.insert(INVENTUR_TABLE, null, vals) ;        	
        	// Update Product List
        	vals.remove(KEY_WEIGHT);
        	db.replace (PRODUCT_TABLE, null, vals) ;        	
        	// done
        	//return db.insert(INVENTUR_TABLE, null, vals) != -1;
            db.setTransactionSuccessful();
            success = true;        	  
          } finally {
            db.endTransaction();
          }         
        return success;
       
    }  
    
    /**
     * Return a Cursor over the list of all items
     * @return Cursor 
     */
    public Cursor fetchInventurDataAll() {
    	Log.d(TAG, "fetch all - thuesing");
        return db.query(INVENTUR_TABLE, 
        		new String[] {KEY_ROWID, KEY_BARCODE, KEY_TITLE, KEY_WEIGHT}, null, null, null, null, null);
    }

    
    public Cursor fetchProductDataAll() {
    	Log.d(TAG, "fetch all - thuesing");
        return db.query(PRODUCT_TABLE, 
        		new String[] {KEY_ROWID, KEY_BARCODE, KEY_TITLE}, null, null, null, null, null);
    }
    
    public String getProducTitleForBarcode(String barcode) { 	
    	Log.d(TAG, "getProducTitleForBarcode " + barcode + " - thuesing");
  
    	Cursor cursor = db.rawQuery("select " + KEY_TITLE + " from " 
    								+ PRODUCT_TABLE + " where " 
    								+ KEY_BARCODE + " = ?", new String[] { barcode.trim() });   
    	if(cursor!=null && cursor.getCount()>0) {
	    	cursor.moveToFirst();    	
	    	int columnIndex = cursor.getColumnIndexOrThrow(KEY_TITLE);    	
	    	String title = cursor.getString(columnIndex);    	
	    	cursor.close();
	    	return title;  
    	} else {
    		return null;
    	}	
    }
    
    public ArrayList<String> getProductTitlesAll() { 	
    	Log.d(TAG, "getTitlesAll - thuesing");    	
    	Cursor cursor = db.rawQuery("select " + KEY_TITLE + " from " 
    								+ PRODUCT_TABLE, null);    
    	
    	ArrayList titles = new ArrayList();  
    	
    	if(cursor!=null && cursor.getCount()>0) {        	
	    	cursor.moveToFirst();
	    	while(!cursor.isAfterLast()) {
	    	     titles.add(cursor.getString(cursor.getColumnIndex(KEY_TITLE))); 
	    	     cursor.moveToNext();
	    	}
	    	cursor.close();   	    	   	
    	} 
    	return titles; // return empty list for Adapter count
    }  
    
    
    /*
    public Cursor rawQuery(String q) { // TODO
    	Log.d(TAG, "raw query" + q + " - thuesing");
        return db.rawQuery(q ,null);
    }
    */
    
    public void clearInventurData() {
    	Log.d(TAG, "clearInventurData - thuesing");
    	db.execSQL("delete from " + INVENTUR_TABLE);  
    } 
    
    public void clearProductData() {
    	Log.d(TAG, "clearProductData - thuesing");
    	db.execSQL("delete from " + PRODUCT_TABLE);  
    } 
    
    public void close() {
        db.close();
    }
    
	static final class ItemData {
	    String barcode;
	    String title;
	    Integer weight;
	    String created_at;
	}
  
    private static class ProductDatabaseHelper extends SQLiteOpenHelper {

        public ProductDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {        
        	Log.d(TAG, "onCreate - thuesing");     	
            StringBuilder CREATE_INVENTUR_TABLE = new StringBuilder();
            CREATE_INVENTUR_TABLE.append("create table ").append(INVENTUR_TABLE)
                .append("(  ")
                .append("   _id integer primary key,")
                .append("   title text,")              
                .append("   barcode text,")
                .append("   weight integer,")                 
                .append("   created_at text")
                .append(")  ");
     
            
            StringBuilder CREATE_PRODUCT_TABLE = new StringBuilder();
            CREATE_PRODUCT_TABLE.append("create table ").append(PRODUCT_TABLE)
                .append("(  ")
                .append("   _id integer primary key,")
                .append("   barcode text not null unique,") // in
                .append("   title text,")
                .append("   created_at text")
                .append(")  ");

            db.execSQL(CREATE_INVENTUR_TABLE.toString());
            db.execSQL(CREATE_PRODUCT_TABLE.toString());           
            
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {            
            db.execSQL("drop table if exists " + INVENTUR_TABLE);      
            db.execSQL("drop table if exists " + PRODUCT_TABLE);    
            onCreate(db);
        }
        
    }    // ProductdatabaseHelper
    

    
    
}
