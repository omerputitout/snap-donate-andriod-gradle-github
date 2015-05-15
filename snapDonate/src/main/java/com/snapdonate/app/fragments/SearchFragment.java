package com.snapdonate.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.snapdonate.app.R;
import com.snapdonate.app.adapter.SearchListAdapter;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.utils.IndexableListView;
import com.snapdonate.app.utils.SUtils;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFragment extends Fragment implements OnClickListener {
	private ArrayList<Charity> mCharityArrayList;
	private SearchListAdapter mSearchListAdapter;
	private IndexableListView mListView;
	private EditText mSearchEditText;
	private LinearLayout mCancelLinearLayout;
	private View mCancelView;

	public SearchFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search, container, false);
		init(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fetchCharitiesFromDatabase();
		setUpListAdapter();
	}

	private void init(View view) {
		mListView = (IndexableListView) view.findViewById(R.id.searchListView);
		mListView.setFastScrollEnabled(true);
		mListView.setFastScrollAlwaysVisible(true);
		mCancelLinearLayout = (LinearLayout) view
				.findViewById(R.id.cancelSearchLinearLayout);
		mCancelView = (View) view.findViewById(R.id.cancelSearchView);
		mCancelView.setOnClickListener(this);
		mCancelLinearLayout.setOnClickListener(this);
		mSearchEditText = (EditText) view.findViewById(R.id.searchEditText);
		mSearchEditText.addTextChangedListener(textWatcher);
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence charSequence, int start, int before,
				int count) {
			if (count > 0) {
				mCancelLinearLayout.setVisibility(View.VISIBLE);
			} else {
				mCancelLinearLayout.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable charSequence) {
			String text = mSearchEditText.getText().toString()
					.toLowerCase(Locale.getDefault());
			mSearchListAdapter.filter(text);
		}
	};
	
	private void fetchCharitiesFromDatabase() {
		ArrayList<Charity> allCharities = DataBaseManager.getCharityList(getActivity());
		mCharityArrayList = new ArrayList<Charity>();
		ArrayList<String> sectionHeadingArrayList = new ArrayList<String>();
		for (int index = 0; index < allCharities.size(); index++) {
			String name = allCharities.get(index).getName();
			if (!sectionHeadingArrayList.contains(Character.toString(
					name.charAt(0)).toUpperCase())) {
				sectionHeadingArrayList.add(Character.toString(
						name.charAt(0)).toUpperCase());
				Charity sectionCharity = new Charity();
				sectionCharity.setName(Character.toString(name.charAt(0))
						.toUpperCase());
				sectionCharity.setId(SUtils.IS_SECTION);
				mCharityArrayList.add(sectionCharity);
				mCharityArrayList.add(allCharities.get(index));
			} else {
				mCharityArrayList.add(allCharities.get(index));
			}
		}
	}
	
	private void setUpListAdapter() {
		mSearchListAdapter = new SearchListAdapter(mCharityArrayList);
		mListView.setAdapter(mSearchListAdapter);
		mListView.setOnItemClickListener(listViewItemClickListener);
	}

	OnItemClickListener listViewItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!mCharityArrayList.get(position).getId().equals(SUtils.IS_SECTION)) {
				SUtils.hideKeyBoard(mSearchEditText, getActivity());
				Charity charity = mCharityArrayList.get(position);
				charity.setMedium(SUtils.CHARITY_MEDIUM_CHOSEN);
				charity.setVuforiaRecognizedName(charity.getName());
				getActivity()
						.getSupportFragmentManager()
						.beginTransaction()
						.replace(
								R.id.mainContentLinearLayout,
								new DonationAmountFragment(
										charity,
										R.id.mainContentLinearLayout),
								SUtils.DONATION_AMOUNT_FRAGMENT)
						.addToBackStack(SUtils.DONATION_AMOUNT_FRAGMENT)
						.commit();
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.cancelSearchView:
			mSearchEditText.setText("");
			break;
			
		case R.id.cancelSearchLinearLayout:
			mSearchEditText.setText("");
			break;
		}		
	}
}
