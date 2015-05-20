package com.myvideo.loveweclass.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class UIUtils {
	static float density = 1;
	
	public static final int MSG_OK = 0;
	public static final int MSG_CANCEL = 1;
	public static final int MSG_OK_CANCEL = 2;
	public static final int MSG_CLOSE = 3;
	
	public static float getDisplayMetricsDensity(Context context) {
		density = context.getResources().getDisplayMetrics().density;
		return density;
	}
	
	public static int getPixel(Context context, int p) {
		if (density != 1) {
			return (int)(p*density + 0.5);
		}
		
		return p;
	}
	
	public static Animation FadeAnimation(float nFromFade, float nToFade) {
		Animation fadeAnimation = new AlphaAnimation(nToFade, nToFade);
		
		return fadeAnimation;
	}
	
	public static Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		
		return inFromRight;
	}

	public static Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		
		return inFromLeft;
	}

	public static Animation inFromBottomAnimation() {
		Animation inFromBottom = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		
		return inFromBottom;
	}

	public static Animation outToLeftAnimation() {
		Animation outToLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		
		return outToLeft;
	}

	public static Animation outToRightAnimation() {
		Animation outToRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
		);
		
		return outToRight;
	}

	public static Animation outToBottomAnimation() {
		Animation outToBottom = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
			Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f
		);
		
		return outToBottom;
	}
	
	public static void NetworkErrorMessage(final Context context, String msg,
			int type, DialogInterface.OnClickListener okClicker){
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setCancelable(false);
		dlg.setMessage(msg);
		
		if (MSG_OK == type || MSG_OK_CANCEL == type) {
			dlg.setPositiveButton("Retry", okClicker);
		} else if (MSG_CLOSE == type) {
			dlg.setPositiveButton("Close", okClicker);
		}
		
		if (MSG_CANCEL == type || MSG_OK_CANCEL == type) {
			dlg.setNegativeButton("Close", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which) {
					((Activity)context).finish();
					System.exit(0);
				}
			});
		}
		dlg.show();
	}

}

