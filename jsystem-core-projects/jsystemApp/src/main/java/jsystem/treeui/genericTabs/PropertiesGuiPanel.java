package jsystem.treeui.genericTabs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel to display, edit and save properties files.
 */
class PropertiesGuiPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int PREFERRED_TEXT_FIELD_WIDTH = 400;
	private static Logger log = Logger.getLogger(PropertiesGuiPanel.class.getName());

	private final File propertiesFile;
	private Properties properties;
	private final Properties labels;
	private JPanel propertiesEntries;
	private Map<String, JTextField> textFields;

	/**
	 * Constructor
	 * 
	 * @param propertiesFile
	 *            The properties file to read and edit
	 * @throws IOException
	 *             If the file was not found or can't be accessed
	 */
	public PropertiesGuiPanel(File propertiesFile) throws IOException {
		super();
		this.propertiesFile = propertiesFile;
		this.labels = new Properties();
		initialize();
	}

	private Properties readPropertiesFromFile() throws IOException {
		if (propertiesFile == null || !propertiesFile.exists()) {
			throw new FileNotFoundException("Properties file was not found");
		}
		Properties p = new Properties();
		try (FileInputStream fis = new FileInputStream(propertiesFile)) {
			p.load(fis);
		}
		return p;
	}

	/**
	 * 
	 * @throws IOException
	 *             If failed to find or access the properties file
	 */
	private void initialize() throws IOException {
		this.properties = readPropertiesFromFile();
		propertiesEntries = new JPanel();
		propertiesEntries.setAlignmentX(Component.LEFT_ALIGNMENT);
		propertiesEntries.setLayout(new BoxLayout(propertiesEntries, BoxLayout.PAGE_AXIS));
		add(propertiesEntries);

		addPropertiesToConfigEntryPanel();
		initButtonPanel();
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		setSize(500, 500);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setVisible(true);
	}

	private void initButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setBorder(BorderFactory.createLineBorder(buttonPanel.getBackground(), 10));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setSize(buttonPanel.getPreferredSize());
		JButton okButton = new JButton("Save");
		okButton.setAlignmentX(LEFT_ALIGNMENT);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setAlignmentX(LEFT_ALIGNMENT);
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cancel();
			}

		});

		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);
		add(buttonPanel);
	}

	private void cancel() {
		log.finer("Canceling the editing of the properties");
		try {
			readPropertiesFromFile();
			addPropertiesToConfigEntryPanel();
			propertiesEntries.revalidate();
			propertiesEntries.repaint();
			JOptionPane.showMessageDialog(this, "Changes in the Difido properties were canceled", "Cancel message",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e1) {
			log.warning("Failed to read properties file due to " + e1.getMessage());
			e1.printStackTrace();
		}

	}

	private void addPropertiesToConfigEntryPanel() {
		this.textFields = new HashMap<String, JTextField>();
		propertiesEntries.removeAll();
		for (Object key : properties.keySet()) {
			String strKey = (String) key;
			JLabel label = new JLabel(label(strKey));
			label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			label.setAlignmentX(LEFT_ALIGNMENT);
			propertiesEntries.add(label);
			JTextField textField = new JTextField(render(properties.getProperty(strKey)));
			textField
					.setMaximumSize(new Dimension(
							textField.getPreferredSize().width > PREFERRED_TEXT_FIELD_WIDTH
									? textField.getPreferredSize().width : PREFERRED_TEXT_FIELD_WIDTH,
					textField.getPreferredSize().height));
			textField.setColumns(30);
			textField.setAlignmentX(LEFT_ALIGNMENT);
			textFields.put(strKey, textField);
			propertiesEntries.add(textField);
		}
	}

	private String label(String strKey) {
		String label = null;
		if (labels != null && labels.containsKey(strKey)) {
			label = labels.getProperty(strKey);
		} else {
			label = strKey;
		}
		label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
		return label.replace(".", " ") + ":";
	}

	private String render(Object object) {
		if (object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

	private void save() {
		log.finer("Saving the properties to file");
		for (Entry<String, JTextField> entry : textFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getText());
		}
		try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
			properties.store(fos, "Properties were edited using the JSystem GUI");
			JOptionPane.showMessageDialog(this, "Difido properties were saved successfully", "Save confirmation",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			log.warning("Failed to save to propeties file due to " + e.getMessage());
			JOptionPane.showMessageDialog(this, "Failed saving Difido properties due to " + e.getMessage(),
					"Error message", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * Easy way to test the JFrame
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		JPanel panel = new PropertiesGuiPanel(new File("C:\\Users\\agmon\\Desktop\\runner\\remoteDifido.properties"));
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);

	}

}