package com.myvideo.loveweclass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DiaryGroupActivity extends NavigationGroupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		Intent intent = new Intent(this, DiaryActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		View view = getLocalActivityManager().startActivity(
				"DiaryGroupActivity", intent).getDecorView();
		replaceView(view, "DiaryGroupActivity");
	}
	
	@Override
	public void onBackPressed() { 
		super.onBackPressed();
	}
}
