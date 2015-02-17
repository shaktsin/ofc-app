package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.model.FilterDataSet;

public class FilterBaseAdapter extends BaseAdapter {
	
	private ArrayList<FilterDataSet> arrData;
	private LayoutInflater inflater;
	private int state=-1;
	private Context mContext;
	
	public FilterBaseAdapter(Context context,ArrayList<FilterDataSet> Datas,int State){
		this.mContext=context;
		this.arrData=Datas;
		this.state=State;
		this.inflater=LayoutInflater.from(mContext);
	}
	
	public void refreshView(ArrayList<FilterDataSet> Datas_,int State){
		this.state=State;
		this.arrData=Datas_;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return arrData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder mViewHolder;
		if (convertView==null) {
			mViewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.activity_createjob_new_sendto_row, null);
			mViewHolder.txt_send=(TextView)convertView.findViewById(R.id.activity_createjob_new_txt_send);
			mViewHolder.chk_box=(ImageView)convertView.findViewById(R.id.checkBox);
			convertView.setTag(mViewHolder);
		}else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		final FilterDataSet mDataSet = arrData.get(position);
		
		mViewHolder.txt_send.setText(mDataSet.getName());
		mViewHolder.chk_box.setSelected((mDataSet.isSelected==1)?true:false);
	
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mDataSet.isSelected = ((mDataSet.isSelected==0) ? 1 : 0);
				if (filterbadpinterface!=null) {
					filterbadpinterface.itemClick(arrData,state);
				}
				notifyDataSetChanged();
			}
		});
		
		return convertView;
	}
	
	class ViewHolder{
		TextView txt_send;
		ImageView chk_box;
	}
	
	public FilterBAdpInterface filterbadpinterface;

	public FilterBAdpInterface getFilterbadpinterface() {
		return filterbadpinterface;
	}

	public void setFilterbadpinterface(FilterBAdpInterface filterbadpinterface) {
		this.filterbadpinterface = filterbadpinterface;
	}

	public interface FilterBAdpInterface {
		public void itemClick(ArrayList<FilterDataSet> arrData, int state);  
	}
}
