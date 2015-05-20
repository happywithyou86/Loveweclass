package com.myvideo.loveweclass;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.LocationPoint;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Categories;
import com.myvideo.loveweclass.data.PictureData;
import com.myvideo.loveweclass.data.PictureDetail;
import com.myvideo.loveweclass.data.UploadResult;
import com.myvideo.loveweclass.ui.UIUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiaryEditActivity extends Activity implements OnClickListener {
	LayoutInflater inflater;
	DiaryEditActivity mInstance;
	boolean bIsEdit = true;
	ImageView checkFood;
	ImageView checkCook;
	ImageView checkSpot;
	
	private static final int DIARY_FOOD = 0;
	private static final int DIARY_COOK = 1;
	private static final int DIARY_SPOT = 2;
	
	int nDiaryType = DIARY_FOOD;
	String strDiaryType = "Food Diary";
	String sid;
	String picId;
	String filepath;
	
	UploadResult uploadResult = new UploadResult();
	
	public static PictureData _detail;
	public static boolean bGoDetail = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);
		
		mInstance = this;
		inflater = LayoutInflater.from(this);
		
		Intent intent = getIntent();
		sid = intent.getStringExtra("sid");
		picId = intent.getStringExtra("picid");
		filepath = intent.getStringExtra("path");
		bIsEdit = intent.getBooleanExtra("edit", false);
		setNavigation();
		setContents();
	}
	
	void setNavigation()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.top_text_bar, null);
		TextView titleText = (TextView)contents.findViewById(R.id.textTitle);
		titleText.setText("Details");
		Button btnAction = (Button)contents.findViewById(R.id.buttonAction);
		btnAction.setText("Post");
		btnAction.setOnClickListener(this);
		btnAction.setVisibility(View.VISIBLE);
		
		Button btnBack = (Button)contents.findViewById(R.id.buttonBack);
		btnBack.setText("Cancel");
		btnBack.setOnClickListener(this);
		btnBack.setVisibility(View.VISIBLE);
		
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nId = v.getId();
		
		if (nId == R.id.buttonAction)
		{
			hideKayboard();
			post();
		} else if (nId == R.id.buttonBack) {
			if (bGoDetail) {
				Intent intent = new Intent(mInstance, DiaryDetailActivity.class);
				intent.putExtra("picid", picId);
				mInstance.startActivity(intent);
			}
			bGoDetail = false;
			_detail = null;
			finish();

		} else if (nId == R.id.linearFood) {
			setType(DIARY_FOOD);
		} else if (nId == R.id.linearCook) {
			setType(DIARY_COOK);
		} else if (nId == R.id.linearSpot) {
			setType(DIARY_SPOT);
		} else if (nId == R.id.buttonDelete) {
			deletePic();
		}
	}
	
	private void deletePic()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("LoveWithClass");
		dialog.setMessage("Are you sure you want to delete this item?");
		dialog.setPositiveButton("No", null);
		dialog.setNegativeButton("Yes",
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String url = WebServiceUrl.DELETE_URL + picId;
				url += "&sid=" + sid;
				(new deleteTask(mInstance, url)).execute();

				dialog.cancel();
			}
		});
		
		dialog.show();
	}
	
	private void post()
	{
		EditText name = (EditText) findViewById(R.id.editName);
		String strName = name.getText().toString();
		
		if (strName.equals("")) {
			Builder dlg = new AlertDialog.Builder(mInstance);
			dlg.setCancelable(false);
			dlg.setTitle("ifood.tv");
			dlg.setMessage("Please enter a Name!");
			
			dlg.setPositiveButton("OK", null);
			dlg.show();
			return;
		}
		
		String strLocation = ((EditText) findViewById(R.id.editLocation)).getText().toString();
		String strComments = ((EditText) findViewById(R.id.editComments)).getText().toString();
		
		LocationPoint location = IfoodTVApplication.gInstance.getLastKnownLocation();
		
		
		String[] param = new String[14];
		param[0] = "sid";
		param[1] = sid;
		param[2] = "title";
		param[3] = strName;
		param[4] = "location";
		param[5] = strLocation;
		param[6] = "comment";
		param[7] = strComments;
		param[8] = "latitude";
		param[9] = String.valueOf(location.Latitude);
		param[10] = "longitude";
		param[11] = String.valueOf(location.Longitude);
		param[12] = "diary_type";
		param[13] = strDiaryType;
		
		if (_detail != null) {
			String url = WebServiceUrl.EDIT_DETAILS + picId;
			url += "&sid=" + sid;
			(new postTask(this, url, filepath, param)).execute();
		}
		else
			(new postTask(this, WebServiceUrl.UPLOAD_URL, filepath, param)).execute();
	}
	
	void setContents()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.diary_edit, null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		Button btnDelete = (Button) contents.findViewById(R.id.buttonDelete);
		btnDelete.setOnClickListener(this);
		if (!bIsEdit) {
			btnDelete.setVisibility(View.GONE);
		}
		checkFood = (ImageView) contents.findViewById(R.id.imageFoodCheck);
		checkCook = (ImageView) contents.findViewById(R.id.imageCookCheck);
		checkSpot = (ImageView) contents.findViewById(R.id.imageSpotCheck);
		
		LinearLayout linearFood = (LinearLayout) contents.findViewById(R.id.linearFood);
		linearFood.setOnClickListener(this);
		LinearLayout linearCook = (LinearLayout) contents.findViewById(R.id.linearCook);
		linearCook.setOnClickListener(this);
		LinearLayout linearSpot = (LinearLayout) contents.findViewById(R.id.linearSpot);
		linearSpot.setOnClickListener(this);
		
		if (_detail != null) {
			EditText editName = (EditText) contents.findViewById(R.id.editName);
			editName.setText(_detail.title);
			EditText editLocation = (EditText) contents.findViewById(R.id.editLocation);
			editLocation.setText(_detail.location);
			EditText editComments = (EditText) contents.findViewById(R.id.editComments);
			editComments.setText(_detail.comment);
			if (_detail.diary_type.equalsIgnoreCase("Food Diary")) {
				setType(DIARY_FOOD);
			} else if (_detail.diary_type.equalsIgnoreCase("Cooking Diary")) {
				setType(DIARY_COOK);
			} else if (_detail.diary_type.equalsIgnoreCase("Spotting Diary")) {
				setType(DIARY_SPOT);
			}
		} else {
			setType(DIARY_FOOD);
		}
		
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}
	
	private class postTask extends AsyncHttpTask<String> {
		public postTask(Context context, String requestUrl, String filepath,
				String... postDataPair) {
			super(context, requestUrl, String.class, filepath, postDataPair);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			String result = (String) objects[0];
			if (result == null) {
				UIUtils.NetworkErrorMessage(mInstance, "Posting failed by network", UIUtils.MSG_OK_CANCEL,
						retryClicker);
				return;
			}

			parseUploadResult(result);
			
			if (!uploadResult.status.equalsIgnoreCase("ok")) {
				//String msg = "Failed to login\n description" + uploadResult.data.description;
				String msg = "Invalid username/password conbination \n or user has been blocked.";
				UIUtils.NetworkErrorMessage(mInstance, msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}
			
			if (uploadResult.node_data.node_id == null) {
				uploadResult.node_data.node_id = picId;
			}
			
			showDetail();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	public class deleteTask extends AsyncHttpTask<String> {
		public deleteTask(Context context, String requestUrl) {
			super(context, requestUrl, String.class, true);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			String result = (String) objects[0];
			if (result == null) {
				UIUtils.NetworkErrorMessage(mInstance, "Posting failed by network", UIUtils.MSG_OK_CANCEL,
						retryClicker);
				return;
			}

			parseUploadResult(result);
			
			if (!uploadResult.status.equalsIgnoreCase("ok")) {
				String msg = "Failed to delete the picture.\n description" + uploadResult.data.description;
				UIUtils.NetworkErrorMessage(mInstance, msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}
			
			if (uploadResult.type.equalsIgnoreCase("pic-delete")) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mInstance);
				dialog.setTitle("LoveWithClass");
				dialog.setMessage("Food Item Deleted");
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
						mInstance.finish();
						DiaryListActivity.mInstance.loadPicList();
					}
				});
				dialog.show();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	private void showDetail() {
		DiaryListActivity.mInstance.loadPicList();
		
		Intent intent = new Intent(mInstance, DiaryDetailActivity.class);
		intent.putExtra("picid", uploadResult.node_data.node_id);
		mInstance.startActivity(intent);
		_detail = null;
		finish();
	}
	
	private void hideKayboard()
	{
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		EditText name = (EditText) findViewById(R.id.editName);
		mgr.hideSoftInputFromWindow(name.getWindowToken(), 0);
		EditText location = (EditText) findViewById(R.id.editLocation);
		mgr.hideSoftInputFromWindow(location.getWindowToken(), 0);
		EditText comment = (EditText) findViewById(R.id.editComments);
		mgr.hideSoftInputFromWindow(comment.getWindowToken(), 0);
	}
	
	
	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			post();
		}

	};
	
	private void setType(int nType)
	{
		checkFood.setBackgroundResource(0);
		checkCook.setBackgroundResource(0);
		checkSpot.setBackgroundResource(0);
		
		switch(nType)
		{
		case DIARY_FOOD:
			strDiaryType = "Food Diary";
			checkFood.setBackgroundResource(R.drawable.check);
			break;
		case DIARY_COOK:
			strDiaryType = "Cooking Diary";
			checkCook.setBackgroundResource(R.drawable.check);
			break;
		case DIARY_SPOT:
			strDiaryType = "Spotting Diary";
			checkSpot.setBackgroundResource(R.drawable.check);
			break;
		}
	}
	
	public void parseUploadResult(String result) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (json == null)
			return;

	    Iterator iter = json.keys();
	    Iterator errIter = null;
	    
	    while(iter.hasNext()){
	        String key = (String)iter.next();
	        String value = null;
			try {
				value = json.getString(key);
				if (key.equals("status"))
				{
					uploadResult.status = value;
					continue;
				}
				
				if (key.equals("params")) {
					uploadResult.params = value;
					continue;
				}
				
				if (key.equals("type"))
				{
					uploadResult.type = value;
					continue;
				}
				
				if (key.equals("data")) {
					JSONObject errJson = new JSONObject(value);
					errIter = errJson.keys();
					while(errIter.hasNext()) {
						String errKey = (String)errIter.next();
						String errValue = errJson.getString(errKey);
						
						if (errKey.equals("code")) {
							uploadResult.data.code = errValue;
							continue;
						}
						
						if (errKey.equals("description")) {
							uploadResult.data.description = errValue;
							continue;
						}
					}
				}

				if (key.equals("node_data")) {
					JSONObject errJson = new JSONObject(value);
					errIter = errJson.keys();
					while(errIter.hasNext()) {
						String errKey = (String)errIter.next();
						String errValue = errJson.getString(errKey);
						
						if (errKey.equals("node-id")) {
							uploadResult.node_data.node_id = errValue;
							continue;
						}
						
						if (errKey.equals("message")) {
							uploadResult.node_data.message = errValue;
							continue;
						}
						
						if (errKey.equals("website_url")) {
							uploadResult.node_data.website_url = errValue;
							continue;
						}
						
						if (errKey.equals("image_url")) {
							uploadResult.node_data.image_url = errValue;
							continue;
						}
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
