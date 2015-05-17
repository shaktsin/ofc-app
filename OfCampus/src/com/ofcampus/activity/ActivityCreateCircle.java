/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CreateCircleParser;
import com.ofcampus.parser.CreateCircleParser.CreateCircleParserInterface;
import com.ofcampus.ui.CustomEditText;
import com.ofcampus.ui.CustomTextView;

public class ActivityCreateCircle extends ActionBarActivity implements OnClickListener {

	private static Context context;
	private static String Authtoken = "";
	private CustomEditText edt_CircleName;
	private CustomEditText edt_CircleDesc;
	private CustomTextView txt_modarator;
	private TextView charCountTextView;
	private String isModarator = "";
	private int txtColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createcircle);

		context = ActivityCreateCircle.this;

		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Club");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initilizView();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initilizView() {
		edt_CircleName = (CustomEditText) findViewById(R.id.fragm_createcircle_edt_verifyCode);
		edt_CircleDesc = (CustomEditText) findViewById(R.id.txt_createCircle_desc);
		edt_CircleDesc.addTextChangedListener(mTextEditorWatcher);
		charCountTextView = (TextView) findViewById(R.id.txtview_char_count);
		txtColor = charCountTextView.getCurrentTextColor();
		((CustomTextView) findViewById(R.id.fragm_createcircle_btn_submit)).setOnClickListener(this);
		txt_modarator = (CustomTextView) findViewById(R.id.fragm_createcircle_txtmodarator);
		txt_modarator.setOnClickListener(this);

	}

	private final TextWatcher mTextEditorWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// This sets a textview to the current length
			if (s.toString().trim().length() > 200) {
				return;
			} else {
				Integer length = 200 - s.toString().length();
				charCountTextView.setText(length.toString());
				if (length == 0) {
					charCountTextView.setTextColor(Color.RED);
					return;
				} else {
					charCountTextView.setTextColor(txtColor);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragm_createcircle_btn_submit:
			createCircleEvent();
			break;
		case R.id.fragm_createcircle_txtmodarator:
			txt_modarator.setSelected(txt_modarator.isSelected() ? false : true);
			isModarator = (txt_modarator.isSelected()) ? "false" : "true";
			break;

		default:
			break;
		}
	}

	private void createCircleEvent() {

		String circleName = edt_CircleName.getText().toString().trim();
		String circleDesc = edt_CircleDesc.getText().toString().trim();

		if (circleName != null && circleName.equals("")) {
			Util.ShowToast(context, getResources().getString(R.string.enter_circlename));
			return;
		}

		if (circleDesc != null && circleDesc.equals("")) {
			Util.ShowToast(context, getResources().getString(R.string.enter_circledesc));
			return;
		}

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		CreateCircleParser mCircleParser = new CreateCircleParser();
		mCircleParser.setCreatecircleparserinterface(new CreateCircleParserInterface() {

			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Succesfully Created Your Circle.");
				((OfCampusApplication) context.getApplicationContext()).isNewCircleCreated = true;
				onBackPressed();
			}

			@Override
			public void OnError() {
				Util.ShowToast(context, "Circle Create Error.");
			}
		});
		mCircleParser.parse(context, mCircleParser.getBody(circleName, circleDesc, isModarator, "3"), Authtoken);
	}
}