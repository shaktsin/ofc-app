package com.ofcampus.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.SpinnerCityAdapter;
import com.ofcampus.adapter.SpinnerIndustryAdapter;
import com.ofcampus.adapter.SpinnerIndustryRoleAdapter;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.PrepareListForJobCreating;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.JobPostParser;
import com.ofcampus.parser.JobPostParser.JobPostParserInterface;
import com.ofcampus.parser.PrepareForCreatingJobParser;
import com.ofcampus.parser.PrepareForCreatingJobParser.PrepareParserInterface;

public class ActivityCreateJob  extends ActionBarActivity implements OnClickListener,OnItemSelectedListener{

	private Context mContext;
	private UserDetails mDetails;
	private Spinner industry,role,location;
	private EditText Experienceto, Experiencefrom, salaryrangeto,
			salaryrangefrom, jobheadline, jobdetails, edt_email, edt_phno, edt_whatsapp; 
	private TextView txt_email,txt_ph,txt_whatsapp;
	
	private String phno,email,whatsapp;
	private ArrayList<IndustryDetails> industries;
	private ArrayList<CityDetails> arrcity;
	
	private SpinnerCityAdapter mSpinnerCityAdapter;
	private SpinnerIndustryAdapter mSpinnerIndustryAdapter;
	private SpinnerIndustryRoleAdapter mSpinnerIndustryRoleAdapter;
	
	private int industryid=-1,rolid=-1,cityid=-1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createjob);

		mContext=ActivityCreateJob.this;
		initilize();
		loadData();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
		finish();
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
			if (!Util.hasConnection(mContext)) {
				Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg)); 
			}else {
				postJobEvent();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_createjob_txt_email:
			if (edt_email.getText().toString().equals("")) {
				return;
			}
			if (txt_email.isSelected()) {
				txt_email.setSelected(false);
			} else {
				txt_email.setSelected(true);
			}
			
			break;
		case R.id.activity_createjob_txt_phno:
			if (edt_phno.getText().toString().equals("")) {
				return;
			}
			if (edt_phno.isSelected()) {
				edt_phno.setSelected(false);
			}else {
				edt_phno.setSelected(true);
			}
			
			break;
		case R.id.activity_createjob_txt_whatsapp:
			if (txt_whatsapp.getText().toString().equals("")) {
				return;
			}
			if (txt_whatsapp.isSelected()) {
				txt_whatsapp.setSelected(false);
			}else {
				txt_whatsapp.setSelected(true);
			}
			
			break;
		case R.id.activity_createjob_txt_uploadjob:
//			if (!Util.hasConnection(mContext)) {
//				Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg)); 
//				return;
//			}
//			postJobEvent();
			
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
	
	
	private void initilize() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("CreateJob");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		industry=(Spinner)findViewById(R.id.activity_createjob_spn_industry);
		role=(Spinner)findViewById(R.id.activity_createjob_spn_role);
		location=(Spinner)findViewById(R.id.activity_createjob_spn_location);
		
		Experienceto = (EditText) findViewById(R.id.activity_createjob_edit_Experienceto);
		Experiencefrom = (EditText) findViewById(R.id.activity_createjob_edit_Experiencefrom);
		salaryrangeto = (EditText) findViewById(R.id.activity_createjob_edit_salaryrangeto);
		salaryrangefrom = (EditText) findViewById(R.id.activity_createjob_edit_salaryrangefrom);
		
		jobheadline = (EditText) findViewById(R.id.activity_createjob_edit_jobheadline);
		jobdetails = (EditText) findViewById(R.id.activity_createjob_edit_jobdetails);
		edt_email = (EditText) findViewById(R.id.activity_createjob_edit_email);
		edt_phno = (EditText) findViewById(R.id.activity_createjob_edit_phno);
		edt_whatsapp = (EditText) findViewById(R.id.activity_createjob_edit_whatsapp);
		
		((TextView) findViewById(R.id.activity_createjob_txt_uploadjob)).setOnClickListener(this);;
		txt_email = (TextView) findViewById(R.id.activity_createjob_txt_email);
		txt_ph = (TextView) findViewById(R.id.activity_createjob_txt_phno);
		txt_whatsapp = (TextView) findViewById(R.id.activity_createjob_txt_whatsapp);
		txt_email.setOnClickListener(this);
		txt_ph.setOnClickListener(this);
		txt_whatsapp.setOnClickListener(this);
		
	}
	
	private void loadData(){
		mDetails=UserDetails.getLoggedInUser(mContext);
		
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
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
		mCreating.parse(mContext, mDetails.getAuthtoken());
		
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
		mSpinnerIndustryAdapter = new SpinnerIndustryAdapter(mContext, Industrys);
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
		mSpinnerIndustryRoleAdapter = new SpinnerIndustryRoleAdapter(mContext,IndustryRoles);
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

		mSpinnerCityAdapter = new SpinnerCityAdapter(mContext, arrcity);
		location.setAdapter(mSpinnerCityAdapter);
		location.setSelection(0, false);
		location.setOnItemSelectedListener(this);
	}

	
	private void postJobEvent(){
		String industry="";
		String industryrole="";
		String location="";
		
		String email_="";
		String ph_="";
		String whats_="";
		
		String experiencto=Experienceto.getText().toString();
		String experiencfrom=Experiencefrom.getText().toString();
		String salaryto=salaryrangeto.getText().toString();
		String salaryfrom=salaryrangefrom.getText().toString();
		String headline=jobheadline.getText().toString();
		String headlinedetails=jobdetails.getText().toString();
		
		
		
		if (industryid==-1) {
			Util.ShowToast(mContext, "Please select Industry.");
			return;
		}else {
			industry=industries.get(industryid).getIndustry_id();
		}
		
		if (rolid==-1) {
			Util.ShowToast(mContext, "Please select Role.");
			return;
		}else {
			industryrole=industries.get(industryid).getIndustryRoles().get(rolid).getIndustryroles_id();
		}
		
		
		if (experiencto.equals("")) {
			Util.ShowToast(mContext, "Please fill the Experience to.");
			return;
		}
		
		if (experiencfrom.equals("")) {
			Util.ShowToast(mContext, "Please fill the Experience from.");
			return;
		}
		
		if(!(Integer.parseInt(experiencfrom)>=Integer.parseInt(experiencto))){
			Util.ShowToast(mContext, "Experienc from value should be greater then the Experienc to value");
			return;
		}
		
		
		if (salaryto.equals("")) {
			Util.ShowToast(mContext, "Please fill the salary to.");
			return;
		}
		
		if (salaryfrom.equals("")) {
			Util.ShowToast(mContext, "Please fill the salary from.");
			return;
		}
		
		if(!(Integer.parseInt(salaryfrom)>=Integer.parseInt(salaryto))){
			Util.ShowToast(mContext, "From Salary value should be greater then the To Salary value");
			return;
		}
		
		if (cityid==-1) {
			Util.ShowToast(mContext, "Please select city.");
			return;
		}else {
			location=arrcity.get(cityid).getCity_id();
		}
		
		if (headline.equals("")) {
			Util.ShowToast(mContext, "Please fill Job Headline.");
			return;
		}
		
		if (headline.length()>500) {
			Util.ShowToast(mContext, "Job Headline exit limit.");
			return;
		}
		
		if (headlinedetails.equals("")) {
			Util.ShowToast(mContext, "Please fill Job Details.");
			return;
		}
		
		if (headlinedetails.length() > 65535) {
			Util.ShowToast(mContext, "Job Details exit limit.");
			return;
		}

		if (txt_email.isSelected()) {
			email_=edt_email.getText().toString();
		}
		
		if (txt_ph.isSelected()) {
			ph_=edt_phno.getText().toString();
		}
		
		if (txt_whatsapp.isSelected()) {
			whats_=edt_whatsapp.getText().toString();
		}
		
		JSONObject jsObj = getBody(industry,industryrole,location,experiencto,experiencfrom,salaryto,salaryfrom,headline,headlinedetails,email_,ph_,whats_);
	
		if (jsObj != null) {
			JobPostParser mJobPostParser = new JobPostParser();
			mJobPostParser.setJobpostparserinterface(new JobPostParserInterface() {
				
				@Override
				public void OnSuccess(JobDetails mJobDetails) {
					if (mJobDetails!=null) {
						((OfCampusApplication)mContext.getApplicationContext()).jobdetails=mJobDetails;
						Intent mIntent = new Intent(mContext,ActivityComment.class);
						Bundle mBundle=new Bundle();
						mBundle.putString("key_dlorcmt", Util.TOOLTITLE[1]);
						mIntent.putExtras(mBundle);
						startActivity(mIntent);
						((Activity) mContext).overridePendingTransition(0, 0); 
						onBackPressed();
					}
					
				}
				
				@Override
				public void OnError() {
					
				}
			});
			mJobPostParser.parse(mContext, jsObj, mDetails.getAuthtoken());

		}
	
	}

	
	
	private JSONObject getBody(String industry, String industryrole,
			String location, String experiencto, String experiencfrom,
			String salaryto, String salaryfrom, String headline,
			String headlinedetails, String email_, String ph_, String whats_) {

		
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("timeSpecified", "true");
			jsObj.put("to", experiencto);
			jsObj.put("from", experiencfrom);
			jsObj.put("salarySpecified", "true");
			
			jsObj.put("salaryTo", salaryto);
			jsObj.put("salaryFrom", salaryfrom);
			jsObj.put("subject", headline);
			jsObj.put("content", headlinedetails);
			
			jsObj.put("replyEmail", email_);
			jsObj.put("replyPhone", ph_);
			jsObj.put("replyWatsApp", whats_);
			
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			
			
			JSONObject obj=new JSONObject();
			obj.put("shareEmail", "-1");
			obj.put("sharePhone", "-1");
			obj.put("shareWatsApp", "-1");
			jsObj.put("shareDto", obj);
			
			
			JSONArray indussArray=new JSONArray();
			for (int i = 0; i <1; i++) {
				indussArray.put(i, industryid);
			}
			
			JSONArray RolesArray=new JSONArray();
			for (int i = 0; i <1; i++) {
				RolesArray.put(i, industryrole);
			}
			
			JSONArray locaArray=new JSONArray();
			for (int i = 0; i <1; i++) {
				locaArray.put(i, location);
			}
			
//			jsObj.put("industryIdList", indussArray);
			jsObj.put("industryRolesIdList", RolesArray);
			jsObj.put("locationIdList", locaArray);
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
	
	
	

	
	
	
	
	
}

