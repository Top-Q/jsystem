package com.aqua.jsystemobject.clients;

import com.aqua.jsystemobject.handlers.JServerHandlers;

public class JReporterClient extends BaseClient {

	public JReporterClient() {
		super();
		handler = JServerHandlers.REPORTER;
	}
	@Override
	String getHandlerName() {
		return handler.getHandlerClassName();
	}
	
	public void pressLogButton() throws Exception{
		callHandleXml("press the logs button", "pressLogButton");
	}
	/**
	 * if reports button is enabled will return true, else will return false.
	 */
	public boolean isReportsButtonEnabled() throws Exception{
		return (Boolean)callHandleXml("is the reports button enabled?", "isReportsButtonEnabled");
	}
	
	public void changeDBpropertyWithGui(String properyKey, String PropertyValue) throws Exception{
		callHandleXml("change db properties by gui", "changeDBpropertyWithGui", properyKey, PropertyValue);
	}
}
