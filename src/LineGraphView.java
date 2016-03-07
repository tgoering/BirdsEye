/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements the interactive line graph that displays the 
	frequency of the searched keyword over time.
 *****************************************************************************/

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LineGraphView extends JPanel{
	private static final long serialVersionUID = 1L;
	public VisApp visApp;
	
	protected List<Integer> xValues, yValues;
	protected List<Integer> xCoords, yCoords;
	protected List<Integer> xCoordsOnInterval, yCoordsOnInterval;
	protected List<Calendar> dates;
	protected String keyword;
	
	protected Rectangle plotRectangle = new Rectangle();
	protected Line2D xAxis = new Line2D.Double();
	protected Line2D yAxis = new Line2D.Double();
	protected ArrayList<Line2D> tickMarks = new ArrayList<Line2D>();
	protected Rectangle timeBeforeSelectedInterval = new Rectangle();
	protected Rectangle timeAfterSelectedInterval = new Rectangle();
	
	protected int animationIndex = 0, animationStart = 0, animationEnd = -1;
	protected int xMin, xMax, yMin, yMax;
	
	protected JLabel graphTitle = new JLabel();
	protected JLabel yMaxLabel = new JLabel();
	protected JLabel yMinLabel = new JLabel();
	protected JLabel mouseCoordinateLabel = new JLabel();
	protected JLabel animationLabel = new JLabel();
	protected ArrayList<JLabel> xLabels = new ArrayList<JLabel>();
	
	protected Line2D mouseLine = new Line2D.Double();
	protected Line2D animationLine = new Line2D.Double();
	protected Ellipse2D animationCircle = new Ellipse2D.Double();
	
	protected int dragStart = -1;
	protected int dragEnd = -1;
	protected Rectangle dragInterval = new Rectangle();
	
	protected boolean isFullScale = true;
	protected boolean grayBeforeSelectedInterval = false;
	protected boolean grayAfterSelectedInterval = false;
	
	protected Font tweetFont = new Font("Helvetica", Font.PLAIN, 12);
	protected Font tweetFontBold = new Font("Helvetica", Font.BOLD, 12);
	protected Font tweetFontSmall = new Font("Helvetica", Font.PLAIN, 10);
	
	protected final int borderSize = 40;
	
	public LineGraphView(VisApp visApp){
		this.visApp = visApp;
		
		addListeners();

		this.setLayout(null);
		initializeLabels();
	}
	
	protected void initializeLabels(){
		graphTitle.setHorizontalAlignment(JLabel.CENTER);
		graphTitle.setFont(new Font("default", Font.BOLD, 12));
		this.add(graphTitle);
		
		yMaxLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		yMaxLabel.setHorizontalAlignment(JLabel.RIGHT);
		yMaxLabel.setVerticalTextPosition(SwingConstants.CENTER);
		yMaxLabel.setVerticalAlignment(JLabel.CENTER);
		this.add(yMaxLabel);
		
		yMinLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		yMinLabel.setHorizontalAlignment(JLabel.RIGHT);
		yMinLabel.setVerticalTextPosition(SwingConstants.CENTER);
		yMinLabel.setVerticalAlignment(JLabel.CENTER);
		this.add(yMinLabel);
		
		mouseCoordinateLabel.setHorizontalAlignment(JLabel.RIGHT);
		yMinLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.add(mouseCoordinateLabel);
		
		animationLabel.setVerticalTextPosition(SwingConstants.CENTER);
		this.add(animationLabel);
	}
	
	protected void addListeners(){
		// As the view is resized, lay out its components again and repaint
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				layOutPlot();
				layOutLabels();
				layOutDataPoints();
				updateAnimation();
				
				repaint();
			}
		});
		
		// Adjust the line graph scale with the scroll wheel
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				int direction = e.getWheelRotation();
				
				// When the mouse is scrolled forward, scale the graph to the selected interval
				if(direction < 0 && isFullScale){
					visApp.setLineGraphFullScale(false);
				}
				// When the mouse is scrolled backward, draw the graph full scale
				else if(direction > 0 && !isFullScale){
					visApp.setLineGraphFullScale(true);
				}
			}
		});
		
		addMouseMotionListener(new MouseAdapter(){
			// As the mouse is moved, update the line that indicates the closest x-value
			public void mouseMoved(MouseEvent e){
				updateMouseMarkers(e.getX(), e.getY());
			}
			
			// As the mouse is dragged, update the highlighted region that represents the selected interval
			public void mouseDragged(MouseEvent e){
				int x = e.getX();
				int y = e.getY();
				
				updateMouseMarkers(x, y);
				
				// Only update the highlighted region if the right mouse button is held
				if(dragStart != -1){
					
					// If the mouse is inside the plot, calculate and set the bounds of the highlight rectangle
					if(plotRectangle.contains(x, y)){
						dragEnd = (int)Math.round(toDataX(x, xMin, xMax, borderSize, plotRectangle.width));
						
						int start, end;
						if(dragEnd > dragStart){
							if(isFullScale){
								start = xCoordsOnInterval.get(dragStart);
								end = xCoordsOnInterval.get(dragEnd);
							}
							else{
								start = xCoordsOnInterval.get(dragStart - animationStart);
								end = xCoordsOnInterval.get(dragEnd - animationStart);
							}
						}
						else{
							if(isFullScale){
								start = xCoordsOnInterval.get(dragEnd);
								end = xCoordsOnInterval.get(dragStart);
							}
							else{
								start = xCoordsOnInterval.get(dragEnd - animationStart);
								end = xCoordsOnInterval.get(dragStart - animationStart);
							}
						}
						dragInterval.setBounds(start, plotRectangle.y, end-start, plotRectangle.height);
					}
					
					// If the mouse is to the left or right of the plot, set the bounds of the highlighted rectangle
					// to either the first possible index or the last possible index
					else if(plotRectangle.contains(plotRectangle.x, y)){
						
						int start, end;
						if(x > plotRectangle.x){
							dragEnd = (int)Math.round(toDataX(xMax, xMin, xMax, borderSize, plotRectangle.width));
							
							if(isFullScale){
								start = xCoordsOnInterval.get(dragStart);
							}
							else{
								start = xCoordsOnInterval.get(dragStart - animationStart);
							}
							end = xCoordsOnInterval.get(xCoordsOnInterval.size()-1);
						}
						else{
							dragEnd = (int)Math.round(toDataX(xMin, xMin, xMax, borderSize, plotRectangle.width));
							
							start = xCoordsOnInterval.get(0);
							if(isFullScale){
								end = xCoordsOnInterval.get(dragStart);
							}
							else{
								end = xCoordsOnInterval.get(dragStart - animationStart);
							}
						}
						
						dragInterval.setBounds(start, plotRectangle.y, end-start, plotRectangle.height);
					}
					
					// When the mouse is dragged away from the plot, stop drawing the highlight rectangle
					else{
						dragInterval.setBounds(0, 0, 0, 0);
					}
				}
			}
		});
		
		// Scrub with right mouse button to set time constraints and set animation index with left mouse button
		addMouseListener(new MouseAdapter(){
			
			// When the right mouse button is pressed inside the plot, begin drawing the highlighted region
			// that represents the selected interval
			public void mousePressed(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON3){
					setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					
					int x = e.getX();
					int y = e.getY();
					
					if(plotRectangle.contains(x, y)){
						dragStart = (int)Math.round(toDataX(x, xMin, xMax, borderSize, plotRectangle.width));
						dragInterval.setBounds(dragStart, plotRectangle.y, 0, plotRectangle.height);
					}
				}
			}
			
			// When the left mouse button is released, set the animation to the closest x-value
			public void mouseReleased(MouseEvent e){
				
				// Set the animation to whatever time the user clicks
				if(e.getButton() == MouseEvent.BUTTON1){
					int x = e.getX();
					int y = e.getY();
					
					if(plotRectangle.contains(x, y)){
						int closestIndex = (int)Math.round(toDataX(x, xMin, xMax, borderSize, plotRectangle.width));
						
						// If the user clicks on the grayed out area before the selected interval, set the animation to the beginning
						if(isFullScale && closestIndex < animationStart){
							visApp.setAnimationIndex(animationStart);
						}
						// If the user clicks on the grayed out area after the selected interval, set the animation to the end
						else if(isFullScale && closestIndex > animationEnd){
							visApp.setAnimationIndex(animationEnd);
						}
						else{
							visApp.setAnimationIndex(closestIndex);
						}
					}
				}
				
				// Set the time constraints to the interval the user dragged with right click
				else if(e.getButton() == MouseEvent.BUTTON3){
					setCursor(Cursor.getDefaultCursor());
					int x = e.getX();
					int y = e.getY();
					
					// If the right click is released over the plot, set the time interval to their dragged range
					if(plotRectangle.contains(x, y) && dragStart != -1){
						dragEnd = (int)Math.round(toDataX(x, xMin, xMax, borderSize, plotRectangle.width));
						
						// Reset to the original time interval if user just right clicks
						if(dragStart == dragEnd){
							visApp.setTimeInterval(0, dates.size()-1);
						}
						else if(dragStart > dragEnd){
							visApp.setTimeInterval(dragEnd, dragStart);
						}
						else{
							visApp.setTimeInterval(dragStart, dragEnd);
						}
					}
					// If the right click is released outside the end of the plot, set the start or end of the
					// time interval to the closest time
					else if(plotRectangle.contains(plotRectangle.x, y) && dragStart != -1){
						if(x > plotRectangle.x){
							if(isFullScale){
								visApp.setTimeInterval(dragStart, dates.size()-1);
							}
							else{
								visApp.setTimeInterval(dragStart, xCoordsOnInterval.size()-1 + animationStart);
							}
						}
						else{
							if(isFullScale){
								visApp.setTimeInterval(0, dragStart);
							}
							else{
								visApp.setTimeInterval(animationStart, dragStart);
							}
						}
					}
					
					updateMouseMarkers(x, y);
					dragStart = -1;
					dragEnd = -1;
					dragInterval.setBounds(0, 0, 0, 0);
				}
			}
		});
	}
	
	// Setters for the information the line graph displays
	public void setData(List<Integer> yValues, List<Calendar> dates, String keyword){
		this.yValues = yValues;
		this.dates = dates;
		this.keyword = keyword;

		xValues = new ArrayList<Integer>();
		for(int i = 0; i < yValues.size(); i++){
			xValues.add(i);
		}

		if(animationEnd == -1){
			animationEnd = dates.size()-1;
		}
		
		updateGraphTitle();
		
		calculateMinMax();
		updateMinMaxLabels();
		
		layOutPlot();
		layOutLabels();
		layOutDataPoints();
		updateAnimation();
		
		repaint();
	}
	
	public void setAnimationIndex(int animationIndex){
		this.animationIndex = animationIndex;
		
		updateAnimation();
		repaint();
	}
	
	public void setTimeInterval(int start, int end){
		animationStart = start;
		animationEnd = end;
		animationIndex = animationStart;
		
		if(start == 0){
			grayBeforeSelectedInterval = false;
		}
		else{
			grayBeforeSelectedInterval = true;
		}
		
		if(end == dates.size()-1){
			grayAfterSelectedInterval = false;
		}
		else{
			grayAfterSelectedInterval = true;
		}
		
		updateGraphTitle();
		
		calculateMinMax();
		updateMinMaxLabels();
		
		layOutPlot();
		layOutDataPoints();
		updateAnimation();
		
		repaint();
	}

	// Get and set if the line graph currently displays the entire time range or just a selected portion
	public boolean isFullScale(){
		return isFullScale;
	}
	
	public void setFullScale(boolean b){
		isFullScale = b;
		
		updateGraphTitle();
		
		calculateMinMax();
		
		layOutPlot();
		layOutDataPoints();	
		updateAnimation();
		
		repaint();
	}
	
	// Adjust the graph title to display the time range and current keyword
	protected void updateGraphTitle(){
		Date start, end;
		if(this.isFullScale){
			start = dates.get(0).getTime();
			end = dates.get(dates.size()-1).getTime();
		}
		else{
			start = dates.get(animationStart).getTime();
			end = dates.get(animationEnd).getTime();
		}
		String startStr = InterfaceConstants.TWEET_DATE_DISPLAY_FORMAT.format(start);
		String endStr = InterfaceConstants.TWEET_DATE_DISPLAY_FORMAT.format(end);
		
		if(keyword.equals("")){
			graphTitle.setText(String.format("Frequency of Tweets from %s to %s", 
					startStr, endStr));
		}
		else{
			graphTitle.setText(String.format("Frequency of Tweets containing \"%s\" from %s to %s",
					keyword, startStr, endStr));
		}
	}
	
	protected void updateMinMaxLabels(){
		yMaxLabel.setText(Integer.toString(yMax));
		yMinLabel.setText(Integer.toString(yMin));
	}
	
	protected void updateAnimation(){
		String date = InterfaceConstants.TWEET_DATE_DISPLAY_FORMAT.format(dates.get(animationIndex).getTime());
		animationLabel.setText(String.format("Animation: %s - %d Tweets", date, yValues.get(animationIndex)));
		
		// Set the position of the vertical animation line
		int x, y;
		if(this.isFullScale){
			x = xCoordsOnInterval.get(animationIndex);
		}
		else{
			x = xCoordsOnInterval.get(animationIndex - animationStart);
		}
		int y1 = plotRectangle.y;
		int y2 = plotRectangle.y + plotRectangle.height;
		animationLine.setLine(x, y1, x, y2);
		
		int circleRadius = 4;
		
		// Set the position of the animation circle
		if(this.isFullScale){
			y = yCoordsOnInterval.get(animationIndex);
		}
		else{
			y = yCoordsOnInterval.get(animationIndex - animationStart);
		}
		x -= circleRadius;
		y -= circleRadius;
		animationCircle.setFrame(x, y, circleRadius*2, circleRadius*2);
	}
	
	// Position the markers that correlate to the mouse's movement
	protected void updateMouseMarkers(int x, int y){
		if(plotRectangle.contains(x, y)){
			int closestIndex = (int)Math.round(toDataX(x, xMin, xMax, borderSize, plotRectangle.width));
			String date = InterfaceConstants.TWEET_DATE_DISPLAY_FORMAT.format(dates.get(closestIndex).getTime());
			mouseCoordinateLabel.setText(String.format("Mouse: %s - %d Tweets", date, yValues.get(closestIndex)));
			
			if(this.isFullScale){
				x = xCoordsOnInterval.get(closestIndex);
			}
			else{
				x = xCoordsOnInterval.get(closestIndex - animationStart);
			}
			int y1 = plotRectangle.y;
			int y2 = plotRectangle.y + plotRectangle.height;
			mouseLine.setLine(x, y1, x, y2);
		}
		else{
			mouseCoordinateLabel.setText("");
			mouseLine.setLine(0, 0, 0, 0);
		}
		
		repaint();
	}
	
	// Calculate the minimum and maximum x- and y-values
	protected void calculateMinMax(){
		if(this.isFullScale){
			xMin = 0;
			xMax = dates.size()-1;
		}
		else{
			xMin = animationStart;
			xMax = animationEnd;
		}
		
		yMin = 0;
		yMax = Integer.MIN_VALUE;
		for(int y : yValues){
			if(y > yMax){
				yMax = y;
			}
		}
		
		if(yMax == 0){
			yMax = 1;
		}
	}
	
	protected void layOutPlot(){
		// Calculate the boundaries of the plot
		int x = borderSize;
		int y = borderSize;
		int width = getWidth() - (borderSize * 2);
		int height = getHeight() - (borderSize * 2);
		plotRectangle.setBounds(x, y, width, height);
		
		// Position the x-axis
		int x1 = plotRectangle.x;
		int x2 = plotRectangle.x + plotRectangle.width;
		y = plotRectangle.y + plotRectangle.height;
		xAxis.setLine(x1, y, x2, y);
		
		// Position the y-axis 
		x = plotRectangle.x;
		int y1 = plotRectangle.y;
		int y2 = plotRectangle.y + plotRectangle.height;
		yAxis.setLine(x, y1, x, y2);
		
		// Calculate which tick marks to draw, based on the plot's size
		int numMinutes = xMax-xMin;
		boolean drawMinuteTicks = (numMinutes*7) < plotRectangle.width;
		boolean drawHalfHourTicks = ((numMinutes/30)*7) < plotRectangle.width;
		boolean drawHourTicks = ((numMinutes/60) * 7) < plotRectangle.width;
		
		// Position any tick marks
		tickMarks = new ArrayList<Line2D>();
		for(int i = xMin; i <= xMax; i++){
			Calendar date = dates.get(i);
			int minute = date.get(Calendar.MINUTE);
			
			// Hour tick marks
			if(drawHourTicks && minute == 0){
				x = toScreenX(i, xMin, xMax, borderSize, plotRectangle.width);
				y1 = borderSize + plotRectangle.height;
				y2 = borderSize + plotRectangle.height - 8;
				
				Line2D tickMark = new Line2D.Double(x, y1, x, y2);
				tickMarks.add(tickMark);
			}
			
			// Quarter-hour tick marks
			else if(drawHourTicks && drawHalfHourTicks && (minute == 15 || minute == 30 || minute == 45)){
				x = toScreenX(i, xMin, xMax, borderSize, plotRectangle.width);
				y1 = borderSize + plotRectangle.height;
				y2 = borderSize + plotRectangle.height - 5;
				
				Line2D tickMark = new Line2D.Double(x, y1, x, y2);
				tickMarks.add(tickMark);
			}
			
			// Minute tick marks
			else if(drawHourTicks && drawHalfHourTicks && drawMinuteTicks){
				x = toScreenX(i, xMin, xMax, borderSize, plotRectangle.width);
				y1 = borderSize + plotRectangle.height;
				y2 = borderSize + plotRectangle.height - 2;
				
				Line2D tickMark = new Line2D.Double(x, y1, x, y2);
				tickMarks.add(tickMark);
			}
		}
	}
	
	protected void layOutLabels(){
		// Position the graph's title
		graphTitle.setBounds(0, 10, getWidth(), 20);
		
		// y-axis label constants
		int yLabelPadL = 8;
		int yLabelPadR = 12;
		int yLabelHeight = 16;
		
		// Position the maximum frequency label
		int x = yLabelPadL;
		int y = borderSize - (yLabelHeight / 2);
		int width = borderSize - yLabelPadR;
		int height = yLabelHeight;
		yMaxLabel.setBounds(x, y, width, height);
		
		// Position the minimum frequency label
		x = yLabelPadL;
		y = borderSize + plotRectangle.height - (yLabelHeight / 2);
		width = borderSize - yLabelPadR;
		height = yLabelHeight;
		yMinLabel.setBounds(x, y, width, height);
		
		// Time/frequency label constants
		int timeLabelWidth = 300;
		int timeLabelHeight = 30;
		
		// Position the label that displays the mouse's position
		x = plotRectangle.x + plotRectangle.width - timeLabelWidth;
		y = plotRectangle.y + plotRectangle.height;
		width = timeLabelWidth;
		height = timeLabelHeight;
		mouseCoordinateLabel.setBounds(x, y, width, height);
		
		// Position the label that displays the animation time
		x = plotRectangle.x;
		y = plotRectangle.y + plotRectangle.height;
		width = timeLabelWidth;
		height = timeLabelHeight;
		animationLabel.setBounds(x, y, width, height);
	}
	
	// Calculate the locations of the selected data points
	protected void layOutDataPoints(){
		xCoords = new ArrayList<Integer>();
		yCoords = new ArrayList<Integer>();
		
		// Convert each data point to a screen position
		for(int i = 0; i < xValues.size(); i++){
			xCoords.add(toScreenX(xValues.get(i), xMin, xMax, borderSize, plotRectangle.width));
			yCoords.add(toScreenY(yValues.get(i), yMin, yMax, borderSize, plotRectangle.height));
		}
		
		// Calculate the bounds of the gray area before the selected interval, if there is one
		if(grayBeforeSelectedInterval){
			timeBeforeSelectedInterval.setBounds(borderSize, borderSize, xCoords.get(animationStart) - borderSize, plotRectangle.height);
		}
		else{
			timeBeforeSelectedInterval.setBounds(0, 0, 0, 0);
		}
		
		// Calculate the bounds of the gray area after the selected interval, if there is one
		if(grayAfterSelectedInterval){
			timeAfterSelectedInterval.setBounds(xCoords.get(animationEnd), borderSize, xCoords.get(xCoords.size()-1) - xCoords.get(animationEnd), plotRectangle.height);
		}
		else{
			timeAfterSelectedInterval.setBounds(0, 0, 0, 0);
		}
		
		// Draw all of the data points if the graph is full scale
		if(this.isFullScale){
			xCoordsOnInterval = xCoords;
			yCoordsOnInterval = yCoords;
		}
		// Otherwise, figure out which data points are in the selected interval
		else{
			xCoordsOnInterval = new ArrayList<Integer>();
			yCoordsOnInterval = new ArrayList<Integer>();
			
			for(int i = animationStart; i <= animationEnd; i++){
				xCoordsOnInterval.add(toScreenX(xValues.get(i), animationStart, animationEnd, borderSize, plotRectangle.width));
				yCoordsOnInterval.add(yCoords.get(i));
			}
		}
	}
	
	// Convert a data x-value to screen pixel location
	protected int toScreenX(double value, double minValue, double maxValue, int offset, int plotWidth){
		double norm = (value - minValue) / (maxValue - minValue);
		int x = offset + (int)(Math.round(norm * plotWidth));
		return x;
	}
	
	// Convert a data y-value to screen pixel location
	protected int toScreenY(double value, double minValue, double maxValue, int offset, int plotHeight){
		double norm = 1.0 - ((value - minValue) / (maxValue - minValue));
		int y = offset + (int)(Math.round(norm * plotHeight));
		return y;
	}
	
	// Convert a screen pixel location to a data x-value
	protected double toDataX(double value, double minValue, double maxValue, int offset, int plotWidth){
		double x = (value - (offset)) / plotWidth;
		x = x *(maxValue - minValue) + minValue;
		return x;
	}
	
	// Convert an index on the full scale graph to an index on the selected interval
	protected int toPartialScaleIndex(int index){
		return -1;
	}
	
	// Convert an index on the selected interval to an index on the full scale graph
	protected int toFullScaleIndex(int index){
		return -1;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		// Use anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Grey out unselected time intervals
		if(this.isFullScale){
			g2.setColor(InterfaceConstants.TWITTER_LIGHTER_GRAY);
			g2.fill(timeBeforeSelectedInterval);
			g2.fill(timeAfterSelectedInterval);
		}
		
		// Draw the graph's x- and y-axes
		g2.setColor(InterfaceConstants.TWITTER_GRAY);
		g2.draw(xAxis);
		g2.draw(yAxis);
		
		// Draw the graph's tick marks
		for(Line2D tickMark : tickMarks){
			g2.draw(tickMark);
		}
		
		// Draw a line at the time nearest the mouse's position
		if(mouseLine != null){
			g2.setColor(InterfaceConstants.TWITTER_GRAY);
			g2.draw(mouseLine);
		}

		// Draw lines between the data points
		if(xCoords != null && yCoords != null){
			g2.setColor(InterfaceConstants.TWITTER_BLUE);
			for(int i = 1; i < xCoordsOnInterval.size(); i++){
				int x1 = xCoordsOnInterval.get(i);
				int y1 = yCoordsOnInterval.get(i);
				int x2 = xCoordsOnInterval.get(i-1);
				int y2 = yCoordsOnInterval.get(i-1);
				
				g2.drawLine(x1, y1, x2, y2);
			}
			
			// Draw the line and circle denoting the current time in the animation
			g2.draw(animationLine);
			g2.fill(animationCircle);
		}
		
		// Draw rectangle that represents the user's selected time interval as they drag the mouse
		g2.setColor(InterfaceConstants.TWITTER_BLUE_SEMI_TRANS);
		g2.fill(dragInterval);
	}
}
