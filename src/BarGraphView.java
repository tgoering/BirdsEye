/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements the bar graph that displays the frequencies of words 
	related to the searched keyword.
 *****************************************************************************/

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BarGraphView extends JPanel{
	private static final long serialVersionUID = 1L;
	public VisApp visApp;
	public TreeMap<String, List<String>> cachedWords = new TreeMap<String, List<String>>();
	public TreeMap<String, List<Integer>> cachedFreqs = new TreeMap<String, List<Integer>>();
	protected List<String> words;
	protected List<Integer> frequencies;
	protected int maxFrequency;
	protected JLabel[] wordLabels;
	protected JLabel[] freqLabels;
	protected Rectangle[] bars;
	protected Line2D axis;
	protected final int numBars = 10;
	protected final int barheight = 18;
	protected final int axisLocation = 85;
	protected final int borderSize = 10;
	protected final int hpad = 2;
	protected final int vpad = 2;
	
	public BarGraphView(VisApp visApp){
		this.visApp = visApp;
		this.setLayout(null);
		
		initializeLabels();
		initializeGraph();
		addListeners();
	}
	
	protected void initializeLabels(){
		wordLabels = new JLabel[numBars];
		freqLabels = new JLabel[numBars];
		
		for(int i = 0; i < numBars; i++){
			JLabel label = new JLabel();
			label.setHorizontalAlignment(JLabel.RIGHT);
			wordLabels[i] = label;
			this.add(label);
			
			label = new JLabel();
			label.setHorizontalAlignment(JLabel.RIGHT);
			label.setForeground(Color.WHITE);
			freqLabels[i] = label;
			this.add(label);
		}
	}
	
	// Create geometric shapes needed for the graph display
	protected void initializeGraph(){
		axis = new Line2D.Double();
		
		bars = new Rectangle[numBars];
		for(int i = 0; i < numBars; i++){
			Rectangle rect = new Rectangle();
			bars[i] = rect;
		}
	}
	
	// Add any necessary listeners
	protected void addListeners(){
		for(JLabel label : freqLabels){
			label.addMouseListener(new MouseAdapter(){
				
				// Highlight bars and use hand cursor as the mouse hovers over bars
				public void mouseEntered(MouseEvent e){
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					JLabel label = (JLabel)e.getSource();
					label.setBackground(InterfaceConstants.TWITTER_BLUE_HIGHLIGHTED);
					label.setOpaque(true);
				}
				
				// Revert to normal when the mouse stops hovering
				public void mouseExited(MouseEvent e){
					setCursor(Cursor.getDefaultCursor());
					JLabel label = (JLabel)e.getSource();
					label.setBackground(InterfaceConstants.TWITTER_BLUE);
					label.setOpaque(false);
				}
				
				// Search for the word when the mouse clicks a bar
				// Search for the existing keyword ORed with the word if the mouse control clicks
			    public void mouseClicked(MouseEvent event){
			    	for(int i = 0; i < freqLabels.length; i++){
			    		if(freqLabels[i].equals(event.getSource())){
			    			if(event.isControlDown()){
			    				visApp.searchKeyword(String.format("%s|%s", visApp.getKeyword(), words.get(i)));
			    			}
			    			else{
			    				visApp.searchKeyword(words.get(i));
			    			}
			    			return;
			    		}
			    	}
			    }
			});
		}
	}
	
	public void setData(String keyword, List<String> words, List<Integer> frequencies){
		this.words = words;
		this.frequencies = frequencies;
		
		// Cache the word for faster lookup later
		cachedWords.put(keyword, words);
		cachedFreqs.put(keyword, frequencies);
	
		calculateMaxFrequency();
		updateLabelText();
		
		layOutLabels();
		layOutGraph();
		repaint();
	}
	
	// Check if a keyword's data is cached
	public boolean containsData(String keyword){
		if(cachedWords.containsKey(keyword)){
			return true;
		}
		else{
			return false;
		}
	}
	
	// Use a keyword's cached data
	public void useData(String keyword){
		words = cachedWords.get(keyword);
		frequencies = cachedFreqs.get(keyword);
		
		calculateMaxFrequency();
		updateLabelText();
		
		layOutLabels();
		layOutGraph();
		repaint();
	}
	
	// Find the max frequency for the given set of words
	protected void calculateMaxFrequency(){
		if(frequencies.size() == 0){
			maxFrequency = 0;
		}
		else{
			maxFrequency = frequencies.get(0);
		}
	}
	
	protected void updateLabelText(){
		// For the given words, update the text and tooltip for each frequency and word label
		for(int i = 0; i < words.size(); i++){
			String word = words.get(i);
			int freq = frequencies.get(i);
			
			wordLabels[i].setText(word);
			wordLabels[i].setToolTipText(String.format("Word: %s, Frequency: %d", word, freq));
			
			freqLabels[i].setText(String.format("%d", freq));
			freqLabels[i].setToolTipText(String.format("Word: %s, Frequency: %d", word, freq));
		}
		
		// Hide any remaining unused labels
		for(int i = words.size(); i < numBars; i++){
			wordLabels[i].setText("");
			wordLabels[i].setToolTipText(null);
			
			freqLabels[i].setText("");
			freqLabels[i].setToolTipText(null);
		}
	}
	
	protected void layOutLabels(){
		// For the given words, position the word and frequency labels
		for(int i = 0; i < words.size(); i++){
			int x = borderSize;
			int y = borderSize + (barheight * i);
			int width = axisLocation - borderSize - hpad;
			int height = barheight + vpad;
			wordLabels[i].setBounds(x, y, width, height);
			
			x = axisLocation + hpad;
			y = borderSize + (barheight * i) + (vpad / 2);
			width = normalizeWidth(frequencies.get(i), 0., maxFrequency, 0, 300-borderSize-axisLocation) - (hpad * 2);
			height = barheight - vpad;
			freqLabels[i].setBounds(x, y, width, height);
		}
		
		// Hide any remaining unused labels
		for(int i = words.size(); i < numBars; i++){
			wordLabels[i].setBounds(0, 0, 0, 0);
			freqLabels[i].setBounds(0, 0, 0, 0);
		}
	}
	
	protected void layOutGraph(){
		// Position the axis
		int x1 = axisLocation;
		int y1 = borderSize;
		int y2 = borderSize + (barheight * words.size());
		axis.setLine(x1, y1, x1, y2);
		
		// For the given words, scale and position the bars
		for(int i = 0; i < words.size(); i++){
			int x = axisLocation;
			int y = borderSize + (barheight * i) + (vpad/2);
			int width = normalizeWidth(frequencies.get(i), 0., maxFrequency, 0, 300-borderSize-axisLocation);
			int height = barheight - vpad;
			
			bars[i].setBounds(x, y, width, height);
		}
		
		// Hide the remaining bars
		for(int i = words.size(); i < numBars; i++){
			bars[i].setBounds(0, 0, 0, 0);
		}
	}
	
	// Convert an integer frequency to a normalized bar width value
	protected int normalizeWidth(double value, double minValue, double maxValue, int offset, int plotWidth){
		double norm = (value - minValue) / (maxValue - minValue);
		int x = offset + (int)(Math.round(norm * plotWidth));
		return x;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		// Fill the background
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		// Draw the bars
		g2.setColor(InterfaceConstants.TWITTER_BLUE);
		if(bars != null){
			for(Rectangle rect : bars){
				g2.fill(rect);
			}
		}
		
		// Draw the axis line
		g2.setColor(InterfaceConstants.TWITTER_GRAY);
		g2.draw(axis);
	}
}
