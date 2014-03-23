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

public class ListViewActivity extends ListActivity {
	private static final String TAG = "InventureListView";
    private ProductDatabase mProductDb;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventur_list);
        mProductDb = new ProductDatabase(this); 
        Log.d(TAG,  "ListViewActivity onCreate - thuesing");   
        getData();
    }

    
    private void clearData() {
    	mProductDb.clearInventurData();
    }
    
   
    @SuppressWarnings("deprecation")
	private void getData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mProductDb.fetchInventurDataAll();
        
        Log.d("ListViewActivity",  "cursor count: " + c.getCount() + " - thuesing");   
        
        startManagingCursor(c);

        String[] from = new String[] { ProductDatabase.KEY_BARCODE , ProductDatabase.KEY_TITLE , ProductDatabase.KEY_WEIGHT};
        int[] to = new int[] { R.id.barcode , R.id.title ,R.id.weight };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter results = new SimpleCursorAdapter(this, R.layout.inventur_row, c, from, to);
        setListAdapter(results);

    }
   
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_view, menu);		
		return true;
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    
 	    case R.id.send_mail:

	          try {
	
	                new ExportDatabaseCSVTask(ListViewActivity.this).execute("");	
	           
	                String subject = "New Inventure CSV";
	                String message = "Have fun!";
	              
	                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);                    
	                //emailIntent.setType("plain/text");
	                emailIntent.setType( "message/rfc822");
	                //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email });
	                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  subject);
	                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message); 
	                
	                
	                File exportDir = new File(Environment.getExternalStorageDirectory(), "InventureApp");     
	                File file = new File(exportDir, "InventureApp.csv");
	                if (!file.exists() || !file.canRead()) {
	                    Toast.makeText(ListViewActivity.this, "Attachment Error", Toast.LENGTH_SHORT).show();
	                    finish();
	                    return true;
	                }
	                Uri fileUri = Uri.fromFile(file);              
	                Log.d(TAG, "File URI " + fileUri + " - thuesing"); 
	                
	                if (fileUri != null) {
	                       emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
	                }                   
	
	                startActivity(Intent.createChooser(emailIntent,"Sending email..."));	                
	              
	               
	          } catch (Throwable t) {
	                Toast.makeText(ListViewActivity.this,"Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
	                Log.e("Error in MainActivity",t.toString());
	          }	  
    		
    		return true;
	    
	    case R.id.clear_all:

	    	new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.clear_list)
	        .setMessage(R.string.really_clear_list)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
                    clearData();
                    Toast.makeText(ListViewActivity.this,"All items deleted!", Toast.LENGTH_LONG).show();
                    ListViewActivity.this.finish();
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








