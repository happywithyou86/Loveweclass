package com.myvideo.loveweclass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.VideoView;

public class MyVideo extends VideoView {

	Context _context;
    public MyVideo(Context context) {
        super(context);
        _context = context;
    }
    
    public void setContext(Context context)
    {
    	_context = context;
    }
    
    public MyVideo(Context context, AttributeSet attrs)
    {
    	super(context, attrs);
    }
    
    public MyVideo(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
    	Display display = ((WindowManager) _context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	int width = display.getWidth();
    	int height = display.getHeight();
    	
    	if (width >= 720)
    		width = 720;
    	
    	if (height >= 480)
    		height = 480;
        setMeasuredDimension(width,height);
    }

}

