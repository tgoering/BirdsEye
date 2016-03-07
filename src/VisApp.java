/******************************************************************************
	Copyright 2016 
	Jeffrey Hamilton
	Tim Goering
	
	The glue that holds the application together, this class holds all
	interface elements and facilitates their interaction.
 *****************************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class VisApp implements ActionListener{
	protected JFrame appFrame;
	
	JMenuItem playPauseMenuItem;
	JMenuItem lineGraphScaleMenuItem;
	
	public Map<Calendar, List<TweetObject>> Tweets;
	public List<List<TweetObject>> tweets;
	public List<Calendar> dates;
	protected String keyword;
	
	protected MapView mapView;
	protected LineGraphView lineGraphView;
	protected OptionsPane optionsPane;
	protected TweetInfoView tweetInfoView;
	protected BarGraphView barGraphView;
	
	protected Timer timer;
	protected int animationIndex, animationStart, animationEnd;
	protected boolean animationIsPlaying;
	protected boolean animationIsLooping;
	
	protected LoadingFilesDialog loadingFilesDialog;
	protected FileParser fileParser;
	
	protected KeywordSearchDialog keywordSearchDialog;
	protected KeywordSearcher keywordSearcher;
	protected Set<String> stopWordsSet;
	
	public static final int QUARTER_SPEED = 200;
	public static final int HALF_SPEED = 100;
	public static final int DEFAULT_SPEED = 50;
	public static final int ONE_AND_A_HALF_SPEED = 33;
	public static final int DOUBLE_SPEED = 25;
	public static final int QUADRUPLE_SPEED = 12;
	
	public VisApp(){
		createFrame();
		createMenuBar();
	}
	
	protected void createFrame(){
		appFrame = new JFrame();
		appFrame.setTitle("Bird's Eye - v1.0.0");
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setBounds(20, 20, 1200, 700);
		appFrame.setMinimumSize(new Dimension(900, 700));
		appFrame.setLocationRelativeTo(null);
		appFrame.setVisible(true);
	}
	
	protected void createMenuBar(){
		appFrame.setJMenuBar(new JMenuBar());
		createFileMenu();
		createViewMenu();
		disableViewMenu();
		createPlaybackMenu();
		disablePlaybackMenu();
	}
	
	protected void createFileMenu(){
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		appFrame.getJMenuBar().add(menu);
		
		JMenuItem menuItem = new JMenuItem("Open Files...");
		menuItem.setPreferredSize(new Dimension(200, menuItem.getPreferredSize().height));
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("open files");
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Exit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("exit");
		menu.add(menuItem);
	}
	
	protected void disableFileMenu(){
		appFrame.getJMenuBar().getMenu(0).setEnabled(false);
	}
	
	protected void enableFileMenu(){
		appFrame.getJMenuBar().getMenu(0).setEnabled(true);
	}
	
	protected void createViewMenu(){
		JMenu menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		appFrame.getJMenuBar().add(menu);
		
		JMenuItem menuItem = new JCheckBoxMenuItem("Show Map View Info Panel");
		menuItem.setPreferredSize(new Dimension(200, menuItem.getPreferredSize().height));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("map info panel");
		menuItem.setSelected(true);
		menu.add(menuItem);
		
		menuItem = new JCheckBoxMenuItem("Highlight Highly Influential Tweets");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("highlight tweets");
		menuItem.setSelected(true);
		menu.add(menuItem);
		
		JMenu submenu = new JMenu("Map Display Type");
		ButtonGroup group = new ButtonGroup();
		
		menuItem = new JRadioButtonMenuItem("Positron Lite");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("positron lite");
		menuItem.setSelected(true);
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("Positron");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("positron");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("Dark Matter Lite");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("dark matter lite");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("Dark Matter");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("dark matter");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menu.add(submenu);
		
		submenu = new JMenu("Map Marker Duration");
		group = new ButtonGroup();
		
		menuItem = new JRadioButtonMenuItem(String.format("Short (%d minutes)", MapView.SHORT_DURATION));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker duration short");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem(String.format("Medium (%d minutes)", MapView.MEDIUM_DURATION));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker duration medium");
		menuItem.setSelected(true);
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem(String.format("Long (%d minutes)", MapView.LONG_DURATION));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker duration long");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menu.add(submenu);
		
		submenu = new JMenu("Map Marker Size");
		group = new ButtonGroup();
		
		menuItem = new JRadioButtonMenuItem("Small");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker radius small");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("Medium");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker radius medium");
		menuItem.setSelected(true);
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("Large");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set marker radius large");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menu.add(submenu);
		
		menu.addSeparator();
		
		lineGraphScaleMenuItem = new JCheckBoxMenuItem("Scale Line Graph to Selected Interval");
		lineGraphScaleMenuItem.addActionListener(this);
		lineGraphScaleMenuItem.setMnemonic(KeyEvent.VK_I);
		lineGraphScaleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
		lineGraphScaleMenuItem.setActionCommand("scale line graph to selected interval");
		menu.add(lineGraphScaleMenuItem);
	}
	
	protected void enableViewMenu(){
		appFrame.getJMenuBar().getMenu(1).setEnabled(true);
	}
	
	protected void disableViewMenu(){
		appFrame.getJMenuBar().getMenu(1).setEnabled(false);
	}
	
	protected void createPlaybackMenu(){
		JMenu menu = new JMenu("Playback");
		menu.setMnemonic(KeyEvent.VK_P);
		appFrame.getJMenuBar().add(menu);
		
		playPauseMenuItem = new JMenuItem("Play");
		playPauseMenuItem.addActionListener(this);
		playPauseMenuItem.setMnemonic(KeyEvent.VK_P);
		playPauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK));
		playPauseMenuItem.setActionCommand("play/pause animation");
		menu.add(playPauseMenuItem);
		
		JMenuItem menuItem = new JMenuItem("Next Frame");
		menuItem.setPreferredSize(new Dimension(200, menuItem.getPreferredSize().height));
		menuItem.addActionListener(this);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("next frame");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Previous Frame");
		menuItem.addActionListener(this);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("previous frame");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Reset Animation");
		menuItem.addActionListener(this);
		menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.setActionCommand("reset animation");
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JCheckBoxMenuItem("Loop Playback");
		menuItem.addActionListener(this);
		menuItem.setMnemonic(KeyEvent.VK_L);
		menuItem.setActionCommand("toggle animation looping");
		menuItem.setSelected(true);
		menu.add(menuItem);
		
		JMenu submenu = new JMenu("Animation Speed");
		ButtonGroup group = new ButtonGroup();
		menu.add(submenu);
		
		menuItem = new JRadioButtonMenuItem("0.25x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed quarter");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("0.5x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed half");
		group.add(menuItem);
		submenu.add(menuItem); 
		
		menuItem = new JRadioButtonMenuItem("1x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed default");
		menuItem.setSelected(true);
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("1.5x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed one and a half");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("2x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed double");
		group.add(menuItem);
		submenu.add(menuItem);
		
		menuItem = new JRadioButtonMenuItem("4x");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("set speed quadruple");
		group.add(menuItem);
		submenu.add(menuItem);
	}
	
	protected void enablePlaybackMenu(){
		appFrame.getJMenuBar().getMenu(2).setEnabled(true);
	}
	
	protected void disablePlaybackMenu(){
		appFrame.getJMenuBar().getMenu(2).setEnabled(false);
	}
	
	protected void createFileDialog(){
		// Create and show the "Open File" dialog
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setMultiSelectionEnabled(true);
		int result = fileChooser.showOpenDialog(appFrame);
		
		// If the user selects one or more files, create a loading screen and parse the files in the background
		if(result == JFileChooser.APPROVE_OPTION){
			
			// Dispose of any open views and disable the menu bar while the files load
			if(mapView != null){
				removeViews();
			}
			disableFileMenu();
			disableViewMenu();
			disablePlaybackMenu();
			appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			// Create a dialog that displays the progress of the file parsing progress
			File[] selectedFiles = fileChooser.getSelectedFiles();
			loadingFilesDialog = new LoadingFilesDialog(0, selectedFiles.length, selectedFiles[0].getAbsolutePath(), this);
			
			// Create a SwingWorker that parses files in the background
			fileParser = new FileParser(selectedFiles);
			fileParser.execute();
		}
	}
	
	// SwingWorker used to parse files in the background
	class FileParser extends SwingWorker<Void, String>{
		File selectedFiles[];
		
		public FileParser(File files[]){
			selectedFiles = files;
		}
		
		protected void parseFile(String filePath) throws Exception{
			JsonTweetReader.readJSON(filePath, Tweets);
			System.out.println(String.format("Parsed file '%s'", filePath));
		}
		
		protected Void doInBackground() throws Exception{
			Tweets = new TreeMap<Calendar, List<TweetObject>>();
			tweets = new ArrayList<List<TweetObject>>();
			dates = new ArrayList<Calendar>();
			
			File file = null;
			try{
				
				// Parse all files selected in the "Open File" dialog
				for(int i = 0; i < selectedFiles.length; i++){
					
					// Cancel the task if the user closes the loading dialog
					if(isCancelled()){
						return null;
					}
					
					file = selectedFiles[i];
					parseFile(file.getAbsolutePath());
					
					// After parsing a file, provide the next file's name to the loading dialog 
					if(i+1 != selectedFiles.length){
						publish(selectedFiles[i+1].getAbsolutePath());
					}
					else{
						publish("");
					}
				}
				System.out.println("Done Parsing Files!");
				
				// Separate the dates from the Tweets for easier traversal
				for(Calendar key : Tweets.keySet()){
					dates.add(key);
					tweets.add(Tweets.get(key));
				}
				Tweets = null;
			}
			
			// Cancel the operation if the file doesn't exist
			catch(FileNotFoundException exception){
				JOptionPane.showMessageDialog(appFrame, 
						String.format("\"%s\"%nThe selected file does not exist", file.getAbsolutePath()),
						"File Not Found", 
						JOptionPane.ERROR_MESSAGE);
				cancel(true);
			}
			
			// Cancel the operation if the file contains errors
			catch(Exception exception){
				JOptionPane.showMessageDialog(appFrame, 
						String.format("\"%s\"%nUnsupported file format", file.getAbsolutePath()),
						"Unsupported File Format",
						JOptionPane.ERROR_MESSAGE);
				cancel(true);
			}
			return null;
		}
		
		// After every file is parsed, update the loading screen
		protected void process(List<String> chunks){
			loadingFilesDialog.setProgress(loadingFilesDialog.getProgress()+1, chunks.get(0));
		}
		
		// If all files were parsed, create everything needed for the visualization and close any open dialogs
		protected void done(){
			if(!isCancelled()){
				createViews();
				createTimer();
				enableViewMenu();
				enablePlaybackMenu();
			}
			enableFileMenu();
			loadingFilesDialog.dispose();
			appFrame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	// Externally cancel the file parsing process
	public void stopParsingFiles(){
		fileParser.cancel(true);
	}
	
	// Helper class used by KeywordSearcher that holds the worker's results
	class SearchResults{
		public List<Integer> frequencies;
		public List<String> topWords;
		public List<Integer> topFrequencies;
		
		SearchResults(List<Integer> frequencies, List<String> topWords, List<Integer> topFrequencies){
			this.frequencies = frequencies;
			this.topWords = topWords;
			this.topFrequencies = topFrequencies;
		}
	}
	
	// SwingWorker used to search for keywords in the background
	class KeywordSearcher extends SwingWorker<SearchResults, Void>{
		protected boolean keywordIsCached;
		
		// Create a set of stop words to increase search efficiency and reduce the amount of unimportant words processed
		public void parseStopWords(){
			stopWordsSet = new HashSet<String>();
			BufferedReader reader = null;
			try{
			    reader = new BufferedReader(new FileReader(new File("resources/stopwords.txt")));
			    String text = null;

			    while((text = reader.readLine()) != null){
			        stopWordsSet.add(text);
			    }
			}
			catch(FileNotFoundException exception){
			    JOptionPane.showMessageDialog(appFrame,
			    		"Missing 'stopwords.txt' file",
			    		"File Not Found",
			    		JOptionPane.ERROR_MESSAGE);
			    cancel(true);
			}
			catch(IOException exception){
			    exception.printStackTrace();
			    cancel(true);
			}
			finally{
			    try{
			        if(reader != null){
			            reader.close();
			        }
			    }
			    catch(IOException exception){}
			}
		}
		
		protected SearchResults doInBackground() throws Exception{
			if(stopWordsSet == null){
				parseStopWords();
			}
			
			// Determine if the keyword has been cached by the bar graph
			keywordIsCached = false;
			if(barGraphView.containsData(keyword)){
				barGraphView.useData(keyword);
				keywordIsCached = true;
			}

			// Create any needed data structures
			List<Integer> frequencies = new ArrayList<Integer>();
			HashMap<String, AtomicInteger> tree = new HashMap<String, AtomicInteger>();
			List<String> topWords = new ArrayList<String>();
			List<Integer> topFrequencies = new ArrayList<Integer>();
			
			try{
				
				// Compile any needed regex patterns beforehand to increase efficiency
				String emptyString = "";
				Pattern keywordPattern = Pattern.compile(keyword.toLowerCase());
				Matcher keywordMatcher = keywordPattern.matcher(emptyString);
				Pattern counterPattern = Pattern.compile("^#?[a-z]+$");
				Matcher counterMatcher = counterPattern.matcher(emptyString);
				
				// Find Tweets that contain the keyword and keep a count of any related words
				AtomicInteger keywordFreq = new AtomicInteger();
				for(List<TweetObject> minute : tweets){
					keywordFreq.set(0);
					for(TweetObject tweet : minute){
						keywordMatcher.reset(tweet.text.toLowerCase());
						if(keyword.equals(emptyString) || keywordMatcher.find()){
							keywordFreq.incrementAndGet();
							
							// Keep a count of any words that are also in Tweets containing the keyword
							if(!keywordIsCached){
								Scanner tokenizer = new Scanner(tweet.text.toLowerCase());
								while(tokenizer.hasNext()){
									String word = tokenizer.next();
									
									// Strip away any nonsense words
									counterMatcher.reset(word);
									
									// Add the word to an associative array that holds words and their frequencies
									if(!stopWordsSet.contains(word) && counterMatcher.matches()){
										if(tree.containsKey(word)){
											tree.get(word).incrementAndGet();
										}
										else{
											tree.put(word, new AtomicInteger(1));
										}
									}
								}
								tokenizer.close();
							}
						}
					}
					frequencies.add(keywordFreq.get());
					
					publish();
				}
				
				// Convert the atomic integers to integers and sort the arrays based on the frequency values
				List<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>();
				for(String s : tree.keySet()){
					Map.Entry<String, Integer> entry = new Map.Entry<String, Integer>(){
						public String getKey(){
							return s;
						}
						public Integer getValue(){
							return tree.get(s).get();
						}
						public Integer setValue(Integer value){
							return null;
						}
					};
					wordList.add(entry);
				}
				Collections.sort(wordList, new WordComparator());
				
				int k = 0;
				for(Map.Entry<String, Integer> word : wordList){
					topWords.add(word.getKey());
					topFrequencies.add(word.getValue());
					
					k++;
				    if(k == 10){
				    	break;
				    }
				}
			}
			catch(PatternSyntaxException exception){
				JOptionPane.showMessageDialog(appFrame, String.format("Invalid search term: %s", exception.getDescription()),
						"Invalid Search Term", JOptionPane.ERROR_MESSAGE);
			}
			
			return new SearchResults(frequencies, topWords, topFrequencies);
		}
		
		// Constantly update the dialog that displays the search progress
		protected void process(List<Void> chunks){
			keywordSearchDialog.setProgress(keywordSearchDialog.getProgress()+1);
		}
		
		@Override
		protected void done(){
			try{
				SearchResults results = get();
				
				lineGraphView.setData(results.frequencies, dates, keyword);
				if(keywordIsCached){
					barGraphView.useData(keyword);
				}
				else{
					barGraphView.setData(keyword, results.topWords, results.topFrequencies);
				}
				
				setAnimationIndex(animationIndex);
				
				mapView.setVisible(true);
				lineGraphView.setVisible(true);
				optionsPane.setVisible(true);
				barGraphView.setVisible(true);
			}
			catch(InterruptedException exception){
				exception.printStackTrace();
			}
			catch(ExecutionException exception){
				exception.printStackTrace();
			}

			keywordSearchDialog.dispose();
			appFrame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	protected void searchKeyword(String keyword){
		appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.keyword = keyword;
		optionsPane.setSearchKeyword(keyword);
		
		keywordSearchDialog = new KeywordSearchDialog(0, dates.size(), keyword, this);
		
		keywordSearcher = new KeywordSearcher();
		keywordSearcher.execute();
	}
	
	protected String getKeyword(){
		return keyword;
	}
	
	protected void createViews(){
		createMapView();
		createLineGraphView();
		createOptionsPane();
		createTweetInfoView();
		createBarGraphView();
		searchKeyword("");
		
		// Lay out the views
		JPanel contentPane = (JPanel)appFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapView, lineGraphView);
		centerPane.setBorder(BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY));
		centerPane.setResizeWeight(1);
		centerPane.setContinuousLayout(true);
		contentPane.add(centerPane, BorderLayout.CENTER);
		
		JPanel eastPane = new JPanel();
		eastPane.setLayout(new BorderLayout());
		eastPane.add(optionsPane, BorderLayout.CENTER);
		
		JPanel southEastPane = new JPanel();
		southEastPane.add(barGraphView);
		southEastPane.add(tweetInfoView);
		southEastPane.setPreferredSize(new Dimension(300, 380));
		eastPane.add(southEastPane, BorderLayout.SOUTH);
		contentPane.add(eastPane, BorderLayout.EAST);
	}
	
	protected void createMapView(){
		mapView = new MapView(this);
		mapView.setBackground(Color.WHITE);
		mapView.setPreferredSize(new Dimension(700, 400));
		mapView.setMinimumSize(new Dimension(700, 200));
		mapView.setVisible(false);
	}
	
	protected void createLineGraphView(){
		lineGraphView = new LineGraphView(this);
		lineGraphView.setBackground(Color.WHITE);
		lineGraphView.setMinimumSize(new Dimension(700, 200));
		lineGraphView.setVisible(false);
	}
	
	public void setLineGraphFullScale(boolean b){
		lineGraphView.setFullScale(b);
		lineGraphScaleMenuItem.setSelected(!b);
	}
	
	protected void createOptionsPane(){
		optionsPane = new OptionsPane(this, dates.get(0), dates.get(dates.size()-1));
		optionsPane.setBackground(Color.WHITE);
		optionsPane.setPreferredSize(new Dimension(300, 265));
		optionsPane.setBorder(BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY));
		optionsPane.setVisible(false);
	}
	
	protected void createTweetInfoView(){
		tweetInfoView = new TweetInfoView();
		tweetInfoView.setBackground(Color.WHITE);
		tweetInfoView.setPreferredSize(new Dimension(300, 180));
		tweetInfoView.setBorder(BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY));
		tweetInfoView.validate();
	}
	
	public void updateTweetInfoView(TweetObject tweet, int index){
		tweetInfoView.setData(tweet, dates.get(index));
	}
	
	protected void createBarGraphView(){
		barGraphView = new BarGraphView(this);
		barGraphView.setBackground(Color.WHITE);
		barGraphView.setPreferredSize(new Dimension(300, 200));
		barGraphView.setBorder(BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY));
	}
	
	protected void removeViews(){
		appFrame.getContentPane().removeAll();
		appFrame.getContentPane().repaint();
		
		barGraphView = null;
		lineGraphView = null;
		mapView = null;
		optionsPane = null;
		tweetInfoView = null;
	}
	
	protected void createTimer(){
		timer = new Timer(50, this);
		timer.setActionCommand("animate");
		animationIndex = 0;
		animationStart = 0;
		animationEnd = tweets.size()-1;
		animationIsPlaying = false;
		animationIsLooping = true;
	}
	
	// Check the animation state
	public boolean animationIsPlaying(){
		return animationIsPlaying;
	}

	public void setAnimationPlaying(boolean b){
		// Stop or start the timer that triggers the next frame to display
		if(b == true){
			timer.start();
		}
		else{
			timer.stop();
		}
		
		// Update the animation state
		animationIsPlaying = b;
		
		// Update the state of the "Play/Pause" button in the options pane
		optionsPane.setAnimationPlaying(b);
		
		// Update the state of the play/pause menu item in the Playback menu
		if(animationIsPlaying){
			playPauseMenuItem.setText("Pause");
		}
		else{
			playPauseMenuItem.setText("Play");
		}
	}
	
	// Returns the index of the animation's currently displayed frame
	public int getAnimationIndex(){
		return animationIndex;
	}
	
	// Set the animation to display a particular frame
	public boolean setAnimationIndex(int animationIndex){
		if(animationIndex < animationStart || animationIndex > animationEnd){		
			JOptionPane.showMessageDialog(appFrame,
					String.format("Time is outside selected time interval%nSelect a new time and try again"),
					"Invalid Time Selected",
					JOptionPane.WARNING_MESSAGE);
		}
		
		this.animationIndex = animationIndex;
		
		lineGraphView.setAnimationIndex(animationIndex);
		
		mapView.resetAnimation();
		int i = animationIndex - mapView.getMarkerDuration();
		if(i < 0){
			i = 0;
		}
		
		// Find all Tweets that contain the keyword and add that data to the map view
		Pattern p = Pattern.compile(keyword.toLowerCase());
		for(; i <= animationIndex; i++){
			List<TweetObject> minute = tweets.get(i);
			List<TweetObject> coordtweets = new ArrayList<TweetObject>();
			for(TweetObject tweet : minute){
				Matcher m = p.matcher(tweet.text);
				if((tweet.latitude != 0 || tweet.longitude != 0) && m.find()){
					coordtweets.add(tweet);
				}
			}
			mapView.setDataFirst(coordtweets);
		}
		return true;
	}
	
	// Similar to setAnimationIndex(), but optimized for incrementally stepping forward through frames
	public void nextFrame(){
		animationIndex++;
		
		// If we've reached the end, reset the animation and start over if looping is set
		if(animationIndex > animationEnd){
			animationIndex = animationStart;
			setAnimationIndex(animationIndex);
			if(!animationIsLooping){
				setAnimationPlaying(false);
			}
			return;
		}
		
		// Update the line graph
		lineGraphView.setAnimationIndex(animationIndex);
		
		// Create a list of Tweets for the map to display
		List<TweetObject> coordtweets = new ArrayList<TweetObject>();
		Pattern p = Pattern.compile(keyword.toLowerCase());
		List<TweetObject> minute = tweets.get(animationIndex);
		for(TweetObject tweet : minute){
			Matcher m = p.matcher(tweet.text.toLowerCase());
			if((tweet.latitude != 0 || tweet.longitude != 0) && m.find()){
				coordtweets.add(tweet);
			}
		}
		
		mapView.setDataFirst(coordtweets);
	}
	
	// Similar to setAnimationIndex(), but optimized for incrementally stepping backward through frames
	public void previousFrame(){
		animationIndex--;
		
		// If we've reached the beginning, go to the end
		if(animationIndex < animationStart){
			animationIndex = animationEnd;
			setAnimationIndex(animationIndex);
			return;
		}
		
		// Update the line graph
		lineGraphView.setAnimationIndex(animationIndex);
		
		// Create a list of Tweets for the map to display
		List<TweetObject> coordtweets = new ArrayList<TweetObject>();
		Pattern p = Pattern.compile(keyword.toLowerCase());
		int index = animationIndex+1 - mapView.getMarkerDuration();
		if(index >= 0){
			List<TweetObject> minute = tweets.get(index);
			for(TweetObject tweet : minute){
				Matcher m = p.matcher(tweet.text.toLowerCase());
				if((tweet.latitude != 0 || tweet.longitude != 0) && m.find()){
					coordtweets.add(tweet);
				}
			}

			mapView.setDataLast(coordtweets);
		}
		else{
			mapView.setDataLast(null);
		}
	}
	
	public void resetAnimation(){
		setAnimationPlaying(false);
		setAnimationIndex(animationStart);
	}
	
	public boolean animationIsLooping(){
		return animationIsLooping;
	}
	
	public void setAnimationLooping(boolean b){
		animationIsLooping = b;
		
		// Update the state of the "Loop Animation" option in the Playback menu
		JMenuItem menuItem = appFrame.getJMenuBar().getMenu(2).getItem(5);
		menuItem.setSelected(animationIsLooping);
	}
	
	public int getAnimationSpeed(){
		return timer.getDelay();
	}
	
	public void setAnimationSpeed(int delay){
		timer.setDelay(delay);
		optionsPane.setAnimationSpeed(delay);
		
		int selected = -1;
		if(delay == QUARTER_SPEED){
			selected = 0;
		}
		else if(delay == HALF_SPEED){
			selected = 1;
		}
		else if(delay == DEFAULT_SPEED){
			selected = 2;
		}
		else if(delay == ONE_AND_A_HALF_SPEED){
			selected = 3;
		}
		else if(delay == DOUBLE_SPEED){
			selected = 4;
		}
		else if(delay == QUADRUPLE_SPEED){
			selected = 5;
		}
		JMenu menu = (JMenu)appFrame.getJMenuBar().getMenu(2).getMenuComponent(6);
		for(int i = 0; i < menu.getItemCount(); i++){
			if(i == selected){
				menu.getItem(i).setSelected(true);
			}
			else{
				menu.getItem(i).setSelected(false);
			}
		}
	}
	
	// Convert a Calendar object to a Tweet list index
	public int dateToIndex(Calendar date){
		String d = date.getTime().toString();
		for(int i = 0; i < dates.size(); i++){
			if(dates.get(i).getTime().toString().equals(d)){
				return i;
			}
		}
		return -1;
	}
	
	// Set the time period displayed by the animation
	public void setTimeInterval(int start, int end){
		if(start == -1){
			JOptionPane.showMessageDialog(appFrame, "Invalid start time selected",
					"Invalid Time Constraints", JOptionPane.ERROR_MESSAGE);
		}
		if(end == -1){
			JOptionPane.showMessageDialog(appFrame, "Invalid end time selected%nAdjust your ",
					"Invalid Time Constraints", JOptionPane.ERROR_MESSAGE);
		}
		else if(start > end){
			JOptionPane.showMessageDialog(appFrame, "Invalid time constraints: start time must precede end time",
					"Invald Time Constraints", JOptionPane.ERROR_MESSAGE);
		}
		else{
			animationStart = start;
			animationEnd = end;
			if(animationIndex < animationStart || animationIndex > animationEnd){
				animationIndex = animationStart;
			}
			
			lineGraphView.setTimeInterval(animationStart, animationEnd);
			setAnimationIndex(animationIndex);
		}
	}
	
	// Handle menu events
	public void actionPerformed(ActionEvent event){
		String action = event.getActionCommand();
		
		if(action.equals("open files")){
			createFileDialog();
		}
		
		else if(action.equals("play/pause animation")){
			setAnimationPlaying(!animationIsPlaying());
		}
		
		else if(action.equals("reset animation")){
			resetAnimation();
		}
		
		else if(action.equals("toggle animation looping")){
			setAnimationLooping(!animationIsLooping());
		}
		
		else if(action.equals("set speed quarter")){
			setAnimationSpeed(QUARTER_SPEED);
		}
		else if(action.equals("set speed half")){
			setAnimationSpeed(HALF_SPEED);
		}
		else if(action.equals("set speed default")){
			setAnimationSpeed(DEFAULT_SPEED);
		}
		else if(action.equals("set speed one and a half")){
			setAnimationSpeed(ONE_AND_A_HALF_SPEED);
		}
		else if(action.equals("set speed double")){
			setAnimationSpeed(DOUBLE_SPEED);
		}
		else if(action.equals("set speed quadruple")){
			setAnimationSpeed(QUADRUPLE_SPEED);
		}
		
		else if(action.equals("animate") || action.equals("next frame")){
			nextFrame();
		}
		else if(action.equals("previous frame")){
			previousFrame();
		}

		else if(action.equals("scrollwrap")){
			mapView.setScrollWrapEnabled(!mapView.isScrollWrapEnabled());
		}
		
		else if(action.equals("map info panel")){
			mapView.setInfoPanelVisible(!mapView.isInfoPanelVisible());
		}
		
		else if(action.equals("highlight tweets")){
			mapView.toggleHighlightedTweets();
		}
		
		else if(action.equals("positron lite")){
			mapView.setMapType(MapView.POSITRON_LITE);
		}
		else if(action.equals("positron")){
			mapView.setMapType(MapView.POSITRON);
		}
		else if(action.equals("dark matter lite")){
			mapView.setMapType(MapView.DARK_MATTER_LITE);
		}
		else if(action.equals("dark matter")){
			mapView.setMapType(MapView.DARK_MATTER);
		}
		
		else if(action.equals("set marker duration short")){
			mapView.setMarkerDuration(MapView.SHORT_DURATION);
		}
		else if(action.equals("set marker duration medium")){
			mapView.setMarkerDuration(MapView.MEDIUM_DURATION);
		}
		else if(action.equals("set marker duration long")){
			mapView.setMarkerDuration(MapView.LONG_DURATION);
		}
		
		else if(action.equals("set marker radius small")){
			mapView.setMarkerSize(MapView.SMALL_RADIUS);
		}
		else if(action.equals("set marker radius medium")){
			mapView.setMarkerSize(MapView.MEDIUM_RADIUS);
		}
		else if(action.equals("set marker radius large")){
			mapView.setMarkerSize(MapView.LARGE_RADIUS);
		}
		
		else if(action.equals("scale line graph to selected interval")){
			setLineGraphFullScale(!lineGraphView.isFullScale);
		}
		
		else if(action.equals("exit")){
			System.exit(0);
		}
	}
	
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("ToolTip.background", new ColorUIResource(250, 250, 250));
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
		finally{
			EventQueue.invokeLater(new Runnable(){
				public void run(){
					new VisApp();
				}
			});
		}
	}
}
