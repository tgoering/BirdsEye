/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements the view that displays Tweets selected from the map
	view. 
 *****************************************************************************/

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TweetInfoView extends JPanel{
	private static final long serialVersionUID = 1L;
	
	protected BufferedImage twitterLogo = null;
	protected JLabel startMessage = new JLabel();
	protected JLabel name = new JLabel();
	protected JLabel screenName = new JLabel();
	protected JLabel text = new JLabel();
	protected JLabel followers = new JLabel();
	protected JLabel timestamp = new JLabel();
	protected Line2D line = new Line2D.Double();
	protected int borderSize = 20;
	
	public TweetInfoView(){
		this.setLayout(null);
		
		startMessage.setForeground(InterfaceConstants.TWITTER_DARK_GRAY);
		startMessage.setVerticalAlignment(SwingConstants.CENTER);
		startMessage.setVerticalTextPosition(SwingConstants.CENTER);
		startMessage.setHorizontalAlignment(SwingConstants.CENTER);
		startMessage.setHorizontalTextPosition(SwingConstants.CENTER);
		startMessage.setFont(InterfaceConstants.TWEET_FONT);
		startMessage.setText("Select a Tweet from the map to display it here");
		this.add(startMessage);

		name.setForeground(InterfaceConstants.TWITTER_DARK_GRAY);
		name.setVerticalAlignment(SwingConstants.TOP);
		name.setVerticalTextPosition(SwingConstants.TOP);
		name.setFont(InterfaceConstants.TWEET_FONT_BOLD);
		this.add(name);
		
		screenName.setForeground(InterfaceConstants.TWITTER_GRAY);
		screenName.setVerticalAlignment(SwingConstants.TOP);
		screenName.setVerticalTextPosition(SwingConstants.TOP);
		screenName.setFont(InterfaceConstants.TWEET_FONT_SMALL);
		this.add(screenName);
		
		text.setForeground(InterfaceConstants.TWITTER_DARK_GRAY);
		text.setVerticalAlignment(SwingConstants.TOP);
		text.setVerticalTextPosition(SwingConstants.TOP);
		text.setFont(InterfaceConstants.TWEET_FONT);
		this.add(text);
		
		followers.setForeground(InterfaceConstants.TWITTER_GRAY);
		followers.setVerticalAlignment(SwingConstants.TOP);
		followers.setVerticalTextPosition(SwingConstants.TOP);
		followers.setFont(InterfaceConstants.TWEET_FONT_SMALL);
		this.add(followers);
		
		timestamp.setForeground(InterfaceConstants.TWITTER_GRAY);
		timestamp.setVerticalAlignment(SwingConstants.TOP);
		timestamp.setVerticalTextPosition(SwingConstants.TOP);
		timestamp.setFont(InterfaceConstants.TWEET_FONT_SMALL);
		this.add(timestamp);
		
		try{
			twitterLogo = ImageIO.read(new File("resources/twitter-bird-16x16.png"));
		}
		catch(IOException exception){
			System.out.println("Missing file: /resources/twitter-bird-16x16.png");
			twitterLogo = null;
		}
		
		layOutLabels();
		repaint();
	}
	
	public void setData(TweetObject tweet, Calendar date){
		if(startMessage != null){
			this.remove(startMessage);
			startMessage = null;
		}
		
		name.setText(tweet.name);
		screenName.setText(String.format("@%s", tweet.screenName));
		
		text.setText(String.format("<html><p>%s</p></html>", tweet.text));
		
		followers.setText(String.format(
				"<html><span style=\"color:#292F33;\"><b>%d</b></span> FOLLOWERS</html>",
				tweet.followerCount));
		
		timestamp.setText(InterfaceConstants.TWEET_TIMESTAMP_FORMAT.format(date.getTime()));
		
		layOutLabels();
		repaint();
	}
	
	public void layOutLabels(){
		if(startMessage != null){
			int x = 0;
			int y = 0;
			int width = getPreferredSize().width;
			int height = getPreferredSize().height;
			startMessage.setBounds(x, y, width, height);		
			return;
		}
		
		System.out.println(getWidth() + " " + getPreferredSize());
		
		int x = borderSize;
		int y = borderSize;
		int width = getWidth() - (borderSize*2) - twitterLogo.getWidth();
		int height = InterfaceConstants.TWEET_FONT_BOLD.getSize() + 4;
		name.setBounds(x, y, width, height);
		
		y = borderSize + name.getHeight() - 2;
		height = InterfaceConstants.TWEET_FONT_SMALL.getSize() + 4;
		screenName.setBounds(x, y, width, height);
		
		y = borderSize + name.getHeight() + screenName.getHeight() + 4;
		width = getWidth() - (borderSize*2);
		height = (InterfaceConstants.TWEET_FONT.getSize()+4) * 4;
		text.setBounds(x, y, width, height);
		
		y = getHeight() - borderSize - (InterfaceConstants.TWEET_FONT.getSize() + 4);
		height = (InterfaceConstants.TWEET_FONT_SMALL.getSize()+4);
		followers.setBounds(x, y, width, height);
		
		System.out.println(followers.getY() + " " + getHeight());
		
		int x1 = borderSize;
		y = followers.getY()-4;
		int x2 = getWidth()-borderSize;
		line.setLine(x1, y, x2, y);
		
		y = (int)line.getY1() - InterfaceConstants.TWEET_FONT_SMALL.getSize() - 8;
		timestamp.setBounds(x, y, width, height);
	}
	
	public void doLayout(){
		layOutLabels();
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		// Anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if(startMessage == null){
			if(twitterLogo != null){
				g2.drawImage(twitterLogo, getWidth()-36, 25, null );
			}
			g2.setColor(InterfaceConstants.TWITTER_LIGHT_GRAY);
			g2.draw(line);
		}
	}
}
