package com.myvideo.loveweclass;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.data.PageInfo;
import com.myvideo.loveweclass.ui.UIUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WebviewActivity extends NavigationActivity {
	LayoutInflater inflater;
	String url;
	String title;
	WebviewActivity mInstance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		mInstance = this;
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		inflater = LayoutInflater.from(this);
		setNavigation();
		setContents();
		loadUrl();
	}
	
	void setNavigation()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.top_text_bar, null);
		Button btnAction = (Button)contents.findViewById(R.id.buttonAction);
		btnAction.setVisibility(View.GONE);
		
		TextView titleText = (TextView)contents.findViewById(R.id.textTitle);
		titleText.setText(title);
		
		Button btnBack = (Button)contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(mOnClickListener);
		
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}
	
	private void loadUrl()
	{
		(new LoadUrlTask(getParent(), url)).execute();
	}
	
	private class LoadUrlTask extends AsyncHttpTask<PageInfo> {
		public LoadUrlTask(Context context, String requestUrl) {
			super(context, requestUrl, PageInfo.class, false);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			PageInfo result = (PageInfo) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			}
			
			showUrl(result);
		}

		@Override
		protected void onPostExecute(PageInfo result) {
			super.onPostExecute(result);
		}
	}
	
	private void showUrl(PageInfo info)
	{
		info.node_data = info.node_data.replace("\r\n", "<br>");
		WebView web = (WebView) findViewById(R.id.webView);
		web.loadDataWithBaseURL(null, info.node_data, "text/html", "UTF-8", null);
	}
	
	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			loadUrl();
		}

	};
	
	void setContents()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.web_view, null);
		
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.setMargins(10, 10, 10, 10);
		contents.setLayoutParams(layout);
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
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
}
