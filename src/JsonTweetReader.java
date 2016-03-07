/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This class holds a static method that parses JSON files using Google's 
	gson library.
 *****************************************************************************/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonTweetReader{
	public static DateFormat TWEET_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
	public static void readJSON(String filename, Map<Calendar, List<TweetObject>> Tweets) throws Exception{
		BufferedReader input;
		FileInputStream fis = null;
		GZIPInputStream gzis = null;
		InputStreamReader isr = null;
		Gson gson = new Gson();
		boolean fileIsGzipped;
		
		// Handle gzipped files
		Pattern p = Pattern.compile(".gz");
		Matcher m = p.matcher(filename);
		if(m.find()){
			fis = new FileInputStream(filename);
			gzis = new GZIPInputStream(fis);
			isr = new InputStreamReader(gzis);
			input = new BufferedReader(isr);
			fileIsGzipped = true;
		}
		else{
			input = new BufferedReader(new FileReader(filename));
			fileIsGzipped = false;
		}
		
		// Parse the file by reading lines of input as JSON objects and extracting necessary information
		for(String line = input.readLine(); line != null; line = input.readLine()){
			if(!line.equals("")){
				JsonObject tweet = gson.fromJson(line, JsonObject.class);
				if(tweet.has("text")){
					
					// Get the Tweet's text
					String text = tweet.get("text").getAsString();
					
					// Get the number of followers the user has
					int followerCount = tweet.get("user").getAsJsonObject().get("followers_count").getAsInt();
					
					// Get the Tweet's latitude and longitude, if present
					double latitude, longitude;
					if(!tweet.get("geo").isJsonNull()){
						JsonArray geo = tweet.get("geo").getAsJsonObject().get("coordinates").getAsJsonArray();
						latitude = geo.get(0).getAsDouble();
						longitude = geo.get(1).getAsDouble();
					}
					else{
						latitude = 0;
						longitude = 0;
					}
					
					// Parse name and screen name if the Tweet is geographically tagged, otherwise the data isn't needed
					String name, screenName;
					if(latitude == 0 && longitude == 0){
						name = null;
						screenName = null;
					}
					else{
						JsonObject user = tweet.get("user").getAsJsonObject();
						name = user.get("name").getAsString();
						screenName = user.get("screen_name").getAsString();
					}
					
					// Parse the Tweet's creation date and convert it to a Calendar object
					String dateStr = tweet.get("created_at").getAsString();
					Calendar date = Calendar.getInstance(Locale.ENGLISH);
					date.setTime(TWEET_DATE_FORMAT.parse(dateStr));
					date.set(Calendar.SECOND, 0);
					
					// Create the object that stores the Tweet's information and add it to a hash
					TweetObject newTweet = new TweetObject(text, name, screenName, followerCount, latitude, longitude);
					if(!Tweets.containsKey(date)){
						Tweets.put(date, new ArrayList<TweetObject>());
					}
					Tweets.get(date).add(newTweet);
				}
			}
		} 

		input.close();
		if(fileIsGzipped){
			isr.close();
			gzis.close();
			fis.close();
		}
	}
}
