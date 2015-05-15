package com.snapdonate.app.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.snapdonate.app.R;
import com.snapdonate.app.adapter.FaqsAdapter;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.model.Faqs;

import java.util.ArrayList;

public class FaqsFragmentActivity extends FragmentActivity {
	private ListView mListView;
	private ArrayList<Faqs> mDataArrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faqs_fragment_activity);
		init();
	}

	// TODO: Nav O1 Working
	@Override
	protected void onResume() {
		super.onResume();
		FaqsAdapter adapter = new FaqsAdapter(mDataArrayList);
		mListView.setAdapter(adapter);
	}

	private void init() {
		mListView = (ListView) findViewById(R.id.faqsListView);
		mDataArrayList = DataBaseManager.getFaqsQuestions(this);
	}

}
