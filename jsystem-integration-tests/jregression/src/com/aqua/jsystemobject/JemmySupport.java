package com.aqua.jsystemobject;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.guiMapping.JsystemMapping;
import jsystem.utils.StringUtils;

import org.netbeans.jemmy.DialogWaiter;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;
import org.netbeans.jemmy.operators.JSpinnerOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.Operator.StringComparator;

/**
 * 
 * @author Yaron Addar
 *
 * This class to written to create a generic support for working with Jemmy support
 * this class is used to operate a GUI written in swing automatically 
 *  
 */
public class JemmySupport{
	JsystemMapping jmap;	
	
	
	JFrameOperator mainFrame;
	int retryNumber  = 1 ;
	int timeOut = 5000 ;
	String returnString = "";

	private boolean silence = false;
	
	private static Logger log = Logger.getLogger(JemmySupport.class.getName());
	
	public JemmySupport(){
		super();
		jmap = JsystemMapping.getInstance();
	}
	
/**
 * Metod name: pushButton
 * Descriptin: This method is designed to push a button using the the jemmy
 *			   support, The method also activates a retry mechanisem until the button is successfully pressed.
 * Params:
 * 				@param windowName - The window the button exist in (Lable of the window)
 * 				@param buttonName The button name to push
 * 				@throws Exception
 * 
 * Retrun Values: None
 * Author: Yaron Addar
 */
	public void pushButton(ContainerOperator mainFrame , String buttonName) throws Exception {
		int i=0 ;
		
		JButtonOperator JB = new JButtonOperator(mainFrame , new TipNameButtonFinder(buttonName) ) ;
		//keep clicking the mouse till has an effect
			while ( JB.isEnabled() && i < retryNumber ){
				this.report("Pushing button "+buttonName+" Attempt-"+i);
				JB.clickMouse();
				Thread.sleep(1000);
				i++;
			}
			
			if ( i == retryNumber && JB.isEnabled() ){ 		// If the button was not pressed successfully
				this.report("Pushing button "+buttonName+" failed");
			}
	}

	/**
	 * finds a specific item in all popupmenu items by text comparison, 
	 * and pushes the relevant menu item.
	 */
	public void pushMenuItem(JPopupMenuOperator pp, String txt) throws Exception{
			final String lText = txt;	
				pp.pushMenu(new String[] { lText }, new StringComparator() {
				public boolean equals(String caption, String match) {
					return caption.toLowerCase().startsWith(lText.toLowerCase());
				}
			});
	}
	
	
	/**
	 * Metod name: pushDialogButton
	 * Descriptin: This method is designed to push a button in an dialod window using the the jemmy
	 *			   support, The method also activates a retry mechanisem until the button is successfully pressed.
	 * Params:
	 * 				@param mainFrame - The dialog name  the button exist in (Lable of the window)
	 * 				@param buttonName The button name to push
	 * 				@param waitForClose - if set to true , the function will wait for the window to be closed.
	 * 				@throws Exception
	 * 
	 * Retrun Values: None
	 * Author: Yaron Addar
	 */
		public void pushDialogButton(JDialogOperator mainFrame , String buttonName , boolean waitForClose) throws Exception {
			int i=0 ;
						
			JButtonOperator JB = new JButtonOperator(mainFrame , new TipNameButtonFinder(buttonName) ) ;
		
				while ( JB.isEnabled() && i < retryNumber ){
					this.report("Pushing button "+buttonName+" Attempt-"+i);
					JB.clickMouse();
					Thread.sleep(1000);
					i++;
				}
				
				if ( i == retryNumber ) 		// If the button was not pressed successfully
					this.report("Pushing button "+buttonName+" failed");
				

				if (waitForClose == true){             			//In case we expect that dialog will close after 
					try{										//the button was pushed, if the dialog still exist 
						i=0; 									//An Attempt do activate the button again will be activated.
						boolean dialogOn = mainFrame.isActive();
						
							while(dialogOn && i < 5){
								this.report("Dialog - "+mainFrame+" still exist");
								JB.clickMouse();
								Thread.sleep(2000);
								dialogOn = mainFrame.isActive();
								i++ ;
							}
							
					}catch ( Exception e){
						this.report("button-"+buttonName+" was not pushed");
					}
				}
		}

	/**
	 * Method name: pushButtonAndWaitForDialog
	 * Description:  This method is designed to push a button and for a dialog window to be opened.
	 *			   support, The method also activates a retry mechanism until the dialog is successfully opened.
	 * Params:
	 * 				@param windowName - The window the button exist in (Label of the window)
	 * 				@param buttonName -  The button name to push
	 * 				@param mainWindow - The dialog window that will be opened. 
	 * 				@throws Exception
	 * 
	 * Return Values: None
	 * Author: Yaron Addar
	*/
	public void pushButtonAndWaitForDialog(String mainWindow,String buttonName,String windowName)throws Exception{
		pushButtonAndWaitAndApproveDialog(mainWindow,buttonName,windowName,null);
	}

	public void pushButtonAndWaitAndApproveDialog(String mainWindow,String buttonName,String windowName,String dialogButton)throws Exception{
		mainFrame = new JFrameOperator(mainWindow);     	//Pressing the button
		JButtonOperator btn = new JButtonOperator(mainFrame , new TipNameButtonFinder(buttonName));
		btn.setEnabled(true);
		log.info(("button is enabled = "+btn.isEnabled()));
		report(("button is enabled = "+btn.isEnabled()));
		btn.push();
		sleep(500);
		JDialogOperator dialog = new JDialogOperator(windowName);
		dialog.setModal(true);
		if (!dialog.isActive()){
			this.report("The Dialog "+windowName+" was not opened successfully.  is visible="+dialog.isVisible());
			Thread.sleep(1000);
			this.report("The Dialog "+windowName+" after 1 second sleep active= " + dialog.isActive() + " is visible="+dialog.isVisible());
		}else {
			this.report("The Dialog "+windowName+" was opened successfully");
		}
		if (dialogButton != null) {
			this.report("----------------------------- pressing button " + dialogButton);
			new JButtonOperator(dialog , new TipNameButtonFinder(dialogButton)).push();
			this.report("----------------------------- button prssed " + dialogButton);
		}
		
	}

	
	/**
	 * Method name: chooseMenuItemAndWaitForDialog
	 * Description:  This method is designed to choose a menu item and wait for a dialog window to be opened.
	 *			   support, The method also activates a retry mechanism until the dialog is successfully opened.
	 * Parameters:
	 * 				@param windowName - The window  that holds the menu (Label of the window)
	 * 				@param buttonName -  The menu item name to be selected
	 * 				@param mainWindow - The dialog window that will be opened. 
	 * 				@throws Exception
	 * 
	 * Return Values: None
	*/
	public JDialogOperator chooseMenuItemAndWaitForDialog(JFrameOperator mainFrame, String menuName, String menuItemName, String dialogName) throws Exception {
		chooseMenuItem(mainFrame, menuName, menuItemName);

		JDialogOperator dialog = new JDialogOperator(dialogName);
		if (!dialog.isActive()){
			report("The Dialog " + dialogName + " was not opened successfully.  is visible = " + dialog.isVisible());
			Thread.sleep(1000);
			report("The Dialog " + dialogName + " after 1 second sleep active = " + dialog.isActive() + " is visible = " + dialog.isVisible());
			return dialog;
		} else {
			report("The Dialog " + dialogName + " was opened successfully");
			return dialog;
		}
	}

	/* Method name: chooseMenuItem
	 * Description:  This method is designed to choose an item from the menu, regardless of its 
	 *               depth. Wait 1000 milliseconds between each level
	 * Parameters:
	 * 				@param mainFrame - The main frame
	 * 				@param menuNames - Array of menu titles.
	 * 				@throws Exception
	 * 
	 * Return Values: JMenuItemOperator
	 */
	public JMenuItemOperator chooseMenuItem (JFrameOperator mainFrame, Object...menuNames) throws Exception {
		return chooseMenuItem(mainFrame, true, menuNames);
	}
	
	public JMenuItemOperator chooseMenuItem (JFrameOperator mainFrame, boolean pushLastLevel, Object...menuNames) throws Exception {
		int waitBetweenItems = 1000;
		JMenuItemOperator selectedItem = null;
		
		for (int index = 0; index < menuNames.length; index++) {
			
			String name = (String)menuNames[index];
			selectedItem = new JMenuItemOperator(mainFrame, name);
			// Push every level, and the last level only if asked
			if ((index != menuNames.length-1) || pushLastLevel) {
				// Pushing "no block" since in some cases we just want to see the
				// sub menu and not make an action
				selectedItem.pushNoBlock();
			}
			Thread.sleep(waitBetweenItems);
		}
		return selectedItem;
	}
	
/**
 * Method name: setCheckBox
 * Description:  This method sets checkbox condition to on or off. The method also activates 
 * 				a retry mechanism until successful
 * 
 * Params:
 * 			@param condition  - true to set it on , false to set it to off
 * 			@param windowName - The window name the checkbox exist in
 * 			@param checkBoxName - The number of the checkbox in the window.
 * 			@throws Exception
 * Retrun Values: None
 * Author: Yaron Addar
 */	
	public void setCheckBox(boolean condition , String windowName, int checkBoxName) throws Exception {
		
		int i=0;
		mainFrame = new JFrameOperator(windowName);
		JCheckBoxOperator jc = new JCheckBoxOperator(mainFrame, checkBoxName);


		if ( condition == true && !jc.isSelected()) {   // if the checkbux needs to be selected and currentlly is not selected
					
				while (!jc.isSelected()&& i < 5){
					this.report("Setting checkox "+checkBoxName+ " to on attempt-"+i);
					jc.clickMouse();
					Thread.sleep(1000);
					i++ ;
				}
			
		}
		else if ( condition == false && jc.isSelected()) {   // if the checkbux needs to be unselected and currentlly is selected
			
			while (jc.isSelected()&& i < retryNumber){
				this.report("Setting checkox "+checkBoxName+ " to off attempt-"+i);
				jc.clickMouse();
				Thread.sleep(1000);
				i++ ;
			}
		}

		if (i == retryNumber)              //If the opearation has failed
			this.report("setting the checkbox "+checkBoxName+"to "+condition+" has failed");
	
	}
/**
 * Metod name: setTableCell
 * Descriptin:  This method sets a table cell value, this method knows how to set a combo box whithin 
 * 				a table cell and knows how distiguish between a regular cell and a cell with a combobox
 * 
 * Params: 
 * 			@param paramTable - The table that is going to be initilized 
 * 			@param row - The row of the cell that is going the to be initilized 
 * 			@param column - The column of the cell that is going the to be initilized 
 * 			@param value - The value that is going to be set
 * 			@param isCombo - A Boolean that tells the method wether the cell has a combo box or not
 * 			@throws Exception
 * Retrun Values: None
 * Author: Yaron Addar
 */
	public void setTableCell (JTableOperator paramTable, int row , int column , String value, boolean isCombo) throws Exception {
		int i = 0;
		String currentValue = "";

		while ( (! currentValue.equals(value) ) && (i < retryNumber) ){
			if (isCombo){
				paramTable.clickOnCell(row, column);
				JComboBoxOperator combo = new JComboBoxOperator(paramTable);
				combo.setEnabled(true);
				combo.selectItem(value);
			}	
			else{
				paramTable.clickOnCell(row, column);
				paramTable.changeCellObject(row, column , value);
			}	

			Thread.sleep(1000);
			report("----- Setting Table cell,Row="+row+",column="+column+",value="+value+"-attempt "+i);
			currentValue = paramTable.getValueAt(row, column).toString();
			i++;
		}
		if ( currentValue.equalsIgnoreCase(value) ) {
			report("Table was updated successfully");
		} else {
			report("Fail to update table");
		}
	}

	/**
	 * expand a tree node and select it
	 * 
	 * @param tree	the tree operator to choose in
	 * @param node	the Tree node NAME to choose
	 * @return	
	 * @throws Exception
	 */
	public int selectInTree(JTreeOperator tree, String node) throws Exception {
		tree.expandRow(0);
		int c = tree.getRowCount();
		TreePath foundPath = null;

		for (int i = 0; i < c; i++) {
			TreePath path = tree.getPathForRow(i);
			System.out.println("Path: " + path);
			Object[] pathElements = path.getPath();
			if (pathElements == null || pathElements.length == 0) {
				continue;
			}
			if (node.equals(pathElements[pathElements.length - 1]
			                             .toString())) {
				foundPath = path;
				break;
			}

		}
		if (foundPath == null) {
			throw new Exception("Path not found node: " + node);
		}

		Thread.sleep(500);
		if (foundPath != null) {
			tree.expandPath(foundPath);
			tree.selectPath(foundPath);
		}

		return 0;
	}


	/**
	 * push a button identified by a String\Index
	 * 
	 * @param operator	the operator the button is inside
	 * @param identifier	the name\Index of the button
	 * @return isEnabled return false if disabled
	 * @throws Exception 
	 */
	public Boolean pushButton(ContainerOperator operator, Object identifier) throws Exception {
		Boolean state = isButtonEnabled(operator, identifier);
		if (state) {
			JButtonOperator button = getButtonOperator(operator, identifier);
			button.push();
		}
		return state;
	}
	/**
	 * Return state of a button identified by a String\Index
	 * 
	 * @param operator	the operator the button is inside
	 * @param identifier	the name\Index of the button
	 * @return isEnabled return false if disabled
	 * @throws Exception 
	 */
	public Boolean isButtonEnabled(ContainerOperator operator, Object identifier) throws Exception {
		JButtonOperator button = getButtonOperator(operator, identifier);
		if (!checkOperator(button, identifier, "Button is disabled!", identifier)){
			return false;
		}
		return true;
	}
	
	/**
	 * locate a button component by a given name or index and return it's operator
	 * 
	 * @param operator	the operator to search in
	 * @param identifier	the name\index to locate radioButton by
	 * @return
	 * @throws Exception 
	 */
	public JButtonOperator getButtonOperator(ContainerOperator operator,Object identifier) throws Exception{
		JButtonOperator button = null;
		if (identifier instanceof String && identifier != null){
			button = new JButtonOperator(operator,new TipNameButtonFinder((String)identifier));
			System.out.println(">>> DEBUG: button is null: "+(button == null));
		}else if(identifier != null){///int
			button = new JButtonOperator(operator,Integer.parseInt(identifier.toString()));
		}else if (identifier == null){
			throw new Exception("the given identifier is null");
		}
		return button;
	}
	
	
	/**
	 * Locate a JSpinner component by a given name or index and return it's operator
	 * 
	 * @param operator	the operator to search in
	 * @param identifier	the name\index to locate JSpinner by
	 * @return
	 */
	public JSpinnerOperator getSpinnerOperator(ContainerOperator operator,Object identifier){
		JSpinnerOperator spinner = null;
		if (identifier instanceof String){
			spinner = new JSpinnerOperator(operator,new RunnerComponentChooser((String)identifier));
		}else{ // int
			spinner = new JSpinnerOperator(operator,Integer.parseInt(identifier.toString()));
		}
		return spinner;
	}
	
	/**
	 * Locate a JSpinner component by a given name or index and return it's operator
	 * and sets his value with the given value.
	 *   
	 * @param operator
	 * @param identifier
	 * @param value
	 */
	public void setSpinnerValue(ContainerOperator operator,Object identifier,Object value){
		JSpinnerOperator spinner = getSpinnerOperator(operator, identifier);
		if (!checkOperator(spinner, identifier, "Spinner is disabled!", identifier)){
			return;
		}
		spinner.setValue(value);
	}
	
	/**
	 * check/uncheck a checkbox
	 * 
	 * @param operator	the oprrator the checkbox is inside
	 * @param identifier	checkbox name\index
	 * @param enable	True will check, False will uncheck
	 */
	public void setCheckBox(ContainerOperator operator,Object identifier,boolean enable){
		JCheckBoxOperator checkBox = getCheckBoxOperator(operator, identifier);
		String check = enable? "check" : "uncheck";
		if (!checkOperator(checkBox, identifier, "Check box is disabled!", identifier+"\nTried to "+check)){
			return;
		}
		checkBox.setSelected(enable);
	}
	
	/**
	 * locate a Checkbox component by a given name or index and return it's operator
	 * 
	 * @param operator	the operator to search in
	 * @param identifier	the name\index to locate radioButton by
	 * @return
	 */
	public JCheckBoxOperator getCheckBoxOperator(ContainerOperator operator,Object identifier){
		JCheckBoxOperator checkBox = null;
		if (identifier instanceof String){
			checkBox = new JCheckBoxOperator(operator,(String) identifier);
		}else { // int
			checkBox = new JCheckBoxOperator(operator,Integer.parseInt(identifier.toString()));
		}
		return checkBox;
	}

	/**
	 * get a JtextField operator
	 * 
	 * @param operator	the container to search in
	 * @param labelText	the label to search before the text field
	 * @param indexNum	how many JTextFields to skip
	 * @return	the JTextField found
	 */
	public JTextFieldOperator getJTextFieldComponent(ContainerOperator operator, String labelText, int indexNum){
		return (JTextFieldOperator)getComponentOperator(operator, labelText, JTextField.class, JTextFieldOperator.class,indexNum);
	}

	/**
	 *  set a text in the first JTextField operator found after the given label
	 * @param operator	the container to search in
	 * @param labelText	the label before the JTextField
	 * @param value	the value to insert
	 */
	public void setTextInFieldOperator(ContainerOperator operator, String labelText,Object value){
		setTextInFieldOperator(operator, labelText, value, 0);
	}

	public void setTextInComboBoxOperator(ContainerOperator operator, String labelText,Object value){
		JComboBoxOperator combo = ((JComboBoxOperator)(getComponentOperator(operator, labelText,JComboBox.class,JComboBoxOperator.class,0)));
		combo.getEditor().setItem(value);
	}

	/**
	 * set a text in a JTextField operator
	 * 
	 * @param operator	the container to search in
	 * @param labelText	the label before the JTextField
	 * @param value	the value to insert
	 * @param indexNum	how many JTextFields to skip
	 */
	public void setTextInFieldOperator(ContainerOperator operator, String labelText,Object value, int indexNum){
		JTextFieldOperator txtField = getJTextFieldComponent(operator, labelText, indexNum);
		if (!checkOperator(txtField, labelText,"Text Field is disabled!" , labelText+"\nTried to insert value "+value)){
			return;
		}
		txtField.setText(value.toString());
	}

	/**
	 * select a given value in a combobox operator
	 * 
	 * @param operator	the operator to search the combobox in
	 * @param txt	the combobox title
	 * @param value	the value to set
	 */
	public void selectInComboBox(ContainerOperator operator, String txt,Object value){
		selectInComboBox(operator, txt, value, 0);
	}

	/**
	 * select a given value in a combobox operator
	 * 
	 * @param operator	the operator to search the combobox in
	 * @param txt	the combobox title
	 * @param value	the value to set
	 * @param indexNum	how many operators to skip
	 */
	public void selectInComboBox(ContainerOperator operator, String txt,Object value, int indexNum){
		JComboBoxOperator combo = ((JComboBoxOperator)(getComponentOperator(operator, txt,JComboBox.class,JComboBoxOperator.class,indexNum)));
		if (!checkOperator(combo, txt,"Combo box is disabled!" , txt+"\nTried to set to "+value)){
			return;
		}
		if (value instanceof String){
			if (!combo.getSelectedItem().toString().equals(value)){
				combo.selectItem(value.toString());
			}
		}else{
			int val = Integer.parseInt(value.toString());
			if (combo.getSelectedIndex() != val){
				combo.selectItem(val);
			}
		}
	}


	/**
	 * locate a radio button by a given identifier (name\index)
	 * 
	 * @param operator	the operator to search in
	 * @param identifier	the name\index to locate radioButton by
	 */
	public void pushRadioButton(ContainerOperator operator, Object identifier){
		JRadioButtonOperator button = getRadioButtonOpertaor(operator, identifier);
		if (!checkOperator(button, identifier, "Button is disabled!", identifier)){
			return;
		}
		button.push();
	}
	
	/**
	 * locate a radio button by a given String or int and return the operator to it
	 * @param operator	the container to look inside
	 * @param identifier	the text\index number
	 * @return
	 */
	public JRadioButtonOperator getRadioButtonOpertaor(ContainerOperator operator, Object identifier){
		JRadioButtonOperator button = null;
		if (identifier instanceof String){
			button = new JRadioButtonOperator(operator,(String) identifier);
		}else{ // int
			button = new JRadioButtonOperator(operator,Integer.parseInt(identifier.toString()));
		}
		return button;
	}
	
	/**
	 * find a table and select a cell in it. double click if requested 
	 * 
	 * @param operator	the operator to search in
	 * @param fieldLabel	the column name
	 * @param value			the value to search in the column cells
	 * @param doubleClick	if True will double click the cell, False will only select
	 */
	public void selectInTable(ContainerOperator operator, String fieldLabel,Object value,boolean doubleClick){
		JTableOperator table = new JTableOperator(operator);
		int column = table.findColumn(fieldLabel);
		int row = table.findCellRow(value.toString(), column, 0);
		if (doubleClick){
			table.clickOnCell(row, column, 2);
		}else{
			table.selectCell(row, column);
		}
	}
	
	/**
	 * check that a given operator is not null or disabled
	 * @param operator	the operator to check
	 * @param name	the key that was searched
	 * @param title	the title to print if disabled
	 * @param msg	the msg to show
	 * @return	true if operator is not null and not disabled
	 */
	private boolean checkOperator(JComponentOperator operator,Object name,String title, Object msg){
		if (operator == null){
			report("Couldn't find operator "+name+" !!",msg.toString(),Reporter.FAIL);
			return false;
		}
		if (!operator.isEnabled()){
			report(title,msg.toString(),Reporter.WARNING);
			return false;
		}
		return true;
	}

	/**
	 * push a button in a given container and wait for the dialog to open
	 * 
	 * @param operator	the operator to find the button in
	 * @param buttonName	the name of the button to push
	 * @param dialogName	the dialog that should appear
	 * @return	the JDialogOperator of the requested dialog or null if not opened
	 * 
	 * @throws Exception
	 */
	public JDialogOperator pushButtonAndWaitForDialog(ContainerOperator operator,String buttonName,String dialogName)throws Exception{

		pushButton(operator, (Object)buttonName);
		JDialogOperator dialog = new JDialogOperator(dialogName);
		if (dialog==null || !dialog.isActive()){
			report("The Dialog "+dialogName+" was not opened successfully",Reporter.WARNING);
			return null;
		}
		report("The Dialog "+dialogName+" was opened successfully");
		return dialog;
	}

	/**
	 * close a dialog window if opened
	 * 
	 * @param dialogName	the dialog window title
	 * @param confirmButton	the confirm button text
	 * @return	true if a dialog was opened, false if none opened
	 * @throws Exception
	 */
	public boolean ConfirmDialogIfExists(String dialogName, String confirmButton)throws Exception{
		JDialogOperator dialog = getDialogIfExists(dialogName, 2);
		if (dialog!=null){
			pushButton(dialog, confirmButton);
			return true;
		}
		return false;
	}
	
	/**
	 * get a JDialogOperator object if it is open
	 * 
	 * @param title	the Dialog title
	 * @param secondsToWait	how many seconds to wait for the dialog to appear
	 * @return
	 */
	public JDialogOperator getDialogIfExists(String title,int secondsToWait){
		DialogWaiter waiter = new DialogWaiter();
		waiter.getTimeouts().setTimeout("DialogWaiter.WaitDialogTimeout", secondsToWait * 1000);
		JDialogOperator dialog;
		try{
			waiter.waitDialog(new RunnerComponentChooser(title));
			dialog = new JDialogOperator(new RunnerComponentChooser(title));
		}catch (Exception e) {
			return null;
		}
		return dialog;
	}

	/**
	 * get the component operator for a given swing component
	 * 
	 * @param operator	the operator to search inside
	 * @param txt	the text to search
	 * @param jcomponentClass	the swing component class
	 * @param operatorClass		the Jemmy component class
	 * @return	the first Jemmy operator matching the requested component
	 */
	@SuppressWarnings("unused")
	private ComponentOperator getComponentOperator(ContainerOperator operator, String txt, Class<?> jcomponentClass,Class<?> operatorClass) {
		return getComponentOperator(operator, txt, jcomponentClass, operatorClass,0);
	}

	/**
	 * get the component operator for a given swing component
	 * 
	 * @param operator	the operator to search inside
	 * @param txt	the text to search
	 * @param jcomponentClass	the swing component class
	 * @param operatorClass		the Jemmy component class
	 * @param indexNumber		how many matching swing components to skip
	 * @return	the Jemmy operator matching the requested component
	 */
	@SuppressWarnings("unchecked")
	public ComponentOperator getComponentOperator(ContainerOperator operator, String txt, Class<?> jcomponentClass,Class operatorClass,int indexNumber) {
		Component c = locateComponent(operator, txt);
		if (c == null){
			return null;
		}
		Container parent = c.getParent();
		Component[] components = parent.getComponents();
		for (int i=0 ; i<components.length ; i++){
			// find Label Component
			if (components[i].equals(c)){
				for ( ; i<components.length ; i++){
					if (jcomponentClass.isInstance(components[i])){
						indexNumber--;
						if (indexNumber>=0){
							continue;
						}
						try {
							return (ComponentOperator) operatorClass.getConstructor(jcomponentClass).newInstance(components[i]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * find a container and return it's operator object
	 * @param container	the container to search in
	 * @param name	the container to find name
	 * @return	the Jemmy operator for the requested container
	 */
	public ContainerOperator getContainerOperator(ContainerOperator container,String name){
		Component panel = locateComponent(container, name);
		if (panel!=null && panel instanceof Container){
			return new ContainerOperator((Container) panel);
		}
		return null;
	}

	/**
	 * locate a component by name, inside a given container
	 * @param operator	the container to look inside
	 * @param text	the text to find
	 * @return
	 */
	public Component locateComponent(ContainerOperator operator, String text){
		return operator.findSubComponent(new RunnerComponentChooser(text));
	}
	
	/**
	 * checks if the dialog exists. if found, check if it is still visible in 1 second intervals
	 * 
	 * @param dialogTitle	the dialog title
	 * @throws Exception
	 */
	public void WaitForDialogToClose(String dialogTitle)throws Exception{
		JDialogOperator processDialog = getDialogIfExists(dialogTitle, 3);
		if (processDialog != null){
			while (processDialog.isVisible()){
				sleep(1000);
			}
			return;
		}
	}
	

	/**
	 * init the return String (the String returned to the client)
	 */
	protected void initReturnString(){
		returnString = "";
	}

	protected void print(Object o){
		returnString +=o;
	}

	protected void println(Object o){
		returnString +=o+"\n";
	}

	protected void println(){
		returnString +="\n";
	}

	/**
	 * report something to the reporter
	 * @param toReport	the title to report
	 */
	public void report(String toReport){
		report(toReport,null,Reporter.PASS);
	}

	/**
	 * report something to the reporter with the given status
	 * @param toReport	the title to report
	 * @param status	Reporter.Pass\Fail\Warning
	 */
	public void report(String toReport,int status){
		report(toReport,null,status);
	}
	
	public void report(String title,String msg,int status){
		if (silence){
			return;
		}
		title = "Server :"+title;
		
		if (!JServer.isRunnerActive){
			log.info(title);
			return;
		}
		
		if (StringUtils.isEmpty(msg)){
			ListenerstManager.getInstance().report(title,status);
		}else{
			ListenerstManager.getInstance().report(title,msg,status);
		}
	}

	protected void step(String title){
		if (!JServer.isRunnerActive){
			log.info(title);
			return;
		}
		ListenerstManager.getInstance().step(title);
	}

	protected void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected String getReturnString(){
		return returnString;
	}

	public void setSilence(boolean silence) {
		this.silence = silence;
	}
	
	/**
	 * Return a reference to a JTabbedPaneOperator within the given father component
	 * @param mainFrame - The Father of the tab 
	 * @param tabName - The tab title
	 * @return a reference to a JTabbedPaneOperator
	 * @throws Exception
	 */
    public JTabbedPaneOperator getJTableFromTab(JDialogOperator mainFrame, String tabName) throws Exception {
    	JTabbedPaneOperator tabbedPaneOperator = new JTabbedPaneOperator(mainFrame, 0);
    	tabbedPaneOperator.selectPage(tabName); 
    	return tabbedPaneOperator;  
    } 
    
    /**
     * This method search for a String key in a table according to the given table, column, and string key, and 
     * return the index (Row) of this key in the table.
     * @param tableOperator - The table to search in
     * @param key - The String to be found
     * @param keyColumn - The column of the key to be search.
     * @return - the row of the given key, in the table 
     */
    public int getTableRowIndexOfValue(JTableOperator tableOperator, String key, int keyColumn) {
    	String value;
    	
    	for (int row = 0; row < tableOperator.getRowCount(); row++) {
    		value = (String)tableOperator.getValueAt(row, keyColumn);
    		if ( value.equals(key) ) {
    			return row;
    		}
    	}
    	return -1;
    }
    
    
    // Needs to be checked and refactored by DAN (Dror)
 
    /**
	 * THis method open a file chooser, and change the directory according to the given newValue parameter
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property
	 * @param newValue - the path of the directory to be selected
     * @throws Exception
     */    
	public void changeDirectory(JDialogOperator dialog, JTableOperator  jTableOperator, int row, String newValue) throws Exception {
		File directory = new File(newValue);
		operateFileChooser(dialog, jTableOperator, row, directory, null);
	}
	
	
	/**
	 * 
	 * THis method open a file chooser, change directory, and select a file from the given directory
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property
	 * @param newValue - the path of the file to be selected
	 * @throws Exception
	 */
	public void changeFile(JDialogOperator dialog, JTableOperator jTableOperator, int row, String newValue) throws Exception {
		File fullPathFileName = new File(newValue);
		String fileName = fullPathFileName.getName();
		String directoryPath = fullPathFileName.getPath();
		File directory = new File(directoryPath);
		operateFileChooser(dialog, jTableOperator, row, directory, fileName);
	}
	
	/**
	 * THis method open a file chooser, change directory, and in case we are looking for a file - select a file from the given directory
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property
	 * @param directory - The directory to be set
	 * @param fileName - The file to be selected
	 * @throws Exception
	 */
	public void operateFileChooser(JDialogOperator dialog, JTableOperator  jTableOperator, int row, File directory, String fileName) throws Exception {

		openContentEditor(dialog, jTableOperator, row);
		JFileChooserOperator fileChooser = new JFileChooserOperator();
		fileChooser.setCurrentDirectory(directory);
		Thread.sleep(5000);
		
		if (fileName != null) {
			fileChooser.chooseFile(fileName);
		}
		
		JButtonOperator selectButton = new JButtonOperator(fileChooser, fileChooser.getApproveButtonText());
		selectButton.clickMouse();
	}
	
	
	/**
	 * This method operate all properties that need a Multi-select list, search_and_multi_select_list.
	 * After opening ListDialog, this method select an item from the list according to the newValue parameter
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property

	 */
	public void changeMultiSelectList(JDialogOperator dialog, JTableOperator jTableOperator, int row, String[] newValues) throws Exception {
		openContentEditor(dialog, jTableOperator, row);
		int[] newSelectedIndexes = new int[newValues.length];
		
		if (dialog != null) {
			JListOperator listOperator = new JListOperator(dialog);
			if (listOperator != null) {
				for (int generalCounte = 0; generalCounte < newValues.length; generalCounte++) {
					int newIndex = listOperator.findItemIndex(newValues[generalCounte]);
					newSelectedIndexes[generalCounte] = newIndex;
				}
				// Clear selection
				listOperator.clearSelection();
				
				// Select all items that were not selected
				listOperator.setSelectedIndices(newSelectedIndexes);
			}
		}
		JButtonOperator setButton = new JButtonOperator(dialog, jmap.getListDialogSetButtonName());
		setButton.press();
	}
	
	/**
	 * This method operate all properties that need a Multi-select list, search_and_list, search_adn_multi_select_list.
	 * After opening ListDialog, this method select an item from the list according to the newValue parameter
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property
	 * @throws Exception
	 */
	public void changeSearchAndList(JDialogOperator dialog, JTableOperator jTableOperator, int row, String newValue) throws Exception {
		openContentEditor(dialog, jTableOperator, row);
		
		JListOperator listOperator = new JListOperator(dialog);
		if (listOperator != null) {
			int counter = listOperator.getModel().getSize();
			if (counter > 1) {
				int newIndex = listOperator.findItemIndex(newValue);
				listOperator.setSelectedIndex(newIndex);
			} else {
				listOperator.setSelectedIndex(0);
			}
		}
		JButtonOperator setButton = new JButtonOperator(dialog, jmap.getListDialogSetButtonName());
		setButton.press();
	}

	/**
	 * This method operate all properties that need a separate dialog in order to change them, such as:
	 * Directory or file that are using a fileChooser dialog.
	 * Multi-select list, search_and_list, search_adn_multi_select_list - that are using the ListDialog.
	 * This method press the ("...") button in order to load the compatible dialog
	 * @param dialog - The dialog that holds the JTable
	 * @param jTableOperator - The Table that holds the current property
	 * @param row - The row that contain the current property
	 * @throws Exception
	 */
	public void openContentEditor(JDialogOperator dialog, JTableOperator jTableOperator, int row) throws Exception {
		jTableOperator.clickOnCell(row, 2);
		
		// Click the ("...") button in order to open the file chooser dialog
		pushButtonAndWaitForDialog(dialog, "...", jmap.getJSystemPropertyFileChooserName());
	}

    // end of "Needs to be checked"
	
}