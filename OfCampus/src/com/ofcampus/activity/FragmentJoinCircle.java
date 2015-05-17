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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.ofcampus.parser.CircleListParser.CircleListParserInterface;
import com.ofcampus.parser.JoinCircleParser;
import com.ofcampus.parser.JoinCircleParser.JoinCircleParserInterface;
import com.ofcampus.ui.CustomTextView;

public class FragmentJoinCircle extends Fragment {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;

	private static String Authtoken = "";
	private ListView joincircle_list;
	private JoinCircleListAdapter mJoinCircleListAdapter;

	/*** For Load more ****/
	private int pageNo = 0;
	private int pagecount = 8;
	private int minimumofsets = 7, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;
	private RelativeLayout footer_pg;

	private static boolean isChapter_ = false;

	public static FragmentJoinCircle newInstance(Context mContext, boolean isChapter, int position) {
		FragmentJoinCircle f = new FragmentJoinCircle();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		isChapter_ = isChapter;
		context = mContext;
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_joincircle, null);
		initilizView(view);
		firstCalling(false);
		return view;
	}

	private void initilizView(View view) {
		joincircle_list = (ListView) view.findViewById(R.id.fragmentjoincircle_list);
		mJoinCircleListAdapter = new JoinCircleListAdapter(context, new ArrayList<CircleDetails>());
		joincircle_list.setAdapter(mJoinCircleListAdapter);

		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);
		joincircle_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mJoinCircleListAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
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

	public void firstCalling(boolean b) {
		resetAllCond();
		getAllCircleList(b, 0, 8);
	}

	private void resetAllCond() {
		pageNo = 0;
		pagecount = 8;
		minimumofsets = 7;
		mLastFirstVisibleItem = 0;
	}

	private void joinCircleEvent(String circleID, final int position_) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JoinCircleParser mJoinCircleParser = new JoinCircleParser();
		mJoinCircleParser.setJoincircleparserinterface(new JoinCircleParserInterface() {

			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Successfully Joined "+(isChapter_?"chapter":"club"));
				mJoinCircleListAdapter.removepostion(position_);
				if (joincircleinterface != null) {
					joincircleinterface.CircleJoin();
				}
			}

			@Override
			public void OnError() {

			}
		});
		mJoinCircleParser.parse(context, mJoinCircleParser.getBody(circleID), Authtoken);
	}

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
						mJoinCircleListAdapter.refreshData(circlerList);
						pageNo = pageNo_ + 1;
					} else {
						mJoinCircleListAdapter.addMoreData(circlerList);
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
		mCircleListParser.parse(context, mCircleListParser.getBody(pageNo_, pagecount_,isChapter_), Authtoken, b);
	}

	public class JoinCircleListAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleDetails> circles = null;

		public JoinCircleListAdapter(Context context, ArrayList<CircleDetails> arrcircle) {

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
				// mHolder.txt_joined=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_joined);
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
			// mHolder.txt_joined.setText("Join");
			// mHolder.txt_membno.setText(mCircleDetails.getMembers()+"\n Members");
			// mHolder.txt_postno.setText(mCircleDetails.getPosts()+"\n Posts");

			// mHolder.join_btn.setText("Join");

			String circleName = mCircleDetails.getName();
			String camelCaseName = Character.toString(Character.toUpperCase(circleName.charAt(0))) + circleName.substring(1).toLowerCase();
			// circleName = new String()circleName.
			mHolder.txt_name.setText(camelCaseName);
			String post_and_members_details = mCircleDetails.getMembers() + " members," + mCircleDetails.getPosts() + " posts";
			mHolder.txt_post_and_members.setText(post_and_members_details);
			// mHolder.last_post.setText("This is the tribute to all metallica fans!");

			mHolder.join_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					joinCircleEvent(circleID, position);
					// ((Button) v).setEnabled(false);
					Button btn = (Button) v;
					btn.setFocusableInTouchMode(false);
					btn.setFocusable(false);
					// btn.setBackgroundDrawable((R.drawable.btn_pressed);
					// btn.setEnabled(false);
					// btn.setText("Member");
					// btn.setTextColor(Color.parseColor("#ffffff"));
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

	public JoinCircleInterface joincircleinterface;

	public JoinCircleInterface getJoincircleinterface() {
		return joincircleinterface;
	}

	public void setJoincircleinterface(JoinCircleInterface joincircleinterface) {
		this.joincircleinterface = joincircleinterface;
	}

	public interface JoinCircleInterface {
		public void CircleJoin();
		// public void refreshAllFromJoinCircle();
	}
}
