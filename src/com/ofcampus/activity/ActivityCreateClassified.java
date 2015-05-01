/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.Util.ClassifSpinnerType;
import com.ofcampus.model.CustomSpinnerDataSets;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.PrepareListForClassified;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CreateClassifiedParser;
import com.ofcampus.parser.CreateClassifiedParser.CreateClassifiedInterface;
import com.ofcampus.parser.PrepareForClassifiedParser;
import com.ofcampus.parser.PrepareForClassifiedParser.PrepareParserInterface;
import com.ofcampus.ui.CustomSpinner;
import com.ofcampus.ui.CustomSpinner.changeListner;

public class ActivityCreateClassified extends ActionBarActivity implements OnClickListener, changeListner {

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public static int GALLERY_REQUEST = 1;
	public static int DOC_REQUEST = 2;
	private UserDetails mDetails;
	private EditText classifiedheadline, classifieddetails, edt_email, edt_phno, edt_sms;
	private TextView circle_spn, catagory_spn, subcatagory_spn, location_spn;

	private ArrayList<View> arrayView = new ArrayList<View>();
	private HorizontalListView mHlvCustomList;
	private ImageAttachmentArrayAdapter mCustomArrayAdapter;
	private ArrayList<AttachmentDataSet> picdatasets = new ArrayList<AttachmentDataSet>();
	private ArrayList<AttachmentDataSet> docpdfdatasets = new ArrayList<AttachmentDataSet>();

	private HorizontalListView pdfattached_list;
	private DOCPDFArrayAdapter mDOCPDFArrayAdapter;

	private ArrayList<CustomSpinnerDataSets> circleList;
	private ArrayList<CustomSpinnerDataSets> categoryList;
	private ArrayList<CustomSpinnerDataSets> subCategoryList;
	private ArrayList<CustomSpinnerDataSets> locationList;
	private Toolbar toolbar;
	private CustomSpinner mCustomSpinner = null;
	private Context context;
	private View parenScroll = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_classified);

		context = ActivityCreateClassified.this;
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Classified");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDetails = UserDetails.getLoggedInUser(context);

		initialize();
		loadData();

		getWindow().getDecorView().postDelayed(new Runnable() {

			@Override
			public void run() {
				int displayH = parenScroll.getHeight();
				RelativeLayout.LayoutParams pram = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, displayH);
				(findViewById(R.id.classified_rel_main)).setLayoutParams(pram);
				parenScroll.setVisibility(View.VISIBLE);
			}
		}, 200);
	}

	@Override
	public void onBackPressed() {
		if (anyviewVisible()) {
			resetViewAll();
		} else {
			super.onBackPressed();
			overridePendingTransition(0, 0);
			finish();
		}

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
				resetViewAll();
				createEvent();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_classified_edit_circle:
			if (circleList != null && circleList.size() >= 1) {
				mCustomSpinner = new CustomSpinner(context, v, circleList, ClassifSpinnerType.MULTISELECTION);
				mCustomSpinner.setChangelistner(this);
				mCustomSpinner.show();
			}

			break;

		case R.id.activity_classified_edit_catagory:
			if (categoryList != null && categoryList.size() >= 1) {
				mCustomSpinner = new CustomSpinner(context, v, categoryList, ClassifSpinnerType.SINGLESELECTION);
				mCustomSpinner.setChangelistner(this);
				mCustomSpinner.show();
			}

			break;

		case R.id.activity_classified_edit_subcatagory:
			if (subCategoryList != null && subCategoryList.size() >= 1) {
				mCustomSpinner = new CustomSpinner(context, v, subCategoryList, ClassifSpinnerType.MULTISELECTION);
				mCustomSpinner.setChangelistner(this);
				mCustomSpinner.show();
			}

			break;

		case R.id.activity_classified_edit_location:
			if (locationList != null && locationList.size() >= 1) {
				mCustomSpinner = new CustomSpinner(context, v, locationList, ClassifSpinnerType.MULTISELECTION);
				mCustomSpinner.setChangelistner(this);
				mCustomSpinner.show();
			}

			break;

		case R.id.activity_createjob_attached:
			Util.HideKeyBoard(context, v);
			if (arrayView.get(0).getVisibility() == View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(0);
			break;

		case R.id.activity_createjob_rply:
			Util.HideKeyBoard(context, v);
			if (arrayView.get(1).getVisibility() == View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(1);
			break;

		case R.id.activity_createjob_docattached:
			Util.HideKeyBoard(context, v);
			if (arrayView.get(2).getVisibility() == View.VISIBLE) {
				resetViewAll();
				return;
			}
			loadForntView(2);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemChange(View mView) {
		switch (mView.getId()) {
		case R.id.activity_classified_edit_circle:
			if (circleList != null && circleList.size() >= 1) {
				String txt = "";
				for (CustomSpinnerDataSets mCircle : circleList) {
					if (mCircle.isSelected == 1) {
						txt = txt + "," + mCircle.getTitle();
					}
				}
				txt = (txt.length() >= 1) ? txt.substring(1) : "";
				circle_spn.setText(txt);
			}

			break;

		case R.id.activity_classified_edit_catagory:
			if (categoryList != null && categoryList.size() >= 1) {
				String txt = "";
				int selectP = 0;
				for (int i = 0; i < categoryList.size(); i++) {
					CustomSpinnerDataSets category = categoryList.get(i);
					if (category.isSelected == 1) {
						txt = txt + "," + category.getTitle();
						selectP = i;
					}
				}
				txt = (txt.length() >= 1) ? txt.substring(1) : "";
				catagory_spn.setText(txt);
				subcatagory_spn.setText("");
				subCategoryList = categoryList.get(selectP).getList();
			}

			break;

		case R.id.activity_classified_edit_subcatagory:
			if (subCategoryList != null && subCategoryList.size() >= 1) {
				String txt = "";
				for (CustomSpinnerDataSets subcatagory : subCategoryList) {
					if (subcatagory.isSelected == 1) {
						txt = txt + "," + subcatagory.getTitle();
					}
				}
				txt = (txt.length() >= 1) ? txt.substring(1) : "";
				subcatagory_spn.setText(txt);
			}

			break;

		case R.id.activity_classified_edit_location:
			if (locationList != null && locationList.size() >= 1) {
				String txt = "";
				for (CustomSpinnerDataSets location : locationList) {
					if (location.isSelected == 1) {
						txt = txt + "," + location.getTitle();
					}
				}
				txt = (txt.length() >= 1) ? txt.substring(1) : "";
				location_spn.setText(txt);
			}

			break;

		default:
			break;
		}

	}

	private void initialize() {
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_profilepic).showImageForEmptyUri(R.drawable.ic_profilepic).showImageOnFail(R.drawable.ic_profilepic)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		parenScroll = findViewById(R.id.classified_parent);
		parenScroll.setVisibility(View.INVISIBLE);
		mHlvCustomList = (HorizontalListView) findViewById(R.id.hlvCustomList);
		mCustomArrayAdapter = new ImageAttachmentArrayAdapter(context, getpicArray(null));
		mHlvCustomList.setAdapter(mCustomArrayAdapter);

		pdfattached_list = (HorizontalListView) findViewById(R.id.pdfattached_list);
		mDOCPDFArrayAdapter = new DOCPDFArrayAdapter(context, getDOCArray(null));
		pdfattached_list.setAdapter(mDOCPDFArrayAdapter);

		classifiedheadline = (EditText) findViewById(R.id.activity_classified_edit_jobtitle);
		classifieddetails = (EditText) findViewById(R.id.activity_classified_edit_jobdescrip);

		edt_email = (EditText) findViewById(R.id.activity_create_edt_emailreply);
		edt_phno = (EditText) findViewById(R.id.activity_create_edt_phone);
		edt_sms = (EditText) findViewById(R.id.activity_create_edt_whatsapp);

		circle_spn = (TextView) findViewById(R.id.activity_classified_edit_circle);
		catagory_spn = (TextView) findViewById(R.id.activity_classified_edit_catagory);
		subcatagory_spn = (TextView) findViewById(R.id.activity_classified_edit_subcatagory);
		location_spn = (TextView) findViewById(R.id.activity_classified_edit_location);

		circle_spn.setOnClickListener(this);
		catagory_spn.setOnClickListener(this);
		subcatagory_spn.setOnClickListener(this);
		location_spn.setOnClickListener(this);

		((ImageView) findViewById(R.id.activity_createjob_attached)).setOnClickListener(this);
		((ImageView) findViewById(R.id.activity_createjob_docattached)).setOnClickListener(this);
		((ImageView) findViewById(R.id.activity_createjob_rply)).setOnClickListener(this);

		arrayView.add(findViewById(R.id.hlvCustomList));
		arrayView.add(findViewById(R.id.rel_reply));
		arrayView.add(findViewById(R.id.pdfattached_list));

		setClicEvent();
	}

	private void setClicEvent() {
		for (View rel : arrayView) {
			rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (anyviewVisible()) {
						resetViewAll();
					}
				}
			});
		}
	}

	private void loadForntView(int position) {
		for (int i = 0; i < arrayView.size(); i++) {
			if (i == position) {
				arrayView.get(i).setVisibility(View.VISIBLE);
			} else {
				arrayView.get(i).setVisibility(View.GONE);
			}
		}
	}

	private void resetViewAll() {
		for (View rel : arrayView) {
			rel.setVisibility(View.GONE);
		}
	}

	private boolean anyviewVisible() {
		boolean isvisible = false;
		for (View rel : arrayView) {
			isvisible = (rel.getVisibility() == View.VISIBLE) ? true : false;
			if (isvisible)
				break;
		}
		return isvisible;
	}

	private ArrayList<AttachmentDataSet> getpicArray(AttachmentDataSet mPicDataSet) {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		if (mPicDataSet == null) {
			mList.add(new AttachmentDataSet());
		}
		return mList;
	}

	private ArrayList<AttachmentDataSet> getDOCArray(AttachmentDataSet mPicDataSet) {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		if (mPicDataSet == null) {
			mList.add(new AttachmentDataSet());
		}
		return mList;
	}

	class AttachmentDataSet {
		String path = "";
	}

	private void loadData() {
		PrepareForClassifiedParser mPrepareForClassifiedParser = new PrepareForClassifiedParser();
		mPrepareForClassifiedParser.setPrepareparserinterface(new PrepareParserInterface() {

			@Override
			public void OnSuccess(PrepareListForClassified mPrepareListForClassified) {
				seekBarDataLoad(mPrepareListForClassified);
			}

			@Override
			public void OnError() {

			}
		});
		mPrepareForClassifiedParser.parse(context, mDetails.getAuthtoken());
	}

	private void seekBarDataLoad(PrepareListForClassified mPrepareListForClassified) {
		categoryList = mPrepareListForClassified.getPrimarycatlist();
		subCategoryList = (categoryList != null && categoryList.size() >= 1) ? categoryList.get(0).getList() : null;
		locationList = mPrepareListForClassified.getCitys();
		circleList = mPrepareListForClassified.getCirclelist();

	}

	private void galleryCalling() {
		Intent i = new Intent(context, ActivityGallery.class);
		startActivityForResult(i, GALLERY_REQUEST);
		overridePendingTransition(0, 0);
	}

	private void docpdfCalling() {
		Intent i = new Intent(context, ActivityChoosePDF.class);
		startActivityForResult(i, DOC_REQUEST);
		overridePendingTransition(0, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
			Bundle mbBundle = data.getExtras();
			String lpicturePath = mbBundle.getString("contents");
			if (lpicturePath != null) {
				AttachmentDataSet mPicDataSet = new AttachmentDataSet();
				mPicDataSet.path = lpicturePath;
				ArrayList<AttachmentDataSet> arrData = new ArrayList<AttachmentDataSet>();
				arrData.add(mPicDataSet);
				mCustomArrayAdapter.addImage(arrData);
			}
		}

		if (requestCode == DOC_REQUEST && resultCode == RESULT_OK && null != data) {

			Bundle mbBundle = data.getExtras();
			String lpicturePath = mbBundle.getString("contents");
			if (lpicturePath != null) {
				AttachmentDataSet mPicDataSet = new AttachmentDataSet();
				mPicDataSet.path = lpicturePath;
				ArrayList<AttachmentDataSet> arrData = new ArrayList<AttachmentDataSet>();
				arrData.add(mPicDataSet);
				mDOCPDFArrayAdapter.addImage(arrData);
			}
		}

	}

	public class ImageAttachmentArrayAdapter extends ArrayAdapter<AttachmentDataSet> {

		private LayoutInflater mInflater;
		private ArrayList<AttachmentDataSet> ImageAttachemtnS;

		public ImageAttachmentArrayAdapter(Context context, ArrayList<AttachmentDataSet> AttachmentDataSets_) {
			super(context, R.layout.inflate_createjob_pic, AttachmentDataSets_);
			this.ImageAttachemtnS = AttachmentDataSets_;
			mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return (ImageAttachemtnS != null) ? ImageAttachemtnS.size() : 0;
		}

		public void addImage(ArrayList<AttachmentDataSet> AttachmentDataSets_) {
			this.ImageAttachemtnS.addAll(AttachmentDataSets_);
			picdatasets = this.ImageAttachemtnS;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
				holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			AttachmentDataSet mDataSet = ImageAttachemtnS.get(position);
			String path = mDataSet.path;
			if (path.equals("")) {
				holder.pic.setImageResource(R.drawable.ic_plus);
				holder.pic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ImageAttachemtnS.size() < 5) {
							galleryCalling();
						} else {
							Util.ShowToast(context, "Exide max list.");
						}
					}
				});
			} else {
				imageLoader.displayImage("file://" + path, holder.pic, options);
			}

			return convertView;
		}

		/** View holder for the views we need access to */
		private class Holder {
			public ImageView pic;
		}
	}

	/**
	 * DOC PDF Attached List.
	 */
	public class DOCPDFArrayAdapter extends ArrayAdapter<AttachmentDataSet> {

		private LayoutInflater mInflater;
		private ArrayList<AttachmentDataSet> DOCPDFDataSets;

		public DOCPDFArrayAdapter(Context context, ArrayList<AttachmentDataSet> AttachmentDataSets_) {
			super(context, R.layout.inflate_createjob_pic, AttachmentDataSets_);
			this.DOCPDFDataSets = AttachmentDataSets_;
			mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return (DOCPDFDataSets != null) ? DOCPDFDataSets.size() : 0;
		}

		public void addImage(ArrayList<AttachmentDataSet> AttachmentDataSets_) {
			this.DOCPDFDataSets.addAll(AttachmentDataSets_);
			docpdfdatasets = this.DOCPDFDataSets;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
				holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			AttachmentDataSet mDataSet = DOCPDFDataSets.get(position);
			String path = mDataSet.path;
			if (path.equals("")) {
				holder.pic.setImageResource(R.drawable.ic_plus);
				holder.pic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (DOCPDFDataSets.size() < 4) {
							docpdfCalling();
						} else {
							Util.ShowToast(context, "Exide max list.");
						}

					}
				});
			} else {
				if (path.contains(".DOC") || path.contains(".doc")) {
					holder.pic.setImageResource(R.drawable.doc);
				} else {
					holder.pic.setImageResource(R.drawable.pdf);
				}

			}

			return convertView;
		}

		/** View holder for the views we need access to */
		private class Holder {
			public ImageView pic;
		}
	}

	private void createEvent() {
		String email_ = edt_email.getText().toString().trim();
		String ph_ = edt_phno.getText().toString().trim();
		String whats_ = edt_sms.getText().toString().trim();
		String headline = classifiedheadline.getText().toString();
		String headlinedetails = classifieddetails.getText().toString();

		if (headline.equals("")) {
			Util.ShowToast(context, "Please fill Job Headline.");
			return;
		}

		if (headline.length() > 500) {
			Util.ShowToast(context, "Classified Headline exit limit.");
			return;
		}

		if (headlinedetails.equals("")) {
			Util.ShowToast(context, "Please fill Classified Details.");
			return;
		}

		if (headlinedetails.length() > 65535) {
			Util.ShowToast(context, "Classified Details exit limit.");
			return;
		}

		if (!ph_.equals("") && ph_.length() < 10 && ph_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid Phone Number.");
			return;
		}

		if (!whats_.equals("") && whats_.length() < 10 && whats_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid SMS Number.");
			return;
		}

		/* CIRCLE */
		String circle_id = "";
		for (CustomSpinnerDataSets mCircle : circleList) {
			if (mCircle.isSelected == 1) {
				circle_id = circle_id + "," + mCircle.getId();
			}
		}

		if (circle_id.equals("")) {
			Util.ShowToast(context, "Please select to.");
			return;
		}
		circle_id = circle_id.substring(1);

		/* SECONDARY */
		String secondary_id = "";
		if (subCategoryList != null && subCategoryList.size() >= 1) {
			for (CustomSpinnerDataSets mSeccondary : subCategoryList) {
				if (mSeccondary.isSelected == 1) {
					secondary_id = secondary_id + "," + mSeccondary.getId();
				}
			}

			if (secondary_id.equals("")) {
				Util.ShowToast(context, "Please select Seccondary.");
				return;
			}
			secondary_id = secondary_id.substring(1);

		}

		/* LOCATION */
		String location_id = "";
		for (CustomSpinnerDataSets mLocation : locationList) {
			if (mLocation.isSelected == 1) {
				location_id = location_id + "," + mLocation.getId();
			}
		}

		if (location_id.equals("")) {
			Util.ShowToast(context, "Please select location.");
			return;
		}
		location_id = location_id.substring(1);

		JSONObject Obj = getBody(headline, headlinedetails, email_, ph_, whats_, circle_id + ",", secondary_id + ",", location_id + ",");

		ArrayList<String> paths = new ArrayList<String>();
		if (picdatasets != null && picdatasets.size() >= 1) {
			for (AttachmentDataSet pic : picdatasets) {
				if (!pic.path.equals("")) {
					paths.add(pic.path);
				}

			}
		}

		ArrayList<String> docpdfPaths = new ArrayList<String>();
		if (docpdfdatasets != null && docpdfdatasets.size() >= 1) {
			for (AttachmentDataSet docpdf : docpdfdatasets) {
				if (!docpdf.path.equals("")) {
					docpdfPaths.add(docpdf.path);
				}

			}
		}

		CreateClassifiedParser mClassifiedParser = new CreateClassifiedParser();
		mClassifiedParser.setCreateclassifiedinterface(new CreateClassifiedInterface() {

			@Override
			public void OnSuccess(JobDetails mJobDetails) {
				if (mJobDetails != null) {
					((OfCampusApplication) context.getApplicationContext()).jobdetails = mJobDetails;
					Intent mIntent = new Intent(context, ActivityClassifiedDetails.class);
					Bundle mBundle = new Bundle();
					mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[2]);
					mIntent.putExtras(mBundle);
					startActivity(mIntent);
					((Activity) context).overridePendingTransition(0, 0);
					finish();
				}
			}

			@Override
			public void OnError() {

			}
		});
		mClassifiedParser.parse(context, Obj, mDetails.getAuthtoken(), paths, docpdfPaths);

	}

	// {"subject":"test classified ","content":"test classified attached image and pdf","replyEmail":"dibakar@ofcampus.com",
	// "replyPhone":"","replyWatsApp":"9883755777","plateFormId":"0","appName":"ofCampus",
	// "shareDto":{"shareEmail":"-1","sharePhone":"-1","shareWatsApp":"-1"},"circleList":["24"],"secondaryCatList":["1"],"locationIdList":["2"]}

	private JSONObject getBody(String headline, String headlinedetails, String email_, String ph_, String whats_, String Circle_id, String secondary_id, String location_id) {

		JSONObject jsObj = new JSONObject();
		try {

			jsObj.put("subject", headline);
			jsObj.put("content", headlinedetails);

			jsObj.put("replyEmail", email_);
			jsObj.put("replyPhone", ph_);
			jsObj.put("replyWatsApp", whats_);

			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");

			JSONObject obj = new JSONObject();
			obj.put("shareEmail", "-1");
			obj.put("sharePhone", "-1");
			obj.put("shareWatsApp", "-1");
			jsObj.put("shareDto", obj);

			String[] circle = Circle_id.split(",");
			JSONArray circleArray = new JSONArray();
			for (int i = 0; i < circle.length; i++) {
				circleArray.put(i, circle[i]);
			}
			jsObj.put("circleList", circleArray);

			String[] secondary = secondary_id.split(",");
			JSONArray secondaryArray = new JSONArray();
			for (int i = 0; i < secondary.length; i++) {
				secondaryArray.put(i, secondary[i]);
			}
			jsObj.put("secondaryCatList", secondaryArray);

			String[] location = location_id.split(",");
			JSONArray locationArray = new JSONArray();
			for (int i = 0; i < location.length; i++) {
				locationArray.put(i, location[i]);
			}
			jsObj.put("locationIdList", locationArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

}
