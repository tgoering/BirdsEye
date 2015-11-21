import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JSONReader{
	public void readJSON(String filename, ArrayList<JSONTweet> tweets){
		BufferedReader input;
		Gson gson = new Gson();
		String line;
		int i = 1;
		
		try{
			FileInputStream fis = new FileInputStream(filename);
			GZIPInputStream gzis = new GZIPInputStream(fis);
			InputStreamReader isr = new InputStreamReader(gzis);
			input = new BufferedReader(isr);
			
			line = input.readLine();
			line = input.readLine();
			boolean line_ends_in_comma = true;
			while(line_ends_in_comma){
				line = input.readLine();
				
				// Strip any commas from the end of the line
				char lastchar = line.charAt(line.length()-1);
				if(lastchar == ','){
					line = line.substring(0, line.length()-1);
				}
				else{
					line_ends_in_comma = false;
				}
				
				// Use the gson library to convert the file's contents to JSON objects
				JsonObject tweet = gson.fromJson(line, JsonObject.class);
				JSONTweet jtweet = new JSONTweet(tweet.get("text"), /*tweet.get("name"),*/ tweet.get("retweet_count"), tweet.get("favorite_count"), tweet.get("geo"));
				tweets.add(jtweet);

			}
			input.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
