/*
 * Created on Dec 16, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scenario.PresentationDefinitions;
import jsystem.framework.scenario.PresentationDefinitions.ParametersOrder;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioParameter;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.ParameterListener;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.teststable.TestsTableController;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;

/**
 * @author guy.arieli
 * 
 */
public class ParametersPanel extends JPanel implements FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1819353790329185280L;

	/**
	 * the tab that contain all the params sections tabs
	 */
	JTabbedPane paramsTab;

	/**
	 * HashMap that will classify the sections, will have section name as a key
	 * and ParamsTableModel as a value.
	 */
	HashMap<String, ParamsTableModel> paramsBySections;

	JButton sortButton;

	public JCheckBox recursiveReference, recursiveRegulerParameter;

	public boolean resetDirty = true;

	/**
	 * The button is public in order to allow access to it from
	 * TestInformationTab
	 */
	public JButton applyJTestContainer;

	ParameterListener listener = null;

	private HashMap<String, RowEditor> tablesRowEditors;

	private HashMap<String, RowRenderer> tablesRowRedenders;

	private Vector<Component> components;

	private HashMap<Parameter, Component> paramsComponents;

	private SortOptionsPanel sortPanel;

	private boolean isJTestContainer;

	String sectionOrder = null;

	int sortSection;

	int sortHeader;

	PresentationDefinitions.ParametersOrder parametersOrder;

	int activeTab;

	double[] headerRatio = new double[] { 0.1, 0.25, 0.05, 0.2 };

	private ParametersSorter sorter;

	private TestsTableController testsTableController;

	// SectionSort
	public static final int SORT_BY_SECTION_AB = 0;

	public static final int SORT_BY_SECTION_STRING = 2;

	// HeaderSort
	static final int SORT_BY_HEADER_NAME = 0;

	static final int SORT_BY_HEADER_DESCRIPTION = 1;

	static final int SORT_BY_HEADER_TYPE = 2;

	static final int SORT_BY_HEADER_VALUE = 3;

	static final int ASCENDING = 0;

	static final int DECENDING = 1;

	// APPLIED - TestsTableController testsTableController - Needed to determine
	// if the test is inside a scenario that
	// is editable only.
	public ParametersPanel(ParameterListener listener,
			TestsTableController testsTableController) {
		this.testsTableController = testsTableController;
		this.listener = listener;
		cleanAll();
		init();
	}

	public void reset() {
		paramsTab.removeAll();
	}

	public void init() {

		paramsTab = SwingUtils.getJTabbedPaneWithBgImage(
				ImageCenter.getInstance().getImage(
						ImageCenter.ICON_TABBES_TOOLBAR_BG),
				ImageCenter.getInstance().getImage(
						ImageCenter.ICON_TABBES_TOOLBAR_BG));

		setLayout(new BorderLayout());

		add(paramsTab, BorderLayout.CENTER);

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());

		recursiveRegulerParameter = new JCheckBox(JsystemMapping.getInstance()
				.getRecursiveRegularCheckBox());
		recursiveRegulerParameter.setSelected(false);
		recursiveRegulerParameter.addActionListener((ActionListener) listener);
		p.add(recursiveRegulerParameter);

		recursiveReference = new JCheckBox(JsystemMapping.getInstance()
				.getRecursiveReferenceCheckBox());
		recursiveReference.setSelected(false);
		recursiveReference.addActionListener((ActionListener) listener);
		p.add(recursiveReference);

		sortPanel = new SortOptionsPanel(this);
		sortButton = new JButton("Sort Sections");
		sortButton.setSize(80, 20);
		sortButton.setToolTipText("Sort Sections");
		sortButton.addActionListener(sortPanel);
		p.add(sortButton);

		applyJTestContainer = new JButton("Apply for Scenario");
		applyJTestContainer.setToolTipText("Apply for Scenario root tests");
		applyJTestContainer.addActionListener((ActionListener) listener);

		/**
		 * at start when no selection has been made the applyJTestContainer
		 * button is not enabled
		 */
		applyJTestContainer.setEnabled(false);

		p.add(applyJTestContainer);

		add(p, BorderLayout.SOUTH);
	}

	/**
	 * reset all sorting parameters
	 */
	public void cleanAll() {
		activeTab = 0;
		sortSection = SORT_BY_SECTION_AB;
		sortHeader = SORT_BY_HEADER_NAME;

		parametersOrder = PresentationDefinitions.ParametersOrder.defaultOrder;
		try {
			String value = JSystemProperties.getInstance().getPreference(
					FrameworkOptions.PARAMETERS_ORDER_DEFAULT);
			if (value != null) {
				parametersOrder = ParametersOrder.valueOf(value);
			}

		} catch (Exception e) {
		}

		sectionOrder = "";
	}

	public void stopEditing() {
		boolean hasDirty = false;
		Object objectValue = "";
		Set<Parameter> params = paramsComponents.keySet();
		if ("true".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT))) 
		{
			for (Parameter currentParameter : params) {
				Component currentComponent = paramsComponents.get(currentParameter);
				if (currentComponent == null) {
					continue;
				}
				try {

					if (currentComponent instanceof JTextField) {
						objectValue = ((JTextField) currentComponent).getText();
					} else if (currentComponent instanceof JComboBox) {
						objectValue = ((JComboBox) currentComponent)
								.getSelectedItem();
					} else if (currentComponent instanceof ParametersTableFileChooser) {
						objectValue = ((ParametersTableFileChooser) currentComponent)
								.getSelectedFile();
					} else if (currentComponent instanceof ParametersTableDateTimeChooser) {
						objectValue = ((ParametersTableDateTimeChooser) currentComponent)
								.getDate();
					} else if (currentComponent instanceof ParametersTableHostChooser) {
						objectValue = ((ParametersTableHostChooser) currentComponent)
								.getSelectedHosts();
					} else if (currentComponent instanceof ParameterTableUserDefine) {
						objectValue = ((ParameterTableUserDefine) currentComponent)
								.getFieldAsString();
					}
					currentParameter.setValue(objectValue);
				} catch (Exception exception) {
					ErrorPanel.showErrorDialog("Failed to set parameter: "
							+ currentParameter.getName() + " with value "
							+ objectValue, exception, ErrorLevel.Warning);
					buildTabs();
				}

				if (!hasDirty) {
					hasDirty = (currentParameter.isDirty() && (!(currentParameter instanceof ScenarioParameter) || (recursiveReference
							.isSelected())));
				}
			}
		}
		if (components != null) {
			for (Component currentComponent : components) {
				if (currentComponent != null) {
					currentComponent.dispatchEvent(new FocusEvent(
							currentComponent, FocusEvent.FOCUS_LOST));
					currentComponent.repaint();
				}
			}
		}
		for (RowEditor rowEditor : tablesRowEditors.values()) {
			rowEditor.stopCellEditing();
		}

		boolean enable = isScenario() && hasDirty;
		applyJTestContainer.setEnabled(enable);

		requestFocusInWindow();
	}

	/**
	 * Collecting all sections from params and building ParamsTableModel for
	 * every section. first empties the paramTab and then reCreates JTables
	 * 
	 * @param pr
	 *            params list
	 */
	public void buildTabs() {
		saveActiveTab();
		paramsTab.removeAll();
		paramsBySections = new HashMap<String, ParamsTableModel>();
		tablesRowEditors = new HashMap<String, RowEditor>();
		tablesRowRedenders = new HashMap<String, RowRenderer>();
		components = new Vector<Component>();

		// sorting sections
		Parameter[] parameters = sortSectionsAndParameters();

		// APPLIED - Disable all parameters
		Boolean isParametersDisable = isParametersDisable(testsTableController.getCurrentNode());
		if (isParametersDisable) {
			for (Parameter parameter : parameters) {
				parameter.setEditable(false);
			}
		}
		// ScenariosManager.getInstance().getScenario(name);

		// if no parameters disable buttons
		for (Parameter currentParameter : parameters) {
			if (resetDirty) {
				currentParameter.resetDirty();
			}
			if (!currentParameter.isVisible()) {
				continue;
			}
			if (currentParameter.getSection() != null) {
				/**
				 * if there is no such section at the paramsBySections map, open
				 * a new one. if no section for this param, put in "General".
				 */
				if (paramsBySections.get(currentParameter.getSection()) == null) {
					paramsBySections.put(currentParameter.getSection(),
							new ParamsTableModel(this));
				}
				((ParamsTableModel) paramsBySections.get(currentParameter
						.getSection())).addParameter(currentParameter);
			} else {
				paramsBySections.put("General", new ParamsTableModel(this));
			}
		}
		resetDirty = true;

		/**
		 * building the tabs with the tables
		 */
		for (int i = 0; i < parameters.length; i++) {
			if (!parameters[i].isVisible()) {
				continue;
			}
			int tabExisteLoac = -1;
			/**
			 * find out if the tab is already exist.
			 */
			for (int j = 0; j < paramsTab.getTabCount(); j++) {
				if (paramsTab.getTitleAt(j).equals(parameters[i].getSection())) {
					tabExisteLoac = j;
				}
			}
			/**
			 * if exist , remove it in order to build it with the new table.
			 */
			if (tabExisteLoac != -1) {
				paramsTab.remove(tabExisteLoac);
			}
			/**
			 * creating table for this tab.
			 */
			final JTable table = new JTable();
			table.getTableHeader().setReorderingAllowed(false);

			RowEditor rowEditor = new RowEditor(table);
			/**
			 * save the row editor according to the table it belongs to.
			 */
			tablesRowEditors.put(parameters[i].getSection(), rowEditor);

			ParamsTableModel model = (ParamsTableModel) paramsBySections
					.get(parameters[i].getSection());
			model.setTableHeader(table.getTableHeader());
			table.setModel(model);

			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			final Dimension screenSizeDimension = Toolkit.getDefaultToolkit()
					.getScreenSize();

			table.getColumnModel()
			.getColumn(0)
			.setPreferredWidth(
					(int) (screenSizeDimension.getWidth() * headerRatio[0]));
			table.getColumnModel()
			.getColumn(1)
			.setPreferredWidth(
					(int) (screenSizeDimension.getWidth() * headerRatio[1]));
			table.getColumnModel()
			.getColumn(2)
			.setPreferredWidth(
					(int) (screenSizeDimension.getWidth() * headerRatio[2]));
			table.getColumnModel()
			.getColumn(3)
			.setPreferredWidth(
					(int) (screenSizeDimension.getWidth() * headerRatio[3]));

			table.getColumnModel().addColumnModelListener(
					new TableColumnModelListener() {
						public void columnAdded(TableColumnModelEvent e) {
						}

						public void columnMoved(TableColumnModelEvent e) {
						}

						public void columnRemoved(TableColumnModelEvent e) {
						}

						public void columnMarginChanged(ChangeEvent e) {
							headerRatio[0] = table.getColumnModel()
									.getColumn(0).getWidth()
									/ screenSizeDimension.getWidth();
							headerRatio[1] = table.getColumnModel()
									.getColumn(1).getWidth()
									/ screenSizeDimension.getWidth();
							headerRatio[2] = table.getColumnModel()
									.getColumn(2).getWidth()
									/ screenSizeDimension.getWidth();
							headerRatio[3] = table.getColumnModel()
									.getColumn(3).getWidth()
									/ screenSizeDimension.getWidth();

						}

						public void columnSelectionChanged(ListSelectionEvent e) {
							// TODO Auto-generated method stub

						}

					});
			table.getColumn("Value").setCellEditor(rowEditor);
			RowRenderer rowRenderer = new RowRenderer();
			tablesRowRedenders.put(parameters[i].getSection(), rowRenderer);

			table.getColumn("Value").setCellRenderer(new ParamsTableRenderer());
			table.getColumn("Value").setHeaderRenderer(
					new ParamTableHeaderRendrer());
			table.getColumn("Type").setCellRenderer(new ParamsTableRenderer());
			table.getColumn("Type").setHeaderRenderer(
					new ParamTableHeaderRendrer());
			table.getColumn("Description").setCellRenderer(
					new ParamsTableRenderer());
			table.getColumn("Description").setHeaderRenderer(
					new ParamTableHeaderRendrer());
			table.getColumn("Name").setCellRenderer(new ParamsTableRenderer());
			table.getColumn("Name").setHeaderRenderer(
					new ParamTableHeaderRendrer());
			table.setRowHeight(20);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.getViewport().setBackground(new Color(0xf6, 0xf6, 0xf6));
			if (parameters[i].getSection().equals("General")
					&& sectionOrder != null
					&& sectionOrder.indexOf("General") == -1) {
				paramsTab.insertTab("General", null, scrollPane,
						"General Parameters", 0);
			} else {
				paramsTab.add(parameters[i].getSection(), scrollPane);
			}

		}

		add(paramsTab, BorderLayout.CENTER);
		setActiveTab(activeTab);
		setParametersValue();
		recursiveReference.setEnabled(isJTestContainer);
		recursiveRegulerParameter.setEnabled(isJTestContainer);
	}

	// APPLIED - Decide whether or not the parameters are disabled, this method got public in order to help the JRegression
	/**
	 * This methods deals only with ScenarioTreeNode that are either RunnerTest or Scenario.
	 * If the ScenarioTreeNode is a RunnerTest it check whether or not its Scenario or one of its Scenario parent can 
	 * be edit from the current view showed in the TestsTableController
	 * If the ScenarioTreeNode is a Scenario it check whether it or one of its parents (excluding the root) can be edit from the current view
	 * showed in the TestsTableController
	 */
	public Boolean isParametersDisable(ScenarioTreeNode currentNode) {
		//Whenever scenario is running, we are not suppoer to change test parameters.
		if (testsTableController.isRunning()){
			return true;
		}
		JTest jTest = currentNode.getTest();
		if (!(jTest instanceof Scenario)) {
			return !isScenarioEditable(currentNode.getTestsScenario());
		}
		if (jTest instanceof Scenario){
			Scenario scenario = (Scenario) jTest;
			return !isScenarioEditable(scenario);
		}
		return false;
	}

	/**
	 * Return false if a Scenario cannot be edit from the current Tree showed in the TestsTableController
	 * Return true if a Scenario can be edit from the current Tree showed in the TestsTableController.
	 * Algorithm:
	 * Check whether the Scenario or one of its parent (excluding the root) is set to be "edit only locally"
	 * In other words if its property RunningProperties.EDIT_LOCAL_ONLY is set to true.
	 * 
	 * Recursive:
	 * if (scenario == root) return true
	 * if (scenario can be edit only locally) return false
	 * return isScenarioEditable(scenario.parent)
	 * 
	 * @param scenario
	 * @return
	 */
	private Boolean isScenarioEditable(Scenario scenario){
		if(scenario.isRoot()){
			return true;
		}
		if(scenario.isEditLocalOnly()){
			return false;
		}
		return isScenarioEditable(scenario.getParentScenario());
	}

	/**
	 * sorting the Parameters array by the relevant method
	 * 
	 * @param pr
	 *            the parameters array
	 * @return a sorted array
	 */
	private Parameter[] sortSectionsAndParameters() {
		return sorter.sortParameters(sortSection, sortHeader, parametersOrder,
				sectionOrder);
	}

	/**
	 * sets a specific parameter value
	 * 
	 * @param tabName
	 *            name of the section
	 * @param parametrName
	 *            name of parameter
	 * @param value
	 *            new value
	 */
	public void setParameterValue(String tabName, String parametrName,
			String value) {
		JTable table;
		for (int i = 0; i < paramsTab.getComponentCount(); i++) {

			if (paramsTab.getTitleAt(i).equals(tabName)) {

				if (paramsTab.getComponentAt(i) instanceof JScrollPane) {
					JScrollPane pane = (JScrollPane) paramsTab
							.getComponentAt(i);
					table = (JTable) pane.getViewport().getView();
					ParamsTableModel model = (ParamsTableModel) table
							.getModel();
					for (int j = 0; j < model.getRowCount(); j++) {
						String paramName = (String) model.getValueAt(i, 0);
						if (paramName.equals(parametrName)) {
							model.setValueAt(value, i, 3);
						}
					}
				}
			}
		}
		stopEditing();
	}

	/**
	 * create a JTabbed with the given parameters builds the tabs
	 * 
	 * @param pr
	 *            a Parameters array
	 */
	private void setParameters(Parameter[] pr) {
		/*
		 * Diable the apply and sort buttons if no params available.
		 */
		if (pr == null || pr.length == 0) {
			sortButton.setEnabled(false);
		} else {
			sortButton.setEnabled(true);
		}
		sorter = new ParametersSorter(pr);
		buildTabs();
	}

	/**
	 * create a JTabbed with the given parameters builds the tabs
	 * 
	 * @param parameters
	 *            a Parameters array
	 * @param scenarioParameters
	 *            scenario parameters or null if there aren't any
	 */
	public void setParameters(Parameter[] parameters,
			ScenarioParameter[] scenarioParameters,
			DistributedExecutionParameter[] distributedExecutionParameters) {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		parameters = (parameters == null) ? new Parameter[0] : parameters;
		scenarioParameters = (scenarioParameters == null) ? new ScenarioParameter[0]
				: scenarioParameters;
		distributedExecutionParameters = (distributedExecutionParameters == null) ? new DistributedExecutionParameter[0]
				: distributedExecutionParameters;

		for (Parameter currentParameter : parameters) {
			params.add(currentParameter);
		}
		for (ScenarioParameter currentScenarioParameter : scenarioParameters) {
			params.add(currentScenarioParameter);
		}
		for (DistributedExecutionParameter currentDistributedExecutionParameter : distributedExecutionParameters) {
			params.add(currentDistributedExecutionParameter);
		}

		setParameters(params.toArray(new Parameter[0]));
	}

	/**
	 * setting all parameters value including comboBoxes
	 * 
	 */
	private void setParametersValue() {
		paramsComponents = new HashMap<Parameter, Component>();
		for (int sectionIndex = 0; sectionIndex < paramsBySections.size(); sectionIndex++) {
			ArrayList<?> params = ((ParamsTableModel) paramsBySections
					.get(paramsBySections.keySet().toArray()[sectionIndex]))
					.getParams();
			for (int parameterIndex = 0; parameterIndex < params.size(); parameterIndex++) {
				Parameter currentParameter = (Parameter) params
						.get(parameterIndex);
				if (!currentParameter.isVisible()) {
					continue;
				}
				RowRenderer rowRenderer = (RowRenderer) tablesRowRedenders
						.get(currentParameter.getSection());
				RowEditor rowEditor = (RowEditor) tablesRowEditors
						.get(currentParameter.getSection());
				if (currentParameter.getProvider() != null) {
					ParameterTableUserDefine userDefine = null;
					try {
						userDefine = new ParameterTableUserDefine(
								currentParameter.getProvider(),
								currentParameter.getParamClass(),
								currentParameter);
					} catch (Exception exception) {
						ErrorPanel.showErrorDialog(
								"Fail to create user defined parameters",
								exception, ErrorLevel.Warning);
						continue;
					}
					components.addElement(userDefine);
					paramsComponents.put(currentParameter, userDefine);
					UserDefinedCellEditor userDefinedEditor = new UserDefinedCellEditor(
							userDefine);
					rowEditor.setEditorAt(parameterIndex, userDefinedEditor);
				} else if (currentParameter instanceof DistributedExecutionParameter
						&& currentParameter.getName().equals(
								DistributedExecutionHelper.AGENTS)) {
					ParametersTableHostChooser chooser = new ParametersTableHostChooser(
							(String) currentParameter.getValue());
					components.addElement(chooser);
					paramsComponents.put(currentParameter, chooser);
					HostsChooserCellEditor hostsChooserEditor = new HostsChooserCellEditor(
							chooser);
					rowEditor.setEditorAt(parameterIndex, hostsChooserEditor);
				} else if (currentParameter.isAsOptions()) {
					JComboBox comboBox = new JComboBox(
							currentParameter.getOptions());
					comboBox.setEditable(false);
					components.addElement(comboBox);
					paramsComponents.put(currentParameter, comboBox);
					ComboBoxRenderer cbr = new ComboBoxRenderer(comboBox);
					comboBox.setSelectedItem(currentParameter.getValue());
					rowRenderer.add(parameterIndex, cbr);
					rowEditor.setEditorAt(parameterIndex,
							new DefaultCellEditor(comboBox));
				} else if ((currentParameter.getType() == ParameterType.STRING_ARRAY)
						&& (currentParameter.getValue() instanceof String[])) {
					String temp = StringUtils.objectArrayToString(
							CommonResources.DELIMITER,
							(Object[]) currentParameter.getValue());
					currentParameter.setValue(temp);
				} else {
					JTextField field = null;
					Object parameterValue = currentParameter.getValue();
					String parameterStringValue = null;
					if (parameterValue != null) {
						parameterStringValue = parameterValue.toString();
					}
					switch (currentParameter.getType()) {
					case STRING:
					case STRING_ARRAY:
					case INT:
					case LONG:
					case FLOAT:
					case DOUBLE:
					case SHORT:
					case REFERENCE:
						field = new JTextField();
						field.setText(parameterStringValue);
						DefaultCellEditor dce1 = new DefaultCellEditor(field);
						dce1.setClickCountToStart(1);
						rowEditor.setEditorAt(parameterIndex, dce1);
						break;

					case BOOLEAN:
						/**
						 * add combobox for boolean type
						 */
						JComboBox comboBox = new JComboBox(new String[] {
								"true", "false" });
						components.addElement(comboBox);
						paramsComponents.put(currentParameter, comboBox);
						if (currentParameter.getValue().toString()
								.equals("false")) {
							comboBox.setSelectedIndex(1);
						} else {
							comboBox.setSelectedIndex(0);
						}

						ComboBoxRenderer comboBoxRender = new ComboBoxRenderer(
								comboBox);
						rowRenderer.add(parameterIndex, comboBoxRender);
						rowEditor.setEditorAt(parameterIndex,
								new DefaultCellEditor(comboBox));

						break;
					case FILE:
						if (!isScenarioEditable(testsTableController.getCurrentNode().getTestsScenario())){
							continue;
						}
						ParametersTableFileChooser chooser = new ParametersTableFileChooser(
								(String) currentParameter.getValue());
						components.addElement(chooser);
						paramsComponents.put(currentParameter, chooser);
						FileChooserCellEditor fileChooserEditor = new FileChooserCellEditor(
								chooser);
						rowEditor
						.setEditorAt(parameterIndex, fileChooserEditor);
						break;
					case DATE:
						if (!isScenarioEditable(testsTableController.getCurrentNode().getTestsScenario())){
							continue;
						}
						ParametersTableDateTimeChooser dateSelector = new ParametersTableDateTimeChooser(
								(String) currentParameter.getValue());
						components.addElement(dateSelector);
						paramsComponents.put(currentParameter, dateSelector);
						DateCellEditor dateCellEditor = new DateCellEditor(
								dateSelector);
						dateSelector.addParameterChangedListener(dateCellEditor);
						rowEditor.setEditorAt(parameterIndex, dateCellEditor);
						break;
					default:
						// not supported
					}
					if (field != null) {
						components.addElement(field);
						paramsComponents.put(currentParameter, field);
					}

				}
				if (!currentParameter.isEditable()) {
					components.get(components.size() - 1).setEnabled(false);
				}
			}
		}
		for (int componentIndex = 0; componentIndex < components.size(); componentIndex++) {
			((Component) components.elementAt(componentIndex))
			.addFocusListener(this);
		}
	}

	public HashMap<String, ParamsTableModel> getParamsBySections() {
		return paramsBySections;
	}

	/**
	 * set the user-defined string of sections order recieve a String array and
	 * convert it to String with spaces
	 * 
	 * @param order
	 *            the string of the order
	 */
	public void setSectionOrder(String[] order) {
		sectionOrder = "";
		if (order != null) {
			for (int i = 0; i < order.length; i++)
				sectionOrder += " " + order[i];
			// setting the user-defined string to be default
			sortSection = SORT_BY_SECTION_STRING;
		}
		sortPanel.createPopUp();
	}

	/**
	 * the sectionOrder is the user-defined string array in the test file if non
	 * exists return an empty String;
	 * 
	 * @return the string of ordered sections
	 */
	public String getSectionOrder() {
		if (sectionOrder == null) {
			return "";
		}
		return sectionOrder;
	}

	/**
	 * signal that a new sorting was defined if the new is diffrent from the old
	 * - resort
	 * 
	 * @param sortSection
	 */
	public void sectionChanged(int sortSection) {
		if (this.sortSection != sortSection) {
			sort(sortSection, sortHeader);
		}
	}

	/**
	 * signal that a header has changed and sorting should be done if it's the
	 * same header switch between ascending and descending order
	 * 
	 * @param sortHeader
	 *            the header to sort by
	 */
	public void headerChanged(int sortHeader) {
		if (this.sortHeader == sortHeader) {
			if (parametersOrder == PresentationDefinitions.ParametersOrder.ascending) {
				parametersOrder = PresentationDefinitions.ParametersOrder.descending;
			} else if (parametersOrder == PresentationDefinitions.ParametersOrder.descending) {
				parametersOrder = PresentationDefinitions.ParametersOrder.defaultOrder;
			} else {
				parametersOrder = PresentationDefinitions.ParametersOrder.ascending;
			}
		} else {
			parametersOrder = PresentationDefinitions.ParametersOrder.ascending;
		}

		sort(sortSection, sortHeader);
	}

	/**
	 * sort the JTabbedPane according to section and header
	 * 
	 * @param sortSection
	 *            SORT_BY_SECTION_AB / SORT_BY_SECTION_STRING /
	 * @param sortHeader
	 *            SORT_BY_HEADER_DESCRIPTION / SORT_BY_HEADER_NAME /
	 *            SORT_BY_HEADER_TYPE / SORT_BY_HEADER_VALUE;
	 */
	public void sort(int sortSection, int sortHeader) {
		this.sortSection = sortSection;
		this.sortHeader = sortHeader;
		buildTabs();
	}

	/**
	 * save current activeTab - for test re-selection
	 * 
	 */
	private void saveActiveTab() {
		this.activeTab = paramsTab.getSelectedIndex();
	}

	/**
	 * sets the active Section to be the activeTab
	 * 
	 * @param activeTab
	 *            Section number (0 is the first)
	 */
	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
		if (activeTab != -1 && paramsTab.getComponentCount() > activeTab)
			paramsTab.setSelectedIndex(activeTab);
	}

	/**
	 * check if the last active section name is also active in this test if yes
	 * - changes to active. if not - stays with test's default
	 * 
	 * @param section
	 */
	public void setActiveTab(String section) {
		int n = paramsTab.getComponentCount();
		for (int i = 0; i < n; i++) {
			if (paramsTab.getTitleAt(i).equals(section)) {
				this.activeTab = i;
				if (activeTab != -1
						&& paramsTab.getComponentCount() > activeTab)
					paramsTab.setSelectedIndex(activeTab);

			}
		}
	}

	/**
	 * get the name of the current active Section
	 * 
	 * @return a string with the name of the active Section
	 */
	public String getActiveSectionName() {
		int tab = paramsTab.getSelectedIndex();
		if (tab > -1)
			return paramsTab.getTitleAt(paramsTab.getSelectedIndex());
		return "";
	}

	/**
	 * updating the panels's values of sorting parameters from a given
	 * PresentationDefinitions object
	 * 
	 * @param sort
	 *            the PresentationDefinitions of the JTest
	 */
	public void changeVals(PresentationDefinitions sort) {
		this.sortSection = sort.getSortSection();
		this.sortHeader = sort.getSortHeader();
		this.parametersOrder = sort.getParamOrder();
		this.activeTab = sort.getActiveTab();
		this.headerRatio = sort.getHeadersRatio();
		this.headerRatio = new double[sort.getHeadersRatio().length];
		System.arraycopy(sort.getHeadersRatio(), 0, this.headerRatio, 0,
				sort.getHeadersRatio().length);
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		if (e.getOppositeComponent() != null
				&& e.getOppositeComponent().getName() != null
				&& e.getOppositeComponent()
				.getName()
				.equals(ParametersTableFileChooser.SELECT_FILE_BUTTON_NAME)) {
			return;
		}

		if (e.getOppositeComponent() != null
				&& e.getOppositeComponent().getName() != null
				&& e.getOppositeComponent()
				.getName()
				.equals(ParametersTableHostChooser.SELECT_HOST_BUTTON_NAME)) {
			return;
		}

		if (e.getOppositeComponent() != null
				&& e.getOppositeComponent().getName() != null
				&& e.getOppositeComponent()
				.getName()
				.equals(ParametersTableDateTimeChooser.SELECT_DATE_BUTTON_NAME)) {
			return;
		}
		if (e.getOppositeComponent() != null
				&& e.getOppositeComponent().getName() != null
				&& e.getOppositeComponent()
				.getName()
				.equals(ParameterTableUserDefine.USER_DEFINED_EDITOR_NAME)) {
			return;
		}
		if (listener != null) {
			listener.parameterChanged(false);
		}
	}

	public void setIsJTestContainer(boolean isJTestContainer) {
		this.isJTestContainer = isJTestContainer;
	}

	private boolean isScenario() {
		return isJTestContainer;
	}

	/**
	 * get both tests and Scenario parameters (if exist)
	 * 
	 * @return
	 */
	public Parameter[] getAllParameters() {
		return sorter.getParameters();
	}

	/**
	 * get only test parameters
	 * 
	 * @return
	 */
	public Parameter[] getTestParameters() {
		Parameter[] parameters = getAllParameters();
		ArrayList<Parameter> retParams = new ArrayList<Parameter>();
		for (Parameter currentParameter : parameters) {
			if (!(currentParameter instanceof ScenarioParameter)) {
				retParams.add(currentParameter);
			}
		}
		return retParams.toArray(new Parameter[0]);
	}

	/**
	 * get only scenario parameters
	 * 
	 * @return
	 */
	public ScenarioParameter[] getScenarioParameters() {
		Parameter[] parameters = getAllParameters();
		ArrayList<Parameter> retParams = new ArrayList<Parameter>();
		for (Parameter currentParameter : parameters) {
			if ((currentParameter instanceof ScenarioParameter)) {
				retParams.add((ScenarioParameter) currentParameter);
			}
		}
		return retParams.toArray(new ScenarioParameter[0]);
	}

	/**
	 * get only scenario parameters
	 * 
	 * @return
	 */
	public DistributedExecutionParameter[] getHostParameters() {
		Parameter[] parameters = getAllParameters();
		ArrayList<DistributedExecutionParameter> retParams = new ArrayList<DistributedExecutionParameter>();
		for (Parameter currentParameter : parameters) {
			if ((currentParameter instanceof DistributedExecutionParameter)) {
				retParams.add((DistributedExecutionParameter) currentParameter);
			}
		}
		return retParams.toArray(new DistributedExecutionParameter[0]);
	}

	public int getActiveTab() {
		return activeTab;
	}

	public double[] getHeaderRatio() {
		return headerRatio;
	}

	public PresentationDefinitions.ParametersOrder getParametersOrder() {
		return parametersOrder;
	}

	public int getSortHeader() {
		return sortHeader;
	}

	public int getSortSection() {
		return sortSection;
	}
}