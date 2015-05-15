package com.snapdonate.app.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.tabs.MainTabScreen;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

public class MenuFragmentActivity extends FragmentActivity implements
		OnClickListener {
	private String mFromString = "";
	private TextView mTabSnapTextView;
	private TextView mTabTodoTextView;
	private TextView mTabFaqsTextView;
	private ImageView mTabSnapImageView;
	private ImageView mTabTodoImageView;
	private ImageView mTabFaqsImageView;
	private ImageView mTabTodoCounterImageView;
	private TextView mTabTodoCounterTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_fragment_activity);
		mFromString = getIntent().getStringExtra(SUtils.FROM);
		setinitialViews();
		setTodoCounter();
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.tabMainContentLinearLayout, new MenuFragment(),
						SUtils.MENU_FRAGMENT).commit();
	}

	private void setinitialViews() {
		mTabSnapTextView = (TextView) findViewById(R.id.tabSnapTextView);
		mTabSnapTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoTextView = (TextView) findViewById(R.id.tabTodoTextView);
		mTabTodoTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoCounterTextView = (TextView) findViewById(R.id.tabTodoCounterTextView);
		mTabTodoCounterTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mTabFaqsTextView = (TextView) findViewById(R.id.tabFaqsTextView);
		mTabFaqsTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabSnapImageView = (ImageView) findViewById(R.id.tabSnapImageView);
		mTabTodoImageView = (ImageView) findViewById(R.id.tabTodoImageView);
		mTabTodoCounterImageView = (ImageView) findViewById(R.id.tabTodoCounterImageView);
		mTabFaqsImageView = (ImageView) findViewById(R.id.tabFaqsImageView);
		mTabSnapImageView.setOnClickListener(this);
		mTabTodoImageView.setOnClickListener(this);
		mTabFaqsImageView.setOnClickListener(this);

	}

	private void setTodoCounter() {
		int count = DataBaseManager.getDonationsTodoCount(this);
		if (count > 0) {
			int width = SUtils.getWindowWidht(this);
			int height = SUtils.getWindowHeight(this);
			float top = height * 0.084f;
			float left = width * 0.433f;
			float imageViewHeight = height * 0.031f;
			float imageViewWidht = width * 0.055f;
			LayoutParams layoutParams = new LayoutParams((int) imageViewWidht,
					(int) imageViewHeight);
			layoutParams.setMargins((int) left, (int) top, 0, 0);
			mTabTodoCounterImageView.setLayoutParams(layoutParams);
			mTabTodoCounterTextView.setLayoutParams(layoutParams);
			mTabTodoCounterTextView.setText(String.valueOf(count));
			mTabTodoCounterImageView.setVisibility(View.VISIBLE);
			mTabTodoCounterTextView.setVisibility(View.VISIBLE);
		} else {
			mTabTodoCounterImageView.setVisibility(View.INVISIBLE);
			mTabTodoCounterTextView.setVisibility(View.INVISIBLE);
		}
	}

	private void loadFaqMenu(View view) {
		if (mFromString.equals(SUtils.IMAGE_TARGETS)) {
			Intent intent = new Intent(view.getContext(), MainTabScreen.class);
			intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
			intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_FAQS_INDEX);
			startActivity(intent);
		} else if (mFromString.equals(SUtils.MAIN_TAB_SCREEN)) {
			Intent intent = new Intent(
					SUtils.BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB);
			intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_FAQS_INDEX);
			sendBroadcast(intent);
		}
		MenuFragmentActivity.this.finish();
	}

	private void loadToDoMenu(View view) {
		if (mFromString.equals(SUtils.IMAGE_TARGETS)) {
			Intent intent = new Intent(view.getContext(), MainTabScreen.class);
			intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
			intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_TODO_INDEX);
			startActivity(intent);
		} else if (mFromString.equals(SUtils.MAIN_TAB_SCREEN)) {
			Intent intent = new Intent(
					SUtils.BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB);
			intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_TODO_INDEX);
			sendBroadcast(intent);
		}
		MenuFragmentActivity.this.finish();
	}

	private void loadSnapMenu(View view) {
		if (mFromString.equals(SUtils.IMAGE_TARGETS)) {
			// TODO: just finish bcz image target is already open in back
			// TODO: stack.
		} else if (mFromString.equals(SUtils.MAIN_TAB_SCREEN)) {
			Intent intent = new Intent(
					SUtils.BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB);
			intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_SNAP_INDEX);
			sendBroadcast(intent);
		}
		MenuFragmentActivity.this.finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tabSnapImageView:
			loadSnapMenu(view);
			break;
		case R.id.tabTodoImageView:
			loadToDoMenu(view);
			break;
		case R.id.tabFaqsImageView:
			loadFaqMenu(view);
			break;
		}
	}
}
