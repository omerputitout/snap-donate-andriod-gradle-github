package com.snapdonate.app.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.snapdonate.app.R;
import com.snapdonate.app.fragments.TodoFragment;
import com.snapdonate.app.utils.SUtils;

public class TodoFragmentActivity extends FragmentActivity {
	private TodoFragment mTodoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_fragment_activity);
		showTodoFragment();
		setUpBroadCastRecievers();
	}

	private void showTodoFragment() {
		mTodoFragment = new TodoFragment();
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.todoMainContentLinearLayout, mTodoFragment,
						SUtils.TODO_FRAGMENT).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadCastReceivers);
	}

	private BroadcastReceiver mBroadCastReceivers = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB)) {
				// TODO: remove the fragments if any attached and attach the
				// TODO: Fragment.
				getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.todoMainContentLinearLayout,
								new TodoFragment(), SUtils.TODO_FRAGMENT)
						.commit();
			}
		}
	};

	private void setUpBroadCastRecievers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB);
		registerReceiver(mBroadCastReceivers, filter);
	}
}
