package com.myvideo.loveweclass;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;

public class IFoodTvLocationListener implements LocationListener 
{
	private static final long RETAIN_GPS_MILLIS = 10000L;

	private Boolean _gpsAvailable = false;
	private Boolean _networkAvailable = false;
	private long _lastGpsTime = 0L;
	private Location _networkLocation;
	
	public Boolean HaveLocation = false;
	public double Latitude;
	public double Longitude;
	
	@Override
	public void onLocationChanged(Location location) 
	{
		if (!HaveLocation)
            HaveLocation = true;

        final long now = SystemClock.uptimeMillis();
        boolean useLocation = false;
        final String provider = location.getProvider();
        if (LocationManager.GPS_PROVIDER.equals(provider))
        {
            // Use GPS if available
        	_lastGpsTime = SystemClock.uptimeMillis();
            useLocation = true;
        }
        else if (LocationManager.NETWORK_PROVIDER.equals(provider))
        {
            // Use network provider if GPS is getting stale
            useLocation = now - _lastGpsTime > RETAIN_GPS_MILLIS;

			if (_networkLocation == null)
				_networkLocation = new Location(location);
            else
            	_networkLocation.set(location);
            
			_lastGpsTime = 0L;
        }

        if (useLocation)
        {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		if (LocationManager.GPS_PROVIDER.equals(provider))
		{
            switch (status)
            {
	            case LocationProvider.AVAILABLE:
	                _gpsAvailable = true;
	                break;
	            case LocationProvider.OUT_OF_SERVICE:
	            case LocationProvider.TEMPORARILY_UNAVAILABLE:
	            	_gpsAvailable = false;
	                
                    // fall back to network location
	                if (_networkLocation != null && _networkAvailable)
	                {
	                    _lastGpsTime = 0L;
	                    onLocationChanged(_networkLocation);
	                }
	                else
	                {
	                    HaveLocation = false;
	                }
	             
	                break;
            }

        } 
		else if (LocationManager.NETWORK_PROVIDER.equals(provider))
		{
            switch (status)
            {
	            case LocationProvider.AVAILABLE:
	            	_networkAvailable = true;
	                break;
	            case LocationProvider.OUT_OF_SERVICE:
	            case LocationProvider.TEMPORARILY_UNAVAILABLE:
	            	_networkAvailable = false;
	                
	                if (!_gpsAvailable)
	                    HaveLocation = false;
	                break;
            }
        }
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
	}
}
