package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ofcampus.model.InstituteDetails;

public class SpinnerBaseAdapter implements SpinnerAdapter {

	 private Context context;
	    /**
	     * The internal data (the ArrayList with the Objects).
	     */
	    private ArrayList<InstituteDetails> data;

	    public SpinnerBaseAdapter(Context context, ArrayList<InstituteDetails> data){
	        this.context = context;
	        this.data = data;
	    }

	    /**
	     * Returns the Size of the ArrayList
	     */
	    @Override
	    public int getCount() {
	        return data.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return data.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return 0;
	    }

	    @Override
	    public int getItemViewType(int position) {
	        return 0;
	    }

	    @Override
	    public int getViewTypeCount() {
	        return 1;
	    }

	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }

	    @Override
	    public boolean isEmpty() {
	        return false;
	    }
	    
	    @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
//	            convertView.setBackgroundColor(Color.parseColor("#E5E5E5"));
	        }
	        TextView txt=(TextView) convertView;
//	        txt.setPadding(10, 10, 10, 10);
	        txt.setTextColor(Color.parseColor("#1F497D"));
	        txt.setText(data.get(position).getNm());
	        return convertView;

	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
	        textView.setTextColor(Color.parseColor("#1F497D"));
//	        textView.setPadding(10, 10, 10, 10);
	        textView.setTypeface(null, Typeface.NORMAL);
	        textView.setText(data.get(position).getNm());
	        return textView;
	    }


	    @Override
	    public void registerDataSetObserver(DataSetObserver observer) {

	    }

	    @Override
	    public void unregisterDataSetObserver(DataSetObserver observer) {

	    }

	
}
