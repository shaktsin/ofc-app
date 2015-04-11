package com.ofcampus.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import com.ofcampus.model.DocumentPath;

public class PdfDocLoader {

	private Context mContext;
	private View v;

	public void load(Context mContext_, String url_, View v_) {
		this.mContext = mContext_;
		this.v = v_;
		Async mAsync = new Async(url_);
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
		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			if (loadlistner!=null) {
				loadlistner.OnProgress(Integer.parseInt(progress[0]));
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (loadlistner != null) {
				if (isSuccess) {
					loadlistner.OnComplete(v);
				} else {
					loadlistner.OnCancel(v);
				}
			} else {
				loadlistner.OnErroe(v);
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
		
		public void OnProgress(int value); 

		public void OnCancel(View v);
	}
}
