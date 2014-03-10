package com.thuesing.inventurelean;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

import com.thuesing.inventurelean.MainActivity.ItemData;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddProduct extends Activity implements OnClickListener {
	public final static String TAG = "InventureAddProduct";
    private static final int REQUEST_BARCODE = 0;
    private static final ItemData mProductData = new ItemData();
	private EditText mBarcodeEdit;
	private EditText mTitleEdit;
	private EditText mWeightEdit;
	private Button mScanButton;
	private Button mAddButton;
	private ProductDatabase mProductDb;

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
        
        Integer weight = getIntent().getIntExtra("com.thuesing.inventure.weight", 0);
      	Log.d(TAG, "getIntExtra " +  weight + " - thuesing");
      	mWeightEdit.setText(weight.toString());
        
        mProductDb = new ProductDatabase(this); 
        
		initUsb();
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
	              	// showInfoDialog(this, "Please fix errors", errors);  // thue: not defined
	            	Toast.makeText(getApplicationContext(), "Please fix: " + errors, Toast.LENGTH_LONG).show();
	            } else {
	                mProductData.barcode = barcode;
	                mProductData.title = title;
	                mProductData.weight = new Integer(weight);

	                mProductDb.insert(mProductData);	                
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
    
    
 	public boolean onCreateOptionsMenu(Menu menu) {
 		// Inflate the menu; this adds items to the action bar if it is present.
 		getMenuInflater().inflate(R.menu.add_product, menu);		
 		return true;
 	} 
 	
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 	    switch (item.getItemId()) {
 	    case R.id.show_data:
    		Intent intent = new Intent(AddProduct.this, ListViewActivity.class);
    		startActivity(intent); 	 	    	
 	        return true;
 	    default:
 	        return super.onOptionsItemSelected(item);
 	    }
 	}
    
    
    /*
     * Scale
     */
    


	private UsbScale mScale;
	private UsbDevice mDevice;
	private UsbManager mUsbManager;
	private UsbEndpoint mEndpoint;
	private UsbDeviceConnection mConnection;
	
	private PowerManager.WakeLock wl;
	
	private int units = 0;
	private int value = 0;
	
	
	private Handler mHandler = new Handler();
	
	private Runnable mUpdateWeight = new Runnable() {
		public void run() {
			executeUsbCommand(0);
			mHandler.postDelayed(this, 100);
		}
	};
	
	public void onResume() {
		super.onResume();
		getUsbDevice();
		mHandler.postDelayed(mUpdateWeight, 100);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		wl.acquire();

	}
	
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateWeight);
		if (wl != null) {
			 wl.release();
			 wl = null;
		}
	}
	
	@Override
	protected void onStop() {
	    super.onStop();  // Always call the superclass method first
	    if(mConnection != null) {
	    	mConnection.close();
	    }
	}


	private int getWeight() {
		return this.value;
	}
	
	public void initUsb() {
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mScale = new UsbScale();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public void getUsbDevice() {
		
		Log.d(TAG, "findingDevices");
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {

				mDevice = deviceIterator.next();
				// your code
				
				Log.d("deviceName", mDevice.getDeviceName() + " - wubbahed");
				Log.d("deviceId", mDevice.getDeviceId() + " - wubbahed");
				Log.d("deviceInterfaceCount", mDevice.getInterfaceCount()
						+ " - wubbahed");
				Log.d("deviceClass", mDevice.getDeviceClass() + " - wubbahed");
				Log.d("deviceProductId", mDevice.getProductId()	+ " - wubbahed");
				Log.d("deviceVendorId", mDevice.getVendorId() + " - wubbahed");

				UsbInterface iFace = mDevice.getInterface(0);
				Log.d("interfaceToString", iFace.toString() + " - wubbahed");

				int endpointCount = iFace.getEndpointCount();
				for (int i = 0; i < endpointCount; i++) {
					mEndpoint = iFace.getEndpoint(i);
					Log.d("endpoint", mEndpoint.toString() + " - wubbahed");
					Log.d("endpointDirection", mEndpoint.getDirection()
							+ " - wubbahed");
					Log.d("endpointType", mEndpoint.getType() + " - wubbahed");
				}

				mConnection = mUsbManager.openDevice(mDevice);
				mConnection.claimInterface(iFace, true);			

		}       

	}
	
	
	public void executeUsbCommand(int id) {
		
		if (mConnection != null) {
			
		 ByteBuffer buffer = ByteBuffer.allocate(8);
	     UsbRequest request = new UsbRequest();
	     request.initialize(mConnection, mEndpoint);
	     request.queue(buffer, 8);
	        
	     
	            int result = mConnection.controlTransfer(mScale.REQUEST_TYPE, mScale.REQUEST, mScale.VALUE, mScale.INDEX, mScale.message, mScale.message.length, 0);
	            //Log.d("result", result + " - wubbahed");
	            if (mConnection.requestWait() == request) {

					int[] measurement = mScale.getWeight(buffer);
					
					units = measurement[0];
					value = measurement[1];
					
					String data = "";
					
					if (units == 11) {
						
						if (value > 160) {
							int pounds = (value - (value % 160)) / 160;
							String ounces = String.format("%.1f", (value%160)*0.1);
							data = pounds + " lbs " + ounces + " oz";
						} else {

							String ounces = String.format("%.1f", value*0.1);
							data = ounces + " oz";
						}
					} else if (units == 2) {
						data = value + " g";
					}
					
	            	TextView t = (TextView) findViewById(R.id.textWeight);
	            	t.setText(data);

	            }
	        } else {
	        	TextView t = (TextView) findViewById(R.id.textWeight);
	        	t.setText("No scale found.");
	        }
			
	}
	
	


    
}