/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines a dialog that displays the current progress of the 
	application as it searches for a specified keyword.
 *****************************************************************************/

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class KeywordSearchDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	protected int progress;
	protected JLabel progressLabel, progressFile;
	protected JProgressBar progressBar;

	public KeywordSearchDialog(int minValue, int maxValue, String keyword, VisApp visApp){
		super(visApp.appFrame);
		
		progress = minValue;
		
		this.setLayout(new BorderLayout());
		this.setTitle("Searching Keyword");
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(400, 60));
		
		if(keyword.equals("")){
			progressLabel = new JLabel("Searching for keyword: ' '");
		}
		else{
			progressLabel = new JLabel(String.format("Searching for keyword: '%s'", keyword));
		}
		progressLabel.setHorizontalAlignment(JLabel.CENTER);
		progressLabel.setPreferredSize(new Dimension(400, 45));
		progressLabel.setVerticalAlignment(JLabel.CENTER);
		topPanel.add(progressLabel);
		
		progressBar = new JProgressBar(minValue, maxValue);
		progressBar.setStringPainted(true);
		progressBar.setForeground(InterfaceConstants.TWITTER_BLUE);
		progressBar.setPreferredSize(new Dimension(400, 25));
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(progressBar, BorderLayout.CENTER);
		
		this.pack();
		this.setLocationRelativeTo(visApp.appFrame.getContentPane());
		this.setVisible(true);
	}
	
	public int getProgress(){
		return progress;
	}

	public void setProgress(int progress){
		this.progress = progress;
		progressBar.setValue(this.progress);
	}
}
