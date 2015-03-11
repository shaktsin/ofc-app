package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.FragmentJoinCircle.JoinCircleInterface;
import com.ofcampus.activity.FragmentYourCircle.YourCircleInterface;
import com.ofcampus.component.PagerSlidingTabStripForCircle;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CircleListParser;
import com.ofcampus.parser.CircleListParser.CircleListParserInterface;

public class ActivityCircle extends ActionBarActivity implements OnPageChangeListener,YourCircleInterface,JoinCircleInterface{

	
	private Context context;
	private static String Authtoken="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle);

		context=ActivityCircle.this;
		
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Circle");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initiliz();
		getAllCircleList(true);
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
		if (((OfCampusApplication)context.getApplicationContext()).isNewCircleCreated) {
			getAllCircleList(false);
			((OfCampusApplication)context.getApplicationContext()).isNewCircleCreated=false;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_circle, menu);
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_createcircle:
			startActivity(new Intent(ActivityCircle.this,ActivityCreateCircle.class));
			overridePendingTransition(0,0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Pager Page Selected.
	 */
	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			
			break;
		case 1:
			
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	private void initiliz() {
		tabs = (PagerSlidingTabStripForCircle) findViewById(R.id.circle_tabs);
		pager = (ViewPager) findViewById(R.id.circle_tabpager);
		adapter = new SelectionPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);
		pager.setOffscreenPageLimit(2);
		tabs.setOnPageChangeListener(this);
	}

    /*Pager section*/
    private PagerSlidingTabStripForCircle tabs;
	private ViewPager pager;
	private SelectionPagerAdapter adapter;
	private FragmentYourCircle mYourCircle;
	private FragmentJoinCircle mJoinCircle;
	
	public class SelectionPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Your Circle", "Join Circle" };

		public SelectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				mYourCircle = FragmentYourCircle.newInstance(position,ActivityCircle.this);
				mYourCircle.setYourcircleinterface(ActivityCircle.this);
				return mYourCircle; 
			case 1:
				mJoinCircle = FragmentJoinCircle.newInstance(position, ActivityCircle.this);
				mJoinCircle.setJoincircleinterface(ActivityCircle.this);
				return mJoinCircle;
			}
			return null;

		}
	}

	
	private void getAllCircleList(boolean b){ 
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		CircleListParser mCircleListParser=new CircleListParser();
		mCircleListParser.setCirclelistparserinterface(new CircleListParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<CircleDetails> circlerList) {
				if (circlerList!=null && circlerList.size()>=1) {
					shortCircleList(circlerList);
				}
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mCircleListParser.parse(context, mCircleListParser.getBody("0","8"), Authtoken,b);
//		{"userId":11,"pageNo":0, "perPage":8, "appName":"ofCampus", "plateFormId":0}
	}
	
	private void shortCircleList(ArrayList<CircleDetails> circlerList){
		ArrayList<CircleDetails> joincircle=new ArrayList<CircleDetails>();
		ArrayList<CircleDetails> yourcirclecircle=new ArrayList<CircleDetails>();
		
		for (CircleDetails circleDetails : circlerList) {
			if (circleDetails.getJoined().equals("true")) {
				yourcirclecircle.add(circleDetails);
			}else {
				joincircle.add(circleDetails);
			}
		}
		
		mYourCircle.refreshData(yourcirclecircle);
		mJoinCircle.refreshData(joincircle);
	}
	
	@Override
	public void refreshFromJoinView() {
		getAllCircleList(false);
		
	}

	@Override
	public void refreshFromYourView() {
		getAllCircleList(false);
	}

}