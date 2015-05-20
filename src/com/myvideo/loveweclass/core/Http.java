package com.myvideo.loveweclass.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Http 
{
	private static final String TAG = Http.class.getSimpleName();
    private static final int POST_TYPE = 1;
    private static final int GET_TYPE = 2;
    private static final String CONTENT_TYPE = "Content-Type";
    public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private final ResponseHandler<String> responseHandler;

    public static String Request(String url)
    {
    	String data = "";
		AndroidHttpClient client = null;

		try
		{
			URI uri = new URI(url);
			HttpUriRequest request = new HttpGet(uri);

			client = AndroidHttpClient.newInstance("LoveWithClass");
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == 200) 
			{
				data = ToString(response.getEntity().getContent());
			} 
			else
			{
				Log.w(TAG, "HTTP returned " + response.getStatusLine().getStatusCode() + " for " + uri);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
		}
		finally
		{
			if (client != null)
				client.close();
		}
		
		return data;
	}
    
    public static String Post(String url, String filepath, String... postDataPair)
    {
		HttpParams myParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(myParams, 30000);
        HttpConnectionParams.setSoTimeout(myParams, 30000);
        
		DefaultHttpClient hc= new DefaultHttpClient(myParams);
		ResponseHandler <String> res=new BasicResponseHandler();
		
		HttpPost postMethod = new HttpPost(url);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postDataPair.length / 2);

		String response = "";
		try {
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int i = 0; i < postDataPair.length; i += 2) {
//				nameValuePairs.add(new BasicNameValuePair(postDataPair[i], postDataPair[i + 1]));
				reqEntity.addPart(postDataPair[i], new StringBody(postDataPair[i + 1]));
			}
			
			File file = new File(filepath);
			ContentBody cbFile = new FileBody(file, "image/jpeg");
			reqEntity.addPart("image_file", cbFile);
			
//			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8);
			postMethod.setEntity(reqEntity);
			response = hc.execute(postMethod,res);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
    }

    public static InputStream RequestStream(String url)
    {
    	InputStream stream = null;
		AndroidHttpClient client = null;

		try
		{
			URI uri = new URI(url);
			HttpUriRequest request = new HttpGet(uri);

			client = AndroidHttpClient.newInstance("TelAware");
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() == 200) 
			{
				stream = response.getEntity().getContent();
			} 
			else
			{
				Log.w(TAG, "HTTP returned " + response.getStatusLine().getStatusCode() + " for " + uri);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (client != null)
				client.close();
		}
		
		return stream;
	}
    
    public Http(final ResponseHandler<String> responseHandler) {
        this.responseHandler = responseHandler;
    }

    /**
     * Perform an HTTP GET operation.
     * 
     */
    public void Get(final String url, final String user, final String pass, final Map<String, String> additionalHeaders) 
    {
        performRequest(
        		null, 
        		url, 
        		user, 
        		pass, 
        		additionalHeaders, 
        		null, 
        		Http.GET_TYPE);
    }

    /**
     * Perform an HTTP POST operation with specified content type.
     * 
     */
    public void Post(final String contentType, final String url, final String user, final String pass, final Map<String, String> additionalHeaders, final Map<String, String> params) 
    {
    	performRequest(
    			contentType, 
    			url, 
    			user, 
    			pass, 
    			additionalHeaders, 
    			params, 
    			Http.POST_TYPE);
    }

    /**
     * Perform an HTTP POST operation with a default conent-type of
     * "application/x-www-form-urlencoded."
     * 
     */
    public void Post(final String url, final String user, final String pass, final Map<String, String> additionalHeaders, final Map<String, String> params) 
    {
        performRequest(
        		Http.MIME_FORM_ENCODED, 
        		url, 
        		user, 
        		pass, 
        		additionalHeaders, 
        		params,
        		Http.POST_TYPE);
    }

    /**
     * Private heavy lifting method that performs GET or POST with supplied url, user, pass, data,
     * and headers.
     * 
     * @param contentType
     * @param url
     * @param user
     * @param pass
     * @param headers
     * @param params
     * @param requestType
     */
    private void performRequest(final String contentType, final String url, final String user, final String pass, final Map<String, String> headers, final Map<String, String> params, final int requestType) 
    {
        Log.d(Http.TAG, "Making HTTP request to url - " + url);

        DefaultHttpClient client = new DefaultHttpClient();       

        SetCredentials(client, user, pass);
        SetHeaders(client, requestType, contentType, headers);

        if (requestType == Http.POST_TYPE) 
        {
            HttpPost method = PreparePost(url, params);
            Execute(client, method);            
        } 
        else if (requestType == Http.GET_TYPE)
        {
            HttpGet method = PrepareGet(url);
            Execute(client, method);
        }
    }

	private HttpGet PrepareGet(final String url) {
		Log.d(Http.TAG, "PrepareGet");
		HttpGet method = new HttpGet(url);
		return method;
	}

	private HttpPost PreparePost(final String url, final Map<String, String> params) 
	{
        Log.d(Http.TAG, "PreparePost");
		
        HttpPost method = new HttpPost(url);

		// data - name/value params
		List<NameValuePair> nvps = null;
		if (params != null && params.size() > 0) 
		{
		    nvps = new ArrayList<NameValuePair>();
		    for (String key : params.keySet()) 
		    {
		        Log.d(Http.TAG, "Adding param: " + key + " | " + params.get(key));
		        nvps.add(new BasicNameValuePair(key, params.get(key)));
		    }
		}
		
		if (nvps != null) 
		{
		    try 
		    {
		        method.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		    } 
		    catch (UnsupportedEncodingException e) 
		    {
		        Log.e(Http.TAG, "", e);
		    }
		}
		
		return method;
	}

	private void SetHeaders(DefaultHttpClient client, final int requestType, final String contentType, final Map<String, String> headers) 
	{
		// process headers using request interceptor
        final Map<String, String> sendHeaders = new HashMap<String, String>();

        if (headers != null && headers.size() > 0) 
            sendHeaders.putAll(headers);
        
        if (requestType == Http.POST_TYPE) 
            sendHeaders.put(Http.CONTENT_TYPE, contentType);
        
        if (sendHeaders.size() > 0) 
        {
            client.addRequestInterceptor(new HttpRequestInterceptor() 
            {
                public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException 
                {
                    for (String key : sendHeaders.keySet()) 
                    {
                        if (!request.containsHeader(key)) {
                            Log.d(Http.TAG, "Adding header: " + key + " | " + sendHeaders.get(key));
                            request.addHeader(key, sendHeaders.get(key));
                        }
                    }
                }
            });
        }
	}

	private void SetCredentials(DefaultHttpClient client, final String user, final String password) 
	{
        if (user == null && password == null)
        	return;

        Log.d(Http.TAG, "User and password present, adding credentials to request");
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
	}
    
    /**
     * Once the client and method are established, execute the request. 
     * 
     * @param client
     * @param method
     */
    private void Execute(HttpClient client, HttpRequestBase method) 
    {
        Log.d(Http.TAG, "Execute()");
        
        // create a response specifically for errors (in case)
        BasicHttpResponse errorResponse = new BasicHttpResponse(new ProtocolVersion("HTTP_ERROR", 1, 1), 500, "ERROR");
        
        try 
        {
            client.execute(method, this.responseHandler);
            Log.d(Http.TAG, "..Completed");
        } 
        catch (Exception e) 
        {
            Log.e(Http.TAG, "", e);
            errorResponse.setReasonPhrase(e.getMessage());
            try 
            {
                this.responseHandler.handleResponse(errorResponse);
            }
            catch (Exception ex) 
            {
                Log.e(Http.TAG, "", ex);
            }
        }
    }
    
	public static ResponseHandler<String> Load(final Handler handler) 
	{
        final ResponseHandler<String> responseHandler = new ResponseHandler<String>() 
        {
            public String handleResponse(final HttpResponse response) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                StatusLine status = response.getStatusLine();
                Log.d(Http.TAG, "StatusCode - " + status.getStatusCode());
                Log.d(Http.TAG, "StatusReasonPhrase - " + status.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                String result = null;
                if (entity != null) {
                    try {
                        result = ToString(entity.getContent());
                        bundle.putString("RESPONSE", result);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        Log.e(Http.TAG, " ", e);
                        bundle.putString("RESPONSE", "Error - " + e.getMessage());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } else {
                    Log.w(Http.TAG, "Empty response entity, HTTP error occurred");
                    bundle.putString("RESPONSE", "Error - " + response.getStatusLine().getReasonPhrase());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                
                return result;
            }
        };
        
        return responseHandler;
    }

	private static String ToString(final InputStream stream) throws IOException 
	{
        StringBuilder data = new StringBuilder();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = null;
    	while ((line = reader.readLine()) != null) 
    	{
        	data.append(line + "\n");
        }
        
        return data.toString();
    }
}
