package com.myvideo.loveweclass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.HomeActivity.imgDownloader;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Recipe;
import com.myvideo.loveweclass.data.ResultChannel;
import com.myvideo.loveweclass.data.ResultDetail;
import com.myvideo.loveweclass.ui.ScrollGallery;
import com.myvideo.loveweclass.ui.UIUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChannelActivity extends NavigationActivity {
	int mainIdx = -1;
	int subIdx = -1;
	int favorIdx = -1;
	String searchUrl = null;

	LayoutInflater inflater;
	IfoodTVApplication application;
	ChannelActivity mInstance;

	ResultChannel _recipes;
	ResultDetail _detail;
	Gallery gallery;
	TextView textrecipes;
	int nCurRecipeIdx;
	Button prev, next;
	LinearLayout curLayout;
	
	private final int screen_portrait = 0;
	private final int screen_landscape = 1;
	
	private int screen_mode = screen_portrait;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		
		Intent intent = getIntent();
		mainIdx = intent.getIntExtra("SubChannelIdx", -1);
		subIdx = intent.getIntExtra("ChannelIdx", -1);
		searchUrl = intent.getStringExtra("SearchUrl");
		favorIdx = intent.getIntExtra("FavorIdx", -1);

		application = (IfoodTVApplication) getApplication();
		IfoodTVApplication.cur_state = IfoodTVApplication.STAT_RECIPES;
		inflater = LayoutInflater.from(this);
		mInstance = this;
		setNavigation();

		LoadChannel();
	}

	void setNavigation() {

		String text;
		
		if (favorIdx != -1)	{
			text  = "";
		} else if (searchUrl == null) {
			text = application.mainChannels.channels.get(mainIdx).value
					.get(subIdx).name;
		} else {
			text = application.query;
		}

		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText(text);
		Button btnAction = (Button) contents.findViewById(R.id.buttonAction);
		btnAction.setVisibility(View.GONE);

		Button btnBack = (Button) contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.VISIBLE);
		if (searchUrl != null)
			btnBack.setText("Search");
		else if (favorIdx != -1)
			btnBack.setText("Favorite");
		btnBack.setOnClickListener(mOnClickListener);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}

	public void LoadChannel()
	{
		String requestUrl = "";
		
		if (favorIdx != -1)
		{
			_recipes = new ResultChannel();
			_recipes.results = application.favoriteRecipe;
			
			nCurRecipeIdx = favorIdx;
			setChannelContents();
			return;
		} else if (searchUrl == null)
		{
			if (mainIdx == -1)		return;
			
			if (application.mainChannels.channels.get(mainIdx).value.get(subIdx).recipes != null) {
				_recipes = application.mainChannels.channels.get(mainIdx).value.get(subIdx).recipes;
				setChannelContents();
				return;
			}

			requestUrl = application.mainChannels.channels.get(mainIdx).value
					.get(subIdx).url;
		} else {
			if (application.searchChannel != null) {
				_recipes = application.searchChannel;
				setChannelContents();
			}
			requestUrl = searchUrl;
		}

		Log.v("LoveWithClass", requestUrl);

		(new LoadChannelTask(getParent(), requestUrl)).execute();
	}

	public void LoadDetails(String szId) {
		String requestUrl = WebServiceUrl.DETAIL_URL
				+ _recipes.results.get(nCurRecipeIdx).id + WebServiceUrl.AUTH_TOKEN;

		Log.v("LoveWithClass", requestUrl);

		(new LoadDetailTask(getParent(), requestUrl)).execute();
	}
	
	public void logDetails(String id, String type) {
		String requestUrl = WebServiceUrl.AppLogger;
		requestUrl += IfoodTVApplication.gInstance.getUniqueId();
		requestUrl += "&id=" + id;
		requestUrl += "&type=" + type;
		requestUrl += "&apptype=android";

		Log.v("LoveWithClass", requestUrl);

		(new logDetailTask(getParent(), requestUrl)).execute();
	}
	
	private class logDetailTask extends AsyncHttpTask<String> {
		public logDetailTask(Context context, String requestUrl) {
			super(context, requestUrl, String.class, false);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	private class LoadChannelTask extends AsyncHttpTask<ResultChannel> {
		public LoadChannelTask(Context context, String requestUrl) {
			super(context, requestUrl, ResultChannel.class, true);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			ResultChannel result = (ResultChannel) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			} else if (!result.status.equals("ok")) {
//				String msg = "status = fail: description="
//						+ result.data.description;
				String msg = "Sorry, no results were found for your search!";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}

			if (searchUrl == null)
			{
				if (mainIdx == -1)		return;
				application.mainChannels.channels.get(mainIdx).value.get(subIdx).recipes = result;
			} else {
				application.searchChannel = result;
			}
			_recipes = result;
			setChannelContents();
		}

		@Override
		protected void onPostExecute(ResultChannel result) {
			super.onPostExecute(result);
		}
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return gallery.onTouchEvent(event);
//	}

	
	private class LoadDetailTask extends AsyncHttpTask<ResultDetail> {
		public LoadDetailTask(Context context, String requestUrl) {
			super(context, requestUrl, ResultDetail.class, false);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			ResultDetail result = (ResultDetail) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_OK_CANCEL, retryDetailClicker);
				return;
			} else if (!result.status.equals("ok")) {
				String msg = "status = fail";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}

			ResultChannel channel;
			if (favorIdx != -1) {
				application.favoriteRecipe.get(favorIdx).detail = result;
			} else if (searchUrl == null)
			{
				if (mainIdx == -1)		return;
				channel = application.mainChannels.channels.get(mainIdx).value.get(subIdx).recipes;
				channel.results.get(nCurRecipeIdx).detail = result;
			} else {
				channel = application.searchChannel;
				channel.results.get(nCurRecipeIdx).detail = result;
			}

			_detail = result;
			setRecipeDetails();
		}

		@Override
		protected void onPostExecute(ResultDetail result) {
			super.onPostExecute(result);
		}
	}

	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			LoadChannel();
		}

	};
	
	DialogInterface.OnClickListener retryDetailClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			LoadDetails(_recipes.results.get(nCurRecipeIdx).id);
		}

	};
	
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
			} else if (nId == R.id.buttonBack) {
				onBackPressed();
			} else if (nId == R.id.buttonPrev) {
				if (nCurRecipeIdx == 0)		return;
				gallery.setSelection(nCurRecipeIdx - 1, true);
			} else if (nId == R.id.buttonNext) {
				if (nCurRecipeIdx == _recipes.results.size() - 1)	return;
				gallery.setSelection(nCurRecipeIdx + 1, true);
			} else if (nId == R.id.imagePlay) {
				application.channelInstance = mInstance;
				Intent intent = new Intent(getParent(), ViewVideoActivity.class);

				if (_recipes.results.get(nCurRecipeIdx).video_url
						.equalsIgnoreCase("false")) {
					intent.putExtra("flv", true);
					intent.putExtra("url",
							_recipes.results.get(nCurRecipeIdx).id);
				} else {
					intent.putExtra("url",
							_recipes.results.get(nCurRecipeIdx).video_url);
				}

				startActivity(intent);
				overridePendingTransition(R.anim.bottom_slide_in, 0);
			} else if (nId == R.id.buttonFavorite)	{
				if (application.isAddedToFavor(_recipes.results.get(nCurRecipeIdx)))
				{
					MainActivity.gInstance.setCurrentTab(2);
				} else{
					addtoFavorite();
				}
			} else if (nId == R.id.buttonDetails) {
				if (screen_mode == screen_landscape)
				{
					application.channelInstance = mInstance;
					Intent intent = new Intent(getParent(), RecipeDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					if (favorIdx != -1)
					{
						intent.putExtra("FavorIdx", favorIdx);
					} else if (searchUrl == null)
					{
						if (mainIdx == -1)	return;
						intent.putExtra("SubChannelIdx", mainIdx);
						intent.putExtra("ChannelIdx", subIdx);
					} else {
						intent.putExtra("SearchUrl", searchUrl);
					}
					intent.putExtra("RecipeIdx", nCurRecipeIdx);

					goNextHistory("ChannelActivity", intent);
					return;
				}
				v.setBackgroundResource(R.drawable.button_s);
				logDetails(_recipes.results.get(nCurRecipeIdx).node_id, _recipes.results.get(nCurRecipeIdx).type);
				LoadDetails(_recipes.results.get(nCurRecipeIdx).id);
			} else if (nId == R.id.buttonIngredients) {
				WebView content = (WebView)curLayout.findViewById(R.id.webContents);
				if (_detail.node_data.field_ingredients_value != null)
				{
					String textData = _detail.node_data.field_ingredients_value.replace("\r\n", "<br>");
					content.loadDataWithBaseURL(null, textData, "text/html", "UTF-8", null);
				}
				Button buttonIngredients = (Button)curLayout.findViewById(R.id.buttonIngredients);
				buttonIngredients.setBackgroundResource(R.drawable.left_s);
				Button buttonPreparation = (Button)curLayout.findViewById(R.id.buttonPreparation);
				buttonPreparation.setBackgroundResource(R.drawable.right_n);
			} else if (nId == R.id.buttonPreparation) {
				WebView content = (WebView)curLayout.findViewById(R.id.webContents);
				if (_detail.node_data.field_directions_value != null)
				{
					String textData = _detail.node_data.field_directions_value.replace("\n", "<br>");
					content.loadDataWithBaseURL(null, textData, "text/html", "UTF-8", null);
				}
				Button buttonIngredients = (Button)curLayout.findViewById(R.id.buttonIngredients);
				buttonIngredients.setBackgroundResource(R.drawable.left_n);
				Button buttonPreparation = (Button)curLayout.findViewById(R.id.buttonPreparation);
				buttonPreparation.setBackgroundResource(R.drawable.right_s);
			} 
		}
	};
	
	private void addtoFavorite()
	{
		Recipe recipe = null;
		
		if (searchUrl == null)	// home page
		{
			recipe = application.mainChannels.channels.get(mainIdx).value
			.get(subIdx).recipes.results.get(nCurRecipeIdx);
		}
		else	// search page
		{
			recipe = application.searchChannel.results.get(nCurRecipeIdx);
		}
		application.addFavorite(recipe);
		
		Builder dlg = new AlertDialog.Builder(mInstance.getParent());
		dlg.setCancelable(false);
		dlg.setTitle("ifood.tv");
		dlg.setMessage("Recipe Added to Favorites!");
		
		dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Button favorite = (Button)findViewById(R.id.buttonFavorite);
				favorite.setText("View Favorites");
			}
			
		});
		dlg.show();
	}
	
	void setRecipeDetails() {
		LinearLayout detailLayout = (LinearLayout)curLayout.findViewById(R.id.layoutDetail);
		detailLayout.setVisibility(View.GONE);
		LinearLayout detailedLayout = (LinearLayout)curLayout.findViewById(R.id.layoutDetailed);
		detailedLayout.setVisibility(View.VISIBLE);
		
		if (_detail == null)	return;

		if (_detail.node_data.field_servings_value != null &&
				!_detail.node_data.field_servings_value.equals("0"))
		{
			TextView textServingValue = (TextView)curLayout.findViewById(R.id.textServingValue);
			textServingValue.setText(_detail.node_data.field_servings_value);
		} else {
			LinearLayout serveLayout = (LinearLayout)curLayout.findViewById(R.id.linearServings);
			serveLayout.setVisibility(View.GONE);
		}
		
		if (_detail.node_data.field_prepration_time_value != null &&
				!_detail.node_data.field_prepration_time_value.equals("0"))
		{
			TextView textPreparationValue = (TextView)curLayout.findViewById(R.id.textPreparationValue);
			textPreparationValue.setText(_detail.node_data.field_prepration_time_value + " mins");
		} else {
			LinearLayout preparationLayout = (LinearLayout)curLayout.findViewById(R.id.linearPreparationTime);
			preparationLayout.setVisibility(View.GONE);
		}
		
		if (_detail.node_data.field_cook_time_value != null &&
				!_detail.node_data.field_cook_time_value.equals("0"))
		{
			TextView textCookingValue = (TextView)curLayout.findViewById(R.id.textCookingValue);
			textCookingValue.setText(_detail.node_data.field_cook_time_value + " mins");
		} else {
			LinearLayout cookLayout = (LinearLayout)curLayout.findViewById(R.id.linearCookingTime);
			cookLayout.setVisibility(View.GONE);
		}
		
		if (_detail.node_data.field_health_index_value != null &&
				!_detail.node_data.field_health_index_value.equals(""))
		{
			TextView textHealthValue = (TextView)curLayout.findViewById(R.id.textHealthValue);
			textHealthValue.setText(_detail.node_data.field_health_index_value);
		} else {
			LinearLayout healthLayout = (LinearLayout)curLayout.findViewById(R.id.linearHealthIndex);
			healthLayout.setVisibility(View.GONE);
		}
		
		WebView content = (WebView)curLayout.findViewById(R.id.webContents);
		WebSettings webSettings = content.getSettings();
		webSettings.setTextSize(WebSettings.TextSize.SMALLER);

		if (_detail.node_data.field_ingredients_value != null)
		{
			String textData = _detail.node_data.field_ingredients_value.replace("\r\n", "<br>");
			content.loadDataWithBaseURL(null, textData, "text/html", "UTF-8", null);
		}
		
		Button buttonIngredients = (Button)curLayout.findViewById(R.id.buttonIngredients);
		buttonIngredients.setBackgroundResource(R.drawable.left_s);
		buttonIngredients.setOnClickListener(mOnClickListener);
		Button buttonPreparation = (Button)curLayout.findViewById(R.id.buttonPreparation);
		buttonPreparation.setBackgroundResource(R.drawable.right_n);
		buttonPreparation.setOnClickListener(mOnClickListener);
	}

	void setChannelContents() {
		int nId;
		if (screen_mode == screen_portrait) {
			nId = R.layout.channel_port;
		} else {
			nId = R.layout.channel_landscape;
		}
		LinearLayout contents = (LinearLayout) inflater.inflate(
				nId, null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		prev = (Button) contents.findViewById(R.id.buttonPrev);
		prev.setOnClickListener(mOnClickListener);
		next = (Button) contents.findViewById(R.id.buttonNext);
		next.setOnClickListener(mOnClickListener);
//		Button play = (Button) contents.findViewById(R.id.buttonPlay);
//		play.setOnClickListener(mOnClickListener);
		
		Button favorite = (Button) contents.findViewById(R.id.buttonFavorite);
		if (favorIdx != -1)
			favorite.setVisibility(View.GONE);
		favorite.setOnClickListener(mOnClickListener);

		Button button = (Button) contents.findViewById(R.id.buttonFacebook);
		button.setOnClickListener(mOnClickListener);
		button = (Button) contents.findViewById(R.id.buttonTwitter);
		button.setOnClickListener(mOnClickListener);

		if (screen_mode == screen_landscape)
		{
			button = (Button) contents.findViewById(R.id.buttonDetails);
			button.setOnClickListener(mOnClickListener);
		}
		textrecipes = (TextView) contents.findViewById(R.id.textRecipes);

		gallery = (Gallery) contents.findViewById(R.id.galleryRecipe);
		gallery.setAdapter(new recipeAdapter(getParent()));
		
        
        if (screen_mode == screen_portrait)	{
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidth = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            if (screenWidth/screenHeight < 0.6)
    		{
    			gallery.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 25));
    		}

        }
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				curLayout = (LinearLayout) view;
				// Do something with position
				textrecipes.setText(String.valueOf(position + 1) + " / "
						+ String.valueOf(_recipes.results.size()));
				nCurRecipeIdx = position;
				if (nCurRecipeIdx == 0) {
					prev.setVisibility(View.INVISIBLE);
				} else {
					prev.setVisibility(View.VISIBLE);
				}
				
				if (nCurRecipeIdx == _recipes.results.size() - 1) {
					next.setVisibility(View.INVISIBLE);
				} else {
					next.setVisibility(View.VISIBLE);
				}
				
//				Button play = (Button) findViewById(R.id.buttonPlay);
//				play.setVisibility(View.VISIBLE);
//				if (searchUrl != null && SearchActivity.bVideoFlag == false)
//					play.setVisibility(View.GONE);
//				
//				if (_recipes.results.get(position).video_url.equalsIgnoreCase("false") ||
//						_recipes.results.get(position).video_flv_url.equalsIgnoreCase("false"))
//					play.setVisibility(View.GONE);
				
				if (screen_mode == screen_landscape)
				{
					TextView title = (TextView)findViewById(R.id.textLandTitle);
//					title.setLines(2);
					title.setText(_recipes.results.get(position).title);
					TextView textAuthor = (TextView) findViewById(R.id.textAuthor);
					textAuthor.setText("By " + _recipes.results.get(position).author);
				}

				Button favorite = (Button) findViewById(R.id.buttonFavorite);
				if (application.isAddedToFavor(_recipes.results.get(position)))	{
					favorite.setText("View Favorites");
				} else {
					favorite.setText("Add to Favorites");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		gallery.setSelection(nCurRecipeIdx);
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.removeAllViews();
		linearContents.addView(contents);
	}

	public class recipeAdapter extends BaseAdapter {
		Context _context;

		public recipeAdapter(Context context) {
			_context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return _recipes.results.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (screen_mode == screen_portrait)	{
				return getPortraitRecipe(position);
			} else {
				return getLandscapeRecipe(position);
			}
		}
	};
	
	public View getPortraitRecipe(int position)
	{
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.recipe_port, null);

		TextView textRecipeTitle = (TextView) contents
				.findViewById(R.id.textRecipeTitle);
		textRecipeTitle.setText(_recipes.results.get(position).title);
		TextView textAuthor = (TextView) contents.findViewById(R.id.textAuthor);
		textAuthor.setText("By " + _recipes.results.get(position).author);
		FrameLayout frameLayout = (FrameLayout) contents
				.findViewById(R.id.frameThumb);
		ImageView thumb = (ImageView) contents.findViewById(R.id.imageThumb);
		thumb.setScaleType(ScaleType.CENTER_CROP);
		ImageView play = (ImageView) contents.findViewById(R.id.imagePlay);
		play.setOnClickListener(mOnClickListener);
		play.setVisibility(View.INVISIBLE);
//		if (searchUrl != null && SearchActivity.bVideoFlag == false)
//			play.setVisibility(View.GONE);
		
		if (_recipes.results.get(position).video_url.equalsIgnoreCase("false") ||
				_recipes.results.get(position).video_flv_url.equalsIgnoreCase("false")) {
			frameLayout.removeView(play);
			//play.setVisibility(View.GONE);
		}
		
		Button button = (Button) contents.findViewById(R.id.buttonDetails);
		button.setOnClickListener(mOnClickListener);
		String strImgFile = ImageDownloader
				.getFileNamefromUrl(_recipes.results.get(position).main_picture);
		
		ProgressBar progress = (ProgressBar) frameLayout.findViewById(R.id.progressBar);
		progress.setVisibility(View.INVISIBLE);
		if (strImgFile.length() > 0) {
			if (ImageDownloader.isCached(strImgFile)) {
				thumb.setImageBitmap(ImageDownloader.getCacheBitmap(strImgFile));
				play.setVisibility(View.VISIBLE);
			} else {
				progress.setVisibility(View.VISIBLE);
				imgDownloader downloader = new imgDownloader(frameLayout, _recipes.results.get(position).main_picture, strImgFile);
				try {
					downloader.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}
		} else {
			thumb.setImageResource(R.drawable.fooddefaultpic);
			play.setVisibility(View.VISIBLE);
		}
//		(new imgDownloader(frameLayout,
//				_recipes.results.get(position).main_picture, strImgFile))
//				.execute();

		LinearLayout layoutDetailed = (LinearLayout) contents
				.findViewById(R.id.layoutDetailed);
		layoutDetailed.setVisibility(View.GONE);
		Gallery.LayoutParams layout = new Gallery.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		
		return contents;
	}
	
	public View getLandscapeRecipe(int position)
	{
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.recipe_landscape, null);
		contents.setGravity(Gravity.CENTER_VERTICAL);

		FrameLayout frameLayout = (FrameLayout) contents
				.findViewById(R.id.frameThumb);
		ImageView thumb = (ImageView) contents.findViewById(R.id.imageThumb);
		thumb.setScaleType(ScaleType.CENTER_CROP);
		ImageView play = (ImageView) contents.findViewById(R.id.imagePlay);
		play.setOnClickListener(mOnClickListener);
		play.setVisibility(View.INVISIBLE);
//		if (searchUrl != null && SearchActivity.bVideoFlag == false)
//			play.setVisibility(View.GONE);
		
		if (_recipes.results.get(position).video_url.equalsIgnoreCase("false") ||
				_recipes.results.get(position).video_flv_url.equalsIgnoreCase("false"))
		{
			frameLayout.removeView(play);
		//	play.setVisibility(View.GONE);
		}
		play.setOnClickListener(mOnClickListener);
		String strImgFile = ImageDownloader
				.getFileNamefromUrl(_recipes.results.get(position).main_picture);
		
		ProgressBar progress = (ProgressBar) frameLayout.findViewById(R.id.progressBar);
		progress.setVisibility(View.INVISIBLE);
		if (strImgFile.length() > 0) {
			if (ImageDownloader.isCached(strImgFile)) {
				thumb.setImageBitmap(ImageDownloader.getCacheBitmap(strImgFile));
				play.setVisibility(View.VISIBLE);
			} else {
				progress.setVisibility(View.VISIBLE);
				imgDownloader downloader = new imgDownloader(frameLayout, _recipes.results.get(position).main_picture, strImgFile);
				try {
					downloader.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}
		} else {
			thumb.setImageResource(R.drawable.fooddefaultpic);
			play.setVisibility(View.VISIBLE);
		}
//		(new imgDownloader(frameLayout,
//				_recipes.results.get(position).main_picture, strImgFile))
//				.execute();

		Gallery.LayoutParams layout = new Gallery.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		return contents;
	}

	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
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
		application.channelInstance = null;
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);

    	Log.v("LoveWithClass", "configuration1 changed");
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	screen_mode = screen_landscape;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	screen_mode = screen_portrait;
        }
        if (_recipes != null)
        	setChannelContents();
    }
    
    public void setConfiguration(Configuration newConfig)
    {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	screen_mode = screen_landscape;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	screen_mode = screen_portrait;
        }
        if (_recipes != null)
        	setChannelContents();
    }
    
    
}
