/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ofcampus.R;
import com.ofcampus.adapter.AlbumRecyclerAdapter;
import com.ofcampus.mediacontroll.PhoneMediaControl.PhotoEntry;

public class ActivityAlbumDetails extends ActionBarActivity implements AlbumRecyclerAdapter.ViewHolder.ClickListener {

	private Context context;
	public static final String PACKAGE = "org.ece.owngallery";
	private Toolbar toolbar;

	public ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();
	private AlbumRecyclerAdapter adapter;
	private int colmSize = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_albumdetails);

		context = ActivityAlbumDetails.this;
		initializeViews();
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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

	private void initializeViews() {
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Gallery");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle mBundle = getIntent().getExtras();
		int Key_ID = Integer.parseInt(mBundle.getString("Key_ID"));
		String title = mBundle.getString("Key_Name");

		if (mBundle == null) {
			onBackPressed();
		} else {
			photos = ActivityGallery.albumsSorted.get(Key_ID).photos;
			toolbar.setTitle(title + "(" + photos.size() + ")");
		}

		adapter = new AlbumRecyclerAdapter(context, ActivityAlbumDetails.this, photos);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setLayoutManager(new GridLayoutManager(context, colmSize, GridLayoutManager.VERTICAL, false));
	}

	@Override
	public void onItemClicked(int position) {
		String path = photos.get(position).path.toString();
		path = path.replace("file://", "");

		Intent intent = new Intent();
		Bundle mBundle = new Bundle();
		mBundle.putString("contents", path);
		intent.putExtras(mBundle);
		setResult(RESULT_OK, intent);
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public boolean onItemLongClicked(int position) {
		return false;
	}

}