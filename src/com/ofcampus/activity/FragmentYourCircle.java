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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.UnJoinCircleParser;
import com.ofcampus.parser.UnJoinCircleParser.UnJoinCircleParserInterface;
import com.ofcampus.ui.CustomTextView;

public class FragmentYourCircle extends Fragment {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	private static String Authtoken="";
	private ListView yourcircle_list ;
	private YourCircleListAdapter mYourCircleListAdapter;
	
	
    /***For Load more****/
    private int minimumofsets = 8,mLastFirstVisibleItem = 0;
    private boolean loadingMore = false;
    private RelativeLayout footer_pg;
    
    
    
	public static FragmentYourCircle newInstance(int position, Context mContext) {
		FragmentYourCircle f = new FragmentYourCircle();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_yourcircle, null);
		initilizView(view);
		return view;
	}

	private void initilizView(View view) {
		yourcircle_list = (ListView) view.findViewById(R.id.fragmentyourcircle_list);
		mYourCircleListAdapter=new YourCircleListAdapter(context, new ArrayList<CircleDetails>());
		yourcircle_list.setAdapter(mYourCircleListAdapter);
		
		
		
		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);
		yourcircle_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mYourCircleListAdapter != null
						&& totalItemCount > minimumofsets
						&& (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context,context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE); 
						loadingMore = true;
//						if (jobsfrginterface!=null) {
//							jobsfrginterface.loadcall(lastJobID);
//						}
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});
	}


	private void unjoinCircleEvent(String circleID) {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		UnJoinCircleParser mUnJoinCircleParser = new UnJoinCircleParser();
		mUnJoinCircleParser.setUnjoincircleparserinterface(new UnJoinCircleParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully UnJoin Circle.");
				if (yourcircleinterface!=null) {
					yourcircleinterface.refreshFromYourView();
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mUnJoinCircleParser.parse(context, mUnJoinCircleParser.getBody(circleID), Authtoken);
	}
	
	
	public void refreshData(ArrayList<CircleDetails> circles){
		mYourCircleListAdapter.refreshData(circles);
		if (circles!=null && circles.size()>=1) {
			yourcircle_list.setSelection(0);
		}
		
	}
	
	public class YourCircleListAdapter extends BaseAdapter{ 

		private Context mContext;
		private LayoutInflater inflater;
		private ArrayList<CircleDetails> circles=null; 
		
		public YourCircleListAdapter(Context context,ArrayList<CircleDetails> arrcircle){
		
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
			final String circleID=mCircleDetails.getId();
			
			mHolder.txt_joined.setText("UnJoin");
			mHolder.txt_membno.setText(mCircleDetails.getMembers()+"\n Members");
			mHolder.txt_postno.setText(mCircleDetails.getPosts()+"\n Posts");
			mHolder.txt_name.setText(mCircleDetails.getName());
			
			
			mHolder.txt_joined.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					unjoinCircleEvent(circleID);
				}
			});

			mHolder.txt_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent mIntent=new Intent(mContext,ActivityCircleProfile.class);
					Bundle mBundle=new Bundle();
					mBundle.putString("CircleID", circleID);
					mIntent.putExtras(mBundle);
					mContext.startActivity(mIntent);
					((Activity) mContext).overridePendingTransition(0,0);
				}
			});

			return convertView;
		}
		
		private class ViewHolder{
			CustomTextView txt_name,txt_postno,txt_membno,txt_joined;
		}

	}
	
	
	public YourCircleInterface yourcircleinterface;

	public YourCircleInterface getYourcircleinterface() {
		return yourcircleinterface;
	}

	public void setYourcircleinterface(YourCircleInterface yourcircleinterface) {
		this.yourcircleinterface = yourcircleinterface;
	}

	public interface YourCircleInterface {
		public void refreshFromYourView();
	}
}
