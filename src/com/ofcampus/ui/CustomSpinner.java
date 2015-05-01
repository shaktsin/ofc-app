package com.ofcampus.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util.ClassifSpinnerType;
import com.ofcampus.model.CustomSpinnerDataSets;

public class CustomSpinner {

	private Context context;
	private View mView;
	private ArrayList<CustomSpinnerDataSets> listSpinnData;
	private LayoutInflater layoutInflater;
	private int defaultWindowSize = 300;
	private PopupWindow pwindow;
	private Rect rect;
	private View layout;
	private ClassifSpinnerType mClassifSpinnerType;
	private int CurrentHitPostion = -1;

	public CustomSpinner(Context context_, View mView_, ArrayList<CustomSpinnerDataSets> listSpinnData_, ClassifSpinnerType mClassifSpinnerType_) {
		this.context = context_;
		this.mView = mView_;
		this.listSpinnData = listSpinnData_;
		this.mClassifSpinnerType = mClassifSpinnerType_;
		rect = locateView(mView_);

		if (mClassifSpinnerType_ == mClassifSpinnerType_.SINGLESELECTION) {
			for (int i = 0; i < listSpinnData.size(); i++) {
				CurrentHitPostion = (listSpinnData.get(i).isSelected == 1) ? i : -1;
				if (CurrentHitPostion != -1) {
					break;
				}
			}
		}

		popupWindow();
	}

	public void popupWindow() {

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = layoutInflater.inflate(R.layout.inflate_customspinner, null);
		// Creating the PopupWindow
		pwindow = new PopupWindow(context);
		pwindow.setContentView(layout);
		pwindow.setHeight(defaultWindowSize);
		pwindow.setFocusable(true);
		ListView listview = (ListView) pwindow.getContentView().findViewById(R.id.dropDownList);
		listview.setAdapter(new SpinnerBaseAdapter());
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pwindow.dismiss();
			}
		});

		pwindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

			}

		});

		Drawable mDrawable = context.getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult);
		pwindow.setWidth(context.getResources().getDisplayMetrics().widthPixels - rect.left - 10);
		pwindow.setBackgroundDrawable(mDrawable);

	}

	public void show() {
		pwindow.setAnimationStyle(android.R.style.TextAppearance_Widget_DropDownItem);
		pwindow.showAtLocation(layout, Gravity.NO_GRAVITY, rect.left, rect.bottom);
	}

	public Rect locateView(View v) {
		int[] loc_int = new int[2];
		if (v == null)
			return null;
		try {
			v.getLocationOnScreen(loc_int);
		} catch (NullPointerException npe) {
			return null;
		}
		Rect location = new Rect();
		location.left = loc_int[0];
		location.top = loc_int[1];

		int Hight = context.getResources().getDisplayMetrics().heightPixels;
		int view_H = v.getHeight();
		int view_W = v.getWidth();

		location.right = location.left + view_W;

		if ((location.top + defaultWindowSize + view_H) > Hight) {
			location.bottom = location.top - defaultWindowSize + OfCampusApplication.dp(10.0f);
		} else {
			location.bottom = location.top + view_H - OfCampusApplication.dp(10.0f);
		}
		return location;
	}

	private class SpinnerBaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return (listSpinnData == null) ? 0 : listSpinnData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.inflate_customspinner_dropitem, null);
				mHolder.txt_title = (TextView) convertView.findViewById(R.id.custm_spinner_title);
				mHolder.checkbox = (ImageView) convertView.findViewById(R.id.custm_spinner_checkbox);

				convertView.setTag(mHolder);

			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			CustomSpinnerDataSets mCustomSpinnerDataSets = listSpinnData.get(position);

			mHolder.txt_title.setText(mCustomSpinnerDataSets.getTitle());
			mHolder.checkbox.setSelected((mCustomSpinnerDataSets.getIsSelected() == 1) ? true : false);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mClassifSpinnerType == ClassifSpinnerType.MULTISELECTION) {
						listSpinnData.get(position).isSelected = (listSpinnData.get(position).getIsSelected() == 1) ? 0 : 1;
					} else {
						listSpinnData.get(position).isSelected = (listSpinnData.get(position).getIsSelected() == 1) ? 0 : 1;
						if (CurrentHitPostion != -1) {
							listSpinnData.get(CurrentHitPostion).isSelected = (listSpinnData.get(CurrentHitPostion).getIsSelected() == 1) ? 0 : 1;
							for (CustomSpinnerDataSets customSpinnerDataSets : listSpinnData.get(CurrentHitPostion).getList()) {
								customSpinnerDataSets.isSelected = 0;
							}
						}
						CurrentHitPostion = position;

					}
					notifyDataSetChanged();
					if (changelistner != null) {
						changelistner.onItemChange(mView);
					}
				}
			});

			return convertView;
		}

	}

	class ViewHolder {
		TextView txt_title;
		ImageView checkbox;
	}

	public changeListner changelistner;

	public changeListner getChangelistner() {
		return changelistner;
	}

	public void setChangelistner(changeListner changelistner) {
		this.changelistner = changelistner;
	}

	public interface changeListner {
		public void onItemChange(View mView);
	}

}
