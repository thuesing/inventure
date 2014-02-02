package com.thuesing.inventurelean;

import java.math.BigDecimal;

import com.thuesing.inventurelean.MainActivity.ProductData;

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
    private static final ProductData mProductData = new ProductData();
	private EditText mBarcodeEdit;
	private EditText mFormatEdit;
	private EditText mTitleEdit;
	private EditText mPriceEdit;
	private Button mScanButton;
	private Button mAddButton;

    // private fields omitted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        mBarcodeEdit = (EditText) findViewById(R.id.barcodeEdit);
        mFormatEdit = (EditText) findViewById(R.id.codeFormatEdit);
        mTitleEdit = (EditText) findViewById(R.id.titleEdit);
        mPriceEdit = (EditText) findViewById(R.id.priceEdit);
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
	            String format = mFormatEdit.getText().toString();
	            String title = mTitleEdit.getText().toString();
	            String price = mPriceEdit.getText().toString();

	            String errors = validateFields(barcode, format, title, price);
	            if (errors.length() > 0) {
	               // thue: not defined
	            	// showInfoDialog(this, "Please fix errors", errors);
	            	Toast.makeText(getApplicationContext(), "Please fix: " + errors, Toast.LENGTH_LONG).show();
	            } else {
	                mProductData.barcode = barcode;
	                mProductData.format = format;
	                mProductData.title = title;
	                mProductData.price = new BigDecimal(price);
                    
	                // TODO
	                // mProductDb.insert(mProductData);
	                
	                // thue: not defined
	                // showInfoDialog(this, "Success", "Product saved successfully");
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

                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                mFormatEdit.setText(format);
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
    
    private static String validateFields(String barcode, String format, String title, String price) {
    	    StringBuilder errors = new StringBuilder();

    	    if (barcode.matches("^\\s*$")) {
    	        errors.append("Barcode required\n");
    	    }

    	    if (format.matches("^\\s*$")) {
    	        errors.append("Format required\n");
    	    }

    	    if (title.matches("^\\s*$")) {
    	        errors.append("Title required\n");
    	    }

    	    if (!price.matches("^-?\\d+(.\\d+)?$")) {
    	        errors.append("Need numeric price\n");
    	    }

    	    return errors.toString();
    }
    
    private void resetForm() {
        mBarcodeEdit.setText(""); 
        mFormatEdit.setText(""); 
        mTitleEdit.setText(""); 
        mPriceEdit.setText(""); 		
	}

    
}