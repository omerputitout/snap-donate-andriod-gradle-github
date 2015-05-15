package com.snapdonate.app.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.model.Faqs;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = SUtils.DATABASE_NAME;
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = SUtils.DATABASEHELPER_TAG;
	private final Context mContext;
	private String DATABASE_PATH = "";
	private SQLiteDatabase mDataBaseSQLiteDatabase;

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);// TODO: 1? its Database
												// Version
		this.mContext = context;
		DATABASE_PATH = getDbPath();
	}

	private String getDbPath() {
		String fullPath = "";
		if (SUtils.IS_TESTING) {
			fullPath = Environment.getExternalStorageDirectory()
					+ File.separator + DATABASE_NAME;
		} else {
			fullPath = mContext.getFilesDir().toString() + File.separator
					+ DATABASE_NAME;
		}
		return fullPath;
	}

	public void createDataBase() throws IOException {
		// TODO: If database not exists copy it from the assets
		boolean doesdataBaseExist = checkDataBase();
		if (!doesdataBaseExist) {
			this.getReadableDatabase();
			this.close();
			try {
				// TODO: Copy the database from assests
				copyDataBase();
				Log.e(TAG, "createDatabase database created");
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	// TODO: Check that the database exists here: /data/data/your
	// package/databases/Database Name
	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			checkDb = SQLiteDatabase.openDatabase(DATABASE_PATH, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			Log.e(this.getClass().toString(), "Error while checking db");
		}
		// TODO: Android doesnÃ•t like resource leaks, everything should be
		// closed
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	// TODO: Copy the database from assets
	private void copyDataBase() throws IOException {
		InputStream inputStream = mContext.getAssets().open(DATABASE_NAME);
		String outFileName = DATABASE_PATH;
		OutputStream outputStream = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}

	// TODO: Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String dbPath = DATABASE_PATH;
		if (mDataBaseSQLiteDatabase == null) {
			try {
				createDataBase();
			} catch (IOException e) {

				e.printStackTrace();
			}
			mDataBaseSQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		return mDataBaseSQLiteDatabase != null;
	}

	@Override
	public synchronized void close() {
		if (mDataBaseSQLiteDatabase != null)
			mDataBaseSQLiteDatabase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase SQLiteDatabase) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	}

	// TODO: Local Functions For Data Management Plus Data Retreiving
	public ArrayList<Charity> getCharityList() {
		String sql = "SELECT * FROM tbl_charity ORDER BY charity_name COLLATE NOCASE ASC";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql, null);
		ArrayList<Charity> charityArrayList = new ArrayList<Charity>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				Charity charity = new Charity();
				charity.setId(cursor.getString(cursor.getColumnIndex("charity_Id")));
				charity.setName(cursor.getString(cursor.getColumnIndex("charity_name")));
				charityArrayList.add(charity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return charityArrayList;
	}

	public Charity getCharityDataByName(String name) {
		String sql = "SELECT * FROM tbl_charity WHERE charity_name = ?";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { name });
		Charity charity = null;
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				charity = new Charity();
				charity.setId(cursor.getString(cursor
						.getColumnIndex("charity_Id")));
				charity.setName(cursor.getString(cursor
						.getColumnIndex("charity_name")));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return charity;
	}

	public void saveDonationForLater(Donation donation) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("charity_Id", donation.getCharity().getId());
		contentValues.put("charity_name", donation.getCharity().getName());
		contentValues.put("vuforia_recognized_logo_name", donation.getCharity()
				.getVuforiaRecognizedName());
		contentValues.put("charity_amount", donation.getAmount());
		contentValues.put("charity_medium", donation.getCharity().getMedium());
		contentValues.put("charity_isTodoOrHistory", donation.getSatus());
		contentValues.put("charity_date", donation.getTimeStamp());
		contentValues.put("isSynced", donation.getIsSynced());
		contentValues.put("saved_server_id", donation.getSavedServerId());

		long row = mDataBaseSQLiteDatabase
				.insert("tbl_donations", null, contentValues);
		SLog.showLog("saveDonationForLater : " + row);
		if (donation.getSatus().equals(SUtils.CHARITY_IS_TODO)) {
			Intent intent = new Intent(
					SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA);
			mContext.sendBroadcast(intent);
		}
	}

	public void addCharityToFavouite(String charityID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("charity_Id", charityID);

		long row = mDataBaseSQLiteDatabase
				.insert("tbl_favourite", null, contentValues);
		SLog.showLog("addCharityToFavouite : " + row);
		Intent intent = new Intent(
				SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA);
		mContext.sendBroadcast(intent);
	}

	public boolean checkCharityExistInFavouite(String charityID) {
		String sql = "SELECT * FROM tbl_favourite WHERE charity_Id = ?";

		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { charityID });

		ArrayList<Charity> dataArrayList = new ArrayList<Charity>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				Charity charity = new Charity();
				charity.setId(cursor.getString(cursor
						.getColumnIndex("charity_Id")));
				dataArrayList.add(charity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;

		if (dataArrayList.size() > 0) {
			return true;
		}
		return false;
	}

	public void updateDonationAtTodoHistoryAndServer(Donation donation) {
		String currentDate = String.valueOf(java.lang.System.currentTimeMillis());
		ContentValues contentValues = new ContentValues();
		contentValues.put("charity_isTodoOrHistory", SUtils.CHARITY_IS_HISTORY);
		contentValues.put("charity_date", currentDate);
		contentValues.put("isSynced", SUtils.IS_SYNCED);
		contentValues.put("charity_donation_reference_id",
				donation.getDonationReferenceId());

		int row = mDataBaseSQLiteDatabase.update("tbl_donations", contentValues,
				"saved_server_id = ?",
				new String[] { donation.getSavedServerId() });
		SLog.showLog("updateDonationAtTodoHistoryAndServer : " + row);
		Intent intent = new Intent(
				SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA);
		mContext.sendBroadcast(intent);
	}

	public int getDonationsTodoCount() {
		String sql = "SELECT * FROM tbl_donations WHERE charity_isTodoOrHistory = ?";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { SUtils.CHARITY_IS_TODO });
		return cursor.getCount();
	}

	public ArrayList<Donation> getDonationsTodoList() {
		String sql = "SELECT * FROM tbl_donations WHERE charity_isTodoOrHistory = ?";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { SUtils.CHARITY_IS_TODO });

		ArrayList<Donation> donationArrayList = new ArrayList<Donation>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				donationArrayList.add(getDonationModelFromCursor(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return donationArrayList;
	}

	public void removeTodoDonation(String autoID) {
		long row = mDataBaseSQLiteDatabase.delete("tbl_donations", "auto_Id = "
				+ autoID, null);
		SLog.showLog("removeTodoDonation : " + row);
		Intent intent = new Intent(
				SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA);
		mContext.sendBroadcast(intent);
	}

	public ArrayList<Donation> getDonationsHistoryList() {
		String sql = "SELECT * FROM tbl_donations WHERE charity_isTodoOrHistory = ?";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { SUtils.CHARITY_IS_HISTORY });
		ArrayList<Donation> donationArrayList = new ArrayList<Donation>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				donationArrayList.add(getDonationModelFromCursor(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return donationArrayList;
	}

	public ArrayList<Donation> getAllUnsycnedDonations() {
		String sql = "SELECT * FROM tbl_donations WHERE isSynced = ?";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql,
				new String[] { SUtils.IS_NOT_SYNCED });

		ArrayList<Donation> donationArrayList = new ArrayList<Donation>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				donationArrayList.add(getDonationModelFromCursor(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return donationArrayList;
	}

	public void updateDataAsSycned(String autoID, String serverID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("isSynced", SUtils.IS_SYNCED);
		contentValues.put("saved_server_id", serverID);

		int row = mDataBaseSQLiteDatabase.update("tbl_donations", contentValues,
				"auto_Id = ?", new String[] { autoID });
		SLog.showLog("updateDataAsSycned : " + row);
	}

	public ArrayList<Charity> getFavouriteCharitys() {
		String sql = "SELECT t1.charity_Id , t2.charity_name , "
				+ "t2.charity_Id FROM tbl_favourite t1 INNER JOIN tbl_charity t2 ON t1.charity_Id = t2.charity_Id";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql, null);
		ArrayList<Charity> charityArrayList = new ArrayList<Charity>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				Charity charity = new Charity();
				charity.setId(cursor.getString(cursor
						.getColumnIndex("charity_Id")));
				charity.setName(cursor.getString(cursor
						.getColumnIndex("charity_name")));
				charityArrayList.add(charity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return charityArrayList;
	}

	public ArrayList<Faqs> getFaqsQuestions() {
		String sql = "SELECT * FROM tbl_faqs";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql, null);

		ArrayList<Faqs> faqsArrayList = new ArrayList<Faqs>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				Faqs faq = new Faqs();
				faq.setAutoId(cursor.getString(cursor.getColumnIndex("auto_Id")));
				faq.setQuestion(cursor.getString(cursor
						.getColumnIndex("question")));
				faq.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
				faqsArrayList.add(faq);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return faqsArrayList;
	}

	public void addTodoTobeDeleted(String savedServerID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("saved_server_Id", savedServerID);

		long row = mDataBaseSQLiteDatabase.insert("tbl_deleteTodoFromServer",
				null, contentValues);
		SLog.showLog("addTodoTobeDeleted : " + row);
	}

	public ArrayList<String> getTodoToBeDeleted() {
		String sql = "SELECT * FROM tbl_deleteTodoFromServer";
		Cursor cursor = mDataBaseSQLiteDatabase.rawQuery(sql, null);

		ArrayList<String> savedServerIdArrayList = new ArrayList<String>();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			do {
				savedServerIdArrayList.add(cursor.getString(cursor
						.getColumnIndex("saved_server_Id")));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return savedServerIdArrayList;
	}

	public void removeFromTodo(String savedServerID) {
		long row = mDataBaseSQLiteDatabase.delete("tbl_deleteTodoFromServer",
				"saved_server_Id = " + savedServerID, null);
		SLog.showLog("removeFromTodo : " + row);
	}

	private Donation getDonationModelFromCursor(Cursor cursor) {
		Donation donation = new Donation();
		donation.setAutoId(cursor.getString(cursor.getColumnIndex("auto_Id")));
		donation.setAmount(cursor.getString(cursor
				.getColumnIndex("charity_amount")));
		donation.setTimeStamp(cursor.getString(cursor.getColumnIndex("charity_date")));
		donation.setIsSynced(cursor.getString(cursor.getColumnIndex("isSynced")));
		donation.setStatus(cursor.getString(cursor
				.getColumnIndex("charity_isTodoOrHistory")));
		donation.setDonationReferenceId(cursor.getString(cursor
				.getColumnIndex("charity_donation_reference_id")));
		donation.setSavedServerId(cursor.getString(cursor
				.getColumnIndex("saved_server_id")));
		Charity charity = new Charity();
		charity.setId(cursor.getString(cursor.getColumnIndex("charity_Id")));
		charity.setName(cursor.getString(cursor.getColumnIndex("charity_name")));
		charity.setVuforiaRecognizedName(cursor.getString(cursor
				.getColumnIndex("vuforia_recognized_logo_name")));
		charity.setMedium(cursor.getString(cursor
				.getColumnIndex("charity_medium")));
		donation.setCharity(charity);
		return donation;
	}
}
