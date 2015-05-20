package com.myvideo.loveweclass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.ChannelActivity.imgDownloader;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Categories;
import com.myvideo.loveweclass.data.Category;
import com.myvideo.loveweclass.data.PictureData;
import com.myvideo.loveweclass.data.ResultCategories;
import com.myvideo.loveweclass.data.ResultPicList;
import com.myvideo.loveweclass.ui.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class DiaryListActivity extends NavigationActivity {
	LayoutInflater inflater;
	public static DiaryListActivity mInstance;

	ResultPicList _result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);

		mInstance = this;
		inflater = LayoutInflater.from(this);
		setNavigation();
		setContents();

		loadPicList();
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

	void setContents() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.diary_list, null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);

		Button btnTakePicture = (Button) contents.findViewById(R.id.buttonTake);
		btnTakePicture.setOnClickListener(mOnClickListener);

		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}

	public void loadPicList() {
		String url = WebServiceUrl.PIC_LIST + DiaryActivity.sid;
		(new loadPicListTask(getParent(), url)).execute();
	}

	private class loadPicListTask extends AsyncHttpTask<String> {
		public loadPicListTask(Context context, String requestUrl) {
			super(context, requestUrl, String.class, true);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			String result = (String) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				// UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
				// UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			}

			parsePicList(result);

			if (!_result.status.equalsIgnoreCase("ok")) {
				String msg = "Failed to get the picture list \n description"
						+ _result.data.description;
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}

			showPicList();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	private void showPicList() {
		TableLayout picTable = (TableLayout) findViewById(R.id.tableLayout);

		picTable.removeAllViews();
		TableRow tr = null;
		for (int i = 0; i < _result.node_data.size(); i++) {
			if (i % 2 == 0) {
				tr = new TableRow(this.getParent());
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tr.setGravity(Gravity.TOP | Gravity.LEFT);
			}
			
			FrameLayout contents = (FrameLayout) inflater.inflate(
					R.layout.pic_thumb, null);
			
			ImageView imageThumb = (ImageView) contents.findViewById(R.id.imageThumb);
			imageThumb.setTag(i);
			imageThumb.setScaleType(ScaleType.CENTER_CROP);
			imageThumb.setOnClickListener(mOnClickListener);
			String strImgFile = ImageDownloader
					.getFileNamefromUrl(_result.node_data.get(i).image_path);
			
			String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
			String path = sd + "/ifoodtv/temp_pic/" + strImgFile;
			
			ProgressBar progress = (ProgressBar) contents.findViewById(R.id.progressBar);
			progress.setVisibility(View.INVISIBLE);
			
			File file = new File(path);
			if (file.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				Bitmap mPhotoBitmap = BitmapFactory.decodeFile(path, options);
				//Bitmap resized = Bitmap.createScaledBitmap(src, 100, 100, true);
				imageThumb.setImageBitmap(mPhotoBitmap);
			} else {
				if (strImgFile.length() > 0) {
					if (ImageDownloader.isCached(strImgFile)) {
						imageThumb.setImageBitmap(ImageDownloader
										.getCacheBitmap(strImgFile));
					} else {
						progress.setVisibility(View.VISIBLE);
						imgDownloader downloader = new imgDownloader(contents,
								_result.node_data.get(i).image_path, strImgFile);
						try {
							downloader.execute();
						} catch (RejectedExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			}

			TableRow.LayoutParams param = new TableRow.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			if (i % 2 == 1)
				param.setMargins(5, 5, 0, 5);
			else
				param.setMargins(5, 5, 5, 5);
			tr.addView(contents, param);

			if (i % 2 == 1 || (i % 2 == 0 && i == _result.node_data.size() - 1)) {
				TableLayout.LayoutParams layout = new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				picTable.addView(tr, layout);
			}
		}
	}
	
	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
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
			} else if (nId == R.id.buttonTake) {
				Intent intent = new Intent(mInstance.getParent(),
						CameraActivity.class);
				mInstance.startActivity(intent);
			} else if (nId == R.id.imageThumb) {
				int nTag = (Integer)v.getTag();
							
				Intent intent = new Intent(mInstance, DiaryDetailActivity.class);
				intent.putExtra("picid", _result.node_data.get(nTag).id);
				mInstance.startActivity(intent);
			}
		}
	};

	public void parsePicList(String result) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (json == null)
			return;

		_result = new ResultPicList();

		Iterator iter = json.keys();
		Iterator nodeIter = null, dataIter = null;
		PictureData data;

		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = null;
			try {
				value = json.getString(key);
				if (key.equals("status")) {
					_result.status = value;
					continue;
				}

				if (key.equals("type")) {
					_result.type = value;
					continue;
				}

				if (key.equals("params")) {
					_result.params = value;
					continue;
				}

				if (key.equals("node_data")) {
					JSONObject nodeJson = new JSONObject(value);
					nodeIter = nodeJson.keys();
					while (nodeIter.hasNext()) {
						String nodeKey = (String) nodeIter.next();
						String nodeValue = nodeJson.getString(nodeKey);
						data = new PictureData();
						data.id = nodeKey;

						JSONObject dataJson = new JSONObject(nodeValue);
						dataIter = dataJson.keys();

						while (dataIter.hasNext()) {
							String dataKey = (String) dataIter.next();
							String dataValue = dataJson.getString(dataKey);

							if (dataKey.equals("title")) {
								data.title = dataValue;
								continue;
							}

							if (dataKey.equals("location")) {
								data.location = dataValue;
								continue;
							}

							if (dataKey.equals("comment")) {
								data.comment = dataValue;
								continue;
							}

							if (dataKey.equals("latitude")) {
								data.latitude = dataValue;
								continue;
							}

							if (dataKey.equals("longitude")) {
								data.longitude = dataValue;
								continue;
							}

							if (dataKey.equals("diary_type")) {
								data.diary_type = dataValue;
								continue;
							}

							if (dataKey.equals("image_path")) {
								data.image_path = dataValue;
								continue;
							}

							if (dataKey.equals("website_url")) {
								data.website_url = dataValue;
								continue;
							}

							if (dataKey.equals("created")) {
								data.created = dataValue;
								continue;
							}
						}

						_result.node_data.add(data);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
