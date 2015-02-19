package com.ofcampus.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.R;
import com.ofcampus.model.ImageDetails;

public class AlbumPagerDialog {

	private Context mContext;
	private Dialog dialog;
	private int position;
	private ArrayList<ImageDetails> arrayUrl=new ArrayList<ImageDetails>();
	
	public AlbumPagerDialog(Context context, ArrayList<ImageDetails> arrayurl, int position_){ 
		this.mContext=context;
		this.arrayUrl=arrayurl;
		this.position=position_;
		
		dialog= new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.inflate_dialog_album_pager);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
		
		ViewPager mPager=(ViewPager)dialog.findViewById(R.id.album_pager);
		
		AlbumPager mAlbumPager=new AlbumPager(mContext, arrayurl);
		mPager.setAdapter(mAlbumPager);
		mPager.setCurrentItem(position);
		
		dialog.setCancelable(true);
		dialog.show();
	}
	
	private class AlbumPager extends PagerAdapter {

		private ArrayList<ImageDetails> arrPhotos;
		private LayoutInflater inflater;
		private Context context_;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions options;

		public AlbumPager(Context context, ArrayList<ImageDetails> arrPhotos_) {
			this.arrPhotos = arrPhotos_;
			this.context_ = context;
			inflater = LayoutInflater.from(context_);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.no_postimage)
					.showImageForEmptyUri(R.drawable.no_postimage)
					.showImageOnFail(R.drawable.no_postimage)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return arrPhotos.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.inflate_pager_imageview,view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.iflate_img_pager);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.iflate_pg);
		
			final String mPhotos = arrPhotos.get(position).getImageURL();
			imageLoader.displayImage(mPhotos, imageView, options,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					spinner.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					spinner.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					spinner.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					spinner.setVisibility(View.GONE);
				}
			});
			
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}
