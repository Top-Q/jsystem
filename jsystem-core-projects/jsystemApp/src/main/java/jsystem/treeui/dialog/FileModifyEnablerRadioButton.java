package jsystem.treeui.dialog;

import javax.swing.JRadioButton;

import jsystem.treeui.dialog.UnmodifiableFileModel.Option;


class FileModifyEnablerRadioButton extends JRadioButton {

	private static final long serialVersionUID = 1L;
	
	private Option option;

	public void setOption(Option option) {
		if (option != null) {
			setText(option.getDescription());
		}
		this.option = option;
	}

	public Option getOption() {
		return option;
	}

}
