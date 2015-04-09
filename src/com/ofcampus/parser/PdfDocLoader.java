package com.ofcampus.parser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.ofcampus.component.CircularCounter;
import com.ofcampus.model.DocumentPath;

public class PdfDocLoader {

	private Context mContext;
	private View v;

	public void load(Context mContext_, String url_, ProgressBar pg_, View v_) {
		this.mContext = mContext_;
		this.v = v_;
		Async mAsync = new Async(url_, pg_);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mAsync.execute();
		}
	}

	private class Async extends AsyncTask<Void, Integer, Void> {

		private String url;
		private ProgressBar pg;
		private boolean isSuccess = false;
		private int index = 0;

		public Async(String url_, ProgressBar pg_) {
			url = url_;
			pg = pg_;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL u = new URL(url);
				URLConnection conn = u.openConnection();
				int contentLength = conn.getContentLength();

				DataInputStream stream = new DataInputStream(u.openStream());

				byte[] buffer = new byte[contentLength];
				stream.readFully(buffer);
				stream.close();

				String path = getFilename(url);
				DataOutputStream fos = new DataOutputStream(
						new FileOutputStream(path));
				fos.write(buffer);
				fos.flush();
				fos.close();
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
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pg.setProgress((int) values[index]);
			index++;
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
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath(), "OfCampus/Document");
		if (!file.exists()) {
			file.mkdirs();
		}
		String uriSting = "";
		if (url.contains(".doc")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".doc");
		} else if (url.contains(".DOC")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".DOC");
		}
		if (url.contains(".docx")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".docx");
		} else if (url.contains(".DOCX")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".DOCX");
		}
		if (url.contains(".pdf")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".pdf");
		} else if (url.contains(".PDF")) {
			uriSting = (file.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".PDF");
		}

		return uriSting;

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
