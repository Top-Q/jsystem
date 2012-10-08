/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import jsystem.framework.report.Reporter;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.SwingUtils;

/**
 * @author guy.arieli
 */
public class TestInfoPanel extends JPanel implements ItemListener {

	private static final long serialVersionUID = -1510054274014713603L;

	public static TestInfoPanel testInfo;

	JLabel lbTestName;

	JLabel lbCount;

	JLabel lbExecutionTime;

	JLabel lbLabel3;

	JComboBox cmbTestStatus;

	JLabel lbTestNameValue;

	JLabel lbCountValue;

	JLabel lbExecutionTimeValue;

	JLabel lbLabel7;

	JPanel pnPanel0;

	JPanel pnErrorCause;

	JTextArea taErrorCauseValue;

	JPanel pnParameters;

	JTextArea taParametersValue;

	JPanel pnDocumentation;

	JTextArea taDocumentationValue;

	JPanel pnSteps;

	JTextArea taStepsValue;

	PropertiesPanel pnProperties;

	public static PublisherTreePanel publisherPanel;

	public void setTestParameters(String tname, int tcount, long executionTime, int status, String parameters, String documentation,
			String steps, String properties, String errorCause) {
		lbTestNameValue.setText(tname);
		lbCountValue.setText(Integer.toString(tcount));
		lbExecutionTimeValue.setText(Long.toString(executionTime / 1000) + "  Sec.");
		if (status == Reporter.PASS) {
			cmbTestStatus.setSelectedItem("Pass");
		} else if (status == Reporter.FAIL) {
			cmbTestStatus.setSelectedItem("Fail");
		} else {
			cmbTestStatus.setSelectedItem("Warning");
		}
		taParametersValue.setText(parameters.replace(' ', '\n'));
		taDocumentationValue.setText(documentation);
		taStepsValue.setText(steps);
		pnProperties.setProperties(properties);
		taErrorCauseValue.setText(errorCause);
	}

	public void setEditing(boolean edit) {
		cmbTestStatus.setVisible(edit);
		taDocumentationValue.setVisible(edit);
		taParametersValue.setVisible(edit);
		taStepsValue.setVisible(edit);
		taErrorCauseValue.setVisible(edit);
	}

	/**
	 */

	/**
	 */
	public TestInfoPanel(PublisherTreePanel parent) {
		publisherPanel = parent;
		setLayout(new BorderLayout());
		pnPanel0 = new JPanel();

		pnPanel0.setBackground(new Color(0xf6, 0xf6, 0xf6));
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout(gbPanel0);

		lbTestName = new JLabel("Test name:");
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 8, 0, 16);
		gbPanel0.setConstraints(lbTestName, gbcPanel0);
		pnPanel0.add(lbTestName);

		lbTestNameValue = new JLabel("");
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(lbTestNameValue, gbcPanel0);
		pnPanel0.add(lbTestNameValue);

		lbCount = new JLabel("Count:");
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 8, 0, 16);
		gbPanel0.setConstraints(lbCount, gbcPanel0);
		pnPanel0.add(lbCount);

		lbCountValue = new JLabel("");
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(lbCountValue, gbcPanel0);
		pnPanel0.add(lbCountValue);

		lbExecutionTime = new JLabel("Execution time:");
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 3;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 8, 0, 16);
		gbPanel0.setConstraints(lbExecutionTime, gbcPanel0);
		pnPanel0.add(lbExecutionTime);

		lbExecutionTimeValue = new JLabel("0");
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 3;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(lbExecutionTimeValue, gbcPanel0);
		pnPanel0.add(lbExecutionTimeValue);

		lbLabel7 = new JLabel("         ");
		gbcPanel0.gridx = 3;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(lbLabel7, gbcPanel0);
		pnPanel0.add(lbLabel7);

		lbLabel3 = new JLabel("Status:");
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 8, 0, 16);
		gbPanel0.setConstraints(lbLabel3, gbcPanel0);
		pnPanel0.add(lbLabel3);

		JPanel pnConbo = new JPanel();
		pnConbo.setLayout(new BorderLayout());
		String[] dataTestStatus = { "Pass", "Fail", "Warning" };
		cmbTestStatus = new JComboBox(dataTestStatus);
		cmbTestStatus.setMaximumSize(new Dimension(12, 8));
		cmbTestStatus.setSize(new Dimension(12, 8));
		cmbTestStatus.addItemListener(this);
		pnConbo.add(cmbTestStatus, BorderLayout.WEST);
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.NONE;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(pnConbo, gbcPanel0);
		pnPanel0.add(pnConbo);

		pnErrorCause = new JPanel();
		pnErrorCause.setBackground(new Color(0xf6, 0xf6, 0xf6));
		pnErrorCause.setBorder(BorderFactory.createTitledBorder("Error Cause"));
		GridBagLayout gbErrorCause = new GridBagLayout();
		GridBagConstraints gbcErrorCause = new GridBagConstraints();
		pnErrorCause.setLayout(gbErrorCause);
		taErrorCauseValue = new JTextArea(2, 10);
		JScrollPane scpErrorCauseValue = new JScrollPane(taErrorCauseValue);
		scpErrorCauseValue.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
		gbcErrorCause.gridx = 1;
		gbcErrorCause.gridy = 1;
		gbcErrorCause.gridwidth = 3;
		gbcErrorCause.gridheight = 1;
		gbcErrorCause.fill = GridBagConstraints.HORIZONTAL;
		gbcErrorCause.weightx = 1;
		gbcErrorCause.weighty = 1;
		gbcErrorCause.anchor = GridBagConstraints.NORTH;
		gbErrorCause.setConstraints(scpErrorCauseValue, gbcErrorCause);
		pnErrorCause.add(scpErrorCauseValue);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 5;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(pnErrorCause, gbcPanel0);
		pnPanel0.add(pnErrorCause);

		/**
		 * 
		 */

		JTabbedPane textTabbed = SwingUtils.getJTabbedPaneWithBgImage(ImageCenter.getInstance()
				.getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG), ImageCenter.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG));

		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 7;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(textTabbed, gbcPanel0);
		pnPanel0.add(textTabbed);

		pnParameters = new JPanel();
		GridBagLayout gbParameters = new GridBagLayout();
		GridBagConstraints gbcParameters = new GridBagConstraints();
		pnParameters.setLayout(gbParameters);
		taParametersValue = new JTextArea(6, 10);
		JScrollPane scpParametersValue = new JScrollPane(taParametersValue);
		gbcParameters.gridx = 1;
		gbcParameters.gridy = 1;
		gbcParameters.gridwidth = 3;
		gbcParameters.gridheight = 1;
		gbcParameters.fill = GridBagConstraints.BOTH;
		gbcParameters.weightx = 1;
		gbcParameters.weighty = 1;
		gbcParameters.anchor = GridBagConstraints.NORTH;
		gbParameters.setConstraints(scpParametersValue, gbcParameters);
		scpParametersValue.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
		pnParameters.add(scpParametersValue);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(pnParameters, gbcPanel0);
		textTabbed.addTab("Parameters", pnParameters);

		pnDocumentation = new JPanel();
		GridBagLayout gbDocumentation = new GridBagLayout();
		GridBagConstraints gbcDocumentation = new GridBagConstraints();
		pnDocumentation.setLayout(gbDocumentation);

		taDocumentationValue = new JTextArea(10, 10);
		JScrollPane scpDocumentationValue = new JScrollPane(taDocumentationValue);
		gbcDocumentation.gridx = 1;
		gbcDocumentation.gridy = 1;
		gbcDocumentation.gridwidth = 3;
		gbcDocumentation.gridheight = 1;
		gbcDocumentation.fill = GridBagConstraints.BOTH;
		gbcDocumentation.weightx = 1;
		gbcDocumentation.weighty = 1;
		gbcDocumentation.anchor = GridBagConstraints.CENTER;
		gbDocumentation.setConstraints(scpDocumentationValue, gbcDocumentation);
		scpDocumentationValue.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
		pnDocumentation.add(scpDocumentationValue);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(pnDocumentation, gbcPanel0);
		textTabbed.addTab("Documentation", pnDocumentation);

		pnSteps = new JPanel();
		GridBagLayout gbSteps = new GridBagLayout();
		GridBagConstraints gbcSteps = new GridBagConstraints();
		pnSteps.setLayout(gbSteps);

		taStepsValue = new JTextArea(10, 10);
		JScrollPane scpStepsValue = new JScrollPane(taStepsValue);
		gbcSteps.gridx = 1;
		gbcSteps.gridy = 1;
		gbcSteps.gridwidth = 3;
		gbcSteps.gridheight = 1;
		gbcSteps.fill = GridBagConstraints.BOTH;
		gbcSteps.weightx = 1;
		gbcSteps.weighty = 1;
		gbcSteps.anchor = GridBagConstraints.CENTER;
		gbSteps.setConstraints(scpStepsValue, gbcSteps);
		scpStepsValue.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
		pnSteps.add(scpStepsValue);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets(0, 0, 0, 0);
		gbPanel0.setConstraints(pnSteps, gbcPanel0);
		textTabbed.addTab("Steps", pnSteps);

		pnProperties = new PropertiesPanel();
		gbcSteps.gridx = 1;
		gbcSteps.gridy = 1;
		gbcSteps.gridwidth = 3;
		gbcSteps.gridheight = 1;
		gbcSteps.fill = GridBagConstraints.HORIZONTAL;
		gbcSteps.weightx = 1;
		gbcSteps.weighty = 1;
		gbcSteps.anchor = GridBagConstraints.CENTER;
		// gbSteps.setConstraints(pnProperties.getsplit(), gbcSteps);

		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbPanel0.setConstraints(pnProperties, gbcPanel0);
		textTabbed.addTab("Properties", pnProperties);

		Dimension d = new Dimension(50, 50);
		pnPanel0.setMaximumSize(d);
		pnPanel0.setMinimumSize(d);
		add(pnPanel0, BorderLayout.CENTER);
		setVisible(true);
		setEditing(false);
	}

	public int getStatus() {
		return cmbTestStatus.getSelectedIndex();
	}

	public String getParameterString() {
		return taParametersValue.getText().replace('\n', ' ');
	}

	public String getDocumentation() {
		return taDocumentationValue.getText();
	}

	public String getSteps() {
		return taStepsValue.getText();
	}

	public Properties getProperties() {
		return pnProperties.getProperties();
	}

	public String getErrorCause() {
		return taErrorCauseValue.getText();
	}

	/**
	 * uses the PublisherPanel to update the xml file
	 * 
	 */
	public static void updateFile() {
		publisherPanel.valueChanged(null);
	}

	public void itemStateChanged(ItemEvent e) {

	}
}
