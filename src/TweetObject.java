/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines the object which holds only the necessary information
	from the streaming JSON objects.
 *****************************************************************************/

public class TweetObject{
	public String text;
	public String name;
	public String screenName;
	public int followerCount;
	public double latitude;
	public double longitude;
	
	public TweetObject(String text, String name, String screenName, int followerCount, double latitude, double longitude){
		this.text = text;
		this.name = name;
		this.screenName = screenName;
		this.followerCount = followerCount;
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
