package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.R;
import com.ofcampus.mediacontroll.PhoneMediaControl;
import com.ofcampus.mediacontroll.PhoneMediaControl.AlbumEntry;
import com.ofcampus.mediacontroll.PhoneMediaControl.PhotoEntry;

public class MyGalleryRecyclerAdapter extends RecyclerView.Adapter<MyGalleryRecyclerAdapter.ViewHolder> {

	private ViewHolder.ClickListener clickListener;
	public ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private int hight = 200;
	private Context context;

	public MyGalleryRecyclerAdapter(Context context_, ViewHolder.ClickListener clickListener, ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted_) {
		super();

		this.clickListener = clickListener;
		this.albumsSorted = albumsSorted_;
		this.context = context_;
		this.hight = context.getResources().getDisplayMetrics().heightPixels / 3;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.nophotos).showImageForEmptyUri(R.drawable.nophotos).showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context_));

	}

	public void refresh(ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted_) {
		this.albumsSorted.clear();
		this.albumsSorted.addAll(albumsSorted_);
		notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gellary_photo, parent, false);
		return new ViewHolder(v, clickListener);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final AlbumEntry mAlbumEntry = albumsSorted.get(position);

		String path = mAlbumEntry.coverPhoto.path;
		if (path != null && !path.equals("")) {
			imageLoader.displayImage("file://" + path, holder.imageview);
		}
		final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp instanceof GridLayoutManager.LayoutParams) {
			GridLayoutManager.LayoutParams sglp = (GridLayoutManager.LayoutParams) lp;
			sglp.height = hight;
			holder.itemView.setLayoutParams(sglp);
		}
		holder.textName.setText(mAlbumEntry.bucketName);
		holder.textCount.setText("" + mAlbumEntry.photos.size());
	}

	@Override
	public int getItemCount() {
		return albumsSorted != null ? albumsSorted.size() : 0;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		private static final String TAG = ViewHolder.class.getSimpleName();

		View selectedOverlay;
		ImageView imageview;
		TextView textName;
		TextView textCount;
		private ClickListener listener;

		public ViewHolder(View itemView, ClickListener listener) {
			super(itemView);

			imageview = (ImageView) itemView.findViewById(R.id.image_);
			selectedOverlay = itemView.findViewById(R.id.selected_overlay);

			textName = (TextView) itemView.findViewById(R.id.album_name);
			textCount = (TextView) itemView.findViewById(R.id.album_count);

			this.listener = listener;

			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onItemClicked(getPosition());
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if (listener != null) {
				return listener.onItemLongClicked(getPosition());
			}

			return false;
		}

		public interface ClickListener {
			public void onItemClicked(int position);

			public boolean onItemLongClicked(int position);
		}
	}

}
