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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CircleListParser;
import com.ofcampus.parser.JoinCircleParser;
import com.ofcampus.parser.UnJoinCircleParser;
import com.ofcampus.parser.CircleListParser.CircleListParserInterface;
import com.ofcampus.parser.JoinCircleParser.JoinCircleParserInterface;
import com.ofcampus.parser.UnJoinCircleParser.UnJoinCircleParserInterface;
import com.ofcampus.ui.CustomTextView;

public class ActivityCircle extends ActionBarActivity {

	private Context context;
	private static String Authtoken = "";
	private int postDelayTime = 700;

	private boolean isChapter = false;
	private final String[] TITLES = { "Your Clubs", "Join Clubs" };
	private String title = "Clubs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle);

		context = ActivityCircle.this;
		Bundle mBundle = getIntent().getExtras();
		if (mBundle != null) {
			isChapter = mBundle.getBoolean("isChapterEvent");
			TITLES[0] = "Your Chapter";
			TITLES[1] = "Join Chapter";
			title = "Chapters";
			isChapter_ = true;
		}
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initiliz();

		getAllCircleList(true, pageNo, pagecount);
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
		// if (!isChapter && ((OfCampusApplication)
		// context.getApplicationContext()).isNewCircleCreated) {
		// ((OfCampusApplication)
		// context.getApplicationContext()).isNewCircleCreated = false;
		// CircleJoin();
		// }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isChapter) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_circle, menu);
			MenuItem item = menu.findItem(R.id.action_createcircle);
			item.setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_createcircle:
			startActivity(new Intent(ActivityCircle.this, ActivityCreateCircle.class));
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Pager Page Selected.
	 */
	// @Override
	// public void onPageSelected(int position) {
	// switch (position) {
	// case 0:
	//
	// break;
	// case 1:
	//
	// break;
	//
	// default:
	// break;
	// }
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 91 && resultCode == RESULT_OK && data != null) {
			boolean isModify = data.getExtras().getBoolean("isDataModify");
			// if (isModify) {
			// refreshView();
			// }
		}
	}

	private ListView circle_list;
	private YourCircleListAdapter mCircleListAdapter;

	/*** For Load more ****/
	private int pageNo = 0;
	private int pagecount = 8;
	private int minimumofsets = 7, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;
	private RelativeLayout footer_pg;

	private void initiliz() {

		circle_list = (ListView) findViewById(R.id.circle_list);
		mCircleListAdapter = new YourCircleListAdapter(context, new ArrayList<CircleDetails>());
		circle_list.setAdapter(mCircleListAdapter);

		footer_pg = (RelativeLayout) findViewById(R.id.activity_home_footer_pg);
		circle_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mCircleListAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						getAllCircleList(false, pageNo, pagecount);
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});
	}

	private boolean isChapter_ = false;

	private void getAllCircleList(boolean b, final int pageNo_, int pagecount_) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		CircleListParser mCircleListParser = new CircleListParser();
		mCircleListParser.setCirclelistparserinterface(new CircleListParserInterface() {

			@Override
			public void OnSuccess(ArrayList<CircleDetails> circlerList) {
				if (circlerList != null && circlerList.size() >= 1) {
					if (pageNo == 0) {
						mCircleListAdapter.refreshData(circlerList);
						pageNo = pageNo_ + 1;
					} else {
						mCircleListAdapter.addMoreData(circlerList);
						pageNo = pageNo_ + 1;
						minimumofsets = minimumofsets + pagecount;
					}
					footer_pg.setVisibility(View.GONE);
					loadingMore = false;
				}
			}

			@Override
			public void OnError() {
				footer_pg.setVisibility(View.GONE);
				loadingMore = false;
			}
		});
		mCircleListParser.parse(context, mCircleListParser.getBody(pageNo_, pagecount_, isChapter_), Authtoken, b);
	}

	public class YourCircleListAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleDetails> circles = null;

		public YourCircleListAdapter(Context context, ArrayList<CircleDetails> arrcircle) {

			this.mContext = context;
			this.circles = arrcircle;
			this.inflater = LayoutInflater.from(context);
		}

		public void refreshData(ArrayList<CircleDetails> arrCircle) {
			this.circles = arrCircle;
			notifyDataSetChanged();
		}

		public void addMoreData(ArrayList<CircleDetails> arrCircles) {
			this.circles.addAll(arrCircles);
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
				convertView = inflater.inflate(R.layout.inflate_circledetails, null);
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
			final String joined = mCircleDetails.getJoined();

			String circleName = mCircleDetails.getName();
			String camelCaseName = Character.toString(Character.toUpperCase(circleName.charAt(0))) + circleName.substring(1).toLowerCase();

			mHolder.txt_name.setText(camelCaseName);
			String post_and_members_details = mCircleDetails.getMembers() + " members," + mCircleDetails.getPosts() + " posts";
			mHolder.txt_post_and_members.setText(post_and_members_details);
			mHolder.join_btn.setEnabled(true);
			mHolder.join_btn.setText((joined.equals("false")) ? "Join" : "Unjoin");

			mHolder.join_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (joined.equals("false")) {
						joinCircleEvent(circleID, position);
					} else {
						unjoinCircleEvent(circleID, position);
					}

				}
			});

			mHolder.txt_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent mIntent = new Intent(mContext, ActivityCircleProfile.class);
					if (isChapter_) {
						mIntent.putExtra("isChapterEvent", true);
					}
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

	private void joinCircleEvent(String circleID, final int position_) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JoinCircleParser mJoinCircleParser = new JoinCircleParser();
		mJoinCircleParser.setJoincircleparserinterface(new JoinCircleParserInterface() {

			@Override
			public void OnSuccess(ArrayList<CircleDetails> circlerList) {
				Util.ShowToast(context, "Successfully Joined " + (isChapter_ ? "chapter" : "club"));
				mCircleListAdapter.refreshData(circlerList);
				pageNo = 1;
			}

			@Override
			public void OnError() {

			}
		});
		mJoinCircleParser.parse(context, mJoinCircleParser.getBody(circleID), Authtoken);
	}

	private void unjoinCircleEvent(String circleID, final int position_) {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		UnJoinCircleParser mUnJoinCircleParser = new UnJoinCircleParser();
		mUnJoinCircleParser.setUnjoincircleparserinterface(new UnJoinCircleParserInterface() {

			@Override
			public void OnSuccess(ArrayList<CircleDetails> circlerList) {
				Util.ShowToast(context, "Successfully unjoined " + (isChapter_ ? "chapter" : "club"));
				mCircleListAdapter.refreshData(circlerList);
				pageNo = 1;
			}

			@Override
			public void OnError() {

			}
		});
		mUnJoinCircleParser.parse(context, mUnJoinCircleParser.getBody(circleID), Authtoken);
	}

}