/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import java.util.logging.Logger;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.report.JSystemListeners;
import jsystem.runner.loader.LoadersManager;

/**
 * 
 * @author goland
 */
public class AboutToChangeToFixtureNotification extends RunnerNotification {
	private static final long serialVersionUID = 468127957018314228L;
	
	private static Logger log = Logger.getLogger(AboutToChangeToFixtureNotification.class.getName());
	
	private String fixtureClassName;
	
	public AboutToChangeToFixtureNotification(Object source,String fixtureClassName) {
		super(AboutToChangeToFixtureNotification.class.getName(), source);
		this.fixtureClassName = fixtureClassName;
	}
	
	public void invokeDispatcher(JSystemListeners mediator){
		ClassLoader loader = LoadersManager.getInstance().getLoader();
		try {
			Class<?> fixtureClass = (Class<?>)loader.loadClass(getFixtureClassName());
			Fixture fixtureInstance = (Fixture)fixtureClass.newInstance();
			mediator.aboutToChangeTo(fixtureInstance);			
		} catch (Exception e){
			log.warning("Failed creating instance of fixture " + getFixtureClassName() + " " + e.getMessage());
		}
	}
	
	public String getFixtureClassName() {
		return fixtureClassName;
	}
}
