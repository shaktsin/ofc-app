/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ReplyJobPostParser;
import com.ofcampus.parser.ReplyJobPostParser.ReplyJobPostParserInterface;

public class ReplyDialog {

	private Context mContext;
	private Dialog dialog;
	private JobDetails mJobDetails;

	private UserDetails mUserDetails;
	private String token = "";
	private String subject;

	public ReplyDialog(Context context, JobDetails mJobDetails_) {

		this.mContext = context;
		this.mJobDetails = mJobDetails_;

		this.mUserDetails = UserDetails.getLoggedInUser(mContext);
		this.token = UserDetails.getLoggedInUser(mContext).getAuthtoken();

		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.inflate_cusdialog_hideevent);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		createView();
		dialog.setCancelable(true);
		dialog.show();

	}
	
	private class ShareDataSet{
		String postID="";
		int icon=0;
		String tag="";
		
		public ShareDataSet(String postID_, int icon_, String tag_) {
			super();
			this.postID = postID_;
			this.icon = icon_;
			this.tag = tag_;
		}
	}
	
	ArrayList<ShareDataSet> arraSet=new ArrayList<ReplyDialog.ShareDataSet>();
	ArrayList<ImageView> arraView=new ArrayList<ImageView>();
	private void createView(){
		String emial = mJobDetails.getReplyEmail();
		String whatsapp = mJobDetails.getReplyWatsApp();
		String phno = mJobDetails.getReplyPhone();
		if (emial != null && !emial.equals("") && Integer.parseInt(emial) >= 1) {
			arraSet.add(new ShareDataSet(emial, R.drawable.ic_gmailicon, 4+""));
		}
		if (whatsapp != null && !whatsapp.equals("") && Integer.parseInt(whatsapp) >= 1) {
			arraSet.add(new ShareDataSet(emial, R.drawable.ic_replysms, 5+""));
		}
		if (phno != null && !phno.equals("") && Integer.parseInt(phno) >= 1) {
			arraSet.add(new ShareDataSet(emial, R.drawable.ic_phone, 6+""));
		}
		
		LinearLayout linear=(LinearLayout)dialog.findViewById(R.id.iflate_custdialog_linear_icon);
		LinearLayout.LayoutParams pram=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
		pram.weight = 1.0f;
		
		if (arraSet!=null && arraSet.size()>=1) {
			for (int i = 0; i < arraSet.size(); i++) {
				ShareDataSet mShareDataSet=arraSet.get(i);
				ImageView mView=new ImageView(mContext);
				mView.setImageResource(mShareDataSet.icon);
				mView.setTag(mShareDataSet.tag);
				mView.setScaleType(ScaleType.CENTER_INSIDE);
				mView.setLayoutParams(pram);
				linear.addView(mView);
				arraView.add(mView);
			}
		}
		
		for (ImageView mView : arraView) {
			mView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					replyJobPost(mJobDetails, Integer.parseInt(v.getTag().toString()));
					dialog.dismiss();
				}
			});
		}
		
	}

	private void replyJobPost(final JobDetails mJobDetails, final int state) {
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, mContext.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		subject = mJobDetails.getSubject();

		ReplyJobPostParser mJobPostParser = new ReplyJobPostParser();
		mJobPostParser.setReplyjobpostparserinterface(new ReplyJobPostParserInterface() {

			@Override
			public void OnSuccess(String replyto) {

				switch (state) {
				case 4:
					emailIntent(replyto);
					break;

				case 5:
					smsIntent(replyto);
					break;

				case 6:
					callIntent(replyto);
					break;

				default:
					break;
				}

			}

			@Override
			public void OnError() {

			}
		});
		mJobPostParser.parse(mContext, mJobPostParser.getBody(state + "", mJobDetails.getPostid()), token, state);
	}

	private void emailIntent(String replyto) {
		Util.ShowToast(mContext, replyto);
		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.setType("plain/text");
			sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
			sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { replyto });
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Re: " + subject);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "");
			mContext.startActivity(sendIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callIntent(String no) {
		try {
			if (no != null && !no.equals("")) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + no));
				mContext.startActivity(callIntent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void smsIntent(String no) {
		try {
			if (no != null && !no.equals("")) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", no, null)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
