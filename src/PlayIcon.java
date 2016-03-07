/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines the icon used for the "play" button.
 *****************************************************************************/

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class PlayIcon implements Icon{
	protected int width, height;
	protected Color color;

	public PlayIcon(int height, Color color){
		this.height = height;
		this.width = (int)((4./5.) * height);
		this.color = color;
	}
	
	public PlayIcon(int width, int height, Color color){
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public Color getIconColor(){
		return color;
	}
	
	public void setIconColor(Color color){
		this.color = color;
	}
	
	public int getIconWidth(){
		return width;
	}
	
	public void setIconWidth(int width){
		this.width = width;
	}
	
	public int getIconHeight(){
		return height;
	}
	
	public void setIconHeight(int height){
		this.height = height;
	}

	public void paintIcon(Component c, Graphics g, int x, int y){
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int[] xCoords = {x, x, x + width};
		int[] yCoords = {y, y + height, y + (height / 2)};
		
		g2.setColor(color);
		g2.fillPolygon(xCoords, yCoords, 3);
	}
}
