package com.ofcampus.databasehelper;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.Util.JobDataReturnFor;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;

public class JOBListTable {

	public static String TABLENAME= "joblist";
	public static String TABJOBSUBIMAGESPATH= "postimagepath";
	
	public static String POSTID="postId";
	public static String SUBJECT="subject";
	public static String CONTENT="content";
	public static String POSTEDON="postedOn";
	public static String POSTUSERID="id";
	public static String POSTUSERNAME="name";
	public static String POSTUSERIMAGE="image";
	public static String ISSYNCDATA="issyncdata";
	public static String IMPORTANT="important";
	
	public static String POSTUSEREMAILID="replyemail";
	public static String POSTUSERPHNO="replyphone";
	public static String POSTUSERWHATSAPPNO="replywatsapp";
	
	public static String POSTIMAGESID="id";
	public static String POSTIMAGESPATH="path";
	
	 private static OfCampusDBHelper dbHelper = null;
	 private static JOBListTable mInstance;
	 private SQLiteDatabase sampleDB;
	 
	public static synchronized JOBListTable getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new JOBListTable(context);
		}
		return mInstance;
	}

	public JOBListTable(Context context) {
		if (dbHelper == null) {
			dbHelper = ((OfCampusApplication) context.getApplicationContext()).DB_HELPER;
		}
	}
	
	

	
	public void inserJobData(ArrayList<JobDetails> Jobs, int size) { 

		try {
			sampleDB = dbHelper.getDB();
			sampleDB.beginTransaction();
			String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?,?,?,?,?,?,?);";
			SQLiteStatement insert = sampleDB.compileStatement(sql);

			if (Jobs.size() > size) {
				for (int i = 0; i < size; i++) {
					JobDetails mJob = Jobs.get(i);
					insert.clearBindings();
					insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
					insert.bindString(2, mJob.getSubject());
					insert.bindString(3, mJob.getContent());
					insert.bindString(4, mJob.getPostedon());
					insert.bindString(5, mJob.getId());
					insert.bindString(6, mJob.getName());
					insert.bindString(7, mJob.getImage());
					insert.bindString(8, mJob.getISSyncData());
					insert.bindLong(9, mJob.getImportant());
					
					insert.bindString(10, mJob.getReplyEmail());
					insert.bindString(11, mJob.getReplyPhone());
					insert.bindString(12, mJob.getReplyWatsApp());
					
					insert.execute();
					inserJobImagePathData(mJob.getImages(), Integer.parseInt(mJob.getPostid()));
				}
				sampleDB.setTransactionSuccessful();
			}else {
				for (int i = 0; i < Jobs.size(); i++) {
					JobDetails mJob = Jobs.get(i);
					insert.clearBindings();
					insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
					insert.bindString(2, mJob.getSubject());
					insert.bindString(3, mJob.getContent());
					insert.bindString(4, mJob.getPostedon());
					insert.bindString(5, mJob.getId());
					insert.bindString(6, mJob.getName());
					insert.bindString(7, mJob.getImage());
					insert.bindString(8, mJob.getISSyncData());
					insert.bindLong(9, mJob.getImportant());
					
					insert.bindString(10, mJob.getReplyEmail());
					insert.bindString(11, mJob.getReplyPhone());
					insert.bindString(12, mJob.getReplyWatsApp());
					
					insert.execute();
					inserJobImagePathData(mJob.getImages(), Integer.parseInt(mJob.getPostid()));
				}
				sampleDB.setTransactionSuccessful();
			}
			Log.e("TAG", "Done");

		} catch (Exception e) {
			Log.e("XML:", e.toString());
		} finally {
			sampleDB.endTransaction();
		}
	}
	
	
	
	public void inserJobImagePathData(ArrayList<ImageDetails> pathArray, int jobpostID) { 

		try {
			String sql = "Insert or Replace into " + TABJOBSUBIMAGESPATH + " values(?,?,?);";
			SQLiteStatement insert = sampleDB.compileStatement(sql);

			if (pathArray!=null && pathArray.size()>=1) {
				for (int i = 0; i < pathArray.size(); i++) {
					insert.clearBindings();
					insert.bindLong(1, jobpostID);
					insert.bindLong(2, pathArray.get(i).getImageID());
					insert.bindString(3, pathArray.get(i).getImageURL());
					insert.execute();
				}
			}
			Log.e("TAG", "Done");

		} catch (Exception e) {
			Log.e("XML:", e.toString());
		} finally {
		}
	}
	
	public boolean deleteAllImagesRelatedtoJobID(String postID){
		boolean success = false;
		String sql = "";
		try {
			sql = "delete from "+TABJOBSUBIMAGESPATH+" where "+POSTID+"='"+postID+"'";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
			sql = "update joblist set issyncdata='0' where issyncdata='1'";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success; 
	}
	
	public void inserJobData(ArrayList<JobDetails> Jobs) {

		try {
			sampleDB = dbHelper.getDB();
			sampleDB.beginTransaction();
			String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?,?,?,?,?,?,?);";
			SQLiteStatement insert = sampleDB.compileStatement(sql);

			for (int i = 0; i < Jobs.size(); i++) {
				JobDetails mJob = Jobs.get(i);
				insert.clearBindings();
				insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
				insert.bindString(2, mJob.getSubject());
				insert.bindString(3, mJob.getContent());
				insert.bindString(4, mJob.getPostedon());
				insert.bindString(5, mJob.getId());
				insert.bindString(6, mJob.getName());
				insert.bindString(7, mJob.getImage());
				insert.bindString(8, mJob.getISSyncData());
				insert.bindLong(9, mJob.getImportant());
				
				insert.bindString(10, mJob.getReplyEmail());
				insert.bindString(11, mJob.getReplyPhone());
				insert.bindString(12, mJob.getReplyWatsApp());
				
				insert.execute();
				inserJobImagePathData(mJob.getImages(), Integer.parseInt(mJob.getPostid()));
			}
			sampleDB.setTransactionSuccessful();
			Log.e("TAG", "Done");

		} catch (Exception e) {
			Log.e("XML:", e.toString());
		} finally {
			sampleDB.endTransaction();
		}
	}
	
	public ArrayList<JobDetails> fatchJobData(JobDataReturnFor mJobDataReturnFor) {
		ArrayList<JobDetails> jobs = null;
		String sql = "";
		if (mJobDataReturnFor==JobDataReturnFor.syncdata) {
			sql = "select * from joblist where issyncdata='1' order by postid desc";
		}else{
			sql = "select * from joblist where issyncdata!='1' order by postid desc";
		}
		Cursor mCursor=null;
		try {
			mCursor = dbHelper.getDB().rawQuery(sql, null);
			if (mCursor != null && mCursor.getCount() >= 1) {
				jobs = GetJobData(mCursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		curcorClose(mCursor);
		return jobs;
	}
	
	public ArrayList<ImageDetails> fatchJobImagePathData(int postID) {
		ArrayList<ImageDetails> paths = null;
		String sql = "";
			sql = "select * from "+TABJOBSUBIMAGESPATH+" where "+POSTID+"='"+postID+"'";
		Cursor mCursor=null;
		try {
			mCursor = dbHelper.getDB().rawQuery(sql, null);
			if (mCursor != null && mCursor.getCount() >= 1) {
				paths=new ArrayList<ImageDetails>();
				if (mCursor.moveToFirst()) {
					do {	
						ImageDetails mImageDetails=new ImageDetails();
						mImageDetails.setImageID(mCursor.getInt(mCursor.getColumnIndex(POSTIMAGESID)));
						mImageDetails.setImageURL(mCursor.getString(mCursor.getColumnIndex(POSTIMAGESPATH)));
						paths.add(mImageDetails);
					} while (mCursor.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		curcorClose(mCursor);
		return paths;
	}
	
	
	private ArrayList<JobDetails> GetJobData(Cursor mCursor){
		ArrayList<JobDetails> jobs=new ArrayList<JobDetails>();
		if (mCursor.moveToFirst()) {
			do {
				JobDetails mDetails = new JobDetails();
				mDetails.setPostid(""+mCursor.getInt(mCursor.getColumnIndex(POSTID)));
				mDetails.setSubject(mCursor.getString(mCursor.getColumnIndex(SUBJECT)));
				mDetails.setContent(mCursor.getString(mCursor.getColumnIndex(CONTENT)));
				mDetails.setPostedon(mCursor.getString(mCursor.getColumnIndex(POSTEDON)));
				mDetails.setId(mCursor.getString(mCursor.getColumnIndex(POSTID)));
				mDetails.setName(mCursor.getString(mCursor.getColumnIndex(POSTUSERNAME)));
				mDetails.setImage(mCursor.getString(mCursor.getColumnIndex(POSTUSERIMAGE)));
				mDetails.setISSyncData(mCursor.getString(mCursor.getColumnIndex(ISSYNCDATA)));
				mDetails.setImportant(mCursor.getInt(mCursor.getColumnIndex(IMPORTANT)));
				
				mDetails.setReplyEmail(mCursor.getString(mCursor.getColumnIndex(POSTUSEREMAILID)));
				mDetails.setReplyPhone(mCursor.getString(mCursor.getColumnIndex(POSTUSERPHNO)));
				mDetails.setReplyWatsApp(mCursor.getString(mCursor.getColumnIndex(POSTUSERWHATSAPPNO)));
				mDetails.setImages(fatchJobImagePathData(Integer.parseInt(mDetails.getPostid())));
				
				jobs.add(mDetails);
			} while (mCursor.moveToNext());
		}
		return jobs;
	}
	
	public boolean deleteoutDatedPost(int count) {
		boolean success = false;
		String sql = "";
		try {
			sql = "delete from joblist where (postId < (((select min(postId) from joblist) +'"+count+"')))";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
			sql = "update joblist set issyncdata='0' where issyncdata='1'";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	public boolean deleteSpamJOb(JobDetails mJobDetails) {
		long success = -1;
		try {
			success = dbHelper.getDB().delete(TABLENAME, POSTID+"=?", new String[]{""+mJobDetails.getPostid()});
			if (success > 0) {
				return true;
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void curcorClose(Cursor cursor){
		try {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/****Query List*****/
//	delete from joblist where postId < (((select min(postId) from joblist) +2))
//	select * from joblist where issyncdata like 1
//	select * from joblist where issyncdata not like 1
	
}
