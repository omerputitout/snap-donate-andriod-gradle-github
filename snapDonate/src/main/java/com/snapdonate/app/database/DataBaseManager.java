package com.snapdonate.app.database;

import android.content.Context;

import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.model.Faqs;

import java.util.ArrayList;

public class DataBaseManager {

	public static void makeDatabase(Context context) {
		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		databaseHelper.openDataBase();
		databaseHelper.close();
	}

	public static void saveDonation(Context context, Donation donation) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.saveDonationForLater(donation);
		dataBaseHelper.close();
	}

	public static void updateDataAsSycned(Context context, String autoID,
			String serverID) {

		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.updateDataAsSycned(autoID, serverID);
		dataBaseHelper.close();
	}

	public static void saveDonationForLater(Context context, Donation donation) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.saveDonationForLater(donation);
		dataBaseHelper.close();
	}

	public static ArrayList<Donation> getDonationsHistoryList(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<Donation> historyDonationArrayList = dataBaseHelper
				.getDonationsHistoryList();
		dataBaseHelper.close();
		return historyDonationArrayList;
	}

	public static ArrayList<Charity> getCharityList(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<Charity> allCharities = dataBaseHelper.getCharityList();
		dataBaseHelper.close();

		return allCharities;
	}

	public static void updateDonationAtTodoHistoryAndServer(Context context,
			Donation donation) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.updateDonationAtTodoHistoryAndServer(donation);
		dataBaseHelper.close();
	}

	public static ArrayList<Donation> getDonationsTodoList(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<Donation> todoDonationArrayList = dataBaseHelper
				.getDonationsTodoList();
		dataBaseHelper.close();

		return todoDonationArrayList;
	}

	public static int getDonationsTodoCount(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		int count = dataBaseHelper.getDonationsTodoCount();
		dataBaseHelper.close();

		return count;
	}

	public static void removeFromTODO(Context context, String savedServerID) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.removeFromTodo(savedServerID);
		dataBaseHelper.close();
	}

	public static ArrayList<Faqs> getFaqsQuestions(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<Faqs> dataArrayList = dataBaseHelper
				.getFaqsQuestions();
		dataBaseHelper.close();

		return dataArrayList;
	}
	
	public static ArrayList<Donation> getAllUnsycnedDonations(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<Donation> unSycnedDonationsArrayList = dataBaseHelper
				.getAllUnsycnedDonations();
		dataBaseHelper.close();
		return unSycnedDonationsArrayList;
	}
	
	public static ArrayList<String> getTodoToBeDeleted(Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		ArrayList<String> todoToBeDeletedIDs = dataBaseHelper
				.getTodoToBeDeleted();
		dataBaseHelper.close();
		
		return todoToBeDeletedIDs;
	}
	
	public static void removeTodoDonation(Context context , String autoID) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.removeTodoDonation(autoID);
		dataBaseHelper.close();
	}
	
	public static void addTodoTobeDeleted(Context context, String savedServerID) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		dataBaseHelper.openDataBase();
		dataBaseHelper.addTodoTobeDeleted(savedServerID);
		dataBaseHelper.close();
	}
}
