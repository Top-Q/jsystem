package jsystem.extensions.paramproviders;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.utilities.GenericCellEditor;
import jsystem.treeui.utilities.ParameterProviderListener;
import jsystem.treeui.utilities.TableCellListener;
import jsystem.utils.ResourcesUtils;
import jsystem.utils.SwingUtils;
import jsystem.utils.beans.BeanElement;

public class CsvPropertiesDialog extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1417293276325153499L;
	
	enum EnumDelimiter {
		
		COMMA(','),
		SEMICOLON(';'),
		TAB('\t'),
		VERTICAL_LINE('|')
		;
		
		EnumDelimiter(char delimiter) {
			this.delimiter = delimiter;
		}
		
		char delimiter;
		
	}
	

	private JTable table;
	
	private JButton duplicateButton;

	private JButton addButton;

	private JButton removeButton;

	private JButton upButton;

	private JButton downButton;

	private JButton okButton;

	private JButton cancelButton;
	
	private JButton importButton;
	
	private JButton exportButton;

	private boolean approved = false;

	private ArrayList<LinkedHashMap<String, String>> mapValues;

	private int currentSelectedRow = -1;

	private BeanCellEditorModel model;

	private boolean isEnabled = true;

	private LinkedHashMap<String, String> referanceMap;

	private GenericCellEditor gce;
	
	private EnumDelimiter selectedDelimiter = EnumDelimiter.COMMA;

	private List<ParameterProviderListener> listenersList = new ArrayList<ParameterProviderListener>();

	public CsvPropertiesDialog(ArrayList<LinkedHashMap<String, String>> mapValues, String title,
			ArrayList<BeanElement> beanElements, LinkedHashMap<String, String> referanceMap, BeanCellEditorModel model,
			boolean isEditable) {
		this.mapValues = mapValues;
		this.referanceMap = referanceMap;
		this.model = model;
		if (this.model == null) {
			this.model = new BeanCellEditorModel(beanElements, mapValues);
		}
		this.isEnabled = isEditable;
		initComponents();
	}

	private void initComponents() {
		table = new JTable();
		table.getSelectionModel().addListSelectionListener(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form");
		table.setModel(model);
		table.setName("Table");

		gce = new GenericCellEditor(model);

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellEditor(gce);
		}

		table.setRowHeight(20);

		getContentPane().add(
				SwingUtils.getJScrollPaneWithWaterMark(
						ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TEST_TREE_BG), table),
				BorderLayout.CENTER);

		JPanel okCancelPanel = SwingUtils.getJPannelWithBgImage(
				ImageCenter.getInstance().getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG), 0);

		okCancelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		duplicateButton = new JButton("Duplicate");
		duplicateButton.addActionListener(this);
		duplicateButton.setEnabled(isEnabled);
		okCancelPanel.add(duplicateButton);
		
		addButton = new JButton("Add...");
		addButton.addActionListener(this);
		addButton.setEnabled(isEnabled);
		okCancelPanel.add(addButton);

		removeButton = new JButton("Remove");
		removeButton.setEnabled(isEnabled);
		removeButton.addActionListener(this);
		okCancelPanel.add(removeButton);

		upButton = new JButton("Up");
		upButton.setEnabled(isEnabled);
		upButton.addActionListener(this);
		okCancelPanel.add(upButton);

		downButton = new JButton("Down");
		downButton.setEnabled(isEnabled);
		downButton.addActionListener(this);
		okCancelPanel.add(downButton);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		okCancelPanel.add(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		okCancelPanel.add(cancelButton);
		
		importButton = new JButton("import...");
		importButton.addActionListener(this);
		okCancelPanel.add(importButton);
		
		exportButton = new JButton("export...");
		exportButton.addActionListener(this);
		okCancelPanel.add(exportButton);

		getContentPane().add(okCancelPanel, BorderLayout.SOUTH);

		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setSelectionForeground(Color.BLACK);

		table.setBackground(new Color(0xf6, 0xf6, 0xf6));
		JTableHeader treeTableHeader = table.getTableHeader();
		treeTableHeader.setBackground(new Color(0xe1, 0xe4, 0xe6));
		table.setEnabled(isEnabled);
		selectFirstRow();
		pack();

		new TableCellListener(table, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				broadcastAction(e);
			}
		});
	}

	private void selectFirstRow() {
		if (table.getRowCount() > 0) {
			table.getSelectionModel().setSelectionInterval(0, 0);
			currentSelectedRow = 0;
		}
	}

	private void selectRow(int row) {
		if (table.getRowCount() >= row && row >= 0) {
			table.getSelectionModel().setSelectionInterval(row, row);
			currentSelectedRow = row;
		}
	}

	private void selectLastRow() {
		int lastRowIndex = table.getRowCount() - 1;
		if (lastRowIndex >= 0) {
			table.getSelectionModel().setSelectionInterval(lastRowIndex, lastRowIndex);
			currentSelectedRow = lastRowIndex;
		}
	}

	public boolean showAndWaitForApprove() throws InterruptedException {
		setVisible(true);
		return approved;
	}

	/**
	 * Boradcast to all the listeners that event occured.
	 * 
	 * @param e
	 */
	private void broadcastAction(ActionEvent e) {
		for (ParameterProviderListener listener : listenersList) {
			listener.actionPerformed(this, e);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		broadcastAction(e);
		gce.stopCellEditing();
		if (okButton.equals(e.getSource())) {
			approved = true;
			dispose();
		} else if (cancelButton.equals(e.getSource())) {
			approved = false;
			dispose();
		} else if (removeButton.equals(e.getSource())) {
			if (currentSelectedRow != -1) {
				mapValues.remove(currentSelectedRow);
				model.fireTableRowsDeleted(currentSelectedRow, currentSelectedRow);
				selectFirstRow();
			}
		} else if (duplicateButton.equals(e.getSource())) {
			if (currentSelectedRow != -1) {
			mapValues.add(new LinkedHashMap<String, String>(mapValues.get(currentSelectedRow)));
				int lastRowIndex = mapValues.size() - 1;
				model.fireTableRowsInserted(lastRowIndex, lastRowIndex);
				selectLastRow();
			}
		} else if (addButton.equals(e.getSource())) {
			mapValues.add(new LinkedHashMap<String, String>(referanceMap));
			int lastRowIndex = mapValues.size() - 1;
			model.fireTableRowsInserted(lastRowIndex, lastRowIndex);
			selectLastRow();
		} else if (upButton.equals(e.getSource())) {
			mapValues.add(currentSelectedRow - 1, mapValues.remove(currentSelectedRow));
			int previosSelection = currentSelectedRow;
			selectRow(previosSelection - 1);
		} else if (downButton.equals(e.getSource())) {
			mapValues.add(currentSelectedRow + 1, mapValues.remove(currentSelectedRow));
			int previosSelection = currentSelectedRow;
			selectRow(previosSelection + 1);
		} else if (importButton.equals(e.getSource())) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("Select a CSV file to open");
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
			try {
				chooser.setCurrentDirectory(new File(ResourcesUtils.retrieveFileFromResourcesFolder("")));
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			int option = chooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				//select delimiter
				JComboBox<EnumDelimiter> list = new JComboBox<>(EnumDelimiter.values());
		        CsvSelectDialog dialog = new CsvSelectDialog("Please select required delimiter: ", list);
		        dialog.setOnOk(s -> selectedDelimiter = (EnumDelimiter) dialog.getSelectedItem());
		        dialog.show();
				
				mapValues.clear();
				if (table.getRowCount() > 0) {
					model.fireTableRowsDeleted(0, table.getRowCount() - 1);
				}
				File file = chooser.getSelectedFile();
				
				try {
			        
			        FileReader filereader = new FileReader(file);
			        CSVParser parser = new CSVParserBuilder().withSeparator(selectedDelimiter.delimiter).build();
			  
			        CSVReader csvReader = new CSVReaderBuilder(filereader)
			        						  .withCSVParser(parser)
			                                  .withSkipLines(0)
			                                  .build();
			        List<String[]> allData = csvReader.readAll();
			  
			        List<String> headers = Arrays.asList(allData.get(0));
			        boolean skip = true;
			        for (String[] row : allData) {
			        	if (skip) {
			        		skip = false;
			        		continue;
			        	}
			        	LinkedHashMap<String, String> lineMap = new LinkedHashMap<>(referanceMap);
			        	int index = 0;
			            for (String cell : row) {
			            	if (headers.size() > index) {
			            		lineMap.put(headers.get(index++).replace("ï»¿", ""), cell);
			            	}
			            }
			            mapValues.add(lineMap);
			        }
			        
			        csvReader.close();
			        filereader.close();
			    }
			    catch (Exception e1) {
			        e1.printStackTrace();
			    }
				model.fireTableRowsInserted(0, mapValues.size() - 1);
				selectLastRow();
			}
		} else if (exportButton.equals(e.getSource())) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("Specify a file to save");
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
			try {
				chooser.setCurrentDirectory(new File(ResourcesUtils.retrieveFileFromResourcesFolder("")));
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			int option = chooser.showSaveDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				//select delimiter
				JComboBox<EnumDelimiter> list = new JComboBox<>(EnumDelimiter.values());
		        CsvSelectDialog dialog = new CsvSelectDialog("Please select required delimiter: ", list);
		        dialog.setOnOk(s -> selectedDelimiter = (EnumDelimiter) dialog.getSelectedItem());
		        dialog.show();
				
				File file = chooser.getSelectedFile();
				String filePath = file.getAbsolutePath();
				if (!filePath.endsWith(".csv")) {
					file = new File(filePath + ".csv");
				}
				
				try {
					FileWriter outputfile = new FileWriter(file);
					
					CSVWriter writer = new CSVWriter(outputfile, selectedDelimiter.delimiter,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            '\\', // Set backslash ('\') as the escape character
                            CSVWriter.DEFAULT_LINE_END);
					
					 List<String[]> data = new ArrayList<String[]>();
					 
					 List<String> headersList = new ArrayList<>();
					 for (int i=0; i < model.getColumnCount(); i++) {
						 headersList.add(model.getColumnName(i));
					 }
					 
					 data.add(headersList.toArray(new String[headersList.size()]));
					 for (int row=0; row < model.getRowCount(); row++) {
						 List<String> rowList = new ArrayList<>();
						 for (int col = 0; col < model.getColumnCount(); col++) {
							rowList.add((String) model.getValueAt(row, col)); 
						 }
						 data.add(rowList.toArray(new String[rowList.size()]));
					 }
					 
					 writer.writeAll(data);
					writer.close();
					outputfile.close();
					JFrame frame = new JFrame();  
				    JOptionPane.showMessageDialog(frame,"Operation Successful.");
				} catch (IOException e1) {
					e1.printStackTrace();
					JFrame frame = new JFrame();  
				    JOptionPane.showMessageDialog(frame, "Operation Error. Check the console for more information...","Alert",JOptionPane.WARNING_MESSAGE);
				}
				
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		for (ParameterProviderListener listener : listenersList) {
			listener.actionPerformed(this, e);
		}
		if (e != null) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			currentSelectedRow = lsm.getLeadSelectionIndex();
		}
		updateButtonStatus();
	}

	public void addListener(ParameterProviderListener listener) {
		if (null == listenersList) {
			listenersList = new ArrayList<ParameterProviderListener>();
		}
		listenersList.add(listener);
	}

	private void updateButtonStatus() {
		if (currentSelectedRow == -1) {
			removeButton.setEnabled(false);
		} else {
			removeButton.setEnabled(isEnabled);
		}
		if (currentSelectedRow == 0 || currentSelectedRow == -1) {
			upButton.setEnabled(false);
		} else {
			upButton.setEnabled(isEnabled);
		}
		if (currentSelectedRow >= (mapValues.size() - 1) || currentSelectedRow == -1) {
			downButton.setEnabled(false);
		} else {
			downButton.setEnabled(isEnabled);
		}
	}

	public void setListeners(List<ParameterProviderListener> listenersList) {
		this.listenersList = listenersList;

	}

	public JTable getTable() {
		return table;
	}

}

