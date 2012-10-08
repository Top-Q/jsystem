/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jsystem.framework.report.ListenerstManager;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * The GraphMonoitor is a singletone class that keeps tabbed form. Tab for each
 * graph monitor.
 * 
 * @author Uri.Koaz
 */
public class GraphMonitorManager implements TestListener {
	// reference to the fraphMonitor object.
	private static GraphMonitorManager ref;

	private MainTabbedForm mtf;

	private Vector<Graph> graphs; // holds all the graphs add to the manager.

	private ViewUpdater updater; // updater that runs the extra thread

	private static Logger log = Logger.getLogger(GraphMonitorManager.class.getName());

	private GraphMonitorManager() {
		mtf = new MainTabbedForm();
		mtf.setTitle("Graph Monitor");
		graphs = new Vector<Graph>();
		ListenerstManager.getInstance().addListener(this);
	}

	/**
	 * Get the graph monitor singleton instance.
	 * 
	 * @return GraphMonitorManager
	 */
	public static GraphMonitorManager getInstance() {
		if (ref == null) {
			ref = new GraphMonitorManager();
			ref.startTest(null);
		}
		return ref;
	}

	/**
	 * build a new Graph object init it with the params updating the
	 * lastGraphAdded, add it to the graphs list and opens a new tab for it at
	 * the main form.
	 * 
	 * @param graphName
	 * @param yAxiesName
	 * @return Graph
	 * 
	 * @throws Exception
	 */
	public synchronized Graph allocateGraph(String graphName, String yAxiesName) throws Exception {
		if (updater == null) {
			updater = new ViewUpdater(this);
			updater.start();
		}
		Graph newGraph = new Graph(graphName, yAxiesName);
		graphs.add(newGraph);
		mtf.addTab(newGraph);
		if (!isVisible()) {
			showForm();
		}
		return newGraph;
	}

	/**
	 * making the main form visible.
	 * 
	 */
	public void showForm() {
		if (graphs.size() != 0) {
			mtf.setVisible(true);
		}
	}

	/**
	 * making the main form unvisble.
	 */
	public void closeForm() {
		mtf.unvisbleForm();
	}

	/**
	 * Is the graph frame is visable
	 * 
	 * @return true if visable
	 */
	public boolean isVisible() {
		return mtf.isVisible();
	}

	/**
	 * The actions that need to be done every loop of the updater.
	 * 
	 */
	public void updateVisableGraph() {
		try {
			if (graphs.size() != 0) {
				mtf.updateActiveTab();
			}
		} catch (Exception e) {
			log.log(Level.FINE, "Fail while updating graph monitor", e);
		}
	}

	/**
	 * clean the form from tabs.
	 * 
	 */
	private void clearAllTabs() {
		mtf.clearAllTabs();
	}

	public void addError(Test arg0, Throwable arg1) {
	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
	}

	/**
	 * The actions that performed when a test will end.
	 */
	public void endTest(Test arg0) {
		if (graphs.size() == 0) {
			return;
		}
		// updater.interrupt();
		if (updater != null) {
			updater.setStop(true);
			try {
				updater.join();
				updater.interrupt();
			} catch (InterruptedException e1) {
			}
			updater = null;
		}

		try {
			for (int i = 0; i < graphs.size(); i++) {
				((Graph) graphs.get(i)).show(ListenerstManager.getInstance());
				// ((Graph) graphs.get(i)).addGraphToDocument(doc);
			}
			// xmlPerTest = toXmlString();
			// ListenerstManager.getInstance().setData(xmlPerTest);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail writing graphs to report", e);
		}
	}

	/**
	 * The actions that performed when a test will start.
	 */
	public void startTest(Test arg0) {
		// try {
		// doc =
		// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		// } catch (ParserConfigurationException e) {
		// log.log(Level.WARNING, "Fail getting Document", e);
		// }
		//
		// Element root = doc.createElement("testGraphs");
		// doc.appendChild(root);

		closeForm();
		graphs.removeAllElements();
		mtf.clearGraphs();
		clearAllTabs();
	}

	/**
	 * turn doc into xml String
	 * 
	 * @return xml string
	 */
	// private String toXmlString(){
	// Source source = new DOMSource(doc);
	// StringWriter writer = new StringWriter();
	// Result strResult = new StreamResult(writer);
	// Transformer xformer;
	// try {
	// xformer = TransformerFactory.newInstance().newTransformer();
	// xformer.transform(source, strResult);
	// return writer.toString();
	// } catch (Exception e) {
	// log.log(Level.WARNING, "Fail while generating data to xml String", e);
	// return null;
	// }
	// }
	/**
	 * return Graph as vector of points.
	 * 
	 * @param graphName
	 *            the graph to get as vector.
	 * @return vector of points
	 */
	public Vector<String> getVectorGraph(String graphName) {
		Vector<String> graphResults = new Vector<String>();
		// try {
		// NodeList axisList = (NodeList) XPathAPI.selectNodeList(doc,
		// "testGraphs/graph[@name=\"" + graphName + "\"]//*[contains(name(),
		// 'Axis')]");
		// for(int i=0;i<axisList.getLength();i++){
		// if(axisList.item(i) instanceof Element){
		// graphResults.add((String)(axisList.item(i)).getChildNodes().item(0).getNodeValue());
		// }
		// }
		// } catch (Exception e) {
		// log.log(Level.WARNING, "Fail to find root Element", e);
		// }
		return graphResults;
	}

	/**
	 * The actual thread.
	 * 
	 * @author Uri.Koaz
	 */
	public class ViewUpdater extends Thread {
		GraphMonitorManager gmm;

		boolean stop = false;

		public ViewUpdater(GraphMonitorManager gmm) {
			super("GraphViewUpdater");
			this.gmm = gmm;
		}

		/**
		 * while running the thread, this method will be run.
		 */
		public void run() {
			while (!stop) {
				gmm.updateVisableGraph();
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					return;
				}
			}
		}

		public void setStop(boolean stop) {
			this.stop = stop;
		}
	}

	/**
	 * The structure of the tabbed Graph Manger Monitor form, with all it
	 * funcionality.
	 * 
	 * @author Uri.Koaz
	 * 
	 */
	public class MainTabbedForm extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6548037009735775760L;

		JPanel pnPanel0; // main panel.

		JTabbedPane tbpTabbedPane1; // main tabbed panel.

		GridBagConstraints gbcPanel0;

		int mainInset;

		HashMap<String, Graph> graphs = new HashMap<String, Graph>();

		public MainTabbedForm() {
			mainInset = 0;
			pnPanel0 = new JPanel();
			GridBagLayout gbPanel0 = new GridBagLayout();
			gbcPanel0 = new GridBagConstraints();
			pnPanel0.setLayout(gbPanel0);

			tbpTabbedPane1 = new JTabbedPane();

			gbcPanel0.gridx = 0;
			gbcPanel0.gridy = 0;
			gbcPanel0.gridwidth = 0;
			gbcPanel0.gridheight = 0;
			gbcPanel0.fill = GridBagConstraints.BOTH;
			gbcPanel0.weightx = 1;
			gbcPanel0.weighty = 1;
			gbcPanel0.anchor = GridBagConstraints.CENTER;

			gbPanel0.setConstraints(tbpTabbedPane1, gbcPanel0);
			pnPanel0.add(tbpTabbedPane1);
			getContentPane().add(pnPanel0);
			pack();
		}

		public void clearGraphs() {
			graphs = new HashMap<String, Graph>();
		}

		/**
		 * make the form unvisible, but not closing it.
		 * 
		 */
		public void unvisbleForm() {
			// this.show(false);
			this.dispose();
		}

		/**
		 * creates a new tab and adding the image to it.
		 * 
		 * @param graph
		 *            as the graph name
		 * @throws IOException
		 */
		public void addTab(Graph graph) throws IOException {
			graphs.put(graph.getName(), graph);
			ImageIcon icon = new ImageIcon(graph.getImageAsByteArray());
			JPanel pnPanel = new JPanel();
			GridBagLayout gbPanel = new GridBagLayout();
			GridBagConstraints gbcPanel = new GridBagConstraints();
			pnPanel.setLayout(gbPanel);

			JLabel lbLabel = new JLabel(icon);

			gbcPanel.gridx = 0;
			gbcPanel.gridy = 0;
			gbcPanel.gridwidth = 1;
			gbcPanel.gridheight = 1;
			gbcPanel.fill = GridBagConstraints.BOTH;
			gbcPanel.weightx = 1;
			gbcPanel.weighty = 1;
			gbcPanel.anchor = GridBagConstraints.CENTER;
			gbcPanel.insets = new Insets(0, 0, 25, 25);
			gbPanel.setConstraints(lbLabel, gbcPanel);
			pnPanel.add(lbLabel);

			tbpTabbedPane1.addTab(graph.getName(), pnPanel);
			tbpTabbedPane1.setSelectedIndex(tbpTabbedPane1.indexOfTab(graph.getName()));

			pack();
		}

		/**
		 * finding the existing tab acording to it name and replacing the image
		 * on it.
		 * 
		 * as the graph name
		 * 
		 * @throws IOException
		 */
		public void updateActiveTab() throws IOException {
			int index = tbpTabbedPane1.getSelectedIndex();
			if (index < 0) {
				return;
			}

			Graph gp = (Graph) graphs.get(tbpTabbedPane1.getTitleAt(index));
			if (gp == null) {
				return;
			}
			ImageIcon icon = new ImageIcon(gp.getImageAsByteArray());
			JPanel pnPanel = new JPanel();
			GridBagLayout gbPanel = new GridBagLayout();
			GridBagConstraints gbcPanel = new GridBagConstraints();
			pnPanel.setLayout(gbPanel);

			JLabel lbLabel = new JLabel(icon);

			gbcPanel.gridx = 0;
			gbcPanel.gridy = 0;
			gbcPanel.gridwidth = 1;
			gbcPanel.gridheight = 1;
			gbcPanel.fill = GridBagConstraints.BOTH;
			gbcPanel.weightx = 1;
			gbcPanel.weighty = 1;
			gbcPanel.anchor = GridBagConstraints.CENTER;
			gbcPanel.insets = new Insets(0, 0, 25, 25);
			gbPanel.setConstraints(lbLabel, gbcPanel);
			pnPanel.add(lbLabel);
			int tabIndex = tbpTabbedPane1.indexOfTab(gp.getName());
			if (tabIndex < 0) {
				return;
			}
			tbpTabbedPane1.setComponentAt(tabIndex, pnPanel);
		}

		/**
		 * deleting all tabs from form.
		 */
		public void clearAllTabs() {
			tbpTabbedPane1.removeAll();
		}
	}
}