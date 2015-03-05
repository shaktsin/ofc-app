package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.JoinCircleParser;
import com.ofcampus.parser.JoinCircleParser.JoinCircleParserInterface;

public class FragmentYourCircle extends Fragment {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	private static String Authtoken="";
	private ListView yourcircle_list ;

	public static FragmentYourCircle newInstance(int position, Context mContext) {
		FragmentYourCircle f = new FragmentYourCircle();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context = mContext;
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_yourcircle, null);
		initilizView(view);
		return view;
	}

	private void initilizView(View view) {
		yourcircle_list = (ListView) view.findViewById(R.id.fragmentyourcircle_list);
	}


	private void unjoinCircleEvent() {

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		JoinCircleParser mJoinCircleParser = new JoinCircleParser();
		mJoinCircleParser.setJoincircleparserinterface(new JoinCircleParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully Join Circle.");
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mJoinCircleParser.parse(context, mJoinCircleParser.getBody(""), Authtoken);
	}
}
