package com.ofcampus.ui;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.ActivityFilterJobs;
import com.ofcampus.adapter.FilterBaseAdapter;
import com.ofcampus.adapter.FilterBaseAdapter.FilterBAdpInterface;
import com.ofcampus.customseekbar.RangeSeekBar;
import com.ofcampus.customseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.ofcampus.model.Circle;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.FilterDataSet;
import com.ofcampus.model.FilterDataSets;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobList;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.FilterParser;
import com.ofcampus.parser.FilterParser.FilterParserInterface;

public class FilterDialog implements FilterBAdpInterface{
	
	
//	public static String INDUSTRY[] = { "BFSI", "Oil and Gas","Retail", "IT/ITES", "Manufacturing" };
//	public static String ROLE[] = { "Consulting", "Finance","Operations", "Marketing", "Human Resources" };
//	public static String LOCATION[] = { "New Delhi/NCR", "Mumbai","Bangalore", "Hyderabad", "Singapore" };
	
	

	private Dialog mDialog;
	private ArrayList<TextView> tab = new ArrayList<TextView>();
	
	private TextView txt_valueexp,txt_valuesal,edit_to;
	private RangeSeekBar exp_seekBar,salary_seekBar;
	
	private int selected_salarymax = 100;
	private int selected_salarymin = 1;
	private int selected_expmax = 15;
	private int selected_expmin = 1;
	
	private ArrayList<FilterDataSet> arrGeneral=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrIndustry=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrRole=new ArrayList<FilterDataSet>();
	private ArrayList<FilterDataSet> arrLoaction=new ArrayList<FilterDataSet>();
	
	private ListView filterlist;
	private LinearLayout otherOp;
	private FilterBaseAdapter mAdapter;
	private FilterDataSets mFilterDataSets;
	private Context context;
	
	public FilterDialog(Context mContext, FilterDataSets mFilterDataSets_) {
		this.mFilterDataSets=mFilterDataSets_;
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
					createFilterAPIJSON();
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
		
//		int salarymax = Integer.parseInt(mFilterDataSets.getSalary_Max());
//		int salarymin = Integer.parseInt(mFilterDataSets.getSalary_Min());
//
//		int expmax = Integer.parseInt(mFilterDataSets.getEXP_Max());
//		int expmin = Integer.parseInt(mFilterDataSets.getEXP_Min());
		
		int salarymax = 100;
		int salarymin = 1;

		int expmax = 15;
		int expmin = 1;

		salary_seekBar.setRangeValues(salarymin, salarymax);
		exp_seekBar.setRangeValues(expmin, expmax);

		txt_valueexp.setText(expmin + "Yrs" + " - " + expmax + "Yrs");
		txt_valuesal.setText(salarymin + "lpa" + " - " + salarymax + "lpa");
		 
		exp_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
	                selected_expmin=minValue;selected_expmax=maxValue;
	                txt_valueexp.setText(minValue + "Yrs"+" - "+maxValue+"Yrs");
		        }
		});
		
		salary_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
	        	selected_salarymin=minValue;selected_salarymax=maxValue; 
                txt_valuesal.setText(minValue + "lpa"+" - "+maxValue+"lpa");
	        }
		});
	}

	private void loadinitialData() {
	
		ArrayList<IndustryDetails> arrindustry =	mFilterDataSets.getArrayIndustry();
		ArrayList<CityDetails> arrCityDetails =	mFilterDataSets.getArrayCity();
		ArrayList<IndustryRoleDetails> arrRoleDetails=mFilterDataSets.getArrRoleDetails();
		ArrayList<Circle> arrCircles =	mFilterDataSets.getArraCircles();
		
		int i=0;
		if (arrCircles!=null && arrCircles.size()>=1) {
			for (Circle mCircle : arrCircles) {
				FilterDataSet mDataSet=new FilterDataSet();
				mDataSet.setID(mCircle.getCircleid());
				mDataSet.setName(mCircle.getCirclename()); 
				arrGeneral.add(mDataSet);
				i++;
			}
		}
		
		if (arrindustry!=null && arrindustry.size()>=1) {
			i=0;
			for (IndustryDetails mIndustryDetails : arrindustry) {
				FilterDataSet mDataSet=new FilterDataSet();
				mDataSet.setID(mIndustryDetails.getIndustry_id());
				mDataSet.setName(mIndustryDetails.getIndustry_name()); 
				arrIndustry.add(mDataSet);
				i++;
			}
		}
		
		if (arrCityDetails!=null && arrCityDetails.size()>=1) {
			i=0;
			for (IndustryRoleDetails mIndustryRoleDetails : arrRoleDetails) {
				FilterDataSet mDataSet=new FilterDataSet();
				mDataSet.setID(mIndustryRoleDetails.getIndustryroles_id());
				mDataSet.setName(mIndustryRoleDetails.getIndustryroles_name()); 
				arrRole.add(mDataSet);
				i++;
			}
		}
		if (arrCityDetails!=null) {
			i=0;
			for (CityDetails CityDetails : arrCityDetails) {
				FilterDataSet mDataSet=new FilterDataSet();
				mDataSet.setID(CityDetails.getCity_id());
				mDataSet.setName(CityDetails.getCity_name()); 
				arrLoaction.add(mDataSet);
				i++;
			}
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
				txt.setTextColor(Color.parseColor("#35475D"));
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
	
	
	
	private void createFilterAPIJSON(){
		String circle="";
		String locationFilter="";
		String industryFilterr="";
		String rolesFilterr="";
		String salaryFilterr=""+selected_salarymin+"~"+selected_salarymax;
		String experienceFilterr=""+selected_expmin+"~"+selected_expmax;
		String token=UserDetails.getLoggedInUser(context).getAuthtoken();
		
		
		
		
		for (FilterDataSet filterDataSet : arrGeneral) {
			if (filterDataSet.isSelected==1) {
				circle=circle+"~"+filterDataSet.getID();
			}
		}
		if (!circle.equals("")) {
			circle=circle.substring(1);
		}
		
		for (FilterDataSet filterDataSet : arrIndustry) {
			if (filterDataSet.isSelected==1) {
				industryFilterr=industryFilterr+"~"+filterDataSet.getID();
			}
		}
		if (!industryFilterr.equals("")) {
			industryFilterr=industryFilterr.substring(1);
		}
		
		for (FilterDataSet filterDataSet : arrLoaction) {
			if (filterDataSet.isSelected==1) {
				locationFilter=locationFilter+"~"+filterDataSet.getID();
			}
		}
		if (!locationFilter.equals("")) {
			locationFilter=locationFilter.substring(1);
		}
		
		for (FilterDataSet filterDataSet : arrRole) {
			if (filterDataSet.isSelected==1) {
				rolesFilterr=rolesFilterr+"~"+filterDataSet.getID();
			}
		}
		if (!rolesFilterr.equals("")) {
			rolesFilterr=rolesFilterr.substring(1);
		}
		
		if (circle.equals("") && locationFilter.equals("") && industryFilterr.equals("") && rolesFilterr.equals("")) {
			Util.ShowToast(context,context.getResources().getString(R.string.Filter_msg_chooseoption));
			return;
		}
		
		FilterParser mFilterParser=new FilterParser(); 
		JSONObject postData = mFilterParser.getBody(circle,locationFilter, industryFilterr, rolesFilterr, salaryFilterr, experienceFilterr);
		
//		Log.i("postData", postData.toString());
		
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,context.getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		mFilterParser.setFilterparserinterface(new FilterParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> jobS) {
				if (jobS!=null && jobS.size()>=1) {
					dialogFinish();
					((OfCampusApplication)context.getApplicationContext()).filterJobs=jobS;
					context.startActivity(new Intent(context,ActivityFilterJobs.class));
					((Activity)context).overridePendingTransition(0, 0);
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mFilterParser.parse(context, postData, token, true);
	}
	
	
	
	private void dialogFinish(){
		if (mDialog!=null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
