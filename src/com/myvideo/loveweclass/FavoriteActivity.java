package com.myvideo.loveweclass;

import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.data.Recipe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FavoriteActivity extends NavigationActivity {
	IfoodTVApplication application;
	LayoutInflater inflater;
	boolean bEdit = false;
	
	
	ArrayList<Integer> deleteList = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		application = (IfoodTVApplication) getApplication();
		inflater = LayoutInflater.from(this);
		setNavigation();
	}

	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText("Favorites");
		Button btnAction = (Button) contents.findViewById(R.id.buttonAction);
		btnAction.setText("Edit");
		btnAction.setOnClickListener(mOnClickListener);
		if (application.favoriteRecipe.size() == 0)
			btnAction.setVisibility(View.INVISIBLE);

		Button btnBack = (Button) contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.INVISIBLE);

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
			if (nId == R.id.buttonAction) {
				if (bEdit)
				{
					bEdit = false;
					Button btnAction = (Button) findViewById(R.id.buttonAction);
					btnAction.setText("Edit");
				}
				else
				{
					bEdit = true;
					Button btnAction = (Button) findViewById(R.id.buttonAction);
					btnAction.setText("Done");
				}
				setContents();
			} else if (nId == R.id.linearFavor)
			{
				int nIdx = ((Integer)v.getTag()).intValue();
				
				Intent intent = new Intent(getParent(), ChannelActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("FavorIdx", nIdx);

				goNextHistory("ChannelActivity", intent);
			} else if (nId == R.id.toggleButton)
			{
				int nIdx = ((Integer)v.getTag()).intValue();
				ToggleButton toggle = (ToggleButton)v;
				if (toggle.isChecked())
				{
					v.setBackgroundResource(R.drawable.selected);
					deleteList.add(nIdx);
				}
				else
				{
					v.setBackgroundResource(R.drawable.unselected);
					for (int i = 0; i < deleteList.size(); i++)
					{
						if (deleteList.get(i).intValue() == nIdx)
						{
							deleteList.remove(i);
							break;
						}
					}
				}
			} else if (nId == R.id.buttonDelete)
			{
				if (deleteList.size() > 0)
				{
					application.removeFavorite(deleteList);
					application.saveFavorite();
					setContents();
				}
			}
		}
	};
	
	OnTouchListener mTouchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.linearFavor)
			{
				int nIdx = ((Integer)v.getTag()).intValue();
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if (application.favoriteRecipe.size() == 1) {
						v.setBackgroundResource(R.drawable.sel_one_item_bg);
					} else if (nIdx == 0)	{
						v.setBackgroundResource(R.drawable.sel_top_item_bg);
					} else if (nIdx == application.favoriteRecipe.size() - 1) {
						v.setBackgroundResource(R.drawable.sel_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.sel_middle_item_bg);
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (application.favoriteRecipe.size() == 1) {
						v.setBackgroundResource(R.drawable.white_one_item_bg);
					} else if (nIdx == 0)	{
						v.setBackgroundResource(R.drawable.white_top_item_bg);
					} else if (nIdx == application.favoriteRecipe.size() - 1) {
						v.setBackgroundResource(R.drawable.white_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.white_middle_item_bg);
					}
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL)
				{
					if (application.favoriteRecipe.size() == 1) {
						v.setBackgroundResource(R.drawable.white_one_item_bg);
					} else if (nIdx == 0)	{
						v.setBackgroundResource(R.drawable.white_top_item_bg);
					} else if (nIdx == application.favoriteRecipe.size() - 1) {
						v.setBackgroundResource(R.drawable.white_bottom_item_bg);
					} else {
						v.setBackgroundResource(R.drawable.white_middle_item_bg);
					}
				}
			}
			return false;
		}
		
	};

	void setContents() {
		
		Button btnAction = (Button) findViewById(R.id.buttonAction);
		if (application.favoriteRecipe.size() > 0)
			btnAction.setVisibility(View.VISIBLE);
		else {
			btnAction.setVisibility(View.INVISIBLE);
			btnAction.setText("Edit");
			bEdit = false;
		}
		
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.favorite, null);
		
		deleteList.clear();
		LinearLayout favorites = (LinearLayout) contents.findViewById(R.id.linearFavorites);
		Button btnDelete = (Button) contents.findViewById(R.id.buttonDelete);
		btnDelete.setOnClickListener(mOnClickListener);
		if (bEdit == false || application.favoriteRecipe.size() == 0)
			btnDelete.setVisibility(View.GONE);

		ArrayList<Recipe> listFavorites = application.favoriteRecipe;
		for (int i = 0; i < listFavorites.size(); i++)
		{
			LinearLayout favor = (LinearLayout)inflater.inflate(R.layout.favor_item, null);
			LinearLayout recipe = (LinearLayout) favor.findViewById(R.id.linearFavor);
			recipe.setTag(i);
			recipe.setOnClickListener(mOnClickListener);
			recipe.setOnTouchListener(mTouchListener);
			
			ToggleButton toggle = (ToggleButton) recipe.findViewById(R.id.toggleButton);
			toggle.setTag(i);
			toggle.setOnClickListener(mOnClickListener);
			if (bEdit == false)
				toggle.setVisibility(View.GONE);

			ImageView thumb = (ImageView) recipe.findViewById(R.id.imageThumb);
			String strImgFile = ImageDownloader
					.getFileNamefromUrl(listFavorites.get(i).main_picture);
			if (strImgFile.length() > 0) {
				if (ImageDownloader.isCached(strImgFile)) {
					thumb.setImageBitmap(ImageDownloader.getCacheBitmap(strImgFile));
				} else {
					imgDownloader downloader = new imgDownloader(recipe, listFavorites.get(i).main_picture, strImgFile);
					try {
						downloader.execute();
					} catch (RejectedExecutionException e) {
						e.printStackTrace();
					}
				}
			} else {
				thumb.setImageResource(R.drawable.fooddefaultpic);
			}
			TextView textTitle = (TextView) recipe.findViewById(R.id.textTitle);
			textTitle.setText(listFavorites.get(i).title);
			textTitle.setTextColor(Color.BLACK);
			TextView textAuthor = (TextView) recipe.findViewById(R.id.textAuthor);
			textAuthor.setText(listFavorites.get(i).author);
			textAuthor.setTextColor(Color.BLACK);
			
			if (listFavorites.size() == 1) {
				recipe.setBackgroundResource(R.drawable.white_one_item_bg);
			} else if (i == 0)	{
				recipe.setBackgroundResource(R.drawable.white_top_item_bg);
			} else if (i == listFavorites.size() - 1) {
				recipe.setBackgroundResource(R.drawable.white_bottom_item_bg);
			} else {
				recipe.setBackgroundResource(R.drawable.white_middle_item_bg);
			}
			LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			favor.setLayoutParams(param1);
			favorites.addView(favor);
		}
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearContents.removeAllViews();
		linearContents.addView(contents, param);
	}
	
	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
		}

	};

	@Override
	protected void onResume() {
		application.readFavorite();
		setContents();
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
}
