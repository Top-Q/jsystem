/*
 * Created on 27/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.suteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.DefaultMenuLayout;
import javax.xml.transform.TransformerException;

import jsystem.framework.TestRunnerFrame;
import jsystem.framework.sut.SutEditor;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.error.ErrorPanel;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

/**
 * SUT tabbed viewer. The editor creates a tab for each SystemObject in the sut
 * file, for each SystemObject it looks for elements with attribute
 * edit="enable" and shows them in the tab of the SystemObject. The editor also
 * looks for the attribute description="bla bla" and shows the description of
 * the element.
 * 
 * If attributes width,height are added to the root element (sut) the size of
 * the dialog will be set according to the value of these attributes.
 * 
 * The editor can be used only if at least one element is set with attribute
 * edit="true"
 * 
 * Example SUT:
 * 
 * <sut width="120" height="100"> <general> <perlHome>D:/Perl</perlHome>
 * <releaseVersion>xxx</releaseVersion> <menu>menu_v2</menu> </general>
 * <jSystemStation> <class>com.aqua.stations.windows.WindowsStation</class>
 * <host edit="enable" description="windows station ip">127.0.0.1</host>
 * <cliUser edit="enable">metalinkdsl\lab</cliUser> <cliPassword >metlab</cliPassword>
 * <cliProtocol>telnet</cliProtocol> </jSystemStation>
 * 
 * </sut>
 * 
 * @author Golan Derazon
 */
public class TabbedSutXmlEditor extends JDialog implements ActionListener, SutEditor {
	private static final long serialVersionUID = 3242874217688222418L;
	private static Logger log = Logger.getLogger(TabbedSutXmlEditor.class.getName());
	boolean save = false;

	public TabbedSutXmlEditor() {
		super(TestRunnerFrame.guiMainFrame);
		setTitle("Edit SUT Fields");
		setModalityType(ModalityType.APPLICATION_MODAL);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	/**
	 * 
	 */
	public boolean isEditable(Document doc) throws Exception {
		try {
			return XPathAPI.selectNodeList(doc, "//*[@edit='enable']").getLength() > 0;
		}catch (Exception e){
			log.log(Level.WARNING,"Failed in sut validation. Please reduce log level to fine to see full error stack");
			log.log(Level.FINE,"Failed in sut validation.",e);
			return false; 
		}
	}

	/**
	 * Called by the framework when the user presses on the edit SUT button.
	 */
	public Document editSut(Document doc, boolean withSave) throws Exception {
		WaitDialog.endWaitDialog();

		// a list which holds all items
		ArrayList<FixedEditObject> allEditObjects = new ArrayList<FixedEditObject>();

		// a list which holds the sections (tabs), each section is also a list
		// which holds the
		// items is the section
		ArrayList<ArrayList<FixedEditObject>> listOfSUTSections = new ArrayList<ArrayList<FixedEditObject>>();

		// A list which holds the names of the sections
		ArrayList<String> sectionNames = new ArrayList<String>();

		boolean foundItemsToEdit = buildEditorDataStructure(doc, allEditObjects, listOfSUTSections, sectionNames);

		if (!foundItemsToEdit) {
			ErrorPanel
					.showErrorDialog(
							"SUT Editor",
							"No items were set to edit=\"enable\"\nIn order to enable tag editing add edit attribute\nto the tags.",
							ErrorLevel.Warning);
			return null;
		}

		buildAndShowDialog(doc, listOfSUTSections, sectionNames);

		if (save && withSave) {
			updateDocument(doc, allEditObjects);
			return doc;
		}
		return null;
	}

	/**
	 * Creates data structures which hold sut editable information.
	 */
	private boolean buildEditorDataStructure(Document doc, ArrayList<FixedEditObject> allEditObjects,
			ArrayList<ArrayList<FixedEditObject>> listOfSUTSections, ArrayList<String> sectionNames)
			throws TransformerException {
		boolean foundItemsToEdit = false;
		Node sutNode = getSutNode(doc);
		NodeList listOfNodes = sutNode.getChildNodes();
		Element el;
		Element e2;
		for (int i = 0; i < listOfNodes.getLength(); i++) {
			Node node = listOfNodes.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			e2 = (Element) node;
			ArrayList<FixedEditObject> sectionItemsList = new ArrayList<FixedEditObject>();

			String tagName = e2.getTagName();
			sectionNames.add(tagName);
			NodeIterator ni = XPathAPI.selectNodeIterator(e2, "//*[@edit='enable']");
			while ((el = (Element) ni.nextNode()) != null) {

				foundItemsToEdit = true;
				FixedEditObject eo = new FixedEditObject();
				eo.label = getName(el);
				eo.description = el.getAttribute("description") == null ? "" : el.getAttribute("description");
				String tabCanonicalName = getName(e2)+"/";
				if (!eo.label.startsWith(tabCanonicalName)) {
					continue;
				}
				String fieldText = "";
				if (el.getFirstChild() != null){
					fieldText = ((Text) el.getFirstChild()).getData();
				}
				eo.field.setText(fieldText);
				eo.element = el;
				sectionItemsList.add(eo);
				allEditObjects.add(eo);
			}
			listOfSUTSections.add(sectionItemsList);
		}
		return foundItemsToEdit;
	}

	private Node getSutNode(Document doc) throws TransformerException {
		NodeList listOfNodes = XPathAPI.selectNodeList(doc, "/sut");
		Node sutNode = listOfNodes.item(0);
		return sutNode;
	}

	/**
	 * Draws and shows the dialog according to the tabs and elements data
	 * structure
	 */
	private void buildAndShowDialog(Document doc, ArrayList<ArrayList<FixedEditObject>> listOfSUTSections,
			ArrayList<String> sectionNames) throws TransformerException {

		JTabbedPane sysObjectsTab = new JTabbedPane();
		int totalNumOfSections = 0;

		for (int i = 0; i < listOfSUTSections.size(); i++) {
			String name = sectionNames.get(i);
			ArrayList<FixedEditObject> eoList = listOfSUTSections.get(i);
			if (eoList.size() == 0) {
				continue;
			}
			totalNumOfSections++;
			JPanel sectionPanel = new JPanel();
			sectionPanel.setLayout(new DefaultMenuLayout(sectionPanel, BoxLayout.Y_AXIS));
			for (int j = 0; j < eoList.size(); j++) {
				sectionPanel.add(((FixedEditObject) eoList.get(j)).getPanel());
			}
			sysObjectsTab.add(name, new JScrollPane(sectionPanel));
		}

		JPanel bp = new JPanel();
		JButton okButton;
		JButton cancelButton;
		okButton = new JButton("Save");
		cancelButton = new JButton("Cancel");

		bp.add(okButton);
		bp.add(cancelButton);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(sysObjectsTab, BorderLayout.CENTER);
		mainPanel.add(bp, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		Toolkit theTk = Toolkit.getDefaultToolkit();
		Dimension temp = theTk.getScreenSize();
		int h = temp.height;
		int w = temp.width;
		setLocation(w / 4, h / 4);
		Dimension dim = getDialogDimention(doc);
		if (dim == null) {
			dim = new Dimension(400, 400);
		}
		setSize(dim);
		setVisible(true);
	}

	/**
	 */
	private Dimension getDialogDimention(Document doc) throws TransformerException{
		Node sutNode = getSutNode(doc);
		String width = ((Element) sutNode).getAttribute("width");
		String height = ((Element) sutNode).getAttribute("height");
		if (width != null && height != null && !"".equals(width.trim()) && !"".equals(height.trim())) {
			return new Dimension(Integer.parseInt(width), Integer.parseInt(height));
		}
		return null;
	}

	/**
	 * Updates document <code>doc</code> with user's updates.
	 */
	private void updateDocument(Document doc, ArrayList<FixedEditObject> allEditObjects) {
		for (int i = 0; i < allEditObjects.size(); i++) {
			FixedEditObject eo = allEditObjects.get(i);
			if (eo.element.hasChildNodes()) {
				NodeList list = eo.element.getChildNodes();
				for (int j = 0; j < list.getLength(); j++) {
					if (list.item(j) instanceof Text) {
						eo.element.removeChild(list.item(j));
					}
				}
			}
			eo.element.appendChild(doc.createTextNode(eo.field.getText()));
		}
	}

	/**
	 * Given a XML elements creates a String which 
	 * presents element's canonical name.
	 */
	private String getName(Element el) {
		Node currentNode = el;
		StringBuffer buf = new StringBuffer();
		while (true) {
			if (currentNode == null || !(currentNode instanceof Element)) {
				break;
			}
			Element e = (Element) currentNode;
			String tagName = e.getTagName();
			String index = e.getAttribute("index");
			if (index != null && !index.equals("")) {
				tagName = tagName + "[" + index + "]";
			}
			buf.insert(0, "/" + tagName);
			currentNode = currentNode.getParentNode();
		}
		return buf.toString();
	}

	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Save")) {
			save = true;
		} else if (e.getActionCommand().equals("Cancel")) {

		}
		dispose();
	}

}

/**
 * 
 */
class FixedEditObject {
	public String label;

	public String value = null;

	public String description = "sss";

	public JTextField field = null;

	public Element element;

	public FixedEditObject() {
		field = new JTextField(30);
	}

	public JPanel getPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.setBorder(BorderFactory.createTitledBorder(label));
		p.add(field, BorderLayout.WEST);
		JLabel descriptionLabel = new JLabel(description);
		descriptionLabel.setToolTipText(description);
		p.add(descriptionLabel, BorderLayout.WEST);
		p.setMaximumSize(new Dimension(350, 70));
		return p;
	}
}
