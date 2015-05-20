package com.myvideo.loveweclass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MoreGroupActivity extends NavigationGroupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		Intent intent = new Intent(this, MoreActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		View view = getLocalActivityManager().startActivity(
				"MoreGroupActivity", intent).getDecorView();
		replaceView(view, "MoreGroupActivity");
	}
	
	@Override
	public void onBackPressed() { 
		super.onBackPressed();
	}
}
