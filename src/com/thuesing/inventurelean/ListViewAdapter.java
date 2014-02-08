package com.thuesing.inventurelean;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// see http://juamir.blogspot.de/2012/12/android-multi-column-listview.html

public class ListViewAdapter extends BaseAdapter {
	    public static final String FIRST_COLUMN = "First";	
	    public static final String SECOND_COLUMN = "Second";	
	    public static final String THIRD_COLUMN = "Third";

	    public ArrayList<HashMap> list;
	    Activity activity;

	    public ListViewAdapter(Activity activity, ArrayList<HashMap> list) {
	        super();
	        this.activity = activity;
	        this.list = list;
	    }	 

	    @Override

	    public int getCount() {
	        // TODO Auto-generated method stub
	        return list.size();
	    } 

	    @Override

	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return list.get(position);
	    }

	    @Override

	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return 0;
	    }

	    private class ViewHolder {
	           TextView title;
	           TextView barcode;
	           TextView weight;
        }


	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        // TODO Auto-generated method stub

                ViewHolder holder;
                LayoutInflater inflater =  activity.getLayoutInflater();	 

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.inventur_row, null);
                    holder = new ViewHolder();
                    holder.title = (TextView) convertView.findViewById(R.id.title);
                    holder.barcode = (TextView) convertView.findViewById(R.id.barcode);
                    holder.weight = (TextView) convertView.findViewById(R.id.weight);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                } 

                HashMap map = list.get(position);
                holder.title.setText((CharSequence) map.get(FIRST_COLUMN));
                holder.barcode.setText((CharSequence) map.get(SECOND_COLUMN));
                holder.weight.setText((CharSequence) map.get(THIRD_COLUMN));
	                
	            return convertView;

	    }	 

	}