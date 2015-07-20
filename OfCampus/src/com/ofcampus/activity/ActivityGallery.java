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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.adapter.BaseFragmentAdapter;
import com.ofcampus.mediacontroll.PhoneMediaControl;
import com.ofcampus.mediacontroll.PhoneMediaControl.AlbumEntry;
import com.ofcampus.mediacontroll.PhoneMediaControl.PhotoEntry;
import com.ofcampus.mediacontroll.PhoneMediaControl.loadAlbumPhoto;

public class ActivityGallery extends ActionBarActivity {

	public static int ALBUMDEATILCODE = 255;
	private Context context;
	public static final String PACKAGE = "org.ece.owngallery";
	private Toolbar toolbar;
	private GridView mView;

	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	public static ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();

	private int itemWidth = 100;
	private ListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		context = ActivityGallery.this;
		initializeView();
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
			overridePendingTransition(0, 0);
			finish();
		}

	}

	private void initializeView() {

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Gallery");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mView = (GridView) findViewById(R.id.grid_view);
		mView.setAdapter(listAdapter = new ListAdapter(ActivityGallery.this));

		int position = mView.getFirstVisiblePosition();
		int columnsCount = 2;
		mView.setNumColumns(columnsCount);
		itemWidth = (OfCampusApplication.displaySize.x - ((columnsCount + 1) * OfCampusApplication.dp(4))) / columnsCount;
		mView.setColumnWidth(itemWidth);

		listAdapter.notifyDataSetChanged();
		mView.setSelection(position);
		mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

				Intent mIntent = new Intent(context, ActivityAlbumDetails.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("Key_ID", position + "");
				mBundle.putString("Key_Name", albumsSorted.get(position).bucketName + "");
				mIntent.putExtras(mBundle);
				startActivityForResult(mIntent, ALBUMDEATILCODE);
			}
		});

		readGallery();
	}

	private void readGallery() {
		PhoneMediaControl mediaControl = new PhoneMediaControl();
		mediaControl.setLoadalbumphoto(new loadAlbumPhoto() {

			@Override
			public void loadPhoto(ArrayList<AlbumEntry> albumsSorted_) {
				albumsSorted = new ArrayList<PhoneMediaControl.AlbumEntry>(albumsSorted_);
				if (mView != null && mView.getEmptyView() == null) {
					mView.setEmptyView(null);
				}
				if (listAdapter != null) {
					listAdapter.notifyDataSetChanged();
				}

			}
		});
		mediaControl.loadGalleryPhotosAlbums(context, 0);
	}

	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private LayoutInflater layoutInflater;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public ListAdapter(Context context) {
			this.mContext = context;
			this.layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.nophotos).showImageForEmptyUri(R.drawable.nophotos).showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
					.cacheOnDisc(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int i) {
			return true;
		}

		@Override
		public int getCount() {
			return albumsSorted != null ? albumsSorted.size() : 0;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = li.inflate(R.layout.photo_picker_album_layout, viewGroup, false);
			}
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = itemWidth;
			params.height = itemWidth;
			view.setLayoutParams(params);

			PhoneMediaControl.AlbumEntry albumEntry = albumsSorted.get(i);
			final ImageView imageView = (ImageView) view.findViewById(R.id.media_photo_image);
			if (albumEntry.coverPhoto != null && albumEntry.coverPhoto.path != null) {
				imageLoader.displayImage("file://" + albumEntry.coverPhoto.path, imageView, options);
			} else {
				imageView.setImageResource(R.drawable.nophotos);
			}
			TextView textView = (TextView) view.findViewById(R.id.album_name);
			textView.setText(albumEntry.bucketName);

			textView = (TextView) view.findViewById(R.id.album_count);
			textView.setText("" + albumEntry.photos.size());

			return view;
		}

		@Override
		public int getItemViewType(int i) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEmpty() {
			return albumsSorted == null || albumsSorted.isEmpty();
		}

		class viewHolder {
			public ImageView imageView;
		}

	}

}
