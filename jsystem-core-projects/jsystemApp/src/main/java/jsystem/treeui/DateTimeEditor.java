/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

/*
 *  DateTimeEditor taken from:
 *	 Swing, Second Edition
 *	 by Matthew Robinson, Pavel Vorobiev
 *
 **/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.PlainDocument;
import javax.swing.text.TextAction;

public class DateTimeEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final long ONE_SECOND = 1000;

	public static final long ONE_MINUTE = 60 * ONE_SECOND;

	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	public static final long ONE_DAY = 24 * ONE_HOUR;

	public static final long ONE_WEEK = 7 * ONE_DAY;

	public final static int TIME = 0;

	public final static int DATE = 1;

	public final static int DATETIME = 2;

	private int m_timeOrDateType;

	private int m_lengthStyle;

	private DateFormat m_format;

	private Calendar m_calendar = Calendar.getInstance();

	private ArrayList<FieldPosition> m_fieldPositions = new ArrayList<FieldPosition>();

	private Date m_lastDate = new Date();

	private Caret m_caret;

	private int m_curField = -1;

	private JTextField m_textField;

	private Spinner m_spinner;

	private AbstractAction m_upAction = new UpDownAction(1, "up");

	private AbstractAction m_downAction = new UpDownAction(-1, "down");

	private boolean m_settingDateText = false; // BUG FIX

	private int[] m_fieldTypes = { DateFormat.ERA_FIELD, DateFormat.YEAR_FIELD, DateFormat.MONTH_FIELD,
			DateFormat.DATE_FIELD, DateFormat.HOUR_OF_DAY1_FIELD, DateFormat.HOUR_OF_DAY0_FIELD,
			DateFormat.MINUTE_FIELD, DateFormat.SECOND_FIELD, DateFormat.MILLISECOND_FIELD,
			DateFormat.DAY_OF_WEEK_FIELD, DateFormat.DAY_OF_YEAR_FIELD, DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD,
			DateFormat.WEEK_OF_YEAR_FIELD, DateFormat.WEEK_OF_MONTH_FIELD, DateFormat.AM_PM_FIELD,
			DateFormat.HOUR1_FIELD, DateFormat.HOUR0_FIELD };

	public DateTimeEditor() {
		m_timeOrDateType = DATETIME;
		m_lengthStyle = DateFormat.SHORT;
		init();
	}

	public DateTimeEditor(int timeOrDateType) {
		m_timeOrDateType = timeOrDateType;
		m_lengthStyle = DateFormat.FULL;
		init();
	}

	public DateTimeEditor(int timeOrDateType, int lengthStyle) {
		m_timeOrDateType = timeOrDateType;
		m_lengthStyle = lengthStyle;
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		m_textField = new JTextField();

		m_textField.setDocument(new DateTimeDocument()); // BUG FIX

		m_spinner = new Spinner();
		m_spinner.getIncrementButton().addActionListener(m_upAction);
		m_spinner.getDecrementButton().addActionListener(m_downAction);
		add(m_textField, "Center");
		add(m_spinner, "East");
		m_caret = m_textField.getCaret();
		m_caret.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				setCurField();
			}
		});
		setupKeymap();
		reinit();
	}

	protected class DateTimeDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;

		public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
			if (m_settingDateText)
				super.insertString(offset, str, a);
		}
	} // BUG FIX

	public int getTimeOrDateType() {
		return m_timeOrDateType;
	}

	public void setTimeOrDateType(int timeOrDateType) {
		m_timeOrDateType = timeOrDateType;
		reinit();
	}

	public int getLengthStyle() {
		return m_lengthStyle;
	}

	public void setLengthStyle(int lengthStyle) {
		m_lengthStyle = lengthStyle;
		reinit();
	}

	public Date getDate() {
		return (m_lastDate);
	}

	// public void setDate(Date date)
	// {
	//    m_lastDate = date;
	//    m_calendar.setTime(m_lastDate);
	//    m_textField.setText(m_format.format(m_lastDate));
	//    getFieldPositions();
	// }

	public void setDate(Date date) {
		m_lastDate = date;
		m_calendar.setTime(m_lastDate);
		m_settingDateText = true;
		m_textField.setText(m_format.format(m_lastDate));
		m_settingDateText = false;
		getFieldPositions();
	} // BUG FIX

	@SuppressWarnings("unused")
	private int getFieldBeginIndex(int fieldNum) {
		int beginIndex = -1;
		for (Iterator<FieldPosition> iter = m_fieldPositions.iterator(); iter.hasNext();) {
			FieldPosition fieldPos = (FieldPosition) iter.next();
			if (fieldPos.getField() == fieldNum) {
				beginIndex = fieldPos.getBeginIndex();
				break;
			}
		}
		return (beginIndex);
	}

	private FieldPosition getFieldPosition(int fieldNum) {
		FieldPosition result = null;
		for (Iterator<FieldPosition> iter = m_fieldPositions.iterator(); iter.hasNext();) {
			FieldPosition fieldPosition = (FieldPosition) iter.next();
			if (fieldPosition.getField() == fieldNum) {
				result = fieldPosition;
				break;
			}
		}
		return (result);
	}

	private void reinit() {
		setupFormat();
		setDate(m_lastDate);
		m_caret.setDot(0);
		setCurField();
		repaint();
	}

	protected void setupFormat() {
		switch (m_timeOrDateType) {
		case TIME:
			m_format = DateFormat.getTimeInstance(m_lengthStyle);
			break;
		case DATE:
			m_format = DateFormat.getDateInstance(m_lengthStyle);
			break;
		case DATETIME:
			m_format = DateFormat.getDateTimeInstance(m_lengthStyle, m_lengthStyle);
			break;
		}
	}

	protected class UpDownAction extends AbstractAction {

		private static final long serialVersionUID = -7660798032188469748L;
		int m_direction; // +1 = up; -1 = down

		public UpDownAction(int direction, String name) {
			super(name);
			m_direction = direction;
		}

		public void actionPerformed(ActionEvent evt) {
			if (!this.isEnabled())
				return;
			boolean dateSet = true;
			switch (m_curField) {
			case DateFormat.AM_PM_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * 12 * ONE_HOUR));
				break;
			case DateFormat.DATE_FIELD:
			case DateFormat.DAY_OF_WEEK_FIELD:
			case DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD:
			case DateFormat.DAY_OF_YEAR_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_DAY));
				break;
			case DateFormat.ERA_FIELD:
				dateSet = false;
				break;
			case DateFormat.HOUR0_FIELD:
			case DateFormat.HOUR1_FIELD:
			case DateFormat.HOUR_OF_DAY0_FIELD:
			case DateFormat.HOUR_OF_DAY1_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_HOUR));
				break;
			case DateFormat.MILLISECOND_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * 1));
				break;
			case DateFormat.MINUTE_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_MINUTE));
				break;
			case DateFormat.MONTH_FIELD:
				m_calendar.set(Calendar.MONTH, m_calendar.get(Calendar.MONTH) + m_direction);
				m_lastDate = m_calendar.getTime();
				break;
			case DateFormat.SECOND_FIELD:
				m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_SECOND));
				break;
			case DateFormat.WEEK_OF_MONTH_FIELD:
				m_calendar.set(Calendar.WEEK_OF_MONTH, m_calendar.get(Calendar.WEEK_OF_MONTH) + m_direction);
				m_lastDate = m_calendar.getTime();
				break;
			case DateFormat.WEEK_OF_YEAR_FIELD:
				m_calendar.set(Calendar.WEEK_OF_MONTH, m_calendar.get(Calendar.WEEK_OF_MONTH) + m_direction);
				m_lastDate = m_calendar.getTime();
				break;
			case DateFormat.YEAR_FIELD:
				m_calendar.set(Calendar.YEAR, m_calendar.get(Calendar.YEAR) + m_direction);
				m_lastDate = m_calendar.getTime();
				break;
			default:
				dateSet = false;
			}

			if (dateSet) {
				int fieldId = m_curField;
				setDate(m_lastDate);
				FieldPosition fieldPosition = getFieldPosition(fieldId);
				m_caret.setDot(fieldPosition.getBeginIndex());

				m_textField.requestFocus();
				repaint();
			}
		}
	}

	protected class BackwardAction extends TextAction {
		private static final long serialVersionUID = 8782340659423126767L;

		BackwardAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				int dot = target.getCaretPosition();
				if (dot > 0) {
					FieldPosition position = getPrevField(dot);
					if (position != null)
						target.setCaretPosition(position.getBeginIndex());
					else {
						position = getFirstField();
						if (position != null)
							target.setCaretPosition(position.getBeginIndex());
					}
				} else
					target.getToolkit().beep();
				target.getCaret().setMagicCaretPosition(null);
			}
		}
	}

	protected class ForwardAction extends TextAction {
		private static final long serialVersionUID = 3348322732916216971L;

		ForwardAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				FieldPosition position = getNextField(target.getCaretPosition());
				if (position != null)
					target.setCaretPosition(position.getBeginIndex());
				else {
					position = getLastField();
					if (position != null)
						target.setCaretPosition(position.getBeginIndex());
				}
				target.getCaret().setMagicCaretPosition(null);
			}
		}
	}

	protected class BeginAction extends TextAction {
		private static final long serialVersionUID = -9028868618267415166L;

		BeginAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				FieldPosition position = getFirstField();
				if (position != null)
					target.setCaretPosition(position.getBeginIndex());
			}
		}
	}

	protected class EndAction extends TextAction {
		private static final long serialVersionUID = -3044788954608522299L;

		EndAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				FieldPosition position = getLastField();
				if (position != null)
					target.setCaretPosition(position.getBeginIndex());
			}
		}
	}

	protected void setupKeymap() {
		Keymap keymap = JTextField.addKeymap("DateTimeKeymap", null);
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), m_upAction);
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), m_downAction);
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new BackwardAction(
				DefaultEditorKit.backwardAction));
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new ForwardAction(
				DefaultEditorKit.forwardAction));
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new BeginAction(
				DefaultEditorKit.beginAction));
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new EndAction(
				DefaultEditorKit.endAction));
		m_textField.setKeymap(keymap);
	}

	private void getFieldPositions() {
		m_fieldPositions.clear();
		for (int ctr = 0; ctr < m_fieldTypes.length; ++ctr) {
			int fieldId = m_fieldTypes[ctr];
			FieldPosition fieldPosition = new FieldPosition(fieldId);
			StringBuffer formattedField = new StringBuffer();
			m_format.format(m_lastDate, formattedField, fieldPosition);
			if (fieldPosition.getEndIndex() > 0)
				m_fieldPositions.add(fieldPosition);
		}
		m_fieldPositions.trimToSize();
		Collections.sort(m_fieldPositions, new Comparator<FieldPosition>() {
			@Override
			public int compare(FieldPosition o1, FieldPosition o2) {
				return o1.getBeginIndex() - o2.getBeginIndex();
			}
		});
	}

	private FieldPosition getField(int caretLoc) {
		FieldPosition fieldPosition = null;
		for (Iterator<FieldPosition> iter = m_fieldPositions.iterator(); iter.hasNext();) {
			FieldPosition chkFieldPosition = (FieldPosition) iter.next();
			if ((chkFieldPosition.getBeginIndex() <= caretLoc) && (chkFieldPosition.getEndIndex() > caretLoc)) {
				fieldPosition = chkFieldPosition;
				break;
			}
		}
		return (fieldPosition);
	}

	private FieldPosition getPrevField(int caretLoc) {
		FieldPosition fieldPosition = null;
		for (int ctr = m_fieldPositions.size() - 1; ctr > -1; --ctr) {
			FieldPosition chkFieldPosition = (FieldPosition) m_fieldPositions.get(ctr);
			if (chkFieldPosition.getEndIndex() <= caretLoc) {
				fieldPosition = chkFieldPosition;
				break;
			}
		}
		return (fieldPosition);
	}

	private FieldPosition getNextField(int caretLoc) {
		FieldPosition fieldPosition = null;
		for (Iterator<FieldPosition> iter = m_fieldPositions.iterator(); iter.hasNext();) {
			FieldPosition chkFieldPosition = (FieldPosition) iter.next();
			if (chkFieldPosition.getBeginIndex() > caretLoc) {
				fieldPosition = chkFieldPosition;
				break;
			}
		}
		return (fieldPosition);
	}

	private FieldPosition getFirstField() {
		FieldPosition result = null;
		if (m_fieldPositions.size() == 0){
			return null;
		}
		try {
			result = ((FieldPosition) m_fieldPositions.get(0));
		} catch (NoSuchElementException ex) {
		}
		return (result);
	}

	private FieldPosition getLastField() {
		FieldPosition result = null;
		try {
			result = ((FieldPosition) m_fieldPositions.get(m_fieldPositions.size() - 1));
		} catch (NoSuchElementException ex) {
		}
		return (result);
	}

	private void setCurField() {
		FieldPosition fieldPosition = getField(m_caret.getDot());
		if (fieldPosition != null) {
			if (m_caret.getDot() != fieldPosition.getBeginIndex())
				m_caret.setDot(fieldPosition.getBeginIndex());
		} else {
			fieldPosition = getPrevField(m_caret.getDot());
			if (fieldPosition != null)
				m_caret.setDot(fieldPosition.getBeginIndex());
			else {
				fieldPosition = getFirstField();
				if (fieldPosition != null)
					m_caret.setDot(fieldPosition.getBeginIndex());
			}
		}

		if (fieldPosition != null)
			m_curField = fieldPosition.getField();
		else
			m_curField = -1;
	}

	public void setEnabled(boolean enable) {
		m_textField.setEnabled(enable);
		m_spinner.setEnabled(enable);
	}

	public boolean isEnabled() {
		return (m_textField.isEnabled() && m_spinner.isEnabled());
	}

	public void showDialog() {
		final Date currentDate = (Date)getDate().clone();
		final JDialog frame = new JDialog();
		JPanel buttonsPanel = new JPanel();
		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		final DateTimeEditor field = this;
		panel.add(field, BorderLayout.NORTH);
		JButton close = new JButton("Close");
		JButton cancel = new JButton("Cancel");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.setVisible(false);
				frame.dispose();
				setDate(currentDate);
			}
		});
		buttonsPanel.add(close);
		buttonsPanel.add(cancel);
		panel.add(buttonsPanel, BorderLayout.SOUTH);
		frame.pack();
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getWidth() / 2, dim.height / 2 - frame.getHeight() / 2);
		frame.setModalityType(ModalityType.APPLICATION_MODAL);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		
		DateTimeEditor field0 = new DateTimeEditor(DateTimeEditor.DATETIME, DateFormat.FULL);
		field0.showDialog();
		
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(panel);
		final DateTimeEditor field = new DateTimeEditor(DateTimeEditor.DATETIME, DateFormat.FULL);
		panel.add(field, BorderLayout.NORTH);

		JPanel buttonBox = new JPanel(new GridLayout(2, 2));
		JButton showDateButton = new JButton("Show Date");
		buttonBox.add(showDateButton);

		final JComboBox timeDateChoice = new JComboBox();
		timeDateChoice.addItem("Time");
		timeDateChoice.addItem("Date");
		timeDateChoice.addItem("Date/Time");
		timeDateChoice.setSelectedIndex(2);
		timeDateChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				field.setTimeOrDateType(timeDateChoice.getSelectedIndex());
			}
		});
		buttonBox.add(timeDateChoice);

		JButton toggleButton = new JButton("Toggle Enable");
		buttonBox.add(toggleButton);
		showDateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println(field.getDate());
			}
		});
		toggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				field.setEnabled(!field.isEnabled());
			}
		});
		panel.add(buttonBox, "South");

		final JComboBox lengthStyleChoice = new JComboBox();
		lengthStyleChoice.addItem("Full");
		lengthStyleChoice.addItem("Long");
		lengthStyleChoice.addItem("Medium");
		lengthStyleChoice.addItem("Short");
		lengthStyleChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				field.setLengthStyle(lengthStyleChoice.getSelectedIndex());
			}
		});
		buttonBox.add(lengthStyleChoice);

		frame.pack();
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getWidth() / 2, dim.height / 2 - frame.getHeight() / 2);
		frame.setVisible(true);
	}
}

class Spinner extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int m_orientation = SwingConstants.VERTICAL;

	private BasicArrowButton m_incrementButton;

	private BasicArrowButton m_decrementButton;

	public Spinner() {
		createComponents();
	}

	public Spinner(int orientation) {
		m_orientation = orientation;
		createComponents();
	}

	public void setEnabled(boolean enable) {
		m_incrementButton.setEnabled(enable);
		m_decrementButton.setEnabled(enable);
	}

	public boolean isEnabled() {
		return (m_incrementButton.isEnabled() && m_decrementButton.isEnabled());
	}

	protected void createComponents() {
		if (m_orientation == SwingConstants.VERTICAL) {
			setLayout(new GridLayout(2, 1));
			m_incrementButton = new BasicArrowButton(SwingConstants.NORTH);
			m_decrementButton = new BasicArrowButton(SwingConstants.SOUTH);
			add(m_incrementButton);
			add(m_decrementButton);
		} else if (m_orientation == SwingConstants.HORIZONTAL) {
			setLayout(new GridLayout(1, 2));
			m_incrementButton = new BasicArrowButton(SwingConstants.EAST);
			m_decrementButton = new BasicArrowButton(SwingConstants.WEST);
			add(m_decrementButton);
			add(m_incrementButton);
		}
	}

	public JButton getIncrementButton() {
		return (m_incrementButton);
	}

	public JButton getDecrementButton() {
		return (m_decrementButton);
	}
}
