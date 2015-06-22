package com.ofcampus.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
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
import com.ofcampus.activity.ActivityClassifiedDetails;
import com.ofcampus.activity.ActivityJobPostedUserDetails;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.DocumentPath;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.PdfDocLoader;
import com.ofcampus.parser.PdfDocLoader.LoadListner;
import com.ofcampus.ui.CustomTextView;

public class ClassifiedListBaseAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JobDetails> jobs = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_post;

	public ClassifiedListBaseAdapter(Context context, ArrayList<JobDetails> arrJobs) {

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
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size() - 1).getPostid());
		notifyDataSetChanged();
	}

	public void refreshSwipeData(ArrayList<JobDetails> arrJobs) {
		ArrayList<JobDetails> Jobs_ = new ArrayList<JobDetails>(arrJobs);
		Jobs_.addAll(jobs);
		this.jobs = Jobs_;
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size() - 1).getPostid());
		notifyDataSetChanged();
	}

	public void refreshloadmoreData(ArrayList<JobDetails> arrJobs) {
		jobs.addAll(arrJobs);
		setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size() - 1).getPostid());
		notifyDataSetChanged();
	}

	public void hideNews(JobDetails hideJob) {
		ArrayList<JobDetails> currentJobList = new ArrayList<JobDetails>(this.jobs);
		ArrayList<JobDetails> modifyJobList = new ArrayList<JobDetails>();
		for (JobDetails jobDetails : currentJobList) {
			if (!hideJob.getPostid().equals(jobDetails.getPostid())) {
				modifyJobList.add(jobDetails);
			}
		}
		this.jobs = modifyJobList;
		if (jobs.size() >= 1) {
			setIDS(jobs.get(0).getPostid(), jobs.get(jobs.size() - 1).getPostid());
		} else {
			setIDS("", "");
		}
		notifyDataSetChanged();
	}

	public void importantNews(JobDetails hideJob) {
		for (JobDetails jobDetails : jobs) {
			if (hideJob.getPostid().equals(jobDetails.getPostid())) {
				jobDetails.important = 1;
			}
		}
		notifyDataSetChanged();
	}

	public void unimportantNews(JobDetails hideJob) {
		for (JobDetails jobDetails : jobs) {
			if (hideJob.getPostid().equals(jobDetails.getPostid())) {
				jobDetails.important = 0;
			}
		}
		notifyDataSetChanged();
	}

	public void likRefreshJob(JobDetails classifiedLiked) {
		for (JobDetails newsDetails : jobs) {
			if (classifiedLiked.getPostid().equals(newsDetails.getPostid())) {
				// postDetails.like = (postDetails.getLike() == 0) ? 1 : 0;
				newsDetails.like = 1;
			}
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {

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
			mHolder.txt_locationandsecprimactg = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_locationandindusrole);
			mHolder.txt_contain = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_contain);

			mHolder.txt_replycount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_reply_count);
			mHolder.txt_likecount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_share_count);
			mHolder.txt_commentcount = (CustomTextView) convertView.findViewById(R.id.joblistview_txt_comment_count);

			mHolder.inflate_doc = (CardView) convertView.findViewById(R.id.inflate_docview);
			mHolder.doc_icon = (ImageView) convertView.findViewById(R.id.doc_icon);
			mHolder.doc_dnd = (ImageView) convertView.findViewById(R.id.doc_downloadIcon);
			mHolder.doc_name = (CustomTextView) convertView.findViewById(R.id.doc_name);

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

			String postedOnText = Util.getPostedOnText(mJobDetails.getPostedon());
			mHolder.txt_postdate.setText(postedOnText);
			mHolder.txt_subject.setText(mJobDetails.getSubject());
			mHolder.txt_contain.setText(mJobDetails.getContent());

			if (!TextUtils.isEmpty(mJobDetails.getLocationandinds())) {
				mHolder.txt_locationandsecprimactg.setVisibility(View.VISIBLE);
				mHolder.txt_locationandsecprimactg.setText(mJobDetails.getLocationandinds());
			} else {
				mHolder.txt_locationandsecprimactg.setVisibility(View.GONE);
			}

			mHolder.img_important.setVisibility(View.VISIBLE);
			mHolder.img_like.setVisibility(View.VISIBLE);
			mHolder.img_arrow.setVisibility(View.VISIBLE);

			mHolder.img_like.setSelected((mJobDetails.like == 1) ? true : false);
			mHolder.img_important.setSelected((mJobDetails.getImportant() == 1) ? true : false);

			// String replycount = mJobDetails.getNumreplies();
			String likecount = mJobDetails.getNumlikes();
			String commentcount = mJobDetails.getNumcomment();

			// if (replycount != null && replycount.length() >= 1 &&
			// !replycount.equalsIgnoreCase("0")) {
			// mHolder.txt_replycount.setVisibility(View.VISIBLE);
			// mHolder.txt_replycount.setText(replycount + " replys");
			// } else {
			// mHolder.txt_replycount.setVisibility(View.GONE);
			// }

			if (likecount != null && likecount.length() >= 1 && !likecount.equalsIgnoreCase("0")) {
				mHolder.txt_likecount.setVisibility(View.VISIBLE);
				mHolder.txt_likecount.setText(likecount + " likes");
			} else {
				mHolder.txt_likecount.setVisibility(View.GONE);
			}

			if (commentcount != null && commentcount.length() >= 1 && !commentcount.equalsIgnoreCase("0")) {
				mHolder.txt_commentcount.setVisibility(View.VISIBLE);
				mHolder.txt_commentcount.setText(commentcount + " comments");
			} else {
				mHolder.txt_commentcount.setVisibility(View.GONE);
			}

			final ArrayList<ImageDetails> Images = mJobDetails.getImages();
			ArrayList<DocDetails> Docs = mJobDetails.getDoclist();

			if (Docs != null && Docs.size() >= 1) {
				mHolder.inflate_doc.setVisibility(View.VISIBLE);
				showDoc(Docs.get(0).getDocURL(), mHolder.doc_dnd, mHolder.doc_icon, mHolder.doc_name, mHolder.inflate_doc);
			} else {
				mHolder.inflate_doc.setVisibility(View.GONE);
			}

			if (Images != null && Images.size() >= 1) {
				mHolder.joblistview_img_post_rel.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Images.get(0).getImageURL(), mHolder.img_post, options_post);
				mHolder.img_post.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						viewOnClick(mJobDetails);
					}
				});

			} else {
				mHolder.joblistview_img_post_rel.setVisibility(View.GONE);
			}

			mHolder.profilepic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					userProfile(mJobDetails);
				}
			});

			mHolder.txt_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					userProfile(mJobDetails);
				}
			});

			mHolder.txt_subject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					viewOnClick(mJobDetails);
				}
			});

			mHolder.txt_contain.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					viewOnClick(mJobDetails);
				}
			});

			mHolder.btn_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classifiedlistinterface != null) {
						classifiedlistinterface.replyClickEvent(mJobDetails);
					}
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
					viewOnClick(mJobDetails);
				}
			});

			mHolder.img_important.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classifiedlistinterface != null) {
						if (mJobDetails.getImportant() == 0) {
							classifiedlistinterface.impClieckEvent(mJobDetails);
						} else {
							classifiedlistinterface.unimpClieckEvent(mJobDetails);
						}

					}
				}
			});

			mHolder.img_like.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (classifiedlistinterface != null && mJobDetails.getLike() == 0) {
						classifiedlistinterface.likeCliekEvent(mJobDetails);
					}
				}
			});

			mHolder.img_arrow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (classifiedlistinterface != null) {
						classifiedlistinterface.hideClieckEvent(mJobDetails);
					}
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView profilepic, doc_icon, doc_dnd;
		ImageView img_arrow, img_important, img_like;
		CustomTextView txt_name, txt_postdate, txt_subject, txt_contain, doc_name, txt_locationandsecprimactg;
		CustomTextView txt_replycount, txt_likecount, txt_commentcount;
		ImageView btn_reply, btn_share, btn_comment;
		ImageView img_post;
		CardView joblistview_img_post_rel, inflate_doc;
	}

	public void setIDS(String fstID, String lstID) {
		if (classifiedlistinterface != null) {
			classifiedlistinterface.firstIDAndlastID(fstID, lstID);
		}
	}

	private void showDoc(final String DocPath, final ImageView doc_dnd, ImageView doc_icon, TextView doc_name, CardView view) {

		String[] splt = DocPath.split("/");
		final String fileNAme = splt[splt.length - 1];
		doc_name.setText(fileNAme);

		if (Util.isPdfFile(DocPath)) {
			doc_icon.setImageResource(R.drawable.pdf);
		} else {
			doc_icon.setImageResource(R.drawable.doc);
		}

		if (Util.isContainDocFile(DocPath)) {
			DocumentPath mDocumentPath = DocumentPath.getPath(mContext);
			if (mDocumentPath != null && mDocumentPath.mapPath.containsKey(DocPath)) {
				doc_dnd.setImageResource(R.drawable.doc_green);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DocumentPath mDocumentPath = DocumentPath.getPath(mContext);
						String path = mDocumentPath.mapPath.get(DocPath);
						Util.viewerOpen(mContext, path);
					}
				});
			} else {
				doc_dnd.setImageResource(R.drawable.docload_g);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						PdfDocLoader mDocLoader = new PdfDocLoader();
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

		} else {
			view.setVisibility(View.GONE);
		}
	}

	public void viewOnClick(JobDetails mJobDetails) {
		((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
		Intent mIntent = new Intent(mContext, ActivityClassifiedDetails.class);
		Bundle mBundle = new Bundle();
		mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[2]);
		mIntent.putExtras(mBundle);
		((Activity) mContext).startActivity(mIntent);
		((Activity) mContext).overridePendingTransition(0, 0);
	}

	public void userProfile(JobDetails mJobDetails) {
		Intent mIntent = new Intent(mContext, ActivityJobPostedUserDetails.class);
		mIntent.putExtra("isUserCame", (mJobDetails.getId().equals(UserDetails.getLoggedInUser(mContext).getUserID())) ? true : false);
		((OfCampusApplication) mContext.getApplicationContext()).jobdetails = mJobDetails;
		mContext.startActivity(mIntent);
		((Activity) mContext).overridePendingTransition(0, 0);
	}

	public ClassifiedListInterface classifiedlistinterface;

	public ClassifiedListInterface getClassifiedlistinterface() {
		return classifiedlistinterface;
	}

	public void setClassifiedlistinterface(ClassifiedListInterface classifiedlistinterface) {
		this.classifiedlistinterface = classifiedlistinterface;
	}

	public interface ClassifiedListInterface {

		public void firstIDAndlastID(String fstID, String lstID);

		public void hideClieckEvent(JobDetails mJobDetails);

		// public void arrowSpamClieckEvent(JobDetails mJobDetails);
		public void impClieckEvent(JobDetails mJobDetails);

		public void unimpClieckEvent(JobDetails mJobDetails);

		public void replyClickEvent(JobDetails mJobDetails);

		public void likeCliekEvent(JobDetails mJobDetails);
	}

}