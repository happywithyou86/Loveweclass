package com.myvideo.loveweclass;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.core.WebServiceUrl;

import android.content.Intent;
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

public class MoreActivity extends NavigationActivity {
	LayoutInflater inflater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard);
		setRequestedOrientation(1);
		
		inflater = LayoutInflater.from(this);
		setNavigation();
		setContents();
	}
	
	void setNavigation()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.top_image_bar, null);
		ImageView titleImage = (ImageView)contents.findViewById(R.id.imageTitle);
		titleImage.setImageResource(R.drawable.nb_ifoodicon);
		Button btnAction = (Button)contents.findViewById(R.id.buttonAction);
		btnAction.setVisibility(View.GONE);
		
		Button btnBack = (Button)contents.findViewById(R.id.buttonBack);
		btnBack.setVisibility(View.GONE);
		
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout top = (LinearLayout) findViewById(R.id.linearTop);
		top.addView(contents);
	}
	
	OnTouchListener mTouchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if (v.getId() == R.id.linearAbout) {
					v.setBackgroundResource(R.drawable.sel_middle_item_bg);
				} else if (v.getId() == R.id.linearTerms){
					v.setBackgroundResource(R.drawable.sel_bottom_item_bg);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				if (v.getId() == R.id.linearAbout) {
					v.setBackgroundResource(R.drawable.white_middle_item_bg);
				} else if (v.getId() == R.id.linearTerms){
					v.setBackgroundResource(R.drawable.white_bottom_item_bg);
				}
			} else if (event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				if (v.getId() == R.id.linearAbout) {
					v.setBackgroundResource(R.drawable.white_middle_item_bg);
				} else if (v.getId() == R.id.linearTerms){
					v.setBackgroundResource(R.drawable.white_bottom_item_bg);
				}
			}
			return false;
		}
		
	};
	
	OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int nId = v.getId();
			if (nId == R.id.buttonAction)
			{
				onBackPressed();
			} else if (v.getId() == R.id.linearAbout) {
				Intent intent = new Intent(getParent(), WebviewActivity.class);
				intent.putExtra("title", "About");
				intent.putExtra("url", WebServiceUrl.ABOUT_URL);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				goNextHistory("About", intent);
			} else if (v.getId() == R.id.linearTerms){
				Intent intent = new Intent(getParent(), WebviewActivity.class);
				intent.putExtra("title", "Terms of Use");
				intent.putExtra("url", WebServiceUrl.TERM_URL);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				goNextHistory("Terms", intent);
			}
		}
	};
	
	void setContents()
	{
		LinearLayout contents = (LinearLayout)inflater.inflate(R.layout.more, null);
		
		LinearLayout about = (LinearLayout) contents.findViewById(R.id.linearAbout);
		about.setOnTouchListener(mTouchListener);
		about.setOnClickListener(mOnClickListener);
		
		LinearLayout terms = (LinearLayout) contents.findViewById(R.id.linearTerms);
		terms.setOnTouchListener(mTouchListener);
		terms.setOnClickListener(mOnClickListener);
		
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contents.setLayoutParams(layout);
		LinearLayout linearContents = (LinearLayout) findViewById(R.id.linearContents);
		linearContents.addView(contents);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
