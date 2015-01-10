package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.JobDetailsParser;
import com.ofcampus.parser.JobDetailsParser.JobDetailsParserInterface;

public class ActivityJobDetails extends ActionBarActivity implements OnClickListener{

	private ImageView profilepic;
	private TextView txt_name,txt_postdate,txt_subject,txt_contain;
	private String from ;
	private String JObID;
	private JobDetails mJobDetails;
	private UserDetails mUserDetails;
	private Context mContext;
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joddetails);

		mContext=ActivityJobDetails.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("JobDetails");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jobdetailsview_txt_reply:

			break;
		case R.id.jobdetailsview_txt_share:

			break;
		case R.id.jobdetailsview_txt_comment:

			break;

		default:
			break;
		}
	}
	
	
	private void initilize() {
		profilepic=(ImageView)findViewById(R.id.jobdetailsview_img_pic);
		txt_name=(TextView)findViewById(R.id.jobdetailsview_txt_name);
		txt_postdate=(TextView)findViewById(R.id.jobdetailsview_txt_postdate);
		txt_subject=(TextView)findViewById(R.id.jobdetailsview_txt_subject);
		txt_contain=(TextView)findViewById(R.id.jobdetailsview_txt_contain);
		((TextView) findViewById(R.id.jobdetailsview_txt_reply)).setOnClickListener(this);
		((TextView) findViewById(R.id.jobdetailsview_txt_share)).setOnClickListener(this);
		((TextView) findViewById(R.id.jobdetailsview_txt_comment)).setOnClickListener(this);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheInMemory(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
	}
	
	private void loadData() {
		try {
			Bundle mBundle=getIntent().getExtras();
			from = mBundle.getString("From");
			JObID= mBundle.getString("JobID");
			
			mUserDetails =  UserDetails.getLoggedInUser(mContext);
			 
			if (from.equals("CreateJob")) {
				mJobDetails=((OfCampusApplication)getApplication()).jobdetails;
				((OfCampusApplication)getApplication()).jobdetails=null;
				((ImageView)findViewById(R.id.jobdetailsview_img_arrow)).setVisibility(View.GONE);
				((RelativeLayout)findViewById(R.id.jobdetailsview_main)).setVisibility(View.VISIBLE);
				setDataInView(mJobDetails);
			} else if (from.equals("JobList")) {
				mJobDetails=((OfCampusApplication)getApplication()).jobdetails;
				((OfCampusApplication)getApplication()).jobdetails=null;
				
				if (!Util.hasConnection(mContext)) {
					Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg)); 
					onBackPressed();
					return;
				}
				
				JobDetailsParser mDetailsParser=new JobDetailsParser();
				mDetailsParser.setJobdetailsparserinterface(new JobDetailsParserInterface() {
					
					@Override
					public void OnSuccess(JobDetails mJobDetails) {
						((RelativeLayout)findViewById(R.id.jobdetailsview_main)).setVisibility(View.VISIBLE);
						txt_contain.setText(mJobDetails.getContent());
					}
					
					@Override
					public void OnError() {
						onBackPressed();
					}
				});
				mDetailsParser.parse(mContext, JObID,mUserDetails.getAuthtoken());
				((ImageView)findViewById(R.id.jobdetailsview_img_arrow)).setVisibility(View.GONE);
				((RelativeLayout)findViewById(R.id.jobdetailsview_main)).setVisibility(View.VISIBLE);
				setDataInView(mJobDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void setDataInView(JobDetails mJobDetails){
		if (mJobDetails!=null) {
			imageLoader.displayImage(mJobDetails.getImage(), profilepic, options);
			txt_name.setText(mJobDetails.getName());
			txt_postdate.setText("Posted on "+mJobDetails.getPostedon());
			txt_subject.setText(mJobDetails.getSubject());
			txt_contain.setText(mJobDetails.getContent());
		}else {
			onBackPressed();
		}
	}
}
