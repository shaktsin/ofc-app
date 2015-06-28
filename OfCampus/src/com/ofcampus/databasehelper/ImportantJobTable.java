///*
// * This is the source code of OfCampus for Android v. 1.0.0.
// * You should have received a copy of the license in this archive (see LICENSE).
// * Copyright @Dibakar_Mistry, 2015.
// */
//package com.ofcampus.databasehelper;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;
//import android.util.Log;
//
//import com.ofcampus.OfCampusApplication;
//import com.ofcampus.model.JobDetails;
//
//public class ImportantJobTable {
//
//	public static String TABLENAME = "importantjoblist";
//
//	public static String POSTID = "postId";
//	public static String SUBJECT = "subject";
//	public static String CONTENT = "content";
//	public static String POSTEDON = "postedOn";
//	public static String POSTUSERID = "id";
//	public static String POSTUSERNAME = "name";
//	public static String POSTUSERIMAGE = "image";
//	public static String ISSYNCDATA = "issyncdata";
//
//	public static String POSTUSEREMAILID = "replyemail";
//	public static String POSTUSERPHNO = "replyphone";
//	public static String POSTUSERWHATSAPPNO = "replywatsapp";
//
//	private static OfCampusDBHelper dbHelper = null;
//	private static ImportantJobTable mInstance;
//	private SQLiteDatabase sampleDB;
//
//	public static synchronized ImportantJobTable getInstance(Context context) {
//		if (mInstance == null) {
//			mInstance = new ImportantJobTable(context);
//		}
//		return mInstance;
//	}
//
//	public ImportantJobTable(Context context) {
//		if (dbHelper == null) {
//			dbHelper = ((OfCampusApplication) context.getApplicationContext()).DB_HELPER;
//		}
//	}
//
//	public void inserJobData(JobDetails Job) {
//
//		try {
//			sampleDB = dbHelper.getDB();
//			sampleDB.beginTransaction();
//			String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?,?,?,?,?,?,?);";
//			SQLiteStatement insert = sampleDB.compileStatement(sql);
//			if (Job != null) {
//				insert.clearBindings();
//				insert.bindLong(1, Integer.parseInt(Job.getPostid()));
//				insert.bindString(2, Job.getSubject());
//				insert.bindString(3, Job.getContent());
//				insert.bindString(4, Job.getPostedon());
//				insert.bindString(5, Job.getId());
//				insert.bindString(6, Job.getName());
//				insert.bindString(7, Job.getImage());
//				insert.bindString(8, Job.getISSyncData());
//				insert.bindLong(9, Job.getImportant());
//
//				insert.bindString(10, Job.getReplyEmail());
//				insert.bindString(11, Job.getReplyPhone());
//				insert.bindString(12, Job.getReplyWatsApp());
//
//				insert.execute();
//				sampleDB.setTransactionSuccessful();
//				Log.e("TAG", "Done");
//			}
//
//		} catch (Exception e) {
//			Log.e("XML:", e.toString());
//		} finally {
//			sampleDB.endTransaction();
//		}
//	}
//
//	public ArrayList<JobDetails> fatchImpJobData() {
//		ArrayList<JobDetails> jobs = null;
//		String sql = "select * from " + TABLENAME + " order by " + POSTID + " desc";
//		Cursor mCursor = null;
//		try {
//			mCursor = dbHelper.getDB().rawQuery(sql, null);
//			if (mCursor != null && mCursor.getCount() >= 1) {
//				jobs = GetImpJobData(mCursor);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		curcorClose(mCursor);
//		return jobs;
//	}
//
//	private ArrayList<JobDetails> GetImpJobData(Cursor mCursor) {
//		ArrayList<JobDetails> jobs = new ArrayList<JobDetails>();
//		if (mCursor.moveToFirst()) {
//			do {
//				JobDetails mDetails = new JobDetails();
//				mDetails.setPostid("" + mCursor.getInt(mCursor.getColumnIndex(POSTID)));
//				mDetails.setSubject(mCursor.getString(mCursor.getColumnIndex(SUBJECT)));
//				mDetails.setContent(mCursor.getString(mCursor.getColumnIndex(CONTENT)));
//				mDetails.setPostedon(mCursor.getString(mCursor.getColumnIndex(POSTEDON)));
//				mDetails.setId(mCursor.getString(mCursor.getColumnIndex(POSTID)));
//				mDetails.setName(mCursor.getString(mCursor.getColumnIndex(POSTUSERNAME)));
//				mDetails.setImage(mCursor.getString(mCursor.getColumnIndex(POSTUSERIMAGE)));
//				mDetails.setISSyncData(mCursor.getString(mCursor.getColumnIndex(ISSYNCDATA)));
//
//				mDetails.setReplyEmail(mCursor.getString(mCursor.getColumnIndex(POSTUSEREMAILID)));
//				mDetails.setReplyPhone(mCursor.getString(mCursor.getColumnIndex(POSTUSERPHNO)));
//				mDetails.setReplyWatsApp(mCursor.getString(mCursor.getColumnIndex(POSTUSERWHATSAPPNO)));
//
//				// mDetails.setImages(fatchJobImagePathData(Integer.parseInt(mDetails.getPostid())));
//				jobs.add(mDetails);
//			} while (mCursor.moveToNext());
//		}
//		return jobs;
//	}
//
//	public boolean deleteUnimpJOb(JobDetails mJobDetails) {
//		long success = -1;
//		try {
//			success = dbHelper.getDB().delete(TABLENAME, POSTID + "=?", new String[] { "" + mJobDetails.getPostid() });
//			if (success > 0) {
//				return true;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	private void curcorClose(Cursor cursor) {
//		try {
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
