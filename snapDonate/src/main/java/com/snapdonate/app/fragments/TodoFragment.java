package com.snapdonate.app.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.snapdonate.app.R;
import com.snapdonate.app.adapter.TodoAdapter;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.services.RemoveDonationsDataOnServerService;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;

import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;

public class TodoFragment extends Fragment implements OnClickListener {
	private static final String LOGOS = "Logos";
	private static final String PNG = ".png";
	private static final String ID = "id";
	private ArrayList<Donation> mTodoDonationArrayList = new ArrayList<Donation>();
	private TodoAdapter mTodoAdapter;
	private SwipeDismissList mSwipeDismissList;
	private TextView mTodoTextView;
	private TextView mHistoryTextView;
	private LinearLayout mListLoaderRelativeLayout;
	private TextView mShortDescriptionTextView;
	private ListView mListview;

	public TodoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.todo, container, false);
		init(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initializeSwipeListView();
		setListAdapter();
		sendFlurryStats();
	}

	private void init(View view) {
		mTodoTextView = (TextView) view.findViewById(R.id.todoTextView);
		mHistoryTextView = (TextView) view.findViewById(R.id.historyTextView);
		mListLoaderRelativeLayout = (LinearLayout) view
				.findViewById(R.id.progressBarLinearLayout);
		mShortDescriptionTextView = (TextView) view
				.findViewById(R.id.shortDescriptionTextView);
		mListview = (ListView) view.findViewById(R.id.todoListView);
		mTodoTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mHistoryTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mShortDescriptionTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mHistoryTextView.setOnClickListener(this);
	}

	private void sendFlurryStats() {
		// TODO: send flurry stat
		Calendar calender = Calendar.getInstance();
		int hours = calender.get(Calendar.HOUR);
		int minutes = calender.get(Calendar.MINUTE);
		int date = calender.get(Calendar.DATE);
		int month = calender.get(Calendar.MONTH);
		int year = calender.get(Calendar.YEAR);
		int totalDonations = 0;
		for (int index = 0; index < mTodoDonationArrayList.size(); index++) {
			totalDonations = totalDonations
					+ Integer.parseInt(mTodoDonationArrayList.get(index)
							.getAmount());
		}
		HashMap<String, String> statsHashMap = new HashMap<String, String>();
		statsHashMap.put(SUtils.DONATION_AMOUNT_TAG, String.valueOf(totalDonations));
		statsHashMap.put(SUtils.TIME_TAG, hours + ":" + minutes);
		statsHashMap.put(SUtils.DATE_TAG, date + "/" + (month + 1) + "/"
				+ (year));

		FlurryAgent.logEvent(SUtils.TODO_EVENT_NAME_FLURRY, statsHashMap);
	}

	public void initializeSwipeListView() {
		int modeInt = 0;
		mSwipeDismissList = new SwipeDismissList(mListview,
				new SwipeDismissList.OnDismissCallback() {
					public SwipeDismissList.Undoable onDismiss(
							AbsListView listView, final int position) {
						deleteDonationFromDatabase(position);
						return null;
					}
				}, SwipeDismissList.UndoMode.values()[modeInt]);
		mSwipeDismissList.setSwipeDirection(SwipeDirection.END);
	}

	private void setListAdapter() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListLoaderRelativeLayout.setVisibility(View.VISIBLE);
					}
				});
				getTodoListData();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListLoaderRelativeLayout.setVisibility(View.GONE);
						if (mTodoDonationArrayList.size() > 0) {
							mShortDescriptionTextView
									.setText(getString(R.string.todo_description_text));
							mTodoAdapter = new TodoAdapter(getActivity(),
									mTodoDonationArrayList);
							mListview.setAdapter(mTodoAdapter);
						} else {
							mShortDescriptionTextView
									.setText(getString(R.string.no_record_found));
						}
					}
				});
			}
		});
		thread.start();
	}

	private void deleteDonationFromDatabase(int position) {
		final Donation donation = (Donation) mTodoAdapter.getItem(position);
		// TODO: Delete that item from the adapter.
		mTodoAdapter.remove(donation);
		// TODO: remove todo from local database
		DataBaseManager.removeTodoDonation(getActivity(), donation.getAutoId());
		if (NetworkManager.isNetworkAvailable(getActivity())
				&& (donation.getSavedServerId() != null)) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(ID, donation.getSavedServerId()));
			// TODO: remove todo from central database
			RemoveDonationsDataOnServerService service = new RemoveDonationsDataOnServerService(
					params);
			service.setDataSendCompletion(new OnDataSendCompletion() {
				@Override
				public void onStartSendingDataToServer() {
				}

				@Override
				public void onCompleteSendingDataToServer(String result) {
					SLog.showLog("result RemoveDonationsDataOnServer : "
							+ result);
				}
			});
			service.execute();
		} else {
			if (donation.getSavedServerId() == null) {
				// TODO: do nothing bcz it is not saved on server
			} else {
				// TODO: add todo to be removed from central database
				DataBaseManager.addTodoTobeDeleted(getActivity(),
						donation.getSavedServerId());
			}
		}
	}

	public void getTodoListData() {
		mTodoDonationArrayList = DataBaseManager
				.getDonationsTodoList(getActivity());
		for (String charityNames : getCharityNamesHashSet(mTodoDonationArrayList)) {
			String imageName = charityNames;
			imageName = imageName.replace("/", "_");
			Bitmap charityBitmap = null;
			try {
				String[] files = getActivity().getAssets().list(LOGOS);
				for (int i = 0; i < files.length; i++) {
					if (files[i].contains(imageName)) {
						charityBitmap = BitmapFactory
								.decodeStream(getActivity().getAssets().open(
										LOGOS + "/" + files[i]));
						if (files[i].equalsIgnoreCase(imageName + PNG)) {
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError ex) {
				ex.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (charityBitmap != null) {
				// present in assets
				for (Donation donation : mTodoDonationArrayList) {
					if (donation.getCharity().getVuforiaRecognizedName()
							.equals(charityNames)) {
						donation.getCharity().setLogo(charityBitmap);
					}
				}
			}
		}
	}

	private HashSet<String> getCharityNamesHashSet(
			ArrayList<Donation> todoDonationArrayList) {
		HashSet<String> charityHashSet = new HashSet<String>();
		for (Donation donation : todoDonationArrayList) {
			charityHashSet
					.add(donation.getCharity().getVuforiaRecognizedName());
		}
		return charityHashSet;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.historyTextView:
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.todoMainContentLinearLayout,
							new HistoryFragment(), SUtils.HISTORY_FRAGMENT)
					.commit();
			break;
		}
	}
}
