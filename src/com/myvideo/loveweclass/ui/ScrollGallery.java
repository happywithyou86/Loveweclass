package com.myvideo.loveweclass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class ScrollGallery extends Gallery {
    private float mInitialX;
    private float mInitialY;
    private boolean mNeedToRebase;
    private boolean mIgnore;

    public ScrollGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollGallery(Context context) {
        super(context);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        if (mNeedToRebase) {
            mNeedToRebase = false;
            distanceX = 0;
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
    	return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mIgnore = false;
                mNeedToRebase = true;
                mInitialX = e.getX();
                mInitialY = e.getY();
                return false;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!mIgnore) {
                    float deltaX = Math.abs(e.getX() - mInitialX);
                    float deltaY = Math.abs(e.getY() - mInitialY);
                    mIgnore = deltaX < deltaY;
                    return !mIgnore;
                }
                return false;
            }
            default: {
                return super.onInterceptTouchEvent(e);
            }
        }
    }

}
