/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.fixture;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.runner.ClassPathFixtureCollector;
import jsystem.runner.loader.LoadersManager;

/**
 * Responsible for the fixture navigation and execution. Support the fixture
 * model.
 * 
 * @author Guy Arieli
 */
public class FixtureManager {
	private static Logger log = Logger.getLogger(FixtureManager.class.getName());

	private static FixtureManager fm = null;

	private Vector<FixtureListener> listeners = new Vector<FixtureListener>();

	private HashMap<String, Fixture> allFixtures = null;

	private boolean disableFixture = false;

	/**
	 * Get and instance of the FixtureManager.
	 * 
	 * @return FixtureManager instance.
	 */
	public static FixtureManager getInstance() {
		if (fm == null) {
			fm = new FixtureManager();
			fm.initFixtureModel();
		}
		return fm;
	}

	String currentFixture = null;

	private FixtureManager() {
	}

	/**
	 * Check if the pass fixture is the current fixture.
	 * 
	 * @param f
	 *            The fixture to be tested
	 * 
	 * @return true if it's the current fixture, false if not.
	 */
	public boolean isCurrent(Fixture f) {
		return f.getName().equals(currentFixture);
	}

	/**
	 * init the fixture model. Should be called when the model changes.
	 */
	public void initFixtureModel() {
		currentFixture = RootFixture.getInstance().getName();
		allFixtures = new HashMap<String, Fixture>();
		allFixtures.put(currentFixture, RootFixture.getInstance());
		ClassPathFixtureCollector cpfc = new ClassPathFixtureCollector();
		Enumeration<String> enum1 = cpfc.collectTests();
		while (enum1.hasMoreElements()) {
			Class<?> c = null;
			try {
				String classToLoad = enum1.nextElement().toString();

				c = LoadersManager.getInstance().getLoader().loadClass(classToLoad);

			} catch (Exception e) {
				log.log(Level.FINE, "Unable to load class", e);
				continue;
			}
			if (!(Fixture.class.isAssignableFrom(c))) {
				continue;
			}
			Object t;
			try {
				t = createFixtureObject(c);
			} catch (Exception e) {
				log.log(Level.FINE, "Unable to create fixture object", e);
				continue;
			}
			if (t != null && t instanceof Fixture) {
				if (t instanceof RootFixture) {
					continue;
				}
				Fixture f = (Fixture) t;
				allFixtures.put(f.getName(), f);
			}
		}
	}

	/**
	 * Get the path to fixture.
	 * 
	 * @param fixture
	 *            The fixture to find the path to.
	 * 
	 * @return Return a vector of the fixture in the path.
	 * @exception Exception
	 */
	public Vector<Fixture> getFixturePath(Fixture fixture) throws Exception {
		Vector<Fixture> v = getFixturePath(fixture.getName());
		return v;
	}

	/**
	 * Create a Vector of all fixtures on the path to the given Fixture,
	 * The parents are first in the path 
	 * @param fixtureName
	 * @return
	 * @throws Exception
	 */
	private Vector<Fixture> getFixturePath(String fixtureName) throws Exception {
		Vector<Fixture> fixtures = new Vector<Fixture>();
		Fixture fixture = (Fixture) allFixtures.get(fixtureName);
		if (fixture == null) {
			throw new Exception("Unknown fixture: " + fixtureName);
		}

		fixtures.add(fixture);
		while (fixture.getParentFixture() != null) {
			Fixture parentFixture = (Fixture) allFixtures.get(fixture.getParentFixture().getName());
			if (parentFixture == null) {
				throw new Exception("Unknown fixture: " + fixture.getParentFixture().getName());
			}
			if (fixtures.contains(parentFixture)) {
				throw new Exception("Fixture error, loop found");
			}
			fixtures.add(0, parentFixture);

			fixture = parentFixture;
		}
		return fixtures;
	}

	private void removeIdenticals(Vector<Fixture> down, Vector<Fixture> up) {
		while (true) {
			if (down.size() == 0 || up.size() == 0) {
				return;
			}
			if (!down.firstElement().equals(up.firstElement())) {
				return;
			}
			down.removeElementAt(0);
			up.removeElementAt(0);
		}
	}

	/**
	 * Navigate to the fixture.
	 * 
	 * @param fixtureName
	 *            The fixture to navigate to.
	 * 
	 * @exception Throwable
	 */
	public void goTo(String fixtureName) throws Throwable {
		if (isDisableFixture()) {
			return;
		}
		fireStart();
		try {
			Vector<Fixture> down;
			Vector<Fixture> up;
			down = getFixturePath(currentFixture);
			up = getFixturePath(fixtureName);
			removeIdenticals(down, up);
			Collections.reverse(down);
			for (int i = 0 ; i < down.size() ; i++) {
				Fixture fixture = (Fixture) down.elementAt(i);
				fireAboutToChange(fixture);
				fixture.run(Fixture.TEARDOWN_DIRECTION);
				Class<?> p = fixture.getParentFixture();
				if (p == null) {
					break;
				}
				setCFixture(p.getName());
				fireFixtureChange((Fixture) allFixtures.get(currentFixture));
			}
			for (int i = 0; i < up.size(); i++) {
				Fixture fixture = (Fixture) up.elementAt(i);
				fixture.run(Fixture.SETUP_DIRECTION);
				setCFixture(fixture.getName());
				fireFixtureChange(fixture);
			}
		} finally {
			fireEnd();
		}

	}

	/**
	 * Navigate to the fixture using the failTearDownPath
	 * 
	 * @param fixtureName
	 *            The fixture name to navigate to.
	 * 
	 * @exception Throwable
	 */
	public void failTo(String fixtureName) throws Throwable {
		if (isDisableFixture()) {
			return;
		}
		fireStart();
		try {
			Vector<Fixture> down = getFixturePath(currentFixture);
			if (!removeNotInDownPath(down, fixtureName)) {
				throw new Exception("No down path to: '" + fixtureName + "' from '" + currentFixture + "'");
			}
			Collections.reverse(down);
			for (int i = 0; i < down.size(); i++) {
				Fixture fixture = (Fixture) down.elementAt(i);
				fireAboutToChange(fixture);
				fixture.run(Fixture.TEARDOWN_FAIL_DIRECTION);

				if (fixture.getParentFixture() == null) {
					break;
				}
				setCFixture(fixture.getParentFixture().getName());
				fireFixtureChange((Fixture) allFixtures.get(currentFixture));
			}

		} finally {
			fireEnd();
		}
	}

	private boolean removeNotInDownPath(Vector<Fixture> path, String fixtureName) {
		if (fixtureName == null) {
			return true;
		}
		while (path.size() > 0) {
			Fixture fixture = (Fixture) path.elementAt(0);
			if (fixture.getName().equals(fixtureName)) {
				path.removeElementAt(0);
				return true;
			} else {
				path.removeElementAt(0);
			}
		}
		return false;
	}

	/**
	 * Get all the childrens of a fixture.
	 * 
	 * @param parent
	 *            The parent fixture to get it childrens.
	 * 
	 * @return A vector with all the childrens fixtures.
	 */
	public ArrayList<Fixture> getAllChildrens(Fixture parent) {
		ArrayList<Fixture> childrens = new ArrayList<Fixture>();
		Iterator<Fixture> iter = allFixtures.values().iterator();
		while (iter.hasNext()) {
			Fixture fixture = (Fixture) iter.next();
			Class<?> fixtureParent = fixture.getParentFixture();
			if (fixtureParent != null) {
				if (parent.getClass().getName().equals(fixtureParent.getName())) {
					childrens.add(fixture);
				}
			}
		}
		return childrens;
	}

	private Object createFixtureObject(Class<?> testClass) throws Exception {
		Constructor<?> constructor;
		constructor = testClass.getConstructor(new Class<?>[0]);
		Object test;
		test = constructor.newInstance(new Object[0]);
		return test;
	}

	public void addListener(FixtureListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Remove a fixture listener.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(FixtureListener listener) {
		listeners.removeElement(listener);
	}

	private void fireStart() {
		Enumeration<FixtureListener> enum1 = listeners.elements();
		while (enum1.hasMoreElements()) {
			enum1.nextElement().startFixturring();
		}
	}

	private void fireEnd() {
		Enumeration<FixtureListener> enum1 = listeners.elements();
		while (enum1.hasMoreElements()) {
			enum1.nextElement().endFixturring();
		}
	}

	private void fireFixtureChange(Fixture fixture) {
		Enumeration<FixtureListener> enum1 = listeners.elements();
		while (enum1.hasMoreElements()) {
			enum1.nextElement().fixtureChanged(fixture);
		}
	}

	private void fireAboutToChange(Fixture fixture) {
		Enumeration<FixtureListener> enum1 = listeners.elements();
		while (enum1.hasMoreElements()) {
			enum1.nextElement().aboutToChangeTo(fixture);
		}
	}

	/**
	 * Get the current fixture.
	 * 
	 * @return The current fixture name.
	 */
	public String getCurrentFixture() {
		return currentFixture;
	}

	/**
	 * Set the current fixture. it will be done without executing the fixture
	 * path.
	 * 
	 * @param currentFixture
	 *            The new fixture.
	 * 
	 * @exception Exception
	 */
	public void setCurrentFixture(String currentFixture) throws Exception {
		Fixture f = (Fixture) allFixtures.get(currentFixture);
		if (f == null) {
			throw new Exception("Unknown fixture: " + currentFixture);
		}
		fireFixtureChange(f);
		setCFixture(currentFixture);
	}

	private void setCFixture(String cfixture) {
		currentFixture = cfixture;
	}

	public Fixture getFixture(String className) {
		return (Fixture) allFixtures.get(className);
	}

	/**
	 * Is the fixture modle disabled
	 * 
	 * @return true if disable, false if not.
	 */
	public boolean isDisableFixture() {
		return disableFixture;
	}

	/**
	 * Set the disable status. If disabled no fixture execution will be done.
	 * 
	 * @param disableFixture
	 *            The disable status to be set.
	 */
	public void setDisableFixture(boolean disableFixture) {
		this.disableFixture = disableFixture;
	}
}
