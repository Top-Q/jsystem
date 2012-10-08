package com.aqua.jsystemobject.handlers;

import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

import utils.DBPropertiesKeys;

import com.aqua.jsystemobject.RunnerComponentChooser;

public class JReporterHandler extends BaseHandler {
	String DBPropertiesDialogTitle = "Database Properties";
	public int pressLogButton() throws Exception{
		JButtonOperator btnOp = jemmyOperation.getButtonOperator(mainFrame, jmap.getLogButton());
		btnOp.push();
		return 0;
	}
	
	public boolean isReportsButtonEnabled() throws Exception{
		JButtonOperator op = jemmyOperation.getButtonOperator(mainFrame, jmap.getOpenReportApplicationButton());
		return op.isEnabled();
	}
	
	
	/**
	 * gets the relevant compnent's name from dbproperties dialog
	 * according to the specified key.
	 * @param key
	 * @return
	 */
	private String getComponantNamebyKey(String key){
		if(key.equals(DBPropertiesKeys.BROWSERPORT.toString())){
			return jmap.getDbPropertiesPortTextField();
		}
		if(key.equals(DBPropertiesKeys.DBDRIVER.toString())){
			return jmap.getDbPropertiesDriverCombobox();
		}
		if(key.equals(DBPropertiesKeys.DBHOST.toString())){
			return jmap.getDbPropertiesHostTextField();
		}
		if(key.equals(DBPropertiesKeys.DBNAME.toString())){
			return jmap.getDbPropertiesNameTextField();
		}
		if(key.equals(DBPropertiesKeys.DBTYPE.toString())){
			return jmap.getDbPropertiesTypeJComboBox();
		}
		if(key.equals(DBPropertiesKeys.PASSWORD.toString())){
			return jmap.getDbPropertiesPasswordField();
		}
		if(key.equals(DBPropertiesKeys.SERVERIP.toString())){
			return jmap.getDbPropertiesServerIpTextField();
		}
		if(key.equals(DBPropertiesKeys.USER.toString())){
			return jmap.getDbPropertiesUserTextField();
		}
		else{
			return null;
		}
	}
}
