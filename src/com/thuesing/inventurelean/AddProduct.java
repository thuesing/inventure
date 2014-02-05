package com.thuesing.inventurelean;

import java.math.BigDecimal;

import com.thuesing.inventurelean.MainActivity.ItemData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddProduct extends Activity implements OnClickListener {
    private static final int REQUEST_BARCODE = 0;
    private static final ItemData mProductData = new ItemData();
	private EditText mBarcodeEdit;
	private EditText mTitleEdit;
	private EditText mWeightEdit;
	private Button mScanButton;
	private Button mAddButton;

    // private fields omitted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        mBarcodeEdit = (EditText) findViewById(R.id.barcodeEdit);
        mTitleEdit = (EditText) findViewById(R.id.titleEdit);
        mWeightEdit = (EditText) findViewById(R.id.weightEdit);
        
        mScanButton = (Button) findViewById(R.id.scanButton);
        mScanButton.setOnClickListener(this);
        mAddButton = (Button) findViewById(R.id.addButton);
        mAddButton.setOnClickListener(this);
        // mProductDb = new ProductDatabase(this); // not yet shown
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
	        case R.id.scanButton:
	            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	            startActivityForResult(intent, REQUEST_BARCODE);
	            break;
	        case R.id.addButton:
	            String barcode = mBarcodeEdit.getText().toString();
	            String title = mTitleEdit.getText().toString();
	            String weight = mWeightEdit.getText().toString();

	            String errors = validateFields(barcode, title, weight);
	            if (errors.length() > 0) {
	               // thue: not defined
	            	// showInfoDialog(this, "Please fix errors", errors);
	            	Toast.makeText(getApplicationContext(), "Please fix: " + errors, Toast.LENGTH_LONG).show();
	            } else {
	                mProductData.barcode = barcode;
	                mProductData.title = title;

                    
	                // TODO
	                // mProductDb.insert(mProductData);
	                
	                // thue: not defined, // showInfoDialog(this, "Success", "Product saved successfully");
	            	Toast.makeText(getApplicationContext(), "Success: Data saved successfully", Toast.LENGTH_LONG).show();
	         	   
	                resetForm();
	            }
	            break;
        }
    }
    

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_BARCODE) {
            if (resultCode == RESULT_OK) {
                String barcode = intent.getStringExtra("SCAN_RESULT");
                mBarcodeEdit.setText(barcode);
                // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // mFormatEdit.setText(format);
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
    
    private static String validateFields(String barcode, String title, String weight) {
    	    StringBuilder errors = new StringBuilder();

    	    if (barcode.matches("^\\s*$")) {
    	        errors.append("Barcode required\n");
    	    }

    	    if (title.matches("^\\s*$")) {
    	        errors.append("Title required\n");
    	    }

    	    if (!weight.matches("^-?\\d+(.\\d+)?$")) {
    	        errors.append("Need numeric weight\n");
    	    }

    	    return errors.toString();
    }
    
    private void resetForm() {
        mBarcodeEdit.setText(""); 
        mWeightEdit.setText(""); 
        mTitleEdit.setText(""); 
	
	}

    
}