package com.myvideo.loveweclass;

import java.io.File;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.PictureDetail;
import com.myvideo.loveweclass.ui.UIUtils;

public class DiaryDetailActivity extends Activity implements OnClickListener {

	LayoutInflater inflater;
	DiaryDetailActivity mInstance;

	String picId;
	PictureDetail _result;

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
		picId = intent.getStringExtra("picid");
		setNavigation();
		setContents();

		loadDetails();
	}

	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_text_bar, null);
		TextView titleText = (TextView) contents.findViewById(R.id.textTitle);
		titleText.setText("");
		Button btnAction = (Button) contents.findViewById(R.id.buttonAction);
		btnAction.setText("Edit");
		btnAction.setOnClickListener(this);
		btnAction.setVisibility(View.VISIBLE);

		Button btnBack = (Button) contents.findViewById(R.id.buttonBack);
		btnBack.setText("Back");
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

		if (nId == R.id.buttonAction) {
			showEdit();
		} else if (nId == R.id.buttonBack) {
			finish();
		} else if (nId == R.id.imageThumb) {
			
		} 
	}
	
	private void showEdit()
	{
		if (_result == null)	return;
		
		Intent intent = new Intent(this, DiaryEditActivity.class);
		
		DiaryEditActivity._detail = _result.node_data;
		intent.putExtra("sid", DiaryActivity.sid);
		intent.putExtra("picid", picId);
		intent.putExtra("edit", true);
		String strImgFile = ImageDownloader
			.getFileNamefromUrl(_result.node_data.image_path);

		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = sd + "/ifoodtv/temp_pic/" + strImgFile;
		intent.putExtra("path", path);
		startActivity(intent);
		DiaryEditActivity.bGoDetail = true;
		finish();
	}

	void setContents() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.diary_detail, null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}

	private void loadDetails() {
		String requestUrl = WebServiceUrl.PIC_DETAILS + picId;

		Log.v("LoveWithClass", requestUrl);
		UIUtils.getDisplayMetricsDensity(this);

		(new LoadDetailsTask(this, requestUrl)).execute();
	}

	private class LoadDetailsTask extends AsyncHttpTask<PictureDetail> {
		public LoadDetailsTask(Context context, String requestUrl) {
			super(context, requestUrl, PictureDetail.class, false);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			PictureDetail result = (PictureDetail) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance, msg,
						UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			}

			_result = result;
			showDetails();
		}

		@Override
		protected void onPostExecute(PictureDetail result) {
			super.onPostExecute(result);
		}
	}

	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			loadDetails();
		}

	};

	private void showDetails() {
		
		if (_result.node_data == null)	return;
		
		ImageView imageThumb = (ImageView) findViewById(R.id.imageThumb);
		String strImgFile = ImageDownloader
				.getFileNamefromUrl(_result.node_data.image_path);

		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = sd + "/ifoodtv/temp_pic/" + strImgFile;

		ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setVisibility(View.INVISIBLE);

		File file = new File(path);
		if (file.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap mPhotoBitmap = BitmapFactory.decodeFile(path, options);
			//Bitmap resized = Bitmap.createScaledBitmap(src, 100, 100, true);
			imageThumb.setImageBitmap(mPhotoBitmap);
		} else {
			FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
			if (strImgFile.length() > 0) {
				if (ImageDownloader.isCached(strImgFile)) {
					imageThumb.setImageBitmap(ImageDownloader
									.getCacheBitmap(strImgFile));
				} else {
					progress.setVisibility(View.VISIBLE);
					imgDownloader downloader = new imgDownloader(frameLayout,
							_result.node_data.image_path, strImgFile);
					try {
						downloader.execute();
					} catch (RejectedExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		TextView textTitle = (TextView) findViewById(R.id.textTitle);
		textTitle.setText(_result.node_data.title);
		TextView textType = (TextView) findViewById(R.id.textType);
		textType.setText(_result.node_data.diary_type);
		TextView textLocation = (TextView) findViewById(R.id.textLocation);
		textLocation.setText(_result.node_data.location);
		WebView webComment = (WebView) findViewById(R.id.webComment);
		String strContent = _result.node_data.comment.replace("\r\n", "<br>");
		strContent = "<body leftmargin=0 style='padding-left:0;margin-left:0;'>" + strContent + "</body>";
		webComment.loadData(strContent, "text/html", "UTF-8");
		TextView textDate = (TextView) findViewById(R.id.textDate);
		textDate.setText(_result.node_data.created);
		
		Button btnFacebook = (Button) findViewById(R.id.buttonFacebook);
		btnFacebook.setOnClickListener(this);
		Button btnTwitter = (Button) findViewById(R.id.buttonTwitter);
		btnTwitter.setOnClickListener(this);
	}
	
	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
		}

	};
	
}
