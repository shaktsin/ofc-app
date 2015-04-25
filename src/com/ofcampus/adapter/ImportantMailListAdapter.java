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
import com.ofcampus.activity.ActivityComment;
import com.ofcampus.activity.ActivityJobPostedUserDetails;
import com.ofcampus.activity.ActivityNewsDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.ui.CustomTextView;
import com.ofcampus.ui.ReplyDialog;

public class ImportantMailListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_post;

	public ImportantMailListAdapter(Context context, ArrayList<JobDetails> arrJobs) {
		this.mContext = context;
		this.jobs = arrJobs;
		this.inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

		options_post = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.no_postimage).showImageForEmptyUri(R.drawable.no_postimage).showImageOnFail(R.drawable.no_postimage).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

	}

	public void refreshData(ArrayList<JobDetails> arrJobs) {
		this.jobs = arrJobs;
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size() - 1).getPostid());
		notifyDataSetChanged();
	}

	public void unImportantJobRemove(JobDetails mJobDetails, int postion) {
		this.jobs.remove(postion);
		notifyDataSetChanged();
	}

	public ArrayList<JobDetails> getJobData() {
		return this.jobs;
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
			mHolder.img_important = (ImageView) convertView.findViewById(R.id.joblistview_img_imp);
			mHolder.txt_name = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_postdate = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_contain = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_contain);
			mHolder.btn_reply = (ImageView) convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.btn_share = (ImageView) convertView.findViewById(R.id.joblistview_txt_share);
			mHolder.btn_comment = (ImageView) convertView.findViewById(R.id.joblistview_txt_comment);

			mHolder.txt_replycount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_reply_count);
			mHolder.txt_sharecount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_share_count);
			mHolder.txt_commentcount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_comment_count);

			mHolder.img_post = (ImageView) convertView.findViewById(R.id.joblistview_img_post);
			mHolder.joblistview_img_post_rel = (CardView) convertView.findViewById(R.id.joblistview_img_post_rel);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final JobDetails mJobDetails = jobs.get(position);

		if (mJobDetails != null) {
			imageLoader.displayImage(mJobDetails.getImage(), mHolder.profilepic, options);
			mHolder.txt_name.setText(mJobDetails.getName());
			mHolder.txt_postdate.setText("Posted on " + mJobDetails.getPostedon());
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			mHolder.txt_contain.setText(mJobDetails.getContent());

			mHolder.img_important.setVisibility(View.VISIBLE);
			mHolder.img_arrow.setVisibility(View.GONE);
			mHolder.img_important.setSelected(true);
			mHolder.img_important.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (importantmaillistadapterinterface != null) {
						importantmaillistadapterinterface.unIportantEvent(mJobDetails, position);
					}
				}
			});

			String replycount = mJobDetails.getNumreplies();
			String sharecount = mJobDetails.getNumshared();
			String commentcount = mJobDetails.getNumcomment();

			if (replycount != null && replycount.length() >= 1 && !replycount.equalsIgnoreCase("0")) {
				mHolder.txt_replycount.setVisibility(View.VISIBLE);
				mHolder.txt_replycount.setText(replycount + " replys");
			} else {
				mHolder.txt_replycount.setVisibility(View.GONE);
			}

			if (sharecount != null && sharecount.length() >= 1 && !sharecount.equalsIgnoreCase("0")) {
				mHolder.txt_sharecount.setVisibility(View.VISIBLE);
				mHolder.txt_sharecount.setText(sharecount + " shareed");
			} else {
				mHolder.txt_sharecount.setVisibility(View.GONE);
			}

			if (commentcount != null && commentcount.length() >= 1 && !commentcount.equalsIgnoreCase("0")) {
				mHolder.txt_commentcount.setVisibility(View.VISIBLE);
				mHolder.txt_commentcount.setText(commentcount + " comments");
			} else {
				mHolder.txt_commentcount.setVisibility(View.GONE);
			}

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

		}
		return convertView;
	}

	private class ViewHolder {
		ImageView profilepic;
		ImageView img_arrow, img_important, img_post;
		CustomTextView txt_name, txt_postdate, txt_subject, txt_contain;
		CustomTextView txt_replycount, txt_sharecount, txt_commentcount;
		ImageView btn_reply, btn_share, btn_comment;
		CardView joblistview_img_post_rel;
	}

	public void setIDS(String fstID, String lstID) {
		if (importantmaillistadapterinterface != null) {
			importantmaillistadapterinterface.firstIDAndlastID(fstID, lstID);
		}
	}

	private void gotToUserDetails(JobDetails mJobDetails) {
		if (!mJobDetails.getName().equals(UserDetails.getLoggedInUser(mContext).getAccountname())) {
			((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
			mContext.startActivity(new Intent(mContext, ActivityJobPostedUserDetails.class));
			((Activity) mContext).overridePendingTransition(0, 0);
		}
	}

	private void gotToPostDetails(JobDetails mJobDetails) {
		String toolTitle = "";
		Intent mIntent = null;
		if (mJobDetails.getPostType().equals("3")) {
			toolTitle = Util.TOOLTITLE[1];
			mIntent = new Intent(mContext, ActivityNewsDetails.class);
		} else {
			toolTitle = Util.TOOLTITLE[0];
			mIntent = new Intent(mContext, ActivityComment.class);
		}
		((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
		Bundle mBundle = new Bundle();
		mBundle.putString(Util.BUNDLE_KEY[0], toolTitle);
		mIntent.putExtras(mBundle);
		mContext.startActivity(mIntent);
		((Activity) mContext).overridePendingTransition(0, 0);
	}

	public ImportantMailListAdapterInterface importantmaillistadapterinterface;

	public ImportantMailListAdapterInterface getImportantmaillistadapterinterface() {
		return importantmaillistadapterinterface;
	}

	public void setImportantmaillistadapterinterface(ImportantMailListAdapterInterface importantmaillistadapterinterface) {
		this.importantmaillistadapterinterface = importantmaillistadapterinterface;
	}

	public interface ImportantMailListAdapterInterface {
		public void unIportantEvent(JobDetails mJobDetails, int postion);

		public void firstIDAndlastID(String fstID, String lstID);
	}
}