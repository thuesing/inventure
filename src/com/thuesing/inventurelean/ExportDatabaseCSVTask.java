package com.thuesing.inventurelean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thuesing.opencsv.CSVWriter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// see http://stackoverflow.com/questions/8724866/how-to-convert-data-base-records-into-csv-file-in-android

public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = "InventureExportTask";
	private ProgressDialog dialog; 
	private ProductDatabase db;
    private Context context;
    
    public ExportDatabaseCSVTask(Context con) {
    	 this.context = con;
    	 dialog = new ProgressDialog(con);
         db = new ProductDatabase(con);  	 
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Exporting database...");
        this.dialog.show();
    }

    protected Boolean doInBackground(final String... args) {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "InventureApp");    	
    	
    	Log.d(TAG, "Export dir: " + exportDir + " - thuesing");

        if (!exportDir.exists()) { exportDir.mkdirs(); }

        File file = new File(exportDir, "InventureApp.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = db.fetchDataAll();
            //csvWrite.writeNext(curCSV.getColumnNames());
            String cplNames[] ={ProductDatabase.KEY_TITLE,ProductDatabase.KEY_BARCODE,ProductDatabase.KEY_WEIGHT};
            while(curCSV.moveToNext()) { // 0 is _id, so start at 1
                String arrStr[] ={curCSV.getString(2),curCSV.getString(1),curCSV.getString(3)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return true;
        } catch(SQLException sqlEx) {
            Log.e("ExportDatabaseCSVTask", sqlEx.getMessage(), sqlEx);
            return false;
        } catch (IOException e) {
            Log.e("ExportDatabaseCSVTask", e.getMessage(), e);
            return false;
        }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) { this.dialog.dismiss(); }
        if (success) {
            Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}
