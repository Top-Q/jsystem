package com.aqua.jsystemobjects.handlers;

import org.jsystem.objects.handlers.HandlerBasic;
import org.jsystem.objects.handlers.HandlersList;

public class JRegressionHandlerList implements HandlersList {

	@Override
	public HandlerBasic[] getHandlersList() {
		return new HandlerBasic[] { new JApplicationHandler(), new JScenarioHandler(), new JTestsTreeHandler(),
				new JReporterHandler(), new JRemoteInformationHandler(), new BaseHandler() };
	}

}
