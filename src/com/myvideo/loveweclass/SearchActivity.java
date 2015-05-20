package com.myvideo.loveweclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Categories;
import com.myvideo.loveweclass.data.Category;
import com.myvideo.loveweclass.data.ResultCategories;
import com.myvideo.loveweclass.ui.UIUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends NavigationActivity implements OnEditorActionListener {
	LayoutInflater inflater;

	static SearchActivity mInstance = null;

	ResultCategories _categories = null;
	IfoodTVApplication application;
	
	final static int CAT_ID = 0x100;
	ArrayList<Integer> listTID = new ArrayList<Integer>();
	ArrayList<Integer> listUID = new ArrayList<Integer>();
	LinearLayout[]	linearCat;
	View selCategory = null;
	
	public static boolean bVideoFlag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		application = (IfoodTVApplication) getApplication();
		inflater = LayoutInflater.from(this);
		mInstance = this;
		setNavigation();
		setContents();
		LoadCategories();
	}
	
	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.search_bar, null);
		EditText txtSearch = (EditText) contents.findViewById(R.id.editSearch);
		txtSearch.setText("");
		txtSearch.setOnEditorActionListener(this);
		Button btnSearch = (Button) contents.findViewById(R.id.buttonSearch);
		btnSearch.setOnClickListener(mOnClickListener);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}
	
	private void resetSearchOption()
	{
		EditText txtSearch = (EditText) findViewById(R.id.editSearch);
		txtSearch.setText("");
		listTID.clear();
		listUID.clear();
		application.query = "";
		
		TextView text;
		if (linearCat != null)
		{
			for (int i = 0; i < linearCat.length; i++)
			{
				if (linearCat[i] != null)
				{
					text = (TextView)linearCat[i].findViewById(R.id.textValue);
					text.setText("");
				}
			}
		}
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if ((actionId == EditorInfo.IME_ACTION_SEARCH) ||
				(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			searchRecipe();
		}
		return false;
	}

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int nId = v.getId();
			if (nId == R.id.buttonAction) {
				// Intent intent = new Intent(getParent(),
				// MeasurementsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				// Intent.FLAG_ACTIVITY_SINGLE_TOP);
				//
				// goNextHistory("Measurements", intent);
			}
			else if (nId == R.id.buttonSearch)
			{
				searchRecipe();
			}
			else if (nId == R.id.toggleVideo)
			{
				ToggleButton videoButton = (ToggleButton) findViewById(R.id.toggleVideo);
				bVideoFlag = videoButton.isChecked();
			}
			else if (nId == R.id.linearCategory)
			{
				int nIdx = ((Integer)v.getTag()).intValue();
				
				selCategory = null;
				if (nIdx < 0 || _categories == null)	return;
				selCategory = v;
				
				Intent intent = new Intent(getParent(), SubCategoryActivity.class);
				intent.putExtra("idx", nIdx);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				goNextHistory("SubCategory", intent);
			}
			else if (nId == R.id.buttonReset)
			{
				resetSearchOption();
			}
		}
	};
	
	private void searchRecipe()
	{
		hideKayboard();
		
		String searchIdList = "";
		for (int i =0 ; i < listTID.size(); i++)
		{
			if (searchIdList.length() == 0)
			{
				searchIdList = "tid:" + String.valueOf(listTID.get(i));
			} else {
				searchIdList += "%20tid:" + String.valueOf(listTID.get(i));
			}
		}
		
		for (int i =0 ; i < listUID.size(); i++)
		{
			if (searchIdList.length() == 0)
			{
				searchIdList = "uid:" + String.valueOf(listUID.get(i));
			} else {
				searchIdList += "%20uid:" + String.valueOf(listUID.get(i));
			}
		}
		
		EditText queryEdit = (EditText) findViewById(R.id.editSearch);
		application.query = queryEdit.getText().toString();
		String searchUrl = WebServiceUrl.SEARCH_URL + application.query;
		searchUrl += WebServiceUrl.SEARCH_PARAM;
		searchUrl += "&filters=" + searchIdList;
		
		if (bVideoFlag)
			if (searchIdList.equals(""))
				searchUrl += "tis_has_video:1";
			else
				searchUrl += "%20tis_has_video:1";

		Intent intent = new Intent(getParent(), ChannelActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("SearchUrl", searchUrl);

		goNextHistory("ChannelActivity", intent);
	}

	private void hideKayboard()
	{
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		EditText queryEdit = (EditText) findViewById(R.id.editSearch);
		mgr.hideSoftInputFromWindow(queryEdit.getWindowToken(), 0);
	}
	
	public void LoadCategories() {
		String requestUrl = WebServiceUrl.CATEGORY_URL;

		Log.v("LoveWithClass", requestUrl);
		UIUtils.getDisplayMetricsDensity(this);

		(new LoadCategoriesTask(getParent(), requestUrl)).execute();
	}

	private class LoadCategoriesTask extends AsyncHttpTask<String> {
		public LoadCategoriesTask(Context context, String requestUrl) {
			super(context, requestUrl, String.class, true);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			String result = (String) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			} else {
				// String msg = "status = fail";
				// UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
				// UIUtils.MSG_CLOSE, null);
				// return;
			}

			parseCategories(result);

			showCategories();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			LoadCategories();
		}

	};

	void setContents() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.search, null);
		Button btnReset = (Button) contents.findViewById(R.id.buttonReset);
		btnReset.setOnClickListener(mOnClickListener);
		ToggleButton videoButton = (ToggleButton) contents.findViewById(R.id.toggleVideo);
		videoButton.setOnClickListener(mOnClickListener);
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}
	
	OnTouchListener mTouchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.linearCategory)
			{
				int nIdx = ((Integer)v.getTag()).intValue();
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if (nIdx == _categories.cats.size() - 1) {
						v.setBackgroundResource(R.drawable.sel_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.sel_middle_item_bg);
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (nIdx == _categories.cats.size() - 1) {
						v.setBackgroundResource(R.drawable.white_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.white_middle_item_bg);
					}
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL)
				{
					if (nIdx == _categories.cats.size() - 1) {
						v.setBackgroundResource(R.drawable.white_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.white_middle_item_bg);
					}
				}
			}
			return false;
		}
		
	};
	
	void showCategories() {
		if (_categories == null || !_categories.status.equals("ok"))
			return;
		
		IfoodTVApplication application = (IfoodTVApplication) getApplication();
		application.setCategories(_categories);
		
		LinearLayout catsLayout = (LinearLayout) findViewById(R.id.linearCategories);
		
		if (_categories.cats.size() > 0)
		{
			LinearLayout linearVideo = (LinearLayout) findViewById(R.id.linearVideo);
			linearVideo.setBackgroundResource(R.drawable.white_top_item_bg);
		}
		LinearLayout category;
		Categories cat;
		linearCat = new LinearLayout[_categories.cats.size()];
		for (int i = 0; i < _categories.cats.size(); i++)
		{
			cat = _categories.cats.get(i);
			category = (LinearLayout) inflater.inflate(
					R.layout.category, null);
			linearCat[i] = (LinearLayout) category.findViewById(R.id.linearCategory);
			linearCat[i].setTag(i);
			linearCat[i].setOnClickListener(mOnClickListener);
			linearCat[i].setOnTouchListener(mTouchListener);
			TextView textname = (TextView) category.findViewById(R.id.textName);
			textname.setText(upperCaseFirstChar(cat.name));
			
			if (i == _categories.cats.size() - 1)
			{
				linearCat[i].setBackgroundResource(R.drawable.white_bottom_item_bg);
			}
			catsLayout.addView(category);
		}
	}

	public final static String upperCaseFirstChar(final String target) {

	    if ((target == null) || (target.length() == 0)) {
	        return target; // You could omit this check and simply live with an
	                       // exception if you like
	    }
	    return Character.toUpperCase(target.charAt(0))
	            + (target.length() > 1 ? target.substring(1) : "");
	}


	public void updateIdList(String selName, boolean bIsTID)
	{
		ArrayList<Integer> list;
		
		if (bIsTID)
		{
			list = listTID;
			Log.v("LoveWithClass", "tid :" + String.valueOf(SubCategoryActivity.prevCat));
		}
		else
		{
			list = listUID;
			Log.v("LoveWithClass", "uid :" + String.valueOf(SubCategoryActivity.prevCat));
		}
		
		if (SubCategoryActivity.bSel && SubCategoryActivity.prevCat != -1)
		{
			boolean bFound = false;
			for(int i = 0; i < list.size(); i++)
				if (list.get(i).intValue() == SubCategoryActivity.prevCat)
				{
					bFound = true;
				}
			
			if (!bFound)
			{
				list.add(SubCategoryActivity.prevCat);
				Log.v("LoveWithClass", "added id " + String.valueOf(SubCategoryActivity.prevCat));
			}
		} else {
			for(int i = 0; i < list.size(); i++)
				if (list.get(i).intValue() == SubCategoryActivity.prevCat)
				{
					list.remove(i);
				}
			Log.v("LoveWithClass", "removed id " + String.valueOf(SubCategoryActivity.prevCat));
		}
		
		if (selCategory != null)
		{
			TextView textValue = (TextView) selCategory.findViewById(R.id.textValue);
			textValue.setText(selName);
		}
	}
	
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

	public void parseCategories(String result) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (json == null)
			return;

		_categories = new ResultCategories();
		_categories.cats = new ArrayList<Categories>();

	    Iterator iter = json.keys();
	    Iterator catIter = null;
	    Categories cats;
	    
	    while(iter.hasNext()){
	        String key = (String)iter.next();
	        String value = null;
			try {
				value = json.getString(key);
				if (key.equals("status"))
				{
					_categories.status = value;
					continue;
				}
			
				if (key.equals("type"))
				{
					_categories.type = value;
					continue;
				}

				JSONObject catJson = new JSONObject(value);
				catIter = catJson.keys();
				cats = new Categories();
				cats.name = key;
				cats.category = new HashMap<String, ArrayList<Category>>();
				while(catIter.hasNext()) {
					String catKey = (String)catIter.next();
					String catValue = catJson.getString(catKey);
					Category cat = new Category();
					cat.name = catKey;
					cat.id = catValue;
					
					char cMapkey = Character.toUpperCase(catKey.charAt(0));
					if (!cats.category.containsKey(String.valueOf(cMapkey)))
					{
						cats.category.put(String.valueOf(cMapkey), new ArrayList<Category>());
					}
					ArrayList<Category> list = cats.category.get(String.valueOf(cMapkey));
					if (list != null)
						list.add(cat);					
				}
				_categories.cats.add(cats);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
