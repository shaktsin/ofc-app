/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.EditProfileParser;
import com.ofcampus.parser.EditProfileParser.mEditProfileParserInterface;

public class ActivityProfileEdit extends ActionBarActivity implements OnClickListener {

	public int GALLERY_REQUEST = 1;
	private Context context;
	private UserDetails mDetails;
	private EditText edtfstname, edtlstname, edtaccname, edteyearname;
	private ImageView pic;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	private String Authtoken = "";
	private String path = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profileedit);

		context = ActivityProfileEdit.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Profile Edit");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		loadData();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_createjob, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_tick:
			if (!Util.hasConnection(context)) {
				Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			} else {
				editUpdateEvent();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.myprofile_view_shape:
			galleryCalling();
			break;
		case R.id.profile_circleView:
			galleryCalling();
			break;

		default:
			break;
		}
	}

	private void galleryCalling() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, GALLERY_REQUEST);
	}

	//
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor lCursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			lCursor.moveToFirst();

			int lColumnIndex = lCursor.getColumnIndex(filePathColumn[0]);
			String lpicturePath = lCursor.getString(lColumnIndex);
			imageLoader.displayImage("file://" + lpicturePath, pic, options);
			path = lpicturePath;
		}

	}

	private void initialize() {
		edtfstname = (EditText) findViewById(R.id.editprofile_edt_firstname);
		edtlstname = (EditText) findViewById(R.id.editprofile_edt_lastname);
		edtaccname = (EditText) findViewById(R.id.editprofile_edt_accountName);
		edteyearname = (EditText) findViewById(R.id.editprofile_edt_yearofGrad);
		((ImageView) findViewById(R.id.myprofile_view_shape)).setOnClickListener(this);
		pic = (ImageView) findViewById(R.id.profile_circleView);
		pic.setOnClickListener(this);
	}

	private void loadData() {
		mDetails = UserDetails.getLoggedInUser(context);
		if (mDetails != null) {
			edtfstname.setText(mDetails.getFstname());
			edtlstname.setText(mDetails.getLstname());
			edtaccname.setText(mDetails.getName());
			edteyearname.setText(mDetails.getYearPass());
			String path = mDetails.getImage();
			if (path != null && !path.equals("")) {
				imageLoader.displayImage(path, pic, options);
			}
			Authtoken = mDetails.getAuthtoken();
		}

	}

	private void editUpdateEvent() {
		String fstname = edtfstname.getText().toString().trim();
		String lstname = edtlstname.getText().toString().trim();
		String accname = edtaccname.getText().toString().trim();
		String yearpass = edteyearname.getText().toString().trim();

		if (fstname.equals("")) {
			Util.ShowToast(context, "Please enter First Name.");
			return;
		}
		if (lstname.equals("")) {
			Util.ShowToast(context, "Please enter Last Name.");
			return;
		}
		if (accname.equals("")) {
			Util.ShowToast(context, "Please enter Account Name.");
			return;
		}
		if (yearpass.equals("")) {
			Util.ShowToast(context, "Please enter Year of Graduation.");
			return;
		}

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JSONObject postDate = getJSONBody(fstname, lstname, accname, yearpass);

		EditProfileParser mEditProfileParser = new EditProfileParser();
		mEditProfileParser.setMeditprofileparserinterface(new mEditProfileParserInterface() {

			@Override
			public void OnSuccess(UserDetails Details) {
				((OfCampusApplication) context.getApplicationContext()).profileEditSuccess = true;
				onBackPressed();
			}

			@Override
			public void OnError() {

			}
		});
		mEditProfileParser.parse(context, postDate, Authtoken, path);

	}

	private JSONObject getJSONBody(String firstName, String lastName, String accountName, String yearOfGrad) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("firstName", firstName);
			jsObj.put("lastName", lastName);
			jsObj.put("accountName", accountName);
			jsObj.put("yearOfGrad", yearOfGrad);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
}