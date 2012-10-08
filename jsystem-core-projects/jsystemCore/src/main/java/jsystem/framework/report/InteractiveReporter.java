/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

public interface InteractiveReporter {

    /**
     * Brings up a dialog where the number of choices is determined
     * by the <code>optionType</code> parameter, where the
     * <code>messageType</code>
     * parameter determines the icon to display.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param title     the title string for the dialog
     * @param message   the text to display
     * @param optionType an integer designating the options available
     *                   on the dialog: <code>YES_NO_OPTION</code>,
     *                  <code>JOptionPane.YES_NO_CANCEL_OPTION</code>,
     *                  or <code>JOptionPane.OK_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is; 
     *                  primarily used to determine the icon from the pluggable
     *                  Look and Feel: <code>JOptionPane.ERROR_MESSAGE</code>,
     *			<code>JOptionPane.INFORMATION_MESSAGE</code>, 
     *                  <code>JOptionPane.WARNING_MESSAGE</code>,
     *                  <code>JOptionPane.QUESTION_MESSAGE</code>,
     *			or <code>JOptionPane.PLAIN_MESSAGE</code>
     * @return an integer indicating the option selected by the user
     */
	public int showConfirmDialog(String title, String message, int optionType, int messageType);

}
