/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import jsystem.guiMapping.JsystemMapping;

/**
 * Use this modal dialog to let the user choose one or more strings from a list
    String[] choices = {"A", "long", "array", "of", "strings"};
    String selectedName = ListDialog.showDialog(
                                componentInControllingFrame,
                                locatorComponent,
                                "A description of the list:",
                                "Dialog Title",
                                choices,
                                mode (single/multiple selection)
                                oldSelections);
 * </pre>
 */
public class JSystemPropertiesListDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JSystemPropertiesListDialog dialog;
    private static String value = "";
    private static Object[] userMultipleSelection = null;
    private JList list;

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static String showDialog(Component frameComp, Component locationComp, String labelText, String title, String[] possibleValues, int mode, String oldSelections) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new JSystemPropertiesListDialog(frame, locationComp, labelText, title, possibleValues, mode, oldSelections);
        dialog.setVisible(true);
        return value;
    }


    /**
     * 
     * @param frame - componentInControllingFrame
     * @param locationComp - the location of the dialog compare to the frame it is corelated to
     * @param labelText - A description of the property value
     * @param title -The title of the dialog (receive the property string name)
     * @param data - an array holding all the list options
     * @param mode - Singel / multi selection
     * @param oldSelections / the last values that were selected (The values apear in the table)
     */
    private JSystemPropertiesListDialog(Frame frame, Component locationComp, String labelText, String title, Object[] data, int mode, String oldSelections) {
        super(frame, title, true);
        setName(JsystemMapping.getInstance().getListDialogName());
        //Create and initialize the buttons.
        JButton cancelButton = new JButton(JsystemMapping.getInstance().getListDialogCancelButtonName());
        cancelButton.addActionListener(this);
        //
        final JButton setButton = new JButton(JsystemMapping.getInstance().getListDialogSetButtonName());
        setButton.setActionCommand(JsystemMapping.getInstance().getListDialogSetButtonName());
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //main part of the dialog
        list = new JList(data) 
        {
			private static final long serialVersionUID = 1L;

			//Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
            public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL && direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0)) {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(visibleRect, orientation, direction);
            }
        };

        list.setSelectionMode(mode);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick(); //emulate button click
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(500, 200));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        setSelectionToMultipleItems(oldSelections);
        pack();
        setLocationRelativeTo(locationComp);
    }

    
    /**
     * Select some of the list items according to the oldSelection parameter that holds the last user selections
     * @param oldSelections - A String holding the last selected items
     */
    private void setSelectionToMultipleItems(String oldSelections) {
    	String currentListItem = null;
    	String currentOldItem = null;
    	String[] oldItems = oldSelections.split(";");
    	int[] indexes = new int[oldItems.length];
    	int counter = 0;
    	
    	for (int listItemIndex = 0; listItemIndex < list.getModel().getSize(); listItemIndex++) {
    		currentListItem = (String)list.getModel().getElementAt(listItemIndex);
        	for (int oldSelectionIndex = 0; oldSelectionIndex < oldItems.length; oldSelectionIndex++) {
        		currentOldItem = oldItems[oldSelectionIndex];
        		if ( currentListItem.equals(currentOldItem) ){
        			indexes[counter] = listItemIndex;
        			counter++;
	        	}
        	}
    	}
    	
    	// In case the old selections contain an item that today does not exists in the new list,
    	// The indexes has empty cells
    	if (counter > 0) {
	    	int[] existingIndexes = new int[counter];
	    	for (int i = 0; i < counter; i++) {
	    		existingIndexes[i] = indexes[i];
	    	}
	   		list.setSelectedIndices(existingIndexes);
        }
    }
    

    /**
     * Convert the user selection from Object[] to string
     * @param userSelections - an array of object holding the user selections
     * @return - A string containing all user selections seperated by ";"
     */
    private String convertMultiSelectionObjectsToString(Object[] userSelections) {
    	StringBuffer allSelections = new StringBuffer();
    	String singleSelection = null;
    	String finalResult = null;
    	for (int i = 0; i < userSelections.length; i++) {
    		singleSelection = (String)userSelections[i];
    		allSelections.append(singleSelection);
    		allSelections.append(";");
    	}
    	finalResult = allSelections.substring(0, allSelections.length()-1);
    	return finalResult;
    }
    

    /**
     * Handle clicks on the Set and Cancel buttons.
     */
    public void actionPerformed(ActionEvent e) {
        if ( ( JsystemMapping.getInstance().getListDialogSetButtonName() ).equals(e.getActionCommand())) {
        	userMultipleSelection = list.getSelectedValues();
            JSystemPropertiesListDialog.value = convertMultiSelectionObjectsToString(userMultipleSelection);
        }
        JSystemPropertiesListDialog.dialog.setVisible(false);
    }
}


