/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.CircleProfile;
import com.ofcampus.model.CircleUserDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.AcceptRequestToJoinCircleParser;
import com.ofcampus.parser.AcceptRequestToJoinCircleParser.AcceptRequestParserInterface;
import com.ofcampus.parser.CircleProfileParser;
import com.ofcampus.parser.CircleProfileParser.CircleProfileParserInterface;
import com.ofcampus.parser.GetAllPendingRequestList;
import com.ofcampus.parser.GetAllPendingRequestList.PendingRequestParserInterface;
import com.ofcampus.parser.JoinCircleParser;
import com.ofcampus.parser.JoinCircleParser.JoinCircleParserInterface;
import com.ofcampus.parser.RejectRequestToJoinCircleParser;
import com.ofcampus.parser.RejectRequestToJoinCircleParser.RejectRequestParserInterface;
import com.ofcampus.parser.UnJoinCircleParser;
import com.ofcampus.parser.UnJoinCircleParser.UnJoinCircleParserInterface;
import com.ofcampus.ui.CustomTextView;

public class ActivityCircleProfile extends ActionBarActivity implements OnClickListener {

	// public static String ISDATAMODIFY_KEY="isDataModify";
	private boolean isDataModify = false;

	private Context context;
	private String authorization = "", circleId = "";
	private boolean isAlreadyJoined = false;

	private ProgressBar pgbar;
	private CustomTextView txt_name, txt_postno, txt_circleno, nodata, txt_description;
	private Button status_circle;
	private ListView post_list, user_list, pendingrqs_list;
	private LinearLayout lin_main;
	private RelativeLayout rel_pg;

	private PostAdapter mpostAdapter;
	private UsersAdapter mUsersAdapter;
	private PendingUsersAdapter mPendingUsersAdapter;
	private CircleDetails mCircleDetails;

	private ArrayList<ImageView> textselection = new ArrayList<ImageView>();
	private ArrayList<CircleUserDetails> arraycircle = null;
	private ArrayList<JobDetails> arraypost = null;
	private ArrayList<CircleUserDetails> pendingUser = null;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	private enum SelectionTab {
		POST, USER, PENDINGREQ
	}

	private String title = "Club Profile";
	private boolean isChapter = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circleprofile);

		context = ActivityCircleProfile.this;
		Bundle mBundle = getIntent().getExtras();
		if (mBundle != null) {
			isChapter = mBundle.getBoolean("isChapterEvent");
			title = "Chapter Profile";
		}

		initilize();
		loadData();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		returnResult();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			returnResult();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.circleprofile_postselection:
			selectTab(SelectionTab.POST);
			break;

		case R.id.circleprofile_circleselection:
			selectTab(SelectionTab.USER);
			break;

		case R.id.circleprofile_pendingreq:
			selectTab(SelectionTab.PENDINGREQ);
			break;
		case R.id.status_circle:
			if (isAlreadyJoined) {
				unjoinCircleEvent(circleId);
			} else {
				joinCircleEvent(circleId);
			}
			break;

		default:
			break;
		}
	}

	private void initilize() {

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mCircleDetails = ((OfCampusApplication) context.getApplicationContext()).mCircleDetails_;

		authorization = UserDetails.getLoggedInUser(context).getAuthtoken();
		circleId = mCircleDetails.getId();

		isAlreadyJoined = (mCircleDetails.getJoined().equalsIgnoreCase("true")) ? true : false;

		status_circle = (Button) findViewById(R.id.status_circle);
		txt_name = (CustomTextView) findViewById(R.id.cricle_name);
		txt_description = (CustomTextView) findViewById(R.id.cricle_description);
		txt_postno = (CustomTextView) findViewById(R.id.circleprofile_postcount);
		txt_circleno = (CustomTextView) findViewById(R.id.circleprofile_memburcount);
		status_circle.setOnClickListener(this);

		pgbar = (ProgressBar) findViewById(R.id.myprofile_view_pgbar);

		textselection = new ArrayList<ImageView>();
		textselection.add((ImageView) findViewById(R.id.circleprofile_postselection));
		textselection.add((ImageView) findViewById(R.id.circleprofile_circleselection));
		if (mCircleDetails.getAdmin().equals("true")) {
			ImageView pendingreq = (ImageView) findViewById(R.id.circleprofile_pendingreq);
			pendingreq.setVisibility(View.VISIBLE);
			textselection.add(pendingreq);
		}

		post_list = (ListView) findViewById(R.id.cricle_post_list);
		user_list = (ListView) findViewById(R.id.cricle_user_list);
		pendingrqs_list = (ListView) findViewById(R.id.cricle_pendingreq_list);
		nodata = (CustomTextView) findViewById(R.id.jobposteduser_nodata);

		setClicklistner();

		mpostAdapter = new PostAdapter(context, new ArrayList<JobDetails>());
		post_list.setAdapter(mpostAdapter);

		mUsersAdapter = new UsersAdapter(context, new ArrayList<CircleUserDetails>());
		user_list.setAdapter(mUsersAdapter);

		mPendingUsersAdapter = new PendingUsersAdapter(context, new ArrayList<CircleUserDetails>());
		pendingrqs_list.setAdapter(mPendingUsersAdapter);

		lin_main = (LinearLayout) findViewById(R.id.circleprf_linearmain);
		rel_pg = (RelativeLayout) findViewById(R.id.jobposteduser_linear_pg);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		Selection(0);
		lin_main.setVisibility(View.GONE);

	}

	private void selectTab(SelectionTab tab) {
		switch (tab) {
		case POST:
			Selection(0);
			post_list.setVisibility(View.VISIBLE);
			user_list.setVisibility(View.GONE);
			pendingrqs_list.setVisibility(View.GONE);
			if (arraypost != null && arraypost.size() >= 1) {
				nodata.setVisibility(View.GONE);
			} else {
				nodata.setText("No Post available");
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		case USER:
			Selection(1);
			post_list.setVisibility(View.GONE);
			user_list.setVisibility(View.VISIBLE);
			pendingrqs_list.setVisibility(View.GONE);
			if (arraycircle != null && arraycircle.size() >= 1) {
				nodata.setVisibility(View.GONE);
			} else {
				nodata.setText("No Circle User available");
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		case PENDINGREQ:
			Selection(2);
			post_list.setVisibility(View.GONE);
			user_list.setVisibility(View.GONE);
			pendingrqs_list.setVisibility(View.VISIBLE);
			if (pendingUser != null && pendingUser.size() >= 1) {
				nodata.setVisibility(View.GONE);
			} else {
				nodata.setText("No Pending User available");
				nodata.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
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
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		rel_pg.setVisibility(View.VISIBLE);
		CircleProfileParser mCircleProfileParser = new CircleProfileParser();
		mCircleProfileParser.setCircleprofileparserinterface(new CircleProfileParserInterface() {

			@Override
			public void OnSuccess(CircleProfile mCircleProfile) {
				processData(mCircleProfile);
				if (mCircleDetails.getAdmin().equals("true")) {
					getPendingList();
				}
			}

			@Override
			public void OnError() {
				rel_pg.setVisibility(View.GONE);
			}
		});
		mCircleProfileParser.parse(context, mCircleProfileParser.getBody(circleId, "0", "8"), authorization);
	}

	private void getPendingList() {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}
		rel_pg.setVisibility(View.VISIBLE);
		GetAllPendingRequestList mAllPendingRequestList = new GetAllPendingRequestList();
		mAllPendingRequestList.setPendingrequestparserinterface(new PendingRequestParserInterface() {

			@Override
			public void OnSuccess(ArrayList<CircleUserDetails> pendingUser_) {
				if (pendingUser_ != null && pendingUser_.size() >= 1) {
					pendingUser = pendingUser_;
					mPendingUsersAdapter.refreshData(pendingUser_);
				}
			}

			@Override
			public void OnError() {
				rel_pg.setVisibility(View.GONE);
			}
		});
		mAllPendingRequestList.parse(context, mAllPendingRequestList.getBody(circleId, "0", "8"), authorization);
	}

	private void acceptRequest(String userId) {
		AcceptRequestToJoinCircleParser mJoinCircleParser = new AcceptRequestToJoinCircleParser();
		mJoinCircleParser.setAcceptrequestparserinterface(new AcceptRequestParserInterface() {

			@Override
			public void OnSuccess() {

			}

			@Override
			public void OnError() {

			}
		});
		mJoinCircleParser.parse(context, mJoinCircleParser.getBody(circleId, userId), authorization);
	}

	private void rejectRequest(String userId) {
		RejectRequestToJoinCircleParser mRejectRequestParser = new RejectRequestToJoinCircleParser();
		mRejectRequestParser.setRejectRequestparserinterface(new RejectRequestParserInterface() {

			@Override
			public void OnSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void OnError() {
				// TODO Auto-generated method stub

			}
		});
		mRejectRequestParser.parse(context, mRejectRequestParser.getBody(circleId, userId), authorization);
	}

	private void processData(CircleProfile mCircleProfile) {
		try {
			arraycircle = mCircleProfile.getArrayCircle();
			arraypost = mCircleProfile.getArrayPost();

			txt_name.setText(mCircleProfile.getCirclename());
			txt_description.setText(mCircleProfile.getCircledesc());
			txt_postno.setText("" + arraypost.size());
			txt_circleno.setText("" + arraycircle.size());

			mpostAdapter.refreshView(mCircleProfile.getArrayPost());
			mUsersAdapter.refreshData(mCircleProfile.getArrayCircle());
			status_circle.setVisibility(View.VISIBLE);
			status_circle.setText((mCircleProfile.getCirclejoined().equalsIgnoreCase("true")) ? "Unjoin" : "Join");

		} catch (Exception e) {
			e.printStackTrace();
		}
		selectTab(SelectionTab.POST);
		lin_main.setVisibility(View.VISIBLE);
		rel_pg.setVisibility(View.GONE);
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
			mHolder.txt_commentname.setText(Util.capitalize(mJobDetails.getName()));
			String postedOn = Util.getPostedOnText(mJobDetails.getPostedon());
			mHolder.txt_commentdate.setText(postedOn);
			mHolder.txt_commenteddetails.setText(mJobDetails.getContent());
			mHolder.txt_subject.setText(mJobDetails.getSubject());

			return convertView;
		}

		private class ViewHolder {
			public ImageView img_commentprfpic;
			public CustomTextView txt_commentname, txt_commentdate, txt_commenteddetails, txt_subject;
		}

	}

	private class UsersAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleUserDetails> circles = null;

		public UsersAdapter(Context context, ArrayList<CircleUserDetails> arrcircle) {

			this.mContext = context;
			this.circles = arrcircle;
			this.inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		public void refreshData(ArrayList<CircleUserDetails> arrJobs) {
			this.circles = arrJobs;
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
				convertView = inflater.inflate(R.layout.inflate_circle_user, parent, false);
				/** Comment Section */

				mHolder.img_commentprfpic = (ImageView) convertView.findViewById(R.id.inflate_comment_img_pic);
				mHolder.txt_name = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_name);
				mHolder.txt_member_since = (CustomTextView) convertView.findViewById(R.id.inflate_member_since);
				mHolder.txt_email = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_email);
				mHolder.txt_grdyear = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_yeargrd);

				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			CircleUserDetails mCircleUserDetails = circles.get(position);

			String url = mCircleUserDetails.getUserimage();
			if (url != null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}

			mHolder.txt_name.setText(Util.capitalize(mCircleUserDetails.getUsername()));
			mHolder.txt_email.setText(mCircleUserDetails.getEmailId());
			if (!(mCircleUserDetails.getMemberSince() == null || mCircleUserDetails.getMemberSince().length() == 0)) {
				mHolder.txt_member_since.setText("Member Since: " + mCircleUserDetails.getMemberSince());
			}
			if (Integer.parseInt(mCircleUserDetails.getUseryearofgrad()) > 0) {
				mHolder.txt_grdyear.setText(mCircleUserDetails.getUseryearofgrad());
			}

			return convertView;
		}

		private class ViewHolder {
			public ImageView img_commentprfpic;
			public CustomTextView txt_name, txt_email, txt_member_since, txt_grdyear;
		}
	}

	private class PendingUsersAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleUserDetails> circles = null;

		public PendingUsersAdapter(Context context, ArrayList<CircleUserDetails> arrcircle) {

			this.mContext = context;
			this.circles = arrcircle;
			this.inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		public void refreshData(ArrayList<CircleUserDetails> arrJobs) {
			this.circles = arrJobs;
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
				convertView = inflater.inflate(R.layout.inflate_pendingreqst_row, parent, false);
				/** Comment Section */

				mHolder.img_commentprfpic = (ImageView) convertView.findViewById(R.id.inflate_comment_img_pic);
				mHolder.txt_name = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_name);
				mHolder.txt_email = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_email);
				mHolder.txt_grdyear = (CustomTextView) convertView.findViewById(R.id.inflate_comment_txt_yeargrd);
				mHolder.txtbtn_accept = (CustomTextView) convertView.findViewById(R.id.inflate_pendingrqst_btnaccept);
				mHolder.txtbtn_reject = (CustomTextView) convertView.findViewById(R.id.inflate_pendingrqst_btnreject);

				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			CircleUserDetails mCircleUserDetails = circles.get(position);
			final String Userid = mCircleUserDetails.getUserid();

			String url = mCircleUserDetails.getUserimage();
			if (url != null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}

			mHolder.txt_name.setText(mCircleUserDetails.getUsername());
			mHolder.txt_email.setText(mCircleUserDetails.getUseryearofgrad());
			mHolder.txt_grdyear.setText(mCircleUserDetails.getUseryearofgrad());

			mHolder.txtbtn_accept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					acceptRequest(Userid);

				}
			});
			mHolder.txtbtn_reject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rejectRequest(Userid);

				}
			});

			return convertView;
		}

		private class ViewHolder {
			public ImageView img_commentprfpic;
			public CustomTextView txt_name, txt_email, txt_grdyear, txtbtn_accept, txtbtn_reject;
		}
	}

	private void joinCircleEvent(String circleID) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JoinCircleParser mJoinCircleParser = new JoinCircleParser();
		mJoinCircleParser.setJoincircleparserinterface(new JoinCircleParserInterface() {

			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Successfully Joined Club");
				isDataModify = true;
				status_circle.setText("Unjoin");
				isAlreadyJoined = true;
			}

			@Override
			public void OnError() {

			}
		});
		mJoinCircleParser.parse(context, mJoinCircleParser.getBody(circleID), authorization);
	}

	private void unjoinCircleEvent(String circleID) {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		UnJoinCircleParser mUnJoinCircleParser = new UnJoinCircleParser();
		mUnJoinCircleParser.setUnjoincircleparserinterface(new UnJoinCircleParserInterface() {

			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Successfully unjoined club");
				isDataModify = true;
				status_circle.setText("Join");
				isAlreadyJoined = false;
			}

			@Override
			public void OnError() {

			}
		});
		mUnJoinCircleParser.parse(context, mUnJoinCircleParser.getBody(circleID), authorization);
	}

	private void returnResult() {
		try {
			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putBoolean("isDataModify", isDataModify);
			intent.putExtras(mBundle);
			setResult(RESULT_OK, intent);
			overridePendingTransition(0, 0);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
