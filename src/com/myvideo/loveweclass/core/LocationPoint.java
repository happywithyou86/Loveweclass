package com.myvideo.loveweclass.core;

public class LocationPoint
{
	public double Latitude;
	public double Longitude;
	
	public String toJsonString()
	{
		return  "{lat:" + Latitude + ", long:" + Latitude + "}";
	}
}
