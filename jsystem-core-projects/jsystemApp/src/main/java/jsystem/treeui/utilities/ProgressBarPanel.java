/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import jsystem.treeui.images.ImageCenter;
import jsystem.utils.ProgressNotifier;

public class ProgressBarPanel extends JPanel implements ProgressNotifier{
	
	private static final long serialVersionUID = 1L;
	private static JDialog progressDialog;
	private static ProgressBarPanel panel;
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private JButton closeButton;
	private boolean closeOnDone = true;
	
	private ProgressBarPanel(boolean closeOnDone) {
		super(new BorderLayout());
		this.closeOnDone = closeOnDone;
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		taskOutput = new JTextArea(5, 30);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);
		taskOutput.setWrapStyleWord(true);
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				disposeProgressFrame();
			}
		});
		closeButton.setEnabled(false);
		add(progressBar,BorderLayout.NORTH);
		JScrollPane pane = new JScrollPane(taskOutput);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(pane, BorderLayout.CENTER);
		add(closeButton,BorderLayout.SOUTH);
	}

	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 */
	public static ProgressNotifier createAndShowProgressPanel(String title,Point location,boolean closeOnDone,Window parent) {
		disposeProgressFrame();
		//Create and set up the window.
		progressDialog = new JDialog(parent,title,ModalityType.APPLICATION_MODAL);
		progressDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		progressDialog.setIconImage(ImageCenter.getInstance()
				.getAwtImage(ImageCenter.ICON_JSYSTEM));

		//Create and set up the content pane.
		panel = new ProgressBarPanel(closeOnDone);
		panel.setOpaque(true); //content panes must be opaque
		progressDialog.setContentPane(panel);
		//Display the window.
		progressDialog.pack();
		progressDialog.setLocation(location);
		progressDialog.setAlwaysOnTop(true);

		SwingWorker<String, Object> worker = new SwingWorker<String, Object>(){
	        public String doInBackground() {
	    		progressDialog.setVisible(true);
	        	return "";
	        } 
		};
		worker.execute();
		return panel;
	}
	
	private static void disposeProgressFrame() {
		if (progressDialog != null) {
			progressDialog.setVisible(false);
			progressDialog.dispose();
		}
	}

	@Override
	public void notifyProgress(String message, int progress) {
		progressBar.setValue(progress);
		taskOutput.append(message+"\n\r");
	}
	
	public static void main(String[] args){
		createAndShowProgressPanel("xxx", new Point(100,100),true,null);
	}

	@Override
	public void done() {
		if (closeOnDone){
			disposeProgressFrame();
		}else {
			closeButton.setEnabled(true);
		}
	}
}