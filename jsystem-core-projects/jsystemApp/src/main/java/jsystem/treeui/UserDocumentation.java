/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.MultipleScenarioOps;
import jsystem.treeui.teststable.TestsTableController;

/**
 * this class builds the user documentation text area and listens to the APPLY
 * button on the test information TAB
 * 
 * @author Nizan Freedman
 * 
 */
public class UserDocumentation extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String doc;

	private static Logger log = Logger.getLogger(UserDocumentation.class.getName());

	private JTextArea testUserDocumentation;

	private JButton applyButton;

	private JButton clearButton;

	private JSplitPane sp;

	private JTest currentTest;

	private TestsTableController testTableController;

	public UserDocumentation(TestsTableController testTableController) {
		this.testTableController = testTableController;
		buildTextArea();
	}

	/**
	 * build a JTextArea with a scroll panel and a "update button" for the user
	 * test documentation
	 * 
	 */
	private void buildTextArea() {
		setLayout(new BorderLayout());

		applyButton = new JButton("Apply");
		applyButton.addMouseListener(this);
		applyButton.setSize(new Dimension(80, 20));

		clearButton = new JButton("Clear");
		clearButton.addMouseListener(this);
		clearButton.setSize(new Dimension(80, 20));

		int width = 250;

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(applyButton, BorderLayout.WEST);
		p.setBackground(new Color(0xf6, 0xf6, 0xf6));

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(clearButton, BorderLayout.WEST);
		p2.setBackground(new Color(0xf6, 0xf6, 0xf6));

		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p, p2);
		sp.setDividerLocation(width / 3);
		sp.setDividerSize(0);

		testUserDocumentation = new JTextArea();
		JScrollPane userSP = new JScrollPane(testUserDocumentation);
		userSP.setSize(200, 100);

		add(userSP, BorderLayout.CENTER);
		add(sp, BorderLayout.SOUTH);
		resetText("", false);
	}

	/**
	 * the implementation is for the APPLY and CLEAR buttons which updates the
	 * appropriate xml file
	 */
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
		JTest test = currentTest;

		if (source.equals(applyButton)) {
			doc = testUserDocumentation.getText();
			try {
				MultipleScenarioOps.updateDocumentation(test, doc);
				testTableController.refreshTree();
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to update scenario after userDoc Apply", e1);
			}
		} else if (source.equals(clearButton)) {
			test.setDocumentation("");
			testUserDocumentation.setText("");
		}

	}

	/**
	 * reset the text area
	 * 
	 * @param txt -
	 *            the txt to show
	 * @param enable -
	 *            to enable editing + button
	 */
	public void resetText(String txt, boolean enable) {
		setEnabled(enable);
		testUserDocumentation.setEnabled(enable);
		applyButton.setEnabled(enable);
		clearButton.setEnabled(enable);
		testUserDocumentation.setText(txt);
	}

	public void setTest(JTest test) {
		this.currentTest = test;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
