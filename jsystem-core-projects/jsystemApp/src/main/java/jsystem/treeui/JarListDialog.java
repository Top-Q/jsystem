/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import jsystem.extensions.report.xml.Reader;
import jsystem.runner.loader.ClassPathBuilder;
import jsystem.treeui.images.ImageCenter;

/**
 * This class`s job is to build a Dialog with all the jars that serveing the
 * Runner. Every jave name will have it version if exists. To get access this
 * Dialog at the Runner GUI choose: Tools -> Show Jar List.
 * 
 * @author Uri.Koaz
 * 
 */
public class JarListDialog {
	
	JLabel sortMessageLabel;
	
	// ;for getting only one instance of this class.
	private static JarListDialog ref;

	// collectios of info
	Vector<String> jarNames;

	Vector<String> jarPath;

	Vector<Object> jarVersions;

	// dialog itself.
	JarsTableDialog tableDialog;

	private static Logger log = Logger.getLogger(Reader.class.getName());

	/**
	 * The constructor is creating the jarlist and init the Table Dialog.
	 * 
	 */
	public JarListDialog() {
		getJarList();
		tableDialog = new JarsTableDialog();
	}

	/**
	 * singletone mechanizem.
	 * 
	 * @return {@link JarListDialog}
	 */
	public static JarListDialog getInstance() {
		if (ref == null) {
			ref = new JarListDialog();
		}
		return ref;
	}

	/**
	 * collecting data to both vectors.
	 */
	private void getJarList() {
		// get the classpath list

		/**
		 * break the jar list according to the File.pathSeparatorChar (Linux = :,
		 * Win = ;)
		 */
		String[] jars = ClassPathBuilder.getClassPath().split(Character.toString(File.pathSeparatorChar));
		jarNames = new Vector<String>();
		jarPath = new Vector<String>();
		jarVersions = new Vector<Object>();
		for (int i = 0; i < jars.length; i++) {
			// filtering the *.jar files
			if (jars[i].endsWith(".jar")) {

				try {

					// creating File object only to get the name of the file
					// without the path.
					File f = new File(jars[i]);

					// add to jar names vector the jar name.
					jarNames.add("  " + f.getName());
					// add to path list the path of the jar
					jarPath.add("  " + f.getAbsolutePath());
					JarFile jar = new JarFile(jars[i]);
					ZipEntry manifestEntry = jar.getEntry("META-INF/MANIFEST.MF");
					if (manifestEntry != null) {
						InputStream in = jar.getInputStream(manifestEntry);
						Properties p = new Properties();
						p.load(in);
						Object ver = p.get("Specification-Version");
						// because jsystem dont have any specification to follow
						// implementation version is the thing to look for in
						// jsystem component
						if (ver == null) {
							ver = p.get("Implementation-Version");
						}
						jarVersions.add(ver);

					} else {
						jarVersions.add("");
					}

				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to read " + jars[i], e);
					jarVersions.add("");
				}
			}
		}
	}

	/**
	 * checking if the Dialog is already on, if not , showing it.
	 * 
	 */
	public void showWindow() {
		getJarList();
		tableDialog = new JarsTableDialog();
		if (!tableDialog.isVisible()) {
			tableDialog.setVisible(true);
		}
	}

	/**
	 * The actual Dialog structure.
	 * 
	 * @author Uri.Koaz
	 * 
	 */
	class JarsTableDialog extends JDialog implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		JTable table;

		JPanel mainPanel;

		Object[][] data;

		JTextField textFind; // text field for "finding" jar.

		public JarsTableDialog() {
			setTitle("Jar List");
			setModalityType(ModalityType.APPLICATION_MODAL);

			((Frame) this.getOwner()).setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			setLocation(screenWidth / 4, screenHeight / 5);
			Dimension d = new Dimension((int) (screenWidth / 1.5), (int) (screenHeight / 1.5));
			setPreferredSize(d);
			data = new Object[jarNames.size()][3];

			for (int i = 0; i < jarNames.size(); i++) {
				data[i][0] = jarNames.get(i);
				data[i][1] = jarPath.get(i);
				data[i][2] = jarVersions.get(i);
			}

			textFind = new JTextField(10);

			table = new JTable(data, new String[] { "Jar Name", "Jar Path", "Jar Version" });
			table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
			DefaultTableCellRenderer tcrColumn = new DefaultTableCellRenderer();
			table.getTableHeader().setDefaultRenderer(new JarsTableHeaderRendrer());

			// aligen to left the versions column
			tcrColumn.setHorizontalAlignment(SwingConstants.LEFT);
			table.getColumnModel().getColumn(1).setCellRenderer(tcrColumn);
			table.getColumnModel().getColumn(2).setCellRenderer(tcrColumn);

			// set the size of coulmn 0
			TableColumn column = table.getColumnModel().getColumn(0);
			table.setSize(d);
			column.setPreferredWidth(400);
			column = table.getColumnModel().getColumn(1);
			column.setPreferredWidth(800);
			column = table.getColumnModel().getColumn(2);
			column.setPreferredWidth(200);

			// "block" the table from writing
			table.setEnabled(false);

			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.setPreferredSize(d);
			mainPanel.setMinimumSize(d);
			//ImageIcon icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_AQUA_LOGO);


			// define button and add a action listner to it.
			JButton findButton = new JButton("Find Jars");
			findButton.addActionListener(this);


			
			JPanel findPanel = new JPanel();
			findPanel.setLayout(new GridBagLayout());
			findPanel.add(findButton,0);
			findPanel.add(textFind,1);
			JPanel findPanel2 = new JPanel();
			findPanel2.setLayout(new BorderLayout());
			findPanel2.add(findPanel,BorderLayout.WEST);		
			mainPanel.add(findPanel2,BorderLayout.NORTH);
			
			JButton sortButton = new JButton("Sort Jars");
			sortButton.addActionListener(this);
			sortMessageLabel= new JLabel("The jars appear in the same order they are loaded");	
			sortMessageLabel.setVisible(true);
	
			JPanel sortPanel = new JPanel();
			sortPanel.setLayout(new GridBagLayout());
			sortPanel.add(sortButton,0);
			sortPanel.add(sortMessageLabel,1);
			JPanel sortPanel2 = new JPanel();
			sortPanel2.setLayout(new BorderLayout());
			sortPanel2.add(sortPanel,BorderLayout.WEST);			
			mainPanel.add(sortPanel2,BorderLayout.SOUTH);
			
			
			
			JScrollPane tableScroll = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tableScroll.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));

			mainPanel.add(tableScroll, BorderLayout.CENTER);
			mainPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));
			//setMinimumSize(d);////
			getContentPane().add(mainPanel);
			setAlwaysOnTop(true);
			pack();
		}

		public void showFrame() {
			setVisible(true);
		}

		int sortCounter=0;		
		Vector<String> tmpJarNames;
		Vector<String> tmpJarPath ;
		Vector<Object> tmpJarVersions;
		public void actionPerformed(ActionEvent e) {
			
			tmpJarNames = new Vector<String>();
			tmpJarPath = new Vector<String>();
			tmpJarVersions = new Vector<Object>();
			
			if (e.getActionCommand() == "Find Jars") {
				sortMessageLabel.setVisible(false);
				findJar();				
			}						
			if (e.getActionCommand() == "Sort Jars") {				
				sortJars();
			}					
		}
		
		public void findJar(){
			
			if(!textFind.getText().trim().equals("")){
				for(int i=0 ;i<jarNames.size();i++){
					tmpJarNames.add(i, jarNames.get(i));
					tmpJarPath.add(i, jarPath.get(i));	
					tmpJarVersions.add(i, jarVersions.get(i));	
				}
				
				
				int tmpIndex= 0;
				for(int i=0 ;i<tmpJarNames.size();i++){
					if ((jarNames.get(i).toString().trim()).indexOf(textFind.getText().trim()) != -1) {
						tmpJarNames.setElementAt(jarNames.get(i), tmpIndex);
						tmpJarPath.setElementAt(jarPath.get(i), tmpIndex);
						tmpJarVersions.setElementAt(jarVersions.get(i), tmpIndex);
						tmpIndex++;
					}
				}
				
				for(int i=0 ;i<tmpJarNames.size();i++){
					if ((jarNames.get(i).toString().trim()).indexOf(textFind.getText().trim()) == -1) {
						tmpJarNames.setElementAt(jarNames.get(i), tmpIndex);
						tmpJarPath.setElementAt(jarPath.get(i), tmpIndex);
						tmpJarVersions.setElementAt(jarVersions.get(i), tmpIndex);
						tmpIndex++;
					}
				}
					
				for (int i = 0; i < table.getRowCount(); i++) {					
					table.setValueAt( tmpJarNames.elementAt(i).toString(), i, 0);
					table.setValueAt( tmpJarPath.elementAt(i).toString(), i, 1);
					if(tmpJarVersions.elementAt(i)!=null){
						table.setValueAt(((String)tmpJarVersions.elementAt(i)).toString(), i, 2);
					}else{
						table.setValueAt(" ", i, 2);
					}
				}
				
				for (int i = 0; i < table.getRowCount(); i++) {
					if (!textFind.getText().equals("")) {
						String newValue = tmpJarNames.get(i).toString();
						if ((table.getValueAt(i, 0).toString().trim()).indexOf(textFind.getText().trim()) != -1) {
							table.setValueAt("<bold>" + newValue, i, 0);
						} else {
							table.setValueAt(newValue, i, 0);
						}
					}
				}
			}else{
				sortCounter=-1;
				sortJars();
			}
		}
		
		public void sortJars(){
			sortCounter++;
			for(int i=0 ;i<jarNames.size();i++){
				tmpJarNames.add(i, jarNames.get(i));
				tmpJarPath.add(i, jarPath.get(i));	
				tmpJarVersions.add(i, jarVersions.get(i));	
			}
						
			if(sortCounter%3==1){
				sortMessageLabel.setText("Sort by ABC");
				sortMessageLabel.setVisible(true);
				for(int i=0 ;i<tmpJarNames.size()-1;i++){					
					for(int j=0 ;j<tmpJarNames.size()-1;j++){							
						if(tmpJarNames.elementAt(j).toString().compareTo(tmpJarNames.elementAt(j+1).toString())>0){									
							
							String tmp = tmpJarNames.elementAt(j);
							tmpJarNames.setElementAt(tmpJarNames.elementAt(j+1), j);
							tmpJarNames.setElementAt(tmp, j+1);
							
							tmp = tmpJarPath.elementAt(j);
							tmpJarPath.setElementAt(tmpJarPath.elementAt(j+1), j);
							tmpJarPath.setElementAt(tmp, j+1);
							
							tmp = (String) tmpJarVersions.elementAt(j);
							tmpJarVersions.setElementAt(tmpJarVersions.elementAt(j+1), j);
							tmpJarVersions.setElementAt(tmp, j+1);																
						}
					}	
				}
			}
			
			if(sortCounter%3==2){	
				sortMessageLabel.setText("Sort by ZYX");
				sortMessageLabel.setVisible(true);
				for(int i=0 ;i<tmpJarNames.size()-1;i++){					
					for(int j=0 ;j<tmpJarNames.size()-1;j++){
						if(tmpJarNames.elementAt(j).toString().compareTo(tmpJarNames.elementAt(j+1).toString())<0){								
							String tmp = tmpJarNames.elementAt(j);
							tmpJarNames.setElementAt(tmpJarNames.elementAt(j+1), j);
							tmpJarNames.setElementAt(tmp, j+1);
							
							tmp = tmpJarPath.elementAt(j);
							tmpJarPath.setElementAt(tmpJarPath.elementAt(j+1), j);
							tmpJarPath.setElementAt(tmp, j+1);
							
							tmp = (String) tmpJarVersions.elementAt(j);
							tmpJarVersions.setElementAt(tmpJarVersions.elementAt(j+1), j);
							tmpJarVersions.setElementAt(tmp, j+1);	
						}
					}	
				}
			}
			
			if(sortCounter%3==0){
				sortMessageLabel.setText("The jars appear in the same order they are loaded");
				sortMessageLabel.setVisible(true);

				 tmpJarNames = jarNames;
				 tmpJarPath = jarPath;
				 tmpJarVersions = jarVersions;
				
			}
			
			
			for (int i = 0; i < table.getRowCount(); i++) {					
				table.setValueAt( tmpJarNames.elementAt(i).toString(), i, 0);
				table.setValueAt( tmpJarPath.elementAt(i).toString(), i, 1);
				if(tmpJarVersions.elementAt(i)!=null){
					table.setValueAt(((String)tmpJarVersions.elementAt(i)).toString(), i, 2);
				}else{
					table.setValueAt(" ", i, 2);
				}
			}
			
			for (int i = 0; i < table.getRowCount(); i++) {
				if (!textFind.getText().equals("")) {
					String newValue = tmpJarNames.get(i).toString();
					if ((table.getValueAt(i, 0).toString().trim()).indexOf(textFind.getText().trim()) != -1) {
						table.setValueAt("<bold>" + newValue, i, 0);
					} else {
						table.setValueAt(newValue, i, 0);
					}
				}
			}
			
			
		}
		
		
	}

	/**
	 * Defined the "bold" for table cell.
	 * 
	 * @author Uri.Koaz
	 * 
	 */
	public class CustomTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {

			Component cell = null;

			// creating the relevant component (JFieldText/StatusPanel)
			boolean isBold = (value.toString().startsWith("<bold>"));
			if (isBold) {
				cell = setJTextFieldAttr(value.toString().subSequence("<bold>".length(), value.toString().length())
						+ "", isBold);
			} else {
				cell = setJTextFieldAttr(value + "", isBold);
			}

			return cell;
		} // of getTableCellRendererComponent function

		private JTextField setJTextFieldAttr(String cellText, boolean isBold) {
			JTextField cell = new JTextField();
			cell.setText(cellText);
			cell.setBorder(BorderFactory.createEmptyBorder());
			if (isBold)
				cell.setFont(new Font("Times", Font.BOLD, 14));
			return cell;
		} // of setJTextFieldAttr

	} // of class

	/**
	 * Main for checking the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JarListDialog jlp = JarListDialog.getInstance();
		jlp.showWindow();
	}

	/**
	 * Rendrerer for table header
	 * 
	 * @author uri.koaz
	 * 
	 */
	public class JarsTableHeaderRendrer extends JLabel implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {

			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TABLE_HEADER));

			setForeground(Color.white);

			switch (column) {
			case 0:
				setText("Jar Name");
				break;

			case 1:
				setText("Jar Path");
				break;
			case 2:
				setText("Jar Version");
				break;
			default:
				break;
			}

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(JLabel.CENTER);

			return this;
		}

		public void paint(Graphics g) {
			Dimension size = this.getSize();
			g.drawImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TABLE_HEADER), 0, 0, size.width,
					size.height, this);

			super.paint(g);
		}
	}
}
