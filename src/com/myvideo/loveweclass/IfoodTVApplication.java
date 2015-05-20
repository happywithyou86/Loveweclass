package com.myvideo.loveweclass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import twitter4j.Twitter;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.myvideo.loveweclass.core.LocationPoint;
import com.myvideo.loveweclass.core.WebServiceUrl;
import com.myvideo.loveweclass.data.Recipe;
import com.myvideo.loveweclass.data.ResultCategories;
import com.myvideo.loveweclass.data.ResultChannel;
import com.myvideo.loveweclass.data.ResultChannels;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IfoodTVApplication extends Application {
	public ResultChannels mainChannels = null;
	public ResultCategories categories = null;
	public ResultChannel searchChannel = null;
	String query = null;
	
	public ArrayList<Recipe> favoriteRecipe = new ArrayList<Recipe>();
	
	public static final int STAT_NORMAL = 0;
	public static final int STAT_RECIPES = 1;
	public static final int STAT_DETAIL = 2;

	public ChannelActivity channelInstance;
	
	public static int cur_state = STAT_NORMAL;
	
	private LocationManager _locationManager;
	private IFoodTvLocationListener _locationListener;
	
	public static IfoodTVApplication gInstance = null;
	public RequestToken mRqToken;
	public AccessToken mAccessToken;
	private SharedPreferences sharedPrefs;
	
	public OAuthConsumer consumer;
	public OAuthProvider provider;
	
	public IfoodTVApplication()
	{
		super();
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		_locationListener = new IFoodTvLocationListener();
		
		gInstance = this;

		requestLocationUpdates();

		initialize();
	}
	
	public void initialize()
	{
		readFavorite();
	}
	
	public void setResultChannels(ResultChannels channels)
	{
		this.mainChannels = channels;
	}
	
	public void setCategories(ResultCategories categories)
	{
		this.categories = categories;
	}
	
	public void addFavorite(Recipe recipe)
	{
		favoriteRecipe.add(recipe);
		
		saveFavorite();
	}
	
	public boolean isAddedToFavor(Recipe recipe)
	{
		for (int i = 0; i< favoriteRecipe.size(); i++)
		{
			if (favoriteRecipe.get(i).id.equalsIgnoreCase(recipe.id))
				return true;
		}
		
		return false;
	}
	
	public void removeFavorite(ArrayList<Integer> arrIdx)
	{
		ArrayList<Recipe> arrIDList = new ArrayList<Recipe>();
		
		for (int i = 0; i < arrIdx.size(); i++)
			arrIDList.add(favoriteRecipe.get(arrIdx.get(i)));

		for (int i = 0; i < arrIDList.size(); i++)
		{
			favoriteRecipe.remove(arrIDList.get(i));
		}
	}
	
	public void saveFavorite()
	{
		Recipe recipe;
		
		SharedPreferences mySharedPreference = getSharedPreferences("favorites", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = null;

		editor = mySharedPreference.edit();
		editor.putInt("Counts", favoriteRecipe.size());
		String index;
		for (int i= 0; i < favoriteRecipe.size(); i++)
		{
			recipe = favoriteRecipe.get(i);
			index = String.valueOf(i);
			editor.putString("id" + index, recipe.id);
			editor.putString("title" + index, recipe.title);
			editor.putString("type" + index, recipe.type);
			editor.putString("author" + index, recipe.author);
			editor.putString("url" + index, recipe.url);
			editor.putString("picture" + index, recipe.picture);
			editor.putString("main_picture" + index, recipe.main_picture);
			editor.putString("like_count" + index, recipe.like_count);
			editor.putString("video_url" + index, recipe.video_url);
			editor.putString("video_flv_url" + index, recipe.video_flv_url);
			editor.putString("ad" + index, recipe.ad);
		}
		editor.commit();
	}
	
	public void readFavorite()
	{
		SharedPreferences mySharedPreference = getSharedPreferences("favorites", Context.MODE_PRIVATE);
		favoriteRecipe.clear();
		int nSize = mySharedPreference.getInt("Counts", 0);
	
		String index;
		for (int i = 0; i < nSize; i++)
		{
			Recipe recipe = new Recipe();
			index = String.valueOf(i);
			recipe.id = mySharedPreference.getString("id" + index, "");
			recipe.title = mySharedPreference.getString("title" + index, "");
			recipe.type = mySharedPreference.getString("type" + index, "");
			recipe.author = mySharedPreference.getString("author" + index, "");
			recipe.url = mySharedPreference.getString("url" + index, "");
			recipe.picture = mySharedPreference.getString("picture" + index, "");
			recipe.main_picture = mySharedPreference.getString("main_picture" + index, "");
			recipe.like_count = mySharedPreference.getString("like_count" + index, "");
			recipe.video_url = mySharedPreference.getString("video_url" + index, "");
			recipe.video_flv_url = mySharedPreference.getString("video_flv_url" + index, "");
			recipe.ad = mySharedPreference.getString("ad" + index, "");
			
			favoriteRecipe.add(recipe);
		}
	}
	
	public void requestLocationUpdates() {
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				60000, 1, _locationListener);
		_locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 60000, 1, _locationListener);
	}

	public void removeLocationUpdates() {
		_locationManager.removeUpdates(_locationListener);
	}
	
	public LocationPoint getLastKnownLocation() {
		LocationPoint point = new LocationPoint();
		if (_locationListener.HaveLocation) {
			point.Latitude = _locationListener.Latitude;
			point.Longitude = _locationListener.Longitude;
		} else {
			Location lastKnownLocation = _locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastKnownLocation == null) {
				lastKnownLocation = _locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			if (lastKnownLocation == null) {
				point.Latitude = 0;
				point.Longitude = 0;
			} else {
				point.Latitude = lastKnownLocation.getLatitude();
				point.Longitude = lastKnownLocation.getLongitude();
			}
		}

		return point;
	}
	
	public String getUniqueId() {
		final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		if (tm != null) {
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = "" + android.provider.Settings.Secure.getString(
							getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), 
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			
			return deviceUuid.toString();
		}
		
		WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
		if (wm != null) {
			return wm.getConnectionInfo().getMacAddress();
		}

		return null;
	}
	
    public void setSssion(String token, long token_expires, Context context) {
    	Log.d("LoveWithClass", token);
        sharedPrefs = PreferenceManager
		                .getDefaultSharedPreferences(context);
		sharedPrefs.edit().putLong("access_expires", token_expires).commit();
		sharedPrefs.edit().putString("access_token", token).commit();
    }
        
}
