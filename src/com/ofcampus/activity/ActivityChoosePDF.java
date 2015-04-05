/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.ofcampus.R;
import com.ofcampus.opendirectory.DirectoryFragment;
import com.ofcampus.opendirectory.DirectoryFragment.DocumentSelectActivityDelegate;

public class ActivityChoosePDF extends ActionBarActivity {


	private Toolbar toolbar;
	private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment mDirectoryFragment; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directory);
		
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Directory");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        
        mDirectoryFragment=new DirectoryFragment();
        mDirectoryFragment.setDelegate(new DocumentSelectActivityDelegate() {
			
			@Override
			public void startDocumentSelectActivity() {
				
			}
			
			@Override
			public void didSelectFiles(DirectoryFragment activity,
					ArrayList<String> files) {
				mDirectoryFragment.showErrorBox(files.get(0).toString()); 
			}

			@Override
			public void updateToolBarName(String name) {
				toolbar.setTitle(name);
				
			}
		});
        fragmentTransaction.add(R.id.fragment_container, mDirectoryFragment,"" + mDirectoryFragment.toString());
		fragmentTransaction.commit();
        
	}
	
	@Override
	protected void onDestroy() {
		mDirectoryFragment.onFragmentDestroy();
		super.onDestroy();
	}

	
	@Override
	public void onBackPressed() {
		if (mDirectoryFragment.onBackPressed_()) {
			super.onBackPressed();
		}
	}
}