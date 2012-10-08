/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.scenarionamehook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jsystem.framework.scenario.ScenariosManager;

public class ProjectNameDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JTextArea nameTxt;
	
	private void initDialog() {
		int width = 300;
		int height = 100;
		setTitle("Project Name");
		setResizable(true);
		setLayout(new BorderLayout());
		setSize(new Dimension(width, height));
		setLocation(new Point(200, 200));

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BorderLayout());
		namePanel.setSize(new Dimension(width, height));

		nameTxt = new JTextArea();
		nameTxt.setBackground(Color.WHITE);
		nameTxt.setSize(new Dimension(width, height / 2));

		JButton okayButton = new JButton("Ok");	
		okayButton.setName("okay");
		okayButton.addActionListener(this);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(width / 2, height / 4));
		cancelButton.addActionListener(this);
		cancelButton.setName("cancel");
		
		JPanel buttonsPanle = new JPanel();
		buttonsPanle.add(okayButton);
		buttonsPanle.add(cancelButton);

		namePanel.add(nameTxt, BorderLayout.CENTER);
		namePanel.add(buttonsPanle, BorderLayout.SOUTH);
		add(namePanel);

	}
	
	private void initData() {
		String projectName = ScenariosManager.getInstance().getCurrentScenario().getProjectName();
		nameTxt.setText(projectName);
	}
	
	public static void showProjectNameDialog() {
		ProjectNameDialog dialog = new ProjectNameDialog();
		dialog.initDialog();
		dialog.initData();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (((JButton)e.getSource()).getName().equals("okay")){
			String projectName = nameTxt.getText();
			ScenariosManager.getInstance().getCurrentScenario().setProjectName(projectName);
			dispose();
		}
		
		if (((JButton)e.getSource()).getName().equals("cancel")){
			dispose();
		}		
	}
}
