package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CircleProfile;
import com.ofcampus.model.CircleUserDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CircleProfileParser;
import com.ofcampus.parser.CircleProfileParser.CircleProfileParserInterface;
import com.ofcampus.ui.CustomTextView;

public class ActivityCircleProfile extends ActionBarActivity implements OnClickListener{

	private Context context;
	private String authorization = "", circleId = "";

	private ProgressBar pgbar;
	private CustomTextView txt_name, txt_postno, txt_circleno, nodata;
	private ListView post_list, user_list;
	private LinearLayout lin_main;
	private RelativeLayout rel_pg;

	private PostAdapter mpostAdapter;
	private UsersAdapter mUsersAdapter;
	private ArrayList<ImageView> textselection = new ArrayList<ImageView>();
	private ArrayList<CircleUserDetails> arraycircle = null;
	private ArrayList<JobDetails> arraypost = null;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circleprofile);

		context = ActivityCircleProfile.this;
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
		case R.id.circleprofile_postselection:
			Selection(0);
			post_list.setVisibility(View.VISIBLE);
			user_list.setVisibility(View.GONE);
			if (arraypost!=null && arraypost.size()>=1) {
				nodata.setVisibility(View.GONE);
			}else {
				nodata.setText("No Post available");
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.circleprofile_circleselection:
			Selection(1);
			post_list.setVisibility(View.GONE);
			user_list.setVisibility(View.VISIBLE);
			if (arraycircle!=null && arraycircle.size()>=1) {
				nodata.setVisibility(View.GONE);
			}else {
				nodata.setText("No Circle User available");
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

	private void initilize() {

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Circle Profile");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		authorization = UserDetails.getLoggedInUser(context).getAuthtoken();
		circleId = getIntent().getExtras().getString("CircleID");


		txt_name = (CustomTextView) findViewById(R.id.cricle_name);
		txt_postno = (CustomTextView) findViewById(R.id.inflt_circlerow_txt_postno); 
		txt_circleno = (CustomTextView) findViewById(R.id.inflt_circlerow_txt_circleno); 
		pgbar = (ProgressBar) findViewById(R.id.myprofile_view_pgbar);

		textselection = new ArrayList<ImageView>();
		textselection.add((ImageView) findViewById(R.id.circleprofile_postselection));
		textselection.add((ImageView) findViewById(R.id.circleprofile_circleselection));
		setClicklistner();

		post_list = (ListView) findViewById(R.id.cricle_post_list);
		user_list = (ListView) findViewById(R.id.cricle_user_list);
		nodata = (CustomTextView) findViewById(R.id.jobposteduser_nodata);

		mpostAdapter = new PostAdapter(context, new ArrayList<JobDetails>());
		post_list.setAdapter(mpostAdapter);

		mUsersAdapter = new UsersAdapter(context, new ArrayList<CircleUserDetails>());
		user_list.setAdapter(mUsersAdapter);

		lin_main = (LinearLayout) findViewById(R.id.circleprf_linearmain);
		rel_pg = (RelativeLayout) findViewById(R.id.jobposteduser_linear_pg);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		Selection(0);
		lin_main.setVisibility(View.GONE);
		
	}

	private void setClicklistner() {
		for (ImageView mImageView : textselection) {
			mImageView.setOnClickListener(this);
		}
	}

	private void Selection(int position) {
		for (int i = 0; i < textselection.size(); i++) {
			ImageView mImageView = textselection.get(i);
			if (position == i) {
				mImageView.setSelected(true);
			} else {
				mImageView.setSelected(false);
			}
		}
	}

	private void loadData() {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		rel_pg.setVisibility(View.VISIBLE);
		CircleProfileParser mCircleProfileParser = new CircleProfileParser();
		mCircleProfileParser.setCircleprofileparserinterface(new CircleProfileParserInterface() {

			@Override
			public void OnSuccess(CircleProfile mCircleProfile) {
				Log.e("TAG", mCircleProfile.toString());
				processData(mCircleProfile);
			}

			@Override
			public void OnError() {
				rel_pg.setVisibility(View.GONE);
			}
		 });
		mCircleProfileParser.parse(context,mCircleProfileParser.getBody(circleId, "0", "8"),authorization);
	}

	
	private void processData(CircleProfile mCircleProfile){
		try {
			
			arraycircle = mCircleProfile.getArrayCircle();
			arraypost = mCircleProfile.getArrayPost();

			
			txt_name.setText(mCircleProfile.getCirclename());
			txt_postno.setText(""+arraypost.size());
			txt_circleno.setText(""+arraycircle.size());
			
			mpostAdapter.refreshView(mCircleProfile.getArrayPost());
			mUsersAdapter.refreshData(mCircleProfile.getArrayCircle());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		lin_main.setVisibility(View.VISIBLE);
		rel_pg.setVisibility(View.GONE);
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
	
	
	private class UsersAdapter extends BaseAdapter{ 
 
		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleUserDetails> circles=null; 
		
		public UsersAdapter(Context context,ArrayList<CircleUserDetails> arrcircle){
		
			this.mContext=context; 
			this.circles=arrcircle; 
			this.inflater=LayoutInflater.from(context);
			
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_profilepic)
					.showImageForEmptyUri(R.drawable.ic_profilepic)
					.showImageOnFail(R.drawable.ic_profilepic)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		public void refreshData(ArrayList<CircleUserDetails> arrJobs){
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
				convertView=inflater.inflate(R.layout.inflate_circle_user, parent,false);
				/**Comment Section*/
				
				mHolder.img_commentprfpic=(ImageView)convertView.findViewById(R.id.inflate_comment_img_pic);
				mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_name);
				mHolder.txt_email=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_email);
				mHolder.txt_grdyear=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_yeargrd);
			
				convertView.setTag(mHolder);
			}else {
				mHolder=(ViewHolder) convertView.getTag();
			}

			CircleUserDetails mCircleUserDetails=circles.get(position);
			
			String url=mCircleUserDetails.getUserimage();
			if (url!=null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}
			
			mHolder.txt_name.setText(mCircleUserDetails.getUsername());
			mHolder.txt_email.setText(mCircleUserDetails.getUseryearofgrad());
			mHolder.txt_grdyear.setText(mCircleUserDetails.getUseryearofgrad()); 
			
			return convertView;
		}
		
		private class ViewHolder{
			public ImageView img_commentprfpic;
			public CustomTextView txt_name,txt_email,txt_grdyear;
		}
	}
}
