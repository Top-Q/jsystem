package jsystem.treeui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import jsystem.treeui.dialog.UnmodifiableFileModel.Option;

/**
 * 
 * @author Tomer Gafner
 * This class represents the Dialog that appear usually when a SaveScenarioAction is fired and JSystem cannot save the files 
 * because they are in read-only status(Unmodifiable).
 * The dialog show which files cannot be modifiable and suggest the user with relevant option how to change
 * the files status.
 * The model for this View is UnmodifiableFileModel.
 */

public class UnmodifiableFileDialog extends JDialog implements  ActionListener{
    
	private static final long serialVersionUID = 1L;
	
	JTextArea unmodifiableFilesTextArea; 
	
	private JPanel contentPane;
    
	private JButton buttonOK;
    
    private  ButtonGroup buttonGroup;
    
    private JButton buttonCancel;
	
    private JPanel panel2;
	
	private UnmodifiableFileModel model;
    
	public static final int OK_OPTION = 1;
    
    public static final int CANCEL_OPTION = 2;
    
    public static int lastUserCloseOperationStatus = 0;
    
    private static UnmodifiableFileDialog instance;
    
    private Option currentSelectedOption;
    
    private ArrayList<FileModifyEnablerRadioButton> buttons = new ArrayList<FileModifyEnablerRadioButton>(); 
    
    static synchronized public UnmodifiableFileDialog getInstance(UnmodifiableFileModel model){
    	if(instance == null){
    		instance = new UnmodifiableFileDialog(model);
    	}
    	instance.refresh();
    	return instance;
    }


	private void refresh() {
		for (FileModifyEnablerRadioButton button : buttons) {
			if(button.getOption() == model.getSelectedOption()){
				button.setSelected(true);
			}
		}
	}


	public void displayFiles(List<File> files) {
		unmodifiableFilesTextArea.setText("");
		for (File file : files) {
			unmodifiableFilesTextArea.append(file.getAbsolutePath()+"\n");
		}
	}



	private UnmodifiableFileDialog(UnmodifiableFileModel model) {
		this.model = model;
    	ArrayList<Option> options = model.getAvailableOptions();
    	for (int i = 0; i < options.size(); i++) {
    		FileModifyEnablerRadioButton radioBtn = new FileModifyEnablerRadioButton();
    		radioBtn.addActionListener(this);
    		radioBtn.setOption(options.get(i));
    		GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = i + 2;
	        gbc.weighty = 1.0;
	        gbc.anchor = GridBagConstraints.WEST;
	        panel2.add(radioBtn, gbc);
	        buttonGroup.add(radioBtn);
	        if(options.get(i) == model.getSelectedOption()){
	        	radioBtn.setSelected(true);
	        	currentSelectedOption = model.getSelectedOption();
	        }
	        buttons.add(radioBtn);
		}
		setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 3, screenHeight / 3);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
    	lastUserCloseOperationStatus = OK_OPTION;
    	model.setSelectedOption(currentSelectedOption);
    	// add your code here
        dispose();
    }

    private void onCancel() {
    	lastUserCloseOperationStatus = CANCEL_OPTION;
// add your code here if necessary
        dispose();
    }

    public int getLastUserCloseOperationStatus(){
    	return lastUserCloseOperationStatus;
    }
   
    
//    public static void main(String[] args) {
//        UnmodifiedFileDialog dialog = new UnmodifiedFileDialog();
//        dialog.setTitle("Files in read only status");
//        ImageIcon icon = new ImageIcon("windows-warning.png");
//        dialog.setIconImage(icon.getImage());
//        dialog.setPreferredSize(new Dimension(700, 350));
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        contentPane.add(panel1, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(scrollPane1, gbc);
        unmodifiableFilesTextArea = new JTextArea();
        unmodifiableFilesTextArea.setEditable(false);
        unmodifiableFilesTextArea.setEnabled(true);
        unmodifiableFilesTextArea.setRows(0);
        unmodifiableFilesTextArea.setText("");
        scrollPane1.setViewportView(unmodifiableFilesTextArea);
        panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 10, 20, 10);
        contentPane.add(panel2, gbc);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(2);
        label1.setText("You can try to change the files to be modifiable using one of the following options");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel2.add(label1, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 20, 10);
        contentPane.add(panel3, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(panel4, gbc);
        buttonOK = new JButton();
        buttonOK.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel4.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel4.add(buttonCancel, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel5, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("JSystem cannot save the scenario until the following files will be modifiable");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel5.add(label2, gbc);
        buttonGroup = new ButtonGroup();
    }


	/**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof FileModifyEnablerRadioButton){
			FileModifyEnablerRadioButton btn = (FileModifyEnablerRadioButton) e.getSource();
			currentSelectedOption = btn.getOption();
		}
	}



}
