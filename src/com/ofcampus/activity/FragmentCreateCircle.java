package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ofcampus.R;
import com.ofcampus.ui.CustomEditText;

public class FragmentCreateCircle extends Fragment implements OnClickListener{

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	private CustomEditText edt_CircleName;

	public static FragmentCreateCircle newInstance(int position, Context mContext) {
		FragmentCreateCircle f = new FragmentCreateCircle();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context = mContext;
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
		((CustomEditText)view.findViewById(R.id.fragm_createcircle_btn_submit)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragm_createcircle_btn_submit:
			
			break;

		default:
			break;
		}
	}
}
