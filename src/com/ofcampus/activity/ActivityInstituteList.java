package com.ofcampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.Util.userType;
import com.ofcampus.gplushelper.GPlusDialog;
import com.ofcampus.gplushelper.GPlusDialog.GPlusDialogListner;
import com.ofcampus.gplushelper.GPlusUser;
import com.ofcampus.model.InstituteDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.RegistrationParser;
import com.ofcampus.parser.RegistrationParser.RegstrationInterface;

public class ActivityInstituteList extends Activity implements OnClickListener{

	
	private Context context;
	private ArrayList<InstituteDetails> institutes;
	private ListView instituteList;
	private RelativeLayout rel_login,rel_list;
	private TextView btn_GPllogin;
	private GPlusUser mUser_;
	private String InstituteID="";
	private boolean isAlreadyClick=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_institutelist);
		
		context=ActivityInstituteList.this;
		initilize();
		loadData();
	}
	
	@Override
	public void onBackPressed() {
		if (rel_login.getVisibility()==View.VISIBLE) {
			LoginViaGplusDesable();
		}else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn_GPllogin:
			Util.HideKeyBoard(context, v);
			GPlusLoginEvent();
			break;
		case R.id.institute_img_back:
			if (isAlreadyClick) {
				return;
			}
			isAlreadyClick=true;
			LoginViaGplusDesable();
			break;
			
		default:
			break;
		}
	}

	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		instituteList=(ListView)findViewById(R.id.institute_list);
		rel_list=(RelativeLayout)findViewById(R.id.institute_rel);
		rel_login=(RelativeLayout)findViewById(R.id.institute_rel_loginbtn);
		((ImageView) findViewById(R.id.institute_img_back)).setOnClickListener(this);
		btn_GPllogin=(TextView) findViewById(R.id.login_btn_GPllogin);
		btn_GPllogin.setOnClickListener(this);
	}
	
	private void loadData() {
		institutes=((OfCampusApplication)getApplication()).institutes_;
		if (institutes!=null && institutes.size()>=1) {
			instituteList.setAdapter(new InstituteAdapter(institutes));
		}
	}
	
	private void GPlusLoginEvent(){
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		GPlusDialog mGPlusDialog  = new GPlusDialog(ActivityInstituteList.this);
		mGPlusDialog.setGplusdialoglistner(new GPlusDialogListner() {
			
			@Override
			public void OnSuccess(GPlusUser mUser) {
				if (mUser!=null) {
					mUser_=mUser;
					RegistrationCalling();
				}
			}
			
			@Override
			public void OnProfileLoadError() {
				GPlusDialog.ShowAlert(ActivityInstituteList.this, "On profile load error.");
			}
			
			@Override
			public void OnPageLoadError() {
				GPlusDialog.ShowAlert(ActivityInstituteList.this, "On Page Load error.");
			}
		});
		mGPlusDialog.show();
	}
	
	private void RegistrationCalling(){

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		String firstName=mUser_.getGiven_name();
		String lastName=mUser_.getFamily_name();
		String accountName=mUser_.getName();
		String email=mUser_.getEmail();
		String password="";
		String instituteId=InstituteID;
		String gender=Util.Gender(mUser_.getGender());
		String verified=mUser_.getVerified_email();
		String thirdPartAuth="true";
		
		RegistrationParser mParser=new RegistrationParser();
		mParser.setRegstrationinterface(new RegstrationInterface() {
			
			@Override
			public void OnSuccess(UserDetails mDetails) {
				if (mDetails!=null) {
					mDetails.saveInPreferense(context);
					Util.ShowToast(context, "Successfully registered");
				}
				moveToHome();
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mParser.parse(context, firstName, lastName, accountName, email, password, instituteId, gender, verified,thirdPartAuth,userType.Gmail);
		
	}
	
	private void moveToHome(){
		startActivity(new Intent(ActivityInstituteList.this,ActivityHome.class));
		overridePendingTransition(0, 0);
		finish();
		
	}
	
	/**
	 * Institute List
	 * @author DIBAKAR
	 *
	 */
	private class InstituteAdapter extends BaseAdapter{

		private ArrayList<InstituteDetails> institutes;
		private LayoutInflater mInflater;
		
		
		public InstituteAdapter(ArrayList<InstituteDetails> institutes_) {
			this.institutes=institutes_;
			this.mInflater=LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return institutes.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder mViewHolder;
			if (convertView==null) {
				mViewHolder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.inflate_institutelist_row, null);
				mViewHolder.txtname=(TextView)convertView.findViewById(R.id.inflt_inst_txtname);
				convertView.setTag(mViewHolder);
			}else {
				mViewHolder=(ViewHolder) convertView.getTag();
			}
			final InstituteDetails mInstituteDetails =institutes.get(position);
			
			if (mInstituteDetails!=null) {
				mViewHolder.txtname.setText(mInstituteDetails.getNm());
			}
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String thType=mInstituteDetails.getThpartyauth();
					String Provider=mInstituteDetails.getProvider();
					if (thType.equals("true") && Provider.equals("1")) {
						LoginViaGplusEnable(mInstituteDetails.getId());
					}else {
						((OfCampusApplication)getApplication()).instituteid=mInstituteDetails.getId();
						moveToLoginScreen();
					}
				}
			});
			return convertView;
		}
	}
	
	private class ViewHolder{
		TextView txtname;
	}
	private void moveToLoginScreen(){
		startActivity(new Intent(ActivityInstituteList.this,ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}
	
	
	private void LoginViaGplusEnable(String ID){  
		InstituteID = ID;
		
		Animation anim=AnimationUtils.loadAnimation(context, R.anim.slide_lefthome);
		rel_list.clearAnimation();
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				rel_login.setVisibility(View.VISIBLE);
				rel_list.setVisibility(View.GONE);
			}
		});
		rel_list.startAnimation(anim);
	}
	private void LoginViaGplusDesable(){  
		InstituteID = "";
		Animation anim=AnimationUtils.loadAnimation(context, R.anim.slide_right);
		rel_login.clearAnimation();
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				rel_login.setVisibility(View.GONE);
				rel_list.setVisibility(View.VISIBLE);
				isAlreadyClick=false;
			}
		});
		rel_login.startAnimation(anim);
	}
	
	
}
