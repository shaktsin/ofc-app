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
import com.ofcampus.activity.ActivityJobPostedUserDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.ui.AlbumPagerDialog;
import com.ofcampus.ui.CustomTextView;

public class NewsListAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs=null;
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_post;
	
	public NewsListAdapter(Context context,ArrayList<JobDetails> arrJobs){
	
		this.mContext=context; 
		this.jobs=arrJobs; 
		this.inflater=LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		options_post = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_postimage)
				.showImageForEmptyUri(R.drawable.no_postimage)
				.showImageOnFail(R.drawable.no_postimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}
	
	public void refreshData(ArrayList<JobDetails> arrJobs){
		this.jobs= arrJobs;
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size()-1).getPostid());
		notifyDataSetChanged();
	}
	
	
	public void refreshSwipeData(ArrayList<JobDetails> arrJobs){
		ArrayList<JobDetails> Jobs_=new ArrayList<JobDetails>(arrJobs);
		Jobs_.addAll(jobs);
		this.jobs=Jobs_;
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size()-1).getPostid());
		notifyDataSetChanged();
	}
	
	public void refreshloadmoreData(ArrayList<JobDetails> arrJobs){
		jobs.addAll(arrJobs);
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size()-1).getPostid());
		notifyDataSetChanged();
	}
//	
//	
//	public void hideJob(JobDetails hideJob){
//		ArrayList<JobDetails> currentJobList=new ArrayList<JobDetails>(this.jobs);
//		ArrayList<JobDetails> modifyJobList=new ArrayList<JobDetails>();
//		for (JobDetails jobDetails : currentJobList) {
//			if (!hideJob.getPostid().equals(jobDetails.getPostid())) {
//				modifyJobList.add(jobDetails);
//			}
//		}
//		this.jobs=modifyJobList;
//		if (jobs.size()>=1) {
//			setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size()-1).getPostid());
//		}else {
//			setIDS("", "");
//		}
//		notifyDataSetChanged();
//	}
//	
//	public void importantJob(JobDetails hideJob){
//		for (JobDetails jobDetails : jobs) {
//			if (hideJob.getPostid().equals(jobDetails.getPostid())) {
//				jobDetails.important=1;
//			}
//		}
//		notifyDataSetChanged();
//	}
//	
//	public void unimportantJob(JobDetails hideJob){
//		for (JobDetails jobDetails : jobs) {
//			if (hideJob.getPostid().equals(jobDetails.getPostid())) {
//				jobDetails.important=0;
//			}
//		}
//		notifyDataSetChanged();
//	}
//	
//	
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder mHolder;
		if (convertView==null) {
			mHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.inflate_joblistrow, null);
			mHolder.profilepic=(ImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.img_important=(ImageView)convertView.findViewById(R.id.joblistview_img_imp);
			mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_postdate=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_contain=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_contain);
			
			mHolder.btn_reply=(ImageView)convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.btn_share=(ImageView)convertView.findViewById(R.id.joblistview_txt_share);
			mHolder.btn_comment=(ImageView)convertView.findViewById(R.id.joblistview_txt_comment);
			
			mHolder.img_post=(ImageView)convertView.findViewById(R.id.joblistview_img_post);
			mHolder.joblistview_img_post_rel=(CardView)convertView.findViewById(R.id.joblistview_img_post_rel);
			
			
			
			convertView.setTag(mHolder);
		}else {
			mHolder=(ViewHolder) convertView.getTag();
		}
		
		final JobDetails mJobDetails = jobs.get(position);
		
		if (mJobDetails!=null) {
			String url=mJobDetails.getImage();
			if (url!=null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.profilepic, options);
			}
			mHolder.txt_name.setText(mJobDetails.getName());
			mHolder.txt_postdate.setText("Posted on "+mJobDetails.getPostedon());
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			mHolder.txt_contain.setText(mJobDetails.getContent());
			mHolder.img_important.setVisibility(View.VISIBLE);
			
			if (mJobDetails.getImportant()==1) {
				mHolder.img_important.setSelected(true);
			}else {
				mHolder.img_important.setSelected(false);
			}

			
			final ArrayList<ImageDetails> Images = mJobDetails.getImages();
			if (Images!=null && Images.size()>=1) {
				mHolder.joblistview_img_post_rel.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Images.get(0).getImageURL(), mHolder.img_post, options_post);
				mHolder.img_post.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new AlbumPagerDialog(mContext, Images,0);
					}
				});
				
			}else {
				mHolder.joblistview_img_post_rel.setVisibility(View.GONE);
			}
			
			mHolder.profilepic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (!mJobDetails.getName().equals(UserDetails.getLoggedInUser(mContext).getAccountname())) {
						((OfCampusApplication)mContext.getApplicationContext()).jobdetails=mJobDetails;
						mContext.startActivity(new Intent(mContext,ActivityJobPostedUserDetails.class));
						((Activity) mContext).overridePendingTransition(0,0);
					}
					
				}
			});
			
			mHolder.txt_subject.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (newslistinterface!=null) {
						newslistinterface.convertViewOnClick(mJobDetails);
					}
				}
			});
			
			mHolder.btn_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (newslistinterface != null) {
						newslistinterface.replyClickEvent(mJobDetails);
					}
				}
			});

			mHolder.btn_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Util.onShareClick(mContext,v,mJobDetails.getSubject(),mJobDetails.getContent()) ;
				}
			});

			mHolder.btn_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (newslistinterface != null) {
						newslistinterface.commentClickEvent(mJobDetails);
					}
				}
			});
			
			mHolder.img_important.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					if (newslistinterface != null) {
//						if (mJobDetails.getImportant()==0) {
//							newslistinterface.impClieckEvent(mJobDetails);
//						}else {
//							newslistinterface.unimpClieckEvent(mJobDetails);
//						}
//						
//					}
				}
			});
			
//			mHolder.img_arrow.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					try {
//						PopupMenu popup = new PopupMenu(mContext, v);
//						popup.getMenuInflater().inflate(R.menu.job_optionmenu, popup.getMenu());
//						popup.show();
//						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//							@Override
//							public boolean onMenuItemClick(MenuItem item) {
//
//								switch (item.getItemId()) {
//								case R.id.hidepost:
//									if (newslistinterface != null) {
//										newslistinterface.arrowHideClieckEvent(mJobDetails);
//									}
//									break;
//								case R.id.spampost:
//									if (newslistinterface != null) {
//										newslistinterface.arrowSpamClieckEvent(mJobDetails);
//									}
//									break;
//
//								default:
//									break;
//								}
//
//								return true;
//							}
//						});
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
		}
		return convertView;
	}
	
	private class ViewHolder{
		ImageView profilepic;
		ImageView img_arrow,img_important;
		CustomTextView txt_name,txt_postdate,txt_subject,txt_contain;
		ImageView btn_reply,btn_share,btn_comment;
		ImageView img_post;
		CardView joblistview_img_post_rel;
	}
	
	public void setIDS(String fstID,String lstID){
		if (newslistinterface!=null) {
			newslistinterface.firstIDAndlastID(fstID,lstID); 
		}
	}
	
	
	public NewsListInterface newslistinterface;

	public NewsListInterface getNewslistinterface() {
		return newslistinterface;
	}

	public void setNewslistinterface(NewsListInterface newslistinterface) {
		this.newslistinterface = newslistinterface;
	}

	public interface NewsListInterface{ 
		public void convertViewOnClick(JobDetails mJobDetails); 
		public void firstIDAndlastID(String fstID, String lstID); 
//		public void arrowHideClieckEvent(JobDetails mJobDetails);  
//		public void arrowSpamClieckEvent(JobDetails mJobDetails);  
//		public void impClieckEvent(JobDetails mJobDetails);  
//		public void unimpClieckEvent(JobDetails mJobDetails);  
		public void replyClickEvent(JobDetails mJobDetails);  
		public void commentClickEvent(JobDetails mJobDetails);  
	}

}