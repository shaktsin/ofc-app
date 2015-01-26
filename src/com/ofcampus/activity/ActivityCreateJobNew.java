package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.SpinnerCityAdapter;
import com.ofcampus.adapter.SpinnerIndustryAdapter;
import com.ofcampus.adapter.SpinnerIndustryRoleAdapter;
import com.ofcampus.customseekbar.RangeSeekBar;
import com.ofcampus.customseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.PrepareListForJobCreating;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.PrepareForCreatingJobParser;
import com.ofcampus.parser.PrepareForCreatingJobParser.PrepareParserInterface;

public class ActivityCreateJobNew  extends ActionBarActivity  implements OnClickListener,OnItemSelectedListener{

	private Context context;
	private UserDetails mDetails;
	private Spinner industry,role,location;
	private EditText jobheadline,jobdetails,edt_email,edt_phno,edt_whatsapp;
	private TextView txt_valueexp,txt_valuesal,edit_to;
	
	private String phno,email,whatsapp;
	
	private ArrayList<IndustryDetails> industries;
	private ArrayList<CityDetails> arrcity;
	
	private SpinnerCityAdapter mSpinnerCityAdapter;
	private SpinnerIndustryAdapter mSpinnerIndustryAdapter;
	private SpinnerIndustryRoleAdapter mSpinnerIndustryRoleAdapter;
	
	private int industryid=-1,rolid=-1,cityid=-1;
	private ArrayList<RelativeLayout> arrayRelative=new ArrayList<RelativeLayout>();
	
	private ArrayList<DataSet> arrData;
	private ListView sendtolist;
	
	@SuppressWarnings("rawtypes")
	private RangeSeekBar exp_seekBar,salary_seekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createjob_new);

		context=ActivityCreateJobNew.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Job");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		seekBarDataLoad();
		loadData();
	}

	@Override
	public void onBackPressed() {
		if (anyviewVisible()) {
			resetViewAll();
		}else {
			super.onBackPressed();
			overridePendingTransition(0, 0);
			finish();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_createjob, menu);
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_tick:
			if (!Util.hasConnection(context)) {
				Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			}else {
//				postJobEvent();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_createjob_edit_to:
			Util.HideKeyBoard(context, v); 
			if (arrayRelative.get(0).getVisibility()==View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(0);
			break;
		case R.id.activity_createjob_edit:
			Util.HideKeyBoard(context, v); 
			if (arrayRelative.get(1).getVisibility()==View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(1);
			break;
		case R.id.activity_createjob_rply:
			Util.HideKeyBoard(context, v); 
			if (arrayRelative.get(2).getVisibility()==View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(2);
			break;

		default:
			break;
		}
	}
	
	
	/* Spinner Select Items */
	@Override
	public void onItemSelected(AdapterView<?> adView, View v, int position,
			long arg3) {

		switch (adView.getId()) {
		case R.id.activity_createjob_spn_industry:
			if (position>=1) {
				industryRolesdropedLoaddata(industries.get(position-1).getIndustryRoles());
				industryid=position-1;
			}else {
				industryRolesdropedLoaddata(null);
				industryid=-1;
			}
			break;

		case R.id.activity_createjob_spn_role:
			if (position>=1) {
				rolid=position-1;
			}else {
				rolid=-1;
			}
			
			break;

		case R.id.activity_createjob_spn_location:
			if (position>=1) {
				cityid=position-1;
			}else {
				cityid=-1;
			}
			
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	
	
	@SuppressWarnings("rawtypes")
	private void initialize() {
		exp_seekBar=(RangeSeekBar)findViewById(R.id.activity_createjob_expseekbar);
		salary_seekBar=(RangeSeekBar)findViewById(R.id.activity_createjob_salaryseekbar);
		
		industry=(Spinner)findViewById(R.id.activity_createjob_spn_industry);
		role=(Spinner)findViewById(R.id.activity_createjob_spn_role);
		location=(Spinner)findViewById(R.id.activity_createjob_spn_location);

			
		jobheadline = (EditText) findViewById(R.id.activity_createjob_edit_jobtitle);
		jobdetails = (EditText) findViewById(R.id.activity_createjob_edit_jobdescrip);
		
		edt_email = (EditText) findViewById(R.id.activity_create_edt_emailreply);
		edt_phno = (EditText) findViewById(R.id.activity_create_edt_phone);
		edt_whatsapp = (EditText) findViewById(R.id.activity_create_edt_whatsapp);
		
		
		txt_valueexp = (TextView) findViewById(R.id.activity_createjob_txt_valueexp);
		txt_valuesal = (TextView) findViewById(R.id.activity_createjob_txt_valuesalary);
		
		edit_to = (TextView)findViewById(R.id.activity_createjob_edit_to);
		edit_to.setOnClickListener(this);
		((ImageView)findViewById(R.id.activity_createjob_attached)).setOnClickListener(this);
		((ImageView)findViewById(R.id.activity_createjob_edit)).setOnClickListener(this);
		((ImageView)findViewById(R.id.activity_createjob_rply)).setOnClickListener(this);
		
		sendtolist=(ListView)findViewById(R.id.activity_createjob_new_sendtolist);
		sendtolist.setAdapter(new SendToBaseAdapter(context, createDataSetForSendTo()));
		
		((RelativeLayout)findViewById(R.id.rel_rplview_main)).setOnClickListener(this);
		((RelativeLayout)findViewById(R.id.rel_additional_main)).setOnClickListener(this);
		
		arrayRelative.add((RelativeLayout)findViewById(R.id.rel_tolist));
		arrayRelative.add((RelativeLayout)findViewById(R.id.rel_detailadd));
		arrayRelative.add((RelativeLayout)findViewById(R.id.rel_reply));
		setClicEvent();
	}
	
	private void setClicEvent(){
		for (RelativeLayout rel: arrayRelative) {
			rel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (anyviewVisible()) {
						resetViewAll();
					}
				}
			});
		}
	}
	private void loadForntView(int position){
		for (int i = 0; i < arrayRelative.size(); i++) {
			if (i==position) {
				arrayRelative.get(i).setVisibility(View.VISIBLE);
			}else {
				arrayRelative.get(i).setVisibility(View.GONE);
			}
		} 
	}
	
	private void resetViewAll(){
		for (RelativeLayout rel: arrayRelative) {
			rel.setVisibility(View.GONE);
		}
	}
	
	private boolean anyviewVisible(){
		boolean isvisible=false;
		for (RelativeLayout rel: arrayRelative) {
			isvisible = (rel.getVisibility()==View.VISIBLE)?true:false;
			if (isvisible)
				break;
		}
		return isvisible;
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
	
	
	private void loadData(){
		mDetails=UserDetails.getLoggedInUser(context);
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));  
			return;
		}
		edt_email.setText(mDetails.getEmail());
		
		PrepareForCreatingJobParser mCreating=new PrepareForCreatingJobParser();
		mCreating.setPrepareparserinterface(new PrepareParserInterface() {
			
			@Override
			public void OnSuccess(PrepareListForJobCreating mPrepareListForJobCreating) {
				if (mPrepareListForJobCreating!=null) {
					loadDropeDownListData(mPrepareListForJobCreating);
				}
			}
			
			@Override
			public void OnError() {
				onBackPressed();
			}
		});
		mCreating.parse(context, mDetails.getAuthtoken());
		
	}
	
	
	
	private void loadDropeDownListData(PrepareListForJobCreating mPrepareListForJobCreating){

		arrcity =mPrepareListForJobCreating.getCitys();
		industries = mPrepareListForJobCreating.getIndustrys();
		
		String eml=mPrepareListForJobCreating.getReplyEmail();
		edt_email.setText((eml!=null && !eml.equals("null") && !eml.equals(""))?eml:"");
		String ph=mPrepareListForJobCreating.getReplyPhone();
		edt_phno.setText((ph!=null && !ph.equals("null") && !ph.equals(""))?ph:"");
		String wthp=mPrepareListForJobCreating.getReplyWatsApp();
		edt_whatsapp.setText((wthp!=null && !wthp.equals("null") && !wthp.equals(""))?wthp:"");
		
		
		industrydropedLoaddata(industries);
		citydropedLoaddata(arrcity);
	}
	
	
	
	
	private void industrydropedLoaddata(ArrayList<IndustryDetails> Industrys_){
		ArrayList<IndustryDetails> Industrys = new ArrayList<IndustryDetails>();
		
		IndustryDetails mIndustryDetails=new IndustryDetails();
		mIndustryDetails.setIndustry_id(-1 + "");
		mIndustryDetails.setIndustry_name("Select Industry");
		Industrys.add(mIndustryDetails);
		
		if (Industrys_ != null && Industrys_.size() >= 1) {
			Industrys.addAll(Industrys_);
			ArrayList<IndustryRoleDetails> IndustryRoles_=Industrys_.get(0).getIndustryRoles();
			if (IndustryRoles_!=null && IndustryRoles_.size()>=1) {
				industryRolesdropedLoaddata(IndustryRoles_);
			}else {
				industryRolesdropedLoaddata(null);
			}
			
		}	
		mSpinnerIndustryAdapter = new SpinnerIndustryAdapter(context, Industrys);
		industry.setAdapter(mSpinnerIndustryAdapter); 
		industry.setSelection(0, false);
		industry.setOnItemSelectedListener(this);
	}

	private void industryRolesdropedLoaddata(ArrayList<IndustryRoleDetails> IndustryRoles_){
		ArrayList<IndustryRoleDetails> IndustryRoles = new ArrayList<IndustryRoleDetails>();
		
		IndustryRoleDetails mIndustryDetails=new IndustryRoleDetails();
		mIndustryDetails.setIndustryroles_id(-1 + "");
		mIndustryDetails.setIndustryroles_name("Select Industry Roles");
		IndustryRoles.add(mIndustryDetails);
		
		if (IndustryRoles_ != null && IndustryRoles_.size() >= 1) {
			IndustryRoles.addAll(IndustryRoles_);
		}	
		mSpinnerIndustryRoleAdapter = new SpinnerIndustryRoleAdapter(context,IndustryRoles);
		role.setAdapter(mSpinnerIndustryRoleAdapter);
		role.setSelection(0, false);
		role.setOnItemSelectedListener(this);
	}

	private void citydropedLoaddata(ArrayList<CityDetails> citys) {

		ArrayList<CityDetails> arrcity = new ArrayList<CityDetails>();

		CityDetails mCityDetails = new CityDetails();
		mCityDetails.setCity_id(-1 + "");
		mCityDetails.setCity_name("Select City");
		mCityDetails.setCity_selected(true + "");
		arrcity.add(mCityDetails);
		
		if (citys != null && citys.size() >= 1) {
			arrcity.addAll(citys);
		}

		mSpinnerCityAdapter = new SpinnerCityAdapter(context, arrcity);
		location.setAdapter(mSpinnerCityAdapter);
		location.setSelection(0, false);
		location.setOnItemSelectedListener(this);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/******************Send to Adapter*********************/
	
	
	private ArrayList<DataSet> createDataSetForSendTo(){
		String[] arrayData=Util.sendto;
		
		arrData=new ArrayList<DataSet>();
		for (String name : arrayData) {
			DataSet mDataSet=new DataSet();
			mDataSet.isSelected=0;
			mDataSet.name = name;
			arrData.add(mDataSet);
		}
		return arrData;
	}
	
	class DataSet{
		public String name;
		public int isSelected;
	}
	private class SendToBaseAdapter extends BaseAdapter {
		
		private ArrayList<DataSet> arrData;
		private LayoutInflater inflater;
		private Context mContext;
		
		public SendToBaseAdapter(Context context,ArrayList<DataSet> Datas){
			this.mContext=context;
			this.arrData=Datas;
			this.inflater=LayoutInflater.from(mContext);
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
				convertView.setTag(mViewHolder);
			}else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			
			final DataSet mDataSet = arrData.get(position);
			
			mViewHolder.txt_send.setText(mDataSet.name);
			mViewHolder.txt_send.setSelected((mDataSet.isSelected==1)?true:false);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					mDataSet.isSelected=((mDataSet.isSelected==1)?0:1);
					try {
						String to="";
						edit_to.setText("");
						for (DataSet data : arrData) {
							if (data.isSelected==1) {
								to=to+","+data.name;
							}
						}
						if (to.equals("")) {
							edit_to.setText("To : ");
						}else {
							edit_to.setText("To : "+to.substring(1));
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
		
		class ViewHolder{
			TextView txt_send;
		}
	}
	

}