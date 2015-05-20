package com.myvideo.loveweclass.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.myvideo.loveweclass.CameraActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class Preview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
    SurfaceHolder mHolder;
    Camera mCamera;
    CameraActivity _activity = null;
    
    public Preview(Context context) {
        super(context);
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;

            exception.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	if (mCamera != null) {
    		mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
    	}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        
        Camera.Size previewSize  = null;
//        for (int i = 0; i < previewSizes.size(); i++) {
//        	if (previewSizes.get(i).width == w && previewSizes.get(i).height >= h) {
//        		previewSize = previewSizes.get(i);
//        		break;
//        	}
//        }

        if (previewSize == null) {
        	previewSize = previewSizes.get(0);
        }
        
        parameters.setPreviewSize(640, 480);//previewSize.width, previewSize.height);
        parameters.set("orientation", "landscape");
//        parameters.set("rotation", 90);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = sd + "/ifoodtv/temp_pic/" + String.valueOf(System.currentTimeMillis()) + ".jpeg";
		File file = new File(path);
		
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Toast.makeText(getContext(), "error : " +
					e.getMessage(), 0).show();
			return;
		}
		
		if (_activity != null)
			_activity.doPost(path);
	}

	public void capture(){
		if (mCamera != null)
			mCamera.takePicture(null, null,this);
		
	}
	
	public void setActivity(CameraActivity activity)
	{
		_activity = activity;
	}
}

