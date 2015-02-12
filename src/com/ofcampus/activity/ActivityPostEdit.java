package com.ofcampus.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.SpinnerCityAdapter;
import com.ofcampus.adapter.SpinnerIndustryAdapter;
import com.ofcampus.adapter.SpinnerIndustryRoleAdapter;
import com.ofcampus.customseekbar.RangeSeekBar;
import com.ofcampus.customseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.ofcampus.model.Circle;
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

public class ActivityPostEdit extends ActionBarActivity implements OnClickListener,OnItemSelectedListener{

	public int GALLERY_REQUEST = 1;
	private Context context;
	private UserDetails mDetails;
	private Spinner industry,role,location;
	private EditText jobheadline,jobdetails,edt_email,edt_phno,edt_whatsapp;
	private TextView txt_valueexp,txt_valuesal,edit_to;
	
	private String phno,email,whatsapp;
	
	private ArrayList<IndustryDetails> industries;
	private ArrayList<CityDetails> arrcity;
	private ArrayList<Circle> circlelist ;
	
	private SpinnerCityAdapter mSpinnerCityAdapter;
	private SpinnerIndustryAdapter mSpinnerIndustryAdapter;
	private SpinnerIndustryRoleAdapter mSpinnerIndustryRoleAdapter;
	
	private int industryid=-1,rolid=-1,cityid=-1;
	private ArrayList<RelativeLayout> arrayRelative=new ArrayList<RelativeLayout>();
	
	private ListView sendtolist;
	private SendToBaseAdapter mSendToBaseAdapter;
	@SuppressWarnings("rawtypes")
	private RangeSeekBar exp_seekBar,salary_seekBar;
	
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	private HorizontalListView mHlvCustomList;
	private CustomArrayAdapter mCustomArrayAdapter;
	private ArrayList<PicDataSet> picdatasets=new ArrayList<PicDataSet>();
	
	
	/***Post Job Details**/
	private JobDetails mJobDetails;
	private String JObID="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createjob_new);

		context=ActivityPostEdit.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Job");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		seekBarDataLoad();
		loadJobData();
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
//			if (!Util.hasConnection(context)) {
//				Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
//			}else {
//				resetViewAll();
//				postJobEvent();
//			}
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
		case R.id.activity_createjob_attached:
			Util.HideKeyBoard(context, v); 
			resetViewAll();
			mHlvCustomList.setVisibility((mHlvCustomList.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE);		
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
		
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_profilepic)
		.showImageForEmptyUri(R.drawable.ic_profilepic)
		.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		mHlvCustomList = (HorizontalListView) findViewById(R.id.hlvCustomList);
		mCustomArrayAdapter=new CustomArrayAdapter(context, getpicArray(null));
		mHlvCustomList.setAdapter(mCustomArrayAdapter);
		
		
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
		mSendToBaseAdapter = new SendToBaseAdapter(context, new ArrayList<Circle>());
		sendtolist.setAdapter(mSendToBaseAdapter);
		
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
		resetList();
	}
	
	private void resetViewAll(){
		for (RelativeLayout rel: arrayRelative) {
			rel.setVisibility(View.GONE);
		}
	}
	
	private void resetList(){
		mHlvCustomList.setVisibility(View.GONE);
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
	
	
	private String exp_min="",exp_max="",salary_min="",salary_max="";
	
	
	@SuppressWarnings("unchecked")
	private void seekBarDataLoad(){
		salary_seekBar.setRangeValues(1, 100);
		exp_seekBar.setRangeValues(0.5f, 15.0f);
		
		txt_valueexp.setText(0.5f + "Yrs"+" - "+15.0f+"Yrs");
		txt_valuesal.setText( 1+ "lpa"+" - "+100+"lpa");
		 
		exp_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {

	                exp_min=(int)Math.round(minValue)+"";exp_max=(int)Math.round(maxValue)+"";
	                txt_valueexp.setText(minValue + "Yrs"+" - "+maxValue+"Yrs");
		        }
		});
		
		salary_seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
	        @Override
	        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
	        	
	        	salary_min=minValue+"";salary_max=maxValue+"";
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
//		edt_email.setText(mDetails.getEmail());
		
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

	private void loadJobData() {
		mJobDetails = ((OfCampusApplication) getApplication()).jobdetails;		
		JObID=mJobDetails.getPostid();

		jobheadline.setText(mJobDetails.getSubject());
		jobdetails.setText(mJobDetails.getContent());
		
		edt_email.setText(mJobDetails.getReplyEmail());
		edt_phno.setText(mJobDetails.getReplyPhone());
		edt_whatsapp.setText(mJobDetails.getReplyWatsApp());
		
		mCustomArrayAdapter.RefreshImage(getJobPicArray());
	}
	
	
	private void loadDropeDownListData(PrepareListForJobCreating mPrepareListForJobCreating){

		arrcity =mPrepareListForJobCreating.getCitys();
		industries = mPrepareListForJobCreating.getIndustrys();
		circlelist = mPrepareListForJobCreating.getCirclelist();
		
		String eml=mPrepareListForJobCreating.getReplyEmail();
		edt_email.setText((eml!=null && !eml.equals("null") && !eml.equals(""))?eml:"");
		String ph=mPrepareListForJobCreating.getReplyPhone();
		edt_phno.setText((ph!=null && !ph.equals("null") && !ph.equals(""))?ph:"");
		String wthp=mPrepareListForJobCreating.getReplyWatsApp();
		edt_whatsapp.setText((wthp!=null && !wthp.equals("null") && !wthp.equals(""))?wthp:"");
		
		
		industrydropedLoaddata(industries);
		citydropedLoaddata(arrcity);
		if (circlelist!=null && circlelist.size()>=1) {
			mSendToBaseAdapter.refreshView(circlelist);
		}
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


	private class SendToBaseAdapter extends BaseAdapter {
		
		private ArrayList<Circle> circleList;  
		private LayoutInflater inflater;
		private Context mContext;
		
		public SendToBaseAdapter(Context context,ArrayList<Circle> circleList_){
			this.mContext=context;
			this.circleList=circleList_;
			this.inflater=LayoutInflater.from(mContext);
		}
		
		public void refreshView(ArrayList<Circle> circleList_){
			this.circleList=circleList_;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return circleList.size();
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
			
			final Circle mCircle = circleList.get(position); 
			
			mViewHolder.txt_send.setText(mCircle.getCirclename());
			mViewHolder.chk_box.setSelected((mCircle.isTick==1)?true:false);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mCircle.isTick = ((mCircle.isTick==0) ? 1 : 0);
					try {
						String to="";
						edit_to.setText("");
						for (Circle data : circleList) {
							if (data.isTick==1) {
								to=to+","+data.getCirclename();
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
			ImageView chk_box;
		}
	}
	
	
	
	/** An array adapter that knows how to render views when given CustomData classes */
	
	
	private ArrayList<PicDataSet> getpicArray(PicDataSet mPicDataSet){
		ArrayList<PicDataSet> mList =new ArrayList<PicDataSet>();
		if (mPicDataSet==null) {
			mList.add(new PicDataSet());
		}
		return mList;
	}
	
	private ArrayList<PicDataSet> getJobPicArray(){
		ArrayList<PicDataSet> mList =new ArrayList<PicDataSet>();
		ArrayList<String> images = mJobDetails.getImages();
		
		mList.add(new PicDataSet());
		
		if (images!=null && images.size()>=1) {
			for (String Path_ :images) {
				PicDataSet mPicDataSet=new PicDataSet();
				mPicDataSet.path=Path_;
				mList.add(mPicDataSet);
			}
		}
		return mList;
	}
	
	
	
	public class CustomArrayAdapter extends ArrayAdapter<PicDataSet> {
	   

		private LayoutInflater mInflater;
	    private ArrayList<PicDataSet> PicDataSets;
	    public CustomArrayAdapter(Context context,ArrayList<PicDataSet> PicDataSets_) {
	        super(context, R.layout.inflate_createjob_pic, PicDataSets_);
	        this.PicDataSets=PicDataSets_;
	        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
		public int getCount() {
			return PicDataSets.size();
		}
	    
	    
	    public void addImage(ArrayList<PicDataSet> picdatasets_){
	    	this.PicDataSets.addAll(picdatasets_);
	    	picdatasets=this.PicDataSets;
	    	notifyDataSetChanged();
	    }
	    
	    public void RefreshImage(ArrayList<PicDataSet> picdatasets_){
	    	this.PicDataSets=picdatasets_;
	    	notifyDataSetChanged();
	    }
	    
	    
	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	        Holder holder;
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
	            holder = new Holder();
	            holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
	            holder.pic_cross = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi_cross);
	            convertView.setTag(holder);
	        } else {
	            holder = (Holder) convertView.getTag();
	        }

	        PicDataSet mDataSet = PicDataSets.get(position);
	        String path=mDataSet.path;
	        holder.pic_cross.setVisibility(View.GONE);
			if (path.equals("")) {
				holder.pic.setImageResource(R.drawable.ic_plus);
				holder.pic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						galleryCalling();
					}
				});
			}else if (path.contains("http://") || path.contains("https://")) {
				 imageLoader.displayImage(path, holder.pic, options);
				 holder.pic_cross.setVisibility(View.VISIBLE);
				 holder.pic_cross.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PicDataSets.remove(position);
						notifyDataSetChanged();
					}
				});
			}else {
				imageLoader.displayImage("file://"+path, holder.pic, options);
			}
			 
			 
			 
	        return convertView;
	    }

	    /** View holder for the views we need access to */
	    private  class Holder {
	        public ImageView pic,pic_cross;
	    }
	}

	class PicDataSet{
		String path="";
	}
	
	private void galleryCalling(){
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, GALLERY_REQUEST);
	}
	
	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor lCursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			lCursor.moveToFirst();

			int lColumnIndex = lCursor.getColumnIndex(filePathColumn[0]);
			String lpicturePath = lCursor.getString(lColumnIndex);
			
			PicDataSet mPicDataSet=new PicDataSet();
			mPicDataSet.path=lpicturePath;
			ArrayList<PicDataSet> arrData= new ArrayList<PicDataSet>();
			arrData.add(mPicDataSet);
			mCustomArrayAdapter.addImage(arrData);
		}

	}
	
	private void postJobEvent(){
		String industry="";
		String industryrole="";
		String location="";
		
		String email_=edt_email.getText().toString().trim();
		String ph_=edt_phno.getText().toString().trim();
		String whats_=edt_whatsapp.getText().toString().trim();
		
		String experiencto=exp_min;
		String experiencfrom=exp_max;
		String salaryto=salary_min;
		String salaryfrom=salary_max;
		String headline=jobheadline.getText().toString();
		String headlinedetails=jobdetails.getText().toString();
		
		if (industryid==-1) {
			Util.ShowToast(context, "Please select Industry.");
			return;
		}else {
			industry=industries.get(industryid).getIndustry_id();
		}
		
		if (rolid==-1) {
			Util.ShowToast(context, "Please select Role.");
			return;
		}else {
			industryrole=industries.get(industryid).getIndustryRoles().get(rolid).getIndustryroles_id();
		}
		
		
		if (experiencto.equals("")) {
			Util.ShowToast(context, "Please fill the Experience to.");
			return;
		}
		
		if (experiencfrom.equals("")) {
			Util.ShowToast(context, "Please fill the Experience from.");
			return;
		}
		
//		if(!(Integer.parseInt(experiencfrom)>=Integer.parseInt(experiencto))){
//			Util.ShowToast(context, "Experienc from value should be greater then the Experienc to value");
//			return;
//		}
		
		
		if (salaryto.equals("")) {
			Util.ShowToast(context, "Please fill the salary to.");
			return;
		}
		
		if (salaryfrom.equals("")) {
			Util.ShowToast(context, "Please fill the salary from.");
			return;
		}
		
//		if(!(Integer.parseInt(salaryfrom)>=Integer.parseInt(salaryto))){
//			Util.ShowToast(context, "From Salary value should be greater then the To Salary value");
//			return;
//		}
		
		if (cityid==-1) {
			Util.ShowToast(context, "Please select city.");
			return;
		}else {
			location=arrcity.get(cityid).getCity_id();
		}
		
		if (headline.equals("")) {
			Util.ShowToast(context, "Please fill Job Headline.");
			return;
		}
		
		if (headline.length()>500) {
			Util.ShowToast(context, "Job Headline exit limit.");
			return;
		}
		
		if (headlinedetails.equals("")) {
			Util.ShowToast(context, "Please fill Job Details."); 
			return;
		}
		
		if (headlinedetails.length() > 65535) {
			Util.ShowToast(context, "Job Details exit limit.");
			return;
		}
		
		if (!ph_.equals("") && ph_.length() < 10 && ph_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid Phone Number.");
			return;
		}
		
		if (!whats_.equals("") && whats_.length() < 10 && whats_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid WhatsApp Number.");
			return;
		}
		
		
		
		String id="";
		for (Circle mCircle : circlelist) {
			if (mCircle.isTick==1) {
				id=id+","+mCircle.getCircleid();
			}
		}
		
		if (id.equals("")) {
			Util.ShowToast(context, "Please select to.");
			return;
		}
		
		id=id.substring(1);
		
		
		JSONObject jsObj = getBody(industry,industryrole,location,experiencto,experiencfrom,salaryto,salaryfrom,headline,headlinedetails,email_,ph_,whats_,id+",");
	
		ArrayList<String> paths=new ArrayList<String>();
		if (picdatasets!=null && picdatasets.size()>=1) {
			for (PicDataSet pic : picdatasets) {
				if (!pic.path.equals("")) {
					paths.add(pic.path);
				}
				
			}
		}
		
		
		
		if (jsObj != null) {
			JobPostParser mJobPostParser = new JobPostParser();
			mJobPostParser.setJobpostparserinterface(new JobPostParserInterface() {
				
				@Override
				public void OnSuccess(JobDetails mJobDetails) {
					if (mJobDetails!=null) {
						((OfCampusApplication)context.getApplicationContext()).jobdetails=mJobDetails;
						Intent mIntent = new Intent(context,ActivityComment.class);
						Bundle mBundle=new Bundle();
						mBundle.putString("key_dlorcmt", Util.TOOLTITLE[1]);
						mIntent.putExtras(mBundle);
						startActivity(mIntent);
						((Activity) context).overridePendingTransition(0, 0); 
						finish();
					}
					
				}
				
				@Override
				public void OnError() {
					
				}
			});
			mJobPostParser.parse(context, jsObj, mDetails.getAuthtoken(),paths);

		}
	
	}

	
	
	private JSONObject getBody(String industry, String industryrole,
			String location, String experiencto, String experiencfrom,
			String salaryto, String salaryfrom, String headline,
			String headlinedetails, String email_, String ph_, String whats_, String Circle_id) { 

		
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
			
			String[] circle=Circle_id.split(",");
			JSONArray circleArray=new JSONArray();
			for (int i = 0; i <circle.length; i++) {
				circleArray.put(i, circle[i]);
			}
			
			JSONArray RolesArray=new JSONArray();
			for (int i = 0; i <1; i++) {
				RolesArray.put(i, industryrole);
			}
			
			JSONArray locaArray=new JSONArray();
			for (int i = 0; i <1; i++) {
				locaArray.put(i, location);
			}
			
			jsObj.put("circleList", circleArray);
			jsObj.put("industryRolesIdList", RolesArray);
			jsObj.put("locationIdList", locaArray);
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
	
}