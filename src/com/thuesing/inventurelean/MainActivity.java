package com.thuesing.inventurelean;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public final static String TAG = "InventureMain";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		initButtons();
		initUsb();
		updateVisuals();        
    }
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	static final class ItemData {
	    String barcode;
	    String title;
	    Integer weight;
	    String created_at;
	}
	
	
	/* 
	 * Scale running 
	 * 
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
		//mHandler.removeCallbacks(mUpdateWeight);
		if (wl != null) {
			 wl.release();
			 wl = null;
		}
	}

	private void initButtons() {
		/*
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "ground_beef", Toast.LENGTH_LONG).show();
			}
		});
		*/
        Button addButton = (Button) findViewById(R.id.addProductButton);
        addButton.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {    
            	int weight = getWeight();
            	Log.d(TAG, "putExtra " + weight + " - thuesing");
            	Intent i = new Intent(MainActivity.this, AddProduct.class);
            	i.putExtra("com.thuesing.inventure.weight", weight);            	
                startActivity(i);
            }
        });	
	
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
		
		//Log.d("findingDevices", " - wubbahed");
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
	
	private void updateVisuals() {
		
	}
	
	

}
