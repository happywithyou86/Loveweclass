package com.myvideo.loveweclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import com.myvideo.loveweclass.R;

public class SplashActivity extends Activity {
	ImageView mImageViewLogo;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(1);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        startAnimation();
    }
    
	private void startAnimation(){
		
		mImageViewLogo = (ImageView)findViewById(R.id.imageViewLogo);
		
		AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.5f);
		alpha.setDuration(1000);
		alpha.setFillAfter(true);
		alpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent loginIntent = new Intent(getBaseContext(), MainActivity.class);
				startActivity(loginIntent);
				finish();
				overridePendingTransition(R.anim.fade_hold,R.anim.fade);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		//alpha.setStartOffset(600);
		//animationSet.addAnimation(alpha);	
		mImageViewLogo.setAnimation(alpha);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
	}
	
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
}