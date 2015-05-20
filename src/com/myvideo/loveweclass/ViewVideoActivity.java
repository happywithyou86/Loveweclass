package com.myvideo.loveweclass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.ui.MyVideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class ViewVideoActivity extends Activity {
	WebView webView;
	MyVideo videoView;
//	String htmlPre = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head><body style='margin:0; pading:0; background-color: black;'>";
//	String htmlCode = " <embed style='width:100%; height:100%' src='http://www.platipus.nl/flvplayer/download/1.0/FLVPlayer.swf?fullscreen=true&video=@VIDEO@' "
//			+ "  autoplay='true' "
//			+ "  quality='high' bgcolor='#000000' "
//			+ "  name='VideoPlayer' align='middle'"
//			+ // width='640' height='480'
//			"  allowScriptAccess='*' allowFullScreen='true'"
//			+ "  type='application/x-shockwave-flash' "
//			+ "  pluginspage='http://www.macromedia.com/go/getflashplayer' />"
//			+ "";
//	String htmlPost = "</body></html>";
	boolean bFLV = false;
	
	private static ProgressDialog progressdialog = null;
	
	private String flvplayerurl = "http://www.ifood.tv/xml/boxee/getFlashEmbeddCode/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.view_video);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		boolean bFLV = intent.getBooleanExtra("flv", false);

		videoView = (MyVideo) findViewById(R.id.videoView);
		webView = (WebView) findViewById(R.id.webview);
		if (bFLV)
		{
			videoView.setVisibility(View.GONE);

			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setAllowFileAccess(true);
			webView.getSettings().setPluginsEnabled(true);
			webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); // thanks
																		// Patrick!
			webView.loadUrl(flvplayerurl + url);
//			htmlCode = htmlCode.replaceAll("@VIDEO@", url);
//			webView.loadDataWithBaseURL("fake://fake/fake", htmlPre + htmlCode
//					+ htmlPost, "text/html", "UTF-8", null);			
		} else {
			if (progressdialog != null)
				progressdialog.dismiss();
			webView.setVisibility(View.GONE);
			videoView.setContext(this);
			videoView.setVideoURI(Uri.parse(url));

			progressdialog = ProgressDialog.show(this, "", " Video Loading...", true);
	        progressdialog.setCancelable(false);

			MediaController ctlr = new MediaController(this);
			ctlr.setMediaPlayer(videoView);
			videoView.setMediaController(ctlr);
			videoView.start();
			videoView.setOnPreparedListener(new OnPreparedListener() {

	            public void onPrepared(MediaPlayer mp) {
	            	if (progressdialog != null)
	            		progressdialog.dismiss();
	                progressdialog = null;
	                
	                if (videoView != null)
	                {
		                videoView.requestFocus();
		                videoView.start();
	                }
	            }
	        });
			
			videoView.setOnErrorListener(new OnErrorListener(){

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					if (progressdialog != null)
						progressdialog.dismiss();
	                progressdialog = null;
	                if (videoView != null)
	                	videoView.stopPlayback();
	                onBackPressed();
					return false;
				}
				
			});
			
			videoView.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					if (videoView != null)
						videoView.stopPlayback();
	                onBackPressed();
				}
				
			});
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if (progressdialog != null)
		{
            progressdialog.dismiss();
            progressdialog = null;
		}
			
		if (bFLV)
		{
			callHiddenWebViewMethod("onPause");

			webView.pauseTimers();
			if (isFinishing()) {
				webView.loadUrl("about:blank");
				setContentView(new FrameLayout(this));
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (bFLV)
		{
			callHiddenWebViewMethod("onResume");

			if (webView != null)
				webView.resumeTimers();
		}
	}
	
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
    	if (videoView != null)
    		videoView.stopPlayback();
    	super.onBackPressed();
    }


	private void callHiddenWebViewMethod(String name) {
		if (webView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(webView);
			} catch (NoSuchMethodException e) {
				Log.v("LoveWithClass", "No such method: " + name + e);
			} catch (IllegalAccessException e) {
				Log.v("LoveWithClass", "Illegal Access: " + name + e);
			} catch (InvocationTargetException e) {
				Log.v("LoveWithClass", "Invocation Target Exception: " + name + e);
			}
		}
	}
}
