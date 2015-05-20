package com.myvideo.loveweclass.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.myvideo.loveweclass.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageDownloader<T> extends AsyncTask<Void, Drawable, T> {
	final static int IMAGE_MAX_SIZE = 800;
	private final static String strDownloadImgFolder = "/ifoodtv/temp_pic/";
	String requestUrl;
	String saveFileName;
	String[] arrRequestUrl;
	String[] arrSaveFile;
	private SoftReference<View> imageViewReference = null;
	public static HashMap<String, SoftReference<Bitmap>> cacheBitmap = new HashMap<String, SoftReference<Bitmap>>();

	public static void clearCacheBitmap () {
	}
	
	public static String getFileNamefromUrl(String strUrl) {
		
		if (strUrl.equals(""))
			return "";

		int nPos = strUrl.lastIndexOf('/');
		if (nPos == -1) {
			return strUrl;
		}
		
		return strUrl.substring(nPos + 1);
	}

	public ImageDownloader(String reqUrl, String saveFile) {
		requestUrl = reqUrl;
		saveFileName = saveFile;
	}

	public ImageDownloader(View imgView, String reqUrl, String saveFile) {
		requestUrl = reqUrl;
		saveFileName = saveFile;
		imageViewReference = new SoftReference<View>(imgView);
	}

	public ImageDownloader(String[] reqUrl, String[] saveFile) {
		arrRequestUrl = reqUrl;
		arrSaveFile = saveFile;
	}
	
	public static boolean isExistImageFile(String strPath) {
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	 	
	 	strPath = sdPath + strDownloadImgFolder + strPath;

	 	File file = new File(strPath);
		return file.exists();
	}
	
	public static boolean isCached(String strPath) {
		if (cacheBitmap.containsKey(strPath))
			return true;
		else
			return false;
	}

	public static boolean loadImageFromFile(String strPath) {
		Bitmap bmp;
		String savefile = strPath;
		try {
		 	if (isExistImageFile(strPath) == false) {
		 		return false;
		 	}

			String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		 	strPath = sdPath + strDownloadImgFolder + strPath;
		 	BitmapFactory.Options opts = new BitmapFactory.Options();
		 	opts.inJustDecodeBounds = true;
		 	
		 	BitmapFactory.decodeFile(strPath, opts);
		 	
		 	if (opts.outWidth * opts.outHeight >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
		 		opts.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE
		 				/(double)Math.max(opts.outHeight, opts.outWidth)) / Math.log(0.5)));
		 	}
		 	opts.inJustDecodeBounds = false;
		 	opts.inPurgeable = true;
		 	opts.inDither = true;
		 	
		 	bmp = BitmapFactory.decodeFile(strPath, opts);
			if (bmp == null)
				return false;
			
			cacheBitmap.put(savefile, new SoftReference<Bitmap>(bmp));
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public static boolean eraseDownloadFolder() {
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File dir = new File(sdPath + strDownloadImgFolder);
		if(dir.exists()) {
			File[] listFile = dir.listFiles();
			for (File file : listFile) {
				file.delete();
			}
			
			dir.delete();
		}
		
		dir = new File(sdPath, "LoveWithClass");
		if (dir.exists()) {
			dir.delete();
			return true;
		}
		
		return false;
	}
	
	public static Bitmap getCacheBitmap(String strPath) {
		if (cacheBitmap.containsKey(strPath)) {
			Bitmap bm = cacheBitmap.get(strPath).get();
			if (bm == null) {
				if (loadImageFromFile(strPath)) {
					return cacheBitmap.get(strPath).get();
				}
			}
			return bm;
		} else {
			return null;
		}
	}

	public Drawable getDrawableFromUrl(String fileUrl,
			String saveFile) throws ClientProtocolException, IOException {
		
		Bitmap bmp = null;

		if (fileUrl == null)		return null;
		
		fileUrl = fileUrl.replace("[", "%5B");
		fileUrl = fileUrl.replace("]", "%5D");
		fileUrl = fileUrl.replace(" ", "%20");
		
		if (cacheBitmap.containsKey(saveFile)) {
			return new BitmapDrawable(getCacheBitmap(saveFile));
		}
		
//		if (loadImageFromFile(saveFile)) {
//			return new BitmapDrawable(bmp);
//		}
	
		HttpGet httpRequest = null;
		try {
			httpRequest = new HttpGet(fileUrl);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		HttpClient httpclient = new DefaultHttpClient();//AndroidHttpClient.newInstance("Android");
		HttpResponse response = httpclient.execute(httpRequest);
		int statecode = response.getStatusLine().getStatusCode();
		if (statecode != HttpStatus.SC_OK) {
//			((AndroidHttpClient)httpclient).close();
			httpclient = null;
			return null;
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
//			((AndroidHttpClient)httpclient).close();
			httpclient = null;
			return null;
		}
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream instream = bufHttpEntity.getContent();
		
		if (instream == null)	return null;
		saveStreamToFile(instream, saveFile);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(instream, null, opts);
		
		if (opts.outHeight * opts.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
			opts.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE /
				(double)Math.max(opts.outHeight, opts.outWidth))/ Math.log(0.5)));
		}
		
		response = httpclient.execute(httpRequest);
		HttpEntity reentity = response.getEntity();
		bufHttpEntity = new BufferedHttpEntity(reentity);
		InputStream reInputStream = bufHttpEntity.getContent();
	
		try {
			opts.inJustDecodeBounds = false;
			bmp = BitmapFactory.decodeStream(reInputStream, null, opts);
			if(bmp == null) {
//				((AndroidHttpClient)httpclient).close();
				httpclient = null;
				return null;
			}
		} finally {
			if (reInputStream != null) {
				reInputStream.close();
			}
			reentity.consumeContent();
			if (instream != null) {
				instream.close();
			}
			entity.consumeContent();
//			((AndroidHttpClient)httpclient).close();
			httpclient = null;
		}

		if (cacheBitmap.containsKey(saveFile)) {
			return new BitmapDrawable(getCacheBitmap(saveFile));
		}
		cacheBitmap.put(saveFile, new SoftReference<Bitmap>(bmp));
		return new BitmapDrawable(bmp);
	}
	
	static void saveStreamToFile(InputStream istream, String filepath) {
		// File file = new File(strPath);
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File dir = new File(sdPath,  "LoveWithClass");
		if(!dir.exists())
			dir.mkdir();

		File downdir = new File(dir.getPath(),  "temp_pic");
		if(!downdir.exists())
			downdir.mkdir();

	 	final File file = new File(downdir, filepath);
		if(file.exists()){
			return;
		}
		
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			try{
				int c = 0;
			    while((c = istream.read()) != -1)
			         os.write(c);
			    
			    os.flush();
				os.close();
			}catch(IOException e){
				e.printStackTrace();
				return;
			}	
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		try {
			if (arrRequestUrl != null) {
				Drawable[] arrDrawable = new Drawable[arrRequestUrl.length];
				
				for (int i = 0; i < arrRequestUrl.length ; i++) {
					arrDrawable[i] = getDrawableFromUrl(arrRequestUrl[i], arrSaveFile[i]);
				}
				
				publishProgress(arrDrawable);
				return (T)arrSaveFile;
			} else {
				Drawable drawable = getDrawableFromUrl(requestUrl, saveFileName);
				if (drawable != null) {
					publishProgress(drawable);
					return (T)saveFileName;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Drawable... drawable) {
		// TODO Auto-generated method stub
		if (imageViewReference != null) {
			View view = imageViewReference.get();
			if (view == null)	return;
			ImageView imgView = (ImageView) view.findViewById(R.id.imageThumb);
			ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);
			ImageView play = (ImageView) view.findViewById(R.id.imagePlay);
			
			if (progress != null)
				progress.setVisibility(View.INVISIBLE);
			
			if (imgView != null) {
				imgView.setImageDrawable(drawable[0]);
			}

			if (play != null)
				play.setVisibility(View.VISIBLE);
		}
	}

}
