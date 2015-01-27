package com.ofcampus.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.FilterBaseAdapter;
import com.ofcampus.adapter.FilterBaseAdapter.FilterBAdpInterface;
import com.ofcampus.customseekbar.RangeSeekBar;
import com.ofcampus.customseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.ofcampus.model.FilterDataSet;

public class FilterDialog implements FilterBAdpInterface{
	
	
	public static String INDUSTRY[] = { "BFSI", "Oil and Gas","Retail", "IT/ITES", "Manufacturing" };
	public static String ROLE[] = { "Consulting", "Finance","Operations", "Marketing", "Human Resources" };
	public static String LOCATION[] = { "New Delhi/NCR", "Mumbai","Bangalore", "Hyderabad", "Singapore" };
	
	

	private Dialog mDialog;
	private ArrayList<TextView> tab = new ArrayList<TextView>();
	
	private TextView txt_valueexp,txt_valuesal,edit_to;
	private RangeSeekBar exp_seekBar,salary_seekBar;
	
	private ArrayList<FilterDataSet> arrGeneral=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrIndustry=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrRole=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrLoaction=new ArrayList<FilterDataSet>();
	
	private ListView filterlist;
	private LinearLayout otherOp;
	private FilterBaseAdapter mAdapter;
	
	private Context context;
	
	public FilterDialog(Context mContext) {
		this.context=mContext;
		mDialog = new Dialog(mContext, R.style.Theme_Dialog_Translucent);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.inflate_dialog_filter_layout);
		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		initialize();
		seekBarDataLoad();
		loadinitialData();
	}

	private void initialize() {

		
		filterlist=(ListView) mDialog.findViewById(R.id.filter_list);
		otherOp=(LinearLayout) mDialog.findViewById(R.id.filter_otheroption);
		
		exp_seekBar=(RangeSeekBar)mDialog.findViewById(R.id.activity_createjob_expseekbar);
		salary_seekBar=(RangeSeekBar)mDialog.findViewById(R.id.activity_createjob_salaryseekbar);
		
		txt_valueexp = (TextView) mDialog.findViewById(R.id.activity_createjob_txt_valueexp);
		txt_valuesal = (TextView) mDialog.findViewById(R.id.activity_createjob_txt_valuesalary);
		
		mAdapter=new FilterBaseAdapter(context, new ArrayList<FilterDataSet>(), 0);
		mAdapter.setFilterbadpinterface(this);
		filterlist.setAdapter(mAdapter);
		
		tab.add((TextView) mDialog.findViewById(R.id.filter_txt_general));
		tab.add((TextView) mDialog.findViewById(R.id.filter_txt_indus));
		tab.add((TextView) mDialog.findViewById(R.id.filter_txt_role));
		tab.add((TextView) mDialog.findViewById(R.id.filter_txt_location));
		tab.add((TextView) mDialog.findViewById(R.id.filter_txt_other));

		for (TextView textView : tab) {
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.isSelected()) {
						return;
					}
					tabClickEvent(v);
				}
			});
		}
		
		((TextView)mDialog.findViewById(R.id.filter_btn_Applyflt)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDialog!=null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
		
		((TextView)mDialog.findViewById(R.id.filter_txt_Clearflt)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClearEvent();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void seekBarDataLoad(){
		salary_seekBar.setRangeValues(1, 100);
		exp_seekBar.setRangeValues(0.5f, 15.0f);
		
		txt_valueexp.setText(1 + "Yrs"+" - "+100+"Yrs");
		txt_valuesal.setText(0.5f + "lpa"+" - "+15.0f+"lpa");
		 
		exp_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
		                // handle changed range values
	                Log.i("", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
	                txt_valueexp.setText(minValue + "Yrs"+" - "+maxValue+"Yrs");
		        }
		});
		
		salary_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
	                // handle changed range values
                Log.i("", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                txt_valuesal.setText(minValue + "lpa"+" - "+maxValue+"lpa");
	        }
		});
	}

	private void loadinitialData() {
	
		int i=0;
		for (String name : Util.sendto) {
			FilterDataSet mDataSet=new FilterDataSet();
			mDataSet.setID(""+i);
			mDataSet.setName(name); 
			arrGeneral.add(mDataSet);
			i++;
		}
		
		i=0;
		for (String name : INDUSTRY) {
			FilterDataSet mDataSet=new FilterDataSet();
			mDataSet.setID(""+i);
			mDataSet.setName(name); 
			arrIndustry.add(mDataSet);
			i++;
		}
		
		i=0;
		for (String name : ROLE) {
			FilterDataSet mDataSet=new FilterDataSet();
			mDataSet.setID(""+i);
			mDataSet.setName(name); 
			arrRole.add(mDataSet);
			i++;
		}
		
		
		i=0;
		for (String name : LOCATION) {
			FilterDataSet mDataSet=new FilterDataSet();
			mDataSet.setID(""+i);
			mDataSet.setName(name); 
			arrLoaction.add(mDataSet);
			i++;
		}
		
	}
	

	public void showDialog() {
		setSelection(0);
		filterlist.setVisibility(View.VISIBLE);
		otherOp.setVisibility(View.GONE);
		mAdapter.refreshView(arrGeneral, 0);
		mDialog.show();
	}

	private void ClearEvent(){
		
		for (FilterDataSet mDataSet : arrGeneral) {
			mDataSet.isSelected=0;
		}
		for (FilterDataSet mDataSet : arrIndustry) {
			mDataSet.isSelected=0;
		}
		for (FilterDataSet mDataSet : arrRole) {
			mDataSet.isSelected=0;
		}
		for (FilterDataSet mDataSet : arrLoaction) {
			mDataSet.isSelected=0;
		}
		
		setSelection(0);
		filterlist.setVisibility(View.VISIBLE);
		otherOp.setVisibility(View.GONE);
		mAdapter.refreshView(arrGeneral, 0);
		
		salary_seekBar.setRangeValues(1, 100);
		exp_seekBar.setRangeValues(0.5f, 15.0f);
		
		txt_valueexp.setText(1 + "Yrs"+" - "+100+"Yrs");
		txt_valuesal.setText(0.5f + "lpa"+" - "+15.0f+"lpa");
		
	}
	private void tabClickEvent(View v) {
		switch (v.getId()) {
		case R.id.filter_txt_general:
			setSelection(0);
			filterlist.setVisibility(View.VISIBLE);
			otherOp.setVisibility(View.GONE);
			mAdapter.refreshView(arrGeneral, 0);
			break;
		case R.id.filter_txt_indus:
			setSelection(1);
			filterlist.setVisibility(View.VISIBLE);
			otherOp.setVisibility(View.GONE);
			mAdapter.refreshView(arrIndustry, 1);
			break;
		case R.id.filter_txt_role:
			setSelection(2);
			filterlist.setVisibility(View.VISIBLE);
			otherOp.setVisibility(View.GONE);
			mAdapter.refreshView(arrRole, 2);
			break;
		case R.id.filter_txt_location:
			setSelection(3);
			filterlist.setVisibility(View.VISIBLE);
			otherOp.setVisibility(View.GONE);
			mAdapter.refreshView(arrLoaction, 3);
			break;
		case R.id.filter_txt_other:
			setSelection(4);
			filterlist.setVisibility(View.GONE);
			otherOp.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	private void setSelection(int position) {
		for (int i = 0; i < tab.size(); i++) {
			TextView txt = tab.get(i);
			if (position == i) {
				txt.setSelected(true);
				txt.setTextColor(Color.BLACK);
			} else {
				txt.setSelected(false);
				txt.setTextColor(Color.WHITE);
			}
		}
	}

	@Override
	public void itemClick(ArrayList<FilterDataSet> arrData, int state) {
		switch (state) {
		case 0:
			arrGeneral=new ArrayList<FilterDataSet>(arrData);
			break;
		case 1:
			arrIndustry=arrData;
			break;
		case 2:
			arrRole=arrData;
			break;
		case 3:
			arrLoaction=arrData;
			break;
		default:
			break;
		}
	}
}
