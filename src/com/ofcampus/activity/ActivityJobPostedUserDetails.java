package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.component.CircleImageView;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobPostedUserDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.GetJobPostedUserProfileParser;
import com.ofcampus.parser.GetJobPostedUserProfileParser.GetJobPostedUserProfileParserInterface;
import com.ofcampus.ui.CustomTextView;

public class ActivityJobPostedUserDetails extends ActionBarActivity implements OnClickListener{

	private ProgressBar pgbar;
	private CircleImageView profilepic;
	private CustomTextView txt_name,txt_email,txt_year,nodata;
	private ImageView profile_imageblur;
	private ListView post_list,circle_list;
	private LinearLayout lin_main;
	private RelativeLayout rel_pg;
	
	private Context context;
	private JobPostedUserDetails mDetails;
	private JobDetails mJobDetails;
	private String Authtoken;
	
	private PostAdapter mpostAdapter;
	private CircleAdapter mCircleAdapter;
	private ArrayList<CustomTextView> textselection=new ArrayList<CustomTextView>();
	private ArrayList<CircleDetails> arraycircle =null;
	private ArrayList<JobDetails> arraypost = null;
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobposteduserdetails);

		context=ActivityJobPostedUserDetails.this;
		mJobDetails = ((OfCampusApplication) getApplication()).jobdetails;	
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		
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
	protected void onResume() {
		super.onResume();
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
		case R.id.inflt_circlerow_txt_postno:
			Selection(0);
			post_list.setVisibility(View.VISIBLE);
			circle_list.setVisibility(View.GONE);
			if (arraypost!=null && arraypost.size()>=1) {
				nodata.setVisibility(View.GONE);
			}else {
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.inflt_circlerow_txt_membno:
			Selection(1);
			post_list.setVisibility(View.GONE);
			circle_list.setVisibility(View.VISIBLE);
			if (arraycircle!=null && arraycircle.size()>=1) {
				nodata.setVisibility(View.GONE);
			}else {
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}
	
	
	private void initilize() {
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Post User");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		profile_imageblur=(ImageView)findViewById(R.id.profile_imageblur);
		profilepic=(CircleImageView)findViewById(R.id.profile_circleView);
		txt_name=(CustomTextView)findViewById(R.id.profile_name);
		txt_email=(CustomTextView)findViewById(R.id.profile_email);
		txt_year=(CustomTextView)findViewById(R.id.profile_class);
		pgbar=(ProgressBar)findViewById(R.id.myprofile_view_pgbar);
		
		textselection=new ArrayList<CustomTextView>();
		textselection.add((CustomTextView)findViewById(R.id.inflt_circlerow_txt_postno));
		textselection.add((CustomTextView)findViewById(R.id.inflt_circlerow_txt_membno));
		setClicklistner();
		
		post_list=(ListView)findViewById(R.id.jobposteduser_post_list);
		circle_list=(ListView)findViewById(R.id.jobposteduser_circle_list);
		nodata=(CustomTextView)findViewById(R.id.jobposteduser_nodata);
		
		
		mpostAdapter=new PostAdapter(context,new ArrayList<JobDetails>()); 
		post_list.setAdapter(mpostAdapter);
		
		mCircleAdapter=new CircleAdapter(context,new ArrayList<CircleDetails>());
		circle_list.setAdapter(mCircleAdapter);
		
		
		lin_main=(LinearLayout)findViewById(R.id.jobposteduser_linearmain);
		rel_pg=(RelativeLayout)findViewById(R.id.jobposteduser_linear_pg);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		Selection(0);
	}
	private void setClicklistner(){
		for (CustomTextView mCustomTextView : textselection) {
			mCustomTextView.setOnClickListener(this);
		}
	}
	private void Selection(int position){
		for (int i = 0; i < textselection.size(); i++) {
			CustomTextView mCustomTextView = textselection.get(i);
			if (position==i) {
				mCustomTextView.setTextColor(Color.parseColor("#35475D"));
				mCustomTextView.setTypeface(null, Typeface.BOLD);
			}else {
				mCustomTextView.setTextColor(Color.GRAY);
				mCustomTextView.setTypeface(null, Typeface.NORMAL);
			}
		}
	}
	
	private void loadData(){ 
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		rel_pg.setVisibility(View.VISIBLE);
		GetJobPostedUserProfileParser mParser=new GetJobPostedUserProfileParser();
		mParser.setGetjobposteduserprofileparserinterface(new GetJobPostedUserProfileParserInterface() {
			
			@Override
			public void OnSuccess(JobPostedUserDetails mJobPostedUserDetails) {
				mDetails=mJobPostedUserDetails;
				rel_pg.setVisibility(View.GONE);
				loadUserDetails();
			}
			
			@Override
			public void OnError() {
				rel_pg.setVisibility(View.GONE);
			}
			
			@Override
			public void NoData() {
				rel_pg.setVisibility(View.GONE);
			}
			
			
		});
		mParser.parse(context, mParser.getBody(mJobDetails.getId(),"0","8"), Authtoken,false );
	}
	
	private void loadUserDetails(){
		txt_name.setText(mDetails.getAccountname());
		txt_email.setText(mDetails.getEmail());
		txt_year.setText("Class "+mDetails.getGradyear());
		imageLoader.displayImage(mDetails.getProfileimagelink(),profilepic, options,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				pgbar.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				pgbar.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				pgbar.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				pgbar.setVisibility(View.GONE);
			}
		});
//		imageLoader.displayImage(mDetails.getProfileimagelink(),profile_imageblur, options);
		profile_imageblur.setImageResource(R.drawable.profile_bg);
		
		arraycircle = mDetails.getArrayCircle();
		arraypost = mDetails.getArrayPost();
		
		if (arraycircle!=null && arraycircle.size()>=1) {
			textselection.get(0).setText(arraypost.size()+"\n Posts");
			mpostAdapter.refreshView(arraypost);
			post_list.setVisibility(View.VISIBLE);
			circle_list.setVisibility(View.GONE);
		}else {
			nodata.setVisibility(View.VISIBLE);
		}
		
		if (arraycircle!=null && arraycircle.size()>=1) {
			textselection.get(1).setText(arraycircle.size()+"\n Circles");
			mCircleAdapter.refreshData(arraycircle);
		}
		
		lin_main.setVisibility(View.VISIBLE);
	}
	
	
	
	
	
	private class PostAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<JobDetails> arrayList;
		private LayoutInflater inflater;
		private ImageLoader imageLoader=ImageLoader.getInstance();
		private DisplayImageOptions options;
		
		public PostAdapter(Context context, ArrayList<JobDetails> arrayList_){
			this.mContext=context;
			this.arrayList=arrayList_;
			this.inflater=LayoutInflater.from(mContext);
			
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_profilepic)
					.showImageForEmptyUri(R.drawable.ic_profilepic)
					.showImageOnFail(R.drawable.ic_profilepic)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	
		}
		
		public void refreshView(ArrayList<JobDetails> arrayList_) { 
			this.arrayList=arrayList_;
			notifyDataSetChanged();
		}
		
		
		@Override
		public int getCount() {
			return arrayList.size();
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
			ViewHolder mHolder;
			if (convertView==null) {
				mHolder=new ViewHolder();
				
				convertView=inflater.inflate(R.layout.inflate_jobposteduser_postlistrow, parent,false);
				/**Comment Section*/
				
				mHolder.img_commentprfpic=(ImageView)convertView.findViewById(R.id.inflate_comment_img_pic);
				mHolder.txt_commentname=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_name);
				mHolder.txt_commentdate=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_postdate);
				mHolder.txt_commenteddetails=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_contain);
				mHolder.txt_subject=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_subject); 
				
				
				
				convertView.setTag(mHolder);
			}else{
				mHolder = (ViewHolder) convertView.getTag();
			}
			
			final JobDetails mJobDetails = arrayList.get(position);
			
			String url=mJobDetails.getImage();
			if (url!=null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}
			mHolder.txt_commentname.setText(mJobDetails.getName());
			mHolder.txt_commentdate.setText("Posted on "+mJobDetails.getPostedon());
			mHolder.txt_commenteddetails.setText(mJobDetails.getContent());
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			
			return convertView;
		}
		private class ViewHolder {
			public ImageView img_commentprfpic;
			public CustomTextView txt_commentname,txt_commentdate,txt_commenteddetails,txt_subject;
		}
		
	}
	
	
	private class CircleAdapter extends BaseAdapter{ 

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleDetails> circles=null; 
		
		public CircleAdapter(Context context,ArrayList<CircleDetails> arrcircle){
		
			this.mContext=context; 
			this.circles=arrcircle; 
			this.inflater=LayoutInflater.from(context);
		}
		
		public void refreshData(ArrayList<CircleDetails> arrJobs){
			this.circles= arrJobs;
			notifyDataSetChanged();
		}

		public void removepostion(int position) {
			if (this.circles.size()>=1) {
				this.circles.remove(position);
				notifyDataSetChanged();
			}
			
		}

		
		@Override
		public int getCount() {
			return circles.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder mHolder;
			if (convertView==null) {
				mHolder=new ViewHolder();
				convertView=inflater.inflate(R.layout.inflate_circledetails, null);
				mHolder.txt_joined=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_joined);
				mHolder.txt_membno=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_membno);
				mHolder.txt_postno=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_postno);
				mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_name);
				convertView.setTag(mHolder);
			}else {
				mHolder=(ViewHolder) convertView.getTag();
			}

			CircleDetails mCircleDetails=circles.get(position);
			
			mHolder.txt_joined.setText("UnJoin");
			mHolder.txt_membno.setText(mCircleDetails.getMembers()+"\n Members");
			mHolder.txt_postno.setText(mCircleDetails.getPosts()+"\n Posts");
			mHolder.txt_name.setText(mCircleDetails.getName());
			
			return convertView;
		}
		
		private class ViewHolder{
			CustomTextView txt_name,txt_postno,txt_membno,txt_joined;
		}
	}
}