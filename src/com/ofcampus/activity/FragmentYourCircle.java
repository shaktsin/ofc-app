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
import com.ofcampus.parser.CircleActivateParser;
import com.ofcampus.parser.CircleActivateParser.CircleActivateParserInterface;
import com.ofcampus.parser.CircleDeActivateParser;
import com.ofcampus.parser.CircleDeActivateParser.CircleDeActivateParserInterface;
import com.ofcampus.parser.CircleJoinListParser;
import com.ofcampus.parser.CircleJoinListParser.CircleJoinListParserInterface;
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
	private int pageNo=0;
	private int pagecount=8;
    private int minimumofsets = 7,mLastFirstVisibleItem = 0;
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
		firstCalling(true); 
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
						getAllCircleList(false,pageNo,pagecount);
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});
	}

	public void firstCalling(boolean b){
		resetAllCond();
		getAllCircleList(b, 0, 8); 
	}

	private void resetAllCond(){
		pageNo=0;
		pagecount=8;
		minimumofsets = 7;
		mLastFirstVisibleItem = 0;
	}
	
	
	private void unjoinCircleEvent(String circleID, final int position_) {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		UnJoinCircleParser mUnJoinCircleParser = new UnJoinCircleParser();
		mUnJoinCircleParser.setUnjoincircleparserinterface(new UnJoinCircleParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully UnJoin Circle.");
				mYourCircleListAdapter.removepostion(position_); 
				if (yourcircleinterface!=null) {
					yourcircleinterface.CircleUnJoined();
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mUnJoinCircleParser.parse(context, mUnJoinCircleParser.getBody(circleID), Authtoken);
	}
	
	
	private void circleActivate(String circleID){
		CircleActivateParser mActivateParser=new CircleActivateParser();
		mActivateParser.setCircleactivateparserinterface(new CircleActivateParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully Activated your Circle.");
				pageNo=0;
				pagecount=8;
				getAllCircleList(false, 0, 8);
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mActivateParser.parse(context, mActivateParser.getBody(circleID), Authtoken);
	}
	
	private void circleDeActivate(String circleID){
		CircleDeActivateParser mDeActivateParser=new CircleDeActivateParser();
		mDeActivateParser.setCircleDeActivateparserinterface(new CircleDeActivateParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully DeActivated your Circle.");
				pageNo=0;
				pagecount=8;
				getAllCircleList(false, 0, 8);
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mDeActivateParser.parse(context, mDeActivateParser.getBody(circleID), Authtoken);
	}
	
	
	private void getAllCircleList(boolean b,final int pageNo_,int pagecount_){ 
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		CircleJoinListParser mCircleListParser=new CircleJoinListParser();
		mCircleListParser.setCirclejoinlistparserinterface(new CircleJoinListParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<CircleDetails> circlerList) {
				if (circlerList!=null && circlerList.size()>=1) {
					if (pageNo==0) {
						mYourCircleListAdapter.refreshData(circlerList);
						pageNo=pageNo_+1;
					}else {
						mYourCircleListAdapter.addMoreData(circlerList);
						pageNo=pageNo_+1;
						minimumofsets=minimumofsets+pagecount;
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
		mCircleListParser.parse(context, mCircleListParser.getBody(pageNo_,pagecount_), Authtoken,b);
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
		
		public void refreshData(ArrayList<CircleDetails> arrCircle){ 
			this.circles= arrCircle;
			notifyDataSetChanged();
		}
		
		public void addMoreData(ArrayList<CircleDetails> arrCircles){ 
			this.circles.addAll(arrCircles);
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
				//mHolder.txt_joined=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_joined);
				//mHolder.txt_membno=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_membno);
				//mHolder.txt_postno=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_postno);
				//mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_name);
				
				//mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
				//mHolder.img_own=(ImageView)convertView.findViewById(R.id.joblistview_img_imp);
				
				mHolder.last_post=(CustomTextView)convertView.findViewById(R.id.inflt_last_posts_details);
				mHolder.txt_post_and_members=(TextView)convertView.findViewById(R.id.post_and_members_info);
				mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.inflt_circlerow_txt_name);
				mHolder.join_btn = (Button)convertView.findViewById(R.id.join_circle);
				
				convertView.setTag(mHolder);
			}else {
				mHolder=(ViewHolder) convertView.getTag();
			}

			CircleDetails mCircleDetails=circles.get(position);
			final String circleID=mCircleDetails.getId();
			
			//mHolder.txt_joined.setText("UnJoin");
			String circleName = mCircleDetails.getName();
			String camelCaseName = Character.toString(Character.toUpperCase(circleName.charAt(0))) +
					circleName.substring(1).toLowerCase();
			
			mHolder.txt_name.setText(camelCaseName);
			String post_and_members_details = mCircleDetails.getMembers() + " members,"+ mCircleDetails.getPosts() + " posts";
			mHolder.txt_post_and_members.setText(post_and_members_details);
			//mHolder.last_post.setText("This is the tribute to all metallica fans!");
			mHolder.join_btn.setEnabled(true);
			mHolder.join_btn.setText("Unjoin");
			
			//mHolder.txt_membno.setText(mCircleDetails.getMembers()+"\n Members");
			//mHolder.txt_postno.setText(mCircleDetails.getPosts()+"\n Posts");
			//mHolder.txt_name.setText(mCircleDetails.getName());
			
			//mHolder.img_own.setVisibility(mCircleDetails.getAdmin().equals("true")?View.VISIBLE:View.GONE);
			//mHolder.img_own.setSelected(mCircleDetails.getAdmin().equals("true")?true:false); 
			
			if (mCircleDetails.getAdmin().equals("true")) { 
				//mHolder.img_arrow.setVisibility(View.VISIBLE);
				//mHolder.img_arrow.setSelected(mCircleDetails.getSelected().equals("true")?true:false); 
//				mHolder.img_arrow.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						if(circles.get(position).getSelected().equals("true")){
//							circleDeActivate(circleID);
//						}else {
//							circleActivate(circleID);
//						}
//					}
//				});
			}else {
				//mHolder.img_arrow.setVisibility(View.INVISIBLE);
			}
			
			mHolder.join_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					unjoinCircleEvent(circleID,position);
				}
			});

			mHolder.txt_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent mIntent=new Intent(mContext,ActivityCircleProfile.class);
//					Bundle mBundle=new Bundle();
//					mBundle.putString("CircleID", circleID);
//					mIntent.putExtras(mBundle);
					((OfCampusApplication)mContext.getApplicationContext()).mCircleDetails_=circles.get(position);
					mContext.startActivity(mIntent);
					((Activity) mContext).overridePendingTransition(0,0);
				}
			});

			return convertView;
		}
		
		private class ViewHolder{
			CustomTextView txt_name,last_post,txt_joined;
			TextView txt_post_and_members;
			Button join_btn;
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
		public void CircleUnJoined();
	}
}
