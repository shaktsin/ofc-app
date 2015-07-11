/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.ActivityJobDetails;
import com.ofcampus.activity.ActivityJobPostedUserDetails;
import com.ofcampus.activity.ActivityNewsDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.ui.CustomTextView;
import com.ofcampus.ui.ReplyDialog;

public class HideJobListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_post;

	public HideJobListAdapter(Context context, ArrayList<JobDetails> arrJobs) {

		this.mContext = context;
		this.jobs = arrJobs;
		this.inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		options_post = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.no_postimage).showImageForEmptyUri(R.drawable.no_postimage).showImageOnFail(R.drawable.no_postimage)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	public void refreshData(ArrayList<JobDetails> arrJobs) {
		this.jobs = arrJobs;
		notifyDataSetChanged();
	}

	public ArrayList<JobDetails> getJobData() {
		return this.jobs;
	}

	public void removepostion(int position) {
		if (this.jobs.size() >= 1) {
			this.jobs.remove(position);
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {
		return jobs.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.inflate_joblistrow, null);
			mHolder.profilepic = (ImageView) convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow = (ImageView) convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.img_like = (ImageView) convertView.findViewById(R.id.joblistview_img_like);
			mHolder.img_important = (ImageView) convertView.findViewById(R.id.joblistview_img_imp);
			mHolder.txt_name = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_postdate = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_contain = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_contain);
			mHolder.btn_reply = (ImageView) convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.btn_share = (ImageView) convertView.findViewById(R.id.joblistview_txt_share);
			mHolder.btn_comment = (ImageView) convertView.findViewById(R.id.joblistview_txt_comment);
			mHolder.img_post = (ImageView) convertView.findViewById(R.id.joblistview_img_post);
			mHolder.joblistview_img_post_rel = (CardView) convertView.findViewById(R.id.joblistview_img_post_rel);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final JobDetails mJobDetails = jobs.get(position);

		if (mJobDetails != null) {
			String url = mJobDetails.getImage();
			if (url != null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.profilepic, options);
			}
			mHolder.txt_name.setText(mJobDetails.getName());
			mHolder.txt_postdate.setText("Posted on " + mJobDetails.getPostedon());
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			mHolder.txt_contain.setText(mJobDetails.getContent());

			mHolder.img_important.setVisibility(View.GONE);
			mHolder.img_like.setVisibility(View.GONE);
			mHolder.img_arrow.setVisibility(View.VISIBLE);
			if (mJobDetails.getImportant() == 1) {
				mHolder.img_important.setSelected(true);
			} else {
				mHolder.img_important.setSelected(false);
			}
			mHolder.img_arrow.setSelected(true);

			final ArrayList<ImageDetails> Images = mJobDetails.getImages();
			if (Images != null && Images.size() >= 1) {
				mHolder.joblistview_img_post_rel.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Images.get(0).getImageURL(), mHolder.img_post, options_post);
				mHolder.img_post.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						gotToPostDetails(mJobDetails);
					}
				});

			} else {
				mHolder.joblistview_img_post_rel.setVisibility(View.GONE);
			}

			mHolder.profilepic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToUserDetails(mJobDetails);
				}
			});
			mHolder.txt_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToUserDetails(mJobDetails);
				}
			});

			mHolder.txt_subject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToPostDetails(mJobDetails);
				}
			});

			mHolder.txt_contain.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToPostDetails(mJobDetails);
				}
			});

			mHolder.btn_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new ReplyDialog(mContext, mJobDetails);
				}
			});

			mHolder.btn_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Util.onShareClick(mContext, v, mJobDetails.getSubject(), mJobDetails.getContent());
				}
			});

			mHolder.btn_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gotToPostDetails(mJobDetails);
				}
			});

			mHolder.img_arrow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// try {
					// PopupMenu popup = new PopupMenu(mContext, v);
					// popup.getMenuInflater().inflate(R.menu.popupmenu_unhide,
					// popup.getMenu());
					// popup.show();
					// popup.setOnMenuItemClickListener(new
					// PopupMenu.OnMenuItemClickListener() {
					// @Override
					// public boolean onMenuItemClick(MenuItem item) {
					//
					// switch (item.getItemId()) {
					// case R.id.hidepost:
					// if (hidejoblistinterface != null) {
					// hidejoblistinterface.arrowUnHideClieckEvent(mJobDetails,position);
					// }
					// break;
					//
					// default:
					// break;
					// }
					//
					// return true;
					// }
					// });
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					if (hidejoblistinterface != null) {
						hidejoblistinterface.arrowUnHideClieckEvent(mJobDetails, position);
					}
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView profilepic;
		ImageView img_arrow, img_important, img_like;
		CustomTextView txt_name, txt_postdate, txt_subject, txt_contain;
		ImageView btn_reply, btn_share, btn_comment;
		ImageView img_post;
		CardView joblistview_img_post_rel;
	}

	private void gotToUserDetails(JobDetails mJobDetails) {
		Intent mIntent = new Intent(mContext, ActivityJobPostedUserDetails.class);
		mIntent.putExtra("isUserCame", (mJobDetails.getId().equals(UserDetails.getLoggedInUser(mContext).getUserID())) ? true : false);
		mIntent.putExtra("userID", mJobDetails.getId());
		mContext.startActivity(mIntent);
		((Activity) mContext).overridePendingTransition(0, 0);
	}

	private void gotToPostDetails(JobDetails mJobDetails) {
		String toolTitle = "";
		Intent mIntent = null;
		if (mJobDetails.getPostType().equals("3")) {
			toolTitle = Util.TOOLTITLE[1];
			mIntent = new Intent(mContext, ActivityNewsDetails.class);
		} else {
			toolTitle = Util.TOOLTITLE[0];
			mIntent = new Intent(mContext, ActivityJobDetails.class);
		}
		((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
		Bundle mBundle = new Bundle();
		mBundle.putString(Util.BUNDLE_KEY[0], toolTitle);
		mIntent.putExtras(mBundle);
		mContext.startActivity(mIntent);
		((Activity) mContext).overridePendingTransition(0, 0);
	}

	public HideJobListInterface hidejoblistinterface;

	public HideJobListInterface getHidejoblistinterface() {
		return hidejoblistinterface;
	}

	public void setHidejoblistinterface(HideJobListInterface hidejoblistinterface) {
		this.hidejoblistinterface = hidejoblistinterface;
	}

	public interface HideJobListInterface {
		public void arrowUnHideClieckEvent(JobDetails mJobDetails, int position);

	}

}
