/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.ofcampus.activity.FragmentJoinCircle.JoinCircleInterface;
import com.ofcampus.activity.FragmentYourCircle.YourCircleInterface;
import com.ofcampus.component.PagerSlidingTabStripForCircle;
import com.ofcampus.model.UserDetails;

public class ActivityCircle extends ActionBarActivity implements OnPageChangeListener, YourCircleInterface, JoinCircleInterface {

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
		}
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initiliz();
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
		if (!isChapter && ((OfCampusApplication) context.getApplicationContext()).isNewCircleCreated) {
			((OfCampusApplication) context.getApplicationContext()).isNewCircleCreated = false;
			CircleJoin();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isChapter) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_circle, menu);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 91 && resultCode == RESULT_OK && data != null) {
			boolean isModify = data.getExtras().getBoolean("isDataModify");
			if (isModify) {
				refreshView();
			}
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

	/* Pager section */
	private PagerSlidingTabStripForCircle tabs;
	private ViewPager pager;
	private SelectionPagerAdapter adapter;
	private FragmentYourCircle mYourCircle;
	private FragmentJoinCircle mJoinCircle;

	public class SelectionPagerAdapter extends FragmentStatePagerAdapter {

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
				mYourCircle = FragmentYourCircle.newInstance(ActivityCircle.this, isChapter, position);
				mYourCircle.setYourcircleinterface(ActivityCircle.this);
				return mYourCircle;
			case 1:
				mJoinCircle = FragmentJoinCircle.newInstance(ActivityCircle.this,isChapter,position);
				mJoinCircle.setJoincircleinterface(ActivityCircle.this);
				return mJoinCircle;
			}
			return null;

		}
	}

	@Override
	public void CircleJoin() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mYourCircle.firstCalling(false);
			}
		}, postDelayTime);
	}

	@Override
	public void CircleUnJoined() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mJoinCircle.firstCalling(false);
			}
		}, postDelayTime);

	}

	public void refreshView() {
		CircleJoin();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CircleUnJoined();
			}
		}, postDelayTime);
	}
}