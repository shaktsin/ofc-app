package com.ofcampus.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ofcampus.R;

public class ActivityCreateJobNew  extends ActionBarActivity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createjob_new);

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Job");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
	

}