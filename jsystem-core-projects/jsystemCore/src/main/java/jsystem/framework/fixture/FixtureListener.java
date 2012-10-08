/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.fixture;

/**
 * Classes that implement the FixtureListener will be notified About fixture
 * events: aboutToChangeTo fixtureChanged startFixturring endFixturring
 * 
 * In order to be notify the class should be add to the ListenersManager.
 * 
 * @author Guy Arieli
 */
public interface FixtureListener {
	/**
	 * Will be called before the execution of the fixture.
	 * 
	 * @param fixture
	 *            The fixture that is going to be executed.
	 */
	public abstract void aboutToChangeTo(Fixture fixture);

	/**
	 * Will be called after the execution of the fixture.
	 * 
	 * @param fixture
	 *            The fixture that was executed.
	 */
	public abstract void fixtureChanged(Fixture fixture);

	/**
	 * Is called before the fixture navigation is started.
	 */
	public abstract void startFixturring();

	/**
	 * Is called after the fixture navigation ends.
	 */
	public abstract void endFixturring();
}
