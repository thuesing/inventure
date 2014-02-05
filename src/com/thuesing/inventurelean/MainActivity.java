package com.thuesing.inventurelean;

import java.math.BigDecimal;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addButton = (Button) findViewById(R.id.addMenuButton);
        addButton.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
        });
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
	

}
