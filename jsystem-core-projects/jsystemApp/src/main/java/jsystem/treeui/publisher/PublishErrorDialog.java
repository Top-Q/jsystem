/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorDialog;

/**
 * 
 * error dialog shown when publish fail notify PublisherRunInfoFrame when closed
 * 
 */
public class PublishErrorDialog extends ErrorDialog implements ActionListener {

	RunInfoFrameListener listener = null;

	public PublishErrorDialog(String title, String detaledMessage, ErrorLevel errorLevel) {
		super(title, detaledMessage, errorLevel, false);
	}

	@Override
	public void init() {

		super.init();
		okButton.addActionListener(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addObjectToNotify(RunInfoFrameListener l) {
		this.listener = l;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("OK")) {
			this.setAlwaysOnTop(false);
			if (listener != null)
				listener.visible(true);
		}

		super.actionPerformed(e);
	}
}
