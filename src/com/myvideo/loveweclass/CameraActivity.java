package com.myvideo.loveweclass;

import java.io.File;

import com.myvideo.loveweclass.R;
import com.myvideo.loveweclass.ui.Preview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CameraActivity extends Activity implements OnClickListener {
	
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	Uri mImageCaptureUri;
	Preview preview;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		Button btnDone = (Button) findViewById(R.id.buttonDone);
		btnDone.setOnClickListener(this);
		btnDone = (Button) findViewById(R.id.buttonCamera);
		btnDone.setOnClickListener(this);
		btnDone = (Button) findViewById(R.id.buttonGallery);
		btnDone.setOnClickListener(this);
		
		preview = (Preview) findViewById(R.id.preview);
		preview.setActivity(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int nId = arg0.getId();
		if (nId == R.id.buttonDone) {
			finish();
		} else if (nId == R.id.buttonCamera) {
			doTakePhotoAction();
		} else if (nId == R.id.buttonGallery) {
			doTakeAlbumAction();
		}
	}
	
	public void doPost(String path)
	{
		Intent intent = new Intent(this, DiaryEditActivity.class);
		intent.putExtra("path", path);
		intent.putExtra("sid", DiaryActivity.sid);

		startActivity(intent);
		finish();
	}

	private void doTakePhotoAction()
	{
		preview.capture();
		 
//		 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		 // temp file url
//		 String url = "DCIM/AT" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//		 mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//
//		 intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//		 intent.putExtra("return-data", true);
//		 startActivityForResult(intent, PICK_FROM_CAMERA);
	}
	 
	private void doTakeAlbumAction()
	{
		// call album
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
		{
			return;
		}

		switch(requestCode)
		{
			case PICK_FROM_ALBUM:
			{
				mImageCaptureUri = data.getData();
				String path = getRealImagePath(mImageCaptureUri);
				doPost(path);
				break;
			}
			case PICK_FROM_CAMERA:
			{
//	    Intent intent = new Intent("com.android.camera.action.CROP");
//	    intent.setDataAndType(mImageCaptureUri, "image/*");
//
//	    intent.putExtra("outputX", 320);
//	    intent.putExtra("outputY", 480);
//	    intent.putExtra("aspectX", 1);
//	    intent.putExtra("aspectY", 1.5);
//	    intent.putExtra("scale", true);
//	    intent.putExtra("return-data", true);
//	    startActivityForResult(intent, CROP_FROM_CAMERA);
				break;
			}
		}
	}
	
	public String getRealImagePath (Uri uriPath)
	{
		String []proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery (uriPath, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		String path = cursor.getString(index);

		return path;
	}
}

