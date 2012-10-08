/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureListener;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.report.TestReporter;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;
import jsystem.utils.TestUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 */
public class TestReporterTable extends JTable implements TestReporter, TestListener, FixtureListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TestReportModel commandsModel = null;

	private int lastIndex = 0;

	/**
	 * Constructs a logger panel
	 */
	public TestReporterTable() {
		commandsModel = new TestReportModel();

		setModel(commandsModel);

		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		final int column0 = (int) (dim.getWidth() * 0.5);
		final int column1 = (int) (dim.getWidth() * 0.1);

		columnModel.getColumn(0).setPreferredWidth(column0);
		columnModel.getColumn(1).setPreferredWidth(column1);

		columnModel.getColumn(0).setHeaderRenderer(new HeaderCellRenderer());
		columnModel.getColumn(1).setHeaderRenderer(new HeaderCellRenderer());

		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);

		setDefaultRenderer(Object.class, new ReporterCellRenderer());

		getTableHeader().repaint();
	}

	public void init() {
		clearTextArea();
	}

	/**
	 * Clear the text area
	 */
	public void clearTextArea() {

		commandsModel.clearModel();
	}

	/**
	 * Copy selected text
	 */
	public void copy() {
	}

	/**
	 * Set the report status (pass/fail/warning)
	 * 
	 * @param status
	 *            int value to indicate Pass/Fail/Warning
	 */
	public void setReportStatus(int status, boolean bold) {

		commandsModel.addStatus(lastIndex, status, bold);
		getTableHeader().repaint();
	}

	public void report(String title, String message, boolean isPass, boolean bold) {
		int status = isPass? Reporter.PASS : Reporter.FAIL;
		report(title, message, status, bold);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.TestReporter#initReporterManager()
	 */
	public void initReporterManager() throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.TestReporter#asUI()
	 */
	public boolean asUI() {
		return false;
	}

	public String getName() {
		return "GUI reporter";
	}

	/**
	 * Clear the table
	 */
	public void clearTable() {
		commandsModel.clearModel();
		getTableHeader().repaint();
	}

	static class ReporterCellRenderer extends JLabel implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final int FAIL = -1;
		
		private final int WARNING = 2;

		private final int MAX_LINE_WORDS = 10;

		private final String MARGIN = "  ";

		private final String SPLIT = " ";

		private final String NEW_LINE = "\n";

		private final int COMMAND = 0;

		private final int STATUS = 1;

		Color bColor;
		
		private static Color BOLD_PASS_COLOR = new Color(0x29, 0x3e, 0xbe);
		private static Color PASS_COLOR = new Color(0x4e, 0x4e, 0x4e);
		
		public ReporterCellRenderer() {

		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {

			/**
			 * color of selected row
			 */
			Color selectColor;

			/**
			 * text color
			 */
			Color textColor;

			Color focusColor;

			TestReportModel model = (TestReportModel) table.getModel();

			/**
			 * current command to write
			 */
			TestReportCommand currentCommand = model.getCommandAt(row);

			/**
			 * if command not exists
			 */
			if (currentCommand == null) {
				return this;
			}

			/**
			 * ddetermine the color for the text
			 */
			if (currentCommand.status == FAIL) {
				textColor = new Color(0xc9, 0x16, 0x25);
			} else {
				if (currentCommand.bold){
					textColor = BOLD_PASS_COLOR;
				}else {
					textColor = PASS_COLOR;
				}
			}

			focusColor = Color.white;

			/**
			 * act acording to the column
			 */
			switch (column) {

			/**
			 * first column
			 */
			case COMMAND:
				setIcon(null);

				String command = (String) value;

				selectColor = Color.white;

				setHorizontalAlignment(SwingConstants.LEFT);

				String[] words = command.split(SPLIT);

				StringBuffer buffer;

				/**
				 * if the command is longer than possible (MAX_LINE_WORDS), show
				 * only the possible length of it
				 */
				if (words.length > MAX_LINE_WORDS) {

					buffer = new StringBuffer();

					for (int i = 1; i <= words.length; i++) {
						String word = words[i - 1];

						if ((i % (MAX_LINE_WORDS)) != 0) {
							buffer = buffer.append(word + SPLIT);
						} else {
							buffer = buffer.append(word + NEW_LINE + MARGIN);
						}
					}
				} else {
					buffer = new StringBuffer(command);
				}

				String text = buffer.toString();
				setText(MARGIN + text);
				setToolTipText(text);

				break;

			/**
			 * case of second column
			 */
			case STATUS:

				selectColor = Color.white;
				setHorizontalAlignment(SwingConstants.CENTER);

				if (currentCommand.bold) {
					setText("");
					setIcon(null);
				} else {
					setText(null);
					
					if (currentCommand.status == FAIL) {
						setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_REPORT_FAIL));
					} else if (currentCommand.status == WARNING) {
						setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_REPORT_WARNING));
					} 
					else {
						setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_REPORT_PASS));
					}
				}
				break;
			default:
				selectColor = Color.white;
				textColor = new Color(0x4e, 0x4e, 0x4e);
				setHorizontalAlignment(SwingConstants.LEFT);
				setText(value.toString());
				break;
			}
			setBorder(new LineBorder(Color.WHITE, -1));

			if (isSelected) {
				setForeground(selectColor);
			} else {
				if (currentCommand.bold) {

					setForeground(textColor);
					setFont(new Font("sansserif", Font.BOLD, 11));

				} else {
					setForeground(textColor);
					setFont(new Font("sansserif", Font.PLAIN, 11));
				}
			}

			if (hasFocus) {
				setForeground(focusColor);
			}

			// Set the correct background colour
			if (isSelected) {
				bColor = UIManager.getColor("Tree.selectionBackground");
			} else {
				if ((row % 2) == 0) {
					bColor = new Color(0xf7, 0xfd, 0xff);
				} else {
					bColor = Color.white;
				}
			}

			if (hasFocus) {
				bColor = UIManager.getColor("Tree.selectionBackground");
			}

			return this;
		}

		public void paint(Graphics g) {

			g.setColor(bColor);

			// Draw a rectangle in the background of the cell
			g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

			super.paint(g);
		}
	}

	static class HeaderCellRenderer extends JLabel implements TableCellRenderer {

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
				setText("Commands");
				break;

			case 1:
				setText("Status");
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

	boolean lastTestStatus = true;
	volatile boolean  inParametersLog = false;
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 *      java.lang.Throwable)
	 */
	public void addError(Test arg0, Throwable arg1) {
		lastTestStatus = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 *      junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		lastTestStatus = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test test) {
		String testName = TestUtils.getTestName(test);
		report("End: " + testName, null, lastTestStatus,true);
		report("", null, true, true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test test) {
		lastTestStatus = true;
		String testName = TestUtils.getTestName(test);
		report("Start: " + testName, null, true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.fixture.FixtureListener#aboutToChangeTo(jsystem.framework.launcher.fixture.Fixture)
	 */
	public void aboutToChangeTo(Fixture fixture) {
		report("Change fixture to: " + StringUtils.getClassName(fixture.getName()), null, true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.fixture.FixtureListener#fixtureChanged(jsystem.framework.launcher.fixture.Fixture)
	 */
	public void fixtureChanged(Fixture fixture) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.fixture.FixtureListener#startFixturring()
	 */
	public void startFixturring() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.fixture.FixtureListener#endFixturring()
	 */
	public void endFixturring() {

	}

	public void report(String title, String message, int status, boolean bold) {
		if (RunnerListenersManager.PARAMETERS_START.equals(title)){
			inParametersLog = true;
		}
		if ("true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_LOG_PARAMS_IN_LEVEL)) && inParametersLog){
			if (RunnerListenersManager.PARAMETERS_END.equals(title)){
				inParametersLog = false;
			}			
			return;
		}
		lastIndex = commandsModel.addCommand(title);
		setReportStatus(status, bold);
		Rectangle r = getCellRect(getRowCount(), 0, true);

		r.setLocation((int) r.getX(), (int) r.getY() + 40);
		try {
			scrollRectToVisible(r);
		} catch (Exception e){ 
			//ignore
			//the method throws an exception which is I think is a result of the
			//L&F we are using.
			//As far as I understand this doesn't have an affect on execution, so 
			//I'm catching the Exception.
		}
	}
}
