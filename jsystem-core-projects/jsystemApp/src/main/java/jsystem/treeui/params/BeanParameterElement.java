package jsystem.treeui.params;

import java.awt.event.ActionListener;

import javax.swing.CellEditor;
import javax.swing.JComponent;


/**
 * a simple class to group all Parameters Bean elements
 * 
 * @author Nizan Freedman
 *
 */
public abstract class BeanParameterElement extends JComponent implements
		ActionListener {

	private CellEditor editor;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6238432034971485671L;

	public void addParameterChangedListener(CellEditor listener){
		this.editor = listener;
	}
	
	protected void parameterChanged(){
		editor.stopCellEditing();
	}

}
