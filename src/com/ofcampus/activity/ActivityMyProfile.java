package com.ofcampus.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.component.CircleImageView;
import com.ofcampus.model.UserDetails;
import com.ofcampus.ui.CustomTextView;

public class ActivityMyProfile extends ActionBarActivity {

	private ProgressBar pgbar;
	private CircleImageView profilepic;
	private CustomTextView txt_name,txt_email;
	private Context context;
	
	private UserDetails mDetails;
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myprofile);

		context=ActivityMyProfile.this;
		initilize();
		loadProfileData();
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
		inflater.inflate(R.menu.menu_postedit, menu);
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_editpost:
			startActivity(new Intent(ActivityMyProfile.this,ActivityProfileEdit.class));
			overridePendingTransition(0,0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void initilize() {
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("MyProfile");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		profilepic=(CircleImageView)findViewById(R.id.profile_circleView);
		txt_name=(CustomTextView)findViewById(R.id.profile_name);
		txt_email=(CustomTextView)findViewById(R.id.profile_email);
		
		pgbar=(ProgressBar)findViewById(R.id.myprofile_view_pgbar);
		
		
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
	}
	
	
	private void loadProfileData(){
		mDetails=UserDetails.getLoggedInUser(context);
		txt_name.setText(mDetails.getName());
		txt_email.setText(mDetails.getEmail());
		
		imageLoader.displayImage("https://s3-ap-southeast-1.amazonaws.com/ofcampus/profile/3_profile_susi.png",profilepic, options,new ImageLoadingListener() {
			
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
		
	}
}
