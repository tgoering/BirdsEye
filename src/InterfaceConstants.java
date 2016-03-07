/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines fonts, colors, etc. to be used throughout the
	application.
 *****************************************************************************/

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class InterfaceConstants{
	// Twitter color scheme
	public static final Color TWITTER_BLUE = new Color(85, 172, 238);
	public static final Color TWITTER_BLUE_HIGHLIGHTED = new Color(116, 187, 241);
	public static final Color TWITTER_BLUE_SEMI_TRANS = new Color(85, 172, 238, 80);
	public static final Color TWITTER_DARK_GRAY = new Color(41, 47, 51);
	public static final Color TWITTER_GRAY = new Color(138, 153, 166);
	public static final Color TWITTER_LIGHT_GRAY = new Color(204, 214, 221);
	public static final Color TWITTER_LIGHTER_GRAY = new Color(225, 232, 237);
	
	// Slightly modified Twitter font scheme
	public static final Font TWEET_FONT = new Font("Helvetica", Font.PLAIN, 12);
	public static final Font TWEET_FONT_BOLD = new Font("Helvetica", Font.BOLD, 12);
	public static final Font TWEET_FONT_SMALL = new Font("Helvetica", Font.PLAIN, 10);
	public static final Font TWEET_FONT_SMALL_BOLD = new Font("Helvetica", Font.BOLD, 10);
	
	// Date display formats
	public static final DateFormat TWEET_TIMESTAMP_FORMAT = new SimpleDateFormat("H:mm a - dd MMM yyyy", Locale.ENGLISH);
	public static final DateFormat TWEET_DATE_DISPLAY_FORMAT = new SimpleDateFormat("HH:mm z EEE yyyy/MM/dd", Locale.ENGLISH);
}
