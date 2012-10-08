/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.Reporter;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.ExtentionsFileFilter;
import jsystem.treeui.actionItems.PublishXmlResultAction;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;

import org.w3c.dom.Element;

public class PublisherTreePanel extends JPanel implements ActionListener, TreeSelectionListener {

	private static final long serialVersionUID = 5322978703651238455L;

	private static Logger log = Logger.getLogger(PublisherTreePanel.class.getName());

	private String reportFileName = "reports.0.xml";

	private JTree tree;

	private PublisherTreeModel treeModel;

	private JScrollPane scrollTree;

	private JButton reloadFileButton;

	private JButton loadFile;

	private JComboBox viewTypeCombo;

	private static ElementNode currentNode;

	private JButton deleteButton;

	private JButton saveButton;

	private TestInfoPanel infoPanel;

	public PublisherTreePanel() {
		super(new BorderLayout());

		File logCurrent = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER),
				"current");

		File reportFile = new File(logCurrent, reportFileName);
		tree = new JTree();

		try {

			treeModel = new PublisherTreeModel(reportFile);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to create tree model", e);
		}

		// tree configuration
		tree.setModel(treeModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setCellRenderer(new ElementRenderer());
		tree.addTreeSelectionListener(this);

		ToolTipManager.sharedInstance().registerComponent(tree);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerSize(4);
		split.setDividerLocation(40);
		scrollTree = new JScrollPane(tree);

		infoPanel = new TestInfoPanel(this);

		JSplitPane messageSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		messageSplit.setLeftComponent(scrollTree);
		messageSplit.setRightComponent(infoPanel);
		messageSplit.setDividerSize(4);
		messageSplit.setDividerLocation(200);

		/**
		 * changing the size of Divider acording to screen size
		 * 
		 * @todo this is a workaround.
		 * @todo we should fix the properties pane component layout.
		 */
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		if (dim.getWidth() > 1024) {
			messageSplit.setDividerLocation((int) (dim.getWidth() / 3.5));
		} else {
			messageSplit.setDividerLocation(200);
		}

		split.setLeftComponent(createToolBar());
		split.setRightComponent(messageSplit);

		add(split, BorderLayout.CENTER);
	}

	/**
	 * Creates the tool bar
	 */
	private JToolBar createToolBar() {

		JToolBar toolBar = SwingUtils.getJToolBarWithBgImage("publisher", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_TOP_TOOLBAR_BG));

		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		reloadFileButton = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_REFRESH_REPORTS));
		reloadFileButton.setToolTipText(JsystemMapping.getInstance().getRefreshPublishButton());
		reloadFileButton.addActionListener(this);

		loadFile = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_IMPORT));
		loadFile.setToolTipText("Load File From the File System");
		loadFile.addActionListener(this);

		viewTypeCombo = new JComboBox(PublisherTreeModel.VIEW_TYPES);
		viewTypeCombo.setToolTipText("Select View Type");
		viewTypeCombo.setSelectedIndex(0);
		viewTypeCombo.addActionListener(this);
		viewTypeCombo.setOpaque(false);

		deleteButton = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_DELETE));
		deleteButton.setToolTipText("Delete Item");
		deleteButton.addActionListener(this);
		deleteButton.setEnabled(false);

		saveButton = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_SAVE));
		saveButton.setToolTipText("Save to Xml");
		saveButton.addActionListener(this);

		toolBar.add(reloadFileButton);
		toolBar.add(loadFile);
		toolBar.addSeparator(new Dimension(20, 0));
		toolBar.add(viewTypeCombo);
		toolBar.addSeparator(new Dimension(20, 0));

		toolBar.add(deleteButton);
		toolBar.addSeparator(new Dimension(20, 0));
		toolBar.add(saveButton);
		toolBar.addSeparator(new Dimension(20, 0));
		toolBar.add(PublishXmlResultAction.getInstance());
		PublishXmlResultAction.getInstance().setEnabled(false);
		return toolBar;
	}

	public JTree getTree() {
		return tree;
	}

	public void refreshAndSelect(boolean fromXml) {
		int[] rows = tree.getSelectionRows();
		refreshTree(fromXml);
		tree.setSelectionRows(rows);

		int numOfTests = tree.getRowCount();
		
		setPublishBtnEnable(numOfTests > 0);
	}

	public void refreshTree(boolean fromXml) {
		clearTextPane();
		try {
			treeModel.refresh(fromXml);
		} catch (Exception e) {
			log.log(Level.WARNING, "fail to refresh model", e);
			return;
		}

		tree.setModel(treeModel);

		expandTree();
		tree.repaint();
	}

	private void expandTree() {
		if (tree == null) {
			return;
		}

		int row = 0;

		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(reloadFileButton)) {

			refreshAndSelect(true);

		} else if (e.getSource().equals(viewTypeCombo)) {
			treeModel.setViewType(viewTypeCombo.getSelectedIndex());
			treeModel.modelChanged();
			refreshTree(false);

		} else if (e.getSource().equals(saveButton)) {
			if (currentNode != null) {
				updateElement(currentNode);
			}
			treeModel.writeToXmlFile();
			refreshAndSelect(true);
		} else if (e.getSource().equals(deleteButton)) {

			/**
			 * multiselection- delete multiple nodes
			 */
			if (tree.getSelectionCount() > 1) {

				TreePath[] paths = tree.getSelectionPaths();
				for (int i = 0; i < paths.length; i++) {
					ElementNode temp = (ElementNode) paths[i].getLastPathComponent();
					ElementNode parent = (ElementNode) temp.getParent();
					if (parent != null) {
						parent.removeElement(temp);
						treeModel.modelChanged();
					}
				}
			}

			else {
				ElementNode parent = (ElementNode) currentNode.getParent();
				if (parent != null) {
					parent.removeElement(currentNode);
					treeModel.modelChanged();

				}
			}
			refreshTree(false);

		} else if (e.getSource().equals(loadFile)) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			chooser.setDialogTitle("Select XML report file");
			ExtentionsFileFilter ff = new ExtentionsFileFilter();
			ff.setDescription("XML files");
			ff.addExtention("xml");
			chooser.setFileFilter(ff);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File file = chooser.getSelectedFile();
			try {
				FileUtils.copyFile(file, new File(reportFileName));
				refreshTree(true);
			} catch (Exception e1) {
				log.log(Level.WARNING, "fail to load model", e1);
			}
		} 
	}

	public void valueChanged(TreeSelectionEvent e) {
		if (currentNode != null) {
			updateElement(currentNode);
		}
		// save the selected node
		currentNode = (ElementNode) tree.getLastSelectedPathComponent();
		if (currentNode != null) {
			deleteButton.setEnabled(true);

			Element element = currentNode.getElement();
			long executeTime = Long.parseLong(element.getAttribute("endTime"))
					- Long.parseLong(element.getAttribute("startTime"));
			infoPanel.setTestParameters(element.getAttribute("name"), Integer.parseInt(element.getAttribute("count")),
					executeTime, currentNode.getStatus(), element.getAttribute("params"), element
							.getAttribute("documentaion"), element.getAttribute("steps"), element
							.getAttribute("properties"), element.getAttribute("failCause"));
			infoPanel.setEditing(true);
		} else {
			deleteButton.setEnabled(false);

			infoPanel.setTestParameters("", 1, 0, Reporter.PASS, "", "", "", "", "");
			infoPanel.setEditing(false);

		}
	}

	/**
	 * signals that the attributes have changed - updated them and writes to xml
	 * file
	 * 
	 * @param node
	 *            the treeNode to update
	 */
	private void updateElement(ElementNode node) {
		int status = infoPanel.getStatus();
		node.setStatus(status);
		String stat = "true";
		if (status == Reporter.FAIL) {
			stat = "false";
		} else if (status == Reporter.WARNING) {
			stat = "warning";
		}
		node.getElement().setAttribute("status", stat);
		node.getElement().setAttribute("params", infoPanel.getParameterString());
		node.getElement().setAttribute("documentaion", infoPanel.getDocumentation());
		node.getElement().setAttribute("steps", infoPanel.getSteps());
		node.getElement().setAttribute("failCause", infoPanel.getErrorCause());
		String properties = StringUtils.propertiesToString(infoPanel.getProperties());
		node.getElement().setAttribute("properties", properties);
		tree.repaint();
	}

	private void clearTextPane() {

	}

	/**
	 * enable/disable "publish" button
	 * 
	 * @param b
	 *            enable/disable
	 */
	public static void setPublishBtnEnable(boolean enablePublishButton) {
		PublishXmlResultAction.getInstance().setEnabled(enablePublishButton);
	}
}
