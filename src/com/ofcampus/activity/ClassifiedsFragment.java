package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ofcampus.R;

public class ClassifiedsFragment extends Fragment implements OnClickListener{

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
	
	public static ClassifiedsFragment newInstance(int position, Context mContext) { 
		ClassifiedsFragment f = new ClassifiedsFragment();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_classifieds, null);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			break;
		}
	}
}