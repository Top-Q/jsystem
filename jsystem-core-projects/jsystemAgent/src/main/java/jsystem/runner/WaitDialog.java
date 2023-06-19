/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import jsystem.framework.TestRunnerFrame;

/**
 * WaitDialog This is just a frame with a JLabel in it with a possible button used for
 * displaying "Please wait..." dialogs and the like
 */

public class WaitDialog extends JDialog {
	private static final long serialVersionUID = -2017901707408101146L;

	private Container parent;

	private String title;

	private JButton cancelBtn = new JButton("Cancel");
	
	private static Object staticLock = new Object();
	
	private final WaitDialogListner listner;

	public WaitDialog(Frame parent, String title, WaitDialogListner listener) {
		super(parent, title);
		this.parent = parent;
		this.title = title;
		this.listner =  listener;
		/*
		 * Set the dialog to be modal
		 */
		setModalityType(ModalityType.DOCUMENT_MODAL);
		initComponents();
	}
	
	/**
	 * Default constructor
	 * 
	 * @param title
	 *            the message to be displayed in the window's titlebar
	 * 
	 */
	public WaitDialog(Frame parent, String title) {
		this(parent,title,null);
	}

	public WaitDialog(Dialog parent, String title) {
		super(parent, title);
		this.parent = parent;
		this.title = title;
		/*
		 * Set the dialog to be modal
		 */
		setModalityType(ModalityType.DOCUMENT_MODAL);
		initComponents();
		this.listner = null;
	}

	public WaitDialog(String title) {
		setTitle(title);
		this.title = title;
		/*
		 * Set the dialog to be modal
		 */
		setModalityType(ModalityType.DOCUMENT_MODAL);
		initComponents();
		this.listner = null;
	}
	
	public WaitDialog() {
		/*
		 * Set the dialog to be modal
		 */
		setModalityType(ModalityType.DOCUMENT_MODAL);
		initComponents();
		this.listner = null;
	}

	private void initComponents() {
		JPanel panel = (JPanel) getContentPane();
		panel.setLayout(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JProgressBar bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(150 + (title != null ? title.length() : 0) * 4, 20));
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setString(title);

		panel.add(bar, BorderLayout.NORTH);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		buttons.add(Box.createHorizontalGlue());
		buttons.add(cancelBtn);
		panel.add(buttons, BorderLayout.SOUTH);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listner != null){
					listner.cancel();
				}
				dispose();
			}
		});

		pack();
		setLocationRelativeTo(parent);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private static WaitDialog dialog = null;

	public synchronized static void launchWaitDialog(final String title, WaitDialogListner listner) {
		/*
		 * Execute the open of the dialog in a thread as the dialog is modal
		 */
		dialog = new WaitDialog(TestRunnerFrame.guiMainFrame, title, listner);
		(new Thread() {
			public void run() {
				dialog.setVisible(true);
			}
		}).start();
		while (!dialog.isVisible()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public static void endWaitDialog() {
		synchronized(staticLock){
			if (dialog == null) {
				return;
			}
			dialog.dispose();
			dialog = null;
		}
	}

	public static void main(String[] args) {
		launchWaitDialog("Just wait",null);
		try {
			Thread.sleep(4000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endWaitDialog();
	}

	public interface WaitDialogListner {
		void cancel();
	}
	
	

}
