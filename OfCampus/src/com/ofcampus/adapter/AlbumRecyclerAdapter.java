package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.R;
import com.ofcampus.mediacontroll.PhoneMediaControl.PhotoEntry;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

	private static final String TAG = AlbumRecyclerAdapter.class.getSimpleName();
	private ViewHolder.ClickListener clickListener;
	private ArrayList<PhotoEntry> photos_;

	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private int hight = 200;
	private Context context;

	public AlbumRecyclerAdapter(Context context_, ViewHolder.ClickListener clickListener, ArrayList<PhotoEntry> photos) {
		super();

		this.clickListener = clickListener;
		this.photos_ = photos;
		this.context = context_;
		this.hight = context.getResources().getDisplayMetrics().heightPixels / 3;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.nophotos).showImageForEmptyUri(R.drawable.nophotos).showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context_));

	}

	public void refresh(ArrayList<PhotoEntry> photos) {
		this.photos_.clear();
		this.photos_.addAll(photos);
		notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo, parent, false);
		return new ViewHolder(v, clickListener);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final PhotoEntry mPhotoEntry = photos_.get(position);

		String path = mPhotoEntry.path;
		if (path != null && !path.equals("")) {
			imageLoader.displayImage("file://" + path, holder.imageview);
		}
		final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp instanceof GridLayoutManager.LayoutParams) {
			GridLayoutManager.LayoutParams sglp = (GridLayoutManager.LayoutParams) lp;
			sglp.height = hight;
			holder.itemView.setLayoutParams(sglp);
		}
	}

	@Override
	public int getItemCount() {
		return photos_.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		private static final String TAG = ViewHolder.class.getSimpleName();

		View selectedOverlay;
		ImageView imageview;

		private ClickListener listener;

		public ViewHolder(View itemView, ClickListener listener) {
			super(itemView);

			imageview = (ImageView) itemView.findViewById(R.id.image_);
			selectedOverlay = itemView.findViewById(R.id.selected_overlay);

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