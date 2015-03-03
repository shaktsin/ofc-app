package com.ofcampus.activity;

import com.ofcampus.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentJoinCircle extends Fragment {
	
	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	public static FragmentJoinCircle newInstance(int position,Context mContext) {
		FragmentJoinCircle f = new FragmentJoinCircle();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context=mContext;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_joincircle, null);
		initilizView(view);
		return view;
	}
	
	private void initilizView(View view) {
		
	}
}
