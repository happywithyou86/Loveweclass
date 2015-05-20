package com.myvideo.loveweclass.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpTask<T> extends AsyncTask<Void, Object, T> 
{
	Context _context;
	Class<T> resultType;
	String requestUrl;
	String[] _postDataPair;
	String _filepath;
	public boolean bShowProgress = false;
	private static ProgressDialog dialog;
	
	String strMsg;
	
	public AsyncHttpTask(Context context, String reqUrl, Class<T> responseType, boolean bProgess)
	{
		_context = context;
		requestUrl = reqUrl;
		resultType = responseType;
		bShowProgress = bProgess;
		strMsg = "Loading...";
	}
	
	public AsyncHttpTask(Context context, String reqUrl, Class<T> responseType, String filepath, String... postDataPair)
	{
		_context = context;
		requestUrl = reqUrl;
		resultType = responseType;
		_filepath = filepath;
		_postDataPair = postDataPair;
		bShowProgress = true;
		strMsg = "Posting...";
	}
	
	@Override
    public void onPreExecute() 
	{
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bShowProgress) {
			if (dialog != null)
				dialog.dismiss();
			
			dialog = ProgressDialog.show(_context, "", strMsg, true);
		}
    }
	
	@SuppressWarnings("unchecked")
	@Override
	protected T doInBackground(Void... params)
	{
		T result = null;

		requestUrl = requestUrl.replace(" ", "%20");
		try
		{
			String type = resultType.getName();
			String response;
			if (_postDataPair != null)
				response = Http.Post(requestUrl, _filepath, _postDataPair);
			else
				response = Http.Request(requestUrl);

			Log.v("LoveWithClass", response);
			if (result instanceof String)
				result = (T)response;
			else if (resultType.getName().equals(String.class.getName()))
				result = (T)response;
			else
				result = Json.Deserialize(response, resultType);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		publishProgress((Object)result);		
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Object...objects) {
	}

	
	@Override
	protected void onPostExecute(T result) 
	{
		try {
			if (bShowProgress) {
				dialog.dismiss();
				dialog = null;
			}
		} catch (Exception e) {
			
		}
		
	}
	
}
