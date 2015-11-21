import java.awt.EventQueue;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class VisApp{
	public VisApp(){
		String filename;
		ArrayList<ArrayList<JSONTweet>> tarray = new ArrayList<ArrayList<JSONTweet>>();  
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 60; j++){
				if(i < 10){
					if(j < 10){
						filename = "C:/Users/Jeffrey/Desktop/2013-04-15/2013-04-15-0" + i + "-0" + j + ".json.gz";
					}
					else{
						filename = "C:/Users/Jeffrey/Desktop/2013-04-15/2013-04-15-0" + i + "-" + j + ".json.gz";
					}
				}
				else{
					if(j < 10){
						filename = "C:/Users/Jeffrey/Desktop/2013-04-15/2013-04-15-" + i + "-0" + j + ".json.gz";
					}
					else{
						filename = "C:/Users/Jeffrey/Desktop/2013-04-15/2013-04-15-" + i + "-" + j + ".json.gz";
					}
				}
				
				ArrayList<JSONTweet> tweets = new ArrayList<JSONTweet>();
				JSONReader input = new JSONReader();
				input.readJSON(filename, tweets);
				tarray.add(tweets);
				System.out.println("COMPLETED HOUR " + i + " MIN " + j);
			}
		}
	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				new VisApp();
			}
		});
	}
}
