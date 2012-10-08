/*
  * Created on 28/03/2010, 10:38:21
  *  
  * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
  */
package jsystem.treeui.multiscenario;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import jsystem.treeui.RunnerAdvancedCmdExecuter;
import jsystem.treeui.RunnerCmd;
import jsystem.utils.FileUtils;
import jsystem.utils.XmlUtils;

import org.apache.tools.ant.util.ReaderInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michael Oziransky
 */
@SuppressWarnings("serial")
public class MultiScenarioDialog extends javax.swing.JDialog {
	
	private static Logger log = Logger.getLogger(MultiScenarioDialog.class.getName());
	
	protected RunnerCmd currentCmd = null;
	protected ArrayList<RunnerCmd> commands;
	protected DefaultListModel model = null;
	protected File configFile = null;
	
    /** Creates new form MultiScenariosScedular */
    public MultiScenarioDialog() {
        initComponents();
        scenariosList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectPathTextField.setEditable(false);
        sutFileTextField.setEditable(false);
        scenarioFileTextField.setEditable(false);
        
        nameTextField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				updateModel();
				scenariosList.validate();
				scenariosList.repaint();
				saveButton.setEnabled(true);
			}
			
			public void focusGained(FocusEvent e) {
			}
		});
        
        repetitionsSpinner.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				updateModel();
				saveButton.setEnabled(true);
			}
			
			public void focusGained(FocusEvent e) {
			}
		});
        repetitionsSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateModel();
				saveButton.setEnabled(true);
			}
		});
        scenariosList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = e.getLastIndex();
				if (index < commands.size()) {
					commandSelectionChanged(commands.get(index));
				}
			}
		});
    }
    
    public void updateModel() {
    	if (currentCmd != null) {
    		currentCmd.setAlias(nameTextField.getText());
    		currentCmd.setProjectPath(projectPathTextField.getText());
    		currentCmd.setSutFile(sutFileTextField.getText());
    		currentCmd.setScenarioFile(scenarioFileTextField.getText());
    		currentCmd.setSaveRunProperties(saveRunPropertiesCheckBox.isSelected());
    		currentCmd.setDependOnPrevious(dependOnPreviousCheckBox.isSelected());
    		currentCmd.setFreezeOnFail(freezeOnFailCheckBox.isSelected());
    		currentCmd.setStopSuiteExecution(stopSuiteExecutionCheckBox.isSelected());
    		currentCmd.setStopEntireExecution(stopEntireExecutionCheckBox.isSelected());
    		currentCmd.setRepetition((Integer)repetitionsSpinner.getValue());   		
    	}
    }
    
    public void loadConfiguration(File commandsFile) {
    	configFile = commandsFile;
    	commands = new ArrayList<RunnerCmd>();
		try {
			// Parse the input file
			Document doc = XmlUtils.getDocumentBuilder().parse(
					new ReaderInputStream(
							new FileReader(commandsFile)));
						
			// Get general attributes
			boolean haltOnStop = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("stop"));
			boolean exitOnFinish = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("exit"));
			haltOnStopCheckBox.setSelected(haltOnStop);
			exitOnFinishCheckBox.setSelected(exitOnFinish);
			
			ArrayList<Element> list = 
				XmlUtils.getElementsByTag("command", doc.getDocumentElement());
			
			for (Element e : list) {
				RunnerCmd cmd = new RunnerCmd();
				
				// Set command attributes
				cmd.setRepetition(Integer.parseInt(e.getAttribute("repetitions")));
				cmd.setDependOnPrevious(Boolean.parseBoolean(e.getAttribute("dependOnPrevious")));
				cmd.setSaveRunProperties(Boolean.parseBoolean(e.getAttribute("saveRunProperties")));
				cmd.setFreezeOnFail(Boolean.parseBoolean(e.getAttribute("freezeOnFail")));
				cmd.setStopSuiteExecution(Boolean.parseBoolean(e.getAttribute("stopSuiteExecution")));
				cmd.setStopEntireExecution(Boolean.parseBoolean(e.getAttribute("stopEntireExecution")));
				cmd.setAlias(e.getAttribute("alias"));
				
				// Set main features
				Element projectPath = XmlUtils.getChildElementsByTag("projectPath", e).get(0);
				cmd.setProjectPath(projectPath.getTextContent());
				Element sutFile = XmlUtils.getChildElementsByTag("sutFile", e).get(0);
				cmd.setSutFile(sutFile.getTextContent());
				Element scenarioName = XmlUtils.getChildElementsByTag("scenarioName", e).get(0);
				cmd.setScenarioFile(scenarioName.getTextContent());
				
				// Add to the list of commands
				commands.add(cmd);
			}
		} catch (Exception e) {}

    	model = new DefaultListModel();
    	for(RunnerCmd cmd: commands){
    		model.addElement(cmd);
    	}    	
    	scenariosList.setModel(model);    	
    }
    
    public void commandSelectionChanged(RunnerCmd cmd) {
    	// Save all changed values
    	updateModel();
    	// Move to the new commands
    	currentCmd = cmd;    	
    	// Update UI with new commands
    	updateProperties();
    }
    
    private void updateProperties() {
    	boolean enable = (currentCmd != null);
    	nameTextField.setEnabled(enable);
		nameTextField.setEnabled(enable);
		projectPathTextField.setEnabled(enable);
		sutFileTextField.setEnabled(enable);
		scenarioFileTextField.setEnabled(enable);
		saveRunPropertiesCheckBox.setEnabled(enable);
		freezeOnFailCheckBox.setEnabled(enable);
		stopSuiteExecutionCheckBox.setEnabled(enable);
		stopEntireExecutionCheckBox.setEnabled(enable);
		dependOnPreviousCheckBox.setEnabled(enable);
		repetitionsSpinner.setEnabled(enable);
		projectPathButton.setEnabled(enable);
		sutFileButton.setEnabled(enable);
		scenarioFileButton.setEnabled(enable);
    	if (currentCmd != null) {
    		nameTextField.setText(currentCmd.getAlias());
    		projectPathTextField.setText(currentCmd.getProjectPath());
    		sutFileTextField.setText(currentCmd.getSutFile());
    		scenarioFileTextField.setText(currentCmd.getScenarioFile());
    		saveRunPropertiesCheckBox.setSelected(currentCmd.isSaveRunProperties());
    		freezeOnFailCheckBox.setSelected(currentCmd.isFreezeOnFail());
    		stopSuiteExecutionCheckBox.setSelected(currentCmd.isStopSuiteExecution());
    		stopEntireExecutionCheckBox.setSelected(currentCmd.isStopEntireExecution());
    		dependOnPreviousCheckBox.setSelected(currentCmd.isDependOnPrevious());
    		repetitionsSpinner.setValue(currentCmd.getRepetition());
    	} else {
    		nameTextField.setText("");
    		projectPathTextField.setText("");
    		sutFileTextField.setText("");
    		scenarioFileTextField.setText("");
    		saveRunPropertiesCheckBox.setSelected(false);
    		freezeOnFailCheckBox.setSelected(false);
    		stopSuiteExecutionCheckBox.setSelected(false);
    		stopEntireExecutionCheckBox.setSelected(false);
    		dependOnPreviousCheckBox.setSelected(false);
    		repetitionsSpinner.setValue(1);
    	}
	}
    
	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        nameTextField = new JTextField();
        projectPathTextField = new JTextField();
        projectPathButton = new JButton();
        sutFileTextField = new JTextField();
        sutFileButton = new JButton();
        scenarioFileTextField = new JTextField();
        scenarioFileButton = new JButton();
        saveRunPropertiesCheckBox = new JCheckBox();
        dependOnPreviousCheckBox = new JCheckBox();
        freezeOnFailCheckBox = new JCheckBox();
        stopSuiteExecutionCheckBox = new JCheckBox();
        stopEntireExecutionCheckBox = new JCheckBox();
        jLabel5 = new JLabel();
        repetitionsSpinner = new JSpinner();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        scenariosList = new JList();
        addButton = new JButton();
        removeButton = new JButton();
        jPanel3 = new JPanel();
        saveButton = new JButton();
        runButton = new JButton();
        loadButton = new JButton();
        saveAsButton = new JButton();
        cancelButton = new JButton();
        haltOnStopCheckBox = new JCheckBox();
        exitOnFinishCheckBox = new JCheckBox();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Multiple Scenarios Execution");
        setResizable(false);

        jPanel1.setBorder(BorderFactory.createTitledBorder("Properties"));
        jPanel1.setName("Properties"); // NOI18N

        jLabel1.setText("Name:");

        jLabel2.setText("Project Path:");

        jLabel3.setText("Sut File:");

        jLabel4.setText("Scenario File:");

        projectPathButton.setText("...");
        projectPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectPathButtonActionPerformed(evt);
            }
        });

        sutFileButton.setText("...");
        sutFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sutFileButtonActionPerformed(evt);
            }
        });

        scenarioFileButton.setText("...");
        scenarioFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                scenarioFileButtonActionPerformed(evt);
            }
        });

        saveRunPropertiesCheckBox.setText("Save run properties after this scenario completion");
        saveRunPropertiesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveRunPropertiesCheckBoxActionPerformed(evt);
            }
        });

        dependOnPreviousCheckBox.setText("Depend on previous scenario failure");
        dependOnPreviousCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dependOnPreviousCheckBoxActionPerformed(evt);
            }
        });

        freezeOnFailCheckBox.setText("Freeze entire execution on this scenario failure");
        freezeOnFailCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                freezeOnFailCheckBoxActionPerformed(evt);
            }
        });
        
        stopSuiteExecutionCheckBox.setText("Stop suite execution on this scenario failure");
        stopSuiteExecutionCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	stopSuiteExecutionCheckBoxActionPerformed(evt);
            }
        });
        
        stopEntireExecutionCheckBox.setText("Stop entire execution on this scenario failure");
        stopEntireExecutionCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	stopEntireExecutionCheckBoxActionPerformed(evt);
            }
        });
        
        jLabel5.setText("Repetitions:");
        
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                	.addComponent(stopSuiteExecutionCheckBox)
                	.addComponent(stopEntireExecutionCheckBox)
                    .addComponent(freezeOnFailCheckBox)
                    .addComponent(dependOnPreviousCheckBox)
                    .addComponent(saveRunPropertiesCheckBox)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(sutFileTextField, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(scenarioFileTextField, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(projectPathTextField, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(projectPathButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sutFileButton, 0, 0, Short.MAX_VALUE))
                                    .addComponent(scenarioFileButton)))
                            .addComponent(repetitionsSpinner, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {projectPathButton, scenarioFileButton, sutFileButton});

        jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {nameTextField, projectPathTextField, scenarioFileTextField, sutFileTextField});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(projectPathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectPathButton))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(sutFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(sutFileButton))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(scenarioFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(scenarioFileButton)
                    .addComponent(jLabel4))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(stopSuiteExecutionCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(saveRunPropertiesCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(dependOnPreviousCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(freezeOnFailCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(stopEntireExecutionCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(repetitionsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {projectPathButton, scenarioFileButton, sutFileButton});

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {nameTextField, projectPathTextField, scenarioFileTextField, sutFileTextField});

        jPanel2.setBorder(BorderFactory.createTitledBorder("Scenarios"));

        jScrollPane1.setViewportView(scenariosList);

        addButton.setText("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {addButton, removeButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(SwingConstants.VERTICAL, new Component[] {addButton, removeButton});
        jPanel3.setBorder(BorderFactory.createTitledBorder("Execution"));


        loadButton.setText("Load");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        saveAsButton.setText("Save As");
        saveAsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        runButton.setText("Run");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        haltOnStopCheckBox.setText("Stop entire execution on user abort");
        haltOnStopCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	haltOnStopCheckBoxActionPerformed(evt);
            }
        });

        exitOnFinishCheckBox.setText("Close JRunner on entire execution completion");
        exitOnFinishCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	exitOnFinishCheckBoxActionPerformed(evt);
            }
        });
        
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(244, Short.MAX_VALUE)
                .addComponent(runButton)
                .addPreferredGap(ComponentPlacement.RELATED)                
                .addComponent(loadButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(loadButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(saveAsButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(haltOnStopCheckBox)
                .addContainerGap(200, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitOnFinishCheckBox)
                .addContainerGap(368, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, runButton, saveButton, saveAsButton, loadButton});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(haltOnStopCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(exitOnFinishCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(saveAsButton)
                    .addComponent(loadButton)
                    .addComponent(runButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
    	if (currentCmd != null) {
    		commands.remove(currentCmd);
    		model.removeElement(currentCmd);
//    		scenariosList.getSelectionModel().setLeadSelectionIndex(commands.size() - 1);
    		currentCmd = null;
    		updateProperties();
    		saveButton.setEnabled(true);
    	}
    }//GEN-LAST:event_removeButtonActionPerformed

    private void dependOnPreviousCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_dependOnPreviousCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_dependOnPreviousCheckBoxActionPerformed

    private void projectPathButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_projectPathButtonActionPerformed
    	String projectPath = currentCmd.getProjectPath();
    	if (projectPath == null || projectPath.isEmpty()) {
    		if (commands.size() >= 2) {
        		RunnerCmd lastCmd = commands.get(commands.size()-2);
        		projectPath = lastCmd.getProjectPath();
    		}
    	}
    	JFileChooser fc = new JFileChooser(new File(projectPath));
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	int returnVal = fc.showOpenDialog(this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            currentCmd.setProjectPath(file.getAbsolutePath());
            updateProperties();
        }
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_projectPathButtonActionPerformed

    private void sutFileButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_sutFileButtonActionPerformed
    	String projectPath = currentCmd.getProjectPath();
    	String pathBase = null;
    	if (projectPath == null || projectPath.isEmpty()) {
    		pathBase = System.getProperty("user.dir");
    	} else {
    		pathBase = new File(projectPath, "sut").getAbsolutePath();
    	}
    	JFileChooser fc = new JFileChooser(new File(pathBase));
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "XML";
			}
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return f.getName().toLowerCase().endsWith(".xml");
			}
		});
    	int returnVal = fc.showOpenDialog(this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            currentCmd.setSutFile("sut" + File.separator + file.getName());
            updateProperties();
        }
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_sutFileButtonActionPerformed

    private void scenarioFileButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_scenarioFileButtonActionPerformed
    	String projectPath = currentCmd.getProjectPath();
    	String pathBase = null;
    	if (projectPath == null || projectPath.isEmpty()) {
    		pathBase = System.getProperty("user.dir");
    	} else {
    		pathBase = new File(projectPath, "scenarios").getAbsolutePath();
    	}
    	JFileChooser fc = new JFileChooser(new File(pathBase));
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "XML";
			}
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return f.getName().toLowerCase().endsWith(".xml");
			}
		});
    	int returnVal = fc.showOpenDialog(this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            File scenarioDir = new File(file.getAbsolutePath());
            // Find the relative path to the scenario directory
            while (scenarioDir.getParentFile() != null) {
            	if (scenarioDir.getName().equals("scenarios")) {
            		break;
            	}
                scenarioDir = scenarioDir.getParentFile();
            }
            if (scenarioDir != null) {
            	String scenarioName = file.getAbsolutePath().substring(scenarioDir.getParentFile().getAbsolutePath().length() + 1);
                currentCmd.setScenarioFile(scenarioName);
                updateProperties();
            }
        }
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_scenarioFileButtonActionPerformed

    private void saveRunPropertiesCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveRunPropertiesCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_saveRunPropertiesCheckBoxActionPerformed

    private void freezeOnFailCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_freezeOnFailCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_freezeOnFailCheckBoxActionPerformed
    
    private void stopSuiteExecutionCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_freezeOnFailCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_freezeOnFailCheckBoxActionPerformed    
    
    private void stopEntireExecutionCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_freezeOnFailCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_freezeOnFailCheckBoxActionPerformed    

    private void haltOnStopCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_haltOnStopCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_haltOnStopCheckBoxActionPerformed

    private void exitOnFinishCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:exitOnFinishCheckBoxActionPerformed
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_exitOnFinishCheckBoxActionPerformed    
    
    private void runButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
    	// Save everything
    	saveButtonActionPerformed(evt);
    	// Close the dialog
    	this.dispose();
    	
    	Thread t = new Thread(new Runnable() {
			public void run() {
				// Execute the scenario
		    	RunnerAdvancedCmdExecuter runCmd = new RunnerAdvancedCmdExecuter(configFile.getAbsolutePath());
				runCmd.init();
			}
		});
		t.start();    	
    }//GEN-LAST:event_runButtonActionPerformed


    private void loadButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    	JFileChooser fc = new JFileChooser(configFile);
    	int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loadConfiguration(file);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    
    private void saveAsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    	JFileChooser fc = new JFileChooser(configFile);
    	int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.exists()) {
            	try {
					file.createNewFile();
					configFile = file;
					saveButtonActionPerformed(evt);
				} catch (IOException e) {
					log.log(Level.SEVERE, "Fail to craete a new configuration file", e);
				}
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void saveButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    	updateModel();
    	try {
			Document doc = XmlUtils.getDocumentBuilder().newDocument();
			Element root = doc.createElement("run");
			root.setAttribute("stop", Boolean.toString(haltOnStopCheckBox.isSelected()));
			root.setAttribute("exit", Boolean.toString(exitOnFinishCheckBox.isSelected()));
			doc.appendChild(root);
			for (RunnerCmd cmd : commands) {
				Element command = doc.createElement("command");
				cmd.toElement(command, doc);
				root.appendChild(command);
			}
			FileUtils.saveDocumentToFile(doc, configFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fail to write to run XML file", e);
		}
		// Indicate to the user that the save was done
		saveButton.setEnabled(false);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    	this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    	boolean firstTime = false;
    	RunnerCmd newCmd = new RunnerCmd();
    	newCmd.setAlias("My New Scenario");
    	if (commands.size() == 0) {
    		firstTime = true;
    	}
    	commands.add(newCmd);
    	model.addElement(newCmd);
    	scenariosList.getSelectionModel().setLeadSelectionIndex(commands.size()-1);
    	if (firstTime) {
    		commandSelectionChanged(newCmd);
    	}
    	saveButton.setEnabled(true);
    }//GEN-LAST:event_addButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addButton;
    private JButton cancelButton;
    private JCheckBox dependOnPreviousCheckBox;
    private JCheckBox exitOnFinishCheckBox;
    private JCheckBox freezeOnFailCheckBox;
    private JCheckBox stopSuiteExecutionCheckBox;
    private JCheckBox stopEntireExecutionCheckBox;
    private JCheckBox haltOnStopCheckBox;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JTextField nameTextField;
    private JButton projectPathButton;
    private JTextField projectPathTextField;
    private JButton removeButton;
    private JSpinner repetitionsSpinner;
    private JButton runButton;
    private JButton loadButton;
    private JButton saveAsButton;
    private JButton saveButton;
    private JCheckBox saveRunPropertiesCheckBox;
    private JButton scenarioFileButton;
    private JTextField scenarioFileTextField;
    private JList scenariosList;
    private JButton sutFileButton;
    private JTextField sutFileTextField;
    // End of variables declaration//GEN-END:variables

}
