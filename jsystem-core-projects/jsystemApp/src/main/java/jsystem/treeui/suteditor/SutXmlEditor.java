/*
 * Created on 27/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.suteditor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

public class SutXmlEditor extends JDialog implements ActionListener, SutEditor, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3242874217688222418L;

	JButton okButton;

	JButton cancelButton;

	Container pane;

	boolean save = false;

	public SutXmlEditor() {
		super(TestRunnerFrame.guiMainFrame);
		setTitle("Edit SUT Fields");
		Toolkit theTk = Toolkit.getDefaultToolkit();
		Dimension temp = theTk.getScreenSize();
		int h = temp.height;
		int w = temp.width;
		setSize(w / 4, h / 2);
		setLocation(w / 4, h / 4);

		pane = getContentPane();

		okButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
		setModalityType(ModalityType.APPLICATION_MODAL);
	}

	/**
	 * 
	 */
	public boolean isEditable(Document doc) throws Exception {
		return XPathAPI.selectNodeList(doc, "//*[@edit='enable']").getLength() > 0;
	}

	public Document editSut(Document doc, boolean withSave) throws Exception {
		/*
		 * close the wait dialog
		 */
		WaitDialog.endWaitDialog();
		addWindowListener(this);
		NodeIterator ni = XPathAPI.selectNodeIterator(doc, "//*[@edit='enable']");
		// NodeIterator ni = XPathAPI.selectNodeIterator(doc,"//*[text() !=
		// '']");
		Element el = null;
		Vector<EditObject> editObjects = new Vector<EditObject>();
		while ((el = (Element) ni.nextNode()) != null) {
			EditObject eo = new EditObject();
			eo.label = getName(el);
			eo.field.setText(((Text) el.getFirstChild()).getData());
			eo.element = el;
			editObjects.addElement(eo);
			// getContentPane().add(eo.getPanel());
		}
		if (editObjects.size() == 0) {
			ErrorPanel
					.showErrorDialog(
							"SUT Editor",
							"No items were set to edit=\"enable\"\nIn order to enable tag editing add edit attribute\nto the tags.",
							ErrorLevel.Error);
			return null;
		}
		pane.setLayout(new GridLayout(editObjects.size() + 1, 1));
		for (int i = 0; i < editObjects.size(); i++) {
			pane.add(((EditObject) editObjects.elementAt(i)).getPanel());
		}

		JPanel bp = new JPanel();
		bp.add(okButton);
		bp.add(cancelButton);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		// button.addActionListener(listener);

		pane.add(bp);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// getContentPane().add(pane);
		pack();
		setVisible(true);
		// synchronized (this) {
		// this.wait();
		// }
		// dispose();
		if (save && withSave) {
			for (int i = 0; i < editObjects.size(); i++) {
				EditObject eo = (EditObject) editObjects.elementAt(i);
				if (eo.element.hasChildNodes()) {
					NodeList list = eo.element.getChildNodes();
					for (int j = 0; j < list.getLength(); j++) {
						if (list.item(j) instanceof Text) {
							eo.element.removeChild(list.item(j));
						}
					}
				}
				eo.element.appendChild(doc.createTextNode(eo.field.getText()));
				// setTextContent(eo.field.getText());
			}
			return doc;
		}
		return null;
	}

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

	public static void main(String[] args) {
		// SutXmlEditor editor = new
		// SutXmlEditor("D:\\work\\projects\\automation__\\jsystem\\classes\\sut\\xml4Test.xml");
		// try {
		// editor.init();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		dispose();
		// synchronized (this) {
		// this.notifyAll();
		// }
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}

class EditObject {
	public String label;

	public String value = null;

	public JTextField field = null;

	public Element element;

	public EditObject() {
		field = new JTextField(30);
	}

	public JPanel getPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder(label));
		p.add(field);
		return p;
	}
}
