import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class VisApp{
	public ArrayList<ArrayList<JSONTweet>> tweets = new ArrayList<ArrayList<JSONTweet>>();
	private JFrame appFrame;
	
	public VisApp(){
		readData();
		createPanel();
	}
	
	public void readData(){
	/*	String filename;
		
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
				
				ArrayList<JSONTweet> minute = new ArrayList<JSONTweet>();
				JSONReader input = new JSONReader();
				input.readJSON(filename, minute);
				tweets.add(minute);
				System.out.println("COMPLETED HOUR " + i + " MIN " + j);
			}
		}*/
	}
	
	public void createPanel(){
		appFrame = new JFrame();
		appFrame.setTitle("TwitterVis");
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		appFrame.setBounds(100, 100, 1000, 600);
		appFrame.setVisible(true);
	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				new VisApp();
			}
		});
	}
}