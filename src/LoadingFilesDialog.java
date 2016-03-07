/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file defines a dialog that displays the current progress of the 
	application as it parses any input files.
 *****************************************************************************/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class LoadingFilesDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	protected int numTasks, progress;
	protected JLabel progressLabel, progressFile;
	protected JProgressBar progressBar;
	
	public LoadingFilesDialog(int minValue, int maxValue, String firstFileName, VisApp visApp){
		super(visApp.appFrame);
		
		numTasks = maxValue;
		progress = minValue;
		
		this.setLayout(new BorderLayout());
		this.setTitle("Loading Files");
		this.setResizable(false);
		
		// When the window is closed, confirm that the user wants to stop parsing files
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				int result = JOptionPane.showConfirmDialog(event.getWindow(), "Do you want to stop loading the selected files?");
				if(result == JOptionPane.OK_OPTION){
					visApp.stopParsingFiles();
				}
			}
		});
		
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(400, 80));
		
		progressLabel = new JLabel(String.format("Loading selected files (%d of %d complete)", minValue, maxValue));
		progressLabel.setHorizontalAlignment(JLabel.CENTER);
		progressLabel.setPreferredSize(new Dimension(400, 35));
		progressLabel.setVerticalAlignment(JLabel.BOTTOM);
		topPanel.add(progressLabel);
		
		progressFile = new JLabel(String.format("Loading: '%s'", firstFileName));
		progressFile.setHorizontalAlignment(JLabel.CENTER);
		progressFile.setPreferredSize(new Dimension(400, 40));
		progressFile.setVerticalAlignment(JLabel.TOP);
		topPanel.add(progressFile);
		
		progressBar = new JProgressBar(minValue, maxValue);
		progressBar.setPreferredSize(new Dimension(400, 25));
		progressBar.setStringPainted(true);
		progressBar.setForeground(InterfaceConstants.TWITTER_BLUE);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(progressBar, BorderLayout.CENTER);
		
		this.pack();
		this.setLocationRelativeTo(visApp.appFrame.getContentPane());
		this.setVisible(true);
	}
	
	public int getProgress(){
		return progress;
	}

	public void setProgress(int progress, String fileName){
		this.progress = progress;
		progressBar.setValue(this.progress);
		
		progressLabel.setText(String.format("Loading selected files (%d of %d complete)", progress, numTasks));
		if(this.progress == numTasks){
			progressFile.setText("Finished loading files!");
		}
		else{
			progressFile.setText(String.format("Loading: '%s'", fileName));
		}
	}
}
