package com.ofcampus.activity;

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
import android.view.MenuItem;

import com.ofcampus.R;
import com.ofcampus.component.PagerSlidingTabStripForCircle;

public class ActivityCircle extends ActionBarActivity implements OnPageChangeListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle);

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Circle");
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
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
	private FragmentCreateCircle mCreateCircle;
	private FragmentJoinCircle mJoinCircle;
	
	public class SelectionPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Create Circle", "Join Circle" };

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
				mCreateCircle = FragmentCreateCircle.newInstance(position,ActivityCircle.this);
				return mCreateCircle;
			case 1:
				mJoinCircle = FragmentJoinCircle.newInstance(position, ActivityCircle.this);
				return mJoinCircle;
			}
			return null;

		}
	}

}