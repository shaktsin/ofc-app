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

	private Context context;
	
	public static final String PACKAGE = "org.ece.owngallery";
	private Toolbar toolbar;
	private GridView mView;
	private Context mContext;

	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	public static ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();

	private int itemWidth = 100;
	private ListAdapter listAdapter;
	private int AlbummID=0;

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


	private void initializeView(){
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Gallery");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mView=(GridView)findViewById(R.id.grid_view);
		mView.setAdapter(listAdapter = new ListAdapter(ActivityGallery.this));

        int position = mView.getFirstVisiblePosition();
		int columnsCount = 3;
        mView.setNumColumns(columnsCount);
        itemWidth = (OfCampusApplication.displaySize.x - ((columnsCount + 1) * OfCampusApplication.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            	
            	String path=photos.get(position).path.toString();
            	path=path.replace("file://", "");
            	
            	Intent intent = new Intent();
				Bundle mBundle = new Bundle();
				mBundle.putString("contents",path);
				intent.putExtras(mBundle);
				setResult(RESULT_OK, intent);
				onBackPressed();
				
            }
        });
        
		readGallery();
	}
 
	private void readGallery(){
		PhoneMediaControl mediaControl=new PhoneMediaControl();
		mediaControl.setLoadalbumphoto(new loadAlbumPhoto() {
			
			@Override
			public void loadPhoto(ArrayList<AlbumEntry> albumsSorted_) {
				albumsSorted =new ArrayList<PhoneMediaControl.AlbumEntry>(albumsSorted_);
				photos=albumsSorted.get(1).photos;
				if (mView != null && mView.getEmptyView() == null) {
					mView.setEmptyView(null);
		        }
		        if (listAdapter != null) {
		            listAdapter.notifyDataSetChanged();
		        }
			}
		});
		mediaControl.loadGalleryPhotosAlbums(mContext,0);
	}
	

	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private LayoutInflater layoutInflater;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public ListAdapter(Context context) {
			this.mContext = context;
			this.layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.nophotos)
					.showImageForEmptyUri(R.drawable.nophotos)
					.showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
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
			return photos != null ? photos.size() : 0;
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
			viewHolder mHolder;
			if (view == null) {
				mHolder = new viewHolder();
				view = layoutInflater.inflate(R.layout.album_image, viewGroup,false);
				mHolder.imageView = (ImageView) view.findViewById(R.id.album_image);
				ViewGroup.LayoutParams params = view.getLayoutParams();
				params.width = itemWidth;
				params.height = itemWidth;
				view.setLayoutParams(params);
				mHolder.imageView.setTag(i);
				
				view.setTag(mHolder);
			} else {
				mHolder = (viewHolder) view.getTag();
			}
			PhotoEntry mPhotoEntry = photos.get(i);
			String path = mPhotoEntry.path;
			if (path != null && !path.equals("")) {
				imageLoader.displayImage("file://" + path, mHolder.imageView);
			}

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
