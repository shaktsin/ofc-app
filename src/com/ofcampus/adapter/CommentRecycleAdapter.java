package com.ofcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.R;
import com.ofcampus.model.JobDetails;

public class CommentRecycleAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private Context mContext;
	private ArrayList<JobDetails> arraJobComment;
	
	private ImageLoader imageLoader=ImageLoader.getInstance();
	private DisplayImageOptions options;
	
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
		
	}

	public void refreshView(ArrayList<JobDetails> arraJobComment_) { 
		this.arraJobComment=arraJobComment_;
		notifyDataSetChanged();
	}
	
	public void refreshView(JobDetails mJobDetails) { 
		this.arraJobComment.add(mJobDetails); 
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder mHolder;
		if (convertView==null) {
			mHolder=new ViewHolder();
			
			convertView=inflater.inflate(R.layout.inflate_commentlist_row, parent,false);
			
			mHolder.img_prfpic=(ImageView)convertView.findViewById(R.id.joblistview_img_pic);
			mHolder.img_arrow=(ImageView)convertView.findViewById(R.id.joblistview_img_arrow);
			mHolder.txt_name=(TextView)convertView.findViewById(R.id.joblistview_txt_name);
			mHolder.txt_date=(TextView)convertView.findViewById(R.id.joblistview_txt_postdate);
			mHolder.txt_subject=(TextView)convertView.findViewById(R.id.joblistview_txt_subject);
			mHolder.txt_jobdetails=(TextView)convertView.findViewById(R.id.joblistview_txt_contain);
			mHolder.linear_buttonsection=(LinearLayout)convertView.findViewById(R.id.joblistview_linear_buttonsection); 
					
			mHolder.txt_load=(TextView)convertView.findViewById(R.id.joblistview_txt_loadAllComment); 
			
			
			mHolder.rel_details=(RelativeLayout)convertView.findViewById(R.id.inflate_joblistview_rel); 
			mHolder.rel_comment=(RelativeLayout)convertView.findViewById(R.id.inflate_comment_rel); 
			
			mHolder.img_commentprfpic=(ImageView)convertView.findViewById(R.id.inflate_comment_img_pic);
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
			mHolder.linear_buttonsection.setVisibility(View.GONE);
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
			}
			if (arraJobComment.size()>12) {
				mHolder.txt_load.setVisibility(View.VISIBLE);
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
		public ImageView img_prfpic, img_arrow,img_commentprfpic;
		public TextView txt_load , txt_name, txt_date, txt_subject, txt_jobdetails,txt_commentname,txt_commentdate,txt_commenteddetails;
		public LinearLayout linear_buttonsection;
		public RelativeLayout rel_details,rel_comment;
	}

	
}
