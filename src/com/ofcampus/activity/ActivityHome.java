package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.SlideMenuAdapter;
import com.ofcampus.model.UserDetails;

public class ActivityHome extends ActionBarActivity {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

	private String NAME = "";
	private String EMAIL = "";
	private int PROFILE = R.drawable.ic_profilepic;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private DrawerLayout Drawer;                                  // Declaring DrawerLayout

    private ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        mContext=ActivityHome.this;
        loadProfileData();
        initilizActionBarDrawer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
    
    
    private void initilizActionBarDrawer(){
        
        /* Assinging the toolbar object ot the view
        and setting the the Action bar to our toolbar
         */
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            toolbar.setTitle("OfCampus");
            setSupportActionBar(toolbar);

            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

            mAdapter = new SlideMenuAdapter(Util.TITLES,Util.ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
            // And passing the titles,icons,header view name, header view email,
            // and header view profile picture

            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


            Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
            mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                    // open I am not going to put anything here)
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    // Code here will execute once drawer is closed
                }



            }; // Drawer Toggle Object Made
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();    // Finally we set the drawer toggle sync State
    }
    
    private void loadProfileData(){
    	UserDetails mUserDetails = UserDetails.getLoggedInUser(mContext);
    	EMAIL = mUserDetails.getEmail();
    	NAME = mUserDetails.getName();
    }
}