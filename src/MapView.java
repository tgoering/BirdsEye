/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements the interactive map view which displays Tweets as 
	data points at their geographic location. It uses the JMapViewer library
	to display the map.
 *****************************************************************************/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import javax.swing.JLabel;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;

public class MapView extends JPanel{
    private static final long serialVersionUID = 1L;
    protected VisApp visApp;
    
	protected Deque <List<TweetObject>> Tweets = new ArrayDeque<List<TweetObject>>();
	protected Deque <List<CustomMapMarker>> markers = new ArrayDeque<List<CustomMapMarker>>();
	
	protected JPanel infoPanel;
    protected JLabel mouseCoordinateLabel, scaleLabel;
	protected Line2D scale, scaleL, scaleR;
	protected int[] arbitraryMeters = {5000000, 2500000, 1000000, 500000, 250000, 100000, 50000, 25000, 10000, 5000, 2500, 
			1000, 500, 250, 100, 50, 25, 10, 5, 1};
	
	// Map tile source constants
	public static final int POSITRON_LITE = 0;
	public static final int POSITRON = 1;
	public static final int DARK_MATTER_LITE = 2;
	public static final int DARK_MATTER = 3;
	
	// Map marker size constants
	public static final int SMALL_RADIUS = 2;
	public static final int MEDIUM_RADIUS = 3;
	public static final int LARGE_RADIUS = 4;
	
	// Map marker display duration constants
	public static final int SHORT_DURATION = 10;
	public static final int MEDIUM_DURATION = 30;
	public static final int LONG_DURATION = 60;
	
	// Default options
	protected int markerDuration = MEDIUM_DURATION;
	protected int markerSize = MEDIUM_RADIUS;
	protected boolean highlightInfluentialTweets = true;
	
	protected int highlightThreshold = 5000;
	protected int numSizes = 6;
	protected int stepSize = highlightThreshold / numSizes;
	
	protected int infoPanelHpad = 8;
	
    protected JMapViewerTree treeMap;
    
	public MapView(VisApp visApp){
		this.visApp = visApp;
		this.setLayout(new BorderLayout());

        initializeMap();
        initializeInfoPanel();
        updateScale();
        addListeners();
    }
	
	// Create the info panel that holds the scale and map coordinates
	protected void initializeInfoPanel(){
        infoPanel = new JPanel(new GridLayout(1, 2)){
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g){
        		Graphics2D g2 = (Graphics2D)g;
        		g2.setColor(getBackground());
        		g2.fill(getBounds());
        		if(scale != null){
        			g2.setColor(InterfaceConstants.TWITTER_DARK_GRAY);
        			g2.draw(scale);
        			g2.draw(scaleL);
        			g2.draw(scaleR);
        		}
        	}
        };
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setPreferredSize(new Dimension(20, 20));
        
        // Initialize labels
        scaleLabel = new JLabel();
        scaleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        scaleLabel.setVerticalAlignment(SwingConstants.CENTER);
        scaleLabel.setPreferredSize(new Dimension(100, 20));
        infoPanel.add(scaleLabel);
        
        mouseCoordinateLabel = new JLabel();
        mouseCoordinateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mouseCoordinateLabel.setVerticalAlignment(SwingConstants.CENTER);
        mouseCoordinateLabel.setPreferredSize(new Dimension(200, 20));
        mouseCoordinateLabel.setBorder(new EmptyBorder(0, 0, 0, infoPanelHpad));
        infoPanel.add(mouseCoordinateLabel);
        
        scale = new Line2D.Double();
        scaleL = new Line2D.Double();
        scaleR = new Line2D.Double();
        
        this.add(infoPanel, BorderLayout.NORTH);
	}
	
	protected void initializeMap(){
		treeMap = new JMapViewerTree("Tweets");
		
		// Create the map tiles and set up the map using the default settings
        map().setTileSource((new ModifiedOsmTileSource.PositronLite()));
        map().setTileLoader(new OsmTileLoader(map()));
        map().setScrollWrapEnabled(true);
        map().setZoom(2);
        map().setZoomContolsVisible(false);

        this.add(treeMap, BorderLayout.CENTER);
	}
	
	protected void addListeners(){
		 // Whenever a point is clicked, display the corresponding Tweet in another view
        map().addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent event){
                if(event.getButton() == MouseEvent.BUTTON1){
                    map().getAttribution().handleAttribution(event.getPoint(), true);
                }
                
                Point p = event.getPoint();

                Iterator<List<CustomMapMarker>> mit = markers.iterator();
                Iterator<List<TweetObject>> tit = Tweets.iterator();
                
                int j = 0;
                while(mit.hasNext()){
                	List<CustomMapMarker> markerList = mit.next();
                	List<TweetObject> tweetList = tit.next();
                	for(int i = 0; i < tweetList.size(); i++){
                		CustomMapMarker marker = markerList.get(i);
                		
	                	Point m = map().getMapPosition(marker.getCoordinate());
	                	int radius = marker.radius;
	                	if(m != null){
	                		double dist = Math.sqrt((p.x-m.x)*(p.x-m.x) + (p.y-m.y)*(p.y-m.y));
	                		if(dist <= radius){
	                			int animationIndex = visApp.getAnimationIndex();
	                			visApp.updateTweetInfoView(tweetList.get(i), animationIndex-j);
	                			return;
	                		}
	                	}
                	}
                	j++;
                }
            }
        	
        	public void mouseExited(MouseEvent e){
                mouseCoordinateLabel.setText("");
        	}
        });
        
        // Change the mouse cursor as points are hovered over
        map().addMouseMotionListener(new MouseAdapter(){
            public void mouseMoved(MouseEvent event){
                Point p = event.getPoint();
                
                Coordinate originalCoord = (Coordinate)map().getPosition(p);
                double originalLon = originalCoord.getLon();
                
                // map().getPosition() returns incorrect coordinates (sometimes greater than 180
                // or less than -180, so correct these values 
                Coordinate correctedCoord = (Coordinate)map().getPosition(p);
                while(correctedCoord.getLon() > 180){
                	correctedCoord.setLon(correctedCoord.getLon() - 360);
                }
                while(correctedCoord.getLon() < -180){
                	correctedCoord.setLon(correctedCoord.getLon() + 360);
                }
                
                double correctedLat = correctedCoord.getLat();
                double correctedLon = correctedCoord.getLon();
                
                if(correctedLat > 85 || correctedLat < -85){
                	mouseCoordinateLabel.setText("");
                }
                else if(!map().isScrollWrapEnabled() && (originalLon > 180 || originalLon < -180)){
                	mouseCoordinateLabel.setText("");
                }
                else{
                	mouseCoordinateLabel.setText(String.format("%6.2f° N, %6.2f° W", correctedLat, correctedLon));
                }
                
                Point m2 = map().getMapPosition(originalCoord);
                
                boolean pointHovered = false;
                for(List<CustomMapMarker> markerList : markers){
                	for(CustomMapMarker marker : markerList){
                		
                		for(Coordinate markerCoord = marker.getCoordinate(); 
                				markerCoord.getLon() <= originalCoord.getLon(); 
                				markerCoord.setLon(markerCoord.getLon() + 360)){
                			Point m = map().getMapPosition(markerCoord);
    	                	int radius = marker.radius;
    	                	if(m2 != null && m != null){
    	                		double dist = Math.sqrt((m2.x-m.x)*(m2.x-m.x) + (m2.y-m.y)*(m2.y-m.y));
    	                		if(dist <= radius){
    	                			pointHovered = true;
    	                			break;
    	                		}
    	                	}
                		}
                		for(Coordinate ctemp = marker.getCoordinate();
                				ctemp.getLon() >= originalCoord.getLon();
                				ctemp.setLon(ctemp.getLon() - 360)){
                			Point m = map().getMapPosition(ctemp);
    	                	int radius = marker.radius;
    	                	if(m2 != null && m != null){
    	                		double dist = Math.sqrt((m2.x-m.x)*(m2.x-m.x) + (m2.y-m.y)*(m2.y-m.y));
    	                		if(dist <= radius){
    	                			pointHovered = true;
    	                			break;
    	                		}
    	                	}
                		} 
                		
	                	if(pointHovered){
	                		break;
	                	}
                	}
                	if(pointHovered){
                		break;
                	}
                }			

                boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
                if(cursorHand || pointHovered){
                    map().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else{
                    map().setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        map().addJMVListener(new JMapViewerEventListener(){
			public void processCommand(JMVCommandEvent e){
				if(e.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) || e.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)){
		            updateScale();
		        }
			}
        });
	}
	
    // Place new Tweets at the head of the queue and recalculate the color of all markers
	// Used when traversing the data set forwards
    protected void setDataFirst(List<TweetObject> tweets){
    	if(tweets != null){
    		
    		// Create a list of map markers from the new minute of data
    		List<CustomMapMarker> temp = new ArrayList<CustomMapMarker>();
    		for(TweetObject tweet : tweets){
    			
    			// Calculate the color and size of the new map marker
    			int size = (tweet.followerCount / stepSize) + markerSize;
    			boolean highlight = false;
    			if(size >= numSizes + markerSize){
            		size = numSizes + markerSize;
            		highlight = true;
            	}
    			temp.add(new CustomMapMarker(null, tweet.latitude, tweet.longitude, size, highlight));
    		}
    		
    		// Plot the markers and add them to the head of a doubly ended queue
    		for(CustomMapMarker m : temp){
    			map().addMapMarker(m);
    		}
    		markers.addFirst(temp);
    		Tweets.addFirst(tweets);
    		
    		// Remove any markers older than 30 minutes or whatever the set duration
    		if(Tweets.size() > markerDuration){
    			
    			// Remove the markers from the plot and the doubly ended queue
    			List<CustomMapMarker> tail = markers.getLast();
				for(CustomMapMarker m : tail){
					map().removeMapMarker(m);
				}
    			markers.removeLast();
    			Tweets.removeLast();
    		}
    	}
    	
    	recolorMarkers();
    	repaint();
    }
    
    // Place new Tweets at the tail of the queue and recalculate the color of all markers
    // Used when traversing the data set backwards
    protected void setDataLast(List<TweetObject> tweets){
    	if(tweets != null){
    		
    		// Create a list of map markers from the new minute of data
    		List<CustomMapMarker> temp = new ArrayList<CustomMapMarker>();
    		for(TweetObject tweet : tweets){
    			
    			// Calculate the color and size of the new map marker
    			int size = (tweet.followerCount / stepSize) + markerSize;
    			boolean highlight = false;
    			if(size >= numSizes + markerSize){
            		size = numSizes + markerSize;
            		highlight = true;
            	}
    			
    			temp.add(new CustomMapMarker(null, tweet.latitude, tweet.longitude, size, highlight));
    		}
    		
			// Plot the markers and add them to the tail of the queue
    		for(CustomMapMarker m : temp){
    			map().addMapMarker(m);
    		}
    		markers.addLast(temp);
    		Tweets.addLast(tweets);
    	}
    	
    	// Because we're traversing backwards, we must always remove markers from the head of the queue 
		List<CustomMapMarker> head = markers.getFirst();
		for(CustomMapMarker m : head){
			map().removeMapMarker(m);
		}
		markers.removeFirst();
		Tweets.removeFirst();
		
    	recolorMarkers();
    	repaint();
    }
    
    // Calculate the alpha of all the markers, decrementing it to simulate fading
    protected void recolorMarkers(){
    	float decay = 255 / (markerDuration+1);
    	int markerAlpha = 255;
    	
    	for(List<CustomMapMarker> temp : markers){
    		Color normalMarkerColor = new Color(85, 172, 238, markerAlpha);
    		Color highlightedMarkerColor = new Color(255, 128, 0, markerAlpha);
    		for(CustomMapMarker m : temp){
    			if(highlightInfluentialTweets && m.highlighted){
    				m.setBackColor(highlightedMarkerColor);
    			}
    			else{
    				m.setBackColor(normalMarkerColor);
    			}
    		}
    		
    		markerAlpha -= decay;
    	}
    }

	protected JMapViewer map(){
		return treeMap.getViewer();
	}
    
	// Update the map's scale as the user interacts with the map
    protected void updateScale(){
    	double mperpix = map().getMeterPerPixel();
    	for(int meter : arbitraryMeters){
    		double lineWidth = meter / mperpix;
    		if(lineWidth < 100 && lineWidth > 25){
    			scale.setLine(infoPanelHpad, 13, infoPanelHpad + lineWidth, 13);
    			scaleL.setLine(infoPanelHpad, 13, infoPanelHpad, 7);
    			scaleR.setLine(infoPanelHpad + lineWidth, 13, infoPanelHpad + lineWidth, 7);
    			
    			if(meter / 1000 > 0){
    				scaleLabel.setText(String.format("%d km", meter/1000));
    			}
    			else{
    				scaleLabel.setText(String.format("%d m", meter));
    			}
    			
    			scaleLabel.setBorder(new EmptyBorder(0, infoPanelHpad + (int)lineWidth + 5, 0, 0));
    			
    			infoPanel.repaint();
    			return;
    		}
    	}
    }
    
    // Tiling the map horizontally
    public boolean isScrollWrapEnabled(){
    	return map().isScrollWrapEnabled();
    }
    public void setScrollWrapEnabled(boolean b){
    	map().setScrollWrapEnabled(b);
    	repaint();
    }
    
    // Show or hide the panel that displays mouse coordinates and meters/pixel
    public boolean isInfoPanelVisible(){
    	return infoPanel.isVisible();
    }
    public void setInfoPanelVisible(boolean b){
    	infoPanel.setVisible(b);
    }
    
    // Turn on or off influential Tweets being highlighted in orange
    public void toggleHighlightedTweets(){
    	highlightInfluentialTweets = !highlightInfluentialTweets;
    	recolorMarkers();
    	repaint();
    }
    
    // Choose the map's tile source
    public void setMapType(int type){
    	if(type == POSITRON_LITE){
    		map().setTileSource(new ModifiedOsmTileSource.PositronLite());
    	}
    	else if(type == POSITRON){
    		map().setTileSource(new ModifiedOsmTileSource.Positron());
    	}
    	else if(type == DARK_MATTER_LITE){
    		map().setTileSource(new ModifiedOsmTileSource.DarkMatterLite());
    	}
    	else if(type == DARK_MATTER){
    		map().setTileSource(new ModifiedOsmTileSource.DarkMatter());
    	}
    }
    
    // Get and set how long markers are displayed on the map
    public int getMarkerDuration(){
    	return markerDuration;
    }
    public void setMarkerDuration(int duration){
    	markerDuration = duration;
    	visApp.setAnimationIndex(visApp.getAnimationIndex());
    	recolorMarkers();
    	repaint();
    }
    
    // Get and set the size of the markers
    public int getMarkerSize(){
    	return markerSize;
    }
    public void setMarkerSize(int size){
    	int oldMarkerSize = markerSize;
    	int sizeDifference = oldMarkerSize - size;
    	markerSize = size;
    	
    	for(List<CustomMapMarker> temp : markers){
    		for(CustomMapMarker m : temp){
    			m.setRadius(m.radius - sizeDifference);
    		}
    	}

    	repaint();
    }
    
    public void resetAnimation(){
    	Tweets = new ArrayDeque<List<TweetObject>>();
    	markers = new ArrayDeque<List<CustomMapMarker>>();
    	map().removeAllMapMarkers();
    }
}
