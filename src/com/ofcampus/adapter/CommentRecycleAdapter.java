package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.component.CircleImageView;
import com.ofcampus.model.JobDetails;
import com.ofcampus.ui.AlbumPagerDialog;
import com.ofcampus.ui.ReplyDialog;

public class CommentRecycleAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private Context mContext;
	private ArrayList<JobDetails> arraJobComment;
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	private int totalCommentCount=0;
	private int CommentCount=0;
	private int pager_Pading;
	
	public CommentRecycleAdapter(Context context,ArrayList<JobDetails> arraJobComment_){
		this.mContext=context;
		this.arraJobComment=arraJobComment_;
		this.inflater=LayoutInflater.from(mContext);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_profilepic)
				.showImageForEmptyUri(R.drawable.ic_profilepic)
				.showImageOnFail(R.drawable.ic_profilepic).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		pager_Pading = (int) (mContext.getResources().getDimension(R.dimen.comment_pager_Pading) / mContext.getResources().getDisplayMetrics().density);
	}

	public void refreshView(ArrayList<JobDetails> arraJobComment_, int totalCommentCount) {  
		this.arraJobComment=arraJobComment_;
		this.totalCommentCount=arraJobComment.size()-1;
		this.CommentCount=totalCommentCount;
		notifyDataSetChanged();
	}
	
	public void refreshView(JobDetails mJobDetails) { 
		this.arraJobComment.add(mJobDetails); 
		notifyDataSetChanged();
	}
	
	public void loadOldCommentView(ArrayList<JobDetails> arraJobComment_) { 
		ArrayList<JobDetails> newlistjob=new ArrayList<JobDetails>();
		
		JobDetails mJobDetails= arraJobComment.get(0);
		mJobDetails.showProgress=0;
		newlistjob.add(mJobDetails);
		
		newlistjob.addAll(arraJobComment_);
		
		ArrayList<JobDetails> urrentlistjob=new ArrayList<JobDetails>(arraJobComment);
		urrentlistjob.remove(0);
		newlistjob.addAll(urrentlistjob);
		
		this.arraJobComment=newlistjob;
		this.totalCommentCount=totalCommentCount+arraJobComment_.size();
		notifyDataSetChanged();
	}
	
	public void loadOldCommentError() { 
		arraJobComment.get(0).showProgress=0;
		notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		return arraJobComment.size();
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
		if (convertView==null) {
			mHolder=new ViewHolder();
			
			convertView=inflater.inflate(R.layout.inflate_commentlist_row_new, parent,false);
			
			mHolder.img_prfpic=(CircleImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.txt_name=(TextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_date=(TextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(TextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_jobdetails=(TextView)convertView.findViewById(R.id.joblistview_txt_contain);
			
			mHolder.txt_btn_reply=(TextView)convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.txt_btn_comment=(TextView)convertView.findViewById(R.id.joblistview_txt_comment);
			mHolder.txt_btn_share=(TextView)convertView.findViewById(R.id.joblistview_txt_share);
			
			
			
			/**New view Appear*/
			
			mHolder.proxytxt_subject=(TextView)convertView.findViewById(R.id.proxyview_txt_subject);
			mHolder.proxytxt_jobdetails=(TextView)convertView.findViewById(R.id.proxyview_txt_contain);
			
			
			mHolder.rel_pagerview=(RelativeLayout)convertView.findViewById(R.id.inc_up_pager);
			mHolder.inc_proxyview=(LinearLayout)convertView.findViewById(R.id.inc_proxyview); 
			mHolder.viewPager=(ViewPager)convertView.findViewById(R.id.jobdetails_album_pager);
			
			/**End of this view**/
			
			mHolder.linear_buttonsection=(LinearLayout)convertView.findViewById(R.id.joblistview_linear_buttonsection); 
					
			mHolder.txt_load=(TextView)convertView.findViewById(R.id.joblistview_txt_loadAllComment); 
			mHolder.rel_progress=(RelativeLayout)convertView.findViewById(R.id.rel_progress); 
			mHolder.rel_details=(RelativeLayout)convertView.findViewById(R.id.inflate_joblistview_rel); 
			mHolder.rel_comment=(RelativeLayout)convertView.findViewById(R.id.inflate_comment_rel); 
			
			mHolder.img_commentprfpic=(CircleImageView)convertView.findViewById(R.id.inflate_comment_img_pic);
			mHolder.txt_commentname=(TextView)convertView.findViewById(R.id.inflate_comment_txt_name);
			mHolder.txt_commentdate=(TextView)convertView.findViewById(R.id.inflate_comment_txt_postdate);
			mHolder.txt_commenteddetails=(TextView)convertView.findViewById(R.id.inflate_comment_txt_contain);
			
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		final JobDetails mJobDetails = arraJobComment.get(position);
		
		if (position==0) {
			mHolder.rel_details.setVisibility(View.VISIBLE);
			mHolder.rel_comment.setVisibility(View.GONE);
			mHolder.linear_buttonsection.setVisibility(View.VISIBLE);
			if (mJobDetails!=null) {
				String url=mJobDetails.getImage();
				if (url!=null && !url.equals("") && !url.equals("null")) {
					imageLoader.displayImage(url, mHolder.img_prfpic, options);
				}
				mHolder.txt_name.setText(mJobDetails.getName());
//				mHolder.txt_name.setText(mJobDetails.getName()+" ("+CommentCount+")");
				mHolder.txt_date.setText("Posted on "+mJobDetails.getPostedon());
				mHolder.txt_subject.setText(mJobDetails.getSubject());
				mHolder.txt_jobdetails.setText(mJobDetails.getContent());
				mHolder.img_arrow.setVisibility(View.GONE);
				
				
				ArrayList<String> Images = mJobDetails.getImages();
				
				if (Images!=null && Images.size()>=1) {
					
					mHolder.proxytxt_subject.setText(mJobDetails.getSubject());
					mHolder.proxytxt_jobdetails.setText(mJobDetails.getContent());
					
					mHolder.rel_pagerview.setVisibility(View.VISIBLE);
					mHolder.inc_proxyview.setVisibility(View.VISIBLE);
					mHolder.viewPager.setVisibility(View.VISIBLE);
					
					mHolder.viewPager.setAdapter(new AlbumPager(mContext, Images));
					mHolder.viewPager.setPadding(pager_Pading, 0, pager_Pading, 0);
					mHolder.viewPager.setClipToPadding(false);
					mHolder.viewPager.setPageMargin(pager_Pading/2); 
					
				}else {
					mHolder.rel_pagerview.setVisibility(View.GONE);
					mHolder.inc_proxyview.setVisibility(View.GONE);
					mHolder.viewPager.setVisibility(View.GONE);
				}
				
				
				mHolder.txt_btn_comment.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (commentitemclicklistner!=null) {
							commentitemclicklistner.commentbuttonCliek(); 
						}
					}
				});
				
				mHolder.txt_btn_share.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Util.onShareClick(mContext,v,arraJobComment.get(position).getSubject(),arraJobComment.get(position).getContent()) ;
					}
				});
				
				mHolder.txt_btn_reply.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new ReplyDialog(mContext, arraJobComment.get(position));
					}
				});
			}
			
			if (CommentCount > totalCommentCount) {
				if (mJobDetails.showProgress==1) {
					mHolder.rel_progress.setVisibility(View.VISIBLE);
					mHolder.txt_load.setVisibility(View.GONE);
				}else {
					mHolder.rel_progress.setVisibility(View.GONE);
					mHolder.txt_load.setVisibility(View.VISIBLE);
				}
				mHolder.txt_load.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mJobDetails.showProgress==0) {
							mJobDetails.showProgress=1;
							notifyDataSetChanged();
							if (commentitemclicklistner!=null) {
								commentitemclicklistner.loadoldData(arraJobComment.get(1).getCommentID()); 
							}
						}
					}
				});
			}else {
				mHolder.rel_progress.setVisibility(View.GONE);
				mHolder.txt_load.setVisibility(View.GONE);
			}
		}else {
			mHolder.rel_details.setVisibility(View.GONE);
			mHolder.rel_comment.setVisibility(View.VISIBLE);
			
			
			String url=mJobDetails.getImage();
			if (url!=null && !url.equals("") && !url.equals("null")) {
				imageLoader.displayImage(url, mHolder.img_commentprfpic, options);
			}
			mHolder.txt_commentname.setText(mJobDetails.getName());
			mHolder.txt_commentdate.setText("Commented on "+mJobDetails.getPostedon());
			mHolder.txt_commenteddetails.setText(mJobDetails.getContent());
		}
		
		return convertView;
	}
	
	private class ViewHolder {
		public CircleImageView img_prfpic,img_commentprfpic;
		public ImageView  img_arrow;
		
		public TextView txt_load , txt_name, txt_date, txt_subject, txt_jobdetails,txt_commentname,txt_commentdate,txt_commenteddetails,proxytxt_subject, proxytxt_jobdetails;
		public TextView txt_btn_comment,txt_btn_share,txt_btn_reply;
		public LinearLayout linear_buttonsection,inc_proxyview;
		public RelativeLayout rel_details,rel_comment,rel_progress,rel_pagerview;
		
		public ViewPager viewPager;
	}
	
	public commentItemClickListner commentitemclicklistner;

	public commentItemClickListner getCommentitemclicklistner() {
		return commentitemclicklistner;
	}

	public void setCommentitemclicklistner(
			commentItemClickListner commentitemclicklistner) {
		this.commentitemclicklistner = commentitemclicklistner;
	}

	public interface commentItemClickListner {
		public void loadoldData(String commentId);
		public void commentbuttonCliek();
	}
	
	
	
	
/****************************************************/
	
	private class AlbumPager extends PagerAdapter {

		private ArrayList<String> arrPhotos;
		private LayoutInflater inflater;
		private Context context_;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private DisplayImageOptions options;
		private float width=0.0f;
		
		public AlbumPager(Context context, ArrayList<String> arrPhotos_) {
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
			width =context_.getResources().getDisplayMetrics().widthPixels;
			width =width - (width*9)/100;

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
			View imageLayout = inflater.inflate(R.layout.inflate_jobdetails_pager_view,view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.iflate_img_pager);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.iflate_pg);
		
			
			
			ViewGroup.LayoutParams pram=new LayoutParams((int)(width),ViewGroup.LayoutParams.MATCH_PARENT);
			
			imageLayout.setLayoutParams(pram);
			
			final String mPhotos = arrPhotos.get(position);
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
			
			
			
			imageLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new AlbumPagerDialog(mContext, arrPhotos,position);
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
