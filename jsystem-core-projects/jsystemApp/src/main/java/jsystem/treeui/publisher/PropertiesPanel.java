/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

/**
 * a panel for the test properties in the publisher tab
 * 
 * @author nizanf
 * 
 */
public class PropertiesPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Properties properties;

	private JPanel propPanel;

	private JPanel topPanel;

	private JScrollPane scroll;

	private JButton apply, add;

	private JSplitPane split;

	private int width = 415;

	public PropertiesPanel() {
		super();
		setLayout(new BorderLayout());
		setMaximumSize(new Dimension(100, 100));
		setBackground(new Color(0xf6, 0xf6, 0xf6));
		init();
	}

	/**
	 * initialize the panel
	 * 
	 */
	private void init() {
		JLabel keyHeader = new JLabel("     Key") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {

				Image image = ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TABLE_HEADER);
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

				super.paintComponent(g);
			}
		};

		JLabel valHeader = new JLabel("     Value") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {

				Image image = ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TABLE_HEADER);
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

				super.paintComponent(g);
			}
		};

		keyHeader.setForeground(Color.white);
		valHeader.setForeground(Color.white);

		JSplitPane headline = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyHeader, valHeader);
		headline.setBackground(new Color(0xf6, 0xf6, 0xf6));
		headline.setDividerLocation(160);
		headline.setDividerSize(0);

		propPanel = new JPanel();
		propPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));
		propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));

		scroll = new JScrollPane();
		scroll.setViewportView(propPanel);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(width + 20, 23));
		scroll.setViewportBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		scroll.getViewport().setViewPosition(new Point(0, 0));
		scroll.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));

		topPanel = new JPanel();
		topPanel.add(scroll);
		Dimension d = new Dimension(width, 60);
		topPanel.setPreferredSize(d);

		JPanel buttonsPanel = new JPanel();

		apply = new JButton("Apply");
		apply.addActionListener(this);
		JPanel applyPanel = new JPanel(new BorderLayout());
		applyPanel.add(apply, BorderLayout.WEST);

		add = new JButton("Add");
		add.addActionListener(this);
		JPanel addPanel = new JPanel(new BorderLayout());
		addPanel.add(add, BorderLayout.WEST);

		buttonsPanel.add(applyPanel);
		buttonsPanel.add(addPanel);
		buttonsPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headline, topPanel);
		sp.setBackground(new Color(0xf6, 0xf6, 0xf6));
		sp.setDividerLocation(30);
		sp.setDividerSize(0);

		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, buttonsPanel);
		split.setDividerLocation(110);
		split.setDividerSize(0);
		d = new Dimension(150, 150);
		split.setMaximumSize(d);
		split.setMinimumSize(d);

		enableButtons(false);
		add(split, BorderLayout.CENTER);
	}

	/**
	 * signal that the properties have changed and the panel should be
	 * reconstructed
	 * 
	 */
	private void updatePanel() {
		propPanel.removeAll();
		Enumeration<Object> e = properties.keys();
		String key, value;
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			value = properties.getProperty(key);
			propPanel.add(getPropAsPanel(key, value));
		}
		int num = propPanel.getComponentCount();
		setSplit(num);

		this.revalidate();
	}

	/**
	 * for graphical reasons - resizing the divider between the properties and
	 * the buttons
	 * 
	 * @param num
	 *            num of current properties
	 */
	private void setSplit(int num) {
		if (num < 4)
			scroll.setPreferredSize(new Dimension(width + 20, 23 * num));
		else
			scroll.setPreferredSize(new Dimension(width + 20, 80));
	}

	/**
	 * creates a JSplitPane of a property couple
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the property value
	 * @return a JSplitPane of the property
	 */
	private JSplitPane getPropAsPanel(String key, String value) {
		JTextField keyField = new JTextField(key);
		keyField.setColumns(20);
		JTextField valField = new JTextField(value);
		valField.setColumns(25);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyField, valField);
		sp.setDividerLocation(160);
		sp.setDividerSize(0);
		return sp;
	}

	/**
	 * get the current properties on the panel
	 * 
	 * @return test properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * set this panel properties
	 * 
	 * @param propString
	 *            the properties string
	 */
	public void setProperties(String propString) {
		enableButtons(true);
		if (propString == null)
			propString = "";
		properties = StringUtils.stringToProperties(propString);
		updatePanel();
	}

	/**
	 * for graphical reasons
	 * 
	 * @return the main JSpliPane
	 */
	public JSplitPane getsplit() {
		return split;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(apply)) {
			saveAllTextFields();
		} else if (source.equals(add)) {
			propPanel.add(getPropAsPanel("", ""));
			setSplit(propPanel.getComponentCount());
			this.revalidate();
			scroll.getViewport().setViewPosition(new Point(0, properties.size() * 20));
		}

	}

	/**
	 * saves all the properties from the text fields. if a field key is empty
	 * (meaning key.trim() equals "" then it is not added)
	 * 
	 */
	private void saveAllTextFields() {
		int num = propPanel.getComponentCount();
		properties.clear();
		JSplitPane sp;
		JTextField key, value;
		String keyS, valueS;
		String emptyProperties = "";
		String characterKeys = "";
		for (int i = 0; i < num; i++) {
			sp = (JSplitPane) propPanel.getComponent(i);
			key = (JTextField) sp.getLeftComponent();
			value = (JTextField) sp.getRightComponent();
			keyS = key.getText();
			valueS = value.getText();
			// key or value are empty
			if (keyS.trim().equals("") || valueS.trim().equals("")) {
				emptyProperties += "\n" + keyS + "=" + valueS;
			}
			// not allowed chars in key or value
			else if (StringUtils.hasNotAllowedSpecialCharacters(keyS)
					|| StringUtils.hasNotAllowedSpecialCharacters(valueS)) {
				characterKeys += "\n" + keyS + "=" + valueS;
			} else {
				properties.setProperty(keyS, valueS);
			}
		}
		if (!emptyProperties.equals("")) {
			ErrorPanel.showErrorDialog("Property with Empty keys/values were not added", "empty values were found in the following Properties: "
					+ emptyProperties, ErrorLevel.Warning);

		}
		if (!characterKeys.equals("")) {
			ErrorPanel.showErrorDialog("Properties are not allowed Special Characters from "
					+ StringUtils.notAllowedCharacters, "found at properties: " + characterKeys, ErrorLevel.Warning);
		}
		updatePanel();
		TestInfoPanel.updateFile();
	}

	/**
	 * enable/disable buttons
	 * 
	 * @param enable
	 *            to enable/disable
	 */
	private void enableButtons(boolean enable) {
		apply.setEnabled(enable);
		add.setEnabled(enable);
	}

}
