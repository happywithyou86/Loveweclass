package com.myvideo.loveweclass;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import com.myvideo.loveweclass.R;

public class MainActivity extends TabActivity {

	public static TabHost tabHost;
	TabHost.TabSpec spec;
	TabWidget tabWidget;

	IfoodTVApplication application;
	final static int arrIcons[] = { R.drawable.home_tab_selector,
			R.drawable.search_tab_selector, R.drawable.favorite_tab_selector, R.drawable.diary_tab_selector,
			R.drawable.more_tab_selector };

	final static String arrTabLabel[] = { "Home", "Search", "Favorite", "My Diary",	"More" };

	MyTabView arrTabs[] = new MyTabView[5];
	public static MainActivity gInstance = null;

	// final int TEXT_ID = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bottom);
		// setRequestedOrientation(1);

		gInstance = this;
		initialize();
	}

	private void initialize() {
		application = (IfoodTVApplication) getApplication();
		
		tabHost = getTabHost();
		tabWidget = getTabWidget();
		Intent intent;

		// home
		intent = new Intent().setClass(this, HomeGroupActivity.class);
		arrTabs[0] = new MyTabView(this, 0, arrTabLabel[0]);
		spec = tabHost.newTabSpec(arrTabLabel[0]).setIndicator(arrTabs[0])
				.setContent(intent);
		tabHost.addTab(spec);

		// search
		intent = new Intent().setClass(this, SearchGroupActivity.class);
		arrTabs[1] = new MyTabView(this, 1, arrTabLabel[1]);
		spec = tabHost.newTabSpec(arrTabLabel[1]).setIndicator(arrTabs[1])
				.setContent(intent);
		tabHost.addTab(spec);

		// favorite
		intent = new Intent().setClass(this, FavoriteGroupActivity.class);
		arrTabs[2] = new MyTabView(this, 2, arrTabLabel[2]);
		spec = tabHost.newTabSpec(arrTabLabel[2]).setIndicator(arrTabs[2])
				.setContent(intent);
		tabHost.addTab(spec);

		// diary
		intent = new Intent().setClass(this, DiaryGroupActivity.class);
		arrTabs[3] = new MyTabView(this, 3, arrTabLabel[3]);
		spec = tabHost.newTabSpec(arrTabLabel[3]).setIndicator(arrTabs[3])
				.setContent(intent);
		tabHost.addTab(spec);

		// more
		intent = new Intent().setClass(this, MoreGroupActivity.class);
		arrTabs[4] = new MyTabView(this, 4, arrTabLabel[4]);
		spec = tabHost.newTabSpec(arrTabLabel[4]).setIndicator(arrTabs[4])
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setOnTabChangedListener(mTabChanged);
		//tabHost.setCurrentTab(0);
		// TextView tv = (TextView)
		// tabHost.getCurrentTabView().findViewById(TEXT_ID); //for Selected Tab
		// tv.setTextColor(Color.parseColor("#51b6dc"));
	}
	
	public void setCurrentTab(int nIdx)
	{
		tabHost.setCurrentTab(nIdx);
	}

	private class MyTabView extends LinearLayout {
		int nIdx = -1;

		// TextView tv;

		public MyTabView(Context c, int drawableIdx, String label) {
			super(c);
			ImageView iv = new ImageView(c);
			nIdx = drawableIdx;
			iv.setImageResource(arrIcons[nIdx]);
			setId(0x100 + nIdx);
			// tv = new TextView(c);
			// tv.setText(label);
			// tv.setGravity(Gravity.CENTER_HORIZONTAL);
			// tv.setTextSize(11.0f);
			// tv.setId(TEXT_ID);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layout.setMargins(0, 3, 0, 0);
			iv.setLayoutParams(layout);
			// layout.setMargins(0, 0, 0, 2);
			// tv.setLayoutParams(layout);

			setOrientation(LinearLayout.VERTICAL);
			setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
			addView(iv);
			// addView(tv);
		}
	}

	OnTabChangeListener mTabChanged = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {

			// TabHost tabHost = getTabHost();
			// for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++)
			// {
			// TextView tv = (TextView)
			// tabHost.getTabWidget().getChildAt(i).findViewById(TEXT_ID);
			// //Unselected Tabs
			// tv.setTextColor(Color.parseColor("#ffffff"));
			//
			// }
			// TextView tv = (TextView)
			// tabHost.getCurrentTabView().findViewById(TEXT_ID); //for Selected
			// Tab
			// tv.setTextColor(Color.parseColor("#51b6dc"));
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int i = 0;
		Log.v("LoveWithClass", "configuration changed");

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
			IfoodTVApplication application = (IfoodTVApplication) getApplication();
			if (application.cur_state != IfoodTVApplication.STAT_NORMAL) {
				tabWidget.setVisibility(View.GONE);
			} else {
				tabWidget.setVisibility(View.VISIBLE);
			}

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
			tabWidget.setVisibility(View.VISIBLE);
		}
		
		IfoodTVApplication application = (IfoodTVApplication) getApplication();
		if (application.channelInstance != null)
		{
			application.channelInstance.onConfigurationChanged(newConfig);
		}
	}
	
	public void reSelect()
	{
		int idx = tabHost.getCurrentTabView().getId();
		if (idx == 1)
		{
			tabHost.setCurrentTab(0);
			tabHost.setCurrentTab(1);
		}
	}
}
