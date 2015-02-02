package com.ofcampus.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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
	private String token="";
	
	public ReplyDialog(Context context, JobDetails mJobDetails_){
		
		this.mContext=context;
		this.mJobDetails=mJobDetails_;
		
		this.mUserDetails=UserDetails.getLoggedInUser(mContext);
		this.token=UserDetails.getLoggedInUser(mContext).getAuthtoken();
		
		dialog= new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.inflate_cusdialog_hideevent);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		
		final String emial=mJobDetails.getReplyEmail();
		final String whatsapp=mJobDetails.getReplyWatsApp();
		final String phno=mJobDetails.getReplyPhone();
		
		
		
			TextView txt_email=(TextView) dialog.findViewById(R.id.iflate_custdialog_email);
			txt_email.setVisibility(View.VISIBLE);
			txt_email.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (emial!=null && !emial.equals("") && Integer.parseInt(emial)>=1) {
						replyJobPost(mJobDetails, 4);
						dialog.dismiss();
					}else {
						dialog.dismiss();
					}
				}
			});
		
		
			TextView txt_whatsapp=(TextView) dialog.findViewById(R.id.iflate_custdialog_whatsapp);
			txt_whatsapp.setVisibility(View.VISIBLE);
			txt_whatsapp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (whatsapp!=null && !whatsapp.equals("")  && Integer.parseInt(whatsapp)>=1) {
						replyJobPost(mJobDetails, 5);
						dialog.dismiss();
					}else {
						dialog.dismiss();
					}
				}
			});
		
		
			TextView txt_ph=(TextView) dialog.findViewById(R.id.iflate_custdialog_ph);
			txt_ph.setVisibility(View.VISIBLE);
			txt_ph.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (phno!=null && !phno.equals("") && Integer.parseInt(phno)>=1) {
						replyJobPost(mJobDetails, 6);
						dialog.dismiss();
					}else {
						dialog.dismiss();
					}
				}
			});
			dialog.setCancelable(true);
			dialog.show();
		
	}
	
	
	private void replyJobPost(final JobDetails mJobDetails, final int state){
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext,mContext.getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		ReplyJobPostParser mJobPostParser=new ReplyJobPostParser();		
		mJobPostParser.setReplyjobpostparserinterface(new ReplyJobPostParserInterface() {
			
			@Override
			public void OnSuccess(String replyto) {
				
						switch (state) {
						case 4:
							emailIntent(replyto);
							break;

						case 5:

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
		mJobPostParser.parse(mContext, mJobPostParser.getBody(state+"", mJobDetails.getPostid()), token,state);
	}
	
	
	
	private void emailIntent(String replyto){ 
		Util.ShowToast(mContext, replyto);
		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.setType("plain/text");
			sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
			sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{replyto});
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "OfCampus");
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Please see this post.");
			mContext.startActivity(sendIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void callIntent(String no){
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+no));
			mContext.startActivity(callIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
