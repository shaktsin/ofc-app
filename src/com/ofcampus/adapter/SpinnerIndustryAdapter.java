/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ofcampus.model.IndustryDetails;

public class SpinnerIndustryAdapter  implements SpinnerAdapter {

	 private Context context;
	 private LayoutInflater inflater;
	 
	    /**
	     * The internal data (the ArrayList with the Objects).
	     */
	    private ArrayList<IndustryDetails> data;

	    public SpinnerIndustryAdapter(Context context, ArrayList<IndustryDetails> data){
	        this.context = context;
	        this.data = data;
	        this.inflater=LayoutInflater.from(context);
	    }
	    
	    public void refreshView(ArrayList<IndustryDetails> DATA_) {
	    	  this.data.clear();
	    	  this.data=DATA_;
	    	  this.notifyAll();
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
	            convertView.setBackgroundColor(Color.WHITE);
	        }
	        TextView txt=(TextView) convertView;
//	        txt.setPadding(10, 10, 10, 10);
	        txt.setTextColor(Color.parseColor("#1F497D"));
	        txt.setText(data.get(position).getIndustry_name());
	        return convertView;

	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
	        textView.setTextColor(Color.parseColor("#1F497D"));
//	        textView.setPadding(10, 10, 10, 10);
	        textView.setTypeface(null, Typeface.NORMAL);
	        textView.setText(data.get(position).getIndustry_name());
	        return textView;
	    }

	    @Override
	    public void registerDataSetObserver(DataSetObserver observer) {

	    }

	    @Override
	    public void unregisterDataSetObserver(DataSetObserver observer) {

	    }
	    class ViewHolder{
			TextView txt_send;
			ImageView chk_box;
		}
	
}
