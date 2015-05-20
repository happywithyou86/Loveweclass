package com.myvideo.loveweclass;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class HomeGroupActivity extends NavigationGroupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		View view = getLocalActivityManager().startActivity(
				"HomeGroupActivity", intent).getDecorView();
		replaceView(view, "HomeGroupActivity");
	}
	
	@Override
	public void onBackPressed() { 
		super.onBackPressed();
	}
	
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//    	super.onConfigurationChanged(newConfig);
//
//    	int i = 0;
//    	Log.v("LoveWithClass", "configuration2 changed");
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//    }
}
