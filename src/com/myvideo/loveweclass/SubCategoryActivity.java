package com.myvideo.loveweclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.data.Categories;
import com.myvideo.loveweclass.data.Category;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SubCategoryActivity extends NavigationActivity implements OnScrollListener {
	LayoutInflater inflater;
	
	SubCategoryActivity mInstance = null; 
	TextView dividerText;
	Categories _category = null;
	ArrayList<Category> listCats = new ArrayList<Category>();
	static public boolean bSel = false;
	static public int prevCat = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		Intent intent = getIntent();
		int nId = intent.getIntExtra("idx", 0);
		prevCat = intent.getIntExtra("prev", -1);
		IfoodTVApplication application = (IfoodTVApplication) getApplication();
		_category = application.categories.cats.get(nId);
		
		inflater = LayoutInflater.from(this);
		mInstance = this;
		setNavigation();
		setContents();
	}
	
	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText(_category.name);
		Button btnAction = (Button) contents.findViewById(R.id.buttonAction);
		btnAction.setVisibility(View.GONE);

		Button btnBack = (Button) contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(mOnClickListener);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int nId = v.getId();
			if (nId == R.id.buttonBack) {
				onBackPressed();
			}
		}
	};

	Comparator<Category> comperator = new Comparator<Category>() {

		@Override
		public int compare(Category lhs, Category rhs) {
			// TODO Auto-generated method stub
			return lhs.name.compareToIgnoreCase(rhs.name);
		}
	};
		
	private void setContents()
	{
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.sub_category, null);

		for(char c = 0x41; c < 0x5b; c++)
		{
			ArrayList<Category> catergories = _category.category.get(String.valueOf(c));
			
			if (catergories == null)	continue;
			for (int i = 0; i < catergories.size(); i++)
				listCats.add(catergories.get(i));
		}
		
		Collections.sort(listCats, comperator);

		ListView listView = (ListView) contents.findViewById(R.id.listSubCats);
		dividerText = (TextView)contents.findViewById(R.id.textDivider);
		dividerText.setTextColor(Color.WHITE);
		dividerText.setPadding(20, 5, 30, 5);
		listView.setBackgroundColor(Color.WHITE);
		if (android.os.Build.VERSION.SDK_INT >= 10)
			listView.setOverscrollFooter(null);
		listView.setAdapter(new CustomListAdapter(getParent()));
		listView.setOnScrollListener(this);
		
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		linearContents.addView(contents, param);
	}
	
	private class CustomListAdapter extends BaseAdapter
	{
		Context _context = null;
		
		CustomListAdapter(Context context)
		{
			_context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listCats.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout cell = new LinearLayout(_context);
			cell.setOrientation(LinearLayout.VERTICAL);
			Category subCat = listCats.get(position);
			TextView divider = null;
			boolean bHaseHeader = false;
			if (position == 0)
			{
				bHaseHeader = true;
			}
			else if (Character.toUpperCase(listCats.get(position -1).name.charAt(0)) !=
					Character.toUpperCase(subCat.name.charAt(0)))
			{
				bHaseHeader = true;
			}
			
			if (bHaseHeader)
			{
				divider = new TextView(_context);
				divider.setBackgroundColor(0xff603913);
				divider.setTextColor(Color.WHITE);
				divider.setText(String.valueOf(Character.toUpperCase(subCat.name.charAt(0))));
				divider.setTypeface(null, Typeface.BOLD);
			}
			
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			if (divider != null)
			{
				divider.setPadding(20, 5, 30, 5);
				cell.addView(divider, param);
			}
			TextView textItem = new TextView(_context);
			textItem.setText(subCat.name);
			textItem.setId(Integer.valueOf(subCat.id));
			textItem.setTextColor(Color.BLACK);
			
			ArrayList<Integer> list;
			if (_category.name.equalsIgnoreCase("producers"))
				list = SearchActivity.mInstance.listUID;
			else
				list = SearchActivity.mInstance.listTID;
			
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).intValue() == Integer.valueOf(subCat.id).intValue())
				{
					textItem.setTextColor(0xff603913);
					break;
				}

			param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			textItem.setPadding(20, 10, 30, 10);
			textItem.setOnTouchListener(mTouchListener);
			cell.addView(textItem, param);
			
			return cell;
		}
	}
	
	OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if (arg1.getAction() == MotionEvent.ACTION_DOWN)
			{
				arg0.setBackgroundColor(0xFF4b8ef0);
			}
			else if (arg1.getAction() == MotionEvent.ACTION_UP)
			{
				boolean bFound = false;
				prevCat = arg0.getId();
				
				ArrayList<Integer> list;
				boolean bIsTID;
				if (_category.name.equalsIgnoreCase("producers"))
				{
					list = SearchActivity.mInstance.listUID;
					bIsTID = false;
				}
				else
				{
					list = SearchActivity.mInstance.listTID;
					bIsTID = true;
				}
				
				for (int i = 0; i < list.size(); i++)
					if (list.get(i).intValue() == prevCat)
					{
						bFound = true;
						break;
					}

				if (bFound)
				{
					bSel = false;
					SearchActivity.mInstance.updateIdList("", bIsTID);
				}
				else
				{
					bSel = true;
					String catname = ((TextView)arg0).getText().toString();
					SearchActivity.mInstance.updateIdList(catname, bIsTID);
				}
				onBackPressed();
				arg0.setBackgroundColor(Color.WHITE);
			}
			else if (arg1.getAction() == MotionEvent.ACTION_CANCEL)
			{
				arg0.setBackgroundColor(Color.WHITE);
			}
			return true;
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		dividerText.setText(String.valueOf(listCats.get(firstVisibleItem).name.charAt(0)));
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}


}
