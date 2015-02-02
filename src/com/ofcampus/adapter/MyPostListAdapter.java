package com.ofcampus.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.ActivityComment;
import com.ofcampus.model.JobDetails;
import com.ofcampus.ui.ReplyDialog;

public class MyPostListAdapter  extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs=null;
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public MyPostListAdapter(Context context,ArrayList<JobDetails> arrJobs){
		
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
			mHolder.profilepic=(ImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.img_important=(ImageView)convertView.findViewById(R.id.joblistview_img_imp);
			mHolder.txt_name=(TextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_postdate=(TextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(TextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_contain=(TextView)convertView.findViewById(R.id.joblistview_txt_contain);
			mHolder.btn_reply=(TextView)convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.btn_share=(TextView)convertView.findViewById(R.id.joblistview_txt_share);
			mHolder.btn_comment=(TextView)convertView.findViewById(R.id.joblistview_txt_comment);
			convertView.setTag(mHolder);
		}else {
			mHolder=(ViewHolder) convertView.getTag();
		}
		
		final JobDetails mJobDetails = jobs.get(position);
		
		if (mJobDetails!=null) {
			imageLoader.displayImage(mJobDetails.getImage(), mHolder.profilepic, options);
			mHolder.txt_name.setText(mJobDetails.getName());
			mHolder.txt_postdate.setText("Posted on "+mJobDetails.getPostedon());
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			mHolder.txt_contain.setText(mJobDetails.getContent());
			
//			if (mJobDetails.getImportant()==1) {
//				mHolder.img_important.setVisibility(View.VISIBLE);
//			}else {
//				mHolder.img_important.setVisibility(View.GONE);
//			}

			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					((OfCampusApplication)mContext.getApplicationContext()).jobdetails=mJobDetails;
					Intent mIntent = new Intent(mContext,ActivityComment.class);
					Bundle mBundle=new Bundle();
					mBundle.putString("key_dlorcmt", Util.TOOLTITLE[1]);
					mIntent.putExtras(mBundle);
					mContext.startActivity(mIntent);
					((Activity) mContext).overridePendingTransition(0, 0); 
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
					Util.onShareClick(mContext,v,mJobDetails.getSubject(),mJobDetails.getContent()) ;
				}
			});

			mHolder.btn_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((OfCampusApplication)mContext.getApplicationContext()).jobdetails=mJobDetails;
					Intent mIntent = new Intent(mContext,ActivityComment.class);
					Bundle mBundle=new Bundle();
					mBundle.putString("key_dlorcmt", Util.TOOLTITLE[0]);
					mIntent.putExtras(mBundle);
					mContext.startActivity(mIntent);
					((Activity) mContext).overridePendingTransition(0, 0); 
				}
			});

		}
		return convertView;
	}
	
	private class ViewHolder{
		ImageView profilepic;
		ImageView img_arrow,img_important;
		TextView txt_name,txt_postdate,txt_subject,txt_contain;
		TextView btn_reply,btn_share,btn_comment;
	}
	
	public void setIDS(String fstID,String lstID){
		if (mypostlistadapterinterface!=null) {
			mypostlistadapterinterface.firstIDAndlastID(fstID,lstID);
		}
	}
	
	
	public MyPostListAdapterInterface mypostlistadapterinterface;
	
	
	
	public MyPostListAdapterInterface getMypostlistadapterinterface() {
		return mypostlistadapterinterface;
	}

	public void setMypostlistadapterinterface(
			MyPostListAdapterInterface mypostlistadapterinterface) {
		this.mypostlistadapterinterface = mypostlistadapterinterface;
	}

	public interface MyPostListAdapterInterface{
		public void convertViewOnClick(JobDetails mJobDetails); 
		public void firstIDAndlastID(String fstID, String lstID); 
	}
}