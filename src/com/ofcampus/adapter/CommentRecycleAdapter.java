/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
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
import android.widget.ImageView.ScaleType;
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
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.DocumentPath;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.parser.PdfDocLoader;
import com.ofcampus.parser.PdfDocLoader.LoadListner;
import com.ofcampus.ui.AlbumPagerDialog;
import com.ofcampus.ui.CustomTextView;
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
	private float width=0.0f;
	
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
		width =mContext.getResources().getDisplayMetrics().widthPixels;
		width =width - (width*9)/100;
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
			
			convertView=inflater.inflate(R.layout.inflate_comment_jobdetails, parent,false);
			mHolder.rel_jobdetails=(RelativeLayout)convertView.findViewById(R.id.view_jobdetails);
			mHolder.rel_comment=(RelativeLayout)convertView.findViewById(R.id.view_comment);
			
			
			/**JOb Details**/
			
			mHolder.img_prfpic=(ImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.txt_name=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_date=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_jobdetails=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_contain);
			
			mHolder.viewPager=(ViewPager)convertView.findViewById(R.id.jobdetails_album_pager);
			mHolder.docPager=(ViewPager)convertView.findViewById(R.id.jobdetails_doc_pager);
			
			
			mHolder.txt_btn_reply=(ImageView)convertView.findViewById(R.id.joblistview_txt_reply);
			mHolder.txt_btn_comment=(ImageView)convertView.findViewById(R.id.joblistview_txt_comment);
			mHolder.txt_btn_share=(ImageView)convertView.findViewById(R.id.joblistview_txt_share);
			
			mHolder.txt_load=(CustomTextView)convertView.findViewById(R.id.joblistview_txt_loadAllComment); 
			mHolder.rel_progress=(RelativeLayout)convertView.findViewById(R.id.rel_progress); 
			
			/**Comment Section*/
			
			mHolder.img_commentprfpic=(ImageView)convertView.findViewById(R.id.inflate_comment_img_pic);
			mHolder.txt_commentname=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_name);
			mHolder.txt_commentdate=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_postdate);
			mHolder.txt_commenteddetails=(CustomTextView)convertView.findViewById(R.id.inflate_comment_txt_contain);
			
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		final JobDetails mJobDetails = arraJobComment.get(position);
		
		if (position==0) {
	
			mHolder.rel_jobdetails.setVisibility(View.VISIBLE);
			mHolder.rel_comment.setVisibility(View.GONE);
			
			if (mJobDetails!=null) {
				String url=mJobDetails.getImage();
				if (url!=null && !url.equals("") && !url.equals("null")) {
					imageLoader.displayImage(url, mHolder.img_prfpic, options);
				}
				mHolder.txt_name.setText(mJobDetails.getName());
				mHolder.txt_date.setText("Posted on "+mJobDetails.getPostedon());
				mHolder.txt_subject.setText(mJobDetails.getSubject());
				mHolder.txt_jobdetails.setText(mJobDetails.getContent());
				mHolder.img_arrow.setVisibility(View.GONE);
				
				
				ArrayList<ImageDetails> Images = mJobDetails.getImages();
				ArrayList<DocDetails> Docs = mJobDetails.getDoclist();
				
				if (Images!=null && Images.size()>=1) {
					mHolder.viewPager.setVisibility(View.VISIBLE);
					mHolder.viewPager.setAdapter(new AlbumPager(mContext, Images));
					mHolder.viewPager.setPadding(pager_Pading, 0, pager_Pading, 0);
					mHolder.viewPager.setClipToPadding(false);
					mHolder.viewPager.setPageMargin(pager_Pading/2); 
					
				}else {
					mHolder.viewPager.setVisibility(View.GONE);
				}
				
				if (Docs!=null && Docs.size()>=1) {
					mHolder.docPager.setVisibility(View.VISIBLE);
					mHolder.docPager.setAdapter(new DocPager(mContext, Docs));
					mHolder.docPager.setPadding(pager_Pading, 0, pager_Pading, 0);
					mHolder.docPager.setClipToPadding(false);
					mHolder.docPager.setPageMargin(pager_Pading/2); 
				}else {
					mHolder.docPager.setVisibility(View.GONE);
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
			mHolder.rel_jobdetails.setVisibility(View.GONE);
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
		public ImageView img_prfpic,img_commentprfpic;
		public ImageView  img_arrow;
		public CustomTextView txt_name, txt_date, txt_subject, txt_jobdetails,txt_commentname,txt_commentdate,txt_commenteddetails,txt_load;
		public ImageView txt_btn_comment,txt_btn_share,txt_btn_reply;
		public RelativeLayout rel_jobdetails,rel_comment,rel_progress;
		
		public ViewPager viewPager,docPager;
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
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
		
		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.inflate_jobdetails_pager_view,view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.iflate_img_pager);		
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.iflate_pg);	

			ViewGroup.LayoutParams pram=new LayoutParams((int)(width),ViewGroup.LayoutParams.MATCH_PARENT);
			
			imageLayout.setLayoutParams(pram);
			
			final String mPhotos = arrPhotos.get(position).getImageURL();
			imageView.setScaleType(ScaleType.CENTER_CROP);
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
	
	/****************************Doc Pager*********************************/
	
	
	private class DocPager extends PagerAdapter {

		private ArrayList<DocDetails> arrDocDetails_;
		private LayoutInflater inflater;
		private Context context_;
		private float width=0.0f;
		
		public DocPager(Context context, ArrayList<DocDetails> arrDocDetails) { 
			this.arrDocDetails_ = arrDocDetails;
			this.context_ = context;
			inflater = LayoutInflater.from(context_);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return arrDocDetails_.size();
		}
		
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
		
		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.inflate_doclayout,view, false);
			assert imageLayout != null;
			ImageView doc_icon = (ImageView) imageLayout.findViewById(R.id.doc_icon);
			final ImageView doc_dnd = (ImageView) imageLayout.findViewById(R.id.doc_downloadIcon);	
			TextView doc_name = (TextView) imageLayout.findViewById(R.id.doc_name);	
			
			final String DocPath = arrDocDetails_.get(position).getDocURL();
			String[] splt = DocPath.split("/");
			final String fileNAme = splt[splt.length - 1];
			doc_name.setText(fileNAme);
			
			if (Util.isPdfFile(DocPath)) {
				doc_icon.setImageResource(R.drawable.pdf);
			}else {
				doc_icon.setImageResource(R.drawable.doc);
			}
					
			if (Util.isContainDocFile(DocPath)) { 
				DocumentPath mDocumentPath=DocumentPath.getPath(mContext);
				if (mDocumentPath!=null && mDocumentPath.mapPath.containsKey(DocPath)) {
					doc_dnd.setImageResource(R.drawable.doc_green);
					imageLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							DocumentPath mDocumentPath=DocumentPath.getPath(mContext);
							String path = mDocumentPath.mapPath.get(DocPath);
							Util.viewerOpen(mContext, path);
						}
					});
				}else {
					doc_dnd.setImageResource(R.drawable.docload_g);
					imageLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							PdfDocLoader mDocLoader=new PdfDocLoader();
							mDocLoader.setLoadlistner(new LoadListner() {
								
								@Override
								public void OnErroe(View v) {
									doc_dnd.setImageResource(R.drawable.docload_g);
								}
								
								@Override
								public void OnComplete(View v) {
									doc_dnd.setImageResource(R.drawable.doc_green);
									notifyDataSetChanged();
								}
								
								@Override
								public void OnCancel(View v) {
									doc_dnd.setImageResource(R.drawable.docload_g);
								}
							});
							mDocLoader.downloadDialog(mContext, fileNAme, DocPath, doc_dnd);
							
						}
					});
				}
				
			}
			
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
