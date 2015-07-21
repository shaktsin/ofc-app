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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.Util.ClassifSpinnerType;
import com.ofcampus.component.HorizontalListView;
import com.ofcampus.model.CustomSpinnerDataSets;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.PrepareListForNewsAndJob;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.EditNewsParser;
import com.ofcampus.parser.EditNewsParser.EditNewsParserInterface;
import com.ofcampus.parser.NewsPostParser;
import com.ofcampus.parser.NewsPostParser.NewsPostParserInterface;
import com.ofcampus.parser.PrepareForCreatingJobParser;
import com.ofcampus.parser.PrepareForCreatingJobParser.PrepareParserInterface;
import com.ofcampus.ui.CustomSpinner;
import com.ofcampus.ui.CustomSpinner.changeListner;

public class ActivityCreateNews extends ActionBarActivity implements OnClickListener, changeListner {

	public static int GALLERY_REQUEST = 1;
	public static int DOC_REQUEST = 2;
	private Context context;
	private UserDetails mDetails;
	private EditText postheadline, postdetails, edt_email, edt_phno, edt_whatsapp;
	private TextView edt_sendto;
	private ArrayList<CustomSpinnerDataSets> circleList;

	private ArrayList<View> arrayView = new ArrayList<View>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private HorizontalListView mHlvCustomList;
	private ImageAttachmentAdapter mCustomArrayAdapter;
	private ArrayList<AttachmentDataSet> picdatasets = new ArrayList<AttachmentDataSet>();
	private ArrayList<AttachmentDataSet> docpdfdatasets = new ArrayList<AttachmentDataSet>();

	private HorizontalListView pdfattached_list;
	private DOCPDFArrayAdapter mDOCPDFArrayAdapter;

	private int createFor = 0;// Create News=0 , Edit News =1;
	private CustomSpinner mCustomSpinner = null;
	private View parenScroll = null;
	private ArrayList<String> deletedIDS = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_news);

		context = ActivityCreateNews.this;
		Bundle mBundle = getIntent().getExtras();
		String title = mBundle.getString("ToolBarTitle");
		createFor = mBundle.getInt("createFor");

		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		loadData();

		getWindow().getDecorView().postDelayed(new Runnable() {

			@Override
			public void run() {
				int displayH = parenScroll.getHeight();
				RelativeLayout.LayoutParams pram = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, displayH);
				(findViewById(R.id.news_rel_main)).setLayoutParams(pram);
				parenScroll.setVisibility(View.VISIBLE);
			}
		}, 200);
		if (createFor == 1) {
			loadJobData();
		}
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
				if (createFor == 0) {
					createNewsEvent();
				} else {
					editNewsEvent();
				}
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_news_edit_circle:
			if (circleList != null && circleList.size() >= 1) {
				mCustomSpinner = new CustomSpinner(context, v, circleList, ClassifSpinnerType.MULTISELECTION);
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
		case R.id.activity_news_edit_circle:
			if (circleList != null && circleList.size() >= 1) {
				String txt = "";
				for (CustomSpinnerDataSets mCircle : circleList) {
					if (mCircle.isSelected == 1) {
						txt = txt + "," + mCircle.getTitle();
					}
				}
				txt = (txt.length() >= 1) ? txt.substring(1) : "";
				edt_sendto.setText(txt);
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

		parenScroll = findViewById(R.id.news_parent);
		parenScroll.setVisibility(View.INVISIBLE);
		mHlvCustomList = (HorizontalListView) findViewById(R.id.hlvCustomList);
		mCustomArrayAdapter = new ImageAttachmentAdapter(context, getpicArray(null));
		mHlvCustomList.setAdapter(mCustomArrayAdapter);

		pdfattached_list = (HorizontalListView) findViewById(R.id.pdfattached_list);
		mDOCPDFArrayAdapter = new DOCPDFArrayAdapter(context, getDOCArray(null));
		pdfattached_list.setAdapter(mDOCPDFArrayAdapter);

		edt_sendto = (TextView) findViewById(R.id.activity_news_edit_circle);
		edt_sendto.setOnClickListener(this);

		postheadline = (EditText) findViewById(R.id.activity_news_edit_jobtitle);
		postdetails = (EditText) findViewById(R.id.activity_news_edit_jobdescrip);

		edt_email = (EditText) findViewById(R.id.activity_create_edt_emailreply);
		edt_phno = (EditText) findViewById(R.id.activity_create_edt_phone);
		edt_whatsapp = (EditText) findViewById(R.id.activity_create_edt_whatsapp);

		((ImageView) findViewById(R.id.activity_createjob_attached)).setOnClickListener(this);
		((ImageView) findViewById(R.id.activity_createjob_rply)).setOnClickListener(this);
		((ImageView) findViewById(R.id.activity_createjob_docattached)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rel_rplview_main)).setOnClickListener(this);

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

	/*** Post Job Details **/
	private JobDetails mJobDetails;
	private String JObID = "";
	private String[] circleIDs = null;

	private void loadJobData() {
		mJobDetails = ((OfCampusApplication) getApplication()).jobdetails;
		JObID = mJobDetails.getPostid();

		postheadline.setText(mJobDetails.getSubject());
		postdetails.setText(mJobDetails.getContent());

		edt_email.setText(mJobDetails.getReplyEmail());
		edt_phno.setText(mJobDetails.getReplyPhone());
		edt_whatsapp.setText(mJobDetails.getReplyWatsApp());

		circleIDs = mJobDetails.getSelectedCircle();

		mCustomArrayAdapter.RefreshImage(getJobPicArray());
		mDOCPDFArrayAdapter.RefreshDoc(getDocArray());

	}

	private void loadData() {
		mDetails = UserDetails.getLoggedInUser(context);

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}
		edt_email.setText(mDetails.getEmail());

		PrepareForCreatingJobParser mCreating = new PrepareForCreatingJobParser();
		mCreating.setPrepareparserinterface(new PrepareParserInterface() {

			@Override
			public void OnSuccess(PrepareListForNewsAndJob mPrepareListForNewsAndJob) {
				if (mPrepareListForNewsAndJob != null) {
					loadDropeDownListData(mPrepareListForNewsAndJob);
				}
			}

			@Override
			public void OnError() {
				onBackPressed();
			}
		});
		mCreating.parse(context, mDetails.getAuthtoken());

	}

	private void loadDropeDownListData(PrepareListForNewsAndJob mPrepareListForNewsAndJob) {

		String eml = mPrepareListForNewsAndJob.getReplyEmail();
		edt_email.setText((eml != null && !eml.equals("null") && !eml.equals("")) ? eml : "");
		String ph = mPrepareListForNewsAndJob.getReplyPhone();
		edt_phno.setText((ph != null && !ph.equals("null") && !ph.equals("")) ? ph : "");
		String wthp = mPrepareListForNewsAndJob.getReplyWatsApp();
		edt_whatsapp.setText((wthp != null && !wthp.equals("null") && !wthp.equals("")) ? wthp : "");

		circleList = mPrepareListForNewsAndJob.getCirclelist();
		if (circleList != null && circleIDs != null && circleIDs.length >= 1) {
			for (CustomSpinnerDataSets item : circleList) {
				if (item.getId().equals(circleIDs[0])) {
					item.isSelected = 1;
					edt_sendto.setText(item.getTitle());
				}
			}
		}
	}

	private ArrayList<AttachmentDataSet> getJobPicArray() {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		ArrayList<ImageDetails> images = mJobDetails.getImages();

		mList.add(new AttachmentDataSet());

		if (images != null && images.size() >= 1) {
			for (ImageDetails mImageDetails : images) {
				AttachmentDataSet mPicDataSet = new AttachmentDataSet();
				mPicDataSet.ID = "" + mImageDetails.getImageID();
				mPicDataSet.path = mImageDetails.getImageURL();
				mList.add(mPicDataSet);
			}
		}
		return mList;
	}

	private ArrayList<AttachmentDataSet> getDocArray() {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		ArrayList<DocDetails> Docs = mJobDetails.getDoclist();

		mList.add(new AttachmentDataSet());

		if (Docs != null && Docs.size() >= 1) {
			for (DocDetails mDocDetails : Docs) {
				AttachmentDataSet mAttachmentDataSet = new AttachmentDataSet();
				mAttachmentDataSet.ID = "" + mDocDetails.getDocID();
				mAttachmentDataSet.path = mDocDetails.getDocURL();
				mList.add(mAttachmentDataSet);
			}
		}
		return mList;
	}

	class AttachmentDataSet {
		String ID = "";
		String path = "";
	}

	/**
	 * An array adapter that knows how to render views when given CustomData
	 * classes
	 */

	private ArrayList<AttachmentDataSet> getpicArray(AttachmentDataSet mAttachmentDataSet) {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		if (mAttachmentDataSet == null) {
			mList.add(new AttachmentDataSet());
		}
		return mList;
	}

	private ArrayList<AttachmentDataSet> getDOCArray(AttachmentDataSet mAttachmentDataSet) {
		ArrayList<AttachmentDataSet> mList = new ArrayList<AttachmentDataSet>();
		if (mAttachmentDataSet == null) {
			mList.add(new AttachmentDataSet());
		}
		return mList;
	}

	public class ImageAttachmentAdapter extends ArrayAdapter<AttachmentDataSet> {

		private LayoutInflater mInflater;
		private ArrayList<AttachmentDataSet> PicDataSets;

		public ImageAttachmentAdapter(Context context, ArrayList<AttachmentDataSet> PicDataSets_) {
			super(context, R.layout.inflate_createjob_pic, PicDataSets_);
			this.PicDataSets = PicDataSets_;
			mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return PicDataSets.size();
		}

		public void addImage(ArrayList<AttachmentDataSet> picdatasets_) {
			this.PicDataSets.addAll(picdatasets_);
			picdatasets = this.PicDataSets;
			notifyDataSetChanged();
		}

		public void RefreshImage(ArrayList<AttachmentDataSet> picdatasets_) {
			this.PicDataSets = picdatasets_;
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
				holder = new Holder();
				holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
				holder.pic_cross = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi_cross);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			AttachmentDataSet mDataSet = PicDataSets.get(position);
			String path = mDataSet.path;
			final String ID = mDataSet.ID;
			holder.pic_cross.setVisibility(View.GONE);
			if (path.equals("")) {
				holder.pic.setImageResource(R.drawable.ic_plus);
				holder.pic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (PicDataSets.size() < 5) {
							galleryCalling();
						} else {
							Util.ShowToast(context, "Exide max list.");
						}

					}
				});
			} else if (path.contains("http://") || path.contains("https://")) {
				imageLoader.displayImage(path, holder.pic, options);
				holder.pic_cross.setVisibility(View.VISIBLE);
				holder.pic_cross.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						PicDataSets.remove(position);
						if (createFor == 1) {// for Edit Post
							deletedIDS.add(ID);
						}
						notifyDataSetChanged();
					}
				});
			} else {
				imageLoader.displayImage("file://" + path, holder.pic, options);
				holder.pic_cross.setVisibility(View.VISIBLE);
				holder.pic_cross.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						PicDataSets.remove(position);
						notifyDataSetChanged();
					}
				});
			}

			return convertView;
		}

		/** View holder for the views we need access to */
		private class Holder {
			public ImageView pic, pic_cross;
		}
	}

	/**
	 * DOC PDF Attached List.
	 */
	public class DOCPDFArrayAdapter extends ArrayAdapter<AttachmentDataSet> {

		private LayoutInflater mInflater;
		private ArrayList<AttachmentDataSet> DOCPDFDataSets;

		public DOCPDFArrayAdapter(Context context, ArrayList<AttachmentDataSet> PicDataSets_) {
			super(context, R.layout.inflate_createjob_pic, PicDataSets_);
			this.DOCPDFDataSets = PicDataSets_;
			mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void RefreshDoc(ArrayList<AttachmentDataSet> docArray) {
			this.DOCPDFDataSets = docArray;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return (DOCPDFDataSets != null) ? DOCPDFDataSets.size() : 0;
		}

		public void addDoc(ArrayList<AttachmentDataSet> picdatasets_) {
			this.DOCPDFDataSets.addAll(picdatasets_);
			docpdfdatasets = this.DOCPDFDataSets;
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.inflate_createjob_pic, parent, false);
				holder.pic = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi);
				holder.pic_cross = (ImageView) convertView.findViewById(R.id.infalte_createjob_pi_cross);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			AttachmentDataSet mDataSet = DOCPDFDataSets.get(position);
			String path = mDataSet.path;
			final String ID = mDataSet.ID;
			holder.pic_cross.setVisibility(View.GONE);

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
			} else if (path.contains("http://") || path.contains("https://")) {
				if (path.contains(".DOC") || path.contains(".doc")) {
					holder.pic.setImageResource(R.drawable.doc);
				} else {
					holder.pic.setImageResource(R.drawable.pdf);
				}
				holder.pic_cross.setVisibility(View.VISIBLE);
				holder.pic_cross.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DOCPDFDataSets.remove(position);
						if (createFor == 1) { // for Edit Post
							deletedIDS.add(ID);
						}
						notifyDataSetChanged();
					}
				});
			} else {
				if (path.contains(".DOC") || path.contains(".doc")) {
					holder.pic.setImageResource(R.drawable.doc);
				} else {
					holder.pic.setImageResource(R.drawable.pdf);
				}
				holder.pic_cross.setVisibility(View.VISIBLE);
				holder.pic_cross.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DOCPDFDataSets.remove(position);
						notifyDataSetChanged();
					}
				});
			}

			return convertView;
		}

		/** View holder for the views we need access to */
		private class Holder {
			public ImageView pic, pic_cross;
		}
	}

	// class PicDataSet {
	// String path = "";
	// }

	private void galleryCalling() {
		Intent i = new Intent(context, ActivityGallery.class);
		startActivityForResult(i, GALLERY_REQUEST);
		overridePendingTransition(0, 0);
	}

	private void docpdfCalling() {
		Intent i = new Intent(ActivityCreateNews.this, ActivityChoosePDF.class);
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
				mDOCPDFArrayAdapter.addDoc(arrData);
			}
		}

	}

	/**
	 * Posting A News.
	 */
	private void createNewsEvent() {

		String email_ = edt_email.getText().toString().trim();
		String ph_ = edt_phno.getText().toString().trim();
		String whats_ = edt_whatsapp.getText().toString().trim();
		String headline = postheadline.getText().toString();
		String headlinedetails = postdetails.getText().toString();

		/* CIRCLE */
		String circle_id = "";
		if (circleList != null && circleList.size() >= 1) {
			for (CustomSpinnerDataSets mCircle : circleList) {
				if (mCircle.isSelected == 1) {
					circle_id = circle_id + "," + mCircle.getId();
				}
			}

			if (circle_id.equals("")) {
				Util.ShowToast(context, "Please select send to.");
				return;
			}
			circle_id = circle_id.substring(1);
		} else {
			Util.ShowToast(context, "Please select send to.");
			return;
		}

		if (headline.equals("")) {
			Util.ShowToast(context, "Please fill Job Headline.");
			return;
		}

		if (headline.length() > 500) {
			Util.ShowToast(context, "Job Headline exit limit.");
			return;
		}

		if (headlinedetails.equals("")) {
			Util.ShowToast(context, "Please fill Job Details.");
			return;
		}

		if (headlinedetails.length() > 65535) {
			Util.ShowToast(context, "Job Details exit limit.");
			return;
		}

		if (!ph_.equals("") && ph_.length() < 10 && ph_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid Phone Number.");
			return;
		}

		if (!whats_.equals("") && whats_.length() < 10 && whats_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid WhatsApp Number.");
			return;
		}

		JSONObject newsObj = getBody(headline, headlinedetails, email_, ph_, whats_, circle_id + ",");

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

		if (newsObj != null) {
			NewsPostParser mPostParser = new NewsPostParser();
			mPostParser.setNewspostparserinterface(new NewsPostParserInterface() {

				@Override
				public void OnSuccess(JobDetails mJobDetails) {
					if (mJobDetails != null) {
						((OfCampusApplication) context.getApplicationContext()).jobdetails = mJobDetails;
						Intent mIntent = new Intent(context, ActivityNewsDetails.class);
						Bundle mBundle = new Bundle();
						mBundle.putString(Util.BUNDLE_KEY[0], Util.TOOLTITLE[1]);
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
			mPostParser.parse(context, newsObj, mDetails.getAuthtoken(), paths, docpdfPaths);
		}

	}

	private JSONObject getBody(String headline, String headlinedetails, String email_, String ph_, String whats_, String Circle_id) {

		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("replyEmail", email_);
			jsObj.put("replyPhone", ph_);
			jsObj.put("replyWatsApp", whats_);

			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");

			jsObj.put("subject", headline);
			jsObj.put("content", headlinedetails);

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

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	/* Edit News Event */
	private void editNewsEvent() {

		String email_ = edt_email.getText().toString().trim();
		String ph_ = edt_phno.getText().toString().trim();
		String whats_ = edt_whatsapp.getText().toString().trim();
		String headline = postheadline.getText().toString();
		String headlinedetails = postdetails.getText().toString();

		/* CIRCLE */
		String circle_id = "";
		if (circleList != null && circleList.size() >= 1) {
			for (CustomSpinnerDataSets mCircle : circleList) {
				if (mCircle.isSelected == 1) {
					circle_id = circle_id + "," + mCircle.getId();
				}
			}

			if (circle_id.equals("")) {
				Util.ShowToast(context, "Please select send to.");
				return;
			}
			circle_id = circle_id.substring(1);
		} else {
			Util.ShowToast(context, "Please select send to.");
			return;
		}

		if (headline.equals("")) {
			Util.ShowToast(context, "Please fill Job Headline.");
			return;
		}

		if (headline.length() > 500) {
			Util.ShowToast(context, "Job Headline exit limit.");
			return;
		}

		if (headlinedetails.equals("")) {
			Util.ShowToast(context, "Please fill Job Details.");
			return;
		}

		if (headlinedetails.length() > 65535) {
			Util.ShowToast(context, "Job Details exit limit.");
			return;
		}

		if (!ph_.equals("") && ph_.length() < 10 && ph_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid Phone Number.");
			return;
		}

		if (!whats_.equals("") && whats_.length() < 10 && whats_.length() > 13) {
			Util.ShowToast(context, "Please enter a valid WhatsApp Number.");
			return;
		}

		JSONObject newsObj = getBodyNewsEdit(headline, headlinedetails, email_, ph_, whats_, circle_id + ",");

		ArrayList<String> paths = new ArrayList<String>();
		if (picdatasets != null && picdatasets.size() >= 1) {
			for (AttachmentDataSet pic : picdatasets) {
				if (!pic.path.equals("") && (!pic.path.contains("http://") || !pic.path.contains("https://")) && pic.ID.equals("")) {
					paths.add(pic.path);
				}

			}
		}

		ArrayList<String> docpdfPaths = new ArrayList<String>();
		if (docpdfdatasets != null && docpdfdatasets.size() >= 1) {
			for (AttachmentDataSet docpdf : docpdfdatasets) {
				if (!docpdf.path.equals("") && (!docpdf.path.contains("http://") || !docpdf.path.contains("https://")) && docpdf.ID.equals("")) {
					docpdfPaths.add(docpdf.path);
				}
			}
		}

		if (newsObj != null) {
			EditNewsParser mEditNewsParser = new EditNewsParser();
			mEditNewsParser.setEditnewsparserinterface(new EditNewsParserInterface() {

				@Override
				public void OnSuccess(JobDetails mJobDetails) {
					if (mJobDetails != null) {
						((OfCampusApplication) context.getApplicationContext()).jobdetails = mJobDetails;
						((OfCampusApplication) context.getApplicationContext()).isNewsDataModify = true;
						((Activity) context).overridePendingTransition(0, 0);
						finish();
					}
				}

				@Override
				public void OnError() {

				}
			});
			mEditNewsParser.parse(context, newsObj, mDetails.getAuthtoken(), paths, docpdfPaths);
		}

	}

	private JSONObject getBodyNewsEdit(String headline, String headlinedetails, String email_, String ph_, String whats_, String Circle_id) {

		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("postId", JObID);
			jsObj.put("replyEmail", email_);
			jsObj.put("replyPhone", ph_);
			jsObj.put("replyWatsApp", whats_);

			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");

			jsObj.put("subject", headline);
			jsObj.put("content", headlinedetails);

			JSONObject obj = new JSONObject();
			obj.put("shareEmail", "-1");
			obj.put("sharePhone", "-1");
			obj.put("shareWatsApp", "-1");
			jsObj.put("shareDto", obj);

			if (deletedIDS != null && deletedIDS.size() >= 1) {
				JSONArray deletedAttachmentArray = new JSONArray();
				for (int i = 0; i < deletedIDS.size(); i++) {
					deletedAttachmentArray.put(i, deletedIDS.get(i));
				}
				jsObj.put("deletedAttachment", deletedAttachmentArray);
			}

			String[] circle = Circle_id.split(",");
			JSONArray circleArray = new JSONArray();
			for (int i = 0; i < circle.length; i++) {
				circleArray.put(i, circle[i]);
			}
			jsObj.put("circleList", circleArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
}
