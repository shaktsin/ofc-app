/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.component.CircleImageView;
import com.ofcampus.custprogressbar.ProgressBarCircularIndeterminate;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobPostedUserDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.GetJobPostedUserProfileParser;
import com.ofcampus.parser.GetJobPostedUserProfileParser.GetJobPostedUserProfileParserInterface;
import com.ofcampus.parser.LoadMoreUserCircleParser;
import com.ofcampus.parser.LoadMoreUserCircleParser.LoadMoreCircleParserInterface;
import com.ofcampus.parser.LoadMoreUserPostParser;
import com.ofcampus.parser.LoadMoreUserPostParser.LoadMorePostParserInterface;
import com.ofcampus.ui.CustomTextView;

public class ActivityJobPostedUserDetails extends ActionBarActivity implements OnClickListener {

	private ProgressBar pgbar;
	private CircleImageView profilepic;
	private CustomTextView txt_name, txt_email, txt_year, nodata;
	private ImageView profile_imageblur;
	private ListView post_list, circle_list;
	private LinearLayout lin_main;
	private ProgressBarCircularIndeterminate indicator_pg;

	private Context context;
	private JobPostedUserDetails mDetails;
	private String Authtoken;
	private String userID = "";
	private PostAdapter mpostAdapter;
	private CircleAdapter mCircleAdapter;
	private ArrayList<CustomTextView> textselection = new ArrayList<CustomTextView>();
	private ArrayList<CircleDetails> arraycircle = null;
	private ArrayList<JobDetails> arraypost = null;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private Toolbar toolbar;

	private boolean isUserCame = false;
	private UserDetails mUserDetails = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobposteduserdetails);

		context = ActivityJobPostedUserDetails.this;
		mUserDetails = UserDetails.getLoggedInUser(context);
		Authtoken = mUserDetails.getAuthtoken();
		try {
			userID = getIntent().getExtras().getString("userID");
			isUserCame = getIntent().getExtras().getBoolean("isUserCame");
		} catch (Exception e) {
			e.printStackTrace();
		}

		initilize();
		if (isUserCame) {
			loadUserDetailsFromUserDeatils();
			lin_main.setVisibility(View.VISIBLE);
		}

		userID = (isUserCame) ? mUserDetails.getUserID() : userID;

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
		if (((OfCampusApplication) context.getApplicationContext()).isPostDataModify) {
			loadUserDetailsFromUserDeatils();
			loadData();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isUserCame) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_postedit, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_editpost:
			startActivity(new Intent(ActivityJobPostedUserDetails.this, ActivityProfileEdit.class));
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 91 && resultCode == RESULT_OK && data != null) {
			boolean isModify = data.getExtras().getBoolean("isDataModify");
			if (isModify && isUserCame) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						loadData();
					}
				}, 500);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inflt_circlerow_txt_postno:
			Selection(0);
			post_list.setVisibility(View.VISIBLE);
			circle_list.setVisibility(View.GONE);
			if (arraypost != null && arraypost.size() >= 1) {
				nodata.setVisibility(View.GONE);
			} else {
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.inflt_circlerow_txt_membno:
			Selection(1);
			post_list.setVisibility(View.GONE);
			circle_list.setVisibility(View.VISIBLE);
			if (arraycircle != null && arraycircle.size() >= 1) {
				nodata.setVisibility(View.GONE);
			} else {
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}

	private void initilize() {

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		profile_imageblur = (ImageView) findViewById(R.id.profile_imageblur);
		profilepic = (CircleImageView) findViewById(R.id.profile_circleView);
		txt_name = (CustomTextView) findViewById(R.id.profile_name);
		txt_email = (CustomTextView) findViewById(R.id.profile_email);
		txt_year = (CustomTextView) findViewById(R.id.profile_class);
		pgbar = (ProgressBar) findViewById(R.id.myprofile_view_pgbar);

		textselection = new ArrayList<CustomTextView>();
		textselection.add((CustomTextView) findViewById(R.id.inflt_circlerow_txt_postno));
		textselection.add((CustomTextView) findViewById(R.id.inflt_circlerow_txt_membno));
		setClicklistner();

		post_list = (ListView) findViewById(R.id.jobposteduser_post_list);
		circle_list = (ListView) findViewById(R.id.jobposteduser_circle_list);
		nodata = (CustomTextView) findViewById(R.id.jobposteduser_nodata);

		mpostAdapter = new PostAdapter(context, new ArrayList<JobDetails>());
		post_list.setAdapter(mpostAdapter);

		mCircleAdapter = new CircleAdapter(context, new ArrayList<CircleDetails>());
		circle_list.setAdapter(mCircleAdapter);

		lin_main = (LinearLayout) findViewById(R.id.jobposteduser_linearmain);
		indicator_pg = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.profile_bg).showImageForEmptyUri(R.drawable.profile_bg).showImageOnFail(R.drawable.profile_bg).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		Selection(0);

		textselection.get(0).setText("0 Posts");
		textselection.get(1).setText("0 Circles");

		footer_pg = (RelativeLayout) findViewById(R.id.loadmore_pg);
		circle_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mCircleAdapter != null && totalItemCount > minimumofsetsCircle && (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItemCircle < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						loadMoreCircle();
					}
				}
				mLastFirstVisibleItemCircle = firstVisibleItem;

			}
		});

		post_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mpostAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						loadMorePost();
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;

			}
		});

	}

	private void setClicklistner() {
		for (CustomTextView mCustomTextView : textselection) {
			mCustomTextView.setOnClickListener(this);
		}
	}

	private void Selection(int position) {
		for (int i = 0; i < textselection.size(); i++) {
			CustomTextView mCustomTextView = textselection.get(i);
			if (position == i) {
				mCustomTextView.setTextColor(Color.parseColor("#35475D"));
				mCustomTextView.setTypeface(null, Typeface.BOLD);
			} else {
				mCustomTextView.setTextColor(Color.GRAY);
				mCustomTextView.setTypeface(null, Typeface.NORMAL);
			}
		}
	}

	private void loadData() {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		pageNoCircle = 0;
		pageNoPost = 0;

		indicator_pg.setVisibility(View.VISIBLE);
		GetJobPostedUserProfileParser mParser = new GetJobPostedUserProfileParser();
		mParser.setGetjobposteduserprofileparserinterface(new GetJobPostedUserProfileParserInterface() {

			@Override
			public void OnSuccess(JobPostedUserDetails mJobPostedUserDetails) {
				mDetails = mJobPostedUserDetails;
				indicator_pg.setVisibility(View.GONE);
				if (!isUserCame) {
					loadUserDetailsFromJob();
				}

				loadPostAndCircle();
			}

			@Override
			public void OnError() {
				indicator_pg.setVisibility(View.GONE);
			}

			@Override
			public void NoData() {
				indicator_pg.setVisibility(View.GONE);
			}

		});
		mParser.parse(context, mParser.getBody((isUserCame) ? mUserDetails.getUserID() : userID, "" + pageNoPost, "" + pageCountPost), Authtoken, false);
	}

	private void loadUserDetailsFromJob() {
		String name = mDetails.getAccountname();
		if (!(name == null || name.length() == 0)) {
			String firstName = null;
			if (name.contains(" ")) {
				firstName = name.substring(0, name.indexOf(' ') - 1);
			} else {
				firstName = name;
			}
			String capitalized = Util.capitalize(firstName);
			capitalized = capitalized.concat("'s");
			if (toolbar != null) {
				toolbar.setTitle(capitalized.trim() + " profile");
			}
		}
		txt_name.setText(mDetails.getAccountname());
		txt_email.setText(mDetails.getEmail());
		txt_year.setText("Class of " + mDetails.getGradyear());
		imageLoader.displayImage(mDetails.getProfileimagelink(), profile_imageblur, options, new ImageLoadingListener() {

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
		// profile_imageblur.setImageResource(R.drawable.profile_bg);
	}

	private void loadUserDetailsFromUserDeatils() {

		UserDetails userDetails = UserDetails.getLoggedInUser(context);
		String name = userDetails.getFstname();
		if (!(name == null || name.length() == 0)) {
			String firstName = null;
			if (name.contains(" ")) {
				firstName = name.substring(0, name.indexOf(' ') - 1);
			} else {
				firstName = name;
			}
			String capitalized = Util.capitalize(firstName);
			capitalized = capitalized.concat("'s");
			if (toolbar != null) {
				toolbar.setTitle(capitalized.trim() + " profile");
			}
		}
		txt_name.setText(userDetails.getFstname() + " " + userDetails.getLstname());
		txt_email.setText(userDetails.getEmail());
		txt_year.setText("Class of " + userDetails.getYearPass());
		imageLoader.displayImage(userDetails.getImage(), profile_imageblur, options, new ImageLoadingListener() {

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
		// profile_imageblur.setImageResource(R.drawable.profile_bg);
	}

	private void loadPostAndCircle() {

		arraycircle = mDetails.getArrayCircle();
		arraypost = mDetails.getArrayPost();

		if (arraypost != null && arraypost.size() >= 1) {
			textselection.get(0).setText(arraypost.size() + " Posts");
			mpostAdapter.refreshView(arraypost);
			post_list.setVisibility(View.VISIBLE);
			circle_list.setVisibility(View.GONE);
			pageNoPost = pageNoPost + 1;
		} else {
			nodata.setVisibility(View.VISIBLE);
		}

		if (arraycircle != null && arraycircle.size() >= 1) {
			textselection.get(1).setText(arraycircle.size() + " Circles");
			mCircleAdapter.refreshData(arraycircle);
			pageNoCircle = pageNoCircle + 1;
		}

		lin_main.setVisibility(View.VISIBLE);
	}

	private class PostAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<JobDetails> arrayList;
		private LayoutInflater inflater;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions options;

		public PostAdapter(Context context, ArrayList<JobDetails> arrayList_) {
			this.mContext = context;
			this.arrayList = arrayList_;
			this.inflater = LayoutInflater.from(mContext);

			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		}

		public void refreshView(ArrayList<JobDetails> arrayList_) {
			this.arrayList = arrayList_;
			notifyDataSetChanged();
		}

		public void loadMore(ArrayList<JobDetails> arrayList_) {
			this.arrayList.addAll(arrayList_);
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
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.inflate_jobposteduser_postlistrow, parent, false);
				/** Comment Section */
				mHolder.img_commentprfpic = (ImageView) convertView.findViewById(R.id.inflate_comment_img_pic);
				mHolder.txt_commentname = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_name);
				mHolder.txt_commentdate = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_postdate);
				mHolder.txt_commenteddetails = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_contain);
				mHolder.txt_subject = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_subject);

				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			final JobDetails mJobDetails = arrayList.get(position);

			String url = mJobDetails.getImage();
			if (url != null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}
			mHolder.img_commentprfpic.setVisibility(View.GONE);
			mHolder.txt_commentname.setText(mJobDetails.getName());
			mHolder.txt_commentdate.setText("Posted on " + mJobDetails.getPostedon());
			mHolder.txt_commenteddetails.setText(mJobDetails.getContent());
			mHolder.txt_subject.setText(mJobDetails.getSubject());

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToPostDetails(mJobDetails);
				}
			});

			return convertView;
		}

		private class ViewHolder {
			public ImageView img_commentprfpic;
			public CustomTextView txt_commentname, txt_commentdate, txt_commenteddetails, txt_subject;
		}

		private void gotToPostDetails(JobDetails mJobDetails) {
			try {
				String toolTitle = "";
				Intent mIntent = null;
				if (mJobDetails.getPostType().equals("3")) {
					toolTitle = Util.TOOLTITLE[1];
					mIntent = new Intent(mContext, ActivityNewsDetails.class);
				} else if (mJobDetails.getPostType().equals("1")) {
					toolTitle = Util.TOOLTITLE[1];
					mIntent = new Intent(mContext, ActivityClassifiedDetails.class);
				} else {
					toolTitle = Util.TOOLTITLE[0];
					mIntent = new Intent(mContext, ActivityJobDetails.class);
				}
				((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
				Bundle mBundle = new Bundle();
				mBundle.putString(Util.BUNDLE_KEY[0], toolTitle);
				mIntent.putExtras(mBundle);
				mContext.startActivity(mIntent);
				((Activity) mContext).overridePendingTransition(0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class CircleAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleDetails> circles = null;

		public CircleAdapter(Context context, ArrayList<CircleDetails> arrcircle) {

			this.mContext = context;
			this.circles = arrcircle;
			this.inflater = LayoutInflater.from(context);
		}

		public void refreshData(ArrayList<CircleDetails> arrJobs) {
			this.circles = arrJobs;
			notifyDataSetChanged();
		}

		public void loadMore(ArrayList<CircleDetails> arrJobs) {
			this.circles.addAll(arrJobs);
			notifyDataSetChanged();
		}

		public void removepostion(int position) {
			if (this.circles.size() >= 1) {
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
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.inflate_userprofile_circle, null);
				mHolder.last_post = (CustomTextView) convertView.findViewById(R.id.inflt_last_posts_details);
				mHolder.txt_post_and_members = (TextView) convertView.findViewById(R.id.post_and_members_info);
				mHolder.txt_name = (CustomTextView) convertView.findViewById(R.id.inflt_circlerow_txt_name);
				mHolder.join_btn = (Button) convertView.findViewById(R.id.join_circle);

				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			CircleDetails mCircleDetails = circles.get(position);
			final String circleID = mCircleDetails.getId();

			String circleName = mCircleDetails.getName();
			String camelCaseName = Character.toString(Character.toUpperCase(circleName.charAt(0))) + circleName.substring(1).toLowerCase();

			mHolder.txt_name.setText(camelCaseName);
			String post_and_members_details = mCircleDetails.getMembers() + " members," + mCircleDetails.getPosts() + " posts";
			mHolder.txt_post_and_members.setText(post_and_members_details);
			mHolder.join_btn.setEnabled(true);
			mHolder.join_btn.setText("Unjoin");
			mHolder.join_btn.setVisibility(View.INVISIBLE);

			mHolder.join_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// unjoinCircleEvent(circleID,position);
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent mIntent = new Intent(mContext, ActivityCircleProfile.class);
					((OfCampusApplication) mContext.getApplicationContext()).mCircleDetails_ = circles.get(position);
					((Activity) mContext).startActivityForResult(mIntent, 91);
					((Activity) mContext).overridePendingTransition(0, 0);
				}
			});

			return convertView;
		}

		private class ViewHolder {
			CustomTextView txt_name, last_post, txt_joined;
			TextView txt_post_and_members;
			Button join_btn;
		}
	}

	/**
	 * load More call for post
	 */

	/*** For Load more ****/
	private int pageNoPost = 0;
	private int pageCountPost = 8;
	private int minimumofsets = pageCountPost - 1, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;
	private RelativeLayout footer_pg;

	private void loadMorePost() {
		LoadMoreUserPostParser morePostParser = new LoadMoreUserPostParser();
		morePostParser.setLoadMorePostParserInterface(new LoadMorePostParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> posts) {
				if (posts != null && posts.size() >= 1) {
					mpostAdapter.loadMore(posts);
					pageNoPost = pageNoPost + 1;

					try {
						String postCount = textselection.get(0).getText().toString();
						postCount = postCount.replace(" Posts", "");
						textselection.get(0).setText(String.valueOf(posts.size() + Integer.parseInt(postCount)) + " Posts");
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

				}
				loadingMore = false;
			}

			@Override
			public void OnError() {
				loadingMore = false;
			}

			@Override
			public void NoData() {
				loadingMore = false;
			}
		});
		morePostParser.parse(context, morePostParser.getBody(userID, "" + pageNoPost, "" + pageCountPost), Authtoken, false);
	}

	/*** For Load more ****/
	private int pageNoCircle = 0;
	private int pageCountCircle = 8;
	private int minimumofsetsCircle = pageCountCircle - 1, mLastFirstVisibleItemCircle = 0;

	private void loadMoreCircle() {

		LoadMoreUserCircleParser moreCircleParser = new LoadMoreUserCircleParser();
		moreCircleParser.setLoadMoreCircleParserInterface(new LoadMoreCircleParserInterface() {

			@Override
			public void OnSuccess(ArrayList<CircleDetails> circleList) {
				if (circleList != null && circleList.size() >= 1) {
					mCircleAdapter.loadMore(circleList);
					pageNoCircle = pageNoCircle + 1;

					try {
						String postCount = textselection.get(1).getText().toString();
						postCount = postCount.replace(" Circles", "");
						textselection.get(0).setText(String.valueOf(circleList.size() + Integer.parseInt(postCount)) + " Circles");
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				loadingMore = false;
			}

			@Override
			public void OnError() {
				loadingMore = false;

			}

			@Override
			public void NoData() {
				loadingMore = false;

			}
		});
		moreCircleParser.parse(context, moreCircleParser.getBody(userID, "" + pageNoCircle, "" + pageCountCircle), Authtoken, false);
	}

}