package com.snapdonate.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.model.Faqs;
import com.snapdonate.app.utils.SFonts;

import java.util.ArrayList;

public class FaqsAdapter extends BaseAdapter implements OnClickListener {
	private static final int ANSWER_LENGTH_LIMIT = 280;
	private static final int MAXIMUM_LINES_LIMIT = 100;
	private ArrayList<Faqs> mFaqsArrayList;

	public FaqsAdapter(ArrayList<Faqs> data) {
		this.mFaqsArrayList = data;
	}

	@Override
	public int getCount() {
		if (mFaqsArrayList != null) {
			return mFaqsArrayList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mFaqsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(mFaqsArrayList.get(position).getAutoId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder = null;
		if (row == null) {
			holder = new Holder();
			LayoutInflater inflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.faqs_item_layout, parent, false);
			holder.questionTextView = (TextView) row
					.findViewById(R.id.questionTextView);
			holder.answerTextView = (TextView) row
					.findViewById(R.id.answerTextView);
			holder.readMoreTextView = (TextView) row
					.findViewById(R.id.readMoreTextView);
			setHolderViewsFont(holder, parent);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}
		setHolderViewsDetail(holder, position);
		setHolderViewsClicklistners(holder, position);
		return row;
	}

	public void setHolderViewsFont(Holder holder, ViewGroup parent) {
		holder.questionTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		holder.answerTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		holder.readMoreTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
	}

	public void setHolderViewsDetail(Holder holder, int position) {
		holder.questionTextView.setText(mFaqsArrayList.get(position)
				.getQuestion());
		holder.answerTextView.setText(mFaqsArrayList.get(position).getAnswer());

		if (mFaqsArrayList.get(position).getAnswer().length() > ANSWER_LENGTH_LIMIT) {
			holder.readMoreTextView.setVisibility(View.VISIBLE);
		}
	}

	private void setHolderViewsClicklistners(Holder holder,int position) {
		holder.readMoreTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.readMoreTextView:
			((TextView) view).setMaxLines(MAXIMUM_LINES_LIMIT);
			view.setVisibility(View.GONE);
			break;
		}
	}
	
	public static class Holder {
		TextView questionTextView;
		TextView answerTextView;
		TextView readMoreTextView;
	}
}
