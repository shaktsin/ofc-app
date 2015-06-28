///*
// * This is the source code of OfCampus for Android v. 1.0.0.
// * You should have received a copy of the license in this archive (see LICENSE).
// * Copyright @Dibakar_Mistry, 2015.
// */
//package com.ofcampus.databasehelper;
//
//import android.content.Context;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.ofcampus.R;
//
//public class OfCampusDBHelper extends SQLiteOpenHelper {
//	private Context context_;
//	private static String DATABASE_NAME = "";
//	private static int DATABASE_VERSION = 0;
//	private String DB_PATH = "";
//	private SQLiteDatabase db;
//
//	public OfCampusDBHelper(Context context) {
//		super(context, context.getResources().getString(R.string.DataBaseName), null, Integer.parseInt(context.getResources().getString(R.string.DataBaseName_Version)));
//		this.context_ = context;
//		DATABASE_NAME = context.getResources().getString(R.string.DataBaseName);
//		DATABASE_VERSION = Integer.parseInt(context.getResources().getString(R.string.DataBaseName_Version));
//		DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
//		context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
//	}
//
//	public SQLiteDatabase getDB() {
//		return db;
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		try {
//			db.execSQL(sqlForCreateJobTAble());
//			db.execSQL(sqlForCreateImpJobTAble());
//			db.execSQL(sqlForCreateJobPostpath());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + JOBListTable.TABLENAME);
//			db.execSQL("DROP TABLE IF EXISTS " + ImportantJobTable.TABLENAME);
//			db.execSQL("DROP TABLE IF EXISTS " + JOBListTable.TABJOBSUBIMAGESPATH);
//			onCreate(db);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public synchronized void close() {
//		if (getDB() != null)
//			getDB().close();
//
//		super.close();
//	}
//
//	public void openDataBase() throws SQLException {
//		try {
//			db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private String sqlForCreateJobTAble() {
//		String sql = "CREATE TABLE " + JOBListTable.TABLENAME + " (" + JOBListTable.POSTID + " INTEGER PRIMARY KEY  NOT NULL ," + JOBListTable.SUBJECT + " TEXT NOT NULL," + JOBListTable.CONTENT
//				+ " TEXT NOT NULL," + JOBListTable.POSTEDON + " TEXT NOT NULL,"
//
//				+ JOBListTable.POSTUSERID + " TEXT NOT NULL," + JOBListTable.POSTUSERNAME + " TEXT NOT NULL," + JOBListTable.POSTUSERIMAGE + " TEXT NOT NULL," + JOBListTable.ISSYNCDATA
//				+ " TEXT NOT NULL," + JOBListTable.IMPORTANT + " TEXT NOT NULL,"
//
//				+ JOBListTable.POSTUSEREMAILID + " TEXT NOT NULL," + JOBListTable.POSTUSERPHNO + " TEXT NOT NULL," + JOBListTable.POSTUSERWHATSAPPNO + " TEXT NOT NULL);";
//		return sql;
//	}
//
//	private String sqlForCreateImpJobTAble() {
//		String sql = "CREATE TABLE " + ImportantJobTable.TABLENAME + " (" + JOBListTable.POSTID + " INTEGER PRIMARY KEY  NOT NULL ," + JOBListTable.SUBJECT + " TEXT NOT NULL," + JOBListTable.CONTENT
//				+ " TEXT NOT NULL," + JOBListTable.POSTEDON + " TEXT NOT NULL,"
//
//				+ JOBListTable.POSTUSERID + " TEXT NOT NULL," + JOBListTable.POSTUSERNAME + " TEXT NOT NULL," + JOBListTable.POSTUSERIMAGE + " TEXT NOT NULL," + JOBListTable.ISSYNCDATA
//				+ " TEXT NOT NULL," + JOBListTable.IMPORTANT + " TEXT NOT NULL,"
//
//				+ JOBListTable.POSTUSEREMAILID + " TEXT NOT NULL," + JOBListTable.POSTUSERPHNO + " TEXT NOT NULL," + JOBListTable.POSTUSERWHATSAPPNO + " TEXT NOT NULL);";
//		return sql;
//	}
//
//	private String sqlForCreateJobPostpath() {
//		String sql = "CREATE TABLE " + JOBListTable.TABJOBSUBIMAGESPATH + " (" + JOBListTable.POSTID + " INTEGER PRIMARY KEY  NOT NULL ," + JOBListTable.POSTIMAGESID + " INTEGER NOT NULL ,"
//				+ JOBListTable.POSTIMAGESPATH + " TEXT NOT NULL);";
//		return sql;
//	}
//}
