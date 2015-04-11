package com.ofcampus.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.component.ProgressBarDeterminate;
import com.ofcampus.model.DocumentPath;

public class PdfDocLoader {

	private Context mContext;
	private View v;
	private Async mAsync;

	public void load(Context mContext_, String url_, View v_) {
		this.mContext = mContext_;
		this.v = v_;
		mAsync = new Async(url_);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mAsync.execute();
		}
	}

	private class Async extends AsyncTask<Void, String, Void> {

		private String url;
		private boolean isSuccess = false;

		public Async(String url_) {
			url = url_;
		}

		@Override
		protected Void doInBackground(Void... params) {
			int count;
			try {
				URL URL_ = new URL(url);
				URLConnection conection = URL_.openConnection();
				conection.connect();
				int lenghtOfFile = conection.getContentLength();
				InputStream input = new BufferedInputStream(URL_.openStream(),8192);

				String path = getFilename(url);
				OutputStream output = new FileOutputStream(path);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
				isSuccess = true;
				DocumentPath mDocumentPath = DocumentPath.getPath(mContext);
				if (mDocumentPath == null) {
					mDocumentPath = new DocumentPath();
					mDocumentPath.savePath(mContext);
				}
				mDocumentPath.mapPath.put(url, path);
				mDocumentPath.savePath(mContext);

			} catch (FileNotFoundException e) {
				isSuccess = false;
			} catch (IOException e) {
				isSuccess = false;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(final String... progress) {
			super.onProgressUpdate(progress);
			try {
				int pgValue = Integer.parseInt(progress[0]);
				pg.setProgress(pgValue);
				txt_parcentage.setText(pgValue + "%");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog = null;
				OnCompleteCall();
			}
		}
	}

	public String getFilename(String url) {
		File file = new File(Environment.getExternalStorageDirectory().getPath(), "OfCampus/Document");
		String[] spltURL = url.split("/");
		String fileName = spltURL[spltURL.length - 1];
		if (!file.exists()) {
			file.mkdirs();
		}
		return (file.getAbsolutePath() + "/" + fileName);
	}

	
	
	private Dialog mDialog = null;
	private ProgressBarDeterminate pg = null;
	private TextView txt_parcentage;

	public void downloadDialog(Context mContext_, String name , String url_, View v_) {
		mDialog = new Dialog(mContext_,R.style.Theme_Dialog_Translucent);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mDialog.setContentView(R.layout.inflate_downloaddialog);
		TextView txt_name = (TextView) mDialog.findViewById(R.id.txt_attachementName);
		
		txt_name.setText(name);
		txt_parcentage = (TextView) mDialog.findViewById(R.id.txt_parsentage);
		pg = (ProgressBarDeterminate) mDialog.findViewById(R.id.progressDeterminate);

		mDialog.setCancelable(true);
		mDialog.show();

		load(mContext_, url_, v_);
		mDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (mAsync != null) {
					mAsync.cancel(true);
					OnCancelCall();
				}
			}
		});
		mDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mAsync != null) {
					mAsync.cancel(true); 
					OnCancelCall();
				}

			}
		});
	}
	
	private void OnCancelCall(){
		if (loadlistner!=null) {
			loadlistner.OnCancel(v);
		}
	}
	
	private void OnCompleteCall(){
		if (loadlistner!=null) {
			loadlistner.OnCancel(v);
		}
	}
	
	private void OnErroeCall(){
		if (loadlistner!=null) {
			loadlistner.OnCancel(v);
		}
	}
	
	
	
	
	public LoadListner loadlistner;

	public LoadListner getLoadlistner() {
		return loadlistner;
	}

	public void setLoadlistner(LoadListner loadlistner) {
		this.loadlistner = loadlistner;
	}

	public interface LoadListner {
		public void OnComplete(View v);

		public void OnErroe(View v);

		public void OnCancel(View v);
	}
}
