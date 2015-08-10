/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.SearchData;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.SearchParser;

public class ActivitySearchList extends ActionBarActivity {
	private ListView searchList;
	private Context context;

	private SearchView searchView = null;
	private String tocken = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchlist);

		context = ActivitySearchList.this;
		tocken = UserDetails.getLoggedInUser(context).getAuthtoken();

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initiliz();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Util.HideKeyBoard(context, searchView);
		overridePendingTransition(0, R.anim.slide_right);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_action_search);
		if (searchItem != null) {
			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		}
		if (searchView != null) {
			searchView.setIconified(false);
			searchView.setQueryHint("Search job,circle,user");
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					searchEvent(query);
					return false;
				}

				@Override
				public boolean onQueryTextChange(final String query) {
					getWindow().getDecorView().postDelayed(new Runnable() {

						@Override
						public void run() {
							searchEvent(query);
						}
					}, 200);
					return false;
				}
			});

		}
		return super.onCreateOptionsMenu(menu);
	}

	private void initiliz() {
		searchList = (ListView) findViewById(R.id.activity_search_searchlist);
		mExampleAdapter = new ExampleAdapter(context, new ArrayList<SearchData>());
		searchList.setAdapter(mExampleAdapter);
	}

	private ExampleAdapter mExampleAdapter;
	private SearchParser mParser = null;
	private ArrayList<SearchData> arrSearchData = new ArrayList<SearchData>();

	private void searchEvent(final String searchString) {

		if (searchString.length() == 0 || searchString.length() < 3) {
			// Util.ShowToast(mContext, "Please enter minimum 3 character.");
			mExampleAdapter.refresh(new ArrayList<SearchData>());
			return;
		}

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Thread.sleep(200);
				if (mParser == null) {
					mParser = new SearchParser();
				}
				mParser.doInBackground_(mParser.getBody(searchString), tocken);
				arrSearchData = mParser.SearchDataList;
				searchHandler.sendEmptyMessage(0);

			}
		}).start();
	}

	Handler searchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (arrSearchData != null && arrSearchData.size() >= 1) {
				mExampleAdapter.refresh(arrSearchData);
			} else {
				mExampleAdapter.refresh(new ArrayList<SearchData>());
			}

		}
	};

	public class ExampleAdapter extends BaseAdapter {

		private ArrayList<SearchData> items;
		private LayoutInflater inflater;

		public ExampleAdapter(Context context, ArrayList<SearchData> items) {
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items = items;

		}

		public void refresh(ArrayList<SearchData> items_) {
			this.items.clear();
			this.items.addAll(items_);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return (items == null) ? 0 : items.size();
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

			ViewHolder mViewHolder;
			if (convertView == null) {
				mViewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.inflate_search_item_row, null);
				mViewHolder.search_itemtext = (TextView) convertView.findViewById(R.id.search_itemtext);

				convertView.setTag(mViewHolder);

			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			SearchData mSearchData = items.get(position);
			switch (mSearchData.getmSearchType()) {
			case USERS:
				mViewHolder.search_itemtext.setText(mSearchData.getData() + " in Users");
				break;

			case CIRCLE:
				mViewHolder.search_itemtext.setText(mSearchData.getData() + " in Clubs");
				break;

			case POSTS:
				if (mSearchData.getDatatype().equals("3")) {
					mViewHolder.search_itemtext.setText(mSearchData.getData() + " in NewsFeed");
				} else if (mSearchData.getDatatype().equals("1")) {
					mViewHolder.search_itemtext.setText(mSearchData.getData() + " in Classifieds");
				} else {
					mViewHolder.search_itemtext.setText(mSearchData.getData() + " in Post");
				}
				break;

			default:
				break;
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SearchData mSearchData = items.get(position);
					if (mSearchData != null) {
						gotToScreen(mSearchData);
					}

				}
			});

			convertView.setBackgroundColor(Color.parseColor("#E5E5E5"));
			mViewHolder.search_itemtext.setTextColor(Color.parseColor("#737373"));

			return convertView;
		}

		private class ViewHolder {
			TextView search_itemtext;
		}

	}

	private void gotToScreen(SearchData mSearchData) {

		switch (mSearchData.getmSearchType()) {

		case USERS:
			JobDetails mJobDetails = new JobDetails();
			mJobDetails.setId(mSearchData.getId());

			Intent mIntent_ = new Intent(context, ActivityJobPostedUserDetails.class);
			mIntent_.putExtra("isUserCame", (mJobDetails.getId().equals(UserDetails.getLoggedInUser(context).getUserID())) ? true : false);
			mIntent_.putExtra("userID", mJobDetails.getId());
			context.startActivity(mIntent_);
			((Activity) context).overridePendingTransition(0, 0);

			searchView.clearFocus();
			break;

		case CIRCLE:
			CircleDetails mCircleDetails = new CircleDetails();
			mCircleDetails.setId(mSearchData.getId());
			mCircleDetails.setAdmin("false");
			((OfCampusApplication) context.getApplicationContext()).mCircleDetails_ = mCircleDetails;
			Intent mIntentClubs = new Intent(context, ActivityCircleProfile.class);
			mIntentClubs.putExtra("CircleType", Util.CircleType.CLUBS.ordinal());
			context.startActivity(mIntentClubs);
			overridePendingTransition(0, 0);
			searchView.clearFocus();
			break;

		case POSTS:
			JobDetails jdetails = new JobDetails();
			jdetails.setPostid(mSearchData.getId());
			if (mSearchData.getDatatype().equals("3")) {
				((OfCampusApplication) context.getApplicationContext()).jobdetails = jdetails;
				Intent mIntent = new Intent(context, ActivityNewsDetails.class);
				Bundle mBundle = new Bundle();
				mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[1]);
				mIntent.putExtras(mBundle);
				startActivity(mIntent);
				((Activity) context).overridePendingTransition(0, 0);
			} else if (mSearchData.getDatatype().equals("1")) {
				((OfCampusApplication) context.getApplicationContext()).jobdetails = jdetails;
				Intent mIntent = new Intent(context, ActivityClassifiedDetails.class);
				Bundle mBundle = new Bundle();
				mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[2]);
				mIntent.putExtras(mBundle);
				startActivity(mIntent);
				((Activity) context).overridePendingTransition(0, 0);
			} else {
				((OfCampusApplication) context.getApplicationContext()).jobdetails = jdetails;
				Intent mIntent = new Intent(context, ActivityJobDetails.class);
				Bundle mBundle = new Bundle();
				mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[0]);
				mIntent.putExtras(mBundle);
				startActivity(mIntent);
				((Activity) context).overridePendingTransition(0, 0);
			}
			searchView.clearFocus();
			break;

		default:
			break;

		}
	}
}
