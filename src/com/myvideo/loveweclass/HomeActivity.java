package com.myvideo.loveweclass;

import java.util.concurrent.RejectedExecutionException;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.AsyncHttpTask;
import com.myvideo.loveweclass.core.ImageDownloader;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.ResultChannels;
import com.myvideo.loveweclass.data.SubChannel;
import com.myvideo.loveweclass.ui.UIUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends NavigationActivity {

	LayoutInflater inflater;
	private final static int GRID_COLS = 4;
	private final static int GRID_ROWS = 4;

	HomeActivity mInstance = null;

	int nTotalPages = 0;
	Panels[] arrPanels = null;
	int[] channelPages = null;
	ResultChannels _channels = null;

	TextView textPages = null;
	int nCurPage = 0;

	Gallery gallery;
	Button prev, next;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		inflater = LayoutInflater.from(this);
		mInstance = this;
		setNavigation();
		LoadChannels();
	}

	void setNavigation() {
		LinearLayout contents = (LinearLayout) inflater.inflate(
				R.layout.top_image_bar, null);
		ImageView titleImage = (ImageView) contents
				.findViewById(R.id.imageTitle);
		titleImage.setImageResource(R.drawable.nb_ifoodicon);
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
			} else if (nId == R.id.buttonPrev) {
				gallery.setSelection(nCurPage - 1, true);
			} else if (nId == R.id.buttonNext) {
				gallery.setSelection(nCurPage + 1, true);
			}
		}
	};

	public void LoadChannels() {
		String requestUrl = WebServiceUrl.CHANNELS_URL;
		
		requestUrl += IfoodTVApplication.gInstance.getUniqueId();

		Log.v("LoveWithClass", requestUrl);
		UIUtils.getDisplayMetricsDensity(this);

		//(new LoadChannelsTask(getParent(), requestUrl)).execute();
	}

	private class LoadChannelsTask extends AsyncHttpTask<ResultChannels> {
		public LoadChannelsTask(Context context, String requestUrl) {
			super(context, requestUrl, ResultChannels.class, true);
		}

		@Override
		protected void onProgressUpdate(Object... objects) {
			super.onProgressUpdate(objects);

			ResultChannels result = (ResultChannels) objects[0];
			if (result == null) {
				String msg = "Network error has occured. Please check the network status of your phone and retry";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_OK_CANCEL, retryClicker);
				return;
			} else if (!result.status.equals("ok")) {
				String msg = "status = fail";
				UIUtils.NetworkErrorMessage(mInstance.getParent(), msg,
						UIUtils.MSG_CLOSE, null);
				return;
			}

			IfoodTVApplication application = (IfoodTVApplication) getApplication();
			application.setResultChannels(result);

			_channels = result;
			setContents();
		}

		@Override
		protected void onPostExecute(ResultChannels result) {
			super.onPostExecute(result);
		}
	}

	DialogInterface.OnClickListener retryClicker = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			//LoadChannels();
		}

	};

	void setContents() {

		int nMaxSize = GRID_COLS * GRID_ROWS;
		nTotalPages = _channels.channels.size();
		channelPages = new int[nTotalPages];
		int temp = 0;
		for (int i = 0; i < channelPages.length; i++) {
			temp = _channels.channels.get(i).value.size();
			channelPages[i] += (temp - 1) / nMaxSize + 1;
			nTotalPages += channelPages[i] - 1;
		}
		
		arrPanels = new Panels[nTotalPages];
		int nCurMainIdx = 0;
		int nCurSubIdx = 0;
		int nPagesSum = channelPages[nCurMainIdx];
		for (int i = 0; i < nTotalPages; i++) {
			arrPanels[i] = new Panels();
			if (nPagesSum - 1 > i) {
				arrPanels[i].nCount = nMaxSize;
				arrPanels[i].mainsIdx = nCurMainIdx;
				arrPanels[i].subsIdx = nMaxSize * nCurSubIdx++;
			} else {
				arrPanels[i].nCount = _channels.channels.get(nCurMainIdx).value
						.size() % nMaxSize;
				arrPanels[i].mainsIdx = nCurMainIdx++;
				arrPanels[i].subsIdx = nMaxSize * nCurSubIdx;
				nCurSubIdx = 0;
				if (nCurMainIdx < channelPages.length - 1)
					nPagesSum += channelPages[nCurMainIdx];
			}
		}

		LinearLayout contents = (LinearLayout) inflater.inflate(R.layout.home,
				null);
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		
		prev = (Button) contents.findViewById(R.id.buttonPrev);
		prev.setOnClickListener(mOnClickListener);
		next = (Button) contents.findViewById(R.id.buttonNext);
		next.setOnClickListener(mOnClickListener);
		gallery = (Gallery) contents.findViewById(R.id.galleryPage);
		textPages = (TextView) contents.findViewById(R.id.textPages);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Do something with position
				textPages.setText(String.valueOf(position + 1) + " / "
						+ String.valueOf(nTotalPages));
				nCurPage = position;
				if (nCurPage == 0) {
					prev.setVisibility(View.INVISIBLE);
				} else if (nCurPage == nTotalPages - 1) {
					next.setVisibility(View.INVISIBLE);
				} else {
					prev.setVisibility(View.VISIBLE);
					next.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		gallery.setAdapter(new layoutAdapter(this.getParent()));
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}

	public class layoutAdapter extends BaseAdapter {

		Context _context;

		public layoutAdapter(Context c) {
			_context = c;
		}

		public int getCount() {
			return nTotalPages;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout contents = (LinearLayout) inflater.inflate(
					R.layout.subchannel, null);

			TextView textView = (TextView) contents
					.findViewById(R.id.textSubChannel);
			textView.setText(_channels.channels
					.get(arrPanels[position].mainsIdx).name);
			TableLayout table = (TableLayout) contents
					.findViewById(R.id.tablePanel);

			TableRow tr = null;
			SubChannel channel = null;
			for (int i = 0; i < arrPanels[position].nCount; i++) {
				if (i % GRID_ROWS == 0) {
					tr = new TableRow(_context);
					tr.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					tr.setGravity(Gravity.TOP | Gravity.LEFT);
				}
				
				LinearLayout cell = new LinearLayout(_context);
				cell.setGravity(Gravity.CENTER_HORIZONTAL);
				cell.setOrientation(LinearLayout.VERTICAL);

				channel = _channels.channels.get(arrPanels[position].mainsIdx).value
						.get(arrPanels[position].subsIdx + i);

				String strImgFile = ImageDownloader
						.getFileNamefromUrl(channel.image);
				FrameLayout thumbLayout = (FrameLayout) inflater.inflate(
						R.layout.image_thumb, null);
				ImageView image = (ImageView) thumbLayout.findViewById(R.id.imageThumb);
				image.setImageResource(R.drawable.globeicon);
				ProgressBar progress = (ProgressBar) thumbLayout.findViewById(R.id.progressBar);
				progress.setVisibility(View.INVISIBLE);
				if (strImgFile.length() > 0) {
					if (ImageDownloader.isCached(strImgFile)) {
						image.setImageBitmap(ImageDownloader.getCacheBitmap(strImgFile));
					} else {
						imgDownloader downloader = new imgDownloader(thumbLayout, channel.image, strImgFile);
						try {
							downloader.execute();
						} catch (RejectedExecutionException e) {
							e.printStackTrace();
						}
					}
				}
//				(new imgDownloader(thumbLayout, channel.image, strImgFile))
//						.execute();
				cell.addView(thumbLayout, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				TextView channelTitle = new TextView(_context);
				channelTitle.setText(channel.name);
				channelTitle.setTextSize(10.0f);
				channelTitle.setTextColor(Color.BLACK);
				channelTitle.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				channelTitle.setLines(2);
//				channelTitle.setPadding(5, 10, 5, 0);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				param.setMargins(5, 10, 5, 0);
				cell.addView(channelTitle, param);

				cell.setId(100 + i);
				cell.setOnClickListener(mClickListener);
				
				tr.addView(cell, new TableRow.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1));
				if (i % GRID_ROWS == GRID_ROWS - 1) {
					TableLayout.LayoutParams layout = new TableLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					layout.setMargins(0, 5, 0, 5);
					table.addView(tr, layout);
				} else if (i == arrPanels[position].nCount - 1) {
					for (int j = 0; j < (GRID_ROWS - 1 - (i % GRID_ROWS)); j++) {
						LinearLayout blank = new LinearLayout(_context);
						tr.addView(blank, new TableRow.LayoutParams(0,
								LayoutParams.WRAP_CONTENT, 1));
					}
					TableLayout.LayoutParams layout = new TableLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					layout.setMargins(0, 5, 0, 5);
					table.addView(tr, layout);
				}
			}

			Gallery.LayoutParams layout = new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			contents.setLayoutParams(layout);

			return contents;
		}
	}

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int nIdx = v.getId() - 100;
			Intent intent = new Intent(getParent(), ChannelActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("SubChannelIdx", arrPanels[nCurPage].mainsIdx);
			intent.putExtra("ChannelIdx", arrPanels[nCurPage].subsIdx + nIdx);

			goNextHistory("ChannelActivity", intent);
		}

	};

	class imgDownloader extends ImageDownloader<String> {

		public imgDownloader(View view, String reqUrl, String saveFile) {
			super(view, reqUrl, saveFile);
			// TODO Auto-generated constructor stub
		}

	};

	private class Panels {
		int mainsIdx = 0;
		int subsIdx = 0;
		int nCount = 0;
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
	
}
