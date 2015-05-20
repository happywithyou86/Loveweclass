package com.myvideo.loveweclass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Categories;
import com.myvideo.loveweclass.data.Category;
import com.myvideo.loveweclass.data.LoginResult;
import com.myvideo.loveweclass.data.ResultCategories;
import com.myvideo.loveweclass.ui.UIUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiaryActivity extends NavigationActivity {
	LayoutInflater inflater;
	public static DiaryActivity gInstance;

	LoginResult loginResult = new LoginResult();
	Dialog dialog;
	public static String sid = "";
	private static final int LOGIN_DLG = 0;
	private static final int FACEBOOK_LOGIN = 1;
	private static final int TWITTER_LOGIN = 2;

	int nCurDlg = LOGIN_DLG;
	int nDlgCount = 0;

	String useremail = "";
	String userid = "";
	String password = "";
	String fullname = "";
	boolean autoLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		gInstance = this;
		inflater = LayoutInflater.from(this);
		setNavigation();
		setContents();
	}

	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText("Food Diary");
		Button btnAction = (Button) contents.findViewById(R.id.buttonAction);
		btnAction.setVisibility(View.GONE);

		Button btnBack = (Button) contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.GONE);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (nCurDlg) {
		case LOGIN_DLG: {
			dialog = new Dialog(gInstance.getParent());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			dialog.setContentView(R.layout.login_dialog);
			Button btnSign = (Button) dialog.findViewById(R.id.buttonSign);
			btnSign.setOnClickListener(mOnClickListener);
			TextView textClose = (TextView) dialog.findViewById(R.id.textClose);
			textClose.setOnClickListener(mOnClickListener);
			break;
		}
		default:
			break;
		}
		return dialog;
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
			} else if (nId == R.id.buttonIfoodTV) {
				nCurDlg = LOGIN_DLG;
				gInstance.showDialog(nDlgCount);
			}  else if (nId == R.id.textClose) {
				gInstance.dismissDialog(nDlgCount);
				nDlgCount++;
				dialog = null;
			} 
		}
	};

	void setContents() {
		SharedPreferences mySharedPreference = getSharedPreferences("ifoodtvlogin", Context.MODE_PRIVATE);
		userid = mySharedPreference.getString("username", "");
		password = mySharedPreference.getString("password", "");
		if (!userid.equalsIgnoreCase("") || !password.equalsIgnoreCase("")) {
			String requestUrl = WebServiceUrl.IFOODTV_URL;
			requestUrl += "user=" + userid;
			requestUrl += "&pass=" + password;

			autoLogin = true;
			loginResult.userauthtoken = mySharedPreference.getString("token", "");
			//showDiaryList(LOGIN_DLG);
			(new signTask(getParent(), requestUrl, LOGIN_DLG)).execute();
		} else {
			LinearLayout contents = (LinearLayout) inflater.inflate(R.layout.diary,
					null);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			contents.setLayoutParams(layout);

			Button btnIFoodTV = (Button) contents.findViewById(R.id.buttonIfoodTV);
			btnIFoodTV.setOnClickListener(mOnClickListener);
			Button btnFacebook = (Button) contents
					.findViewById(R.id.buttonFacebook);
			btnFacebook.setOnClickListener(mOnClickListener);
			Button btnTwitter = (Button) contents.findViewById(R.id.buttonTwitter);
			btnTwitter.setOnClickListener(mOnClickListener);

			LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
			linearContents.addView(contents);	
		}
	}

	public void login(int nType) {
		String requestUrl = "";
		if (nType == LOGIN_DLG) {
			requestUrl = WebServiceUrl.IFOODTV_URL;
			EditText editUserName = (EditText) dialog
					.findViewById(R.id.editUsername);
			userid = editUserName.getText().toString();
			requestUrl += "user=" + userid;
			EditText editPassword = (EditText) dialog
					.findViewById(R.id.editPassword);
			password = editPassword.getText().toString();
			requestUrl += "&pass=" + password;

			(new signTask(getParent(), requestUrl, LOGIN_DLG)).execute();
		} else if (nType == FACEBOOK_LOGIN) {
			requestUrl = WebServiceUrl.THIRD_PARTY_URL;
			requestUrl += "id=" + userid;
			requestUrl += "&service=FACEBOOK";
			requestUrl += "&email=" + useremail;
			requestUrl += "&name=" + fullname;
			requestUrl += "&auth-token=1212551";

			(new signTask(getParent(), requestUrl, FACEBOOK_LOGIN)).execute();
		} else if (nType == TWITTER_LOGIN) {
			requestUrl = WebServiceUrl.THIRD_PARTY_URL;
			requestUrl += "id=" + userid;
			requestUrl += "&service=TWITTER";
			requestUrl += "&email=" + useremail;
			requestUrl += "&name=" + fullname;
			requestUrl += "&auth-token=1212551";

			(new signTask(getParent(), requestUrl, TWITTER_LOGIN)).execute();
		}

	}

	private class signTask extends AsyncHttpTask<String> {
		int nType;

		public signTask(Context context, String requestUrl, int nType) {
			super(context, requestUrl, String.class, true);
			this.nType = nType;
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			String result = (String) objects[0];
			if (result == null || result.equalsIgnoreCase("")) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";

				if (nType == LOGIN_DLG) {
					UIUtils.NetworkErrorMessage(gInstance.getParent(), msg,
							UIUtils.MSG_OK_CANCEL, retryClicker);
				} else if (nType == FACEBOOK_LOGIN) {
					UIUtils.NetworkErrorMessage(gInstance.getParent(), msg,
							UIUtils.MSG_OK_CANCEL, retryClicker1);
				} else if (nType == TWITTER_LOGIN) {
					UIUtils.NetworkErrorMessage(gInstance.getParent(), msg,
							UIUtils.MSG_OK_CANCEL, retryClicker2);
				}
				return;
			}

			parseLoginResult(result);

			if (!loginResult.status.equalsIgnoreCase("ok")) {
				String msg = "Invalid username/password conbination \n or user has been blocked.";
				//String msg = "Failed to login\n description"
					//	+ loginResult.data.description;
				UIUtils.NetworkErrorMessage(gInstance.getParent(), msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}
			
			if (nType == LOGIN_DLG) {
				SharedPreferences mySharedPreference = getSharedPreferences("ifoodtvlogin", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = mySharedPreference.edit();
				editor.putString("username", userid);
				editor.putString("password", password);
				editor.putString("token", loginResult.userauthtoken);
				editor.commit();
			} else {
				SharedPreferences mySharedPreference = getSharedPreferences("ifoodtvlogin", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = mySharedPreference.edit();
				editor.putString("username", "");
				editor.putString("password", "");
				editor.putString("token", "");
				editor.commit();
			}

			if (nType == TWITTER_LOGIN)
				autoLogin = true;
			showDiaryList(nType);
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
			login(LOGIN_DLG);
		}

	};

	DialogInterface.OnClickListener retryClicker1 = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			login(FACEBOOK_LOGIN);
		}

	};

	DialogInterface.OnClickListener retryClicker2 = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			login(TWITTER_LOGIN);
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

	private void showDiaryList(int nType) {
		if (autoLogin == false) {
			gInstance.dismissDialog(nDlgCount);
			nDlgCount++;
		}
		dialog = null;

		sid = loginResult.userauthtoken;
		Intent intent = new Intent(getParent(), DiaryListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		goNextHistory("DiaryListActivity", intent);
	}

	public void parseLoginResult(String result) {
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
		Categories cats;

		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = null;
			try {
				value = json.getString(key);
				if (key.equals("status")) {
					loginResult.status = value;
					continue;
				}

				if (key.equals("user-auth-token")) {
					loginResult.userauthtoken = value;
					continue;
				}

				if (key.equals("user-id")) {
					loginResult.userid = value;
					continue;
				}

				if (key.equals("type")) {
					loginResult.type = value;
					continue;
				}

				if (key.equals("data")) {
					JSONObject errJson = new JSONObject(value);
					errIter = errJson.keys();
					while (errIter.hasNext()) {
						String errKey = (String) errIter.next();
						String errValue = errJson.getString(errKey);

						if (errKey.equals("code")) {
							loginResult.data.code = errValue;
							continue;
						}

						if (errKey.equals("description")) {
							loginResult.data.description = errValue;
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
