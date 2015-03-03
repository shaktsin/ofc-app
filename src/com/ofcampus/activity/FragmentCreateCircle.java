package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CreateCircleParser;
import com.ofcampus.parser.CreateCircleParser.CreateCircleParserInterface;
import com.ofcampus.ui.CustomEditText;
import com.ofcampus.ui.CustomTextView;

public class FragmentCreateCircle extends Fragment implements OnClickListener{

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	private static String Authtoken="";
	private CustomEditText edt_CircleName;
	private CustomTextView txt_modarator;
	private String isModarator="";

	public static FragmentCreateCircle newInstance(int position, Context mContext) {
		FragmentCreateCircle f = new FragmentCreateCircle();
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
		View view = inflater.inflate(R.layout.fragment_createcircle, null);
		initilizView(view);
		return view;
	}

	private void initilizView(View view) {
		edt_CircleName=(CustomEditText)view.findViewById(R.id.fragm_createcircle_edt_verifyCode);
		((CustomTextView)view.findViewById(R.id.fragm_createcircle_btn_submit)).setOnClickListener(this);
		txt_modarator =(CustomTextView)view.findViewById(R.id.fragm_createcircle_txtmodarator);
		txt_modarator.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragm_createcircle_btn_submit:
			createCircleEvent();
			break;
		case R.id.fragm_createcircle_txtmodarator:
			txt_modarator.setSelected(txt_modarator.isSelected()?false:true); 
			isModarator = (txt_modarator.isSelected())?"true":"false";
			break;

		default:
			break;
		}
	}
	
	private void createCircleEvent(){
		
		String circleName=edt_CircleName.getText().toString().trim();
		
		if (circleName!=null && circleName.equals("")) {
			Util.ShowToast(context,getResources().getString(R.string.enter_circlename));
			return;
		}
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		CreateCircleParser mCircleParser=new CreateCircleParser();
		mCircleParser.setCreatecircleparserinterface(new CreateCircleParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully Created Your Circle.");
			}
			
			@Override
			public void OnError() {
				Util.ShowToast(context,"Circle Create Error.");
			}
		});
		mCircleParser.parse(context, mCircleParser.getBody(circleName, isModarator, "3"), Authtoken);
	}
}
