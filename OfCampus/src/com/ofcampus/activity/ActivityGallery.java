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
import com.ofcampus.adapter.MyGalleryRecyclerAdapter;
import com.ofcampus.mediacontroll.PhoneMediaControl;
import com.ofcampus.mediacontroll.PhoneMediaControl.AlbumEntry;
import com.ofcampus.mediacontroll.PhoneMediaControl.loadAlbumPhoto;

public class ActivityGallery extends ActionBarActivity implements MyGalleryRecyclerAdapter.ViewHolder.ClickListener {

	public static int ALBUMDEATILCODE = 255;
	private Context context;
	public static final String PACKAGE = "org.ece.owngallery";
	private Toolbar toolbar;
	private RecyclerView recyclerView;

	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	private MyGalleryRecyclerAdapter adapter;
	private int colmSize = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		context = ActivityGallery.this;
		initializeView();
	}

	@Override
	public void onBackPressed() {
		if (albumsSorted != null) {
			albumsSorted = null;
		}
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ALBUMDEATILCODE && resultCode == RESULT_OK && null != data) {
			Bundle mbBundle = data.getExtras();
			String lpicturePath = mbBundle.getString("contents");

			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putString("contents", lpicturePath);
			intent.putExtras(mBundle);
			setResult(RESULT_OK, intent);
			if (albumsSorted != null) {
				albumsSorted = null;
			}
			overridePendingTransition(0, 0);
			finish();
		}

	}

	private void initializeView() {

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Gallery");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new MyGalleryRecyclerAdapter(context, ActivityGallery.this, new ArrayList<PhoneMediaControl.AlbumEntry>());
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setLayoutManager(new GridLayoutManager(context, colmSize, GridLayoutManager.VERTICAL, false));

		readGallery();
	}

	private void readGallery() {
		PhoneMediaControl mediaControl = new PhoneMediaControl();
		mediaControl.setLoadalbumphoto(new loadAlbumPhoto() {

			@Override
			public void loadPhoto(ArrayList<AlbumEntry> albumsSorted_) {
				albumsSorted = new ArrayList<PhoneMediaControl.AlbumEntry>(albumsSorted_);

				if (adapter != null && albumsSorted.size() >= 1) {
					adapter.refresh(albumsSorted);
				}

			}
		});
		mediaControl.loadGalleryPhotosAlbums(context, 0);
	}

	@Override
	public void onItemClicked(int position) {
		Intent mIntent = new Intent(context, ActivityAlbumDetails.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("Key_ID", position + "");
		mBundle.putString("Key_Name", albumsSorted.get(position).bucketName + "");
		mIntent.putExtras(mBundle);
		startActivityForResult(mIntent, ALBUMDEATILCODE);
	}

	@Override
	public boolean onItemLongClicked(int position) {
		return false;
	}

}
