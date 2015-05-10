package com.ofcampus.activity.helper;

import java.util.ArrayList;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;

import android.content.Context;

public class ListItemEventHelper {
	private Context context;
	private JobDetails jobdetails;
	private String tocken="";

	public ListItemEventHelper(Context context_,JobDetails jobdetails_) {
		this.context = context_;
		this.jobdetails = jobdetails_;
		this.tocken= UserDetails.getLoggedInUser(context).getAuthtoken();
	}
	
	
	public void UnImptCalling(int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostUnHideUnImpParser PostUnHideUnImpParser = new PostUnHideUnImpParser();
		PostUnHideUnImpParser.setPostunhideunimpparserinterface(new PostUnHideUnImpParserInterface() {

			@Override
			public void OnSuccess() {
				jobdetails.important = 0;
			}

			@Override
			public void OnError() {
				Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", jobdetails.getPostid()), tocken);
	}
	
	
	public void HideCalling(final int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostJobHideMarkedParser markedParser = new PostJobHideMarkedParser();
		markedParser.setPostjobhidemarkedparserinterface(new PostJobHideMarkedParserInterface() {

			@Override
			public void OnSuccess() {
				if (state == 1 || state == 3) { // Hide

				} else if (state == 2) {// Important
					jobdetails.important = 1;

				}else if (state == 13) { // Like
					jobdetails.like=0;
				}
			}

			@Override
			public void OnError() {

			}
		});
		markedParser.parse(context, markedParser.getBody(state + "", jobdetails.getPostid()), tocken);
	}
	
	
	
	
	public EventInterface eventinterface;

	public EventInterface getEventinterface() {
		return eventinterface;
	}

	public void setEventinterface(EventInterface eventinterface) {
		this.eventinterface = eventinterface;
	}

	public interface EventInterface {
		public void OnSuccess();
	}
	
}
