/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.StringUtils;

/**
 * Parameters table component for agent selection.
 * @author goland
 */
public class ParametersTableHostChooser extends JComponent implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	public static String SELECT_HOST_BUTTON_NAME = "HOST_CHOOSE_SELECT_FILE";
	
	private JTextField field;
	private String[] urls;
	public ParametersTableHostChooser(String selectedHosts){
		super();
		setLayout(new BorderLayout());
		field = new JTextField();
		Dimension dim = field.getSize();
		dim.width = 100;
		field.setSize(dim);
		String[] hosts;
		if (StringUtils.isEmpty(selectedHosts)){
			hosts = new String[0];
		}else {
			hosts = StringUtils.split(selectedHosts, ";");
		}
		setSelectedHosts(hosts);
		add(field,BorderLayout.CENTER);		
		JButton openHostsChooser  = new JButton("...");
		openHostsChooser.setName(SELECT_HOST_BUTTON_NAME);
		openHostsChooser.addActionListener(this);
		add(openHostsChooser,BorderLayout.EAST);	
	}
	
	public void actionPerformed(ActionEvent e) {
		AgentsSelectionDialog agentSelectDialog = new AgentsSelectionDialog();
		try {
			
			agentSelectDialog.initDialog(urls);
		}catch (Exception e1){
			ErrorPanel.showErrorDialog("Failed opening ",e1,ErrorLevel.Error);
		}
		if (agentSelectDialog.isOkay()){
			setSelectedHosts(agentSelectDialog.getSelectedUrls());
		}
	}

	public synchronized void addFocusListener(FocusListener l) {
		field.addFocusListener(l);
	}
	
	public String getSelectedHosts() {
		return field.getText();
	}

	public void setSelectedHosts(String[] selectedHosts) {
		urls = selectedHosts;
		String hosts = selectedHosts == null || selectedHosts.length ==0 ? "" : StringUtils.objectArrayToString(";",(Object[])selectedHosts);
		this.field.setText(new String(hosts));
//		Fixed Ticket #148 - start
		this.field.requestFocus();
//		Fixed Ticket #148 - end
	}
    
	public void repaint() {
		field.repaint();
    }
}
