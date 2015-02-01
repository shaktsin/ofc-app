package com.ofcampus.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.ActivityMyProfile;
import com.ofcampus.component.CircleImageView;
import com.ofcampus.model.JobDetails;

public class JobListBaseAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs=null;
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public JobListBaseAdapter(Context context,ArrayList<JobDetails> arrJobs){
	
		this.mContext=context; 
		this.jobs=arrJobs; 
		this.inflater=LayoutInflater.from(context);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		
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
	
	
	public void hideJob(JobDetails hideJob){
		ArrayList<JobDetails> currentJobList=new ArrayList<JobDetails>(this.jobs);
		ArrayList<JobDetails> modifyJobList=new ArrayList<JobDetails>();
		for (JobDetails jobDetails : currentJobList) {
			if (!hideJob.getPostid().equals(jobDetails.getPostid())) {
				modifyJobList.add(jobDetails);
			}
		}
		this.jobs=modifyJobList;
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size()-1).getPostid());
		notifyDataSetChanged();
	}
	
	public void importantJob(JobDetails hideJob){
		for (JobDetails jobDetails : jobs) {
			if (hideJob.getPostid().equals(jobDetails.getPostid())) {
				jobDetails.important=1;
			}
		}
		notifyDataSetChanged();
	}
	
	public ArrayList<JobDetails> getJobData(){
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
			mHolder.profilepic=(CircleImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.img_important=(ImageView)convertView.findViewById(R.id.joblistview_img_imp);
			mHolder.txt_name=(TextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_postdate=(TextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(TextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_contain=(TextView)convertView.findViewById(R.id.joblistview_txt_contain);
			mHolder.btn_reply=(TextView)convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.btn_share=(TextView)convertView.findViewById(R.id.joblistview_txt_share);
			mHolder.btn_comment=(TextView)convertView.findViewById(R.id.joblistview_txt_comment);
			mHolder.mHlvCustomList=(HorizontalListView)convertView.findViewById(R.id.hlvCustomList);
			
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

			
			ArrayList<String> Images = mJobDetails.getImages();
			
			if (Images!=null && Images.size()>=1) {
				mHolder.mHlvCustomList.setVisibility(View.VISIBLE);
				mHolder.mHlvCustomList.setAdapter(new CustomArrayAdapter(mContext, Images));
			}
			
			mHolder.profilepic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mContext.startActivity(new Intent(mContext,ActivityMyProfile.class));
					((Activity) mContext).overridePendingTransition(0,0);
				}
			});
			
			mHolder.txt_subject.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (joblistinterface!=null) {
						joblistinterface.convertViewOnClick(mJobDetails);
					}
				}
			});
			
			mHolder.btn_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (joblistinterface != null) {
						joblistinterface.replyClickEvent(mJobDetails);
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
					if (joblistinterface != null) {
						joblistinterface.commentClickEvent(mJobDetails);
					}
				}
			});
			
			mHolder.img_important.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (joblistinterface != null) {
						joblistinterface.impClieckEvent(mJobDetails);
					}
				}
			});
			
			mHolder.img_arrow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						PopupMenu popup = new PopupMenu(mContext, v);
						popup.getMenuInflater().inflate(R.menu.job_optionmenu, popup.getMenu());
						popup.show();
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {

								switch (item.getItemId()) {
								case R.id.hidepost:
									if (joblistinterface != null) {
										joblistinterface.arrowHideClieckEvent(mJobDetails);
									}
									break;
								case R.id.spampost:
									if (joblistinterface != null) {
										joblistinterface.arrowSpamClieckEvent(mJobDetails);
									}
									break;

								default:
									break;
								}

								return true;
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return convertView;
	}
	
	private class ViewHolder{
		CircleImageView profilepic;
		ImageView img_arrow,img_important;
		TextView txt_name,txt_postdate,txt_subject,txt_contain;
		TextView btn_reply,btn_share,btn_comment;
		HorizontalListView mHlvCustomList;
	}
	
	public void setIDS(String fstID,String lstID){
		if (joblistinterface!=null) {
			joblistinterface.firstIDAndlastID(fstID,lstID);
		}
	}
	
	
	public jobListInterface joblistinterface;
	
	
	public jobListInterface getJoblistinterface() {
		return joblistinterface;
	}

	public void setJoblistinterface(jobListInterface joblistinterface) {
		this.joblistinterface = joblistinterface;
	}

	public interface jobListInterface{
		public void convertViewOnClick(JobDetails mJobDetails); 
		public void firstIDAndlastID(String fstID, String lstID); 
		public void arrowHideClieckEvent(JobDetails mJobDetails);  
		public void arrowSpamClieckEvent(JobDetails mJobDetails);  
		public void impClieckEvent(JobDetails mJobDetails);  
		public void replyClickEvent(JobDetails mJobDetails);  
		public void commentClickEvent(JobDetails mJobDetails);  
	}

	
	
	/****************************************************/
	
	public class CustomArrayAdapter extends ArrayAdapter<String> {
		   

		private LayoutInflater mInflater;
	    private ArrayList<String> PicDataSets;
	    public CustomArrayAdapter(Context context,ArrayList<String> PicDataSets_) {
	        super(context, R.layout.inflate_createjob_pic, PicDataSets_);
	        this.PicDataSets=PicDataSets_;
	        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
		public int getCount() {
			return PicDataSets.size();
		}
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        Holder holder;

	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
	            holder = new Holder();
	            holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
	            holder.pgbar = (ProgressBar) convertView.findViewById(R.id.progressBar);
	            
	            convertView.setTag(holder);
	        } else {
	            holder = (Holder) convertView.getTag();
	        }
	        
	        final ProgressBar pgbar = holder.pgbar;
	        
	        String path=PicDataSets.get(position);
	        imageLoader.displayImage(path, holder.pic, options,new ImageLoadingListener() {
				
	        	@Override
				public void onLoadingStarted(String arg0, View arg1) {
					pgbar.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					pgbar.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					pgbar.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					pgbar.setVisibility(View.GONE);
				}
			});

	        return convertView;
	    }

	    /** View holder for the views we need access to */
	    private  class Holder {
	        public ImageView pic;
	        public ProgressBar pgbar; 
	    }
	}

	class PicDataSet{
		String path="";
	}
	
}
