/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import jsystem.treeui.images.ImageCenter;
import jsystem.utils.SwingUtils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author guy.arieli
 */
public class PropertiesDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 8791323184447988380L;
	
	private JTable table;
	private JButton okButton;
	private JButton cancelButton;
	private boolean approved = false;
    private CellEditorModel model;
	private GenericCellEditor cellEditor;
    private boolean isEditable = true;

    /** Creates new form NewJFrame */
	public PropertiesDialog(String title, CellEditorModel model,boolean isEditable) {
        this.model = model;
        this.isEditable = isEditable;
		initComponents();
	}

	private void initComponents() {
		table = new JTable();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form");

		table.setModel((TableModel)model);
		table.setName("Table");
		table.setRowHeight(20);
		cellEditor = new GenericCellEditor(model);
		table.getColumnModel().getColumn(1).setCellEditor(cellEditor);

		getContentPane().add(SwingUtils.getJScrollPaneWithWaterMark(ImageCenter.getInstance().getAwtImage(
				ImageCenter.ICON_TEST_TREE_BG), table), BorderLayout.CENTER);

		JPanel okCancelPanel = SwingUtils.getJPannelWithBgImage(ImageCenter.getInstance().getImage(
				ImageCenter.ICON_SCEANRIO_TOOLBAR_BG), 0);
		okCancelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));


		okButton = new JButton("OK");
		okButton.addActionListener(this);
		okCancelPanel.add(okButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		okCancelPanel.add(cancelButton);
		
		getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
		
		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setSelectionForeground(Color.BLACK);
		
		table.setBackground(new Color(0xf6, 0xf6, 0xf6));
		JTableHeader treeTableHeader = table.getTableHeader();
		treeTableHeader.setBackground(new Color(0xe1, 0xe4, 0xe6));

        table.setEnabled(isEditable);
		pack();
	}
	
	public boolean showAndWaitForApprove() throws InterruptedException{
		setVisible(true);
		return approved;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		cellEditor.stopCellEditing();
		if(okButton.equals(e.getSource())){
			approved = true;
		}
		dispose();
	}


}
