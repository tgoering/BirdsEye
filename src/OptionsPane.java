/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements the options pane, which includes options for
	searching keywords, adjusting the animation speed, etc.
 *****************************************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class OptionsPane extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	JTextField searchField;
	JButton playPauseButton;
	JComboBox<String> animationSpeedSelect;
	
	JComboBox<Integer> startYearSelect;
	JComboBox<Integer> startMonthSelect;
	JComboBox<Integer> startDaySelect;
	JComboBox<String> startHourSelect;
	JComboBox<String> startMinuteSelect;
	
	JComboBox<Integer> endYearSelect;
	JComboBox<Integer> endMonthSelect;
	JComboBox<Integer> endDaySelect;
	JComboBox<String> endHourSelect;
	JComboBox<String> endMinuteSelect;
	
	JComboBox<Integer> setYearSelect;
	JComboBox<Integer> setMonthSelect;
	JComboBox<Integer> setDaySelect;
	JComboBox<String> setHourSelect;
	JComboBox<String> setMinuteSelect;
	
	protected NextIcon nextIcon = new NextIcon(14, InterfaceConstants.TWITTER_DARK_GRAY);
	protected PrevIcon prevIcon = new PrevIcon(14, InterfaceConstants.TWITTER_DARK_GRAY);
	
	protected VisApp visApp;
	protected final int componentHeight = 22;
	
	public OptionsPane(VisApp visApp, Calendar startDate, Calendar endDate){
		this.visApp = visApp;
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 5, 5);
		
		createSearchPanel(constraints);
		createAnimationControlsPanel(constraints);
		createTimeConstraintsPanel(constraints, startDate, endDate);
	}
	
	protected void createSearchPanel(GridBagConstraints constraints){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY),
				"Search"));
		panel.setPreferredSize(new Dimension(290, 60));
		
		// Search field
		searchField = new JTextField();
		searchField.setToolTipText("Search keyword");
		searchField.addActionListener(this);
		searchField.setActionCommand("search keyword");
		searchField.setPreferredSize(new Dimension(175, componentHeight));
		
		// Position search field
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = .5;
		constraints.weighty = .5;
		constraints.insets.set(5, 5, 5, 2);
		panel.add(searchField, constraints);
		
		// Search button
		JButton button = new JButton("Search");
		button.setToolTipText("Search keyword");
		button.addActionListener(this);
		button.setActionCommand("search keyword");
		button.setPreferredSize(new Dimension(75, componentHeight));
		button.setFocusPainted(false);
		
		// Position search button
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets.set(5, 2, 5, 5);
		panel.add(button, constraints);
		
		add(panel);
	}
	
	protected void createAnimationControlsPanel(GridBagConstraints constraints){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(InterfaceConstants.TWITTER_LIGHT_GRAY),
				"Animation Controls"));
		panel.setPreferredSize(new Dimension(290, 60));
		
		// Play/pause button
		playPauseButton = new JButton("Play");
		playPauseButton.setToolTipText("Play animation");
		playPauseButton.addActionListener(this);
		playPauseButton.setActionCommand("play/pause animation");
		playPauseButton.setPreferredSize(new Dimension(90, componentHeight));
		playPauseButton.setMinimumSize(new Dimension(90, componentHeight));
		playPauseButton.setFocusPainted(false);
		
		// Position play/pause button
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.insets.set(5, 2, 5, 5);
		panel.add(playPauseButton, constraints);
		
		// Animation speed label
		JLabel label = new JLabel("Speed:");
		label.setLabelFor(animationSpeedSelect);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setPreferredSize(new Dimension(70, componentHeight));
		
		// Position animation speed label
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(1, 1, 1, 1);
		panel.add(label, constraints);
		
		// Speed selection combo box
		String speeds[] = {"0.25x", "0.5x", "1x", "1.5x", "2x", "4x"};
		animationSpeedSelect = new JComboBox<String>(speeds);
		((JLabel)animationSpeedSelect.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		animationSpeedSelect.setSelectedIndex(2);
		animationSpeedSelect.setToolTipText("Set animation speed");
		animationSpeedSelect.addActionListener(this);
		animationSpeedSelect.setActionCommand("set speed");
		animationSpeedSelect.setPreferredSize(new Dimension(60, componentHeight));
		animationSpeedSelect.setFocusable(false);
		
		// Position speed selection combo box
		constraints.gridx = 5;
		constraints.gridy = 0;
		panel.add(animationSpeedSelect, constraints);
		
		// Previous frame button
		JButton button = new JButton(new PrevIcon(14, InterfaceConstants.TWITTER_DARK_GRAY));
		button.setPreferredSize(new Dimension(componentHeight + 5, componentHeight));
		button.setMinimumSize(new Dimension(componentHeight + 5, componentHeight));
		button.addActionListener(this);
		button.setActionCommand("prev frame");
		button.setFocusPainted(false);
		
		// Position previous frame button
		constraints.gridx = 2;
		constraints.gridy = 0;
		panel.add(button, constraints);
		
		// Next frame Button
		button = new JButton(new NextIcon(14, InterfaceConstants.TWITTER_DARK_GRAY));
		button.setPreferredSize(new Dimension(componentHeight + 5, componentHeight));
		button.setMinimumSize(new Dimension(componentHeight + 5, componentHeight));
		button.addActionListener(this);
		button.setActionCommand("next frame");
		button.setFocusPainted(false);
	
		// Position next frame button
		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		panel.add(button, constraints);
		
		add(panel);
	}
	
	protected void createTimeConstraintsPanel(GridBagConstraints constraints, Calendar startDate, Calendar endDate){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createTitledBorder("Time Constraints"));
		panel.setPreferredSize(new Dimension(290, 120));
		
		// Parse the given start and end date strings to determine which times to add
		int startYear = startDate.get(Calendar.YEAR);
		int startMonth = startDate.get(Calendar.MONTH);
		int startDay = startDate.get(Calendar.DAY_OF_MONTH);
		int startHour = startDate.get(Calendar.HOUR_OF_DAY);
		int startMinute = startDate.get(Calendar.MINUTE);
		
		int endYear = endDate.get(Calendar.YEAR);
		int endMonth = endDate.get(Calendar.MONTH);
		int endDay = endDate.get(Calendar.DAY_OF_MONTH);
		int endHour = endDate.get(Calendar.HOUR_OF_DAY);
		int endMinute = endDate.get(Calendar.MINUTE);
		
		// Create the combo boxes to select the time
		startYearSelect = new JComboBox<Integer>();
		startYearSelect.setToolTipText("Start year");
		startYearSelect.setFocusable(false);
		
		startMonthSelect = new JComboBox<Integer>();
		startMonthSelect.setToolTipText("Start month");
		startMonthSelect.setFocusable(false);
		
		startDaySelect = new JComboBox<Integer>();
		startDaySelect.setToolTipText("Start day");
		startDaySelect.setFocusable(false);
		
		startHourSelect = new JComboBox<String>();
		startHourSelect.setToolTipText("Start hour");
		startHourSelect.setFocusable(false);
		
		startMinuteSelect = new JComboBox<String>();
		startMinuteSelect.setToolTipText("Start minute");
		startMinuteSelect.setFocusable(false);
		
		endYearSelect = new JComboBox<Integer>();
		endYearSelect.setToolTipText("End year");
		endYearSelect.setFocusable(false);
		
		endMonthSelect = new JComboBox<Integer>();
		endMonthSelect.setToolTipText("End month");
		endMonthSelect.setFocusable(false);
		
		endDaySelect = new JComboBox<Integer>();
		endDaySelect.setToolTipText("End day");
		endDaySelect.setFocusable(false);
		
		endHourSelect = new JComboBox<String>();
		endHourSelect.setToolTipText("End hour");
		endHourSelect.setFocusable(false);
		
		endMinuteSelect = new JComboBox<String>();
		endMinuteSelect.setToolTipText("End minute");
		endMinuteSelect.setFocusable(false);
		
		int j = -1;
		int endIndex = 0;
		for(int i = startYear; i <= endYear; i++){
			startYearSelect.addItem(i);
			endYearSelect.addItem(i);
			j++;
			if(i == endYear){
				endIndex = j;
			}
		}
		endYearSelect.setSelectedIndex(endIndex);
		
		if(startYear < endYear){
			j = -1;
			int k = -1;
			int startIndex = 0;
			endIndex = 0;
			for(int i = 1; i <= 12; i++){
				startMonthSelect.addItem(i);
				endMonthSelect.addItem(i);
			
				k++;
				if(i == startMonth){
					startIndex = k;
				}
				j++;
				if(i == endMonth){
					endIndex = j;
				}
			}
			startMonthSelect.setSelectedIndex(startIndex);
			endMonthSelect.setSelectedIndex(endIndex);
			
			j = -1;
			k = -1;
			startIndex = 0;
			endIndex = 0;
			for(int i = 1; i <= 31; i++){
				startDaySelect.addItem(i);
				endDaySelect.addItem(i);
				
				k++;
				if(i == startDay){
					startIndex = k;
				}
				j++;
				if(i == endDay){
					endIndex = j;
				}
			}
			startDaySelect.setSelectedIndex(startIndex);
			endDaySelect.setSelectedIndex(endIndex);
		}
		else{
			j = -1;
			int k = -1;
			int startIndex = 0;
			endIndex = 0;
			for(int i = startMonth; i <= endMonth; i++){
				startMonthSelect.addItem(i);
				endMonthSelect.addItem(i);
				
				k++;
				if(i == startMonth){
					startIndex = k;
				}
				j++;
				if(i == endMonth){
					endIndex = j;
				}
			}
			startMonthSelect.setSelectedIndex(startIndex);
			endMonthSelect.setSelectedIndex(endIndex);
			
			if(startDay < startMonth){
				j = -1;
				k = -1;
				startIndex = 0;
				endIndex = 0;
				for(int i = 1; i <= 31; i++){
					startDaySelect.addItem(i);
					endDaySelect.addItem(i);
					
					k++;
					if(i == startDay){
						startIndex = k;
					}
					j++;
					if(i == endDay){
						endIndex = j;
					}
				}
				startDaySelect.setSelectedIndex(startIndex);
				endDaySelect.setSelectedIndex(endIndex);
			}
			
			else{
				j = -1;
				k = -1;
				startIndex = 0;
				endIndex = 0;
				for(int i = startDay; i <= endDay; i++){
					startDaySelect.addItem(i);
					endDaySelect.addItem(i);
					
					k++;
					if(i == startDay){
						startIndex = k;
					}
					j++;
					if(i == endDay){
						endIndex = j;
					}
				}
				startDaySelect.setSelectedIndex(startIndex);
				endDaySelect.setSelectedIndex(endIndex);
			}
		}
		j = -1;
		int k = -1;
		int startIndex = 0;
		endIndex = 0;
		for(int i = 0; i < 24; i++){
			startHourSelect.addItem(String.format("%02d", i));
			endHourSelect.addItem(String.format("%02d", i));
			
			k++;
			if(i == startHour){
				startIndex = k;
			}
			j++;
			if(i == endHour){
				endIndex = j;
			}
		}
		startHourSelect.setSelectedIndex(startIndex);
		endHourSelect.setSelectedIndex(endIndex);
		
		j = -1;
		k = -1;
		startIndex = 0;
		endIndex = 0;
		for(int i = 0; i < 60; i++){
			startMinuteSelect.addItem(String.format("%02d", i));
			endMinuteSelect.addItem(String.format("%02d", i));
			
			k++;
			if(i == startMinute){
				startIndex = k;
			}
			j++;
			if(i == endMinute){
				endIndex = j;
			}
		}
		startMinuteSelect.setSelectedIndex(startIndex);
		endMinuteSelect.setSelectedIndex(endIndex);
		
		/*****************************************************************************************/	
		
		// Start time constraint label
		JLabel label = new JLabel("Start:");
		label.setToolTipText("set start time");
		
		// Position start time constraint label
		constraints.insets = new Insets(4, 1, 1, 1);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.weightx = .5;
		constraints.weighty = .4;
		constraints.gridwidth = 1;
		panel.add(label, constraints);
		
		// Position start hour combo box
		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(startHourSelect, constraints);
		
		// Position colon between start hour and minute
		label = new JLabel(":");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 2;
		constraints.gridy = 0;
		panel.add(label, constraints);
		
		// Position start minute combo box
		constraints.gridx = 3;
		constraints.gridy = 0;
		panel.add(startMinuteSelect, constraints);
		
		// Position separating space between time and date
		label = new JLabel("  ");
		constraints.gridx = 4;
		constraints.gridy = 0;
		panel.add(label, constraints);
		
		// Position start year combo box
		constraints.gridx = 5;
		constraints.gridy = 0;
		panel.add(startYearSelect, constraints);
		
		// Position slash between start year and month
		label = new JLabel("/");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 6;
		constraints.gridy = 0;
		panel.add(label, constraints);
		
		// Position start month combo box
		constraints.gridx = 7;
		constraints.gridy = 0;
		panel.add(startMonthSelect,constraints);
		
		// Position slash between start month and day
		label = new JLabel("/");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 8;
		constraints.gridy = 0;
		panel.add(label, constraints);
		
		// Position start day combo box
		constraints.gridx = 9;
		constraints.gridy = 0;
		panel.add(startDaySelect,constraints);
		
		// End time constraint label
		label = new JLabel("End:");
		label.setToolTipText("Set end time");
		
		// Position end time constraint label
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(label,constraints);
		
		// Position end hour combo box
		constraints.gridx = 1;
		constraints.gridy = 1;
		panel.add(endHourSelect,constraints);
		
		// Position colon between end hour and minute
		label = new JLabel(":");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 2;
		constraints.gridy = 1;
		panel.add(label,constraints);
		
		// Position end minute combo box
		constraints.gridx = 3;
		constraints.gridy = 1;
		panel.add(endMinuteSelect,constraints);
		
		// Position space between end time and date
		label = new JLabel("  ");
		constraints.gridx = 4;
		constraints.gridy = 1;
		panel.add(label,constraints);
		
		// Position end year combo box
		constraints.gridx = 5;
		constraints.gridy = 1;
		panel.add(endYearSelect,constraints);
		
		// Position slash between end year and month
		label = new JLabel("/");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 6;
		constraints.gridy = 1;
		panel.add(label,constraints);
		
		// Position end month combo box
		constraints.gridx = 7;
		constraints.gridy = 1;
		panel.add(endMonthSelect,constraints);
		
		// Position slash between end month and day
		label = new JLabel("/");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		constraints.gridx = 8;
		constraints.gridy = 1;
		panel.add(label,constraints);
		
		// Position end day combo box
		constraints.gridx = 9;
		constraints.gridy = 1;
		panel.add(endDaySelect,constraints);
		
		// Apply changes button
		JButton button = new JButton("Apply");
		button.setToolTipText("Apply the selected time constraints");
		button.addActionListener(this);
		button.setActionCommand("apply time constraints");
		button.setFocusPainted(false);
		
		JPanel subPanel = new JPanel(new GridLayout(1, 2));
		
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = .5;
		constraints.weighty = .5;
		constraints.gridwidth = 0;
		panel.add(subPanel, constraints);
		subPanel.add(button);
		
		// Reset changes button
		button = new JButton("Reset");
		button.setToolTipText("Reset all time constraints");
		button.addActionListener(this);
		button.setActionCommand("reset time constraints");
		button.setFocusPainted(false);
		
		// Position apply changes button
		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		subPanel.add(button);
		
		this.add(panel);
	}
	
	public void setSearchKeyword(String searchKeyword){
		searchField.setText(searchKeyword);
		repaint();
	}
	
	public void setAnimationPlaying(boolean b){
		if(b == true){
			playPauseButton.setText("Pause");
			playPauseButton.setToolTipText("Pause animation");
		//	playPauseButton.setIcon(pauseIcon);
		}
		else{
			playPauseButton.setText("Play");
		//	playPauseButton.setIcon(playIcon);
			playPauseButton.setToolTipText("Play animation");
		}
		repaint();
	}
	
	public void setAnimationSpeed(int selection){
		if(selection == VisApp.QUARTER_SPEED){
			animationSpeedSelect.setSelectedIndex(0);
		}
		else if(selection == VisApp.HALF_SPEED){
			animationSpeedSelect.setSelectedIndex(1);
		}
		else if(selection == VisApp.DEFAULT_SPEED){
			animationSpeedSelect.setSelectedIndex(2);
		}
		else if(selection == VisApp.ONE_AND_A_HALF_SPEED){
			animationSpeedSelect.setSelectedIndex(3);
		}
		else if(selection == VisApp.DOUBLE_SPEED){
			animationSpeedSelect.setSelectedIndex(4);
		}
		else if(selection == VisApp.QUADRUPLE_SPEED){
			animationSpeedSelect.setSelectedIndex(5);
		}
		repaint();
	}

	public void actionPerformed(ActionEvent event){
		String action = event.getActionCommand();
		
		if(action.equals("search keyword")){
			visApp.searchKeyword(searchField.getText());
		}
		
		else if(action.equals("play/pause animation")){
			visApp.setAnimationPlaying(!visApp.animationIsPlaying());
		}
		
		else if(action.equals("set speed")){
			int selection = animationSpeedSelect.getSelectedIndex();
			
			if(selection == 0){
				visApp.setAnimationSpeed(VisApp.QUARTER_SPEED);
			}
			else if(selection == 1){
				visApp.setAnimationSpeed(VisApp.HALF_SPEED);
			}
			else if(selection == 2){
				visApp.setAnimationSpeed(VisApp.DEFAULT_SPEED);
			}
			else if(selection == 3){
				visApp.setAnimationSpeed(VisApp.ONE_AND_A_HALF_SPEED);
			}
			else if(selection == 4){
				visApp.setAnimationSpeed(VisApp.DOUBLE_SPEED);
			}
			else if(selection == 5){
				visApp.setAnimationSpeed(VisApp.QUADRUPLE_SPEED);
			}
		}
		
		else if(action.equals("next frame")){
			visApp.nextFrame();
		}
		
		else if(action.equals("prev frame")){
			visApp.previousFrame();
		}
		
		else if(action.equals("set animation index")){
			visApp.setAnimationIndex(50);
		}
		
		else if(action.equals("apply time constraints")){
			int startYear = (Integer)startYearSelect.getSelectedItem();
			int startMonth = (Integer)startMonthSelect.getSelectedItem();
			int startDay = (Integer)startDaySelect.getSelectedItem();
			int startHour = (Integer)startHourSelect.getSelectedIndex();
			int startMinute = (Integer)startMinuteSelect.getSelectedIndex();
			
			int endYear = (Integer)endYearSelect.getSelectedItem();
			int endMonth = (Integer)endMonthSelect.getSelectedItem();
			int endDay = (Integer)endDaySelect.getSelectedItem();
			int endHour = (Integer)endHourSelect.getSelectedIndex();
			int endMinute = (Integer)endMinuteSelect.getSelectedIndex();
			
			Calendar startDate = Calendar.getInstance(Locale.ENGLISH);
			startDate.set(startYear, startMonth, startDay, startHour, startMinute, 0);
			Calendar endDate = Calendar.getInstance(Locale.ENGLISH);
			endDate.set(endYear, endMonth, endDay, endHour, endMinute, 0);
			
			visApp.setTimeInterval(visApp.dateToIndex(startDate), visApp.dateToIndex(endDate));
		}
		
		else if(action.equals("reset time constraints")){
			visApp.setTimeInterval(0, visApp.dates.size()-1);
		}
	}
}
