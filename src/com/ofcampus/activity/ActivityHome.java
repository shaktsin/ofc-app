package com.ofcampus.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.Util.JobDataReturnFor;
import com.ofcampus.activity.JobsFragment.JobsFrgInterface;
import com.ofcampus.adapter.SlideMenuAdapter;
import com.ofcampus.adapter.SlideMenuAdapter.viewCLickEvent;
import com.ofcampus.component.PagerSlidingTabStrip;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.FilterDataSets;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobList;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CountSyncParser;
import com.ofcampus.parser.FilterJobParser;
import com.ofcampus.parser.JobListParserNew;
import com.ofcampus.parser.JobListParserNew.JobListParserNewInterface;
import com.ofcampus.ui.FilterDialog;

public class ActivityHome extends ActionBarActivity implements OnClickListener,viewCLickEvent,OnPageChangeListener,JobsFrgInterface{

	

	private String NAME = "";
	private String EMAIL = "";
	private String tocken = "";
	private int PROFILE = R.drawable.ic_profilepic;

    private Toolbar toolbar;                            

    private RecyclerView mRecyclerView;                         
    private SlideMenuAdapter mAdapter;                   
    private RecyclerView.LayoutManager mLayoutManager;            
    private DrawerLayout Drawer;                                
    private SearchView searchView = null;
    private ImageView img_composejob;
    
    private ActionBarDrawerToggle mDrawerToggle;               
    private Context mContext;

    
    /*Pager section*/
    private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private JobsFragment mJobsFragment;
	private ClassifiedsFragment mClassifiedsFragment;
	private MeetupsFragment mMeetupsFragment;

	private TextView txt_countjob,txt_countclass ,txt_countmetup;
	
	/**Filter Data***/
	private  FilterDataSets mFilterDataSets=null;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        mContext=ActivityHome.this;
        loadProfileData();
        initilizActionBarDrawer();
        initilizePagerview();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			stopservice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			stopservice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			stopservice();
			startService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		if (searchItem != null) {
			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		}
		
		if (searchView != null) {
			searchView.setIconifiedByDefault(true);
			searchView.setQueryHint("Search job");
			searchView.setOnQueryTextListener(new OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String s) {
					searchView.clearFocus();
				    Util.ShowToast(mContext, "TextSubmit : " + s);
					return true;
				}
				
				@Override
				public boolean onQueryTextChange(String arg0) {
					return false;
				}
			});

			searchView.setOnSearchClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					closeDraware();
					img_composejob.setVisibility(View.GONE);
				}
			});
			
			MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
			    @Override
			    public boolean onMenuItemActionCollapse(MenuItem item) {
			    	img_composejob.setVisibility(View.VISIBLE);
			        return true;  
			    }

			    @Override
			    public boolean onMenuItemActionExpand(MenuItem item) {
			        return true; 
			    }
			});
			
			searchView.setOnCloseListener(new OnCloseListener() {
				
				@Override
				public boolean onClose() {
					img_composejob.setVisibility(View.VISIBLE);
					return false;
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }else if (id == R.id.action_filter) {
        	if (mFilterDataSets!=null) {
        		FilterDialog mDialog=new FilterDialog(mContext,mFilterDataSets);
            	mDialog.showDialog();
			}
        	return true;
		}
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_home_img_composejob:
			startActivity(new Intent(ActivityHome.this,ActivityCreateJobNew.class));
			overridePendingTransition(0,0);
			break;

		default:
			break;
		}
	}
    
	@Override
	public void OnViewItemClick(int position) {
		switch (position) {

		case 1:
			closeDraware();
			startActivity(new Intent(ActivityHome.this,ActivityMyProfile.class));
			overridePendingTransition(0,0);
			break;
		case 2:
			closeDraware();
			startActivity(new Intent(ActivityHome.this,ActivityMyPost.class));
			overridePendingTransition(0,0);
			break;
		case 3:
			closeDraware();
			startActivity(new Intent(ActivityHome.this,ActivityImportantmail.class));
			overridePendingTransition(0,0);
			break;
		case 4:
			closeDraware();
			startActivity(new Intent(ActivityHome.this,ActivitySettings.class));
			overridePendingTransition(0,0);
			break;
		case 5:
			closeDraware();
			showLogutDialog();
			break;

		default:
			break;
		}
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
		case 2:
			
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
	
	/**
	 * For Job Fragment Pull to refresh calling.
	 */
	@Override
	public void pulltorefreshcall(String jobID) {
		try {
			ArrayList<JobDetails> jobs = JOBListTable.getInstance(mContext).fatchJobData(JobDataReturnFor.syncdata);
			if (jobs != null && jobs.size() >= 1) {
				try {
					if (txt_countjob.getVisibility() == View.VISIBLE) {
						int cout = Integer.parseInt(txt_countjob.getText().toString());
						if (jobs.size() == cout) {
							txt_countjob.setVisibility(View.GONE);
						} else if (cout >= 1 && cout > jobs.size()) {
							txt_countjob.setVisibility(View.VISIBLE);
							txt_countjob.setText((cout - jobs.size()) + "");
						}
					}
				} catch (Exception e) {
					Log.e("pulltorefreshcall success", e.toString());
					e.printStackTrace();
				}
				mJobsFragment.refreshSwipeDataInAdapter(jobs);
				JOBListTable.getInstance(mContext).deleteoutDatedPost(jobs.size());
			} else {
				Util.ShowToast(mContext, "No more job updated.");
				mJobsFragment.refreshComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * For Job Fragment Pull to refresh calling.
	 */
	
	@Override
	public void loadcall(String jobID) {
		JobListParserNew mParserNew=new JobListParserNew();
		mParserNew.setJoblistparsernewinterface(new JobListParserNewInterface() {
			
			@Override
			public void OnSuccess(JobList mJobList) {
				if (mJobList != null) {
					ArrayList<JobDetails> jobs = mJobList.getJobs();
					if (jobs != null && jobs.size() >= 1) {
						mJobsFragment.refreshLoadMoreDataInAdapter(jobs);
					}else {
						Util.ShowToast(mContext, "No more job available.");
						mJobsFragment.refreshComplete();
					}
				}
			}

			@Override
			public void OnError() {
				mJobsFragment.refreshComplete();
			}
		});
		mParserNew.parse(mContext, mParserNew.getBody(jobID, 2+""), tocken,false);
	}
	
	
	@Override
	public void firstLoadCall() {
		new loadExistDataFromDB().execute();
	}
	
	
	private void initilizActionBarDrawer() {
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("OfCampus");
		setSupportActionBar(toolbar);

		mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
		mRecyclerView.setHasFixedSize(true);
		img_composejob = (ImageView)findViewById(R.id.activity_home_img_composejob);
		img_composejob.setOnClickListener(this);
		
		mAdapter = new SlideMenuAdapter(ActivityHome.this,Util.TITLES, Util.ICONS, NAME, EMAIL,PROFILE);
		mAdapter.setViewclickevent(this);
		mRecyclerView.setAdapter(mAdapter);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
		mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar,
				R.string.openDrawer, R.string.closeDrawer) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

		};
		Drawer.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}
    
    private void loadProfileData(){
    	UserDetails mUserDetails = UserDetails.getLoggedInUser(mContext);
    	EMAIL = mUserDetails.getEmail();
    	NAME = mUserDetails.getName();
    	tocken = mUserDetails.getAuthtoken();
    }
    
    private void initilizePagerview(){
    	
    	txt_countjob = (TextView) findViewById(R.id.activity_home_jobcount);
    	txt_countclass = (TextView) findViewById(R.id.activity_home_classcount);
    	txt_countmetup = (TextView) findViewById(R.id.activity_home_meetcount);
    	
    	tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);
		pager.setOffscreenPageLimit(3);
		tabs.setOnPageChangeListener(this);
    }
    
    private void closeDraware(){
    	if (Drawer.isDrawerOpen(GravityCompat.START)) {
			Drawer.closeDrawers();
		}
    }
    
    
    
    public void loadJobList() {

		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext,getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JobListParserNew mParserNew=new JobListParserNew();
		mParserNew.setJoblistparsernewinterface(new JobListParserNewInterface() {
			
			@Override
			public void OnSuccess(JobList mJobList) {
				if (mJobList != null) {
					ArrayList<JobDetails> jobs = mJobList.getJobs();
					if (jobs != null && jobs.size() >= 1) {
						mJobsFragment.refreshDataInAdapter(jobs);
					}
				}
			}

			@Override
			public void OnError() {

			}
		});
		mParserNew.parse(mContext, mParserNew.getBody(), tocken,true);
	}
    
    
    
    private void showLogutDialog(){
    	AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
    	alert.setTitle("Logout");
    	alert.setMessage("Do you want to logout?");
    	alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Util.ShowToast(mContext,"Successfully logout.");
				UserDetails.logoutUser(mContext);
				startActivity(new Intent(ActivityHome.this,ActivitySplash.class));
				overridePendingTransition(0,0);
				finish();
			}
		});
		alert.create();
		alert.show();
    }
    
    public class MyPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Jobs", "Classifieds","Meetups"};

		public MyPagerAdapter(FragmentManager fm) {
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
				mJobsFragment = JobsFragment.newInstance(position,ActivityHome.this);
				mJobsFragment.setJobsfrginterface(ActivityHome.this);
				return mJobsFragment;
			case 1:
				mClassifiedsFragment = ClassifiedsFragment.newInstance(position,ActivityHome.this);
				return mClassifiedsFragment;
			case 2:
				mMeetupsFragment = MeetupsFragment.newInstance(position,ActivityHome.this);
				return mMeetupsFragment;
			}
			return null;
			
		}
	}
    
    /**********************************************   Sync service ***********************************************************/
    
	private Timer timer;
	private MyTask mTask;
	private String[] count={"","",""};
	
	public void stopservice() {
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timer = null;
			}
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startService() {
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timer = null;
				timer = new Timer();
				mTask = new MyTask();
				timer.scheduleAtFixedRate(mTask, Util.delay, Util.period);
			}
			if (timer == null) {
				timer = new Timer();
				mTask = new MyTask();
				timer.scheduleAtFixedRate(mTask, Util.delay, Util.period);
			}
		} catch (Exception e) {
			Log.i("TaskTimerNullcheck", "TaskTimerNullcheck_excep");
			e.printStackTrace();
		}
	}

	private class MyTask extends TimerTask {
		@Override
		public void run() {
			try {
				
				if (Util.hasConnection(mContext)) {
					ArrayList<JobDetails> arrJOb = JOBListTable.getInstance(mContext).fatchJobData(JobDataReturnFor.syncdata); 
					if (arrJOb!=null && arrJOb.size()>=1) {
						count[0]=""+arrJOb.size();
						count[1]="";
						count[2]="";
					}else {
						if (mJobsFragment.firsttJobID!=null && !mJobsFragment.firsttJobID.equals("")) {
							CountSyncParser countSyncParser=new CountSyncParser();
							arrJOb = countSyncParser.parse(mContext, countSyncParser.getBody(mJobsFragment.firsttJobID, 1+""), tocken);
							if (arrJOb!=null && arrJOb.size()>=1) {
								count[0]=""+arrJOb.size();
								count[1]="";
								count[2]="";
							}else {
								count[0]="";
								count[1]="";
								count[2]="";
							}
						}
						
					}
					
					try {
						mFilterDataSets =	new FilterJobParser().parse(mContext, tocken);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					handler.sendEmptyMessage(0);
				}
				
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (count!=null) {
					String jobcount = count[0] ;
					String classcount = count[1] ;
					String meetupcount = count[2] ;
					if (jobcount!=null && !jobcount.equals("") && !jobcount.equals("0")) {
						txt_countjob.setVisibility(View.VISIBLE);
						txt_countjob.setText(jobcount);
					}else {
						txt_countjob.setVisibility(View.GONE);
					}
					if (classcount!=null && !classcount.equals("") && !classcount.equals("0")) {
						txt_countclass.setVisibility(View.VISIBLE);
						txt_countclass.setText(classcount);
					}else {
						txt_countclass.setVisibility(View.GONE);
					}
					if (meetupcount!=null && !meetupcount.equals("") && !meetupcount.equals("0")) {
						txt_countmetup.setVisibility(View.VISIBLE);
						txt_countmetup.setText(meetupcount);
					}else {
						txt_countmetup.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

    
	private class loadExistDataFromDB extends AsyncTask<Void, Void, Void> {

		private ArrayList<JobDetails> arrayJob;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				arrayJob = JOBListTable.getInstance(mContext).fatchJobData(JobDataReturnFor.Normal);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Fatching Data from DB", e.getMessage().toString());
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			try {
				if (arrayJob!=null && arrayJob.size()>=1) {
					mJobsFragment.refreshDataInAdapter(arrayJob);
				}else {
					loadJobList();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Load Data in list", e.getMessage().toString());
			}
			loadFilterData();
		}

	}
	
	
	private void loadFilterData(){
		if (Util.hasConnection(mContext)) {
			new loadFilterData().execute();
		}
	}
	
	private class loadFilterData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				mFilterDataSets =	new FilterJobParser().parse(mContext, tocken);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}
    
}