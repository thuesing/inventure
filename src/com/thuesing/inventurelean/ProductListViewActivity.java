/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thuesing.inventurelean;

import java.io.File;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ProductListViewActivity extends ListActivity {
	private static final String TAG = "InventureListView";
    private AppDatabase mAppDb;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventur_list);
        mAppDb = new AppDatabase(this); 
        Log.d(TAG,  "DataListViewActivity onCreate - thuesing");   
        getData();
    }

    
    private void clearProductData() {
    	mAppDb.clearProductData();
    }
    
   
    @SuppressWarnings("deprecation")
	private void getData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mAppDb.fetchProductDataAll();
        
        Log.d("DataListViewActivity",  "cursor count: " + c.getCount() + " - thuesing");   
        
        startManagingCursor(c);

        String[] from = new String[] { AppDatabase.KEY_BARCODE , AppDatabase.KEY_TITLE};
        int[] to = new int[] { R.id.barcode , R.id.title};
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter results = new SimpleCursorAdapter(this, R.layout.product_row, c, from, to);
        setListAdapter(results);
        //c.close();

    }
   
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_list_view, menu);		
		return true;
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    
	    case R.id.clear_all:

	    	new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.clear_list)
	        .setMessage(R.string.really_clear_list)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
                    clearProductData();
                    Toast.makeText(ProductListViewActivity.this,"All data deleted!", Toast.LENGTH_LONG).show();
                    ProductListViewActivity.this.finish();
                    //startActivity(getIntent());
	            }

	        })
	        .setNegativeButton(R.string.no, null)
	        .show();
	    	
	    	
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    
    
}








