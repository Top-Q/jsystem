package com.aqua.jsystemobjects.handlers;

import java.io.File;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

import com.aqua.jsystemobjects.RunnerComponentChooser;
import com.aqua.utils.DBPropertiesKeys;

public class JReporterHandler extends BaseHandler {
	
	private String DBPropertiesDialogTitle = "Database Properties";
	
	/**
	 * returns true if the reports button is enabled.
	 * @return
	 * @throws Exception
	 */
	public boolean isReportsButtonEnabled() throws Exception{
		JButtonOperator op = jemmySupport.getButtonOperator(mainFrame, jmap.getOpenReportApplicationButton());
		return op.isEnabled();
	}
	
	
	/**
	 * push the init reporters button and wait for
	 * dialog to close
	 * @return
	 * @throws Exception
	 */
	public int initReporters() throws Exception {
		JMenuBarOperator bar = new JMenuBarOperator(mainFrame);
		bar.pushMenu(jmap.getInitReportMenu(), "|");
		jemmySupport.WaitForDialogToClose(jmap.getInitReportDialogTitle());
		return 0;
	}
	
	public String getReportDir() throws Exception{
		return System.getProperty("user.dir") + File.separator + JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER)
				+ File.separator + "current" + File.separator + "reports.0.xml";
	}
	
	/**
	 * 
	 * @return the current reports dir with ending "/"
	 * @throws Exception
	 */
	public String getReportsDir() throws Exception{
		return System.getProperty("user.dir") + File.separator + JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER)
		+ File.separator + "current/";
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
