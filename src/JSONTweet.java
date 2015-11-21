import com.google.gson.JsonElement;

public class JSONTweet{
	public String text;
	public String screen_name;
	public int retweet_count;
	public int favorite_count;
	public JsonElement geo;
	
	public JSONTweet(JsonElement text, /*JsonElement screen_name,*/ JsonElement retweet_count, JsonElement favorite_count, JsonElement geo){
		if(text.isJsonNull()){ this.text = null; }
		else{ this.text = text.getAsString(); }
		
	//	if(screen_name.isJsonNull()){ this.screen_name = null; }
	//	else{ this.screen_name = screen_name.getAsString(); }
		
		if(retweet_count.isJsonNull()){ this.retweet_count = Integer.MIN_VALUE; }
		else{ this.retweet_count = retweet_count.getAsInt(); }
		
		if(favorite_count.isJsonNull()){ this.favorite_count = Integer.MIN_VALUE; }
		else{ this.favorite_count = favorite_count.getAsInt(); }
		
		if(geo.isJsonNull()){ this.geo = null; ;}
		else{ this.geo = geo; }
	}
}