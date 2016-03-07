/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines the icon used for the "next frame" button.
 *****************************************************************************/

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class NextIcon extends PlayIcon{
	int vertLineWidth;
	
	public NextIcon(int height, Color color){
		super(height, color);
		calcVertLineWidth();
	}
	
	public NextIcon(int width, int height, Color color){
		super(width, height, color);
		calcVertLineWidth();
	}
	
	protected void calcVertLineWidth(){
		vertLineWidth = (width/5);
		if(vertLineWidth == 0){
			vertLineWidth = 1;
		}
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y){
		super.paintIcon(c, g, x, y);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(color);
		g2.fillRect(x + width - vertLineWidth, y, vertLineWidth, height);
	}
}
