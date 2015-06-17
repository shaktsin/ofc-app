/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.ClassifiedListBaseAdapter;
import com.ofcampus.adapter.ClassifiedListBaseAdapter.ClassifiedListInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ClassifiedListParser;
import com.ofcampus.parser.ClassifiedListParser.ClassifiedListParserInterface;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;
import com.ofcampus.ui.ReplyDialog;

public class FragmentClassifieds extends Fragment implements OnClickListener, ClassifiedListInterface, OnRefreshListener {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;

	private ListView classifiedlist;
	private RelativeLayout footer_pg;
	private ClassifiedListBaseAdapter mClassifiedListAdapter;
	private String tocken = "";

	/*** For Load more ****/
	public String firsttID = "", lastID = "";
	private int minimumofsets = 5, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;

	private SwipeRefreshLayout swipeLayout;

	public ArrayList<JobDetails> notifyfeeds = null;

	public static FragmentClassifieds newInstance(int position, Context mContext) {
		FragmentClassifieds f = new FragmentClassifieds();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context = mContext;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_classifieds, null);
		initilizView(view);
		initilizeSwipeRefresh(view);
		loadData(true);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	@Override
	public void firstIDAndlastID(String fstID, String lstID) {
		firsttID = fstID;
		lastID = lstID;
	}

	@Override
	public void replyClickEvent(JobDetails mJobDetails) {
		new ReplyDialog(context, mJobDetails);
	}

	@Override
	public void onRefresh() {
		pulltorefreshcall();
	}

	private void initilizView(View view) {
		classifiedlist = (ListView) view.findViewById(R.id.activity_home_classifiedlist);
		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);

		mClassifiedListAdapter = new ClassifiedListBaseAdapter(context, new ArrayList<JobDetails>());
		mClassifiedListAdapter.setClassifiedlistinterface(this);
		classifiedlist.setAdapter(mClassifiedListAdapter);

		classifiedlist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mClassifiedListAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						loadMore(lastID);
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});

	}

	private void initilizeSwipeRefresh(View v) {
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.pull_blue_bright, R.color.pull_green_light, R.color.pull_orange_light, R.color.pull_red_light);
	}

	public int getAdapterCount() {
		if (mClassifiedListAdapter == null) {
			return 0;
		} else {
			return mClassifiedListAdapter.getCount();
		}
	}

	/**
	 * Initial Load News Calling.
	 * 
	 * @param b
	 */
	public void loadData(boolean b) {
		UserDetails mUserDetails = UserDetails.getLoggedInUser(context);
		tocken = mUserDetails.getAuthtoken();

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		ClassifiedListParser mClassifiedListParser = new ClassifiedListParser();
		mClassifiedListParser.setNewsfeedlistparserinterface(new ClassifiedListParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> classifiedsList) {
				if (classifiedsList != null && classifiedsList.size() >= 1) {
					mClassifiedListAdapter.refreshData(classifiedsList);
				}
			}

			@Override
			public void OnError() {
				refreshComplete();
			}
		});
		mClassifiedListParser.isShowingPG_ = b;
		mClassifiedListParser.parse(context, mClassifiedListParser.getBody(), tocken);
	}

	/** News Sync Process 07 April 20015 **/
	public boolean isNewsComming() {
		if (!firsttID.equals("") && notifyfeeds == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNewsCommingFstTime() {
		if (firsttID.equals("") && lastID.equals("") && getAdapterCount() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private ClassifiedListParser mClassifiedListParser = null;

	public String getUpdateClassifiedCount() {
		String count = "";
		try {
			if (mClassifiedListParser == null) {
				mClassifiedListParser = new ClassifiedListParser();
			}
			ArrayList<JobDetails> news = null;
			if (isNewsCommingFstTime() || isNewsComming()) {
				news = notifyfeeds = mClassifiedListParser.bgSyncCalling(context, mClassifiedListParser.getBody(firsttID, 1 + ""), tocken);
			}
			count = (news != null && news.size() >= 1) ? news.size() + "" : "";
			mClassifiedListParser = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/** News Sync Process **/

	private void pulltorefreshcall() {
		if (notifyfeeds != null && notifyfeeds.size() >= 1) {
			mClassifiedListAdapter.refreshSwipeData(notifyfeeds);
			notifyfeeds = null;
			if (fgclassifiedinterface != null) {
				fgclassifiedinterface.pullToRefreshCallCompleteForClass();
			}
		} else {
			Util.ShowToast(context, "No more News updated.");
		}
		refreshComplete();
	}

	/**
	 * Load more Refresh Load News Calling.
	 */
	private void loadMore(String JobID) {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		ClassifiedListParser mClassifiedListParser = new ClassifiedListParser();
		mClassifiedListParser.setNewsfeedlistparserinterface(new ClassifiedListParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> classifiedList) {
				if (classifiedList != null && classifiedList.size() >= 1) {
					mClassifiedListAdapter.refreshloadmoreData(classifiedList);
				} else {
					Util.ShowToast(context, "No more Classifieds available.");
				}
				refreshComplete();
			}

			@Override
			public void OnError() {
				refreshComplete();
			}
		});
		mClassifiedListParser.isShowingPG_ = false;
		mClassifiedListParser.parse(context, mClassifiedListParser.getBody(JobID, 2 + ""), tocken);
	}

	/**
	 * Check progress showing or not.
	 */
	public void refreshComplete() {
		if (swipeLayout.isRefreshing()) {
			swipeLayout.setRefreshing(false);
		}
		if (footer_pg.getVisibility() == View.VISIBLE) {
			footer_pg.setVisibility(View.GONE);
			loadingMore = false;
		}
	}

	@Override
	public void hideClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 1);
	}

	@Override
	public void impClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 2);
	}

	@Override
	public void unimpClieckEvent(JobDetails mJobDetails) {
		UnImptCalling(mJobDetails, 11);
	}

	@Override
	public void likeCliekEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 13);
	}

	private void HideCalling(final JobDetails mJobDetails, final int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostJobHideMarkedParser markedParser = new PostJobHideMarkedParser();
		markedParser.setPostjobhidemarkedparserinterface(new PostJobHideMarkedParserInterface() {

			@Override
			public void OnSuccess() {
				if (state == 1 || state == 3) {
					JOBListTable.getInstance(context).deleteSpamJOb(mJobDetails);
					mClassifiedListAdapter.hideNews(mJobDetails);
				} else if (state == 2) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 1;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).inserJobData(mJobDetails);
					mClassifiedListAdapter.importantNews(mJobDetails);
				} else if (state == 11) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 0;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mClassifiedListAdapter.unimportantNews(mJobDetails);
				} else if (state == 13) {
					// ArrayList<JobDetails> arr=new ArrayList<JobDetails>();
					// mJobDetails.like=0;
					// arr.add(mJobDetails);
					// JOBListTable.getInstance(context).inserJobData(arr);
					// ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mClassifiedListAdapter.likRefreshJob(mJobDetails);
				}
			}

			@Override
			public void OnError() {

			}
		});
		markedParser.parse(context, markedParser.getBody(state + "", mJobDetails.getPostid()), tocken);
	}

	private void UnImptCalling(final JobDetails mJobDetails, final int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostUnHideUnImpParser PostUnHideUnImpParser = new PostUnHideUnImpParser();
		PostUnHideUnImpParser.setPostunhideunimpparserinterface(new PostUnHideUnImpParserInterface() {

			@Override
			public void OnSuccess() {
				ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
				mJobDetails.important = 0;
				arr.add(mJobDetails);
				JOBListTable.getInstance(context).inserJobData(arr);
				ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
				mClassifiedListAdapter.unimportantNews(mJobDetails);
			}

			@Override
			public void OnError() {

			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", mJobDetails.getPostid()), tocken);
	}

	/**
	 * Interface for News Fragment.
	 */
	public FgClassifiedInterface fgclassifiedinterface;

	public FgClassifiedInterface getFgclassifiedinterface() {
		return fgclassifiedinterface;
	}

	public void setFgclassifiedinterface(FgClassifiedInterface fgclassifiedinterface) {
		this.fgclassifiedinterface = fgclassifiedinterface;
	}

	public interface FgClassifiedInterface {
		public void pullToRefreshCallCompleteForClass();// After pull to refresh
														// complete remove the
														// notification.
	}

}