/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements a mutable version of JMapViewer's MapMarkerCircle
	class. The extended functionality includes dynamically changing the
	markers' color and size.
 *****************************************************************************/

import org.openstreetmap.gui.jmapviewer.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class CustomMapMarker extends MapMarkerCircle{
	public boolean highlighted;
	public int radius;
	
    public CustomMapMarker(Color color, double lat, double lon, int radius, boolean highlighted){
    	super(null, null, new Coordinate(lat, lon), radius, STYLE.FIXED, new Style(null, color, null, getDefaultFont()));
    	
    	this.radius = radius;
    	this.highlighted = highlighted;
    }
    
    public void setRadius(int radius){
	   this.radius = radius;
    }
    
    @Override
    public void paint(Graphics g, Point position, int i){
    	int diameter = radius * 2;
    	
    	if(g instanceof Graphics2D && getBackColor() != null){
	    	Graphics2D g2 = (Graphics2D)g;
	    	
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    	
	    	Composite oldComposite = g2.getComposite();
	    	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
	    	g2.setPaint(getBackColor());
	    	g2.fillOval(position.x - radius, position.y - radius, diameter, diameter);
	    	g2.setComposite(oldComposite);
    	}
    }
}
