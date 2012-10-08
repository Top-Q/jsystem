/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.StringReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jsystem.runner.ErrorLevel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

public class ErrorDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6496007195935617105L;

	private String title;

	private String detailedMessage;

	protected JButton okButton;
	
	protected JButton cancelButton;

	private JButton detailsButton;

	private JTextArea textArea;

	private JScrollPane textPanel;

	private static final int MIN_HIGHT = 150;

	private ImageIcon errorIcon;
	
	private boolean cancel = false;

	private boolean showCancel = false;

	private ErrorLevel errorLevel = ErrorLevel.Warning;

	public ErrorDialog(String title, String detaledMessage, ErrorLevel errorLevel, boolean showCancel) {
		this.title = title;
		this.errorLevel = errorLevel;
		this.detailedMessage = detaledMessage;
		this.showCancel = showCancel;
	}

	public void init() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		setPreferredSize(new Dimension(400, MIN_HIGHT));
		setResizable(false);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel);
		setTitle(errorLevel.toString());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);

		switch (errorLevel) {
		case Warning:
			errorIcon = ImageCenter.getInstance().getImage(ImageCenter.ICON_WARNING);
			break;
		case Error:
			errorIcon = ImageCenter.getInstance().getImage(ImageCenter.ICON_ERROR);
			break;
		case Info:
			errorIcon = ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO);
			break;

		default:
			break;
		}

		mainPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 3, screenHeight / 3);

		JLabel label = new JLabel(processMessage(title), errorIcon, JLabel.LEFT);
		label.setFont(new Font("sansserif", Font.BOLD, 13));
		mainPanel.add(label, BorderLayout.NORTH);

		JPanel buttomsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		detailsButton = new JButton("Info");
		detailsButton.addActionListener(this);
		buttomsPanel.add(okButton);
		if(showCancel){
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
			buttomsPanel.add(cancelButton);
		}
		buttomsPanel.add(detailsButton);
		buttomsPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));
		mainPanel.add(buttomsPanel, BorderLayout.SOUTH);

		textArea = new JTextArea(detailedMessage, 8, 0);
		textArea.setVisible(false);
		textArea.setEditable(false);
		textPanel = new JScrollPane(textArea);
		textPanel.setVisible(false);
		mainPanel.add(textPanel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	public boolean isCancel(){
		return cancel;
	}

	private String processMessage(String m) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		BufferedReader reader = new BufferedReader(new StringReader(m));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				buf.append(line);
				buf.append("<br>");
			}
		} catch (Exception ignore) {
		}
		buf.append("</html>");
		return buf.toString();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(detailsButton)) {
			if (textArea.isVisible()) {
				textPanel.setVisible(false);
				textArea.setVisible(false);
				detailsButton.setText("Info");
				setPreferredSize(new Dimension(400, MIN_HIGHT));
			} else {
				textPanel.setVisible(true);
				textArea.setVisible(true);
				detailsButton.setText("Hide");
				setPreferredSize(new Dimension(400, 300));
			}
			pack();
			repaint();
		} else if (e.getSource().equals(okButton)) {
			this.dispose();
		} else if(e.getSource().equals(cancelButton)){
			cancel = true;
			this.dispose();
		}
	}

	public static void main(String[] args) {
		ErrorDialog dialog = new ErrorDialog("Fail to open zip file", StringUtils.getStackTrace(new Exception()) + "\n"
				+ StringUtils.getStackTrace(new Exception()) + "\n" + StringUtils.getStackTrace(new Exception()),
				ErrorLevel.Error, false);
		dialog.init();
	}
}
