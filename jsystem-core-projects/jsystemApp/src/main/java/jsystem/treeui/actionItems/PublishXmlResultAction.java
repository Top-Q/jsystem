/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.jfree.util.Log;

import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.agent.publisher.PublisherManager;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;

public class PublishXmlResultAction extends IgnisAction {

	private static final long serialVersionUID = 1L;

	private static PublishXmlResultAction action;

	private PublishXmlResultAction() {
		super();
		putValue(Action.NAME, "Publish Xml Result");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getPublishButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_PUBLISH));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_PUBLISH));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
		putValue(Action.ACTION_COMMAND_KEY, "publish-xml-result");
	}

	public static PublishXmlResultAction getInstance() {
		if (action == null) {
			action = new PublishXmlResultAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// PublisherRunInfoFrame.setParent(TestRunner.treeView);
		(new Thread() {
			public void run() {
				WaitDialog.launchWaitDialog("Publishing Reports", null, "Publishing Reports", false);
				try {
					try {
						PublisherManager.getInstance().getPublisher().publish(null, true);
					} catch (Exception e) {
						Log.error("Failed to publish reports", e);
					}
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		}).start();
	}

}
