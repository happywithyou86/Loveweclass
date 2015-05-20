package com.myvideo.loveweclass;

import java.util.concurrent.RejectedExecutionException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Recipe;
import com.myvideo.loveweclass.data.ResultChannel;
import com.myvideo.loveweclass.data.ResultDetail;
import com.myvideo.loveweclass.ui.UIUtils;

public class RecipeDetailActivity extends NavigationActivity implements OnEditorActionListener {
	int mainIdx = -1;
	int subIdx = -1;
	int favorIdx = -1;
	int recipeIdx = 0;
	String searchUrl = null;
	IfoodTVApplication application;
	LayoutInflater inflater;
	Recipe _result;
	LinearLayout contents;
	FrameLayout content;
	LinearLayout main;
	LinearLayout ingredient;
	LinearLayout preparation;
	CountDownTimer mCountDown;

	private final int screen_main = 0;
	private final int screen_ingred = 1;
	private final int screen_preparation = 2;

	String[] strIngredient = new String[0];
	String[] strPreparation = new String[0];

	Gallery gallery;
	int nCurDirectionIdx = 0;

	RecipeDetailActivity mInstance;
	int mElapse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		application = (IfoodTVApplication) getApplication();
		Intent intent = getIntent();
		mainIdx = intent.getIntExtra("SubChannelIdx", -1);
		subIdx = intent.getIntExtra("ChannelIdx", -1);
		recipeIdx = intent.getIntExtra("RecipeIdx", 0);
		favorIdx = intent.getIntExtra("FavorIdx", -1);
		searchUrl = intent.getStringExtra("SearchUrl");
		
		inflater = LayoutInflater.from(this);
		setNavigation();
		mInstance = this;
		IfoodTVApplication.cur_state = IfoodTVApplication.STAT_DETAIL;

		if (_result.detail == null) {
			LoadDetails();
		} else
			showRecipe();
	}

	void LoadDetails() {
		String requestUrl = WebServiceUrl.DETAIL_URL + _result.id
				+ WebServiceUrl.AUTH_TOKEN;

		Log.v("LoveWithClass", requestUrl);

		(new LoadDetailTask(getParent(), requestUrl)).execute();
	}

	void setNavigation() {
		
		if (favorIdx != -1){
			_result = application.favoriteRecipe.get(favorIdx);
		} else if (searchUrl == null)
		{
			_result = application.mainChannels.channels.get(mainIdx).value.get(subIdx).recipes.results
					.get(recipeIdx);
		} else {
			_result = application.searchChannel.results.get(recipeIdx);
		}
		
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText(_result.title);
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
			if (nId == R.id.buttonAction) {
				// Intent intent = new Intent(getParent(),
				// MeasurementsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				// Intent.FLAG_ACTIVITY_SINGLE_TOP);
				//
				// goNextHistory("Measurements", intent);
			} else if (nId == R.id.buttonBack) {
				onBackPressed();
			} else if (nId == R.id.buttonMain) {
				showMain();
			} else if (nId == R.id.buttonIngredient) {
				showIngredient();
			} else if (nId == R.id.buttonPreparation) {
				showPreparation();
			} else if (nId == R.id.buttonPrev) {
//				WebView view = (WebView) preparation
//						.findViewById(R.id.webPreparation);
				nCurDirectionIdx--;
				if (nCurDirectionIdx <= 0) {
					nCurDirectionIdx = 0;
					Button button = (Button) preparation
							.findViewById(R.id.buttonPrev);
					button.setVisibility(View.INVISIBLE);
				} else if (nCurDirectionIdx == strPreparation.length - 2) {
					Button button = (Button) preparation
							.findViewById(R.id.buttonNext);
					button.setVisibility(View.VISIBLE);
				}
//				view.loadData(strPreparation[nCurDirection], "text/html",
//						"UTF-8");
				if (gallery != null)
					gallery.setSelection(nCurDirectionIdx);
				TextView title = (TextView) preparation
						.findViewById(R.id.textIndex);
				title.setText("Step " + String.valueOf(nCurDirectionIdx + 1)
						+ " of " + String.valueOf(strPreparation.length));
			} else if (nId == R.id.buttonNext) {
//				WebView view = (WebView) preparation
//						.findViewById(R.id.webPreparation);
				nCurDirectionIdx++;
				if (nCurDirectionIdx >= strPreparation.length - 1) {
					nCurDirectionIdx = strPreparation.length - 1;
					Button button = (Button) preparation
							.findViewById(R.id.buttonNext);
					button.setVisibility(View.INVISIBLE);
				} else if (nCurDirectionIdx == 1) {
					Button button = (Button) preparation
							.findViewById(R.id.buttonPrev);
					button.setVisibility(View.VISIBLE);
				}
//				view.loadData(strPreparation[nCurDirection], "text/html",
//						"UTF-8");
				if (gallery != null)
					gallery.setSelection(nCurDirectionIdx);
				TextView title = (TextView) preparation
						.findViewById(R.id.textIndex);
				title.setText("Step " + String.valueOf(nCurDirectionIdx + 1)
						+ " of " + String.valueOf(strPreparation.length));
			} else if (nId == R.id.buttonSetTimer)
			{
				Button button = (Button) preparation.findViewById(R.id.buttonSetTimer);
				if (button.getText().toString().equals("Start"))
				{
					startCount();
					button.setText("Pause");
				}
				else if (button.getText().toString().equals("Pause")) {
					mCountDown.cancel();
					button.setText("Start");
				} else {
					EditText minutes = (EditText) preparation.findViewById(R.id.editMinutes);
					minutes.requestFocus();
					button.setText("Start");
				}
			} else if (nId == R.id.editMinutes)
			{
				Button button = (Button) preparation.findViewById(R.id.buttonSetTimer);
				button.setText("Start");
			} else if (nId == R.id.editSecond)
			{
				Button button = (Button) preparation.findViewById(R.id.buttonSetTimer);
				button.setText("Start");
			} 
		}
	};
	

	private class LoadDetailTask extends AsyncHttpTask<ResultDetail> {
		public LoadDetailTask(Context context, String requestUrl) {
			super(context, requestUrl, ResultDetail.class, true);
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
				channel = application.mainChannels.channels.get(mainIdx).value
						.get(subIdx).recipes;
				channel.results.get(recipeIdx).detail = result;
			} else {
				channel = application.searchChannel;
				channel.results.get(recipeIdx).detail = result;
			}			
			_result.detail = result;
			showRecipe();
		}

		@Override
		protected void onPostExecute(ResultDetail result) {
			super.onPostExecute(result);
		}
	}

	DialogInterface.OnClickListener retryDetailClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			LoadDetails();
		}

	};

	private void showRecipe() {
		contents = (LinearLayout) inflater
				.inflate(R.layout.recipe_detail, null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		content = (FrameLayout) contents.findViewById(R.id.frameContents);

		FrameLayout.LayoutParams layoutContent = new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		main = (LinearLayout) inflater.inflate(
				R.layout.recipe_detail_landscape, null);
		main.setLayoutParams(layoutContent);
		content.addView(main);
		initMain();

		ingredient = (LinearLayout) inflater.inflate(
				R.layout.recipe_detail_ingredient, null);
		ingredient.setLayoutParams(layoutContent);
		content.addView(ingredient);
		initIngredient();

		preparation = (LinearLayout) inflater.inflate(
				R.layout.recipe_detail_preparation, null);
		preparation.setLayoutParams(layoutContent);
		content.addView(preparation);
		initPraparation();

		showMain();

		Button button = (Button) contents.findViewById(R.id.buttonMain);
		button.setOnClickListener(mOnClickListener);
		button = (Button) contents.findViewById(R.id.buttonIngredient);
		button.setOnClickListener(mOnClickListener);
		button = (Button) contents.findViewById(R.id.buttonPreparation);
		button.setOnClickListener(mOnClickListener);

		refreshButtons(screen_main);

		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.removeAllViews();
		linearContents.addView(contents);
	}

	public void refreshButtons(int nType) {
		Button button = (Button) contents.findViewById(R.id.buttonMain);
		if (nType == screen_main) {
			button.setBackgroundResource(R.drawable.detail_button_s);
			button.setTextColor(Color.WHITE);
		} else {
			button.setBackgroundResource(R.drawable.detail_button_n);
			button.setTextColor(0xff603913);
		}

		button = (Button) contents.findViewById(R.id.buttonIngredient);
		if (nType == screen_ingred) {
			button.setBackgroundResource(R.drawable.detail_button_s);
			button.setTextColor(Color.WHITE);
		} else {
			button.setBackgroundResource(R.drawable.detail_button_n);
			button.setTextColor(0xff603913);
		}

		button = (Button) contents.findViewById(R.id.buttonPreparation);
		if (nType == screen_preparation) {
			button.setBackgroundResource(R.drawable.detail_button_s);
			button.setTextColor(Color.WHITE);
		} else {
			button.setBackgroundResource(R.drawable.detail_button_n);
			button.setTextColor(0xff603913);
		}

	}

	public void showMain() {
		preparation.setVisibility(View.GONE);
		ingredient.setVisibility(View.GONE);
		main.setVisibility(View.VISIBLE);

		refreshButtons(screen_main);
	}

	public void initMain() {
		View view = main.findViewById(R.id.linearImage);
		String strImgFile = ImageDownloader
				.getFileNamefromUrl(_result.main_picture);
		ImageView thumb = (ImageView) view.findViewById(R.id.imageThumb);
		Button button = (Button) main.findViewById(R.id.buttonFacebook);
		button.setOnClickListener(mOnClickListener);
		button = (Button) main.findViewById(R.id.buttonTwitter);
		button.setOnClickListener(mOnClickListener);
		if (strImgFile.length() > 0) {
			if (ImageDownloader.isCached(strImgFile)) {
				thumb.setImageBitmap(ImageDownloader.getCacheBitmap(strImgFile));
			} else {
				imgDownloader downloader = new imgDownloader(view, _result.main_picture, strImgFile);
				try {
					downloader.execute();
				} catch (RejectedExecutionException e) {
					e.printStackTrace();
				}
			}
		} else {
			thumb.setImageResource(R.drawable.fooddefaultpic);
		}
//		(new imgDownloader(view, _result.main_picture, strImgFile)).execute();
		
		if (_result.detail.node_data.field_servings_value != null &&
			!_result.detail.node_data.field_servings_value.equals("0")) {
			TextView textServingValue = (TextView) main
					.findViewById(R.id.textServingValue);
			textServingValue
					.setText(_result.detail.node_data.field_servings_value);
		} else {
			LinearLayout serveLayout = (LinearLayout) main
					.findViewById(R.id.linearServings);
			serveLayout.setVisibility(View.GONE);
		}

		if (_result.detail.node_data.field_prepration_time_value != null &&
				!_result.detail.node_data.field_prepration_time_value.equals("0")) {
			TextView textPreparationValue = (TextView) main
					.findViewById(R.id.textPreparationValue);
			textPreparationValue
					.setText(_result.detail.node_data.field_prepration_time_value
							+ " mins");
		} else {
			LinearLayout preparationLayout = (LinearLayout) main
					.findViewById(R.id.linearPreparationTime);
			preparationLayout.setVisibility(View.GONE);
		}

		if (_result.detail.node_data.field_cook_time_value != null &&
				!_result.detail.node_data.field_cook_time_value.equals("0")) {
			TextView textCookingValue = (TextView) main
					.findViewById(R.id.textCookingValue);
			textCookingValue
					.setText(_result.detail.node_data.field_cook_time_value
							+ " mins");
		} else {
			LinearLayout cookLayout = (LinearLayout) main
					.findViewById(R.id.linearCookingTime);
			cookLayout.setVisibility(View.GONE);
		}

		if (_result.detail.node_data.field_health_index_value != null &&
				!_result.detail.node_data.field_health_index_value.equals("")) {
			TextView textHealthValue = (TextView) main
					.findViewById(R.id.textHealthValue);
			textHealthValue
					.setText(_result.detail.node_data.field_health_index_value);
		} else {
			LinearLayout healthLayout = (LinearLayout) main
					.findViewById(R.id.linearHealthIndex);
			healthLayout.setVisibility(View.GONE);
		}
	}

	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
		}

	};

	public void showIngredient() {
		preparation.setVisibility(View.GONE);
		ingredient.setVisibility(View.VISIBLE);
		main.setVisibility(View.GONE);

		refreshButtons(screen_ingred);
	}

	public void initIngredient() {
		
		if (_result.detail.node_data.field_ingredients_value == null)	return;
		
		ListView list = (ListView) ingredient
				.findViewById(R.id.listIngredients);
		strIngredient = _result.detail.node_data.field_ingredients_value
				.split("\r\n");

		if (android.os.Build.VERSION.SDK_INT >= 10) {
			//list.setOverscrollFooter(null);
		}
		list.setAdapter(new CustomListAdapter(getParent()));
	}

	private class CustomListAdapter extends BaseAdapter {
		Context _context;

		public CustomListAdapter(Context c) {
			_context = c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return strIngredient.length;
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
			// WebView view = new WebView(_context);
			// WebSettings webSettings = view.getSettings();
			// webSettings.setTextSize(WebSettings.TextSize.LARGER);
			// view.loadData(strIngredient[position], "text/html", "UTF-8");
			TextView view = new TextView(_context);
			view.setText(strIngredient[position]);
			view.setTextColor(Color.BLACK);
			view.setPadding(10, 10, 10, 10);
			view.setTextSize(16.0f);

			return view;
		}
	}

	public void showPreparation() {
		preparation.setVisibility(View.VISIBLE);
		ingredient.setVisibility(View.GONE);
		main.setVisibility(View.GONE);

		refreshButtons(screen_preparation);
	}

	public void initPraparation() {
		Button button = (Button) preparation.findViewById(R.id.buttonPrev);
		button.setOnClickListener(mOnClickListener);
		button.setVisibility(View.INVISIBLE);
		button = (Button) preparation.findViewById(R.id.buttonNext);
		button.setOnClickListener(mOnClickListener);
		
		button = (Button) preparation.findViewById(R.id.buttonSetTimer);
		button.setOnClickListener(mOnClickListener);
		
		EditText minutes = (EditText) preparation.findViewById(R.id.editMinutes);
		minutes.setText("30");
		minutes.setOnClickListener(mOnClickListener);
		minutes.setOnEditorActionListener(this);
		
		EditText second = (EditText) preparation.findViewById(R.id.editSecond);
		second.setText("00");
		second.setOnClickListener(mOnClickListener);
		second.setOnEditorActionListener(this);
		
		if (_result.detail.node_data.field_directions_value == null)	return;

		_result.detail.node_data.field_directions_value = _result.detail.node_data.field_directions_value
				.replace("\r\n\r\n", "\r\n");
		strPreparation = _result.detail.node_data.field_directions_value
				.split("\r\n");

		TextView title = (TextView) preparation.findViewById(R.id.textIndex);
		title.setText("Step " + String.valueOf(nCurDirectionIdx + 1) + " of "
				+ String.valueOf(strPreparation.length));
		
		gallery = (Gallery) preparation.findViewById(R.id.galleryDirection);
		gallery.setAdapter(new recipeAdapter(getParent()));
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				nCurDirectionIdx = arg2;
				
				if (nCurDirectionIdx == 0)
				{
					Button button = (Button) preparation
							.findViewById(R.id.buttonPrev);
					button.setVisibility(View.INVISIBLE);
				} else if (nCurDirectionIdx == strPreparation.length-1)
				{
					Button button = (Button) preparation
							.findViewById(R.id.buttonNext);
					button.setVisibility(View.INVISIBLE);
				} else {
					Button button = (Button) preparation
							.findViewById(R.id.buttonPrev);
					button.setVisibility(View.VISIBLE);
					button = (Button) preparation
							.findViewById(R.id.buttonNext);
					button.setVisibility(View.VISIBLE);
				}
				
				TextView title = (TextView) preparation
						.findViewById(R.id.textIndex);
				title.setText("Step " + String.valueOf(nCurDirectionIdx + 1)
						+ " of " + String.valueOf(strPreparation.length));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
//		WebView view = (WebView) preparation.findViewById(R.id.webPreparation);
//		WebSettings webSettings = view.getSettings();
//		webSettings.setTextSize(WebSettings.TextSize.NORMAL);
//		view.loadData(strPreparation[nCurDirection], "text/html", "UTF-8");
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if ((actionId == EditorInfo.IME_ACTION_DONE) ||
				(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			hideKeyboard();
		}
		return false;
	}
	
	private void hideKeyboard()
	{
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		EditText minutes = (EditText) preparation.findViewById(R.id.editMinutes);
		EditText second = (EditText) preparation.findViewById(R.id.editSecond);
		mgr.hideSoftInputFromWindow(minutes.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(second.getWindowToken(), 0);
	}
	
	public class recipeAdapter extends BaseAdapter {
		Context _context;

		public recipeAdapter(Context context) {
			_context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return strPreparation.length;
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
			
			TextView view = new TextView(_context);
			view.setLinksClickable(true);
			view.setText(Html.fromHtml(strPreparation[position]));
			view.setMovementMethod(LinkMovementMethod.getInstance());
			view.setTextColor(Color.BLACK);
			view.setTextSize(16.0f);
			view.setPadding(20, 10, 20, 10);
//			WebView view = new WebView(_context);
//			WebSettings webSettings = view.getSettings();
//			webSettings.setTextSize(WebSettings.TextSize.NORMAL);
//			view.loadData(strPreparation[position], "text/html", "UTF-8");
			Gallery.LayoutParams layout = new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,
					android.widget.Gallery.LayoutParams.MATCH_PARENT);
			view.setLayoutParams(layout);
//			view.setHorizontalScrollBarEnabled(false);
//			view.setVerticalScrollBarEnabled(false);
//			view.setPadding(10, 10, 10, 10);
//			view.setFocusable(false);
//			view.setClickable(false);

			
			return view;
		}
	};
	
	public void startCount()
	{
		final EditText minutes = (EditText) preparation.findViewById(R.id.editMinutes);
		final EditText second = (EditText) preparation.findViewById(R.id.editSecond);
		
		int min = Integer.valueOf(minutes.getText().toString());
		int sec = Integer.valueOf(second.getText().toString());

		mElapse = min * 60 + sec;
		mCountDown = new CountDownTimer(mElapse * 1000, 1000){

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				mCountDown.cancel();
				mElapse = 0;
				minutes.setText("30");
				second.setText("00");
				Button button = (Button) preparation.findViewById(R.id.buttonSetTimer);
				button.setText("Set");
				Builder dlg = new AlertDialog.Builder(mInstance.getParent());
				dlg.setCancelable(false);
				dlg.setTitle("ifood.tv");
				dlg.setMessage("Timer Complete!");
				dlg.setPositiveButton("OK", null);
				dlg.show();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				mElapse--;
				
				minutes.setText(String.format("%02d", mElapse / 60));
				second.setText(String.format("%02d", mElapse % 60));
			}
			
		}.start();
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
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
}
