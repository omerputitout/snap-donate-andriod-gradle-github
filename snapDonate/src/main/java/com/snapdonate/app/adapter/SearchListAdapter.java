package com.snapdonate.app.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.StringMatcher;

public class SearchListAdapter extends BaseAdapter implements SectionIndexer {
	private float mSectionRowHeight = 0.0f;
	private float mCharityRowHeight = 0.0f;
	private String mSectionsString = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private ArrayList<Charity> mCharityArrayList;
	private ArrayList<Charity> mFilterCharityArrayList;
	private LayoutParams mSectionRowLayoutParams;
	private LayoutParams mCharityRowLayoutParams;
	private LayoutInflater mInflater = null;

	public SearchListAdapter(ArrayList<Charity> data) {
		this.mCharityArrayList = data;
		this.mFilterCharityArrayList = new ArrayList<Charity>();
		this.mFilterCharityArrayList.addAll(data);
	}

	@Override
	public int getCount() {
		if (mCharityArrayList != null) {
			return mCharityArrayList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mCharityArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(mCharityArrayList.get(position).getId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mInflater == null) {
			mInflater = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			setRowParams(parent.getContext());
		}
		View row = convertView;
		if (mCharityArrayList.get(position).getId().equals(SUtils.IS_SECTION)) {
			row = mInflater.inflate(R.layout.search_index_item_layout, parent,
					false);
			row.setLayoutParams(mSectionRowLayoutParams);
			TextView nameTextView = (TextView) row
					.findViewById(R.id.indexNameTextView);
			nameTextView.setTypeface(SFonts.getInstance(parent.getContext())
					.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_XBOLD));
			nameTextView.setText(mCharityArrayList.get(position).getName());
		} else {
			row = mInflater.inflate(R.layout.search_charity_item_layout,
					parent, false);
			row.setLayoutParams(mCharityRowLayoutParams);
			TextView nameTextView = (TextView) row
					.findViewById(R.id.charityNameTextView);
			nameTextView.setTypeface(SFonts.getInstance(parent.getContext())
					.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
			nameTextView.setText(mCharityArrayList.get(position).getName());
		}
		return row;
	}

	private void setRowParams(Context context) {
		int height = SUtils.getWindowHeight(context);
		mSectionRowHeight = height * 0.057f;
		mCharityRowHeight = height * 0.053f;
		mSectionRowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (mSectionRowHeight));
		mCharityRowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (mCharityRowHeight));
	}

	public String getItemAsString(int position) {
		return mCharityArrayList.get(position).getName();
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO: If there is no item for current section, previous section will
		// be selected.
		for (int row = section; row >= 0; row--) {
			for (int column = 0; column < getCount(); column++) {
				String name = getItemAsString(column);
				if (row == 0) {
					// TODO: For numeric section
					for (int innerRow = 0; innerRow <= 9; innerRow++) {
						if (StringMatcher.match(String.valueOf(name.charAt(0)),
								String.valueOf(innerRow))) {
							return column;
						}
					}
				} else {
					if (StringMatcher.match(String.valueOf(name.charAt(0)),
							String.valueOf(mSectionsString.charAt(row)))) {
						return column;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSectionsString.length()];
		for (int i = 0; i < mSectionsString.length(); i++) {
			sections[i] = String.valueOf(mSectionsString.charAt(i));
		}
		return sections;
	}

	// TODO: Filter Class
	public void filter(String text) {
		mCharityArrayList.clear();
		if (text.length() == 0) {
			mCharityArrayList.addAll(mFilterCharityArrayList);
		} else {
			for (Charity charity : mFilterCharityArrayList) {
				if (charity.getName().toLowerCase(Locale.getDefault())
						.contains(text)) {
					mCharityArrayList.add(charity);
				}
			}
		}
		notifyDataSetChanged();
	}
}
